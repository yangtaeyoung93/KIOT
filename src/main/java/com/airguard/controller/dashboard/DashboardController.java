package com.airguard.controller.dashboard;

import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(CommonConstant.URL_DASHBOARD)
public class DashboardController {

  private static final String SUPER_ITEM = "대시보드";

  @ApiOperation(value = "대시보드 수신/미수신 현황 - 구", tags = "웹 페이지 Url")
  @RequestMapping(value = "/receive", method = RequestMethod.GET)
  public String receiveDashboardPage(Model model) throws Exception {
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "수신/미수신 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "receive");
    return "/dashboard/receive";
  }

  @ApiOperation(value = "대시보드 장비 현황 - 구", tags = "웹 페이지 Url")
  @RequestMapping(value = "/equi", method = RequestMethod.GET)
  public String equiDashboardPage(Model model) throws Exception {

    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "장비 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "equi");
    return "/dashboard/equi";
  }

  @ApiOperation(value = "대시보드 사용자/그룹 현황 - 구", tags = "웹 페이지 Url")
  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public String userDashboardPage(Model model) throws Exception {

    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "사용자/그룹 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "user");
    return "/dashboard/user";
  }

  @ApiOperation(value = "동별미세먼지 현황 - 구", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dust", method = RequestMethod.GET)
  public String dustDashboardPage(Model model) throws Exception {

    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "동별미세먼지 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "dust");
    return "/dashboard/dust";
  }
}
