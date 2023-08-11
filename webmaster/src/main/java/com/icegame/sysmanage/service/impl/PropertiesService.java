package com.icegame.sysmanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.AllSrvMail;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.AllSrvMailMapper;
import com.icegame.sysmanage.service.IAllSrvMailService;
import com.icegame.sysmanage.service.IPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PropertiesService implements IPropertiesService {

	@Value("${projectName}")
	public String projectName;

	@Value("${xls_base_dir}")
	public String xls_base_dir;

	@Value("${jsonPath}")
	public String jsonPath;

	@Value("${jsonSuffix}")
	public String jsonSuffix;

	@Value("${version}")
	public String version;

	@Value("${project}")
	public String currentProject;

    @Value("${language}")
    public String currentLanguage;

	@Value("${port_pay}")
    public String port_pay;

	@Value("${xq_mall_url}")
	public String xq_mall_url;

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public String getBaseDir() {
		return xls_base_dir;
	}

	@Override
	public String getJsonPath() {
		return jsonPath;
	}

	@Override
	public String getJsonSuffix() {
		return jsonSuffix;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getCurrentProject() {
		return currentProject;
	}

    @Override
    public String getCurrentLanguage() {
        return currentLanguage;
    }

	@Override
	public String getPayAgentRechargePort() {
		return port_pay;
	}

	@Override
	public String getXqMallUrl() {
		return xq_mall_url;
	}

}
