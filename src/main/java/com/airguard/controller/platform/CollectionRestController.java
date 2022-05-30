package com.airguard.controller.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.airguard.model.dong.AirGeoInfo;
import com.airguard.model.platform.AirMapOaqVo;
import com.airguard.service.dong.DongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.airguard.model.platform.ResultCollectionHisVo;
import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = CommonConstant.URL_API_COLLECTION, produces = MediaType.APPLICATION_JSON_VALUE)
public class CollectionRestController {

  private static final Logger logger = LoggerFactory.getLogger(CollectionRestController.class);

  @Autowired
  private PlatformService service;

  @Autowired
  private DongService dongService;

  private static final Map<String, Object> result = new HashMap<>();

  @ApiOperation(value = "IAQ 최근 측정 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/iaq", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiIaq() throws Exception {
    List<ResultCollectionVo> res = service.selectTotalSensorApi(CommonConstant.PARAM_SENSOR_IAQ);

    result.put("data", res);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "OAQ 최근 측정 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/oaq", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiOaq(@RequestParam String masterIdx) throws Exception {
    masterIdx = masterIdx == "" ? "0" : masterIdx;
    List<ResultCollectionVo> res = service.selectTotalSensorApi(CommonConstant.PARAM_SENSOR_OAQ);
    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "DOT 최근 측정 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/dot", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiDot(@RequestParam String masterIdx) throws Exception {
    masterIdx = masterIdx == "" ? "0" : masterIdx;
    List<ResultCollectionVo> res = service.selectTotalSensorApi(CommonConstant.PARAM_SENSOR_DOT);
    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "VENT 최근 측정 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/vent", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiVent(@RequestParam("masterIdx")String masterIdx) throws Exception {
    masterIdx = masterIdx == "" ? "0" : masterIdx;
    List<ResultCollectionVo> res =
        service.selectTotalSensorVentApi(CommonConstant.PARAM_SENSOR_VENT,masterIdx);
    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "IAQ-VENT 최근 측정 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/connect", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiConnect() throws Exception {
    List<ResultCollectionVo> res =
        service.selectTotalSensorConnectApi(CommonConstant.PARAM_SENSOR_CONNECT);
    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @CrossOrigin
  @ApiOperation(value = "AirMap 1차, 최근 측정 데이터 조회 API", tags = "에어맵 API")
  @RequestMapping(value = "/list/airmap", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiAirMap(
      @RequestParam(required = false) String siDo) throws Exception {

    List<AirMapOaqVo> res = service.selectTotalSensorAirMap(siDo);
    List<AirGeoInfo> res2 = dongService.selectAirKorApi(siDo);
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> resultData = new HashMap<>();
    Map<String, Object> data = new HashMap<>();

    data.put("kweather", res);
    data.put("airkorea", res2);
    resultData.put("size-airkorea", res2.size());
    resultData.put("size-kweather", res.size());
    resultData.put("data", data);
    result.put("result", resultData);
    result.put("status", HttpStatus.OK.value());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @CrossOrigin
  @ApiOperation(value = "AirMap 시각화, 최근 측정 데이터 조회 API - 시각화", tags = "에어맵 API")
  @RequestMapping(value = "/list/airmap/lightly", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiAirMapLightly(
      @RequestParam(required = false) String siDo, @RequestParam(required = false) String type) throws Exception {
    Map<String, Object> result = new HashMap<>();
    List<Map<String, Object>> resData = new ArrayList<>();
    boolean errorFlag = false;
    String resultMessage = "SUCCESS";
    long httpStatusCode = 200;
    siDo =  siDo == null? "" : siDo;
    type = type==null? "" : type;
    try {

      if ("all".equals(type)) {
        resData = service.selectTotalSensorAirMapLightly(siDo, type);
        resData.addAll(dongService.selectAirKorApiLightly(siDo));
        resData.addAll(service.selectTotalSensorAirMapLightly(siDo, "OAQ"));
        resData.addAll(service.selectTotalSensorAirMapLightly(siDo, "DOT"));

      } else if ("OAQ".equals(type.toUpperCase())) {
        resData = service.selectTotalSensorAirMapLightly(siDo, type);

      } else if ("DOT".equals(type.toUpperCase())) {
        resData = service.selectTotalSensorAirMapLightly(siDo, type);

      } else if ("kweather".equals(type)) {
        resData = service.selectTotalSensorAirMapLightly(siDo, type);
  
      } else {
        resData = dongService.selectAirKorApiLightly(siDo);
      }

    } catch (Exception e) {
      errorFlag = true;
      httpStatusCode = -100;
      resultMessage = e.getMessage();
    }

    result.put("data", resData);

    result.put("message", resultMessage);
    result.put("statusCode", httpStatusCode);
    result.put("error", errorFlag);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @CrossOrigin
  @ApiOperation(value = "AirMap 1차, 수집 데이터 이력 조회 API", tags = "에어맵 API")
  @RequestMapping(value = "/history/airmap", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionSensorApiAirMap(HttpServletRequest req) throws Exception {

    String deviceType = req.getParameter("deviceType") == null ? "" : req.getParameter("deviceType").toLowerCase().trim();
    String serial = req.getParameter("serial") == null ? "" : req.getParameter("serial").trim();
    String element = req.getParameter("element") == null ? "" : req.getParameter("element").trim();
    String date = req.getParameter("date") == null ? "20210526" : req.getParameter("date").trim();

    Map<String, Object> result = new HashMap<>();
    Map<String, Object> resultData = new HashMap<>();
    List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    data = service.collectionSensorApiAirMap(deviceType, serial, element, date);

    resultData.put("size-airkorea", data.size());
    resultData.put("data", data);
    result.put("result", resultData);
    result.put("status", HttpStatus.OK.value());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @CrossOrigin
  @ApiOperation(value = "AirMap 시각화, 스테이션 정보 조회", tags = "에어맵 API")
  @RequestMapping(value = "/info/airmap/station", method = RequestMethod.GET)
  public ResponseEntity<Object> selectAirMapStationInfo(String serial, String deviceType) throws Exception {
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> resData = new LinkedHashMap<>();
    String resultMessage = "SUCCESS";
    long httpStatusCode = 200;
    boolean errorFlag = false;

    try {

      resData = service.selectAirMapStationInfo(serial, deviceType.toLowerCase());

    } catch (Exception e) {
      resultMessage = e.getMessage();
      httpStatusCode = -100;
      errorFlag = true;
    }

    result.put("data", resData);

    result.put("message", resultMessage);
    result.put("statusCode", httpStatusCode);
    result.put("error", errorFlag);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @CrossOrigin
  @ApiOperation(value = "AirMap 시각화, 수집 데이터 이력 조회 API", tags = "에어맵 API")
  @RequestMapping(value = "/history/airmap/lightly", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionSensorApiAirMapLightly(HttpServletRequest req) throws Exception {
    Map<String, Object> result = new HashMap<>();
    List<Map<String, Object>> resData = new ArrayList<>();
    boolean errorFlag = false;
    String resultMessage = "SUCCESS";
    long httpStatusCode = 200;

    String deviceType = req.getParameter("deviceType") == null ? "" : req.getParameter("deviceType").toLowerCase().trim();
    String serial = req.getParameter("serial") == null ? "" : req.getParameter("serial").trim();
    String element = req.getParameter("element") == null ? "" : req.getParameter("element").trim();
    String date = req.getParameter("date") == null ? "today" : req.getParameter("date").trim(); // today, yesterday

    try {

      resData = service.collectionSensorApiAirMap(deviceType, serial, element, date);

    } catch (Exception e) {
      errorFlag = true;
      httpStatusCode = -100;
      resultMessage = e.getMessage();
    }

    result.put("data", resData);

    result.put("message", resultMessage);
    result.put("statusCode", httpStatusCode);
    result.put("error", errorFlag);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "수집 데이터 이력  조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/history", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionSensorApi(HttpServletRequest request) throws Exception {
    String deviceType = request.getParameter("deviceType"); // iaq
    String serial = request.getParameter("serial"); // IAQP2000001
    String startTime = request.getParameter("startTime"); // 2020/04/21-00:00:00
    String endTime = request.getParameter("endTime"); // 2020/04/21-23:59:59
    String standard = request.getParameter("standard"); // sum, 5m-avg-none, 1h-avg-none,
    // 1d-avg-none, 1n-avg-none
    String connect = request.getParameter("connect"); // 0 = VENT 연동 X, 1 = VENT 연동 O
    Map<String, Object> result = new HashMap<>();
    List<ResultCollectionHisVo> res;

    try {

      if (standard.equals("1d-avg-none") || standard.equals("1n-avg-none")) {
        res = service.selectSensorHistoryDayMonth(deviceType, serial, startTime, endTime, standard,
            connect);
      } else {
        res = service.selectSensorApi(deviceType, serial, startTime, endTime, standard, connect);
      }

    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", CommonConstant.R_SUCC_CODE);
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "(테스트 장비) 수집 데이터 최근 데이터  조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/test/list/{type}", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTestTotalSensorApiIaq(@PathVariable("type") String type)
      throws Exception {
    String sensorType = "";
    List<ResultCollectionVo> res;
    switch (type) {
      case "iaq":
        sensorType = CommonConstant.PARAM_SENSOR_IAQ;
        break;
      case "oaq":
        sensorType = CommonConstant.PARAM_SENSOR_OAQ;
        break;
      case "dot":
        sensorType = CommonConstant.PARAM_SENSOR_DOT;
        break;
    }

    if (type.equals("vent"))
      res = service.selectTestTotalSensorVentApi(CommonConstant.PARAM_SENSOR_VENT);
    else
      res = service.selectTestTotalSensorApi(sensorType);

    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/device", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionDeviceList() throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("data", service.collectionDeviceList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "대용량 데이터 다운로드 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/data/excel/download", method = RequestMethod.GET)
  public ResponseEntity<?> dataExcelDownload(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Map<String, Object> result = new HashMap<>();

    request.getParameterValues("serial");
    String deviceType = request.getParameter("deviceType"); // iaq
    String serial = request.getParameter("serial"); // IAQP2000001
    String startTime = request.getParameter("startTime"); // 2020/04/21-00:00:00
    String endTime = request.getParameter("endTime"); // 2020/04/21-23:59:59
    String standard = request.getParameter("standard"); // sum, 5m-avg-none, 1h-avg-none,

    try {

      service.downloadPlatformDataMultiDownload(deviceType, serial, startTime, endTime, standard,
          response);

      result.put("resultCode", CommonConstant.R_SUCC_CODE);

    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 OffSet 업데이트 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/offset/update", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> collectionOffsetUpdate(HttpServletRequest request)
      throws Exception {
    Map<String, Object> result = new HashMap<>();
    String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
    HashMap<String, Object> reMap = new HashMap<>();
    reMap.put("serial", serial);

    service.offsetUpdate(reMap);

    result.put("data", reMap);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "상세 페이지 차트 PDF 파일 저장 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/chart/save", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> collectionPdfSave(HttpServletRequest req)
      throws Exception {
    Map<String, Object> result = new HashMap<>();
    logger.error("request :: {}", req);

    MultipartHttpServletRequest mReq = (MultipartHttpServletRequest) req;
    MultipartFile file = mReq.getFile("equiFile");

    String serverFilePath = "D:\\html2pdf\\";

    File dest = new File(serverFilePath + "test" + ".pdf");
    assert file != null;
    file.transferTo(dest);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ExceptionHandler(value = HttpStatusCodeException.class)
  public Object handleRuntimeException() {

    Map<String, Object> result = new HashMap<>();
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("resultCode", 0);
    result.put("data", dataMap);

    return result;
  }
}
