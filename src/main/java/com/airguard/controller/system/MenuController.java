package com.airguard.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
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

import com.airguard.model.common.Admin;
import com.airguard.model.system.Menu;
import com.airguard.service.system.MenuService;
import com.airguard.util.AES256Util;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_ADMIN_MENU)
public class MenuController {

    @Autowired
    private MenuService service;

    private static final String SUPER_ITEM = "시스템관리 / 운영자 관리";

    @ApiOperation(value = "운영자 관리 => 메뉴 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String menuPageList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "메뉴 관리");
        model.addAttribute("menuList", service.selectHighRankMenuList());
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "admin");
        model.addAttribute("threeDepth", "menu");
        return "/system/menuList";
    }

    @ApiOperation(value = "운영자 관리 => 메뉴 권한 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/auth/list", method = RequestMethod.GET)
    public String authMenuPageList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "메뉴 권한 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "admin");
        model.addAttribute("threeDepth", "menuAuth");

        return "/system/adminMenuList";
    }

    @ApiOperation(value = "운영자 관리 => 메뉴 권한 관리 (등록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/auth/detail/{idx}", method = RequestMethod.GET)
    public String menuPageAuthDetail(@PathVariable("idx") String idx, Model model) throws Exception {

        model.addAttribute("menuDatas", service.selectAdminMenuOne(idx));

        model.addAttribute("idx", idx);
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "메뉴 권한 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "admin");
        model.addAttribute("threeDepth", "menuAuth");

        return "/system/menuAuthDetail";
    }

    @ApiOperation(value = "메뉴 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> menuGet() throws Exception {

        List<Menu> equiCatList = service.selectMenuList("");

        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 권한 목록 조회 API - 전체 메뉴", tags = "시스템 관리 API")
    @RequestMapping(value = "/auth/get", method = RequestMethod.GET)
    public ResponseEntity<Object> menuAuthGet()
            throws Exception {

        List<Admin> dataList = service.selectAdminMenuLIst();

        Map<String, Object> result = new HashMap<>();
        result.put("data", dataList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 권한 목록 조회 API - 허용 메뉴", tags = "시스템 관리 API")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public ResponseEntity<Object> menuData(HttpServletRequest request) throws Exception {
        String adminId = "";

        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("ADMIN_AUTH")) {
                adminId = AES256Util.decrypt(c.getValue());
            }
        }
        List<Menu> equiCatList = service.selectMenuAuthList(adminId);
        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Menu> menuInsert(@RequestBody Menu reqBody) throws Exception {
        service.insertMenu(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Menu> menuUpdate(@RequestBody Menu reqBody) throws Exception {
        service.updateMenu(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Menu> menuDelete(@RequestBody Menu reqBody) throws Exception {
        for (String s : reqBody.getChArr())
            service.deleteMenu(s);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 권한 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/auth/insert", method = RequestMethod.POST)
    public ResponseEntity<?> menuAuthInsert(@RequestBody Menu reqBody) throws Exception {
        Map<String, String> res = new HashMap<>();
        service.insertMenuAuth(reqBody.getAdminIdx(), reqBody.getMenuIdx());
        res.put("message", CommonConstant.SUCCESS);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @ApiOperation(value = "메뉴 권한 삭제 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/auth/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> menuAuthDelete(@RequestBody Menu reqBody) throws Exception {
        Map<String, String> res = new HashMap<>();
        service.deleteMenuAuth(reqBody.getAdminIdx(), reqBody.getMenuIdx());
        res.put("message", CommonConstant.SUCCESS);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
