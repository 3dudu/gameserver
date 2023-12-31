package com.icegame.framework.utils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chesterccw
 * @date 2019-07-18 10:51:46
 */
public class WriteExcel {

    /**
     * 导出表的列名
     */
    private String[] rowName;

    /**
     * 每行作为一个Object对象
     */
    private List<Object[]>  dataList = new ArrayList<Object[]>();


    public WriteExcel(String[] rowName,List<Object[]>  dataList){
        this.dataList = dataList;
        this.rowName = rowName;
    }

    /**
     * 每张sheet所拥有的最大行数(excel最大65535)
     */
    private static final int SHEET_ROWS = 30000;

    /**
     * 初始 sheet1
     */
    private int sheetIndex = 1;

    /**
     * 导出数据
     * @return
     * @throws Exception
     */
	public InputStream export() throws Exception{

        // 创建工作簿对象
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 创建工作表 默认sheet1
        HSSFSheet sheet = workbook.createSheet("sheet1");

        // sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
        //获取列头样式对象
        HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);

        // 单元格样式对象
        HSSFCellStyle style = this.getStyle(workbook);

        // 定义所需列数
        int columnNum = rowName.length;

        // 在索引2的位置创建行(最顶端的行开始的第二行)
        HSSFRow rowRowName = sheet.createRow(0);

        // 设置列头
        setHeaser(columnNum, rowRowName, columnTopStyle);

        HSSFRow row = null;

        /**
         * rowIndex 主要是为了在新创建个sheet的时候从这个sheet的第1行开始写入
         */
        int rowIndex = 0;

        //将查询出的数据设置到sheet对应的单元格中
        for(int i=0;i < dataList.size();i++){

            if( i > (SHEET_ROWS * sheetIndex)){
                sheet = workbook.createSheet("sheet" + (sheetIndex + 1));

                // 定义所需列数
                columnNum = rowName.length;

                // 在索引2的位置创建行(最顶端的行开始的第二行)
                rowRowName = sheet.createRow(0);

                // 设置列头
                setHeaser(columnNum, rowRowName, columnTopStyle);

                // 如果是新的sheet 重置 rowIndex 为 0
                rowIndex = 0;

                // 每次创建完了自增s heet
                sheetIndex++;

            }

            //遍历每个对象
            Object[] obj = dataList.get(i);

            //创建所需的行数
            row = sheet.createRow(rowIndex + 1);

            for(int j=0; j<obj.length; j++){
                //设置单元格的数据类型
                HSSFCell  cell = null;
                cell = row.createCell(j,HSSFCell.CELL_TYPE_STRING);
                if(!"".equals(obj[j]) && obj[j] != null){
                    //设置单元格的值
                    cell.setCellValue(obj[j].toString());
                }
                //设置单元格样式
                cell.setCellStyle(style);
            }

            rowIndex++ ;

        }

        int sheetNum = workbook.getNumberOfSheets();

        /**
         * 遍历所有sheet 给每个sheet 设置列宽
         */
        for(int i = 0 ; i < sheetNum ; i++){
            HSSFSheet tempSheet = workbook.getSheetAt(i);
            // 自动设置列宽
            autoWidth(columnNum, tempSheet);
        }


        ByteArrayOutputStream os=new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] content=os.toByteArray();
        InputStream is=new ByteArrayInputStream(content);
        return is;
    }

    /*
     * 列头单元格样式
     */
	public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {

        // 设置字体
        HSSFFont font = workbook.createFont();

        //设置字体大小
        font.setFontHeightInPoints((short)12);

        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("宋体");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    /*
   * 列数据信息单元格样式
   */
	public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short)12);
        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("宋体");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    /**
     * 设置header
     * @param columnNum
     * @param rowRowName
     * @param columnTopStyle
     */
    public void setHeaser(int columnNum, HSSFRow rowRowName, HSSFCellStyle columnTopStyle){

        // 将列头设置到sheet的单元格中
        for(int n = 0 ; n < columnNum ; n++){
            //创建列头对应个数的单元格
            HSSFCell cellRowName = rowRowName.createCell(n);

            //设置列头单元格的数据类型
            cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);

            HSSFRichTextString text = new HSSFRichTextString(rowName[n]);

            //设置列头单元格的值
            cellRowName.setCellValue(text);

            //设置列头单元格样式
            cellRowName.setCellStyle(columnTopStyle);
        }
    }

    public void autoWidth(int columnNum, HSSFSheet sheet){

        //让列宽随着导出的列长自动适应
        for (int colNum = 0; colNum < columnNum; colNum++) {
            int columnWidth = sheet.getColumnWidth(colNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                HSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }
                if (currentRow.getCell(colNum) != null) {
                    HSSFCell currentCell = currentRow.getCell(colNum);
                    if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            if(colNum == 0){
                sheet.setColumnWidth(colNum, (columnWidth-2) * 256);
            }else{
                try{
                    sheet.setColumnWidth(colNum, (columnWidth+4) * 256);
                }catch (IllegalArgumentException e){
                    sheet.setColumnWidth(colNum, 255);
                }
            }
        }
    }

}
