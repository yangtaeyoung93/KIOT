package com.airguard.controller.custom;

import com.airguard.exception.CookieAuthException;
import com.airguard.exception.ParameterException;
import com.airguard.model.custom.DataVo;
import com.airguard.model.custom.ResultVo;
import com.airguard.model.custom.clust.dto.DeviceInfoDto;
import com.airguard.model.dong.AirGeoInfo;
import com.airguard.model.dong.SearchDong;
import com.airguard.model.platform.ResultCollectionHisVo;
import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.service.app.v2.Air365PushV2Service;
import com.airguard.service.custom.CustomService;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.dong.DongService;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import com.airguard.util.FileProcessUtil;
import com.airguard.util.RestApiCookieManageUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/api/custom")
public class CustomController {

  Logger logger = LoggerFactory.getLogger(CustomController.class);
  DataVo dataVo = new DataVo();
  ResultVo resultVo = new ResultVo();

  @Autowired
  private CustomService service;

  @Autowired
  private PlatformService platformService;

  @Autowired
  private DatacenterService datacenterService;

  @Autowired
  private DongService dongService;

  @ApiOperation(value = "?????? ?????????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "userType", value = "????????? ?????? ??????", required = true),
      @ApiImplicitParam(name = "userId", value = "????????? ??????", required = true),
      @ApiImplicitParam(name = "password", value = "????????? ?????? ??????", required = true)})
  @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json")
  public DataVo kiotCustomLogin(@RequestBody HashMap<String, String> jsonData) throws Exception {
    HashMap<String, String> dataMp = new LinkedHashMap<String, String>();

    String userType = jsonData.get("userType") == null ? "group" : jsonData.get("userType").trim();
    String userId = jsonData.get("userId") == null ? "" : jsonData.get("userId").trim();
    String password = jsonData.get("password") == null ? "" : jsonData.get("password").trim();

    dataMp.put("userType", userType);
    dataMp.put("userId", userId);
    dataMp.put("password", password);

    dataVo.setData(dataMp);
    dataVo.setResult(service.customLoginCheck(userType, userId, password) ? 1 : 0);

    return dataVo;
  }

  @ApiOperation(value = "????????? ?????? ?????? (?????? ??????)", tags = "?????? ??????????????? API")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "request", value = "Prod / Dev Domain ?????? ??????", required = true),
      @ApiImplicitParam(name = "serial", value = "?????? ????????? ??????", required = true),
      @ApiImplicitParam(name = "pMode", value = "?????? ??????", required = true),
      @ApiImplicitParam(name = "aMode", value = "?????? ??????", required = false),
      @ApiImplicitParam(name = "hMode", value = "?????? ??????", required = false),
      @ApiImplicitParam(name = "wMode", value = "?????? ??????", required = false),})
  @RequestMapping(value = "/hum/mqtt", method = RequestMethod.POST, produces = "application/json")
  public ResultVo kiotHumMqttConrol(HttpServletRequest request,
      @RequestBody HashMap<String, String> jsonData) throws Exception {
    int resultCode = 0;

    String serial = jsonData.get("serial");
    String pMode = jsonData.get("pMode") == null ? "P1" : jsonData.get("pMode").trim();
    String aMode = jsonData.get("aMode") == null ? "000" : jsonData.get("aMode").trim();
    String hMode = jsonData.get("hMode") == null ? "00" : jsonData.get("hMode").trim();
    String wMode = jsonData.get("wMode") == null ? "00" : jsonData.get("wMode").trim();

    String mode = pMode.concat(aMode).concat(hMode).concat(wMode);
    logger.error("HUM ????????? ?????? ==> serial :: {}, mode :: {}", serial, mode);
    resultCode = platformService.postPlatformRequestVent(serial, mode, request.getLocalName());

    resultVo.setResult(resultCode);

    return resultVo;
  }

  @ApiOperation(value = "????????? ?????? ?????? ?????? ?????? (?????? ??????)", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "serial", value = "?????? ????????? ??????", required = true)})
  @RequestMapping(value = "/hum/status", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json")
  public DataVo kiotHumStatusData(String serial) throws Exception {
    Map<String, Object> statusData;
    statusData = service.getHumStatusData(serial.trim());
    dataVo.setData(statusData);
    dataVo.setResult(1);

    return dataVo;
  }

  @ApiOperation(value = "?????????, ??????????????? (IAQ & OAQ ???) ?????? ????????? ??????,", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "serial", value = "?????? ????????? ??????", required = true),
      @ApiImplicitParam(name = "deviceType", value = "?????? ??????", required = true),
      @ApiImplicitParam(name = "searchType", value = "?????? ????????? API ??????", required = true),
      @ApiImplicitParam(name = "fromDate", value = "?????? ????????? (From)", required = true),
      @ApiImplicitParam(name = "toDate", value = "?????? ????????? (To)", required = true),})
  @RequestMapping(value = "/get/data", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json")
  public DataVo getKiotData(String serialNum, String deviceType, String searchType, String fromDate,
      String toDate) throws Exception {

    ArrayList dataList =
        service.getDataFromKiotApi(serialNum, deviceType, searchType, fromDate, toDate);

    dataVo.setData(dataList);
    dataVo.setResult(CommonConstant.R_SUCC_CODE);

    return dataVo;
  }

  @ApiOperation(value = "?????????, ??????????????? (IAQ & OAQ ???) ?????? ????????? ??????,", tags = "?????? ??????????????? API")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "searchType", value = "?????? ????????? API ??????", required = true),
      @ApiImplicitParam(name = "id", value = "????????? ??????", required = true),})
  @RequestMapping(value = "/get/dataAll", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json")
  public DataVo getKiotDataById(int searchType, String id) throws Exception {
    DataVo dataVo = new DataVo();

    ArrayList dataList = service.getDataFromKiotApiById(searchType, id);

    dataVo.setData(dataList);
    dataVo.setResult(CommonConstant.R_SUCC_CODE);

    return dataVo;
  }

  @ApiOperation(value = "?????? ?????????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "request",
      value = "?????? (MultipartHttpServletRequest)", required = true),})
  @RequestMapping(value = "/image", method = RequestMethod.POST)
  public ResultVo deviceModelImageUpload(MultipartHttpServletRequest request) throws Exception {

    resultVo.setResult(CommonConstant.R_SUCC_CODE);

    String fileName =
        request.getParameter("fileName") == null ? "" : request.getParameter("fileName");
    String filePath =
        request.getParameter("filePath") == null ? "" : request.getParameter("filePath");

    try {

      MultipartFile photo = request.getFile("imageFile");
      String finalFileName = fileName.concat(".").concat(
          photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1));
      FileProcessUtil.fileSave(photo, filePath, filePath.concat(finalFileName));

    } catch (Exception e) {
      resultVo.setResult(CommonConstant.R_FAIL_CODE);
    }

    return resultVo;
  }

  @ApiOperation(value = "??????????????? (IAQ & OAQ ???) ?????? ????????? ??????, ?????? ??????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "????????? ??????", required = true),
      @ApiImplicitParam(name = "userType", value = "?????? ??????", required = true),})
  @RequestMapping(value = "/get/monitorData", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json")
  public DataVo getKiotListData(String userId, String userType) throws Exception {
    List<ResultCollectionVo> selectList = new ArrayList<>();
    List<String> deviceTypes;
    if(userType.equals("group")){
      deviceTypes = datacenterService.selectGroupDeviceType(userId);
    }else{
      deviceTypes = datacenterService.selectDeviceType(userId);
    }
    for (String deviceType : deviceTypes) {
      if (deviceType != null) {
        selectList.addAll(service.getMonitorDataFromKiotApi(userType.trim(), userId, deviceType));
      }
    }

    dataVo.setData(selectList);

    return dataVo;
  }

  @ApiOperation(value = "??????????????? (VENT) ?????? ????????? ??????, ?????? ??????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "request", value = "Prod / Dev Domain ?????? ??????", required = true),
      @ApiImplicitParam(name = "userId", value = "????????? ??????", required = true),
      @ApiImplicitParam(name = "userType", value = "????????? ?????? ??????", required = true)})
  @RequestMapping(value = "/get/ventMonitorData", method = RequestMethod.GET,
      produces = "application/json")
  public DataVo getKiotVentListData(HttpServletRequest request, String userId, String userType)
      throws Exception {
    List<?> selectList = new ArrayList<>();

    if ("group".equals(userType.toLowerCase())) {
      selectList = service.selectGroupHumSensorApi(userId, request.getLocalName());

    } else {
      selectList = service.selectPrivateHumSensorApi(userId, request.getLocalName());
    }

    dataVo.setData(selectList);
    dataVo.setResult(1);

    return dataVo;
  }

  @ApiOperation(value = "???????????????  (IAQ & OAQ ???) ?????? ?????? ??????, ?????? ??????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "serial", value = "?????? ????????? ??????", required = true)})
  @RequestMapping(value = "/get/elements", method = RequestMethod.GET,
      produces = "application/json")
  public DataVo getModelElements(String serial) throws Exception {
    List<?> datas = new ArrayList<>();

    datas = service.selectDeviceModelElements(serial);
    dataVo.setData(datas);
    dataVo.setResult(1);

    return dataVo;
  }

  @ApiOperation(value = "??????????????? & ??????????????? ?????? ????????? ??????, ?????? ??????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({@ApiImplicitParam(name = "deviceType", value = "?????? ??????", required = true),
      @ApiImplicitParam(name = "serial", value = "?????? ?????????", required = true),
      @ApiImplicitParam(name = "startTime", value = "?????? ????????? (From)", required = true),
      @ApiImplicitParam(name = "endTime", value = "?????? ????????? (To)", required = true),
      @ApiImplicitParam(name = "standard", value = "????????? ?????? (??? ??????, 5??? ??????, ??? ??????)", required = true),
      @ApiImplicitParam(name = "connect", value = "IAQ-VENT ?????? ??????", required = true),})
  @RequestMapping(value = "/get/historyData", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/json")
  public DataVo getKiotHistoryData(@RequestBody HashMap<String, String> request) throws Exception {
    String deviceType = request.get("deviceType") == null ? "iaq" : request.get("deviceType");
    String serial = request.get("serial");
    String startTime = request.get("startTime"); // 2020/04/21-00:00:00
    String endTime = request.get("endTime"); // 2020/04/21-23:59:59
    String standard = request.get("standard") == null ? "sum" : request.get("standard"); // sum,
    String connect = request.get("connect") == null ? "1" : request.get("connect"); // 0 = VENT ??????

    List<ResultCollectionHisVo> datas;
    if (standard.equals("1d-avg-none") || standard.equals("1n-avg-none")) {
      datas = platformService.selectSensorHistoryDayMonth(deviceType, serial, startTime, endTime,
          standard, connect);

    } else {
      datas = platformService.selectSensorApi(deviceType, serial, startTime, endTime, standard,
          connect);
    }

    dataVo.setResult(CommonConstant.R_SUCC_CODE);
    dataVo.setData(datas);

    return dataVo;
  }

  // ??? ?????????
  @ApiOperation(value = "???????????? ????????? ?????? ??????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/siList", method = RequestMethod.GET)
  public ResponseEntity<Object> siList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("data", dongService.siList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // ??? ?????????
  @ApiOperation(value = "?????? ????????? ?????? ??????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/guList", method = RequestMethod.GET)
  public ResponseEntity<Object> guList(@RequestParam("searchSdcode") String searchSdcode)
      throws Exception {
    Map<String, Object> result = new HashMap<>();

    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);

    result.put("data", dongService.guList(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // ??? ?????????
  @ApiOperation(value = "?????? ????????? ?????? ??????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/dongList", method = RequestMethod.GET)
  public ResponseEntity<Object> dongList(@RequestParam("searchSdcode") String searchSdcode,
      @RequestParam("searchSggcode") String searchSggcode) throws Exception {

    Map<String, Object> result = new HashMap<>();

    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);
    search.setSearchSggcode(searchSggcode);

    result.put("data", dongService.dongList(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "IAQ ?????? ?????? ????????? ?????? (?????? ?????? ?????? ??????)", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/iaqRealTimeBySerial", method = RequestMethod.POST)
  public List<DeviceInfoDto> iaqRealTimeBySerial(String[] serials) throws Exception {
    List<DeviceInfoDto> result = new ArrayList<>();
    result = service.iaqRealTimeBySerial(serials);
    return result;
  }

  @ApiOperation(value = "OAQ ?????? ?????? ????????? ??????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/oaqRealTime", method = RequestMethod.GET)
  public List<ResultCollectionVo> oaqRealTime() throws Exception {
    List<ResultCollectionVo> res =
        platformService.selectTotalSensorApi(CommonConstant.PARAM_SENSOR_OAQ);
    return res;
  }

  @ApiOperation(value = "DOT ?????? ?????? ????????? ??????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/dotRealTime", method = RequestMethod.GET)
  public List<ResultCollectionVo> dotRealTime() throws Exception {
    List<ResultCollectionVo> res =
        platformService.selectTotalSensorApi(CommonConstant.PARAM_SENSOR_DOT);

    return res;
  }

  @ApiOperation(value = "AirKorea ?????? ?????? ????????? ?????? (?????? ?????? ??????)", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/airkorRealTime", method = RequestMethod.GET)
  public List<DeviceInfoDto> airkorRealTime(@RequestParam(required = false) String dCode)
      throws Exception {
    List<AirGeoInfo> res = dongService.selectAirKorApi(dCode);
    List<DeviceInfoDto> dd = new ArrayList<>();
    for (int i = 0; i < res.size(); i++) {
      DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
      deviceInfoDto.setDcode(res.get(i).getDCode());
      deviceInfoDto.setLat(String.valueOf(res.get(i).getLat()));
      deviceInfoDto.setLon(String.valueOf(res.get(i).getLon()));
      deviceInfoDto.setSerial(res.get(i).getAAirCode());
      deviceInfoDto.setSensor(res.get(i).getSensor());
      deviceInfoDto.setStationName(res.get(i).getAAirName());
      deviceInfoDto.setDeviceIdx(res.get(i).getAAirAddr());
      dd.add(deviceInfoDto);
    }
    return dd;
  }

  @ApiOperation(value = "Jeju ?????? ?????? ????????? ??????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/jejuRealTime", method = RequestMethod.GET)
  public List<DeviceInfoDto> jejuRealTime() {
    List<DeviceInfoDto> result = new ArrayList<>();
    try {
      result = service.jejuRealTime();
    } catch (Exception e) {
      System.out.println("Error : " + e);
    }
    return result;
  }


  // ?????? ???????????? ????????? ??????, ??????????????? ?????????
  @ApiOperation(value = "?????? ???????????? ????????? ??????, ??????????????? ?????????", tags = "?????? ??????????????? API")
  @RequestMapping(value = "/airKorHistory", method = RequestMethod.GET)
  public ResponseEntity<Object> dongDataDetail(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();

    try {
      String dcode = request.getParameter("dcode");
      String startTime = request.getParameter("startTime");
      String endTime = request.getParameter("endTime");
      String type = request.getParameter("type");
      result.put("data", dongService.historyData(dcode, startTime, endTime, "sum", type));
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "There is no matching data .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // ?????? ?????? ?????? API
  @ApiOperation(value = "?????? ?????? ?????? ?????? ??????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "groupId", value = "?????? ????????? ??????", required = true),
    @ApiImplicitParam(name = "idx", value = "?????? ?????? ??????", defaultValue = "0")
  })
  @RequestMapping(value = "/seocho/push", method = RequestMethod.GET)
  public HashMap<String, Object> seochoPushHistoryG(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    String groupId =
        request.getParameter("groupId") == null ? "" : request.getParameter("groupId").trim();

    res.put("data", service.selectGroupPushHistory(groupId));

    return res;
  }

  // ?????? ???????????? ?????? API
  @ApiOperation(value = "?????? ???????????? ?????? ??????", tags = "?????? ??????????????? API")
  @ApiImplicitParams({
  })
  @RequestMapping(value = "/seocho/notice", method = RequestMethod.GET)
  public HashMap<String, Object> seochoNoticeList(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    res.put("data", service.selectSeochoNoticeList());

    return res;
  }
}
