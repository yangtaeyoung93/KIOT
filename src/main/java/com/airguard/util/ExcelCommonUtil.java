package com.airguard.util;

import com.airguard.model.system.DeviceElements;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelCommonUtil {

  public static void writeListToFile(String fileName, List<HashMap<String, Object>> exList,
      HttpServletResponse res)
      throws Exception {
    Workbook workbook = null;

    if (fileName.endsWith("xlsx")) {
      workbook = new XSSFWorkbook();
    } else if (fileName.endsWith("xls")) {
      workbook = new HSSFWorkbook();
    }

    assert workbook != null;
    Sheet sheet = workbook.createSheet("cordova");

    int rowIndex = 0;
    int excelHead = 0;

    for (HashMap<String, Object> obj : exList) {
      Row row = sheet.createRow(rowIndex++);

      if (excelHead == 0) {
        Cell cell0 = row.createCell(0);
        cell0.setCellValue("Colum Head 1");
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("Colum Head 2");
        Cell cell2 = row.createCell(2);
        cell2.setCellValue("Colum Head 3");
        excelHead++;

      } else {
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(obj.get("").toString());
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("obj.get() 2");
        Cell cell2 = row.createCell(2);
        cell2.setCellValue("obj.get() 3");

      }
    }

    sheet.setColumnWidth(0, 3000);
    sheet.setColumnWidth(1, 10000);
    sheet.setColumnWidth(2, 7000);

    res.setHeader("Content-Disposition", "attachment;fileName=test22.xlsx");
    res.setContentType("application/vnd.ms-excel");

    OutputStream out = new BufferedOutputStream(res.getOutputStream());
    workbook.write(out);
    out.flush();
    out.close();
  }

  public static void exampleFile(HttpServletResponse res) throws Exception {
    Workbook workbook;
    workbook = new XSSFWorkbook();

    Sheet sheet = workbook.createSheet("cordova");

    Row row = sheet.createRow(0);

    Cell cell0 = row.createCell(0);
    cell0.setCellValue("시리얼 번호 (CELL_TYPE_STRING)");
    Cell cell1 = row.createCell(1);
    cell1.setCellValue("생산 일자 (Etc, YYYY-MM-DD)");
    Cell cell2 = row.createCell(2);
    cell2.setCellValue("테스트 여부 (Etc, Y or N)");

    for (int i = 1; i < 2; i++) {
      Row rowEx = sheet.createRow(i);
      Cell cellEx1 = rowEx.createCell(0);
      cellEx1.setCellType(Cell.CELL_TYPE_STRING);
      Cell cellEx2 = rowEx.createCell(1);
      cellEx2.setCellType(Cell.CELL_TYPE_STRING);
      Cell cellEx3 = rowEx.createCell(2);
      cellEx3.setCellType(Cell.CELL_TYPE_STRING);
    }

    sheet.setColumnWidth(0, 8000);
    sheet.setColumnWidth(1, 8000);
    sheet.setColumnWidth(2, 7000);

    res.setHeader("Content-Disposition", "attachment;fileName=Device_form.xlsx");
    res.setContentType("application/vnd.ms-excel");

    OutputStream out = new BufferedOutputStream(res.getOutputStream());
    workbook.write(out);
    out.flush();
    out.close();
  }

  public static void sheetMaker(SXSSFWorkbook workbook, List<DeviceElements> datas,String[] CSVHEADER) throws Exception {

    int rowIndex = 0;
    int excelHead = 0;

    Sheet sheet = workbook.createSheet("sheet1");

    Row row = sheet.createRow(rowIndex++);
    for (String head : CSVHEADER) {
      Cell headerCell = row.createCell(excelHead++);
      headerCell.setCellValue(head);
    }


    int cellCount = 0;

    for (DeviceElements data : datas) {
      Row bodyRow = sheet.createRow(rowIndex++);
      Cell bodyCell = bodyRow.createCell(cellCount++);

      bodyCell.setCellValue(data.getKorName() == null? "-": data.getKorName());
      bodyCell = bodyRow.createCell(cellCount++);
      bodyCell.setCellValue(data.getEngName() == null? "-": data.getEngName());
      bodyCell = bodyRow.createCell(cellCount++);
      bodyCell.setCellValue(data.getElementUnit() == null? "-": data.getElementUnit());
      bodyCell = bodyRow.createCell(cellCount++);
      bodyCell.setCellValue(data.getElementConvert() == null? "-": data.getElementConvert());
      bodyCell = bodyRow.createCell(cellCount++);
      bodyCell.setCellValue(data.getViewName() == null? "-": data.getViewName());
      bodyCell = bodyRow.createCell(cellCount++);
      if(data.getValidDigits() == null ){
        bodyCell.setCellValue("-");
      }else if(data.getValidDigits() != null && data.getValidDigits().equals("0")){
        bodyCell.setCellValue("정수");

      }else{
        bodyCell.setCellValue("소수점 ".concat(data.getValidDigits()).concat("자리"));
      }

      bodyCell = bodyRow.createCell(cellCount++);
      bodyCell.setCellValue(data.getDataMin() == null?"":data.getDataMin().concat("~").concat(data.getDataMax() == null?"":data.getDataMax()));
      bodyCell = bodyRow.createCell(cellCount++);
      bodyCell.setCellValue(data.getDataProcessMin() == null?"":data.getDataProcessMin().concat(",").concat(data.getDataProcessMax() == null?"":data.getDataProcessMax()));

      cellCount = 0;
    }

  }
}
