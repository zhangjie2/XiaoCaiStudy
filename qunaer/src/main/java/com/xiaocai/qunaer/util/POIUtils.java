/**
 * Copyright(c) 2011-2012 by YouCredit Inc.
 * All Rights Reserved
 */
package com.xiaocai.qunaer.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;


/**
 * @author ijay
 */
public class POIUtils {
    public static Row buildRowWithStringValues(Sheet sheet, int rowIndex,
            int startCol, short hAlign, short vAlign, String... values) {
        Row row = sheet.createRow(rowIndex);

        for (int i = startCol; i < values.length + startCol; i++) {
            POIUtils.createCell(row, i, hAlign, vAlign).setCellValue(
                values[i - startCol]);
        }

        return row;
    }

    public static Row buildRowWithValues(Sheet sheet, int rowIndex,
            int startCol, String... values) {
        return POIUtils.buildRowWithStringValues(sheet, rowIndex, startCol,
            CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_TOP, values);
    }

    public static Row buildRowWithStringValues(Sheet sheet, int rowIndex,
            int startCol, String... values) {
        return POIUtils.buildRowWithStringValues(sheet, rowIndex, startCol,
            CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_TOP, values);
    }

    public static void buildColumnWithValues(Sheet sheet, int colIndex,
            int startRow, short hAlign, short vAlign, String... values) {
        for (int i = startRow; i < startRow + values.length; i++) {
            Row r = sheet.createRow(i);
            POIUtils.createCell(r, colIndex, hAlign, vAlign).setCellValue(
                values[i - startRow]);
        }
    }

    public static void buildColumnWithValues(Sheet sheet, int colIndex,
            int startRow, String... values) {
        POIUtils.buildColumnWithValues(sheet, colIndex, startRow,
            CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_TOP, values);
    }

    public static Cell createCell(Row row, int index, short hAlign, short vAlign) {
        Cell cell = row.createCell(index);

        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        style.setAlignment(hAlign);
        style.setVerticalAlignment(vAlign);
        cell.setCellStyle(style);

        return cell;
    }

    public static Cell createCell(Row row, int index) {
        return POIUtils.createCell(row, index, CellStyle.ALIGN_RIGHT,
            CellStyle.VERTICAL_TOP);
    }

    /**
     * 鏋勫缓鈥滐拷?璁★拷?鍗曞厓锟�
     *
     * @param cell
     * @param size
     * @return
     */
    public static Cell sumCell(Cell cell, int size) {
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        StringBuilder sumBuilder = new StringBuilder(5);
        sumBuilder
            .append("SUM(")
            .append(new CellReference(rowIndex + 1, colIndex).formatAsString())
            .append(':')
            .append(
                new CellReference(rowIndex + size, colIndex).formatAsString())
            .append(')');
        cell.setCellFormula(sumBuilder.toString());
        return cell;
    }

    /**
     * 鏍煎紡鍖栧崟鍏冩牸鏍煎紡涓洪噾棰濇牱锟�
     *
     * @param cell
     * @return
     */
    public static Cell formatCell4Amount(Cell cell) {
        Workbook workbook = cell.getSheet().getWorkbook();
        short amountFormat = workbook.createDataFormat().getFormat("#,##0.00");
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setDataFormat(amountFormat);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public static String convertToCSV(Sheet sheet, int columns) {
        Iterator<Row> rows = sheet.iterator();
        StringBuilder sb = new StringBuilder();

        while (rows.hasNext()) {
            Row row = rows.next();

            for (int i = 0; i < columns; i++) {
                Cell cell = row.getCell(i);

                if (cell != null) {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            sb.append(cell.getStringCellValue());
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            NumberFormat format = NumberFormat.getInstance();
                            format.setGroupingUsed(false);
                            sb.append(format.format(cell.getNumericCellValue()));
                            break;
                    }
                }

                sb.append(',');
            }
            sb.delete(sb.length() - 1, sb.length()).append("\r\n");// 鍒犳帀锟�锟斤拷锟�锟斤拷閫楀彿锛屽姞鍥炶溅
        }

        return sb.toString();
    }

