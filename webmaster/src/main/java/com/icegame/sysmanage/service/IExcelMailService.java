package com.icegame.sysmanage.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.ExcelMail;

public interface IExcelMailService {

    Long addExcelMail(ExcelMail excelMail);

    JSONObject syncExcelMail(Long id);

    ExcelMail getExcelMailById(Long id);

    PageInfo<ExcelMail> getExcelMailList(ExcelMail excelMail, PageParam pageParam);
}
