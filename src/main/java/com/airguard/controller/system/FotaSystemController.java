package com.airguard.controller.system;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_FOTA)
public class FotaSystemController {

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => FOTA 정보 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list/{type}", method = RequestMethod.GET)
    public String adminPageDetail(@PathVariable("type") String type, Model model) throws Exception {
        model.addAttribute("type", type);
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", type.toUpperCase());
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "fota");
        model.addAttribute("threeDepth", type);

        return "/system/" + type + "FotaList";
    }
}