    /**
     * 鑾峰彇鍗曞厓鏍硷拷?
     *
     * @param cell
     * @param allowBlank
     * @param defaultValue
     * @return
     */
    public static <T> T getValue(Cell cell, boolean allowBlank, T defaultValue) {
        if (cell == null) {
            return defaultValue;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
            case Cell.CELL_TYPE_BLANK:
                String str = StringUtils.trimToEmpty(cell.getStringCellValue());
                if (!allowBlank && StringUtils.isBlank(str)) {
                    str = (String) defaultValue;
                }

                return (T) str;
            case Cell.CELL_TYPE_NUMERIC:
                return (T) new BigDecimal(cell.getNumericCellValue() + "");
        }

        return defaultValue;
    }

    /**
     * 锟�锟斤拷姣忎竴琛屾暟锟�
     *
     * @param row
     * @param format
     *        鏍煎紡
     * @throws Exception 
     * @throws InvalidDataException
     */
    public static void checkFormatStrByRow(Row row, String... format) throws Exception
           {
        if (row == null) {
            throw new Exception("楠岃瘉鐨勮涓嶈兘涓虹┖");
        }

        if (!ArrayUtils.isEmpty(format)) {
            for (int i = 0; i < format.length; i++) {
                String formatStr = POIUtils.getValue(row.getCell(i), true, "");
                if (!format[i].equalsIgnoreCase(formatStr)) {
                    throw new Exception("");
                }
            }
        }
    }

    /**
     * @param sheet
     * @param format
     *        鏍煎紡
     * @param valid
     *        鏄惁楠岃瘉姣忎竴涓崟鍏冩牸
     * @return
     */
    public static List<Map<String, Object>> initExcelData(Sheet sheet,
            String[] format, boolean valid) {
        List<Map<String, Object>> readDataList = new LinkedList<>();
        // 鍒濆鍖栧鍏ユ暟锟�
        Row row = null;
        for (int rowIndex = 1; (row = sheet.getRow(rowIndex)) != null; rowIndex++) {
            Map<String, Object> readData = new HashMap<>();
            Boolean isValid = true;
            readData.put("valid", false);
            int i = 0;
            for (int cellIndex = 0; cellIndex < format.length; cellIndex++) {
                String key = format[cellIndex];
                Cell cell = row.getCell(cellIndex);
                String remark = "";
                Object value = POIUtils.getValue(cell, false, null);

                if (valid
                    && (value == null || StringUtils.isBlank(value.toString()))) {// 楠岃瘉鏄惁鍚堟硶
                    remark = key + "涓嶈兘涓虹┖";
                    isValid = false;
                }

                readData.put("remark", readData.get("remark") == null ? remark
                    : readData.get("remark") + remark);
                readData.put(key, value);
                if (value == null || StringUtils.isBlank(value.toString())) {
                    i++;
                }
            }
            if (i == format.length) {
                continue;
            }
            readData.put("valid", isValid);
            readDataList.add(readData);
        }
        return readDataList;
    }

    /**
     * 鍚慐xcel row 涓啓鍏ユ暟锟�
     *
     * @param line
     * @param index
     * @param sheet
     * @param rowNo
     */
    public static void fillRow(List<String> line, int index, Sheet sheet,
            int rowNo) {
        Row row = sheet.createRow(rowNo);
        for (String string : line) {
            if (index == -1) {
                index++;
                continue;
            }
            row.createCell(index).setCellValue(string);
            index++;
        }
    }

