package com.airguard.controller.dashboard;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airguard.model.common.Search;
import com.airguard.service.dashboard.DashboardService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(CommonConstant.URL_API_DASHBOARD)
public class DashboardRestController {

    @Autowired
    private DashboardService service;

    private static final String SUCCESS = "SUCCESS";
    private static final int DEFAULT_MINUTE = 5;

    @ApiOperation(value = "수신 미수신 현황 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/receive", method = RequestMethod.GET, produces = "application/json")
    public Object receiveDashboardApi(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();

        Search search = new Search();
        search.setSearchValue(
                request.getParameter("statType") == null || request.getParameter("statType").equals("")
                        ? "ALL"
                        : request.getParameter("statType"));
        search.setSearchValue2(
                request.getParameter("deviceType") == null || request.getParameter("deviceType").equals("")
                        ? "ALL"
                        : request.getParameter("deviceType"));

        result.put("status", SUCCESS);
        result.put("data", service.selectReceiveDashboard(search));

        return result;
    }

    @ApiOperation(value = "수신 미수신 현황 조회 API - count", tags = "시스템 관리 API")
    @RequestMapping(value = "/receive/cnt", method = RequestMethod.GET, produces = "application/json")
    public Object receiveDashboardCntApi(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();

        int minute = (request.getParameter("minute") == null
                || !isNumeric(request.getParameter("minute")) || request.getParameter("minute").equals("0")
                || request.getParameter("minute").equals("")) ? DEFAULT_MINUTE
                : Integer.parseInt(request.getParameter("minute"));

        Map<String, Object> mapResult = service.getReceiveCnt(minute);
        result.put("data", mapResult.get("data"));
        result.put("dataUser", mapResult.get("dataUser"));

        result.put("standardTime", minute);
        result.put("status", SUCCESS);

        return result;
    }

    @ApiOperation(value = "수신 미수신 현황 조회 API - count", tags = "시스템 관리 API")
    @RequestMapping(value = "/receive/cnt/m", method = RequestMethod.GET, produces = "application/json")
    public Object receiveDashboardCntApiMySQL() throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("data", service.selectReceiveCntDashboard());
        return result;
    }

    @ApiOperation(value = "대시보드 사용자 통계 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    public Object userDashboardApi() throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userMap = new HashMap<>();

        userMap.put("cnt", service.selectUserDashboardCnt());
        userMap.put("userLogin", service.selectUserDashboardLoginCnt());

        result.put("status", SUCCESS);
        result.put("data", userMap);

        return result;
    }

    @ApiOperation(value = "대시보드 장비 통계 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/device", method = RequestMethod.GET, produces = "application/json")
    public Object deviceDashboardApi() throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> deviceMap = new HashMap<>();
        Map<String, Object> deviceConnectMap = new HashMap<>();

        deviceConnectMap.put("deviceConnect", service.selectDeviceDashboardHisCnt());
        deviceConnectMap.put("ventConnect", service.selectDeviceDashboardHisVentCnt());

        deviceMap.put("cnt", service.selectDeviceDashboardCnt());
        deviceMap.put("connect", deviceConnectMap);

        result.put("status", SUCCESS);
        result.put("data", deviceMap);

        return result;
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
