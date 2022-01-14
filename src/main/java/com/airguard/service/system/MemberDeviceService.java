package com.airguard.service.system;

import com.airguard.exception.SQLException;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.common.Search;
import com.airguard.model.dong.DongGeo;
import com.airguard.model.system.Device;
import com.airguard.model.system.FileVo;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Space;
import com.airguard.model.system.Vent;
import com.airguard.service.platform.PlatformService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class MemberDeviceService {

  private static final Logger logger = LoggerFactory.getLogger(MemberDeviceService.class);

  @Autowired
  MemberDeviceMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  @Autowired
  private PlatformService platformService;

  public List<MemberDevice> selectMemberDeviceList(Search search) {
    return mapper.selectMemberDeviceList(search);
  }

  public List<MemberDevice> selectMemberDeviceOne(String idx) {
    return readOnlyMapper.selectMemberDeviceOne(idx);
  }

  public List<Vent> selectVentOne(List<MemberDevice> deviceDatas) {
    List<Vent> ventDatas = new ArrayList<>();

    for (MemberDevice device : deviceDatas) {
      if ((device.getDeviceType()).equals("IAQ")) {
        ventDatas.addAll(readOnlyMapper.selectMemberDeviceVentOne(device.getDeviceIdx()));
      }
    }

    return ventDatas;
  }

  public MemberDevice selectMemberDeviceCnt(String idx) {
    return readOnlyMapper.selectMemberDeviceCnt(idx);
  }

  public List<MemberDevice> selectDeviceTypeList() {
    return readOnlyMapper.selectDeviceTypeList();
  }

  public List<MemberDevice> selectDeviceModelList(String idx) {
    return readOnlyMapper.selectDeviceModelList(idx);
  }

  public List<MemberDevice> selectDeviceSerialList(String idx) {
    return readOnlyMapper.selectDeviceSerialList(idx);
  }

  public List<MemberDevice> selectDeviceVentList() {
    return readOnlyMapper.selectDeviceVentList();
  }

  public List<Space> selectSpaceList(String idx) {
    return readOnlyMapper.selectSpaceList(idx);
  }

  public List<Space> selectParantSpaceList(String idx) {
    return readOnlyMapper.selectParantSpaceList(idx);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void insertMemberDevice(MemberDevice md) {
    md.setRelatedDevice("");
    mapper.insertMemberDevice(md);

    if (md.getVentDeviceIdxs() != null && !md.getVentDeviceIdxs().isEmpty()) {
      for (String ventDeviceIdx : md.getVentDeviceIdxs()) {
        mapper.insertMemberDeviceVent(md.getMemberIdx(), md.getDeviceIdx(), ventDeviceIdx);
      }
    }
  }

  public List<Vent> selectMemberDeviceVentOne(String idx) {
    return readOnlyMapper.selectMemberDeviceVentOne(idx);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void updateMemberDevice(MemberDevice md) {
    deleteMemberDevice(md);
    insertMemberDeviceOne(md);
  }

  public void insertMemberDeviceOne(MemberDevice md) {
    if (md.getRelatedDevice().equals(null)) {
      md.setRelatedDevice("");
    }

    mapper.insertMemberDevice(md);
  }

  public FileVo selectFileInfo(String deviceIdx, String fileType) {
    return readOnlyMapper.selectFileInfo(deviceIdx, fileType);
  }

  public void fileUpload(FileVo vo) {
    mapper.fileUpload(vo);
  }

  public void updateFile(FileVo vo) {
    mapper.updateFile(vo);
  }

  public void deleteFile(String idx) {
    mapper.deleteFile(idx);
  }

  public void deleteMemberDevice(MemberDevice md) {
    mapper.deleteMemberDevice(md);
  }

  public void insertMemberDeviceVent(Vent vent) {
    mapper.insertMemberDeviceVent(vent.getMemberIdx(), vent.getIaqDeviceIdx(),
        vent.getVentDeviceIdx());
  }

  public void updateMemberDeviceVent(Vent vent) {
    mapper.updateMemberDeviceVent(vent);
  }

  public void deleteMemberDeviceVent(String idx) {
    mapper.deleteMemberDeviceVent(idx);
  }

  public void deleteMemberDeviceVentAll(Vent vent) {
    mapper.deleteMemberDeviceVentAll(vent);
  }

  public String selectVentSerialIdx(String iaqSerial) {
    return readOnlyMapper.selectVentSerialIdx(iaqSerial);
  }

  public int checkStationName(String memberIdx, String StationName) {
    int resultCode = 0;
    int result = readOnlyMapper.checkStationName(memberIdx, StationName);

    if (result == 0) {
      resultCode = 1;// 사용가능
    }

    return resultCode;
  }

  public List<HashMap<String, Object>> selectMemberDeviceListAir365() {
    return readOnlyMapper.selectMemberDeviceListAi365();
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public String xlsxMemberDeviceExcelReader(MultipartHttpServletRequest req) {
    List<MemberDevice> memberDeviceList = new ArrayList<>();

    int readRow = 0;
    String result = "SUCCESS";
    MultipartFile file = req.getFile("excel");
    XSSFWorkbook workbook;

    List<String> checkDongList = new ArrayList<>();
    List<DongGeo> dongList = readOnlyMapper.selectDongList();
    for (DongGeo dong : dongList) {
      checkDongList.add(dong.getDcode());
    }

    try {

      workbook = new XSSFWorkbook(file.getInputStream());

      XSSFSheet curSheet;
      XSSFRow curRow;
      XSSFCell curCell;

      MemberDevice vo;

      int memberIdx;
      int deviceIdx;
      int parentSpaceIdx;
      List<String> ventIdxs;
      String deviceTypeIdx;

      curSheet = workbook.getSheetAt(0);

      for (int rowIndex = 1; rowIndex <= curSheet.getLastRowNum(); rowIndex++) {
        curRow = curSheet.getRow(rowIndex);
        readRow = rowIndex;
        vo = new MemberDevice();
        memberIdx = 0;
        parentSpaceIdx = 0;
        Device device;
        deviceTypeIdx = "";

        String value;

        if (curRow.getCell(0) != null) {
          value = curRow.getCell(0).getStringCellValue();
          if (!"".equals(value)) {
            ventIdxs = new ArrayList<>();
            for (int cellIndex = 0; cellIndex < curSheet.getRow(0).getLastCellNum(); cellIndex++) {
              curCell = curRow.getCell(cellIndex);
              switch (curCell.getCellType()) {
                case XSSFCell.CELL_TYPE_FORMULA:
                  value = curCell.getCellFormula();
                  break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                  value= Integer.toString((int)curCell.getNumericCellValue());
                  break;
                case XSSFCell.CELL_TYPE_STRING:
                  value = curCell.getStringCellValue() + "";
                  break;
                case XSSFCell.CELL_TYPE_BLANK:
                  value = "";
                  break;
                case XSSFCell.CELL_TYPE_ERROR:
                  value = curCell.getErrorCellValue() + "";
                  break;
                default:
                  value = "";
                  break;
              }

              switch (cellIndex) {
                case 0:// 사용자 계정
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    memberIdx = readOnlyMapper.excelUploadCheckUserId(value);
                  }

                  if (memberIdx == 0) {
                    return "사용자 계정을 확인하세요. (" + "A, " + (rowIndex + 1) + ")";
                  } else {
                    vo.setMemberIdx(Integer.toString(memberIdx));
                  }

                  break;
                case 1:// 시리얼 번호
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    deviceIdx = readOnlyMapper.excelUploadCheckSerialNum(value);

                    if (deviceIdx == 0) {
                      return "이미 등록된 장비이거나 잘못된 시리얼입니다. (" + "B, " + (rowIndex + 1) + ")";
                    } else {
                      vo.setDeviceIdx(Integer.toString(deviceIdx));
                      device = readOnlyMapper.getDeviceInfoBySerial(value);
                      deviceTypeIdx = device.getDeviceTypeIdx();
                    }

                  } else {
                    return "시리얼 번호를 확인하세요. (" + "B, " + (rowIndex + 1) + ")";
                  }

                  break;
                case 2: // 스테이션 명
                  if (value.length() > 51) {
                    return "스테이션 이름 입력값이 너무 깁니다. (" + "C, " + (rowIndex + 1) + ")";
                  } else {
                    if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                      if (checkStationName(vo.getMemberIdx(), value) != 1) {
                        return "이미 등록된 스테이션 이름입니다. (" + "C, " + (rowIndex + 1) + ")";
                      } else {
                        vo.setStationName(value);
                      }
                    } else {
                      return "스테이션 이름을 입력하세요. (" + "C, " + (rowIndex + 1) + ")";
                    }
                  }
                  break;
                case 3: // 상위 공간 카테고리
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    parentSpaceIdx =
                        readOnlyMapper.excelUploadGetParentSpaceIdx(value, vo.getDeviceIdx());
                  }
                  if (parentSpaceIdx == 0) {
                    return "상위 공간 카테고리를 확인하세요. (" + "D, " + (rowIndex + 1) + ")";
                  }

                  break;
                case 4: // 하위 공간 카테고리
                  int spaceIdx;
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    spaceIdx = readOnlyMapper.excelUploadGetSpaceIdx(value,
                        Integer.toString(parentSpaceIdx));
                    if (spaceIdx == 0) {
                      return "하위 공간 카테고리가 상위 카테고리와 맞지 않습니다. (" + "E, " + (rowIndex + 1) + ")";
                    } else {
                      vo.setSpaceIdx(Integer.toString(spaceIdx));
                    }

                  } else {
                    return "하위 공간 카테고리를 입력하세요. (" + "E, " + (rowIndex + 1) + ")";
                  }

                  break;
                case 5: // 위도
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    if (Pattern.matches(
                        "^(?:-|-?(?:\\d|[1-9]\\d?|1(?:[0-7]\\d?)?|1(?:80?)?)(?:\\.[0-9]{0,30})?)$",
                        value.trim())) {
                      vo.setLat(value);
                    } else {
                      return "위도를 정확히 입력하세요. (" + "F, " + (rowIndex + 1) + ")";
                    }
                  } else {
                    return "위도를 입력하세요. (" + "F, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 6: // 경도
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    if (Pattern.matches(
                        "^(?:-|-?(?:\\d|[1-9]\\d?|1(?:[0-7]\\d?)?|1(?:80?)?)(?:\\.[0-9]{0,30})?)$",
                        value.trim())) {
                      vo.setLon(value);
                    } else {
                      return "경도를 정확히 입력하세요. (" + "G, " + (rowIndex + 1) + ")";
                    }
                  } else {
                    return "경도를 입력하세요. (" + "G, " + (rowIndex + 1) + ")";
                  }
                  break;

                case 7: // 행정동코드ex.1111010600
                  if (!"false".equals(value.trim()) && value.length() == 10) {
                    if (!checkDongList.contains(value.trim())) {
                      return "행정동 코드가 잘못되었습니다. (" + "H, " + (rowIndex + 1) + ")";
                    } else {
                      vo.setDcode(value);
                    }
                  } else {
                    return "행정동 코드를 확인하세요. (" + "H, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 8: // 에어맵사용여부
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    // 에어맵 사용유무 Y or N 검사
                    if ("Y".equals(value.trim()) || "N".equals(value.trim())) {
                      vo.setAirMapYn(value);
                    } else {
                      return "에어맵 사용유무는 Y나 N으로 입력하세요. (" + "I, " + (rowIndex + 1) + ")";
                    }
                  } else {
                    return "에어맵 사용 여부를 입력하세요. (" + "I, " + (rowIndex + 1) + ")";
                  }
                  break;

                case 9:// 고객담당자
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setDepartName(value);
                  } else {
                    return "고객담당자를 입력하세요. (" + "J, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 10: // 고객연락처
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setDepartPhoneNumber(value);
                  } else {
                    return "고객연락처를 입력하세요. (" + "K, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 11: // 영업담당자
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setSalesName(value);
                  } else {
                    return "영업담당자를 입력하세요. (" + "L, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 12: // 설치 일자
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipDt(value);
                  } else {
                    return "설치일자를 입력하세요. (" + "M, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 13: // 설치 담당자
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipName(value);
                  } else {
                    return "설치담당자를 입력하세요. (" + "N, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 14: // 설치주소
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipAddr(value);
                  } else {
                    return "설치주소를 입력하세요. (" + "O, " + (rowIndex + 1) + ")";
                  }
                  break;
                case 15:// 설치주소 상세(필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipAddr2(value);
                  }
                  break;
                case 16:// 비고(필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEtc(value);
                  }
                  break;
                case 17:// 환기청정기-1 (필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    /// 스테이션이 IAQ가 아닌 경우

                    if (!deviceTypeIdx.equals("1")) {
                      return "환기청정기 등록이 불가능한 장비입니다. (" + "R, " + (rowIndex + 1) + ")";

                      // IAQ인 경우
                    } else {
                      device = readOnlyMapper.getDeviceInfoBySerial(value);
                      if (device != null) {
                        String ventdeviceTypeIdx = device.getDeviceTypeIdx();
                        if (!ventdeviceTypeIdx.equals("7")) {
                          return "환기청정기가 아닙니다. (" + "R, " + (rowIndex + 1) + ")";
                        } else {
                          if (readOnlyMapper.excelUploadCheckVentSerialNum(value) != 0) {
                            ventIdxs.add(Integer
                                .toString(readOnlyMapper.excelUploadCheckVentSerialNum(value)));
                          } else {
                            return "이미 등록된 환기청정기입니다. (" + "R, " + (rowIndex + 1) + ")";
                          }
                        }
                      } else {
                        return "환기청정기 시리얼 확인 (" + "R, " + (rowIndex + 1) + ")";
                      }
                    }
                  }
                  break;
                case 18:// 환기청정기-2 (필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    /// 스테이션이 IAQ가 아닌 경우

                    if (!deviceTypeIdx.equals("1")) {
                      return "환기청정기 등록이 불가능한 장비입니다. (" + "S, " + (rowIndex + 1) + ")";

                      // IAQ인 경우
                    } else {
                      device = readOnlyMapper.getDeviceInfoBySerial(value);
                      if (device != null) {
                        String ventdeviceTypeIdx = device.getDeviceTypeIdx();

                        if (!ventdeviceTypeIdx.equals("7")) {
                          return "환기청정기가 아닙니다. (" + "S, " + (rowIndex + 1) + ")";
                        } else {
                          if (readOnlyMapper.excelUploadCheckVentSerialNum(value) != 0) {
                            String ventIdx = Integer.toString(device.getIdx());
                            if (!ventIdxs.contains(ventIdx)) {
                              ventIdxs.add(Integer
                                  .toString(readOnlyMapper.excelUploadCheckVentSerialNum(value)));
                            } else {
                              return "중복된 환기청정기 시리얼입니다. (" + "S, " + (rowIndex + 1) + ")";
                            }
                          } else {
                            return "이미 등록된 환기청정기입니다. (" + "S, " + (rowIndex + 1) + ")";
                          }
                        }
                      } else {
                        return "환기청정기 시리얼 확인 (" + "S, " + (rowIndex + 1) + ")";
                      }
                    }
                  }
                  break;
                case 19: // 환기청정기-3 (필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    /// 스테이션이 IAQ가 아닌 경우

                    if (!deviceTypeIdx.equals("1")) {
                      return "환기청정기 등록이 불가능한 장비입니다. (" + "T, " + (rowIndex + 1) + ")";

                      // IAQ인 경우
                    } else {
                      device = readOnlyMapper.getDeviceInfoBySerial(value);
                      if (device != null) {
                        String ventdeviceTypeIdx = device.getDeviceTypeIdx();

                        if (!ventdeviceTypeIdx.equals("7")) {
                          return "환기청정기가 아닙니다. (" + "T, " + (rowIndex + 1) + ")";
                        } else {
                          if (readOnlyMapper.excelUploadCheckVentSerialNum(value) != 0) {
                            String ventIdx = Integer.toString(device.getIdx());
                            if (!ventIdxs.contains(ventIdx)) {
                              ventIdxs.add(Integer
                                  .toString(readOnlyMapper.excelUploadCheckVentSerialNum(value)));
                            } else {
                              return "중복된 환기청정기 시리얼입니다. (" + "T, " + (rowIndex + 1) + ")";
                            }
                          } else {
                            return "이미 등록된 환기청정기입니다. (" + "T, " + (rowIndex + 1) + ")";
                          }
                        }
                      } else {
                        return "환기청정기 시리얼 확인 (" + "T, " + (rowIndex + 1) + ")";
                      }
                    }
                  }
                  break;
                case 20: // 환기청정기-4 (필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    /// 스테이션이 IAQ가 아닌 경우

                    if (!deviceTypeIdx.equals("1")) {
                      return "환기청정기 등록이 불가능한 장비입니다. (" + "U, " + (rowIndex + 1) + ")";

                      // IAQ인 경우
                    } else {
                      device = readOnlyMapper.getDeviceInfoBySerial(value);
                      if (device != null) {
                        String ventdeviceTypeIdx = device.getDeviceTypeIdx();

                        if (!ventdeviceTypeIdx.equals("7")) {
                          return "환기청정기가 아닙니다. (" + "U, " + (rowIndex + 1) + ")";
                        } else {
                          if (readOnlyMapper.excelUploadCheckVentSerialNum(value) != 0) {
                            String ventIdx = Integer.toString(device.getIdx());
                            if (!ventIdxs.contains(ventIdx)) {
                              ventIdxs.add(Integer
                                  .toString(readOnlyMapper.excelUploadCheckVentSerialNum(value)));
                            } else {
                              return "중복된 환기청정기 시리얼입니다. (" + "U, " + (rowIndex + 1) + ")";
                            }
                          } else {
                            return "이미 등록된 환기청정기입니다. (" + "U, " + (rowIndex + 1) + ")";
                          }
                        }
                      } else {
                        return "환기청정기 시리얼 확인 (" + "U, " + (rowIndex + 1) + ")";
                      }
                    }
                  }
                  break;
                case 21: // 환기청정기-5 (필수 아님)
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    /// 스테이션이 IAQ가 아닌 경우

                    if (!deviceTypeIdx.equals("1")) {
                      return "환기청정기 등록이 불가능한 장비입니다. (" + "V, " + (rowIndex + 1) + ")";

                      // IAQ인 경우
                    } else {
                      device = readOnlyMapper.getDeviceInfoBySerial(value);
                      if (device != null) {
                        String ventdeviceTypeIdx = device.getDeviceTypeIdx();

                        if (!ventdeviceTypeIdx.equals("7")) {
                          return "환기청정기가 아닙니다. (" + "V, " + (rowIndex + 1) + ")";
                        } else {
                          if (readOnlyMapper.excelUploadCheckVentSerialNum(value) != 0) {
                            String ventIdx = Integer.toString(device.getIdx());
                            if (!ventIdxs.contains(ventIdx)) {
                              ventIdxs.add(Integer
                                  .toString(readOnlyMapper.excelUploadCheckVentSerialNum(value)));
                            } else {
                              return "중복된 환기청정기 시리얼입니다. (" + "V, " + (rowIndex + 1) + ")";
                            }
                          } else {
                            return "이미 등록된 환기청정기입니다. (" + "V, " + (rowIndex + 1) + ")";
                          }
                        }
                      } else {
                        return "환기청정기 시리얼 확인 (" + "V, " + (rowIndex + 1) + ")";
                      }
                    }
                  }
                  break;
                default:
                  break;
              }
            }

            logger.info("vo : " + vo);
            if (!ventIdxs.isEmpty()) {
              vo.setVentDeviceIdxs(ventIdxs);
            }

            memberDeviceList.add(vo);

          }
        }
      }

      for (MemberDevice data : memberDeviceList) {
        data.setSetTemp("");
        data.setRelatedDevice("");
        insertMemberDevice(data);
        platformService.publisherPlatform(platformService.idxToUserId(data.getMemberIdx()), "USER",
            true, req.getLocalName());
        platformService.publisherPlatform(platformService.idxToUserId(data.getMemberIdx()), "USER",
            false, req.getLocalName());
        platformService.publisherPlatform(platformService.memberIdxToGroupId(data.getMemberIdx()),
            "GROUP", true, req.getLocalName());
        platformService.publisherPlatform(platformService.memberIdxToGroupId(data.getMemberIdx()),
            "GROUP", false, req.getLocalName());
      }

    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return "(Row number - " + (readRow + 1) + ")";
    }

    logger.info("result : " + result);
    return result;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public String xlsxMemberDeviceExcelReaderUpdate() throws IOException, InvalidFormatException {
    List<MemberDevice> memberDeviceList = new ArrayList<>();

    ClassPathResource resource = new ClassPathResource("static/a.xlsx");

    Workbook workbook = WorkbookFactory.create(resource.getFile());

    int readRow = 0;
    String result = "SUCCESS";

    try {
      Sheet curSheet;
      Row curRow;
      Cell curCell;

      MemberDevice vo;

      curSheet = workbook.getSheetAt(0);

      for (int rowIndex = 1; rowIndex <= curSheet.getLastRowNum(); rowIndex++) {

        curRow = curSheet.getRow(rowIndex);
        readRow = rowIndex;
        vo = new MemberDevice();
//        logger.error(String.valueOf(curSheet.getRow(0).getLastCellNum()));
        String value;

        if (curRow.getCell(0) != null) {
          value = curRow.getCell(0).getStringCellValue();
          if (!"".equals(value)) {
            for (int cellIndex = 0; cellIndex < curSheet.getRow(0).getLastCellNum(); cellIndex++) {
              curCell = curRow.getCell(cellIndex);
              switch (curCell.getCellType()) {
                case Cell.CELL_TYPE_FORMULA:
                  value = curCell.getCellFormula();
                  break;
                case Cell.CELL_TYPE_NUMERIC:
                  Date date = curCell.getDateCellValue();
                  value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                  break;
                case Cell.CELL_TYPE_STRING:
                  value = curCell.getStringCellValue() + "";
                  break;
                case Cell.CELL_TYPE_BLANK:
                  value = "";
                  break;
                case Cell.CELL_TYPE_ERROR:
                  value = curCell.getErrorCellValue() + "";
                  break;
                default:
                  value = "";
                  break;
              }

              switch (cellIndex) {
                case 0:
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setSerialNum(value);
                  } else {
                    return "시리얼 번호를 확인하세요. (" + "A, " + (rowIndex + 1) + ")";
                  }

                  break;
                case 1:
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setDepartName(value);
                  } else {
                    vo.setDepartName(null);
                  }

                  break;
                case 2:
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setDepartPhoneNumber(value);
                  } else {
                    vo.setDepartPhoneNumber(null);
                  }

                  break;
                case 3:
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipDt(value);
                  } else {
                    vo.setEquipDt(null);
                  }

                  break;
                case 4:
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipName(value);
                  } else {
                    vo.setEquipName(null);
                  }

                  break;
                case 5:
                  if (!"false".equals(value.trim()) && !"".equals(value.trim())) {
                    vo.setEquipAddr2(value);
                  } else {
                    vo.setEquipAddr2("");
                  }

                  break;
                default:
                  break;
              }
            }

            memberDeviceList.add(vo);

          }
        }
      }

      for (MemberDevice data : memberDeviceList) {
        logger.info(data.getSerialNum());

        if (mapper.updateMemberDeviceBySerial(data) != 1) {
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        } else {
          logger.info("SUCCESS");
        }
      }

    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return "(Row number - " + (readRow + 1) + ")";
    }

    return result;
  }
}
