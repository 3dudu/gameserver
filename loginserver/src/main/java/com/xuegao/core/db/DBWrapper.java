package com.xuegao.core.db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.exception.DeException;
import com.xuegao.core.util.ClassPathUtil;
/**
 * 数据库操作类
 * @author ccx
 */
public class DBWrapper {
	private String proxool_conname = null;
	private static Logger logger = Logger.getLogger(DBWrapper.class);
	private static Map<String, DBWrapper> dbWrapperInstances=new ConcurrentHashMap<String, DBWrapper>();
	private ExecutorService executorService=null;
	private int syncThreadSize=1;
	private static final ThreadLocal<Map<String, TransactionConnection>> localTransConnection=new ThreadLocal<Map<String, TransactionConnection>>();
	static{
		try {
			File f=ClassPathUtil.findFile("proxool.properties");
			if(f==null){
				logger.error("proxool.properties not found!");
			}else {
				logger.info("开始加载数据库配置-->配置文件地址:"+f.getPath());
				config(f.getPath());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

	}
	private DBWrapper(String proxoolName){
		this.proxool_conname=proxoolName;
		executorService=Executors.newFixedThreadPool(syncThreadSize);
	}

	public DBWrapper setSyncThreadSize(int size){
		if(executorService==null){
			executorService=Executors.newFixedThreadPool(size);
		}else {
			if(size!=syncThreadSize){
				executorService.shutdown();
				executorService=null;
				executorService=Executors.newFixedThreadPool(size);
			}
		}
		return this;
	}

	/**
	 * 加载数据库连接配置
	 * @param proxoolConfigPath
	 */
	public static void config(String proxoolConfigPath){
		try {
			Properties properties=new Properties();
			properties.load(new FileInputStream(proxoolConfigPath));
			config(properties);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 加载数据库连接配置
	 * @param proxoolProperties
	 */
	public static void config(Properties proxoolProperties){
		try {
			PropertyConfigurator.configure(proxoolProperties);
			logger.info("加载proxool数据库连接配置:"+proxoolProperties.toString());
		} catch (ProxoolException e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 加载数据库连接配置，dbname默认为alias
	 * @param ip
	 * @param port
	 * @param dbName
	 * @param user
	 * @param pass
	 */
	public static void config(String ip, int port, String dbName, String user,String pass){
		Properties p = new Properties();
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.alias", dbName);
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.driver-url","jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?autoReconnect=true&characterEncoding=utf-8");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.driver-class","com.mysql.jdbc.Driver");
		p.setProperty("jdbc-" + dbName.hashCode() + ".user", user);
		p.setProperty("jdbc-" + dbName.hashCode() + ".password", pass);
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.maximum-connection-count", "100");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.maximum-active-time", "3600000");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.house-keeping-sleep-time", "14400000");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.house-keeping-test-sql", "select now()");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.simultaneous-build-throttle", "100");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.test-before-use","false");
		p.setProperty("jdbc-" + dbName.hashCode() + ".proxool.trace", "false");
		config(p);
	}
	/**
	 * 获取DBWrapper操作实例
	 * @param proxoolName
	 * @return
	 */
	public static synchronized DBWrapper getInstance(String proxoolName){
		DBWrapper dbWrapper=dbWrapperInstances.get(proxoolName);
		if(dbWrapper!=null){
			return dbWrapper;
		}else {
			dbWrapper=new DBWrapper(proxoolName);
			dbWrapperInstances.put(proxoolName, dbWrapper);
			return dbWrapper;
		}
	}

	/**
	 * 批量更新
	 * @param sql
	 * @param pars
	 * @return
	 */
	public int[] executeBatch(String sql, List<Object[]> pars) {
		Connection con = null;
		PreparedStatement psta = null;
		Object[] params=null;
		try {
			con = getConnection();
			psta = con.prepareStatement(sql);
			int size = pars.size();
			if (pars != null && size > 0) {
				for (int i = 0; i < size; i++) {
					params = pars.get(i);
					if (params != null && params.length > 0) {
						psta.clearParameters();
						setPsValue(psta, params);
						psta.addBatch();
					}
				}
			}
			int[] results=psta.executeBatch();
			return results;
		} catch (SQLException e) {
			rollbackFailureTransaction();
			logger.error(fetchSqlAndParamStr(sql, params), e);
			throw new DBException(sql, new Object[]{"参数集合数量:"+pars==null?0:pars.size()}, e);
		} finally {
			close(null, psta, con);
		}
	}



	/**
	 * 异步批量更新
	 * @param sql
	 * @param pars
	 * @return
	 */
	public void syncExecuteBatch(final String sql, final List<Object[]> pars) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				executeBatch(sql, pars);
			}
		});
	}


	/**
	 * 插入，并获取自增长ID编号
	 * @param sql
	 * @param params
	 * @return
	 */
	public Long insertGetId(String sql,Object...params){
		Connection con = null;
		PreparedStatement psta = null;
		ResultSet res=null;
		try {
			con = getConnection();
			psta = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			if (params != null && params.length > 0) {
				setPsValue(psta, params);
			}
			psta.executeUpdate();
			res = psta.getGeneratedKeys();
			if(res.next()){
				return res.getLong(1);
			}
		} catch (SQLException e) {
			rollbackFailureTransaction();
			logger.error(fetchSqlAndParamStr(sql, params), e);
			throw new DBException(sql, params, e);
		} finally {
			close(res, psta, con);
		}
		return null;
	}

	public static void setPsValue(PreparedStatement psta,Object...params) throws SQLException{
		for (int i = 1; i <= params.length; i++) {
			Object object=params[i-1];
			if(object==null){
				psta.setString(i, null);
			}else if(object instanceof Date){
				psta.setString(i, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)object));
			}else if(object instanceof byte[]){
				psta.setBytes(i, (byte[]) object);
			}else if(object instanceof Boolean){
				byte b=((Boolean)object)?(byte)1:(byte)0;
				psta.setByte(i, b);
			}else if(object instanceof Integer){
				psta.setInt(i,(Integer)object);
			}else{
				psta.setString(i, object.toString());
			}
		}
	}

	/**
	 * 执行单条
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int execute(String sql, Object... params) {

		Connection con = null;
		PreparedStatement psta = null;
		try {
			con = getConnection();
			psta = con.prepareStatement(sql);
			if (params != null && params.length > 0) {
				setPsValue(psta, params);
			}
			return psta.executeUpdate();
		} catch (SQLException e) {
			rollbackFailureTransaction();
			logger.error(fetchSqlAndParamStr(sql, params), e);
			throw new DBException(sql, params, e);
		} finally {
			close(null, psta, con);
		}
	}

	/**
	 * 异步执行单条
	 * @param sql
	 * @param params
	 * @return
	 */
	public void syncExecute(final String sql, final Object... params) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					execute(sql, params);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		});
	}

	/**
	 * 批量更新（事务操作，不要与beginTransaction（）连用)
	 * @param sqls
	 * @return
	 */
	public int[] executeBatch(String[] sqls) {
		Connection con = null;
		Statement sta = null;
		int[] res=null;
		try {
			con = getConnection();
			sta = con.createStatement();
			for (int i = 0; i < sqls.length; i++) {
				sta.addBatch(sqls[i]);
			}
			res=sta.executeBatch();
			return res;
		} catch (SQLException e) {
			rollbackFailureTransaction();
			logger.error(e.getMessage(), e);
			throw new DBException("sql数量:"+(sqls==null?0:sqls.length), sqls, e);
		} finally {
			close(null, sta, con);
		}
	}
	/**
	 * 异步批量更新
	 * @param sqls
	 * @return
	 */
	public void syncExecuteBatch(final String[] sqls) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				executeBatch(sqls);
			}
		});
	}

	private void close(ResultSet rs, Statement sta, Connection con) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (sta != null) {
			try {
				sta.close();
				sta = null;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (con != null) {
			try {
				TransactionConnection transactionConnection=getLocalThreadTransConnection(proxool_conname);
				if(transactionConnection==null){
					if(!con.isClosed())con.close();
					con = null;
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 查询列表
	 * @param sql
	 * @param params
	 * @return
	 */
	public JSONArray queryForList(String sql, Object... params) {
		JSONArray lstList = new JSONArray();
		Connection con = null;
		PreparedStatement psta = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			psta = con.prepareStatement(sql);
			if (params != null && params.length > 0) {
				setPsValue(psta, params);
			}
			rs = psta.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int c = rsmd.getColumnCount();
			String[] headColumns = new String[c];
			for (int i = 1; i <= c; i++) {
				headColumns[i - 1] = rsmd.getColumnLabel(i);
			}
			while (rs.next()) {
				JSONObject bean = new JSONObject();
				for (int i = 1; i <= c; i++) {
					bean.put(headColumns[i - 1], rs.getObject(i));
				}
				lstList.add(bean);
			}
			return lstList;
		} catch (Exception e) {
			rollbackFailureTransaction();
			logger.error(fetchSqlAndParamStr(sql, params), e);
			throw new DBException(sql, params, e);
		} finally {
			close(rs, psta, con);
		}
	}

	/**
	 * 分页查询
	 * @param sql
	 * @param startIndex
	 * @param size
	 * @param params
	 * @return    {totalCount:100,data:[]}
	 */
	public JSONObject queryForPageList(String sql,int startIndex,int size, Object... params){
		JSONObject rs=new JSONObject();
		long totalCount=queryForLong("select count(1) c from ("+sql+") t20150304", params);
		JSONArray array=queryForList("select * from ("+sql+") t20150304 limit "+startIndex+","+size, params);
		rs.put("totalCount", totalCount);
		rs.put("data", array);
		return rs;
	}



	/**
	 * 异步查询列表
	 * @param sql
	 * @param params
	 * @return
	 */
	public void syncQueryForList(final String sql, final Object[] params,final CallBackExecutor<JSONArray> callBackExecutor) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				JSONArray result=queryForList(sql, params);
				callBackExecutor.execute(result);
			}
		});
	}

	/**
	 * 查询整数
	 * @param sql
	 * @param params
	 * @return
	 */
	public Long queryForLong(String sql, Object... params) {
		JSONArray list = queryForList(sql, params);
		if (list == null || list.size() == 0) {
			return null;
		} else {
			JSONObject obj = list.getJSONObject(0);
			try {
				return obj.getLong(obj.keySet().iterator().next().toString());
			} catch (JSONException e) {
				logger.error(fetchSqlAndParamStr(sql, params), e);
				throw new DBException(sql, params, e);
			}
		}
	}

	/**
	 * 异步查询整数
	 * @param sql
	 * @param params
	 * @return
	 */
	public void syncQueryForLongr(final String sql, final Object[] params,final CallBackExecutor<Long> callBackExecutor) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				Long num=queryForLong(sql, params);
				callBackExecutor.execute(num);
			}
		});
	}

	/**
	 * 查询单条
	 * @param sql
	 * @param params
	 * @return
	 */
	public JSONObject queryForBean(String sql, Object... params) {
		JSONArray list = queryForList(sql, params);
		if (list == null || list.size() < 1) {
			return null;
		} else {
			return list.getJSONObject(0);
		}
	}

	/**
	 * 批量查询单条
	 * @param sql
	 * @param params
	 * @return
	 */
	public void syncQueryForBean(final String sql, final Object[] params,final CallBackExecutor<JSONObject> callBackExecutor) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				JSONObject result=queryForBean(sql, params);
				callBackExecutor.execute(result);
			}
		});
	}



	public synchronized Connection getConnection() throws SQLException{
		TransactionConnection tranConnection=getLocalThreadTransConnection(proxool_conname);
		if(tranConnection==null){
			Connection connection= DriverManager.getConnection(proxool_conname);
			connection.setAutoCommit(true);
			return connection;
		}else {
			if(tranConnection.getConnection()==null){
				Connection connection= DriverManager.getConnection(proxool_conname);
				connection.setAutoCommit(false);
				tranConnection.setConnection(connection);
			}
			return tranConnection.getConnection();
		}
	}

	/**
	 * 开启事务
	 */
	public void beginTransaction(){
		TransactionConnection tranConnection=getLocalThreadTransConnection(proxool_conname);
		if(tranConnection!=null){
			throw new DeException("last transaction is not closed yet!location="+tranConnection.getTranBeginClassname()+"."+tranConnection.getTranBeginMethodName()+" "+tranConnection.getTranBeginNum());
		}
		tranConnection=new TransactionConnection();
		StackTraceElement stackTraceElement=Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length-1];
		tranConnection.setTranBeginClassname(stackTraceElement.getClassName());
		tranConnection.setTranBeginMethodName(stackTraceElement.getClassName());
		tranConnection.setTranBeginNum(stackTraceElement.getLineNumber());

		setLocalThreadTransConnection(this.proxool_conname, tranConnection);
		logger.debug("begin transaction-->"+tranConnection.getTranBeginClassname()+"."+tranConnection.getTranBeginMethodName()+tranConnection.getTranBeginNum());
	}
	/**
	 * 结束事务
	 */
	public void endTransaction(){
		TransactionConnection tranConnection=getLocalThreadTransConnection(proxool_conname);
		if(tranConnection==null){
			//不存在事务
			return;
		}
		logger.debug("close success transaction-->"+tranConnection.getTranBeginClassname()+"."+tranConnection.getTranBeginMethodName()+tranConnection.getTranBeginNum());
		try {
			if(tranConnection.getConnection()!=null){
				tranConnection.getConnection().commit();
				tranConnection.getConnection().close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new DeException(e.getMessage());
		}
		setLocalThreadTransConnection(proxool_conname, null);
	}
	/**
	 * 回滚事务
	 */
	public void rollbackFailureTransaction(){
		TransactionConnection tranConnection=getLocalThreadTransConnection(proxool_conname);
		if(tranConnection==null){
			//不存在事务
			return;
		}
		logger.debug("close failure transaction-->"+tranConnection.getTranBeginClassname()+"."+tranConnection.getTranBeginMethodName()+tranConnection.getTranBeginNum());
		try {
			if(tranConnection.getConnection()!=null){
				tranConnection.getConnection().rollback();
				tranConnection.getConnection().setAutoCommit(true);
				tranConnection.getConnection().close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new DeException(e.getMessage());
		}
		setLocalThreadTransConnection(proxool_conname, null);
	}
	//获取事务连接
	private static synchronized TransactionConnection getLocalThreadTransConnection(String proxool_name){
		Map<String, TransactionConnection> map=localTransConnection.get();
		if(map==null){
			map=new HashMap<String, TransactionConnection>();
			localTransConnection.set(map);
		}
		return map.get(proxool_name);
	}
	//设置事务连接
	private static synchronized void setLocalThreadTransConnection(String proxool_name,TransactionConnection transactionConnection){
		Map<String, TransactionConnection> map=localTransConnection.get();
		if(map==null){
			map=new HashMap<String, TransactionConnection>();
			localTransConnection.set(map);
		}
		if(transactionConnection==null){
			map.remove(proxool_name);
		}else {
			map.put(proxool_name, transactionConnection);
		}
	}

	//异常时，打印的sql及参数
	public static String fetchSqlAndParamStr(String sql,Object...params){
		StringBuffer sb=new StringBuffer();
		sb.append(""+sql);
		List<Object> list=new ArrayList<>();
		if(params!=null){
			for(int i=0;i<params.length;i++){
				list.add(params[i]);
			}
		}
		sb.append(list.toString());
		return sb.toString();
	}

}
