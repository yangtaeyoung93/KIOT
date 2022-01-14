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

import com.airguard.model.common.Admin;
import com.airguard.model.common.Search;
import com.airguard.service.common.AdminService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_ADMIN)
public class AdminSystemController {

    @Autowired
    private AdminService service;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "관리자 관리 => 관리자 계정 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String adminPageList(Model model) throws Exception {
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "운영자 계정 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "admin");
        model.addAttribute("threeDepth", "admin");

        return "/system/adminList";
    }

    @ApiOperation(value = "관리자 관리 => 관리자 계정 관리 (등록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String adminPageInsert(Model model) throws Exception {

        model.addAttribute("btn", "id='insertBtn' value='등 록'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "운영자 계정 등록");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "admin");
        model.addAttribute("threeDepth", "admin");

        return "/system/adminDetail";
    }

    @ApiOperation(value = "관리자 관리 => 관리자 계정 관리 (상세)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail/{idx}", method = RequestMethod.GET)
    public String adminPageDetail(@PathVariable("idx") String idx, Model model) throws Exception {

        model.addAttribute("adminData", service.selectAdminOne(idx));

        model.addAttribute("idx", idx);
        model.addAttribute("btn", "id='updateBtn' value='수 정'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "운영자 계정 상세");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "admin");
        model.addAttribute("threeDepth", "admin");

        return "/system/adminDetail";
    }

    @ApiOperation(value = "관리자 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> adminGet(HttpServletRequest request) throws Exception {
        String reqSearchUseYn = request.getParameter("searchUseYn");
        String reqSearchType = request.getParameter("searchType");
        String reqSearchValue = request.getParameter("searchValue");

        Search search = new Search();

        search.setSearchUseYn(reqSearchUseYn);
        search.setSearchType(reqSearchType);
        search.setSearchValue(reqSearchValue);

        List<Admin> equiCatList = service.selectAdminList(search);

        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "관리자 계정 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Admin> adminInsert(@RequestBody Admin reqBody) throws Exception {
        service.insertAdmin(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "관리자 계정 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Admin> adminUpdate(@RequestBody Admin reqBody) throws Exception {
        service.updateAdmin(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "관리자 계정 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Admin> adminDelete(@RequestBody Admin reqBody) throws Exception {
        for (String s : reqBody.getChArr())
            service.deleteAdmin(s);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }
}
