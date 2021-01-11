package org.linlinjava.litemall.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PoiExcelExport {
    
    // 文件名
    private String fileName;
    // sheet名
    private String sheetName;
    // 表头字体
    private String titleFontType = "Arial Unicode MS";
    // 表头背景色
    private String titleBackColor = "C1FBEE";
    // 表头字号
    private short titleFontSize = 12;
    // 添加自动筛选的列 如 A:M
    private String address = "";
    // 正文字体
    private String contentFontType = "Arial Unicode MS";
    // 正文字号
    private short contentFontSize = 12;
    // Float类型数据小数位
    private String floatDecimal = ".00";
    // Double类型数据小数位
    private String doubleDecimal = ".00";
    // 设置列的公式
    private String colFormula[] = null;

    DecimalFormat floatDecimalFormat = new DecimalFormat(floatDecimal);
    DecimalFormat doubleDecimalFormat = new DecimalFormat(doubleDecimal);
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private HSSFWorkbook workbook = null;

    public PoiExcelExport(String fileName, String sheetName) {
        this.fileName = fileName;
        this.sheetName = sheetName;
        workbook = new HSSFWorkbook();
    }

    public PoiExcelExport(HttpServletResponse response, String fileName,
                          String sheetName) {
        this.sheetName = sheetName;
        workbook = new HSSFWorkbook();
    }

    /**
     * 设置表头字体.
     * 
     * @param titleFontType
     */
    public void setTitleFontType(String titleFontType) {
        this.titleFontType = titleFontType;
    }

    /**
     * 设置表头背景色.
     * 
     * @param titleBackColor
     *            十六进制
     */
    public void setTitleBackColor(String titleBackColor) {
        this.titleBackColor = titleBackColor;
    }

    /**
     * 设置表头字体大小.
     * 
     * @param titleFontSize
     */
    public void setTitleFontSize(short titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    /**
     * 设置表头自动筛选栏位,如A:AC.
     * 
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 设置正文字体.
     * 
     * @param contentFontType
     */
    public void setContentFontType(String contentFontType) {
        this.contentFontType = contentFontType;
    }

    /**
     * 设置正文字号.
     * 
     * @param contentFontSize
     */
    public void setContentFontSize(short contentFontSize) {
        this.contentFontSize = contentFontSize;
    }

    /**
     * 设置float类型数据小数位 默认.00
     * 
     * @param doubleDecimal
     *            如 ".00"
     */
    public void setDoubleDecimal(String doubleDecimal) {
        this.doubleDecimal = doubleDecimal;
    }

    /**
     * 设置doubel类型数据小数位 默认.00
     * 
     * @param floatDecimalFormat
     *            如 ".00
     */
    public void setFloatDecimalFormat(DecimalFormat floatDecimalFormat) {
        this.floatDecimalFormat = floatDecimalFormat;
    }

    /**
     * 设置列的公式
     * 
     * @param colFormula
     *            存储i-1列的公式 涉及到的行号使用@替换 如A@+B@
     */
    public void setColFormula(String[] colFormula) {
        this.colFormula = colFormula;
    }

    /**
     * 写excel.
     * 
     * @param titleColumn
     *            对应bean的属性名
     * @param titleName
     *            excel要导出的表名
     * @param dataList
     *            数据
     */
    public void wirteExcel(String titleColumn[], String titleName[],
                           List<?> dataList, HttpServletResponse response) {
        // 添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet = workbook.createSheet(this.sheetName);
        // 新建文件
        OutputStream out = null;
        try {
            // 直接写到输出流中
            out = response.getOutputStream();
            fileName = fileName + ".xls";
            response.setContentType("application/vnd.ms-excel");
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename="
                            + URLEncoder.encode(fileName, "UTF-8"));

            // 写入excel的表头
            Row titleNameRow = workbook.getSheet(sheetName).createRow(0);
            // 设置样式
            HSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle = (HSSFCellStyle) setFontAndBorder(titleStyle,
                    titleFontType, (short) titleFontSize);
            titleStyle = (HSSFCellStyle) setColor(titleStyle, titleBackColor,
                    (short) 10);

            for (int i = 0; i < titleName.length; i++) {
                sheet.setColumnWidth(i, 20 * 256); // 设置宽度
                Cell cell = titleNameRow.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(titleName[i].toString());
            }

            // 为表头添加自动筛选
            if (!"".equals(address)) {
                CellRangeAddress c = (CellRangeAddress) CellRangeAddress
                        .valueOf(address);
                sheet.setAutoFilter(c);
            }

            // 通过反射获取数据并写入到excel中
            if (dataList != null && dataList.size() > 0) {
                // 设置样式
                HSSFCellStyle dataStyle = workbook.createCellStyle();
                titleStyle = (HSSFCellStyle) setFontAndBorder(titleStyle,
                        contentFontType, (short) contentFontSize);

                if (titleColumn.length > 0) {
                    for (int rowIndex = 1; rowIndex <= dataList.size(); rowIndex++) {
                        Object obj = dataList.get(rowIndex - 1); // 获得该对象
                        Class clsss = obj.getClass(); // 获得该对对象的class实例
                        Row dataRow = workbook.getSheet(sheetName).createRow(
                                rowIndex);
                        for (int columnIndex = 0; columnIndex < titleColumn.length; columnIndex++) {
                            String title = titleColumn[columnIndex].toString()
                                    .trim();
                            if("-".equals(title)) {//特殊处理，值为null，导入用
                                dataRow.createCell(columnIndex).setCellValue("");
                            }else if (!"".equals(title)) { // 字段不为空
                                // 使首字母大写
                                String UTitle = Character.toUpperCase(title .charAt(0)) + title.substring(1, title.length()); // 使其首字母大写;
                                String methodName = "get" + UTitle;
                                // 设置要执行的方法
                                Method method = null;
                                try {
                                    method = clsss.getDeclaredMethod(methodName);
                                } catch (NoSuchMethodException e) {
                                    method = clsss.getDeclaredMethod("get"+title);
                                }

                                // 获取返回类型
                                String returnType = method.getReturnType()
                                        .getName();

                                String data = method.invoke(obj) == null ? ""
                                        : method.invoke(obj).toString();
                                Cell cell = dataRow.createCell(columnIndex);
                                if (data != null && !"".equals(data)) {
                                    if ("java.lang.Integer".equals(returnType)) {
                                        cell.setCellValue(Integer.parseInt(data));
                                    } else if ("java.lang.Long".equalsIgnoreCase(returnType)) {
                                        cell.setCellValue(Long.parseLong(data));
                                    } else if ("java.lang.Float".equalsIgnoreCase(returnType)) {
                                        cell.setCellValue(floatDecimalFormat.format(Float.parseFloat(data)));
                                    } else if ("java.lang.Double".equalsIgnoreCase(returnType)) {
                                        cell.setCellValue(doubleDecimalFormat.format(Double.parseDouble(data)));
                                    } else if("java.util.Date".equalsIgnoreCase(returnType)){
                                        cell.setCellValue(sdf.format(new Date(data)));
                                    } else if("java.time.LocalDateTime".equalsIgnoreCase(returnType)){
                                        cell.setCellValue(df.format(LocalDateTime.parse(data)));
                                    } else {
                                        cell.setCellValue(data);
                                    }
                                }
                            } else { // 字段为空 检查该列是否是公式
                                if (colFormula != null) {
                                    String sixBuf = colFormula[columnIndex]
                                            .replace("@", (rowIndex + 1) + "");
                                    Cell cell = dataRow.createCell(columnIndex);
                                    cell.setCellFormula(sixBuf.toString());
                                }
                            }
                        }
                    }

                }
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * @Title: wirteExcelByMap
     * @Description: 适合list<Map<String,object>> 格式的导出
     * @param titleColumn    对应bean的属性名
     * @param titleName 要导出的表名
     * @param dataList  导出数据
     * @param response
     * @author chenruide
     * @date 2019-12-02 02:43:55
     */
    public void wirteExcelByMap(String titleColumn[], String titleName[],
                                List<?> dataList, HttpServletResponse response) {
        // 添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet = workbook.createSheet(this.sheetName);
        // 新建文件
        OutputStream out = null;
        try {
            // 直接写到输出流中
            out = response.getOutputStream();
            fileName = fileName + ".xls";
            response.setContentType("application/vnd.ms-excel");
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename="
                            + URLEncoder.encode(fileName, "UTF-8"));

            // 写入excel的表头
            Row titleNameRow = workbook.getSheet(sheetName).createRow(0);
            // 设置样式
            HSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle = (HSSFCellStyle) setFontAndBorder(titleStyle,
                    titleFontType, (short) titleFontSize);
            titleStyle = (HSSFCellStyle) setColor(titleStyle, titleBackColor,
                    (short) 10);

            for (int i = 0; i < titleName.length; i++) {
                sheet.setColumnWidth(i, 20 * 256); // 设置宽度
                Cell cell = titleNameRow.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(titleName[i].toString());
            }

            // 为表头添加自动筛选
            if (!"".equals(address)) {
                CellRangeAddress c = (CellRangeAddress) CellRangeAddress
                        .valueOf(address);
                sheet.setAutoFilter(c);
            }

            // 通过反射获取数据并写入到excel中
            if (dataList != null && dataList.size() > 0) {
                titleStyle = (HSSFCellStyle) setFontAndBorder(titleStyle,
                        contentFontType, (short) contentFontSize);

                if (titleColumn.length > 0) {
                    for (int rowIndex = 1; rowIndex <= dataList.size(); rowIndex++) {
                        Map<String, Object> obj = (Map<String, Object>) dataList.get(rowIndex - 1); // 获得该对象
                        Row dataRow = workbook.getSheet(sheetName).createRow(rowIndex);
                        for (int columnIndex = 0; columnIndex < titleColumn.length; columnIndex++) {
                            String title = titleColumn[columnIndex].toString().trim();
                            if (!"".equals(title)) { // 字段不为空
                                Cell cell = dataRow.createCell(columnIndex);
                                if(StringUtils.isBlank(obj.get(title)+"")) {
                                    cell.setCellValue("");
                                }else {
                                    if(StringUtils.isNotBlank(obj.get(title)+"") && !"null".equals(obj.get(title)+"")) {
                                        cell.setCellValue(obj.get(title)+"");
                                    }else {
                                        cell.setCellValue("");
                                    }
                                }
                            } else { // 字段为空 检查该列是否是公式
                                if (colFormula != null) {
                                    String sixBuf = colFormula[columnIndex]
                                            .replace("@", (rowIndex + 1) + "");
                                    Cell cell = dataRow.createCell(columnIndex);
                                    cell.setCellFormula(sixBuf.toString());
                                }
                            }
                        }
                    }

                }
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将16进制的颜色代码写入样式中来设置颜色
     * 
     * @param style
     *            保证style统一
     * @param color
     *            颜色：66FFDD
     * @param index
     *            索引 8-64 使用时不可重复
     * @return
     */
    public CellStyle setColor(CellStyle style, String color, short index) {
        if (color != "" && color != null) {
            // 转为RGB码
            int r = Integer.parseInt((color.substring(0, 2)), 16); // 转为16进制
            int g = Integer.parseInt((color.substring(2, 4)), 16);
            int b = Integer.parseInt((color.substring(4, 6)), 16);
            // 自定义cell颜色
            HSSFPalette palette = workbook.getCustomPalette();
            palette.setColorAtIndex((short) index, (byte) r, (byte) g, (byte) b);

            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(index);
        }
        return style;
    }

    /**
     * 设置字体并加外边框
     * 
     * @param style
     *            样式
     * @param style
     *            字体名
     * @param style
     *            大小
     * @return
     */
    public CellStyle setFontAndBorder(CellStyle style, String fontName,
            short size) {
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints(size);
        font.setFontName(fontName);
    //  font.setBold(true);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN); // 下边框
        style.setBorderLeft(BorderStyle.THIN);// 左边框
        style.setBorderTop(BorderStyle.THIN);// 上边框
        style.setBorderRight(BorderStyle.THIN);// 右边框
        return style;
    }
    
     /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 
     * @param path
     *            文件夹路径
     */
    public static void isExist(String path) {
        File file = new File(path);
        // 判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void publicMethod(String tabelName, String sheetName,
                                    String titleColumn[], String titleName[], List<?> lists, HttpServletResponse response) {
        Date date = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        String remotefile = tabelName + "_" + sdFormat.format(date);
        PoiExcelExport pee = new PoiExcelExport(remotefile, sheetName);
        pee.wirteExcel(titleColumn, titleName, lists,response);
    }
    
    /**
     * @Title: exportByMap
     * @Description: 导出excel
     * @param tabelName 导出表格名
     * @param sheetName 
     * @param titleColumn   对应bean的属性名
     * @param titleName  表格展示的头
     * @param lists list<Map> 格式的数据
     * @param response
     * @author chenruide
     * @date 2019-12-02 03:34:31
     */
    public static void exportByMap(String tabelName, String sheetName,
                                   String titleColumn[], String titleName[], List<?> lists, HttpServletResponse response) {
        Date date = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        String remotefile = tabelName + "_" + sdFormat.format(date);
        PoiExcelExport pee = new PoiExcelExport(remotefile, sheetName);
        pee.wirteExcelByMap(titleColumn, titleName, lists,response);
    }
    /**
     * 获取前一天日期
     * @param date
     * @return
     */
    public static Date getLastoneDate(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1); //今天的时间加一天  
        date = calendar.getTime();  
        return date;          
    }  
}