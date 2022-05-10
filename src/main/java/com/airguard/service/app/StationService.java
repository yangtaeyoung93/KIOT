package com.airguard.service.app;

import java.lang.reflect.Field;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.airguard.exception.SQLException;
import com.airguard.model.app.*;
import com.airguard.model.system.Device;
import com.airguard.model.system.DeviceElements;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.model.platform.PlatformSensorDto;
import com.airguard.model.platform.ResultCollectionHisVo;
import com.airguard.model.platform.SensorDataDto;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class StationService {

  private static final Logger logger = LoggerFactory.getLogger(StationService.class);

  @Autowired
  StationMapper mapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public ResponseModel chgStationName(AppStation station, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      AppUser reqUser = new AppUser();
      reqUser.setStationNo(station.getStationNo());
      reqUser.setUserId(station.getUserId());
      AppUser userInfo = readOnlyMapper.userSerialMatchCheck(reqUser);

      if (readOnlyMapper.stationNameCheck(station.getStationName(), userInfo.getMemberIdx()) != 0) {
        logger.error(
            "Call chgStationName API FAIL : [ ErrorCode : {} ErrorMsg : Please Station name Check, StationName : {}, ClientIp : {}",
            1L, station.getStationName(), request.getHeader("X-Forwarded-For") + " ]");
        res.setErrorCode(1L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;

      } else if (userInfo.getMemberIdx().isEmpty() || userInfo.getDeviceIdx().isEmpty()) {
        logger.error(
            "Call chgStationName API FAIL : [ ErrorCode : {} ErrorMsg : Please Member & Device Auth Check, StationName : {}, ClientIp : {}",
            4L, station.getStationName(), request.getHeader("X-Forwarded-For") + " ]");
        res.setErrorCode(4L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;

      } else {
        station.setDeviceIdx(userInfo.getDeviceIdx());
        mapper.chgStationName(station);
        res.setErrorCode(0L);
        res.setResult(CommonConstant.R_SUCC_CODE);
      }

    } catch (RuntimeException e) {
      logger.error(
          "Call chgStationName API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, StationName : {}, ClientIp : {}",
          9L, station.getStationName(), request.getHeader("X-Forwarded-For") + " ]");
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(chgStationName) KIOT");
    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseModel chgStation(AppStation station, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    AppStation stationIdxs = readOnlyMapper.selectDeviceIdxs(station);
    AppStation oldStation = readOnlyMapper.selectInfoSave(stationIdxs.getOldDeviceIdx());
    oldStation.setNewDeviceIdx(stationIdxs.getNewDeviceIdx());

    if (readOnlyMapper.serialCheck(station.getNewStationNo()) != 0) {
      logger.error(
          "Call chgStation API FAIL : [ ErrorCode : {} ErrorMsg : Please Serial Number Check, New Serial : {}, ClientIp : {}",
          1L, station.getNewStationNo(), request.getHeader("X-Forwarded-For") + " ]");
      res.setErrorCode(1L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else if (stationIdxs.getMemberIdx().isEmpty() || stationIdxs.getOldDeviceIdx().isEmpty()
        || stationIdxs.getNewDeviceIdx().isEmpty()) {
      logger.error(
          "Call chgStation API FAIL : [ ErrorCode : {} ErrorMsg : Please Serial Number Check, New Serial : {}, ClientIp : {}",
          4L, station.getNewStationNo(), request.getHeader("X-Forwarded-For") + " ]");
      res.setErrorCode(4L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else {
      mapper.delStationInfo(stationIdxs.getOldDeviceIdx());
      mapper.delVentInfo(stationIdxs.getOldDeviceIdx());
      mapper.addStationInfo(oldStation);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  public ResponseModel stationShared(AppStation station, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      mapper.stationShared(station);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (RuntimeException e) {
      logger.error(
          "Call stationShared API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, UserId : {}, ClientIp : {}",
          9L, station.getUserId(), request.getHeader("X-Forwarded-For") + " ]");
      throw new RuntimeException("(stationShared) KIOT");
    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseModel regionUpdate(AppStation station, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    AppUser reqUser = new AppUser();
    reqUser.setStationNo(station.getStationNo());
    reqUser.setUserId(station.getUserId());
    AppUser userInfo = readOnlyMapper.userSerialMatchCheck(reqUser);

    if (userInfo.getMemberIdx().isEmpty() || userInfo.getDeviceIdx().isEmpty()) {
      logger.error(
          "Call regionUpdate API FAIL : [ ErrorCode : {} ErrorMsg : Please Member Id & Serial Number Check, UserId : {}, ClientIp : {}",
          4L, station.getUserId(), request.getHeader("X-Forwarded-For") + " ]");
      res.setErrorCode(4L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else {
      station.setDeviceIdx(userInfo.getDeviceIdx());
      mapper.memberRegionUpdate(station);
      mapper.infoRegionUpdate(station);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);
    }

    res.setErrorCode(0L);
    res.setResult(CommonConstant.R_SUCC_CODE);

    return res;
  }

  public ResStationModel getCategory(AppStation station, HttpServletRequest request) {
    ResStationModel res = new ResStationModel();

    try {

      AppStation stationInfoOne = readOnlyMapper.getStationInfoOne(station);

      if (stationInfoOne != null) {
        res.setStationType(stationInfoOne.getStationType());
        res.setMainCategory(stationInfoOne.getMainCategory());
        res.setSubCategory(stationInfoOne.getSubCategory());

      } else {
        logger.error(
            "Call getCategory API API FAIL : [ ErrorCode : {} ErrorMsg : Serial number is not searched, Serial : {}, ClientIp : {}",
            9L, station.getStationNo(), request.getHeader("X-Forwarded-For") + " ]");
        res.setStationType("Serial number is not searched");
        res.setMainCategory("Serial number is not searched");
        res.setSubCategory("Serial number is not searched");

      }

      res.setInterest(1L);
      res.setStationNo(station.getStationNo());

      StationItemModel item = new StationItemModel();

      List<CategoryModel> categoryList = new ArrayList<>();

      List<AppStation> stationInfoList = readOnlyMapper.getStationInfoList();
      item.setCount(stationInfoList.get(0).getCount());
      String[] mainNames = stationInfoList.get(0).getSubCategoryNames().split(",");
      String[] mainIdxs = stationInfoList.get(0).getSubCategoryIdxs().split(",");

      boolean forFlag = true;
      int j = 0;
      for (AppStation s : stationInfoList) {

        if (forFlag) {
          forFlag = false;
          continue;
        }

        CategoryModel category = new CategoryModel();
        String mIdxs = s.getMainCategoryIdxs();

        for (String mi : mainIdxs) {
          if (mi.equals(mIdxs)) {
            category.setMain(mainNames[j]);
            category.setMainNum(mainIdxs[j]);
            j++;
            break;
          }
        }

        category.setSub(s.getSubCategoryNames());
        category.setSubNum(s.getSubCategoryIdxs());

        categoryList.add(category);
      }

      item.setCategoryList(categoryList);
      res.setItem(item);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (RuntimeException e) {
      logger.error(
          "Call getCategory API API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, Serial : {}, ClientIp : {}",
          9L, station.getStationNo(), request.getHeader("X-Forwarded-For") + " ]");
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(getCategory) KIOT");
    }

    return res;
  }

  public ResponseModel setCategory(AppStation station, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      station.setSpaceIdx(station.getSubCategory());
      mapper.setStationInfo(station);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (RuntimeException e) {
      logger.error(
          "Call setCategory API API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {}",
          9L, request.getHeader("X-Forwarded-For") + " ]");
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(setCategory) KIOT");
    }

    return res;
  }

  public IotDataModel iotData(String serial, HttpServletRequest request) throws Exception {
    IotDataModel res = new IotDataModel();
    String apiType;

    try {

      String deviceType = readOnlyMapper.getDeviceType(serial);
      AppStation stationInfoVo = readOnlyMapper.getStationInfo(serial);

      if (deviceType == null || deviceType.equals("")) {
        logger.error(
            "Call iotData API FAIL : [ ErrorMsg : Please Serial Number Check, Serial : {}, ClientIp : {}",
            serial, request.getHeader("X-Forwarded-For") + " ]");
        throw new Exception("(iotData) KIOT, Please Serial Number Check .");
      }

      switch (deviceType) {
        case "IAQ":
          apiType = CommonConstant.PARAM_SENSOR_IAQ;
          break;
        case "OAQ":
          apiType = CommonConstant.PARAM_SENSOR_OAQ;
          break;
        case "DOT":
          apiType = CommonConstant.PARAM_SENSOR_DOT;
          break;
        default:
          throw new RuntimeException("(iotData) KIOT, Please Serial Number Check .");
      }

      SensorDataDto apiData = selectCollectionApi(serial, apiType);
      res.setDate(apiData.getTm() == null ? CommonConstant.NULL_DATA : apiData.getTm());
      res.setTemp(apiData.getTemp() == null ? CommonConstant.NULL_DATA : apiData.getTemp());
      res.setHumi(apiData.getHumi() == null ? CommonConstant.NULL_DATA : apiData.getHumi());
      res.setPm10(apiData.getPm10() == null ? CommonConstant.NULL_DATA : apiData.getPm10());
      res.setPm25(apiData.getPm25() == null ? CommonConstant.NULL_DATA : apiData.getPm25());
      res.setNoise(apiData.getNoise() == null ? CommonConstant.NULL_DATA : apiData.getNoise());
      res.setCo2(apiData.getCo2() == null ? CommonConstant.NULL_DATA : apiData.getCo2());
      res.setVocs(apiData.getVoc() == null ? CommonConstant.NULL_DATA : apiData.getVoc());
      res.setCo(apiData.getCo() == null ? CommonConstant.NULL_DATA : apiData.getCo());
      res.setHcho(apiData.getHcho() == null ? CommonConstant.NULL_DATA : apiData.getHcho());
      res.setRn(apiData.getRn() == null ? CommonConstant.NULL_DATA : apiData.getRn());

      res.setStudy_idx(
          apiData.getCici() == null ? (apiData.getCoci() == null ? CommonConstant.NULL_DATA
              : apiData.getCoci())
              : apiData.getCici());

      res.setStationShared(stationInfoVo.getStationShared());
      res.setKweather(stationInfoVo.getVentStations());
      res.setMainCategory(stationInfoVo.getHighSpaceIdx());
      res.setSubCategory(stationInfoVo.getSpaceIdx());

      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (HttpStatusCodeException e) {
      logger.error(
          "Call iotData API FAIL : [ ErrorMsg : HttpStatusCodeException, Serial : {}, ClientIp : {}",
          serial, request.getHeader("X-Forwarded-For") + " ]");

      res.setDate(CommonConstant.NULL_DATA);
      res.setTemp(CommonConstant.NULL_DATA);
      res.setHumi(CommonConstant.NULL_DATA);
      res.setPm10(CommonConstant.NULL_DATA);
      res.setPm25(CommonConstant.NULL_DATA);
      res.setNoise(CommonConstant.NULL_DATA);
      res.setCo2(CommonConstant.NULL_DATA);
      res.setVocs(CommonConstant.NULL_DATA);
      res.setStudy_idx(CommonConstant.NULL_DATA);
      res.setStationShared(CommonConstant.NULL_DATA);
      res.setKweather(CommonConstant.NULL_DATA);
      res.setMainCategory(CommonConstant.NULL_DATA);
      res.setSubCategory(CommonConstant.NULL_DATA);
      res.setCo(CommonConstant.NULL_DATA);
      res.setHcho(CommonConstant.NULL_DATA);
      res.setRn(CommonConstant.NULL_DATA);

      res.setResult(CommonConstant.R_SUCC_CODE);

      return res;

    } catch (RuntimeException e) {
      logger.error(
          "Call iotData API FAIL : [ ErrorMsg : RuntimeException, Serial : {}, ClientIp : {}",
          serial, request.getHeader("X-Forwarded-For") + " ]");

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(iotData) KIOT");
    }

    return res;
  }

  public Map<String, Object> iotDataV2(String serial, HttpServletRequest request) throws Exception {
    Map<String, Object> res = new HashMap<>();
    String apiType;

    try {
      String deviceType;
      AppStation stationInfoVo;

      List<String> deviceElCheck = new ArrayList<>();

      deviceType = readOnlyMapper.getDeviceType(serial);
      stationInfoVo = readOnlyMapper.getStationInfo(serial);
      if (deviceType == null) {
        throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
      } else if (stationInfoVo == null) {
        throw new SQLException(SQLException.SQL_EXCEPTION);
      } else {
        Device deviceInfo = readOnlyMapper.getDeviceInfoBySerial(serial);
        List<DeviceElements> deviceElList = readOnlyMapper
            .selectOneDeviceModelElements(deviceInfo.getDeviceModelIdx());

        for (DeviceElements deviceEl : deviceElList) {
          if (deviceEl.getGubun().equals("1")) {
            deviceElCheck.add(deviceEl.getEngName());
          }
        }
      }

      switch (deviceType) {
        case "":
          logger.error(
              "Call iotData API FAIL : [ ErrorMsg : Please Serial Number Check, Serial : {}, ClientIp : {}",
              serial, request.getHeader("X-Forwarded-For") + " ]");
          throw new Exception("(iotData) KIOT, Please Serial Number Check .");
        case "IAQ":
          apiType = CommonConstant.PARAM_SENSOR_IAQ;
          break;
        case "OAQ":
          apiType = CommonConstant.PARAM_SENSOR_OAQ;
          break;
        case "DOT":
          apiType = CommonConstant.PARAM_SENSOR_DOT;
          break;
        default:
          throw new RuntimeException("(iotData) KIOT, Please Serial Number Check .");
      }

      SensorDataDto apiData = selectCollectionApi(serial, apiType);

      Field[] fields = apiData.getClass().getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        if (deviceElCheck.contains(field.getName())) {
          try {
            res.put(field.getName(), field.get(apiData));
            deviceElCheck.remove(field.getName());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }

      for (String elCheck : deviceElCheck) {
        res.put(elCheck, "NA");
      }

      res.put("date", apiData.getTm() == null ? CommonConstant.NULL_DATA : apiData.getTm());
      res.put("total", apiData.getCici() == null ? CommonConstant.NULL_DATA : apiData.getCici());
      res.put("study_idx",
          apiData.getCici() == null ? (apiData.getCoci() == null ? CommonConstant.NULL_DATA
              : apiData.getCoci()) : apiData.getCici());
      res.put("station_shared", stationInfoVo.getStationShared());
      res.put("kweather", stationInfoVo.getVentStations());
      res.put("main_category", stationInfoVo.getHighSpaceIdx());
      res.put("sub_category", stationInfoVo.getSpaceIdx());

      res.put("result", CommonConstant.R_SUCC_CODE);

    } catch (
        HttpStatusCodeException e) {
      logger.error(
          "Call iotData API FAIL : [ ErrorMsg : HttpStatusCodeException, Serial : {}, ClientIp : {}",
          serial, request.getHeader("X-Forwarded-For") + " ]");
      res.put("result", CommonConstant.R_SUCC_CODE);
      return res;

    } catch (
        RuntimeException e) {
      logger.error(
          "Call iotData API FAIL : [ ErrorMsg : RuntimeException, Serial : {}, ClientIp : {}",
          serial, request.getHeader("X-Forwarded-For") + " ]");

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(iotData) KIOT");
    }

    return res;
  }

  public SensorDataDto selectCollectionApi(String serial, String deviceType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    SensorDataDto result;

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR
        + deviceType + "/" + serial);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Gson gson = new Gson();

    PlatformSensorDto resData = gson.fromJson(jObj.toString(), PlatformSensorDto.class);
    result = resData.getData();

    return result;
  }

  public ReportModel dailyReport(String serial, String fromDateStr, HttpServletRequest request)
      throws Exception {
    ReportModel res = new ReportModel();

    try {

      SimpleDateFormat transDateFormat = new SimpleDateFormat("yyyyMMdd");

      Calendar cal = Calendar.getInstance();
      Date fromDate = transDateFormat.parse(fromDateStr);
      cal.setTime(fromDate);
      cal.add(Calendar.DATE, 1);
      cal.add(Calendar.SECOND, -1);

      Date toDate = cal.getTime();

      SimpleDateFormat transStrFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
      String from = transStrFormat.format(fromDate);
      String to = transStrFormat.format(toDate);

      String apiType = "";
      String deviceType = readOnlyMapper.getDeviceType(serial);

      if (deviceType == null || deviceType.equals("")) {
        logger.error(
            "Call dailyReport API FAIL : [ ErrorMsg : deviceType Check, Serial : {}, ClientIp : {}",
            serial, request.getHeader("X-Forwarded-For") + " ]");
        res.setErrorCode(3L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;
      }

      switch (deviceType) {
        case "IAQ":
          apiType = "iaq";
          break;
        case "OAQ":
          apiType = "oaq";
          break;
        case "DOT":
          apiType = "dot";
          break;
      }

      List<ResultCollectionHisVo> collectionDataList =
          selectSensorApi(apiType, serial, from, to, "5m-avg-none", "0");

      List<ResultCollectionHisVo> zerofilleDataList;

      StringBuilder dataDtStr = new StringBuilder();
      StringBuilder tempStr = new StringBuilder();
      StringBuilder humiStr = new StringBuilder();
      StringBuilder pm10Str = new StringBuilder();
      StringBuilder pm25Str = new StringBuilder();
      StringBuilder dustStr = new StringBuilder();
      StringBuilder dustStr2 = new StringBuilder();
      StringBuilder vocsStr = new StringBuilder();
      StringBuilder co2Str = new StringBuilder();

      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(fromDate);
      cal2.add(Calendar.DATE, 1);
      cal2.add(Calendar.SECOND, -1);
      String toDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal2.getTime());

      if (collectionDataList.size() != 0) {
        zerofilleDataList = appAvgDataDailyZerofill(collectionDataList, 5, fromDate, toDt);
      } else {
        res.setResult(0L);
        res.setDataCount(0);
        res.setErrorCode(2L);
        res.setDataDt(CommonConstant.NULL_DATA);
        res.setDate(fromDateStr);
        res.setCo2(CommonConstant.NULL_DATA);
        res.setDust(CommonConstant.NULL_DATA);
        res.setDust2(CommonConstant.NULL_DATA);
        res.setHumi(CommonConstant.NULL_DATA);
        res.setPm10(CommonConstant.NULL_DATA);
        res.setPm25(CommonConstant.NULL_DATA);
        res.setTemp(CommonConstant.NULL_DATA);
        res.setVocs(CommonConstant.NULL_DATA);

        return res;
      }

      int i = 0;
      for (ResultCollectionHisVo vo : zerofilleDataList) {
        String point = (i == 0) ? "" : ",";

        dataDtStr.append(point)
            .append((vo == null || vo.getTm() == null) ? CommonConstant.NULL_DATA : vo.getTm());
        tempStr.append(point).append((vo == null || vo.getTemp() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getTemp()));
        humiStr.append(point).append((vo == null || vo.getHumi() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getHumi()));
        pm10Str.append(point).append((vo == null || vo.getPm10() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getPm10()));
        pm25Str.append(point).append((vo == null || vo.getPm25() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getPm25()));
        dustStr.append(point).append((vo == null || vo.getPm10() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getPm10()));
        dustStr2.append(point)
            .append((vo == null || vo.getPm25() == null) ? CommonConstant.NULL_DATA
                : dataFormatStr(vo.getPm25()));
        vocsStr.append(point).append((vo == null || vo.getVoc() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getVoc()));
        co2Str.append(point).append((vo == null || vo.getCo2() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getCo2()));
        i++;
      }

      res.setDataCount(i);
      res.setResult(CommonConstant.R_SUCC_CODE);
      res.setErrorCode(0L);
      res.setDate(fromDateStr);
      res.setTemp(tempStr.toString());
      res.setHumi(humiStr.toString());
      res.setPm10(pm10Str.toString());
      res.setPm25(pm25Str.toString());
      res.setDust(dustStr.toString());
      res.setDust2(dustStr2.toString());
      res.setVocs(vocsStr.toString());
      res.setCo2(co2Str.toString());

    } catch (RuntimeException e) {
      logger.error(
          "Call dailyReport API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, Serial : {}, ClientIp : {}",
          9L, serial, request.getHeader("X-Forwarded-For") + " ]");
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(dailyReport) KIOT");
    }

    return res;
  }

  public List<ResultCollectionHisVo> appAvgDataDailyZerofill(List<ResultCollectionHisVo> datas,
      int standard, Date from, String to) throws ParseException {
    List<ResultCollectionHisVo> res = new LinkedList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ResultCollectionHisVo obj;
    Date toDt = sdf.parse(to);
    Calendar cal;

    long diffTimeMinute = (toDt.getTime() - from.getTime()) / (60000L * standard);

    for (int i = 0; i <= diffTimeMinute; i++) {
      obj = new ResultCollectionHisVo();
      cal = Calendar.getInstance();
      cal.setTime(from);
      cal.add(Calendar.MINUTE, (i * standard));
      String dt = sdf.format(cal.getTime());

      for (ResultCollectionHisVo iObj : datas) {
        if (dt.equals(iObj.getFormatTimestamp())) {
          obj = iObj;
          break;
        }
      }

      res.add(obj);
    }

    return res;
  }

  public List<ResultCollectionHisVo> appAvgDataMonthZerofill(List<ResultCollectionHisVo> datas,
      int standard, Date from, String to) throws ParseException {
    List<ResultCollectionHisVo> res = new LinkedList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ResultCollectionHisVo obj;
    Date toDt = sdf.parse(to);
    Calendar cal;

    long diffTimeMinute = (toDt.getTime() - from.getTime()) / (60000L * standard);

    for (int i = 0; i <= diffTimeMinute; i++) {
      obj = new ResultCollectionHisVo();
      cal = Calendar.getInstance();
      cal.setTime(from);
      cal.add(Calendar.MINUTE, (i * standard));
      String dt = sdf.format(cal.getTime());

      for (ResultCollectionHisVo iObj : datas) {
        if (dt.equals(iObj.getFormatTimestamp())) {
          obj = iObj;
          break;

        }
      }

      res.add(obj);
    }

    return res;
  }

  public ReportModel monthlyReport(String serial, String fromDateStr, HttpServletRequest request)
      throws Exception {
    ReportModel res = new ReportModel();

    try {

      SimpleDateFormat transDateFormat = new SimpleDateFormat("yyyyMMdd");
      Calendar cal = Calendar.getInstance();
      Date fromDate = transDateFormat.parse(fromDateStr);
      cal.setTime(fromDate);
      cal.add(Calendar.MONTH, 1);
      Date toDate = cal.getTime();

      SimpleDateFormat transStrFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
      String from = transStrFormat.format(fromDate);
      String to = transStrFormat.format(toDate);

      String apiType = "";
      String deviceType = readOnlyMapper.getDeviceType(serial);

      if (deviceType == null || deviceType.equals("")) {
        logger.error("Call monthlyReport API FAIL : [ ErrorMsg : please deviceType Check . ,"
            + " Serial : {}, DeviceType : {}", serial, deviceType + " ]");
        res.setErrorCode(3L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;
      }

      switch (deviceType) {
        case "IAQ":
          apiType = "iaq";
          break;
        case "OAQ":
          apiType = "oaq";
          break;
        case "DOT":
          apiType = "dot";
          break;
      }

      List<ResultCollectionHisVo> collectionDataList =
          selectSensorApi(apiType, serial, from, to, "1d-avg-none", "0");

      List<ResultCollectionHisVo> zerofilleDataList;

      StringBuilder dataDtStr = new StringBuilder();
      StringBuilder tempStr = new StringBuilder();
      StringBuilder humiStr = new StringBuilder();
      StringBuilder pm10Str = new StringBuilder();
      StringBuilder pm25Str = new StringBuilder();
      StringBuilder dustStr = new StringBuilder();
      StringBuilder dustStr2 = new StringBuilder();
      StringBuilder vocsStr = new StringBuilder();
      StringBuilder co2Str = new StringBuilder();

      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(fromDate);
      cal2.add(Calendar.DATE, 7);
      String toDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal2.getTime());

      if (collectionDataList.size() != 0) {
        zerofilleDataList = appAvgDataMonthZerofill(collectionDataList, 1440, fromDate, toDt);
      } else {
        res.setResult(0L);
        res.setErrorCode(2L);
        res.setDataCount(0);
        res.setDataDt(CommonConstant.NULL_DATA);
        res.setDate(fromDateStr);
        res.setCo2(CommonConstant.NULL_DATA);
        res.setDust(CommonConstant.NULL_DATA);
        res.setDust2(CommonConstant.NULL_DATA);
        res.setHumi(CommonConstant.NULL_DATA);
        res.setPm10(CommonConstant.NULL_DATA);
        res.setPm25(CommonConstant.NULL_DATA);
        res.setTemp(CommonConstant.NULL_DATA);
        res.setVocs(CommonConstant.NULL_DATA);

        return res;
      }

      int i = 0;
      for (ResultCollectionHisVo vo : zerofilleDataList) {
        String point = (i == 0) ? "" : ",";
        dataDtStr.append(point)
            .append((vo == null || vo.getTm() == null) ? CommonConstant.NULL_DATA : vo.getTm());
        tempStr.append(point).append((vo == null || vo.getTemp() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getTemp()));
        humiStr.append(point).append((vo == null || vo.getHumi() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getHumi()));
        pm10Str.append(point).append((vo == null || vo.getPm10() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getPm10()));
        pm25Str.append(point).append((vo == null || vo.getPm25() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getPm25()));
        dustStr.append(point).append((vo == null || vo.getPm10() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getPm10()));
        dustStr2.append(point)
            .append((vo == null || vo.getPm25() == null) ? CommonConstant.NULL_DATA
                : dataFormatStr(vo.getPm25()));
        vocsStr.append(point).append((vo == null || vo.getVoc() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getVoc()));
        co2Str.append(point).append((vo == null || vo.getCo2() == null) ? CommonConstant.NULL_DATA
            : dataFormatStr(vo.getCo2()));
        i++;
        if (i == 7) {
          break;
        }
      }

      res.setDataCount(i);
      res.setResult(CommonConstant.R_SUCC_CODE);
      res.setErrorCode(0L);
      res.setDate(fromDateStr);
      res.setTemp(tempStr.toString());
      res.setHumi(humiStr.toString());
      res.setPm10(pm10Str.toString());
      res.setPm25(pm25Str.toString());
      res.setDust(dustStr.toString());
      res.setDust2(dustStr2.toString());
      res.setVocs(vocsStr.toString());
      res.setCo2(co2Str.toString());

    } catch (RuntimeException e) {
      logger.error(
          "Call monthlyReport API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, Serial : {}, ClientIp : {}",
          9L, serial, request.getHeader("X-Forwarded-For") + " ]");
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(monthlyReport) KIOT");
    }

    return res;
  }


  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseGroupDidModel groupDid(String groupNo) throws JSONException {
    ResponseGroupDidModel res = new ResponseGroupDidModel();

    List<AppGroupDidVO> list = readOnlyMapper.getGroupDid(groupNo);
    List<AppGroupDid> agd = new ArrayList<>();


    String lat = list.get(0).getLat();
    String lon = list.get(0).getLon();

    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(10 * 1000);
    factory.setReadTimeout(30 * 1000);
    RestTemplate restTemplate = new RestTemplate(factory);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type",MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    headers.add("auth",CommonConstant.AUTH);



    URI uri = URI.create("https://kwapi.kweather.co.kr/v1/gis/geo/loctoaddr?lat="+lat+"&lon="+lon);

    RequestEntity<String> req = new RequestEntity<>(headers,HttpMethod.GET,uri);
    ResponseEntity<String> response = restTemplate.exchange(req, String.class);
    StringBuilder sb = new StringBuilder();
    String cityId;
    try {
      JSONObject jobj = new JSONObject(response.getBody());
      JSONObject obj = jobj.getJSONObject("data");

      sb.append(obj.get("sido_nm")).append(" ").append(obj.get("sg_nm")).append(" ").append(obj.get("emd_nm"));
      cityId = obj.get("city_id").toString();
    } catch (Exception e) {
      throw e;
    }
    AppGroupDid appGroupDid = new AppGroupDid();
    for (AppGroupDidVO vo : list) {
      appGroupDid.setStationNo(vo.getStationNo());
      appGroupDid.setStationName(vo.getStationName());
      appGroupDid.setRegionNo(cityId);
      appGroupDid.setRegionName(sb.toString());
      agd.add(appGroupDid);
    }

    res.setListCount(agd.size());
    res.setObj(agd);

    res.setErrorCode(0L);
    res.setResult(CommonConstant.R_SUCC_CODE);

    return res;
  }

  public String dataFormatStr(Object data) {
    double doubleData = Double.parseDouble(data.toString());
    long longData = Math.round(doubleData);
    return Long.toString(longData);
  }

  public List<ResultCollectionHisVo> selectSensorApi(String deviceType, String serial,
      String startTime, String endTime, String standard, String connect) throws Exception {
    RestTemplate restTemplate = new RestTemplate();

    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> pm25RawMap = new LinkedHashMap<>();
    Map<String, Double> pm10RawMap = new LinkedHashMap<>();
    Map<String, Double> tempMap = new LinkedHashMap<>();
    Map<String, Double> humiMap = new LinkedHashMap<>();
    Map<String, Double> co2Map = new LinkedHashMap<>();
    Map<String, Double> vocMap = new LinkedHashMap<>();
    Map<String, Double> noiseMap = new LinkedHashMap<>();
    Map<String, Double> ciciMap = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> ciciPm10Map = new LinkedHashMap<>();
    Map<String, Double> ciciPm25Map = new LinkedHashMap<>();
    Map<String, Double> ciciCo2Map = new LinkedHashMap<>();
    Map<String, Double> ciciVocMap = new LinkedHashMap<>();
    Map<String, Double> ciciTempMap = new LinkedHashMap<>();
    Map<String, Double> ciciHumiMap = new LinkedHashMap<>();
    Map<String, Double> ciciNoiseMap = new LinkedHashMap<>();
    Map<String, Double> luxMap = new LinkedHashMap<>();
    Map<String, Double> uvMap = new LinkedHashMap<>();
    Map<String, Double> wbgtMap = new LinkedHashMap<>();
    Map<String, Double> coMap = new LinkedHashMap<>();
    Map<String, Double> hchoMap = new LinkedHashMap<>();
    Map<String, Double> o3Map = new LinkedHashMap<>();
    Map<String, Double> rnMap = new LinkedHashMap<>();
    Map<String, Double> no2Map = new LinkedHashMap<>();
    Map<String, Double> so2Map = new LinkedHashMap<>();
    Map<String, Double> accxMap = new LinkedHashMap<>();
    Map<String, Double> accxMaxMap = new LinkedHashMap<>();
    Map<String, Double> accyMap = new LinkedHashMap<>();
    Map<String, Double> accyMaxMap = new LinkedHashMap<>();
    Map<String, Double> acczMap = new LinkedHashMap<>();
    Map<String, Double> acczMaxMap = new LinkedHashMap<>();
    Map<String, Double> winddMap = new LinkedHashMap<>();
    Map<String, Double> winddMaxMap = new LinkedHashMap<>();
    Map<String, Double> windsMap = new LinkedHashMap<>();
    Map<String, Double> windsMaxMap = new LinkedHashMap<>();
    Map<String, Double> cociMap = new LinkedHashMap<>();
    Map<String, Double> cociPm10Map = new LinkedHashMap<>();
    Map<String, Double> cociPm25Map = new LinkedHashMap<>();
    Map<String, Double> cociHumiMap = new LinkedHashMap<>();
    Map<String, Double> cociTempMap = new LinkedHashMap<>();

    Map<String, String> regDateMap = new LinkedHashMap<>();
    Map<String, String> powerMap = new LinkedHashMap<>();
    Map<String, String> airVolumeMap = new LinkedHashMap<>();
    Map<String, String> filterAlarmMap = new LinkedHashMap<>();
    Map<String, String> airModeMap = new LinkedHashMap<>();
    Map<String, String> autoModeMap = new LinkedHashMap<>();

    Map<String, String> cmdWMap = new LinkedHashMap<>();
    Map<String, String> cmdPMap = new LinkedHashMap<>();
    Map<String, String> aiModeDevicesMap = new LinkedHashMap<>();

    List<ResultCollectionHisVo> resCol = new ArrayList<>();

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    ObjectMapper objectMapper = new ObjectMapper();

    serial = serial == null ? "" : serial;
    URI url = URI.create(CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY);
    JSONObject jsonReq = new JSONObject();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "*/*");
    headers.add("Content-Type", "application/json;charset=UTF-8");

    JSONArray datasetArr = new JSONArray();
    JSONObject dataset1 = new JSONObject();
    JSONObject tagData = new JSONObject();

    tagData.put("sensor", "*");

    String metricType = "";
    switch (deviceType) {
      case "iaq":
        metricType = "kw-iaq-sensor-kiot.";
        break;
      case "oaq":
        metricType = "kw-oaq-sensor-kiot.";
        break;
      case "dot":
        metricType = "kw-oaq-sensor-dot.";
        break;
      case "vent":
        metricType = "kw-vent-sensor-kiot.";
        break;
    }

    String metric = metricType + serial;
    dataset1.put("aggregator", "sum");
    dataset1.put("metric", metric);
    dataset1.put("tags", tagData);
    dataset1.put("downsample", standard);

    datasetArr.put(0, dataset1);

    jsonReq.put("start", startTime);
    jsonReq.put("end", endTime);
    jsonReq.put("timezone", "Asia/Seoul");
    jsonReq.put("useCalendar", true);
    jsonReq.put("queries", datasetArr);

    HttpEntity<String> entity = new HttpEntity<>(jsonReq.toString(), headers);
    ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());

    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object senOb = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map senMap = objectMapper.convertValue(senOb, Map.class);

      String sensor = (String) senMap.get("sensor");

      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "tm":
            tmMap.put(key, (Double) reMap.get(key));
            break;
          case "pm25_raw":
            pm25RawMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm10_raw":
            pm10RawMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "temp":
            tempMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "humi":
            humiMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "co2":
            co2Map.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "voc":
            vocMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "noise":
            noiseMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici":
            ciciMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm10":
            pm10Map.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm25":
            pm25Map.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "cici_pm10":
            ciciPm10Map.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_pm25":
            ciciPm25Map.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_co2":
            ciciCo2Map.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_voc":
            ciciVocMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_temp":
            ciciTempMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_humi":
            ciciHumiMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_noise":
            ciciNoiseMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "lux":
            luxMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "uv":
            uvMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "wbgt":
            wbgtMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "co":
            coMap.put(key, (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "hcho":
            hchoMap.put(key, (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "o3":
            o3Map.put(key, (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "rn":
            rnMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "no2":
            no2Map.put(key, (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "so2":
            so2Map.put(key, (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "accx":
            accxMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accx_max":
            accxMaxMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accy":
            accyMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accy_max":
            accyMaxMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accz":
            acczMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accz_max":
            acczMaxMap.put(key, (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "windd":
            winddMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "windd_max":
            winddMaxMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "winds":
            windsMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "winds_max":
            windsMaxMap.put(key, (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "coci":
            cociMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_pm10":
            cociPm10Map.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_pm25":
            cociPm25Map.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_humi":
            cociHumiMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_temp":
            cociTempMap.put(key, (double) Math.round((Double) reMap.get(key)));
            break;
          case "reg_date":
            regDateMap.put(key, reMap.get(key).toString());
            break;
          case "power":
            powerMap.put(key, reMap.get(key).toString());
            break;
          case "air_volume":
            airVolumeMap.put(key, reMap.get(key).toString());
            break;
          case "filter_alarm":
            filterAlarmMap.put(key, reMap.get(key).toString());
            break;
          case "air_mode":
            airModeMap.put(key, reMap.get(key).toString());
            break;
          case "auto_mode":
            autoModeMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_w":
            cmdWMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_p":
            cmdPMap.put(key, reMap.get(key).toString());
            break;
          case "ai_mode_devices":
            aiModeDevicesMap.put(key, reMap.get(key).toString());
            break;
        }
      }
    }

    Iterator<String> keys2 = tmMap.keySet().iterator();

    if (deviceType.equals("vent")) {
      keys2 = regDateMap.keySet().iterator();
    }

    if (connect.equals("1")) {
      keys2 = cmdWMap.keySet().iterator();
    }

    while (keys2.hasNext()) {
      String key = keys2.next();
      ResultCollectionHisVo vo = new ResultCollectionHisVo();

      vo.setTimestamp(key);

      long timestamp = Long.parseLong(key);
      Date date = new java.util.Date(timestamp * 1000L);
      SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);

      vo.setFormatTimestamp(formattedDate);

      vo.setTm(tmMap.get(key));
      vo.setPm25_raw(pm25RawMap.get(key));
      vo.setPm10_raw(pm10RawMap.get(key));
      vo.setTemp(tempMap.get(key));
      vo.setHumi(humiMap.get(key));
      vo.setCo2(co2Map.get(key));
      vo.setVoc(vocMap.get(key));
      vo.setNoise(noiseMap.get(key));
      vo.setCici(ciciMap.get(key));
      vo.setPm10(pm10Map.get(key));
      vo.setPm25(pm25Map.get(key));
      vo.setCici_pm10(ciciPm10Map.get(key));
      vo.setCici_pm25(ciciPm25Map.get(key));
      vo.setCici_co2(ciciCo2Map.get(key));
      vo.setCici_voc(ciciVocMap.get(key));
      vo.setCici_temp(ciciTempMap.get(key));
      vo.setCici_humi(ciciHumiMap.get(key));
      vo.setCici_noise(ciciNoiseMap.get(key));
      vo.setLux(luxMap.get(key));
      vo.setUv(uvMap.get(key));
      vo.setWbgt(wbgtMap.get(key));
      vo.setCo(coMap.get(key));
      vo.setHcho(hchoMap.get(key));
      vo.setO3(o3Map.get(key));
      vo.setRn(rnMap.get(key));
      vo.setNo2(no2Map.get(key));
      vo.setSo2(so2Map.get(key));
      vo.setAccx(accxMap.get(key));
      vo.setAccx_max(accxMaxMap.get(key));
      vo.setAccy(accyMap.get(key));
      vo.setAccy_max(accyMaxMap.get(key));
      vo.setAccz(acczMap.get(key));
      vo.setAccz_max(acczMaxMap.get(key));
      vo.setWindd(winddMap.get(key));
      vo.setWindd_max(winddMaxMap.get(key));
      vo.setWinds(windsMap.get(key));
      vo.setWinds_max(windsMaxMap.get(key));
      vo.setCoci(cociMap.get(key));
      vo.setCoci_pm10(cociPm10Map.get(key));
      vo.setCoci_pm25(cociPm25Map.get(key));
      vo.setCoci_humi(cociHumiMap.get(key));
      vo.setCoci_temp(cociTempMap.get(key));
      vo.setReg_date(regDateMap.get(key));
      vo.setPower(powerMap.get(key));
      vo.setAir_volume(airVolumeMap.get(key));
      vo.setFilter_alarm(filterAlarmMap.get(key));
      vo.setAir_mode(airModeMap.get(key));
      vo.setAuto_mode(autoModeMap.get(key));
      vo.setCmd_w(cmdWMap.get(key));
      vo.setCmd_p(cmdPMap.get(key));
      vo.setAi_mode_devices(aiModeDevicesMap.get(key));

      resCol.add(vo);
    }

    return resCol;
  }

  public IotSignalDataModel iotDataForSignal(String serial, HttpServletRequest request)
      throws Exception {
    IotSignalDataModel res = new IotSignalDataModel();
    String apiType;

    try {
      String deviceType = readOnlyMapper.getDeviceType(serial);
      if (deviceType == null || deviceType.equals("")) {
        logger.error(
            "Call iotDataForSignal API FAIL : Please Serial Number Check . [ Serial : {}, ClientIp : {}",
            serial, request.getHeader("X-Forwarded-For") + "]");
        throw new RuntimeException("(iotData) KIOT, Please Serial Number Check .");
      }

      switch (deviceType) {
        case "IAQ":
          apiType = CommonConstant.PARAM_SENSOR_IAQ;
          break;
        case "OAQ":
          apiType = CommonConstant.PARAM_SENSOR_OAQ;
          break;
        case "DOT":
          apiType = CommonConstant.PARAM_SENSOR_DOT;
          break;
        default:
          throw new RuntimeException("(iotData) KIOT, Please Serial Number Check .");
      }

      SensorDataDto apiData = selectCollectionApi(serial, apiType);
      res.setDate(apiData.getTm() == null ? CommonConstant.NULL_DATA : apiData.getTm());
      res.setTemp(apiData.getTemp() == null ? CommonConstant.NULL_DATA : apiData.getTemp());
      res.setHumi(apiData.getHumi() == null ? CommonConstant.NULL_DATA : apiData.getHumi());
      res.setPm10(apiData.getPm10() == null ? CommonConstant.NULL_DATA : apiData.getPm10());
      res.setPm25(apiData.getPm25() == null ? CommonConstant.NULL_DATA : apiData.getPm25());
      res.setNoise(apiData.getNoise() == null ? CommonConstant.NULL_DATA : apiData.getNoise());
      res.setCo2(apiData.getCo2() == null ? CommonConstant.NULL_DATA : apiData.getCo2());
      res.setVocs(apiData.getVoc() == null ? CommonConstant.NULL_DATA : apiData.getVoc());
      res.setAccx(apiData.getAccx() == null ? CommonConstant.NULL_DATA : apiData.getAccx());
      res.setAccy(apiData.getAccy() == null ? CommonConstant.NULL_DATA : apiData.getAccy());
      res.setAccz(apiData.getAccz() == null ? CommonConstant.NULL_DATA : apiData.getAccz());
      res.setLux(apiData.getLux() == null ? CommonConstant.NULL_DATA : apiData.getLux());
      res.setUv(apiData.getUv() == null ? CommonConstant.NULL_DATA : apiData.getUv());
      res.setWindd(apiData.getWindd() == null ? CommonConstant.NULL_DATA : apiData.getWindd());
      res.setWinds(apiData.getWinds() == null ? CommonConstant.NULL_DATA : apiData.getWinds());
      res.setSo2(apiData.getSo2() == null ? CommonConstant.NULL_DATA : apiData.getSo2());
      res.setNo2(apiData.getNo2() == null ? CommonConstant.NULL_DATA : apiData.getNo2());
      res.setNh3(apiData.getNh3() == null ? CommonConstant.NULL_DATA : apiData.getNh3());
      res.setH2s(apiData.getH2s() == null ? CommonConstant.NULL_DATA : apiData.getH2s());
      res.setWbgt(apiData.getWbgt() == null ? CommonConstant.NULL_DATA : apiData.getWbgt());
      res.setO3(apiData.getO3() == null ? CommonConstant.NULL_DATA : apiData.getO3());

      res.setCo(CommonConstant.NULL_DATA);
      res.setHcho(CommonConstant.NULL_DATA);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (HttpStatusCodeException e) {
      res.setDate(CommonConstant.NULL_DATA);
      res.setTemp(CommonConstant.NULL_DATA);
      res.setHumi(CommonConstant.NULL_DATA);
      res.setPm10(CommonConstant.NULL_DATA);
      res.setPm25(CommonConstant.NULL_DATA);
      res.setNoise(CommonConstant.NULL_DATA);
      res.setCo2(CommonConstant.NULL_DATA);
      res.setVocs(CommonConstant.NULL_DATA);
      res.setAccx(CommonConstant.NULL_DATA);
      res.setAccy(CommonConstant.NULL_DATA);
      res.setAccz(CommonConstant.NULL_DATA);
      res.setLux(CommonConstant.NULL_DATA);
      res.setUv(CommonConstant.NULL_DATA);
      res.setWindd(CommonConstant.NULL_DATA);
      res.setWinds(CommonConstant.NULL_DATA);
      res.setSo2(CommonConstant.NULL_DATA);
      res.setNo2(CommonConstant.NULL_DATA);
      res.setNh3(CommonConstant.NULL_DATA);
      res.setH2s(CommonConstant.NULL_DATA);
      res.setWbgt(CommonConstant.NULL_DATA);
      res.setO3(CommonConstant.NULL_DATA);

      res.setCo(CommonConstant.NULL_DATA);
      res.setHcho(CommonConstant.NULL_DATA);
      res.setResult(CommonConstant.R_SUCC_CODE);

      return res;
    } catch (RuntimeException e) {
      logger.error(
          "Call iotDataForSignal API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, Serial : {}, ClientIp : {}",
          9L, serial, request.getHeader("X-Forwarded-For") + " ]");
      throw new RuntimeException("(iotDataForSignal) KIOT");
    }

    return res;
  }

  public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
    JSONObject jsonObject = new JSONObject();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      try {
        jsonObject.put(key, value);
      } catch (JSONException e) {
        throw new RuntimeException("(iotDataForSignal) KIOT");
      }
    }

    return jsonObject;
  }

  public List<HashMap<String, Object>> getElements(String stationNo) {
    return mapper.getElements(stationNo);
  }
}
