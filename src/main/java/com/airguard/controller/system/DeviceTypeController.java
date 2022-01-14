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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

import com.airguard.model.system.DeviceType;
import com.airguard.service.system.DeviceTypeService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_DEVICE_TYPE)
public class DeviceTypeController {

    @Autowired
    DeviceTypeService service;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 장비 유형 카테고리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String categoryEquiSearch(HttpServletRequest request, Model model) throws Exception {
        String reqUseYn = request.getParameter("useYn");

        if (reqUseYn == null || reqUseYn.equals("")) {
            reqUseYn = "Y";
        }

        List<DeviceType> equiCatList = service.selectCategoryList(reqUseYn);
        model.addAttribute("catList", equiCatList);
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "장비 유형 카테고리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "type");

        return "/system/deviceTypeList";
    }

    @ApiOperation(value = "장비 유형 카테고리 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> categoryEquiGet(HttpServletRequest request)
            throws Exception {
        String reqUseYn = request.getParameter("useYn");

        if (reqUseYn == null || reqUseYn.equals("")) {
            reqUseYn = "Y";
        }

        List<DeviceType> equiCatList = service.selectCategoryList(reqUseYn);

        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 유형 카테고리 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<DeviceType> categoryEquiInsert(@RequestBody DeviceType reqBody) throws Exception {
        service.categorySave(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 유형 카테고리 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<DeviceType> categoryEquiUpdate(@RequestBody DeviceType reqBody)
            throws Exception {
        service.categoryUpdate(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 유형 카테고리 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<DeviceType> categoryEquiDelete(@RequestBody DeviceType reqBody)
            throws Exception {
        for (String s : reqBody.getChArr()) {
            service.categoryDelete(s);
        }
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }
}
