package com.airguard.controller.app;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.airguard.exception.ParameterException;
import com.airguard.model.app.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.service.app.StationService;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(CommonConstant.URL_API_APP)
public class StationRestController {

    private static final Logger logger = LoggerFactory.getLogger(StationRestController.class);

    @Autowired
    private StationService service;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private DatacenterService dataCenterService;

    @ApiOperation(value = "스테이션 이름 변경", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/stationname", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseModel stationName(@ModelAttribute RequestModel reqStation,
                                     HttpServletRequest request) throws Exception {

        if (reqStation.getId().isEmpty() || reqStation.getStation_name().isEmpty()
                || reqStation.getStation_no().isEmpty()) {
            throw new Exception();
        }

        AppStation station = new AppStation();
        station.setUserId(reqStation.getId());
        station.setStationName(reqStation.getStation_name());
        station.setStationNo(reqStation.getStation_no());

        ResponseModel res = service.chgStationName(station, request);
        res.setDataUrl(request.getRequestURL().toString());
        return res;
    }

    @ApiOperation(value = "스테이션 변경", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/stationchange", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseModel stationChange(@ModelAttribute RequestModel reqStation,
                                       HttpServletRequest request) throws Exception {

        if (reqStation.getId().isEmpty() || reqStation.getOld_station_no().isEmpty()
                || reqStation.getNew_station_no().isEmpty()) {
            throw new Exception();
        }

        AppStation station = new AppStation();
        station.setUserId(reqStation.getId());
        station.setOldStationNo(reqStation.getOld_station_no());
        station.setNewStationNo(reqStation.getNew_station_no());

        ResponseModel res = service.chgStation(station, request);
        if (res.getResult() == CommonConstant.R_FAIL_CODE) {
            return res;
        }

        platformService.publisherPlatform(station.getUserId(), "USER", true, request.getLocalName());
        platformService.publisherPlatform(station.getUserId(), "USER", false, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(station.getUserId()), "GROUP",
                true, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(station.getUserId()), "GROUP",
                false, request.getLocalName());
        res.setDataUrl(request.getRequestURL().toString());

        return res;
    }

    @ApiOperation(value = "스테이션 공유 설정", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/stationshared", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseModel stationShared(@ModelAttribute RequestModel reqStation,
                                       HttpServletRequest request) throws Exception {

        if (reqStation.getId().isEmpty() || reqStation.getStation_shared().isEmpty()
                || reqStation.getStation_no().isEmpty()) {
            throw new Exception();
        }

        AppStation station = new AppStation();
        station.setUserId(reqStation.getId());
        station.setStationShared(reqStation.getStation_shared());
        station.setStationNo(reqStation.getStation_no());

        ResponseModel res = service.stationShared(station, request);
        res.setDataUrl(request.getRequestURL().toString());

        return res;
    }

    @ApiOperation(value = "위치(지역) 설정", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/stationlocate", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseModel stationLocate(@ModelAttribute RequestModel reqStation,
                                       HttpServletRequest request) throws Exception {

        if (reqStation.getId().isEmpty() || reqStation.getStation_no().isEmpty()
                || reqStation.getLat().isEmpty() || reqStation.getLon().isEmpty()
                || reqStation.getRegion().isEmpty() || reqStation.getRegion_name().isEmpty()) {
            throw new Exception();
        }

        AppStation station = new AppStation();
        station.setUserId(reqStation.getId());
        station.setStationNo(reqStation.getStation_no());
        station.setLat(reqStation.getLat());
        station.setLon(reqStation.getLon());
        station.setRegion(reqStation.getRegion());
        station.setRegionName(reqStation.getRegion_name());

        ResponseModel res = service.regionUpdate(station, request);
        res.setDataUrl(request.getRequestURL().toString());

        return res;
    }

    @ApiOperation(value = "스테이션 설치 장소 (수신용)", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/getCategory", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResStationModel getCategory(@ModelAttribute RequestModel reqStation, HttpServletRequest request) throws Exception {

        ResStationModel res;

        if (reqStation.getStation_no().isEmpty()) {
            throw new Exception();
        }
        AppStation station = new AppStation();
        station.setStationNo(reqStation.getStation_no());
        res = service.getCategory(station, request);

        return res;
    }




    @ApiOperation(value = "스테이션 설치 장소 설정", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/setCategory", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseModel setCategory(@ModelAttribute RequestModel reqStation,
                                     HttpServletRequest request) throws Exception {

        if (reqStation.getStation_no().isEmpty() || reqStation.getMain_category().isEmpty()
                || reqStation.getSub_category().isEmpty() || reqStation.getInterest().isEmpty()) {
            throw new Exception();
        }

        AppStation station = new AppStation();
        station.setStationNo(reqStation.getStation_no());
        station.setMainCategory(reqStation.getMain_category());
        station.setSubCategory(reqStation.getSub_category());
        station.setInterest(reqStation.getInterest());

        ResponseModel res = service.setCategory(station, request);
        res.setDataUrl(request.getRequestURL().toString());

        return res;
    }

    @ApiOperation(value = "IoT 데이터 조회", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/iotData", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/xml")
    public IotDataModel getIotData(@ModelAttribute RequestModel reqStation, HttpServletRequest request) throws Exception {

        IotDataModel res;

        if (reqStation.getStation_no().isEmpty()) {
            throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
        } else {
            res = service.iotData(reqStation.getStation_no(), request);
        }

        return res;
    }

