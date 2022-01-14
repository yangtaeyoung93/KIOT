package com.airguard.service.system;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.airguard.mapper.main.system.DeviceMapper;
import com.airguard.model.common.Search;
import com.airguard.model.system.Device;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class DeviceService {

  private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

  @Autowired
  DeviceMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<Device> selectDeviceList(Search search) {
    return readOnlyMapper.selectDeviceList(search);
  }

  public Device selectDeviceOne(String idx) {
    return readOnlyMapper.selectDeviceOne(idx);
  }

  public void insertDevice(Device device) {
    mapper.insertDevice(device);
  }

  public void updateDevice(Device device) {
    mapper.updateDevice(device);
  }

  public List<Device> selectOaqList() {return readOnlyMapper.selectOaqList();}

  public int deleteDevice(String idx) {

    try {

      if (readOnlyMapper.deleteDeviceMemberCheck(idx) != 0) {
        return 2;

      } else if (readOnlyMapper.deleteDeviceVentCheck(idx) != 0) {
        return 3;

      } else if (readOnlyMapper.deleteDeviceVentCheck2(idx) != 0) {
        return 4;

      } else {
        mapper.deleteDevice(idx);
        return 1;
      }

    } catch (Exception e) {
      return 9;
    }
  }

  public List<Device> selectDataDownloadList() {
    return readOnlyMapper.selectDataDownloadList();
  }

  public int checkSerialNum(String serialNum) {
    int resultCode = 0;
    int result = readOnlyMapper.checkSerialNum(serialNum);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public String xlsxDeviceExcelReader(MultipartHttpServletRequest req) {
    List<Device> deviceDataList = new ArrayList<>();
    String result = "SUCCESS";
    MultipartFile file = req.getFile("excel");
    XSSFWorkbook workbook;

    String deviceTypeIdx = req.getParameter("deviceTypeIdx");
    String deviceModelIdx = req.getParameter("deviceModelIdx");

    try {

      assert file != null;
      workbook = new XSSFWorkbook(file.getInputStream());

      XSSFSheet curSheet;
      XSSFRow curRow;
      XSSFCell curCell;
      Device vo;

      curSheet = workbook.getSheetAt(0);

      for (int rowIndex = 1; rowIndex <= curSheet.getLastRowNum(); rowIndex++) {
        curRow = curSheet.getRow(rowIndex);
        vo = new Device();
        String value;

        if (curRow.getCell(0) != null) {
          if (!"".equals(curRow.getCell(0).getStringCellValue())) {
            for (int cellIndex = 0; cellIndex < curSheet.getRow(0).getLastCellNum(); cellIndex++) {

              curCell = curRow.getCell(cellIndex);
              switch (curCell.getCellType()) {
                case XSSFCell.CELL_TYPE_FORMULA:
                  value = curCell.getCellFormula();
                  break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                  return "TYPE_CHECK";
                case XSSFCell.CELL_TYPE_STRING:
                  value = curCell.getStringCellValue() + "";
                  break;
                case XSSFCell.CELL_TYPE_BLANK:
                  value = curCell.getBooleanCellValue() + "";
                  break;
                case XSSFCell.CELL_TYPE_ERROR:
                  value = curCell.getErrorCellValue() + "";
                  break;
                default:
                  value = "";
                  break;
              }

              switch (cellIndex) {
                case 0:
                  if (value.length() > 31) {
                    return "SERIAL_SIZE_CHECK";
                  }
                  if (!"".equals(value.trim())) {
                    if (checkSerialNum(value) != 1) {
                      return value;
                    }
                  }
                  vo.setSerialNum(value);
                  break;
                case 1:
                  if (value.length() > 11) {
                    return "PRODUCT_SIZE_CHECK";
                  }
                  vo.setProductDt(value);
                  break;
                case 2:
                  if (value.length() > 2) {
                    return "YN_SIZE_CHECK";
                  }
                  vo.setTestYn(value);
                  break;
                default:
                  break;
              }
              vo.setDeviceModelIdx(deviceModelIdx);
              vo.setDeviceTypeIdx(deviceTypeIdx);

            }
            deviceDataList.add(vo);
          }
        }
      }

      for (Device data : deviceDataList) {
        mapper.insertDevice(data);
      }

    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return "FAIL";
    }

    return result;
  }
}
