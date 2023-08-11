package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.ExcelMail;

import java.util.List;

public interface ExcelMailMapper {

    Long addExcelMail(ExcelMail excelMail);

    boolean updateSuccessMail(ExcelMail excelMail);

    ExcelMail getExcelMailById(Long id);

    List<ExcelMail> getExcelMailList(ExcelMail excelMail);
}
