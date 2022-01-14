package com.airguard.controller.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airguard.model.control.ResultControlVo;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(CommonConstant.URL_API_CONTROL)
public class ControlRestController {

  @Autowired
  PlatformService service;

  private static final String PARAM_CONTROL_IAQ = "/kw-ick";
  private static final String PARAM_CONTROL_OAQ = "/kw-ock";
  private static final String PARAM_CONTROL_DOT = "/kw-ocd";

  @ApiOperation(value = "IAQ 관제 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/iaq", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalControlApiIaq() throws Exception {
    List<ResultControlVo> res = service.selectTotalControlApi(PARAM_CONTROL_IAQ, "iaq");

    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "OAQ 관제 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/oaq", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalControlApiOaq() throws Exception {
    List<ResultControlVo> res = service.selectTotalControlApi(PARAM_CONTROL_OAQ, "oaq");

    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "DOT 관제 데이터 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list/dot", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalControlApiDot() throws Exception {
    List<ResultControlVo> res = service.selectTotalControlApi(PARAM_CONTROL_DOT, "dot");

    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
