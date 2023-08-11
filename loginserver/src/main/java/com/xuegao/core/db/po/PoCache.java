package com.xuegao.core.db.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.xuegao.core.db.DBWrapper;

public class PoCache {
	private static Logger logger=Logger.getLogger(PoCache.class);
	//max po size
	public static final int MAXINUMSIZE=5000000;
	
	private static final LoadingCache<CacheKeyVo,BasePo> cache=CacheBuilder
			.newBuilder()
			.maximumSize(MAXINUMSIZE)
//			.expireAfterAccess(24, TimeUnit.HOURS)
			.build(
				new CacheLoader<CacheKeyVo, BasePo>() {
					@Override
					public BasePo load(CacheKeyVo arg0) throws Exception {
						try {
							PoInfoVo poInfoVo=PoInfoVo.getPoInfo(arg0.getClazz());
							String sql="select * from `"+poInfoVo.getTableName()+"` where `id`=?";
							JSONObject object=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForBean(sql, arg0.getId());
							if(object==null){
								return null;
							}
							BasePo basePo=JSON.toJavaObject(object, arg0.getClazz());
							basePo.loadData();
							basePo.isLoad=true;
							return basePo;
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							return null;
						}
					}
            });
	
	@SuppressWarnings("unchecked")
	public static <T extends BasePo>T get(Class<? extends BasePo> clazz,Long id){
		CacheKeyVo key=new CacheKeyVo(clazz, id);
		try {
			BasePo basePo = cache.get(key);
			if(basePo==null){
				logger.error("PoCache没有获取到对象,class="+clazz.getSimpleName()+",id="+id);
			}
			return (T)basePo;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<? extends BasePo> getAll(Class<? extends BasePo> clazz,Iterable<? extends Long> ids){
		List<CacheKeyVo> list=new ArrayList<CacheKeyVo>();
		List<BasePo> list2=new ArrayList<>();
		try {
			for(Long id:ids){
				list.add(new CacheKeyVo(clazz, id));
			}
			ImmutableMap<CacheKeyVo, BasePo> immutableMap= cache.getAllPresent(list);
			List<BasePo> list1=immutableMap.values().asList();
			return list1;
		} catch (Exception e) {
			logger.error("PoCache.getAll(keys)异常,class="+clazz.getName()+",ids="+ids.toString());
		}
		return list2;
	}
	
	public static void clearKey(Class<? extends BasePo> clazz,Long id){
		CacheKeyVo key=new CacheKeyVo(clazz, id);
		cache.asMap().remove(key);
	}
}
