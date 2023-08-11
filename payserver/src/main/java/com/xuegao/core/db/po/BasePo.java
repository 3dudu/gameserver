package com.xuegao.core.db.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.util.StringUtil;

/**
 * 数据库对象的基类
 * @author ccx
 */
public abstract class BasePo {
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public static Logger logger=Logger.getLogger(BasePo.class);
	
	
	private Map<String, Object> changedPropertiesMap=new ConcurrentHashMap<String, Object>();
	protected void changed(String key,Object newData){
		changedPropertiesMap.put(key, newData);
	}
	
	
	
	@Override
	public String toString() {
		return JSON.toJSONString(this,SerializerFeature.WriteMapNullValue);
	}
	
	public PoInfoVo poInfo(){
		return PoInfoVo.getPoInfo(this.getClass());
	}
	
	public int insertToDB(){
		this.saveData();
		SqlAndParamVo sqlAndParamVo=fetchInsertSql();
		int rs =DBWrapper.getInstance(this.poInfo().getProxoolAlias()).execute(sqlAndParamVo.getSql(), sqlAndParamVo.getParams());
		return rs;
	}
	public void insertToDB_WithSync(){
		this.saveData();
		SqlAndParamVo sqlAndParamVo=fetchInsertSql();
		DBWrapper.getInstance(this.poInfo().getProxoolAlias()).syncExecute(sqlAndParamVo.getSql(), sqlAndParamVo.getParams());
	}
	
	public Long insertToDBAndGetId(){
		this.saveData();
		SqlAndParamVo sqlAndParamVo=fetchInsertSql();
		Long id= DBWrapper.getInstance(this.poInfo().getProxoolAlias()).insertGetId(sqlAndParamVo.getSql(), sqlAndParamVo.getParams());
		return id;
	}
	
	public int deleteFromDB(){
		String sql=this.poInfo().fetchDeleteSql();
		int rs= DBWrapper.getInstance(this.poInfo().getProxoolAlias()).execute(sql, this.getId());
		return rs;
	}
	public int updateToDB(){
		this.saveData();
		SqlAndParamVo sqlAndParamVo=fetchUpdateSql();
		int  rs = DBWrapper.getInstance(this.poInfo().getProxoolAlias()).execute(sqlAndParamVo.getSql(), sqlAndParamVo.getParams());
		return rs;
	}
	/**
	 * 异步更新
	 */
	public void updateToDB_WithSync(){
		this.saveData();
		SqlAndParamVo sqlAndParamVo=fetchUpdateSql();
		DBWrapper.getInstance(this.poInfo().getProxoolAlias()).syncExecute(sqlAndParamVo.getSql(), sqlAndParamVo.getParams());
	}
	public static <T extends BasePo>T findRealEntity(Class<? extends BasePo> clazz,Long id){
		T t=PoCache.get(clazz, id);
		return t;
	}
	/**
	 * 生成插入sql语句
	 * @return
	 */
	public SqlAndParamVo fetchInsertSql(){
		PoInfoVo poInfoVo=this.poInfo();
		List<String> columns=new ArrayList<String>();
		if(this.getId()!=null){
			columns.add("id");
		}
		columns.addAll(poInfoVo.getColumnsExceptId());
//		JSONObject object=(JSONObject) JSON.toJSON(this);
		String[] cols=columns.toArray(new String[0]);
		String[] wenhaos=new String[columns.size()];
		for(int i=0;i<wenhaos.length;i++){
			wenhaos[i]="?";
		}
		String sql="insert into `"+poInfoVo.getTableName()+"`("+StringUtil.implode(",", cols,"`","`")+") values("+StringUtil.implode(", ", wenhaos)+")";
		Object[] objects=new Object[cols.length];
		for(int i=0;i<cols.length;i++){
			if(i==0&&this.getId()!=null){
				objects[i]=this.getId();
			}else{
				objects[i]=poInfoVo.getColumnValue(this,cols[i]);
			}
		}
		SqlAndParamVo sqlAndParamVo=new SqlAndParamVo(sql, objects);
		return sqlAndParamVo;
	}
	
	/**
	 * 生成修改语句
	 * @return
	 */
	public SqlAndParamVo fetchUpdateSql(){
		PoInfoVo poInfoVo=this.poInfo();
		String[] cols=poInfoVo.getColumnsExceptId().toArray(new String[0]);
		String sql="update `"+poInfoVo.getTableName()+"` set "+StringUtil.implode(",", cols,"`","`=?")+" where `id`=?";
		Object[] objects=new Object[poInfoVo.getColumnsExceptId().size()+1];
		for(int i=0;i<objects.length;i++){
			if(i==objects.length-1){
				objects[i]=this.getId();
			}else{
				objects[i]=poInfoVo.getColumnValue(this,cols[i]);
			}
		}
		SqlAndParamVo sqlAndParamVo=new SqlAndParamVo(sql, objects);
		return sqlAndParamVo;
	}
	
	public void loadData(){
		
	}
	
	public void saveData(){
		
	}
	//从数据库中加载数据完成
	@JSONField(serialize=false)
	public volatile boolean isLoad=false;
	
	public void markChanged(){
		if(isLoad){
			BasePoSyncPool.push(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends BasePo>T findEntityFromDB(Class<T> clazz,Integer id){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(clazz);
		String sql="select * from `"+poInfoVo.getTableName()+"` where `id`=?";
		JSONObject object=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForBean(sql, id);
		BasePo basePo=JSON.toJavaObject(object, clazz);
		return (T)basePo;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends BasePo>List<T> findAllEntitiesFromDB(Class<T> clazz){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(clazz);
		String sql="select * from `"+poInfoVo.getTableName()+"`";
		JSONArray array=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForList(sql);
		List<T> list=new ArrayList<>();
		for(int i=0;i<array.size();i++){
			JSONObject object=array.getJSONObject(i);
			BasePo basePo=JSON.toJavaObject(object, clazz);
			list.add((T) basePo);
		}
		return list;
	}
	
	public static DBWrapper fetchDBWrapper(Class<? extends BasePo> clazz){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(clazz);
		return DBWrapper.getInstance(poInfoVo.getProxoolAlias());
	}
}
