/**
* 文件名：ExeclUtil.java
* 创建日期： 2017年7月26日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年7月26日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.tools.io.StreamUtil;

/**
 * 功能描述：
 * execl功能工具类
 */
public class ExeclUtil
{
 
    private static Logger log = LoggerFactory.getLogger(ExeclUtil.class);
    private static volatile ExeclUtil instance;
    
    /**
     * 单例模式
     */
    public static ExeclUtil getInstance()
    {
        if (null == instance)
        {
            synchronized (ExeclUtil.class)
            {
                if (null == instance)
                {
                    instance = new ExeclUtil();
                    return instance;
                }
            }
        }
        return instance;
    }
    
    /*
     * 导出数据
     * */
    @SuppressWarnings("deprecation")
    public void export(String title,String[] rowName, List<Object[]>  dataList ,String path, boolean needMerge) {
        try{
            log.info("exportExecl,title={}|path={}",title,path);
            HSSFWorkbook workbook = new HSSFWorkbook();                        // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(title);                     // 创建工作表
            
            // 产生表格标题行
            HSSFRow rowm = sheet.createRow(0);
            HSSFCell cellTiltle = rowm.createCell(0);
            
            //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
            HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
            HSSFCellStyle style = this.getStyle(workbook);                    //单元格样式对象
            
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (rowName.length-1)));
            cellTiltle.setCellStyle(columnTopStyle);
            cellTiltle.setCellValue(title);
            
            // 定义所需列数
            int columnNum = rowName.length;
            HSSFRow rowRowName = sheet.createRow(1);                // 在索引2的位置创建行(最顶端的行开始的第二行)
            
            // 将列头设置到sheet的单元格中
            for(int n=0;n<columnNum;n++){
                HSSFCell  cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
                cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cellRowName.setCellValue(text);                                    //设置列头单元格的值
                cellRowName.setCellStyle(columnTopStyle);                        //设置列头单元格样式
            }
            
