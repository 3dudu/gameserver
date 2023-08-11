package com.icegame.init;


import com.icegame.sysmanage.entity.HelpSys;
import com.icegame.sysmanage.service.IHelpSysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InitData implements InitializingBean{

    @Autowired
    private IHelpSysService helpSysService;

    private static Logger logger = LoggerFactory.getLogger(InitData.class);

    public Map<String, List<?>> map = new HashMap<String, List<?>>();

    private static HelpSys helpSys = new HelpSys();

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("============ Master启动完成... ============");

        loadHelpData();

    }



    public void loadHelpData(){

        // 先清理掉map中的数据
        map.clear();

        logger.info("============ 初始化【帮助系统】所需资源... ============");

        List<HelpSys> officalList = helpSysService.getAllOfficalList(helpSys);

        List<HelpSys> strategyList = helpSysService.getAllStrategyList(helpSys);

        validateAndPut("officalList",officalList);

        validateAndPut("strategyList",strategyList);

        logger.info("============ 【帮助系统】所有资源初始化完成... ============");

    }

    public void validateAndPut(String key, List<?> list){

        if(list != null){
            map.put(key,list);
            logger.info(" 初始化[{}]资源完成... ",key);
        }else{
            logger.info(" [{}]为空，跳过初始化... ",key);
        }
    }

    public  Map<String, List<?>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<?>> map) {
        this.map = map;
    }
}