    @ApiOperation(value = "IoT 데이터 조회 Version 2", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/iotData/v2", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getIotDataV2(@ModelAttribute RequestModel reqStation, HttpServletRequest request) throws Exception {

        Map<String, Object> res;

        if (reqStation.getStation_no().isEmpty()) {
            throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
        } else {
            res = service.iotDataV2(reqStation.getStation_no(), request);
        }

        return res;
    }

    @ApiOperation(value = "데이터 분석 (일간)", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/dailyreport", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ReportModel dailyReport(@ModelAttribute RequestModel reqStation, HttpServletRequest request) throws Exception {

        ReportModel res;

        if (reqStation.getStation_no().isEmpty() || reqStation.getDate().isEmpty()) {
            throw new Exception();
        }
        res = service.dailyReport(reqStation.getStation_no(), reqStation.getDate(), request);

        return res;
    }

    @ApiOperation(value = "데이터 분석 (월간)", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/monthlyreport", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ReportModel monthlyReport(@ModelAttribute RequestModel reqStation, HttpServletRequest request) throws Exception {

        ReportModel res;

        if (reqStation.getStation_no().isEmpty() || reqStation.getDate().isEmpty()) {
            throw new Exception();
        }
        res = service.monthlyReport(reqStation.getStation_no(), reqStation.getDate(), request);

        return res;
    }

    @ApiOperation(value = "그룹 DID 정보 조회", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/didgroup", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseGroupDidModel groupDid(@ModelAttribute RequestModel reqStation) throws Exception {

        ResponseGroupDidModel res;

        if (reqStation.getGroup_no().isEmpty()) {
            throw new Exception();
        }
        res = service.groupDid(reqStation.getGroup_no());

        return res;
    }

    @ApiOperation(value = "그룹 스테이션 정보 조회", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/stationInfo", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public ResponseGroupDidModel stationInfo(@ModelAttribute RequestModel reqStation) throws Exception {

        ResponseGroupDidModel res;

        if (reqStation.getGroup_no().isEmpty()) {
            throw new Exception();
        }
        res = service.stationInfo(reqStation.getGroup_no());

        return res;
    }

    @ApiOperation(value = "미세먼지 신호등 IoT 데이터 조회", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/iotDataForSignal", method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/xml")
    public IotSignalDataModel iotDataForSignal(@ModelAttribute RequestModel reqStation, HttpServletRequest request) throws Exception {

        IotSignalDataModel res;

        if (reqStation.getStation_no().isEmpty()) {
            throw new Exception();
        }
        res = service.iotDataForSignal(reqStation.getStation_no(), request);

        return res;
    }

    @ApiOperation(value = "최근 측정 데이터 조회 (그룹 계정)", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/datacenter/list", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> groupCollectionDataList(HttpServletRequest request) throws Exception {
        Map<String, Object> reMap = new HashMap<>();
        List<ResultCollectionVo> selectList = new ArrayList<>();
        String groupId = request.getParameter("groupId");
        reMap.put("groupId", groupId);
        for (String deviceType : dataCenterService.selectGroupDeviceType(groupId)) {
            selectList.addAll(dataCenterService.selectGroupSensorApi(groupId, deviceType,
                    request.getLocalName()));
        }

        reMap.put("data", selectList);

        return reMap;
    }

    @ApiOperation(value = "미세먼지 평균 데이터 조회", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/datacenter/pm/avg", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> groupCollectionAvgDataList(HttpServletRequest request) throws Exception {
        Map<String, Object> reMap = new HashMap<>();
        List<ResultCollectionVo> selectList = new ArrayList<>();
        String groupId = request.getParameter("groupId");
        reMap.put("groupId", groupId);
        for (String deviceType : dataCenterService.selectGroupDeviceType(groupId)) {
            selectList.addAll(dataCenterService.selectGroupSensorApi(groupId, deviceType,
                    request.getLocalName()));
        }

        reMap.put("data", dataCenterService.collectionDataListAvg(selectList));

        return reMap;
    }

    @ApiOperation(value = "측정 항목 조회 (장비 기준)", tags = "AirGuard.K App, 프로젝트")
    @RequestMapping(value = "/elements", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> getElements(HttpServletRequest request) throws Exception {
        Map<String, Object> reMap = new HashMap<>();
        String stationNo = request.getParameter("station_no");
        reMap.put("data", service.getElements(stationNo));
        return reMap;
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ExceptionModel handleNullPointerException() {
        logger.error(CommonConstant.NULL_TARGET_EX_MSG);
        ExceptionModel res = new ExceptionModel();
        res.setError_message(CommonConstant.NULL_TARGET_EX_MSG);
        res.setError_code(91L);
        res.setResult(0);
        return res;
    }

    @ExceptionHandler(value = SQLException.class)
    public ExceptionModel handleSQLException() {
        logger.error(CommonConstant.SQL_EX_MSG);
        ExceptionModel res = new ExceptionModel();
        res.setError_message(CommonConstant.SQL_EX_MSG);
        res.setError_code(92L);
        res.setResult(0);
        return res;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ExceptionModel handleRuntimeException() {
        logger.error(CommonConstant.RUNTIME_EX_MSG);
        ExceptionModel res = new ExceptionModel();
        res.setError_message(CommonConstant.RUNTIME_EX_MSG);
        res.setError_code(9L);
        res.setResult(0);
        return res;
    }

    @ExceptionHandler(value = Exception.class)
    public ExceptionModel handleException() {
        logger.error(CommonConstant.SERVER_EX_MSG);
        ExceptionModel res = new ExceptionModel();
        res.setError_message(CommonConstant.SERVER_EX_MSG);
        res.setError_code(99L);
        res.setResult(0);
        return res;
    }
}
