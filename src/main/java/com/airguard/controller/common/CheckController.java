package com.airguard.controller.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.airguard.model.app.ExceptionModel;
import com.airguard.model.common.ResultResponse;
import com.airguard.model.system.Space;
import com.airguard.service.common.CheckService;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/check")
public class CheckController {

  @Autowired
  CheckService service;

  @ApiOperation(value = "관리자 계정 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/adminId", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkAdminUserId(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("userId"));
    res.setResultCode(service.checkAdminUserId(request.getParameter("userId")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "개인 사용자 계정 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/userId", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkUserId(HttpServletRequest request) throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("userId"));
    res.setResultCode(service.checkUserId(request.getParameter("userId")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "그룹 사용자 계정 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/groupId", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkGroupId(HttpServletRequest request) throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("groupId"));
    res.setResultCode(service.checkGroupId(request.getParameter("groupId")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "상위그룹 사용자 계정 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/masterId", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkMasterId(HttpServletRequest request) throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("masterId"));
    res.setResultCode(service.checkMasterId(request.getParameter("masterId")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "DID 코드 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/didCode", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkDidCode(HttpServletRequest request) throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("didCode"));
    res.setResultCode(service.checkDidCode(request.getParameter("didCode")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 시리얼 번호 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/serialNum", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkSerialNum(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("serialNum"));
    res.setResultCode(service.checkSerialNum(request.getParameter("serialNum")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 모델명 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/deviceModel", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkDeviceModel(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("deviceModel"));
    res.setResultCode(service.checkDeviceModel(request.getParameter("deviceModel"),
        request.getParameter("deviceTypeIdx")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 유형 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/deviceType", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkDeviceType(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("deviceType"));
    res.setResultCode(service.checkDeviceType(request.getParameter("deviceType")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 유형 명 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/deviceTypeName", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkDeviceTypeName(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("deviceTypeName"));
    res.setResultCode(service.checkDeviceTypeName(request.getParameter("deviceTypeName")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 스테이션 명 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/stationName", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkStationName(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();
    res.setInputValue(request.getParameter("stationName"));
    res.setResultCode(service.checkStationName(request.getParameter("memberIdx"),
        request.getParameter("stationName")));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "공간 카테고리 검사 API", tags = "유효성 검사")
  @RequestMapping(value = "/spaceName", method = RequestMethod.GET)
  public ResponseEntity<ResultResponse> checkSpaceName(HttpServletRequest request)
      throws Exception {
    ResultResponse res = new ResultResponse();

    Space reqSpace = new Space();
    reqSpace.setDeviceTypeIdx(Integer.parseInt(request.getParameter("deviceTypeIdx")));
    reqSpace.setParentSpaceIdx(Integer.parseInt(request.getParameter("parentSpaceIdx")));
    reqSpace.setSpaceName(request.getParameter("spaceName"));

    res.setResultCode(service.checkSpaceName(reqSpace));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ExceptionHandler(value = Exception.class)
  public ExceptionModel handleException() {
    ExceptionModel res = new ExceptionModel();
    res.setError_code(9L);
    res.setResult(0);
    return res;
  }
}
