package com.icegame.gm.mapper;


import com.icegame.gm.entity.AgentRecharge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AgentRechargeMapper {

	List<AgentRecharge> getAllAgentRecharge();

	List<AgentRecharge> getAgentRechargeByConditions(@Param("agentRecharge") AgentRecharge agentRecharge,
													 @Param("startTime")Long startTime,
													 @Param("passTime")Long passTime);
	List<AgentRecharge> getAgentRechargeByConditionsAndMutServer(@Param("agentRecharge") AgentRecharge agentRecharge,
													 @Param("startTime")Long startTime,
													 @Param("passTime")Long passTime);

	boolean addAgentRecharge(AgentRecharge agentRecharge);

	boolean updateAgentRecharge(AgentRecharge agentRecharge);

	boolean deleteAgentRecharge(@Param("id") Integer id);

	AgentRecharge getAgentRechargeById(@Param("id")Long id);
}
