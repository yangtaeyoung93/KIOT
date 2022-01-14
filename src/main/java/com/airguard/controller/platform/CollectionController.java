package com.airguard.controller.platform;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(CommonConstant.URL_COLLECTION)
public class CollectionController {

  @ApiOperation(value = "수집 데이터 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/device/list/{type}", method = RequestMethod.GET)
  public String collectionDeviceList(@PathVariable("type") String type, Model model)
      throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", type.toUpperCase());
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", type);

    return "/collection/" + type;
  }

  @ApiOperation(value = "수집 데이터 IAQ-VENT 연동 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/connect/list", method = RequestMethod.GET)
  public String collectionConnectList(Model model) throws Exception {

    model.addAttribute("superItem", "수집데이터");
    model.addAttribute("item", "IAQ-VENT 연동");
    model.addAttribute("oneDepth", "collection");
    model.addAttribute("twoDepth", "connect");

    return "/collection/connect";
  }

  @ApiOperation(value = "수집 데이터 상세 팝업 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/popup", method = RequestMethod.POST)
  public String detailPopup() throws Exception {
    return "/collection/popup";
  }

  @ApiOperation(value = "프로젝트 관리, 상세 팝업 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/proPopup", method = RequestMethod.POST)
  public String projectManagePopup() throws Exception {
    return "/collection/proPopup";
  }

  @ApiOperation(value = "VENT 상세 팝업 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/vent_popup", method = RequestMethod.POST)
  public String detailVentPopup() throws Exception {
    return "/collection/vent_popup";
  }

  @ApiOperation(value = "수집 데이터 IAQ-VENT 연동 상세 팝업 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/connect_popup", method = RequestMethod.POST)
  public String detailConnectPopup() throws Exception {
    return "/collection/connect_popup";
  }
}
