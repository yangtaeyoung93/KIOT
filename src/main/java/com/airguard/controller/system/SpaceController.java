package com.airguard.controller.system;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.airguard.model.common.Search;
import com.airguard.model.system.Space;
import com.airguard.service.system.DeviceTypeService;
import com.airguard.service.system.SpaceService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_SPACE)
public class SpaceController {

  @Autowired
  SpaceService service;

  @Autowired
  DeviceTypeService tyService;

  private static final String SUPER_ITEM = "시스템관리";

  @ApiOperation(value = "시스템 관리 => 설치 공간 카테고리 (목록)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String spaceSearch(Model model) throws Exception {
    model.addAttribute("deviceType", tyService.selectCategoryList("Y"));
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "설치 공간 카테고리");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "space");
    return "/system/spaceList";
  }

  @ApiOperation(value = "설치 공간 카테고리 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public ResponseEntity<Object> spaceGet(HttpServletRequest request) throws Exception {
    String reqDeviceTypeIdx = request.getParameter("deviceTypeIdx");
    String reqSpaceName = request.getParameter("spaceName");
    String reqParentSpaceIdx = request.getParameter("parentSpaceIdx");
    String reqUseYn = request.getParameter("useYn");

    Search search = new Search();
    search.setSearchValue(reqDeviceTypeIdx);
    search.setSearchValue2(reqSpaceName);
    search.setSearchValue3(reqParentSpaceIdx);
    search.setSearchUseYn(reqUseYn);

    List<Space> spaceList = service.selectSpaceList(search);

    Map<String, Object> result = new HashMap<>();
    result.put("data", spaceList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "설치 공간 카테고리 목록 조회 (상위) API", tags = "시스템 관리 API")
  @RequestMapping(value = "/high", method = RequestMethod.GET)
  public ResponseEntity<Object> selectHighSpace(HttpServletRequest request) throws Exception {
    String reqDeviceTypeIdx = request.getParameter("deviceTypeIdx");

    List<Space> spaceList = service.selectHighSpace(reqDeviceTypeIdx);

    Map<String, Object> result = new HashMap<>();
    result.put("data", spaceList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "설치 공간 카테고리 등록 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/post", method = RequestMethod.POST)
  public ResponseEntity<Space> spaceInsert(@RequestBody Space reqBody) throws Exception {
    service.insertSpace(reqBody);
    reqBody.setRestApiMessage(CommonConstant.SUCCESS);

    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

  @ApiOperation(value = "설치 공간 카테고리 수정 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/put", method = RequestMethod.PUT)
  public ResponseEntity<Space> spaceUpdate(@RequestBody Space reqBody) throws Exception {
    service.updateSpace(reqBody);
    reqBody.setRestApiMessage(CommonConstant.SUCCESS);

    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

  @ApiOperation(value = "설치 공간 카테고리 삭제 API", tags = "시스템 관리 API")
  @Transactional(isolation = Isolation.READ_COMMITTED)
  @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
  public ResponseEntity<Integer> spaceDelete(HttpServletRequest request) throws Exception {
    int result = 0;

    if (service.selectLowSpaceAuth(request.getParameter("idx")) == 0) {
      service.deleteSpace(request.getParameter("idx"));
    } else {
      result = 1;
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
