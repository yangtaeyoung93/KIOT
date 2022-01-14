package com.airguard.controller.biot;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airguard.service.biot.BiotService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class BiotRestController {

  @Autowired
  private BiotService service;

  private static final String DONG_PM = "p";
  private static final String DONG_LOC = "l";

  private static final Long ID_ER_CODE = 2L;
  private static final Long IP_ER_CODE = 3L;
  private static final Long SERVER_ER_CODE = 9L;

  private static final String ID_ER_MSG = "Please check ID .";
  private static final String IP_ER_MSG = "IP not permission .";
  private static final String SERVER_ER_MSG = "Server Exception .";

  @ApiOperation(value = "삼성 B.IoT 제공 API", tags = "외부 제공 API")
  @RequestMapping(value = "/data", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
  public Map<String, Object> biotRestApi(HttpServletRequest request) throws Exception {
    Map<String, Object> resultMap = new HashMap<>();

    String apiType = request.getParameter("api_type");
    String idType = request.getParameter("id_type");
    String id = request.getParameter("id");
    String allowIp = request.getHeader("X-Forwarded-For");

    if (allowIp == null)
      allowIp = request.getRemoteAddr();

    int authRes = service.apiAuthCheck(idType, id, apiType, allowIp);
    if (authRes == 1)
      throw new IdMatchException();
    else if (authRes == 2)
      throw new IpMatchException();

    switch (apiType) {
      case "KIOT_IAQ":
        resultMap = service.selectIaqSensorApi(idType, id);
        break;
      case "KIOT_OAQ":
        resultMap = service.selectOaqSensorApi(idType, id);
        break;
      case "DONG":
        resultMap = service.selectDongCollectionList(DONG_PM);
        break;
      case "DONG_MAST":
        resultMap = service.selectDongCollectionList(DONG_LOC);
        break;
    }

    return resultMap;
  }

  /*
   * Error Class
   */
  private static class IdMatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public IdMatchException() {
      super();
    }
  }

  private static class IpMatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public IpMatchException() {
      super();
    }
  }

  @ExceptionHandler(value = IdMatchException.class)
  public Object handleIdMatchException() {
    Map<String, Object> res = new HashMap<>();
    res.put("result", ID_ER_CODE);
    res.put("msg", ID_ER_MSG);
    return res;
  }

  @ExceptionHandler(value = IpMatchException.class)
  public Object handleIpMatchException() {
    Map<String, Object> res = new HashMap<>();
    res.put("result", IP_ER_CODE);
    res.put("msg", IP_ER_MSG);
    return res;
  }

  @ExceptionHandler(value = Exception.class)
  public Object handleException() {
    Map<String, Object> res = new HashMap<>();
    res.put("result", SERVER_ER_CODE);
    res.put("msg", SERVER_ER_MSG);
    return res;
  }
}
