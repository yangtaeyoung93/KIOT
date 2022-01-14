package com.airguard.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

import com.airguard.model.common.ResultResponse;
import com.airguard.model.common.Search;
import com.airguard.model.system.GroupDid;
import com.airguard.service.system.GroupDidService;
import com.airguard.service.system.GroupService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_GROUP_DID)
public class GroupDidController {

    @Autowired
    GroupDidService service;

    @Autowired
    GroupService groupService;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 그룹 DID (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String groupDidPageList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "그룹 DID 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "did");

        return "/system/groupDidList";
    }

    @ApiOperation(value = "시스템 관리 => 그룹 DID (등록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/insert", method = RequestMethod.GET)
    public String groupDidPageInsert(Model model) throws Exception {

        model.addAttribute("btn", "id='insertBtn' value='등 록'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "그룹 DID 등록");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "did");

        return "/system/groupDidInsert";
    }

    @ApiOperation(value = "시스템 관리 => 그룹 DID (상세))", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail/{idx}", method = RequestMethod.GET)
    public String groupDidPageDetail(@PathVariable("idx") String idx, Model model) throws Exception {

        model.addAttribute("groupData", groupService.selectGroupOne(idx));
        model.addAttribute("groupDidList", service.selectGroupDidOne(idx));

        model.addAttribute("idx", idx);
        model.addAttribute("btn", "id='updateBtn' value='수 정'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "그룹 DID 상세");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "did");

        return "/system/groupDidDetail";
    }

    @ApiOperation(value = "그룹 DID 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> groupDidGet(HttpServletRequest request) throws Exception {
        String reqSearchValue = request.getParameter("searchValue");
        String reqSearchType = request.getParameter("searchType");
        String reqSearchUseYn = request.getParameter("searchUseYn");

        Search search = new Search();
        search.setSearchValue(reqSearchValue);
        search.setSearchType(reqSearchType);
        search.setSearchUseYn(reqSearchUseYn);

        List<GroupDid> equiCatList = service.selectGroupDidList(search);
        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<GroupDid> groupDidInsert(@RequestBody GroupDid reqBody) throws Exception {
        service.insertGroupDid(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<GroupDid> groupDidUpdate(@RequestBody GroupDid reqBody) throws Exception {
        service.updateGroupDid(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID 삭제 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> groupDidDelete(HttpServletRequest request) throws Exception {
        service.deleteGroupDid(request.getParameter("idx"));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID, 사용자 계정 추가 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/member/insert", method = RequestMethod.POST)
    public ResponseEntity<Object> groupDidMemberInsert(@RequestBody GroupDid groupDid) throws Exception {
        ResultResponse res = new ResultResponse();

        GroupDid qRes = service.groupDidMemberCheck(groupDid);

        if (qRes != null) {
            res.setInputValue(qRes.getUserId());
            res.setResultCode(0);
        } else {
            res.setInputValue("MEMBER_INSERT");
            service.insertGroupDidMemberOne(groupDid);
            res.setResultCode(1);
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID, 사용자 계정 삭제 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/member/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> groupDidMemberDelete(HttpServletRequest request) throws Exception {
        service.deleteGroupDidMemberOne(request.getParameter("groupDidIdx"), request.getParameter("memberIdx"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID, 그룹 계정 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/ajaxGroupList", method = RequestMethod.GET)
    public ResponseEntity<Object> selectGroupList() throws Exception {
        List<GroupDid> groupList = service.selectGroupList();

        return new ResponseEntity<>(groupList, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID, 그룹 사용자 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/ajaxGroupMemberList", method = RequestMethod.GET)
    public ResponseEntity<Object> selectGroupMemberList(HttpServletRequest request) throws Exception {
        List<GroupDid> groupMemberList = service.selectGroupMemberList(request.getParameter("idx"));

        return new ResponseEntity<>(groupMemberList, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 DID, DID 그룹 사용자 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/ajaxGroupDidMemberList", method = RequestMethod.GET)
    public ResponseEntity<Object> selectGroupDidMemberList(HttpServletRequest request) throws Exception {
        List<GroupDid> groupDidMemberList = service.selectGroupDidMemberList(request.getParameter("idx"));

        return new ResponseEntity<>(groupDidMemberList, HttpStatus.OK);
    }
}
