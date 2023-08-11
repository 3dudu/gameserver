package com.xuegao.core.db.po;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

public class PoInfoVo {
	
	private static Logger logger=Logger.getLogger(PoInfoVo.class);
	
	private String tableName;
	private String proxoolAlias;
	private List<String> columnsExceptId=new ArrayList<String>();
	private Map<String, Field> fieldMap=new HashMap<String, Field>();
	
	public String getTableName() {
		return tableName;
	}
	public String getProxoolAlias() {
		return proxoolAlias;
	}
	
	public List<String> getColumnsExceptId() {
		return columnsExceptId;
	}
	
	public Map<String, Field> getFieldMap() {
		return fieldMap;
	}
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	private static Map<Class<?>, PoInfoVo> poInfoMap=new ConcurrentHashMap<Class<?>, PoInfoVo>();
	public static PoInfoVo getPoInfo(Class<? extends BasePo> clazz){
		PoInfoVo poInfoVo=poInfoMap.get(clazz);
		if(poInfoVo==null){
			poInfoVo=new PoInfoVo();
			Table table=clazz.getAnnotation(Table.class);
			if(table!=null){
				poInfoVo.tableName=table.tableName();
				poInfoVo.proxoolAlias=table.proxoolAlias();
			}
			Field[] fields=clazz.getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				Column column=fields[i].getAnnotation(Column.class);
				if(column!=null){
					if(!"id".equals(fields[i].getName())){
						poInfoVo.getColumnsExceptId().add(fields[i].getName());
					}
					poInfoVo.getFieldMap().put(fields[i].getName(), fields[i]);
					fields[i].setAccessible(true);
				}
			}
			poInfoMap.put(clazz, poInfoVo);
			//check poInfoVo
			if(poInfoVo.getTableName()==null||poInfoVo.getTableName().length()==0){
				logger.error(clazz.toString()+" has no @Table(tablename)");
			}else if(poInfoVo.getProxoolAlias()==null||poInfoVo.getProxoolAlias().length()==0){
				logger.error(clazz.toString()+" has no @Table(proxoolAlias)");
			}
		}
		return poInfoVo;
	}
	
	/**
	 * 生成删除语句
	 * @return
	 */
	public String fetchDeleteSql(){
		String sql="delete from `"+this.getTableName()+"` where `id`=?";
		return sql;
	}
	
	/**
	 * 生成查询语句
	 * @return
	 */
	public String fetchQuerySql(){
		String sql="select * from `"+this.getTableName()+"` where `id`=?";
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	public <T>T getColumnValue(BasePo basePo,String columnName){
		try {
			
			Field field=this.fieldMap.get(columnName);
			Object object=field.get(basePo);
			return (T)object;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
}
