package com.airguard.controller.dong;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(CommonConstant.URL_DONG)
public class DongController {

  @ApiOperation(value = "동별 미세먼지 실황", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/dashboard/dev", method = RequestMethod.GET)
  public String dongPage2(Model model) throws Exception {
    model.addAttribute("gubun", "동별 미세먼지&보정");
    model.addAttribute("item", "동별 미세먼지 실황");
    model.addAttribute("oneDepth", "dong");
    model.addAttribute("twoDepth", "regionDev");
    return "/region/mapDashboardDev";
  }

  @ApiOperation(value = "동별 미세먼지 데이터", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/data", method = RequestMethod.GET)
  public String dongPage3(Model model) throws Exception {
    model.addAttribute("superItem", "동별 미세먼지&보정");
    model.addAttribute("item", "동별 미세먼지 데이터");
    model.addAttribute("oneDepth", "dong");
    model.addAttribute("twoDepth", "data");
    return "/region/dataList";
  }

  @ApiOperation(value = "동별 미세먼지 상세 팝업 화면", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/data/popup", method = RequestMethod.POST)
  public String dongPage4() throws Exception {
    return "/region/popup";
  }

  @ApiOperation(value = "에어코리아 데이터", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/airkor", method = RequestMethod.GET)
  public String dongPage5(Model model) throws Exception {
    model.addAttribute("superItem", "동별 미세먼지&보정");
    model.addAttribute("item", "에어코리아 데이터");
    model.addAttribute("oneDepth", "dong");
    model.addAttribute("twoDepth", "airKor");
    return "/region/airKorList";
  }

  @ApiOperation(value = "에어코리아 상세 팝업 화면", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/airkor/popup", method = RequestMethod.POST)
  public String dongPage6() throws Exception {
    return "/region/airKorPopUp";
  }

  @ApiOperation(value = "미세먼지 보정산출", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/region/{type}", method = RequestMethod.GET)
  public String dongPage7(@PathVariable("type") String type, Model model) throws Exception {
    type = type.toUpperCase();
    model.addAttribute("gubun", "미세먼지 보정산출");
    model.addAttribute("item", type);
    model.addAttribute("oneDepth", "dong");
    model.addAttribute("twoDepth", "offsetRegion");
    model.addAttribute("threeDepth", type);
    if (type.equals("OAQ"))
      return "/region/offsetMapOaq";
    else
      return "/region/offsetMapDot";
  }

  @ApiOperation(value = "미세먼지 보정데이터", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/pm/{type}", method = RequestMethod.GET)
  public String dongPage8(@PathVariable("type") String type, Model model) throws Exception {
    type = type.toUpperCase();
    model.addAttribute("superItem", "미세먼지 보정데이터");
    model.addAttribute("item", type);
    model.addAttribute("oneDepth", "dong");
    model.addAttribute("twoDepth", "offsetData");
    model.addAttribute("threeDepth", type);
    if (type.equals("OAQ"))
      return "/region/offsetDataOaq";
    else
      return "/region/offsetDataDot";
  }

  @ApiOperation(value = "미세먼지 보정 상세 팝업 화면", tags = "웹 페이지 Url")
  @RequestMapping(value = "/region/offset/popup", method = RequestMethod.POST)
  public String dongPage9() throws Exception {
    return "/region/dataPopup";
  }
}
