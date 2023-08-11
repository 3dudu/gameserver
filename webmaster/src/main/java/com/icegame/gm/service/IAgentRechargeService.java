package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.AgentRecharge;

import java.io.InputStream;
import java.util.Map;

public interface IAgentRechargeService {


    PageInfo<AgentRecharge> getAgentRechargeList(AgentRecharge agentRecharge, Long startTime,
                                                 Long passTime, PageParam pageParam);

    Map<String, Object> saveAgentRecharge(AgentRecharge agentRecharge);

    Map<String, Object> syncAgentRechargeToPay(Long id);

    InputStream export(AgentRecharge agentRecharge, Long startTime, Long passTime) throws Exception;
}

