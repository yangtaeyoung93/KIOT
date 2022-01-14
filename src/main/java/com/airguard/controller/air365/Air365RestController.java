package com.airguard.controller.air365;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.system.MemberDeviceService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/air365", produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365RestController {

  static final Logger logger = LoggerFactory.getLogger(Air365RestController.class);

  @Autowired
  private MemberDeviceService memberDeviceService;

  @Autowired
  private DatacenterService service;

  @ApiOperation(value = "AIR365 App, 사용자 장비 조회", tags = "구, AIR 365 API")
  @RequestMapping(value = "/member/device/list", method = RequestMethod.GET)
  public HashMap<String, Object> memberDeviceAllList() {
    HashMap<String, Object> result = new HashMap<>();

    try {

      result.put("data", memberDeviceService.selectMemberDeviceListAir365());

    } catch (Exception e) {
      result.put("resultCode", 0);
    }

    result.put("resultCode", 1);

    return result;
  }

  @ApiOperation(value = "AIR365 App, 측정요소 조회", tags = "구, AIR 365 API")
  @RequestMapping(value = "/elements", method = RequestMethod.GET)
  public HashMap<String, Object> getModelElements(HttpServletRequest request) {
    HashMap<String, Object> result = new HashMap<String, Object>();

    int resultCode = 0;

    try {

      String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
      String auth = request.getParameter("auth") == null ? "" : request.getParameter("auth");
      String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");

      logger.error("/api/ai365/elements API REQUEST :: {}, {}, {}", serial, auth, userId);

      if (serial.equals("")) 
        result.put("data", service.selectDeviceModelElements(auth, userId));
      else 
        result.put("data", service.selectDeviceModelElements(serial));

      resultCode = 1;

      logger.error("/api/ai365/elements API RESPONSE :: {}", result);

    } catch (Exception e) {
      resultCode = 0;
    }

    result.put("resultCode", resultCode);

    return result;
  }
}