    /**
     * 鑾峰彇鍗曞厓锟�鏃ユ湡锟�
     *
     * @param xssfCell
     * @return Date
     */
    public static Date getDateValue(XSSFCell xssfCell) {
        Date result = null;
        if (xssfCell == null || xssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || xssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(xssfCell)) {// 澶勭悊鏃ユ湡鏍煎紡銆佹椂闂存牸锟�
                result = xssfCell.getDateCellValue();
            } else if (xssfCell.getCellStyle().getDataFormat() == 58) {
                // 澶勭悊鑷畾涔夋棩鏈熸牸寮忥細m鏈坉锟�閫氳繃鍒ゆ柇鍗曞厓鏍肩殑鏍煎紡id瑙ｅ喅锛宨d鐨勶拷?锟�8)
                double value = xssfCell.getNumericCellValue();
                result = DateUtil.getJavaDate(value);
            }
            return result;
        } else if (xssfCell.getCellType() == Cell.CELL_TYPE_STRING) {
            String str = xssfCell.getStringCellValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            if (StringUtils.isNotBlank(str) && str.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            } else if (StringUtils.isNotBlank(str) && str.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd");
            } else if (StringUtils.isNotBlank(str) && str.contains("?")) {
                sdf = new SimpleDateFormat("yyyyMMdd");
            } else if (StringUtils.isNotBlank(str) && str.contains("\\.")) {
                sdf = new SimpleDateFormat("yyyy.MM.dd");
            }
            try {
                result = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�鏃ユ湡锟�
     *
     * @param hssfCell
     * @return Date
     */
    public static Date getDateValue(HSSFCell hssfCell) {
        Date result = null;
        if (hssfCell == null || hssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || hssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(hssfCell)) {// 澶勭悊鏃ユ湡鏍煎紡銆佹椂闂存牸锟�
                result = hssfCell.getDateCellValue();
            } else if (hssfCell.getCellStyle().getDataFormat() == 58) {
                // 澶勭悊鑷畾涔夋棩鏈熸牸寮忥細m鏈坉锟�閫氳繃鍒ゆ柇鍗曞厓鏍肩殑鏍煎紡id瑙ｅ喅锛宨d鐨勶拷?锟�8)
                double value = hssfCell.getNumericCellValue();
                result = DateUtil.getJavaDate(value);
            } else {
                int value = ((Double) hssfCell.getNumericCellValue())
                    .intValue();
                if (value > 0
                    && StringUtils.length(value + StringUtils.EMPTY) == 8) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    try {
                        result = sdf.parse(value + StringUtils.EMPTY);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            }
            return result;
        } else if (hssfCell.getCellType() == Cell.CELL_TYPE_STRING) {
            String str = hssfCell.getStringCellValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            if (StringUtils.isBlank(str)) {
                return null;
            }
            if (str.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            } else if (str.contains("/") && str.contains(":")) {
                str = StringUtils.replace(str, "  ", " ");
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            } else if (str.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd");
            } else if (NumberUtils.isNumber(str)) {
                sdf = new SimpleDateFormat("yyyyMMdd");
            } else if (str.contains("?")) {
                sdf = new SimpleDateFormat("yyyyMMdd");
            } else if (str.contains("\\.")) {
                sdf = new SimpleDateFormat("yyyy.MM.dd");
            }
            try {
                result = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�鏁板瓧锟�
     *
     * @param xssfCell
     * @return Date
     */
    public static BigDecimal getBigDecimalValue(XSSFCell xssfCell) {
        BigDecimal result = BigDecimal.ZERO;
        if (xssfCell == null || xssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || xssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            Double value = xssfCell.getNumericCellValue();
            result = new BigDecimal(value.toString());
        } else if (xssfCell.getCellType() == Cell.CELL_TYPE_STRING) {
            String str = xssfCell.getStringCellValue();
            str = StringUtils.remove(str, '?');
            str = StringUtils.remove(str, '?');
            str = StringUtils.remove(str, "涓囧厓");
            str = StringUtils.remove(str, ',');
            if (str != null && str.matches("^[0-9]+(.[0-9]{0,4})?$")) {
                result = new BigDecimal(str);
            }
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�鏁板瓧锟�
     *
     * @param hssfCell
     * @return Date
     */
    public static BigDecimal getBigDecimalValue(HSSFCell hssfCell) {
        BigDecimal result = BigDecimal.ZERO;
        if (hssfCell == null || hssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || hssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            Double value = hssfCell.getNumericCellValue();
            result = new BigDecimal(value.toString() + StringUtils.EMPTY);
        } else if (hssfCell.getCellType() == Cell.CELL_TYPE_STRING) {
            String str = StringUtils.trimToEmpty(hssfCell.getStringCellValue());
            str = StringUtils.remove(str, '?');
            str = StringUtils.remove(str, '?');
            str = StringUtils.remove(str, "涓囧厓");
            str = StringUtils.remove(str, ',');
            str = StringUtils.remove(str, '-');
            str = StringUtils.remove(str, '?');
            str = StringUtils.remove(str, '_');
            if (str != null && str.matches("^[0-9]+(.[0-9]{0,4})?$")) {
                result = new BigDecimal(str);
            }
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�瀛楃锟�
     *
     * @param xssfCell
     * @return Date
     */
    public static String getStringValue(XSSFCell xssfCell) {
        String result = StringUtils.EMPTY;
        if (xssfCell == null || xssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || xssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (Cell.CELL_TYPE_STRING == xssfCell.getCellType()) {
            return StringUtils.trimToEmpty(xssfCell.getStringCellValue());
        } else if (Cell.CELL_TYPE_NUMERIC == xssfCell.getCellType()) {
            double value = xssfCell.getNumericCellValue();
            BigDecimal temp = new BigDecimal("" + value);
            result = StringUtils.trimToEmpty(temp.toPlainString());
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�瀛楃锟�
     *
     * @param hssfCell
     * @return Date
     */
    public static String getStringValue(HSSFCell hssfCell) {
        String result = StringUtils.EMPTY;
        if (hssfCell == null || hssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || hssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (Cell.CELL_TYPE_STRING == hssfCell.getCellType()) {
            return StringUtils.trimToEmpty(hssfCell.getStringCellValue());
        } else if (Cell.CELL_TYPE_NUMERIC == hssfCell.getCellType()) {
            double value = hssfCell.getNumericCellValue();
            BigDecimal temp = new BigDecimal("" + value);
            result = StringUtils.trimToEmpty(temp.toPlainString());
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�瀛楃锟�
     *
     * @param xssfCell
     * @return Date
     */
    public static int getIntValue(XSSFCell xssfCell) {
        int result = 0;
        if (xssfCell == null || xssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || xssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (Cell.CELL_TYPE_STRING == xssfCell.getCellType()) {
            String temp = StringUtils
                .trimToEmpty(xssfCell.getStringCellValue());
            if (StringUtils.isNotBlank(temp) && temp.matches("\\d{1,10}")) {
                try {
                    result = Integer.parseInt(temp);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else if (Cell.CELL_TYPE_NUMERIC == xssfCell.getCellType()) {
            double value = xssfCell.getNumericCellValue();
            BigDecimal temp = new BigDecimal("" + value);
            result = temp.intValue();
        }
        return result;
    }

    /**
     * 鑾峰彇鍗曞厓锟�瀛楃锟�
     * Thread-9072
     * 
     * @param hssfCell
     * @return Date
     */
    public static int getIntValue(HSSFCell hssfCell) {
        int result = 0;
        if (hssfCell == null || hssfCell.getCellType() < Cell.CELL_TYPE_NUMERIC
            || hssfCell.getCellType() >= Cell.CELL_TYPE_ERROR) {
            return result;
        }
        if (Cell.CELL_TYPE_STRING == hssfCell.getCellType()) {
            String temp = StringUtils
                .trimToEmpty(hssfCell.getStringCellValue());
            if (StringUtils.isNotBlank(temp) && temp.matches("\\d{1,10}")) {
                try {
                    result = Integer.parseInt(temp);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else if (Cell.CELL_TYPE_NUMERIC == hssfCell.getCellType()) {
            double value = hssfCell.getNumericCellValue();
            BigDecimal temp = new BigDecimal("" + value);
            result = temp.intValue();
        }
        return result;
    }
}
