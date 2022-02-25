package com.airguard.controller.air365.v3;

import com.airguard.exception.CookieAuthException;
import com.airguard.exception.ParameterException;
import com.airguard.service.app.v3.Air365UserV3Service;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import com.airguard.util.RestApiCookieManageUtil;
import com.airguard.util.Sha256EncryptUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365_V3, produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365UserV3RestController {

  @Autowired
  private Air365UserV3Service service;

  @Autowired
  private PlatformService platformService;

  @ApiOperation(value = "로그인", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "password", value = "사용자 암호", required = true),
      @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "admin, member, group", required = true),
      @ApiImplicitParam(name = "token", value = "구글 메시지 토큰")
  })
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public HashMap<String, Object> appLogin(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String password =
        request.getParameter("password") == null ? "" : request.getParameter("password").trim();
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();
    String token =
        request.getParameter("token") == null ? "" : request.getParameter("token").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(password)) {
      throw new ParameterException(ParameterException.NULL_PW_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("password", password);
      reqInfo.put("userType", userType);
      reqInfo.put("token", token);

      res = service.login(reqInfo, response);
    }

    return res;
  }


}
