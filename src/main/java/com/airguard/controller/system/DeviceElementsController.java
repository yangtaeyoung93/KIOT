
package com.airguard.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import io.swagger.annotations.ApiOperation;

import com.airguard.model.system.DeviceElements;
import com.airguard.service.system.DeviceElementsService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_DEVICE_ELEMENTS)
public class DeviceElementsController {

    @Autowired
    DeviceElementsService service;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 장비 측정요소 카테고리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String deviceElementsList(Model model) throws Exception {

        List<DeviceElements> deviceElementsList = service.selectDeviceElementsAll();

        model.addAttribute("list", deviceElementsList);
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "장비 측정요소 카테고리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "elements");

        return "/system/deviceElementsList";
    }

    @ApiOperation(value = "장비 측정요소 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> deviceElementsGet() throws Exception {
        List<DeviceElements> deviceElementsList = service.selectDeviceElementsAll();
        Map<String, Object> result = new HashMap<>();
        result.put("data", deviceElementsList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 측정요소 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<DeviceElements> deviceElementsInsert(@RequestBody DeviceElements reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        service.insertDeviceElements(reqBody);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 측정요소 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<DeviceElements> deviceElementsUpdate(@RequestBody DeviceElements reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        service.updateDeviceElements(reqBody);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @ApiOperation(value = "장비 측정요소 삭제 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<DeviceElements> deviceElementsDelete(@RequestBody DeviceElements reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        for (String s : reqBody.getChArr()) {
            service.deleteDeviceElements(s);
        }

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }
}