            //将查询出的数据设置到sheet对应的单元格中
            for(int i=0;i<dataList.size();i++){
                
                Object[] obj = dataList.get(i);//遍历每个对象
                HSSFRow row = sheet.createRow(i+2);//创建所需的行数
                
                for(int j=0; j<obj.length; j++){
                    HSSFCell  cell = null;   //设置单元格的数据类型
//                    if(j == 0){
//                        cell = row.createCell(j,HSSFCell.CELL_TYPE_NUMERIC);
//                        cell.setCellValue(i+1);    
//                    }else{
//                        cell = row.createCell(j,HSSFCell.CELL_TYPE_STRING);
//                        if(!"".equals(obj[j]) && obj[j] != null){
//                            cell.setCellValue(obj[j].toString());                        //设置单元格的值
//                        }
//                    }
                    cell = row.createCell(j,HSSFCell.CELL_TYPE_STRING);
                    if(!"".equals(obj[j]) && obj[j] != null){
                        cell.setCellValue(obj[j].toString());                        //设置单元格的值
                    }
                    cell.setCellStyle(style);                                    //设置单元格样式
                }
            }
            //让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                // 根据需求实现，因为需求的第一行是合并的大标题，所以这里列宽自适应从第2行（rowNum = 1）开始
                for (int rowNum = 1; rowNum < sheet.getLastRowNum(); rowNum++) {
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
                            String cellValue = "";
                            try
                            {
                                cellValue = currentCell.getStringCellValue();
                            }
                            catch (Exception e)
                            {}
                            if(AssertUtil.isNotEmpty(currentCell) && AssertUtil.isNotEmpty(cellValue)){
                                // 一个汉字占两位（GBK）来计算，否则汉字多的情况下，空白内容太宽
                                int length = currentCell.getStringCellValue().trim().getBytes("GBK").length;
                                if (columnWidth < length) {
                                    columnWidth = length;
                                }
                            }
                        }
                    }
                }
                // 设置列宽度值，针对不同的列 做一个宽度度控制处理，+4可以使内容不那么贴近边框
                sheet.setColumnWidth(colNum, (columnWidth+4) * 256);
            }
            
            if(workbook !=null){
                OutputStream out = null;
                try
                {
                    if (needMerge && AssertUtil.isNotEmpty(dataList))
                    {
                        // 将第一列进行值相同的行单元格合并
                        addMergedRegion(sheet, 0, 2, sheet.getLastRowNum(),
                            workbook);
                    }
                    // 写文件
                    out = new FileOutputStream(path);
                    workbook.write(out);
                    out.flush();
                }
                catch (Exception e1)
                {
                    log.error("exportExecl out file failure.", e1);
                }
                finally
                {
                    StreamUtil.close(out);
                }
            }

        }catch(Exception e){
            log.error("exportExecl failure.",e);
        }
        
    }    
    
    /**  
     * 合并单元格  
     * @param sheet 要合并单元格的excel 的sheet工作表对象
     * @param cellLine  要合并的列  
     * @param startRow  要合并列的开始行  
     * @param endRow    要合并列的结束行 
     * @param workBook  excel整体工作薄对象
     */  
    private static void addMergedRegion(HSSFSheet sheet, int cellLine,
        int startRow, int endRow, HSSFWorkbook workBook)
    {

        HSSFCellStyle style = workBook.createCellStyle(); // 样式对象    
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
        //设置左边框;   
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色; 
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        
        // 获取第一行的数据,以便后面进行比较
        String s_will = sheet.getRow(startRow).getCell(cellLine)
            .getStringCellValue();
        // 上下两列数据是否比对成功标识
        boolean flag = false;
        // 比对成功计数
        int count = 0;
        
        for (int i = 1; i <= endRow; i++)
        {
            String s_current = "";
            // 兼容一下获取不到列数据值的情况（比如单元格是被合并过的情况）
            if (sheet.getRow(i).getCell(0) != null)
            {
                s_current = sheet.getRow(i).getCell(0).getStringCellValue();
            }
            if (s_will.equals(s_current))
            {
                flag = true;
                count ++;
            }
            else
            {
               if(flag){
                   // 合并参数： 首行----当前的行减去要合并的行数
                   // 最后一行----当前行数减去1，因为当前行与上一行不匹配才合并
                   // 首列、最后一列   单列合并，所以这里两个值都是一样的
                   sheet.addMergedRegion(new CellRangeAddress(startRow-count,
                       i-1, cellLine, cellLine));
                   HSSFRow row = sheet.getRow(startRow-count);
                   String cellValueTemp = sheet.getRow(startRow-count)
                       .getCell(0).getStringCellValue();
                   HSSFCell cell = row.createCell(0);
                   cell.setCellValue(cellValueTemp); // 跨单元格显示的数据
                   cell.setCellStyle(style); // 样式 主要为了设置合并后居中
                   count = 0;
                   flag = false;
               }
            }
            s_will = s_current;
            startRow = i;
            
            // 由于上面循环中合并的单元放在有下一次不相同单元格的时候做的，
            // 所以最后如果几行有相同单元格则要运行下面的合并单元格。
            if (i == endRow &&  flag)
            {
                sheet.addMergedRegion(new CellRangeAddress(endRow - count,
                    endRow, cellLine, cellLine));
                String cellValueTemp = sheet.getRow(endRow - count).getCell(0)
                    .getStringCellValue();
                HSSFRow row = sheet.getRow(endRow - count);
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(cellValueTemp); // 跨单元格显示的数据
                cell.setCellStyle(style); // 样式 主要为了设置合并后居中
            }
        }
    }
    
    /* 
     * 列头单元格样式
     */    
      public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {
          
            // 设置字体
          HSSFFont font = workbook.createFont();
          //设置字体大小
          font.setFontHeightInPoints((short)11);
          //字体加粗
          font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
          //设置字体名字 
          font.setFontName("Courier New");
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
          //设置背景颜色
          style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
          style.setFillForegroundColor(HSSFColor.YELLOW.index);
          
          return style;
          
      }
      
      /*  
     * 列数据信息单元格样式
     */  
      public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
            // 设置字体
            HSSFFont font = workbook.createFont();
            //设置字体大小
            //font.setFontHeightInPoints((short)10);
            //字体加粗
            //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            //设置字体名字 
            font.setFontName("Courier New");
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
            //style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //设置垂直对齐的样式为居中对齐; 
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
           
            return style;
      
      }
      
      public static void main(String[] args)
    {
          String title = "测试宝宝";
          String[] rowsName = new String[]{"序号","货物运输批次号","提运单号","状态","录入人","录入时间"};
          List<Object[]>  dataList = new ArrayList<Object[]>();
          Object [] objs = null;
          objs = new String[rowsName.length];
          objs[0] = "是因为呼呼Y鱼鱼回就怎么回还好还好哈";
          objs[1] = "123";
          objs[2] = "456";
          objs[3] = "哈哈哈哈";
          objs[4] = "呵呵和呵呵";
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String date = df.format(new Date());
          objs[5] = date;
          dataList.add(objs);
          objs = new String[rowsName.length];
          objs[0] = "是因为呼呼Y鱼鱼回就怎么回还好还好哈";
          objs[1] = "1233";
          objs[2] = "4564";
          objs[3] = "哈哈哈哈12";
          objs[4] = "呵呵和呵12";
          SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          date = df2.format(new Date());
          objs[5] = date;
          dataList.add(objs);
          
          objs = new String[rowsName.length];
          objs[0] = "8";
          objs[1] = "123323";
          objs[2] = "4564121";
          objs[3] = "哈哈哈哈1122";
          objs[4] = "呵呵和呵1212";
          SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          date = df3.format(new Date());
          objs[5] = date;
          dataList.add(objs);
          
          objs = new String[rowsName.length];
          objs[0] = "6";
          objs[1] = "323323";
          objs[2] = "464121";
          objs[3] = "5哈哈哈1122";
          objs[4] = "5呵和呵1212";
          SimpleDateFormat df4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          date = df4.format(new Date());
          objs[5] = date;
          dataList.add(objs);
          
          objs = new String[rowsName.length];
          objs[0] = "6";
          objs[1] = "3323323";
          objs[2] = "23464121";
          objs[3] = "6哈哈哈1122";
          objs[4] = "6呵和呵1212";
          SimpleDateFormat df5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          date = df5.format(new Date());
          objs[5] = date;
          dataList.add(objs);
          objs = new String[rowsName.length];
          objs[0] = "7";
          objs[1] = "3323323";
          objs[2] = "23464121";
          objs[3] = "6哈哈哈1122";
          objs[4] = "6呵和呵1212";
          SimpleDateFormat df6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          date = df6.format(new Date());
          objs[5] = date;
          dataList.add(objs);
          ExeclUtil util = new ExeclUtil();
          String fileName = "您好-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
          String path = "D:\\temp\\" + fileName;
          util.export(title, rowsName, dataList,path,true);
    }
}

