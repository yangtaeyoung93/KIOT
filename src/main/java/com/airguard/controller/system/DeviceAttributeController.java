
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

import com.airguard.model.system.DeviceAttribute;
import com.airguard.service.system.DeviceAttributeService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_DEVICE_ATTRIBUTE)
public class DeviceAttributeController {

    @Autowired
    DeviceAttributeService service;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 장비 속성 카테고리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String deviceAttributesList(Model model) throws Exception {

        List<DeviceAttribute> deviceAttributeList = service.selectDeviceAttributeAll();

        model.addAttribute("list", deviceAttributeList);
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "장비 속성 카테고리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "attribute");
        return "/system/deviceAttributeList";
    }

    @ApiOperation(value = "장비 속성 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> deviceAttributesGet() throws Exception {
        List<DeviceAttribute> deviceAttributeList = service.selectDeviceAttributeAll();
        Map<String, Object> result = new HashMap<>();
        result.put("data", deviceAttributeList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 속성 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<DeviceAttribute> deviceAttributesInsert(@RequestBody DeviceAttribute reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        service.insertDeviceAttribute(reqBody);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 속성 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<DeviceAttribute> deviceAttributesUpdate(@RequestBody DeviceAttribute reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        service.updateDeviceAttribute(reqBody);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @ApiOperation(value = "장비 속성 삭제 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<DeviceAttribute> deviceAttributesDelete(@RequestBody DeviceAttribute reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        for (String s : reqBody.getChArr()) {
            service.deleteDeviceAttribute(s);
        }

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }
}
