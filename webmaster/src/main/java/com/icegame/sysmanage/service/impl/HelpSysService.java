package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.init.InitData;
import com.icegame.sysmanage.controller.LoginController;
import com.icegame.sysmanage.entity.HelpSys;
import com.icegame.sysmanage.mapper.HelpSysMapper;
import com.icegame.sysmanage.service.IHelpSysService;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HelpSysService implements IHelpSysService {

    private static Logger logger = Logger.getLogger(HelpSysService.class);


    @Autowired
	private HelpSysMapper helpSysMapper;

    @Autowired
    private InitData initData;

    public Map<String,List<?>> map;

    public List<HelpSys> officalList;

    public List<HelpSys> strategyList;

    /**
     *
     * @param helpSys
     * @param pageParam
     * @return
     * ----------------------------------------
     * @date 2019-06-15 11:01:14
     * @author wuzhijian
     * 修改返回master后台的时间戳转换为服务器进行转换
     * ----------------------------------------
     */
	@Override
	public PageInfo<HelpSys> getHelpSysList(HelpSys helpSys, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<HelpSys> helpSysList = helpSysMapper.getHelpSysList(helpSys);
        if(helpSysList.size() > 0){
            for(HelpSys sys : helpSysList){
                sys.setCreateTime(TimeUtils.stampToDateWithMill(sys.getCreateTime()));
            }
        }
		PageInfo<HelpSys> pageInfo = new PageInfo<HelpSys>(helpSysList);
		return pageInfo;
	}

	// 获取所有【官方】文章
	@Override
	public List<HelpSys> getAllOfficalList(HelpSys helpSys) {
		return helpSysMapper.getAllOfficalList(helpSys);
	}

	// 获取所有【攻略】文章
	@Override
	public List<HelpSys> getAllStrategyList(HelpSys helpSys) {
		return helpSysMapper.getAllStrategyList(helpSys);
	}

	@Override
	public HelpSys getHelpSysById(Long id) {
		return helpSysMapper.getHelpSysById(id);
	}

	@Override
	public boolean addHelpSys(HelpSys helpSys) {

        map = initData.getMap();
        officalList = (List<HelpSys>)map.get("officalList");
        strategyList = (List<HelpSys>)map.get("strategyList");

        Long officalId = 0L;
        Long strategyId = 0L;
        Long currentId;

        // 计算要写入的id
        if(officalList != null && officalList.size() > 0){
            officalId = officalList.get(officalList.size() - 1).getId();
        }
        if(strategyList != null && strategyList.size() > 0){
            strategyId = strategyList.get(strategyList.size() - 1).getId();
        }
        currentId = officalId >= strategyId ? officalId + 1 : strategyId + 1;
        helpSys.setId(currentId);

        boolean flag = false;

        try{
            // 先修改数据库
            flag = helpSysMapper.addHelpSys(helpSys);

            // 再修改内存
            if(flag){
                switch (helpSys.getDiffType()){
                    // diffType  1:官方  2:攻略
                    case 1 :
                        officalList.add(helpSys);
                        map.put("officalList",officalList);
                        break;
                    case 2 :
                        strategyList.add(helpSys);
                        map.put("strategyList",strategyList);
                        break;
                }
            }
        }catch (Exception e){
            logger.error("帮助系统添加信息异常，异常：" + e.getMessage());
        }


        return flag;
    }

	@Override
	public boolean delHelpSys(Long id) {

        map = initData.getMap();
        officalList = (List<HelpSys>)map.get("officalList");
        strategyList = (List<HelpSys>)map.get("strategyList");


        boolean flag = false;

        try{

            // 先修改数据库
            flag = helpSysMapper.delHelpSys(id);

            // 然后修改内存
            if(flag){
                for (int i = 0 ; i < officalList.size() ; i++){
                    if(id.equals(officalList.get(i).getId())){
                        officalList.remove(i);
                        break;
                    }
                }

                for (int i = 0 ; i < strategyList.size() ; i++){
                    if(id.equals(strategyList.get(i).getId())){
                        strategyList.remove(i);
                        break;
                    }
                }
            }

        }catch (Exception e){
            logger.error("帮助系统删除信息异常，异常：" + e.getMessage());
        }

		return flag;
	}

	@Override
    public boolean updateHelpSys(HelpSys helpSys) {

        map = initData.getMap();
        officalList = (List<HelpSys>)map.get("officalList");
        strategyList = (List<HelpSys>)map.get("strategyList");


        boolean flag = false;

        try {

            // 先修改数据库
            flag = helpSysMapper.updateHelpSys(helpSys);

            // 然后然后内存
            if(flag){
                for (int i = 0 ; i < officalList.size() ; i++){
                    if(helpSys.getId().equals(officalList.get(i).getId())){
                        officalList.remove(i);
                        officalList.add(i,helpSys);
                        break;
                    }
                }

                for (int i = 0 ; i < strategyList.size() ; i++){
                    if(helpSys.getId().equals(strategyList.get(i).getId())){
                        strategyList.remove(i);
                        strategyList.add(i,helpSys);
                        break;
                    }
                }
            }

        }catch (Exception e){
            logger.error("帮助系统修改信息异常，异常：" + e.getMessage());
        }

        return flag;
    }

    @Override
    public boolean clickZan(Long id) {

	    boolean isZan = false;

        map = initData.getMap();
        officalList = (List<HelpSys>)map.get("officalList");
        strategyList = (List<HelpSys>)map.get("strategyList");

        // 先修改内存 设置点赞数上线 999999
        for (int i = 0 ; i < officalList.size() ; i++){
            if(id.equals(officalList.get(i).getId())){
                if(officalList.get(i).getZan() <= 999999){
                    isZan = true;
                    officalList.get(i).setZan(officalList.get(i).getZan() + 1);
                    break;
                }
            }
        }

        for (int i = 0 ; i < strategyList.size() ; i++){
            if(id.equals(strategyList.get(i).getId())){
                if(strategyList.get(i).getZan() <= 999999){
                    isZan = true;
                    strategyList.get(i).setZan(strategyList.get(i).getZan() + 1);
                    break;
                }
            }
        }

        // 然后修改数据库
        if(isZan){
            return helpSysMapper.clickZan(id);
        }else{
            return helpSysMapper.clickZan(id);
        }
    }

}
