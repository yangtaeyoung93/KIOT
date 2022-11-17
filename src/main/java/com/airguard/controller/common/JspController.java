package com.airguard.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/new")
public class JspController {

  /**
   * 대시보드 수신/미수신 현황
   *
   * @param model
   * @return String "/dashboard/receive"
   */
  @ApiOperation(value = "대시보드 수신/미수신 현황", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dashboard/receive", method = RequestMethod.GET)
  public String pageDashboardReceive(Model model) throws Exception {

    model.addAttribute("superItem", "대시보드");
    model.addAttribute("item", "수신/미수신 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "receive");
    return "/dashboard/receive";
  }

  /**
   * 대시보드 장비 현황
   *
   * @param model
   * @return String "/dashboard/receive"
   */
  @ApiOperation(value = "대시보드 장비 현황", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dashboard/equi", method = RequestMethod.GET)
  public String pageDashboardEqui(Model model) throws Exception {

    model.addAttribute("superItem", "대시보드");
    model.addAttribute("item", "장비 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "equi");
    return "/dashboard/equi";
  }

  /**
   * 대시보드 사용자/그룹 현황
   *
   * @param model
   * @return String "/dashboard/user"
   */
  @ApiOperation(value = "대시보드 사용자/그룹 현황", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dashboard/user", method = RequestMethod.GET)
  public String pageDashboardUser(Model model) throws Exception {

    model.addAttribute("superItem", "대시보드");
    model.addAttribute("item", "사용자/그룹 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "user");
    return "/dashboard/user";
  }

  /**
   * 대시보드 Elastic 현황
   *
   * @param model
   * @return String "/dashboard/user"
   */
  @ApiOperation(value = "대시보드 Elastic 현황", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dashboard/es", method = RequestMethod.GET)
  public String pageDashboardEs(Model model) throws Exception {

    model.addAttribute("superItem", "대시보드");
    model.addAttribute("item", "ES 대시보드");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "es");
    return "/dashboard/es";
  }

  /**
   * 동별미세먼지 현황
   *
   * @param model
   * @return String "/dashboard/dust"
   */
  @ApiOperation(value = "동별미세먼지 현황", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dashboard/dust", method = RequestMethod.GET)
  public String pageDashboardDust(Model model) throws Exception {

    model.addAttribute("superItem", "대시보드");
    model.addAttribute("item", "동별미세먼지 현황");
    model.addAttribute("oneDepth", "dashboard");
    model.addAttribute("twoDepth", "dust");
    return "/dashboard/dust";
  }

  /**
   * IAQ 수집정보
   *
   * @param model
   * @return String "/collection/iaq"
   */
  @ApiOperation(value = "IAQ 수집정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/collection/iaq", method = RequestMethod.GET)
  public String pageIaqCollect(Model model) throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "IAQ");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "iaq");
    return "/collection/iaq";
  }

  /**
   * OAQ 수집정보
   *
   * @param model
   * @return String "/collection/oaq"
   */
  @ApiOperation(value = "OAQ 수집정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/collection/oaq", method = RequestMethod.GET)
  public String pageOaqCollect(Model model) throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "OAQ");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "oaq");
    return "/collection/oaq";
  }

  /**
   * DOT 수집정보
   *
   * @param model
   * @return String "/collection/dot"
   */
  @ApiOperation(value = "DOT 수집정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/collection/dot", method = RequestMethod.GET)
  public String pageDotCollect(Model model) throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "DOT");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "dot");
    return "/collection/dot";
  }

  /**
   * 환기청정기 상태정보
   *
   * @param model
   * @return String "/collection/vent"
   */
  @ApiOperation(value = "환기청정기 상태정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/collection/vent", method = RequestMethod.GET)
  public String pageVent(Model model) throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "VENT");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "vent");
    return "/collection/vent";
  }

  /**
   * IAQ-VENT 연동
   *
   * @param model
   * @return String "/collection/connect"
   */
  @ApiOperation(value = "IAQ-VENT 연동 수집 정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/collection/connect", method = RequestMethod.GET)
  public String pageConnect(Model model) throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "IAQ-VENT-OAQ 연동");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "connect");
    return "/collection/connect";
  }

  @ApiOperation(value = "대용량 데이터 다운로드", tags = "웹 페이지 Url")
  @RequestMapping(value = "/collection/dataDownload", method = RequestMethod.GET)
  public String pageDataDownload(Model model) throws Exception {
    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "데이터 다운로드");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "dataDownload");
    return "/collection/dataDownload";
  }

  @ApiOperation(value = "동별 미세먼지 데이터 다운로드", tags = "웹 페이지 Url")
  @RequestMapping(value = "/dong/region/dataDownload", method = RequestMethod.GET)
  public String pageDongDataDownload(Model model) throws Exception {
    model.addAttribute("superItem", "동별 미세먼지&보정");
    model.addAttribute("item", "동별 미세먼지 데이터 다운로드");
    model.addAttribute("oneDepth", "dong");
    model.addAttribute("twoDepth", "dataDownload");
    return "/region/dongDataDownload";
  }

  /**
   * IAQ 관제정보
   *
   * @param model
   * @return String "/monitor/iaq"
   */
  @ApiOperation(value = "IAQ 관제정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/monitor/iaq", method = RequestMethod.GET)
  public String pageIaqMonitor(Model model) throws Exception {

    model.addAttribute("superItem", "관제데이터");
    model.addAttribute("item", "IAQ");
    model.addAttribute("oneDepth", "monitor");
    model.addAttribute("twoDepth", "iaq");
    return "/monitor/iaq";
  }

  /**
   * OAQ 관제정보
   *
   * @param model
   * @return String "/monitor/oaq"
   */
  @ApiOperation(value = "OAQ 관제정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/monitor/oaq", method = RequestMethod.GET)
  public String pageOaqMonitor(Model model) throws Exception {

    model.addAttribute("superItem", "관제데이터");
    model.addAttribute("item", "OAQ");
    model.addAttribute("oneDepth", "monitor");
    model.addAttribute("twoDepth", "oaq");
    return "/monitor/oaq";
  }

  /**
   * DOT 관제정보
   *
   * @param model
   * @return String "/monitor/dot"
   */
  @ApiOperation(value = "DOT 관제정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/monitor/dot", method = RequestMethod.GET)
  public String pageDotMonitor(Model model) throws Exception {

    model.addAttribute("superItem", "관제데이터");
    model.addAttribute("item", "DOT");
    model.addAttribute("oneDepth", "monitor");
    model.addAttribute("twoDepth", "dot");
    return "/monitor/dot";
  }

  /**
   * FOTA 관제정보
   *
   * @param model
   * @return String "/monitor/fota"
   */
  @ApiOperation(value = "FOTA 관제정보", tags = "웹 페이지 Url")
  @RequestMapping(value = "/monitor/fota", method = RequestMethod.GET)
  public String pageFotaMonitor(Model model) throws Exception {

    model.addAttribute("superItem", "관제데이터");
    model.addAttribute("item", "FOTA");
    model.addAttribute("oneDepth", "monitor");
    model.addAttribute("twoDepth", "fota");
    return "/monitor/fota";
  }

  /**
   * 동별미세먼지 보정 페이지
   *
   * @param model
   * @return String "/region/dashboard"
   */
  @ApiOperation(value = "동별 미세먼지&보정", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/dashboard", method = RequestMethod.GET)
  public String pageRegionPm10(Model model) throws Exception {

    model.addAttribute("gubun", "동별 미세먼지&보정");
    model.addAttribute("item", "동별 미세먼지 실황");
    model.addAttribute("oneDepth", "region");
    model.addAttribute("twoDepth", "dashboard");
    return "/region/dashboard";
  }

  /**
   * 테스트 페이지 (비동기)
   *
   * @return String "/sample/testAsync"
   */
  @ApiOperation(value = "테스트 페이지 (비동기)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/sample/testAsync", method = RequestMethod.GET)
  public String pageSampleTestAsync() throws Exception {

    return "/sample/testAsync";
  }

  /**
   * 테스트 페이지 (비동기2) aJax 통신, 비동기 방식
   *
   * @return String "/sample/testSync"
   */
  @ApiOperation(value = "테스트 페이지 (비동기2) aJax 통신, 비동기 방식", tags = "웹 페이지 Url")
  @RequestMapping(value = "/sample/testSync", method = RequestMethod.GET)
  public String pageSampleTestSync() throws Exception {

    return "/sample/testSync";
  }

  /**
   * 테스트 페이지 (비동기3) aJax 통신, 비동기 방식
   *
   * @return String "/sample/testSync"
   */
  @ApiOperation(value = "테스트 페이지 (비동기3) aJax 통신, 비동기 방식", tags = "웹 페이지 Url")
  @RequestMapping(value = "/sample/testSync2", method = RequestMethod.GET)
  public String pageSampleTestSync2() throws Exception {

    return "/sample/testSync2";
  }

  /**
   * 테스트 페이지 (비동기3) aJax 통신, 비동기 방식
   *
   * @return String "/sample/testSync"
   */
  @ApiOperation(value = "프로젝트 관리용, 관리자 페이지 (Example, 그린서초)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/project/admin/{groupId}", method = RequestMethod.GET)
  public String pageProjectManage(Model model, @PathVariable("groupId") String groupId) throws Exception {
    model.addAttribute("groupId", groupId);
    model.addAttribute("superItem", "프로젝트관리");
    model.addAttribute("item", groupId);
    return "/collection/proManage";
  }

  @ApiOperation(value = "외부 프로젝트 관리용, 관리자 페이지 (Example, 구로구)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/project/external", method = RequestMethod.GET)
  public String externalProjectManage(Model model) throws Exception {

    return "/collection/externalProManage";
  }
}
