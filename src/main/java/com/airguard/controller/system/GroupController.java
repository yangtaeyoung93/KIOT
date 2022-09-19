package com.airguard.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.airguard.model.system.Group;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.GroupService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_GROUP)
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService service;

    @Autowired
    private PlatformService platformService;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 그룹 계정 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String groupPageList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "그룹 계정 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "user");

        return "/system/groupList";
    }

    @ApiOperation(value = "시스템 관리 => 커스텀 웹 페이지 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/custom", method = RequestMethod.GET)
    public String groupPageCustomList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "커스텀 웹페이지");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "custom");

        return "/system/groupCustomList";
    }

    @ApiOperation(value = "시스템 관리 => 그룹 계정 관리 (등록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String groupPageInsert(Model model) throws Exception {

        model.addAttribute("btn", "id='insertBtn' value='등 록'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "그룹 계정 등록");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "user");

        return "/system/groupDetail";
    }

    @ApiOperation(value = "시스템 관리 => 그룹 계정 관리 (상세)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail/{idx}", method = RequestMethod.GET)
    public String groupPageDetail(@PathVariable("idx") String idx, Model model) throws Exception {

        Group group = service.selectGroupOne(idx);
        model.addAttribute("groupData", group);

        if (!group.getGroupCustomUrl().equals("")) {
            model.addAttribute("groupCustomUrls", group.getGroupCustomUrl().split(","));
        } else {
            model.addAttribute("groupCustomUrls", "");
        }

        model.addAttribute("idx", idx);
        model.addAttribute("btn", "id='updateBtn' value='수 정'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "그룹 계정 상세");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "user");

        return "/system/groupDetail";
    }

    @ApiOperation(value = "그룹 & 사용자 연동 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public ResponseEntity<Object> groupMemberList() throws Exception {
        return new ResponseEntity<>(service.selectGroupMemberList(), HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 계정 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> groupGet(HttpServletRequest request) throws Exception {
        String reqSearchType = request.getParameter("searchType");
        String reqSearchValue = request.getParameter("searchValue");
        String reqSearchValue2 = request.getParameter("searchValue2");
        String reqSearchUseYn = request.getParameter("searchUseYn");

        Search search = new Search();

        search.setSearchType(reqSearchType);
        search.setSearchValue(reqSearchValue);
        search.setSearchValue2(reqSearchValue2);
        search.setSearchUseYn(reqSearchUseYn);

        List<Group> groupData = service.selectGroupList(search);

        Map<String, Object> result = new HashMap<>();
        result.put("data", groupData);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get/{idx}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> groupGetOne(@PathVariable("idx") String idx) throws Exception {
        Map<String, Object> result = new HashMap<>();
        logger.error("/system/group/get API REQUEST :: idx => {}", idx);
        result.put("data", service.selectGroupOne(idx));
        result.put("memberData", service.selectGroupMembers(idx));
        logger.error("/system/group/get API RESPONSE :: {} ", result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 웹 페이지 계정 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/custom/get", method = RequestMethod.GET)
    public ResponseEntity<Object> groupCustomListGet(HttpServletRequest request) throws Exception {
        String searchValue = request.getParameter("searchValue") == null ? "" : request.getParameter("searchValue");
        List<Group> groupCustomList = service.selectGroupCustomList(searchValue);
        Map<String, Object> result = new HashMap<>();
        result.put("data", groupCustomList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 계정 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Group> groupInsert(HttpServletRequest req, @RequestBody Group reqBody) throws Exception {
        service.insertGroup(reqBody);
        platformService.publisherPlatform(reqBody.getGroupId(), "GROUP", true, req.getLocalName());
        platformService.publisherPlatform(reqBody.getGroupId(), "GROUP", false, req.getLocalName());
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }


    @ApiOperation(value = "그룹 계정 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/getSet", method = RequestMethod.GET)
    public void  groupset(HttpServletRequest req) throws Exception {
        logger.info("tttt");
        try {
            Search search = new Search();
            search.setSearchUseYn("Y");
            List<Group> groupData = service.selectGroupList(search);
            //String groupId = groupData.get(0).getGroupId();
            //platformService.publisherPlatform2(groupId, "GROUP", true, "220.95.238.39");
            //platformService.publisherPlatform3(groupId, "GROUP", true, "220.95.238.39");
           // platformService.publisherPlatform2(groupId, "GROUP", false, "220.95.238.39");
            for (int i = 0; i < groupData.size(); i++) {
                String groupId = groupData.get(i).getGroupId();

                platformService.publisherPlatform(groupId, "GROUP", true, "220.95.238.39");
                platformService.publisherPlatform(groupId, "GROUP", false, "220.95.238.39");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "그룹 계정 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Group> groupUpdate(HttpServletRequest req, @RequestBody Group reqBody) throws Exception {
        service.updateGroup(reqBody, true);
        platformService.publisherPlatform(
                platformService.idxToGroupId(Integer.toString(reqBody.getIdx())), "GROUP", true,
                req.getLocalName());
        platformService.publisherPlatform(
                platformService.idxToGroupId(Integer.toString(reqBody.getIdx())), "GROUP", false,
                req.getLocalName());
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 계정 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Group> groupDelete(HttpServletRequest req, @RequestBody Group reqBody) throws Exception {
        int index = 0;
        for (String s : reqBody.getChArr()) {
            reqBody.setResultCode(service.deleteGroup(s));
            if (reqBody.getResultCode() != 1) {
                reqBody.setCheckName(reqBody.getNameArr().get(index));
                break;
            }

            platformService.publisherPlatform(platformService.idxToGroupId(s), "GROUP", true,
                    req.getLocalName());
            platformService.publisherPlatform(platformService.idxToGroupId(s), "GROUP", false,
                    req.getLocalName());
            index++;
        }

        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 계정 비밀번호 변경 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/change/password", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> groupPwChg(HttpServletRequest req) throws Exception {
        Map<String, Object> result = new HashMap<>();

        Group groupInfo = new Group();
        groupInfo.setGroupIdx(req.getParameter("groupIdx"));
        groupInfo.setGroupPw(req.getParameter("password"));
        service.updateGroup(groupInfo, false);
        result.put("resultCode", CommonConstant.SUCCESS);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
