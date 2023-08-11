package com.icegame.gm.util;

import java.util.Map;

import com.icegame.gm.entity.BatchDelete;

import com.icegame.gm.entity.ExportTable;
import net.sf.json.JSONArray;

public class MapUtils {
	
	public static String getGoodsId(BatchDelete bd){
		
		Map<Long,Long> npcIds = bd.getNpcIds();
		Map<String,String> magicIds = bd.getMagicIds();
		Map<Long,Long> itemIds = bd.getItemIds();
		Map<Long,Long> horseIds = bd.getHorseIds();
		
		JSONArray goosId = new JSONArray();
		
		if(npcIds.size()>0){
			for (Long value : npcIds.values()) { 
				goosId.add(value); 
			}
		}
		if(magicIds.size()>0){
			for (String value : magicIds.values()) { 
				goosId.add(value); 
			}
		}
		if(itemIds.size()>0){
			for (Long value : itemIds.values()) { 
				goosId.add(value); 
			}
		}
		if(horseIds.size()>0){
			for (Long value : horseIds.values()) { 
				goosId.add(value); 
			}
		}
		
		
		return goosId.toString();
	}

	public static String getPlayerId(ExportTable et){

		Map<Long,Long> playerIds = et.getPlayerIds();

		JSONArray playerId = new JSONArray();

		if(playerIds.size()>0){
			for (Long value : playerIds.values()) {
				playerId.add(value);
			}
		}


		return playerId.toString();
	}

}
