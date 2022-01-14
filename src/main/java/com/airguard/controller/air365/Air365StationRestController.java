package com.airguard.controller.air365;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.airguard.service.app.Air365StationService;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import com.airguard.util.RestApiCookieManageUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365, produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365StationRestController {

    private static final Logger logger = LoggerFactory.getLogger(Air365StationRestController.class);

    @Autowired
    private Air365StationService service;

    @Autowired
    private PlatformService platformService;

    /*
     * AIR365 App, 보유 스테이션 목록 조회
     */
    @ApiOperation(value = "AIR365 App, 보유 스테이션 목록 조회", tags = "구, AIR 365 API")
    @RequestMapping(value = "/station/list", method = {RequestMethod.GET, RequestMethod.POST})
    public HashMap<String, Object> selectStationList(HttpServletRequest request) throws Exception {

        if (!RestApiCookieManageUtil.userCookieCheck(request))
            throw new CookieAuthMatchException();

        LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String userType = request.getParameter("userType") == null ? "" : request.getParameter("userType");

        if ("".equals(userId) || "".equals(userType))
            throw new RequestMatchException();

        res = service.selectStationList(userId, userType);
        return res;
    }

    /*
     * AIR365 App, 스테이션 추가
     */
    @ApiOperation(value = "AIR365 App, 스테이션 추가", tags = "구, AIR 365 API")
    @RequestMapping(value = "/station/insert", method = RequestMethod.POST)
    public HashMap<String, Object> insertStation(HttpServletRequest request) throws Exception {

        if (!RestApiCookieManageUtil.userCookieCheck(request))
            throw new CookieAuthMatchException();

        LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
        String stationName = request.getParameter("stationName") == null ? "" : request.getParameter("stationName");
        String lon = request.getParameter("lon") == null ? "" : request.getParameter("lon");
        String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat");

        if ("".equals(userId) || "".equals(serial) || "".equals(stationName) || "".equals(lon) || "".equals(lat))
            throw new RequestMatchException();

        HashMap<String, Object> reqInfo = new HashMap<String, Object>();
        reqInfo.put("userId", userId);
        reqInfo.put("serial", serial);
        reqInfo.put("stationName", stationName);
        reqInfo.put("lon", lon);
        reqInfo.put("lat", lat);

        res = service.insertStation(reqInfo);

        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId), CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId), CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());

        return res;
    }

    /*
     * AIR365 App, 스테이션 삭제
     */
    @ApiOperation(value = "AIR365 App, 스테이션 삭제", tags = "구, AIR 365 API")
    @RequestMapping(value = "/station/delete", method = {RequestMethod.DELETE, RequestMethod.POST})
    public HashMap<String, Object> deleteStation(HttpServletRequest request) throws Exception {

        if (!RestApiCookieManageUtil.userCookieCheck(request))
            throw new CookieAuthMatchException();

        LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");

        if ("".equals(userId) || "".equals(serial))
            throw new RequestMatchException();

        res = service.deleteStation(serial);

        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId), CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId), CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());

        return res;
    }

    /*
     * AIR365 App, 스테이션 위치 정보 변경
     */
    @ApiOperation(value = "AIR365 App, 스테이션 위치 정보 변경", tags = "구, AIR 365 API")
    @RequestMapping(value = "/station/position/update", method = {RequestMethod.PUT, RequestMethod.POST})
    public HashMap<String, Object> updateStationPosition(HttpServletRequest request) throws Exception {

        if (!RestApiCookieManageUtil.userCookieCheck(request)) throw new CookieAuthMatchException();

        LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
        String lon = request.getParameter("lon") == null ? "" : request.getParameter("lon");
        String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat");

        if ("".equals(userId) || "".equals(serial) || "".equals(lon) || "".equals(lat))
            throw new RequestMatchException();

        HashMap<String, Object> reqInfo = new HashMap<String, Object>();
        reqInfo.put("userId", userId);
        reqInfo.put("serial", serial);
        reqInfo.put("stationName", null);
        reqInfo.put("lon", lon);
        reqInfo.put("lat", lat);

        res = service.updateStationPosition(reqInfo);

        return res;
    }

    /*
     * AIR365 App, 스테이션 환기청정기 연동 추가
     */
    @ApiOperation(value = "AIR365 App, 스테이션 환기청정기 연동 추가", tags = "구, AIR 365 API")
    @RequestMapping(value = "/station/insert/vent", method = RequestMethod.POST)
    public HashMap<String, Object> insertVentConnect(HttpServletRequest request) throws Exception {

        if (!RestApiCookieManageUtil.userCookieCheck(request))
            throw new CookieAuthMatchException();

        LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
        String ventSerial = request.getParameter("ventSerial") == null ? "" : request.getParameter("ventSerial");

        if ("".equals(userId) || "".equals(serial) || "".equals(ventSerial))
            throw new RequestMatchException();

        HashMap<String, Object> reqInfo = new HashMap<String, Object>();
        reqInfo.put("userId", userId);
        reqInfo.put("serial", serial);
        reqInfo.put("ventSerial", ventSerial);

        res = service.insertVentConnect(reqInfo);

        platformService.postPlatformRequestConnect(serial, request.getLocalName());
        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId),
                CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId),
                CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());

        return res;
    }

    /*
     * AIR365 App, 스테이션 환기청정기 연동 삭제
     */
    @ApiOperation(value = "AIR365 App, 스테이션 환기청정기 연동 삭제", tags = "구, AIR 365 API")
    @RequestMapping(value = "/station/delete/vent", method = {RequestMethod.DELETE, RequestMethod.POST})
    public HashMap<String, Object> deleteVentConnect(HttpServletRequest request)
            throws Exception {

        if (!RestApiCookieManageUtil.userCookieCheck(request))
            throw new CookieAuthMatchException();

        LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
        String ventSerial = request.getParameter("ventSerial") == null ? "" : request.getParameter("ventSerial");

        if ("".equals(userId) || "".equals(serial) || "".equals(ventSerial))
            throw new RequestMatchException();

        HashMap<String, Object> reqInfo = new HashMap<String, Object>();
        reqInfo.put("ventSerial", ventSerial);

        res = service.deleteVentConnect(reqInfo);

        platformService.postPlatformRequestConnect(serial, request.getLocalName());
        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
        platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId), CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
        platformService.publisherPlatform(platformService.userIdToGroupId(userId), CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());

        return res;
    }

    /* ================================================ */

    private static class CookieAuthMatchException extends Exception {
        private static final long serialVersionUID = 1L;

        public CookieAuthMatchException() {
            super();
        }
    }

    private static class RequestMatchException extends Exception {
        private static final long serialVersionUID = 1L;

        public RequestMatchException() {
            super();
        }
    }

    private static class DuplicateDataException extends Exception {
        private static final long serialVersionUID = 1L;

        public  DuplicateDataException() {
            super();
        }
    }

    @ExceptionHandler(value = CookieAuthMatchException.class)
    public HashMap<String, Object> handleCookieAuthMatchException() {
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.COOKIE_EX_MSG);
        res.put("errorCode", 8L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = RequestMatchException.class)
    public HashMap<String, Object> handleRequestMatchException() {
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.WRONG_PARAM_EX_MSG);
        res.put("errorCode", 90L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = NullPointerException.class)
    public HashMap<String, Object> handleNullPointerException() {
        logger.error(CommonConstant.NULL_PARAM_EX_MSG);
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.NULL_PARAM_EX_MSG);
        res.put("errorCode", 91L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = SQLException.class)
    public HashMap<String, Object> handleSQLException() {
        logger.error(CommonConstant.SQL_EX_MSG);
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.SQL_EX_MSG);
        res.put("errorCode", 92L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public HashMap<String, Object> handleDuplicateKeyException() {
        logger.error(CommonConstant.DUPLICATE_EX_MSG);
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.DUPLICATE_EX_MSG);
        res.put("errorCode", 93L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = DuplicateDataException.class)
    public HashMap<String, Object> handleDuplicateDataException() {
        logger.error(CommonConstant.DUPLICATE_EX_MSG);
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.DUPLICATE_EX_MSG);
        res.put("errorCode", 93L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public HashMap<String, Object> handleRuntimeException() {
        logger.error(CommonConstant.RUNTIME_EX_MSG);
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.RUNTIME_EX_MSG);
        res.put("errorCode", 9L);
        res.put("result", 0);
        return res;
    }

    @ExceptionHandler(value = Exception.class)
    public HashMap<String, Object> handleException() {
        logger.error(CommonConstant.SERVER_EX_MSG);
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("message", CommonConstant.SERVER_EX_MSG);
        res.put("errorCode", 99L);
        res.put("result", 0);
        return res;
    }
}
