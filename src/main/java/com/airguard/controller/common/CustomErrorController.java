package com.airguard.controller.common;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.annotations.ApiOperation;

@Controller
public class CustomErrorController implements ErrorController {

  private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
  private static final String PATH = "/error";

  @ApiOperation(value = "에러 페이지 Redirection", tags = "에러 페이지 관리")
  @RequestMapping(value = PATH)
  public String error(HttpServletRequest request, Model model) {
    String errorCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString();
    String url = request.getRequestURL().toString();
    logger.error("ERROR Code : {}", errorCode);
    logger.error("ERROR Url : {}", url);
    model.addAttribute("url", url);

    if (Integer.parseInt(errorCode) == (HttpStatus.SC_NOT_FOUND)) {
      return "/errors/404_error";
    }

    return "/";
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }
}
