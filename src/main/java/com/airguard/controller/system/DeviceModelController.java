package com.airguard.controller.system;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

import com.airguard.model.common.Search;
import com.airguard.model.system.DeviceModel;
import com.airguard.service.system.DeviceAttributeService;
import com.airguard.service.system.DeviceElementsService;
import com.airguard.service.system.DeviceModelService;
import com.airguard.service.system.DeviceTypeService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_DEVICE_MODEL)

public class DeviceModelController {

  @Autowired
  DeviceModelService service;

  @Autowired
  DeviceElementsService elService;

  @Autowired
  DeviceAttributeService atService;

  @Autowired
  DeviceTypeService tyService;

  private static final String SUPER_ITEM = "시스템관리";

  @ApiOperation(value = "시스템 관리 => 장비 모델 (목록)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String deviceModelList(Model model) throws Exception {

    model.addAttribute("deviceType", tyService.selectCategoryList("Y"));
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "장비 모델");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "model");

    return "/system/deviceModelList";
  }

  @ApiOperation(value = "시스템 관리 => 장비 모델 (등록)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/detail", method = RequestMethod.GET)
  public String deviceModelInsertPage(Model model) {

    model.addAttribute("deviceType", tyService.selectCategoryList("Y"));
    model.addAttribute("deviceAt", atService.selectDeviceAttribute());
    model.addAttribute("deviceAtVent", atService.selectDeviceAttributeVent());
    model.addAttribute("deviceEl", elService.selectDeviceElements());
    model.addAttribute("deviceElVent", elService.selectDeviceElementsVent());

    model.addAttribute("selected", "selected");
    model.addAttribute("btn", "id='insertBtn' value='등 록'");

    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "장비 모델 등록");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "model");

    return "/system/deviceModelDetail";
  }

  @ApiOperation(value = "시스템 관리 => 장비 모델 (상세)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/detail/{idx}", method = RequestMethod.GET)
  public String deviceModelDetail(@PathVariable("idx") String idx, Model model) {

    DeviceModel deviceModel = service.selectDeviceModelOne(idx);

    model.addAttribute("deviceModelIdx", idx);
    model.addAttribute("data", deviceModel);
    model.addAttribute("deviceType", tyService.selectCategoryList("Y"));
    model.addAttribute("deviceAt", service.selectOneDeviceModelAttribute(idx));
    model.addAttribute("deviceAtVent", service.selectOneDeviceModelAttributeVent(idx));
    model.addAttribute("deviceEl", service.selectOneDeviceModelElements(idx));
    model.addAttribute("deviceElVent", service.selectOneDeviceModelElementsVent(idx));

    model.addAttribute("btn", "id='updateBtn' value='수 정'");

    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "장비 모델 상세");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "model");

    return "/system/deviceModelDetail";
  }

  @ApiOperation(value = "장비 모델 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public ResponseEntity<Object> categoryEquiGet(HttpServletRequest request)
      throws Exception {
    Search searchModel = new Search();

    String reqUseYn = request.getParameter("useYn");
    String reqValue = request.getParameter("value");
    String reqDeviceType = request.getParameter("deviceType");

    if (reqUseYn == null || reqUseYn.equals("")) {
      reqUseYn = "Y";
    }

    searchModel.setSearchUseYn(reqUseYn);
    searchModel.setSearchValue(reqValue);
    searchModel.setSearchType(reqDeviceType);

    List<DeviceModel> deviceModelList = service.selectDeviceModel(searchModel);

    Map<String, Object> result = new HashMap<>();
    result.put("data", deviceModelList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 모델 목록 입력 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/post", method = RequestMethod.POST)
  public ResponseEntity<DeviceModel> deviceModelInsert(@RequestBody DeviceModel reqBody)
      throws Exception {
    reqBody.setRestApiMessage(CommonConstant.SUCCESS);
    service.insertDeviceModel(reqBody);

    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

  @ApiOperation(value = "장비 모델 목록 수정 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/put", method = RequestMethod.PUT)
  public ResponseEntity<DeviceModel> deviceModelUpdate(@RequestBody DeviceModel reqBody)
      throws Exception {
    reqBody.setRestApiMessage(CommonConstant.SUCCESS);
    service.updateDeviceModel(reqBody);
    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @ApiOperation(value = "장비 모델 목록 삭제 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
  public ResponseEntity<DeviceModel> deviceModelDelete(@RequestBody DeviceModel reqBody)
      throws Exception {
    reqBody.setRestApiMessage(CommonConstant.SUCCESS);
    int index = 0;
    for (String s : reqBody.getChArr()) {
      reqBody.setResultCode(service.deleteDeviceModel(s));
      if (reqBody.getResultCode() != 1) {
        reqBody.setCheckName(reqBody.getNameArr().get(index));
        break;
      }
      index++;
    }

    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

}
