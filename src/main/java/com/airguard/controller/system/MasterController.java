package com.airguard.controller.system;

import com.airguard.model.common.Search;
import com.airguard.model.system.Group;
import com.airguard.model.system.Master;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.GroupService;
import com.airguard.service.system.MasterService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_MASTER)
public class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    @Autowired
    private MasterService service;

    @Autowired
    private PlatformService platformService;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 상위그룹 계정 등록 관리 (목록)", tags = "웹 페이지 Url")
    @GetMapping(value = "/list")
    public String masterPageList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "상위그룹 계정 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "group");
        model.addAttribute("threeDepth", "user");

        return "/system/masterList";
    }

    @ApiOperation(value = "시스템 관리 => 그룹 계정 관리 (등록)", tags = "웹 페이지 Url")
    @GetMapping(value = "/detail")
    public String masterPageInsert(Model model) throws Exception {

        model.addAttribute("btn", "id='insertBtn' value='등 록'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "상위그룹 계정 등록");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "master");
        model.addAttribute("threeDepth", "group");

        return "/system/masterDetail";
    }

    @ApiOperation(value = "시스템 관리 => 상위그룹 계정 관리 (상세)", tags = "웹 페이지 Url")
    @GetMapping(value = "/detail/{idx}")
    public String groupPageDetail(@PathVariable("idx") String idx, Model model) throws Exception {
        Master master = service.selectMasterOne(idx);
        model.addAttribute("masterData", master);

        model.addAttribute("idx", idx);
        model.addAttribute("btn", "id='updateBtn' value='수 정'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "상위그룹 계정 상세");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "master");
        model.addAttribute("threeDepth", "group");
        return "/system/groupDetail2";
    }

    @ApiOperation(value = "상위그룹 & 그룹 연동 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public ResponseEntity<Object> masterGroupList() throws Exception {
        return new ResponseEntity<>(service.selectMasterGroupList(), HttpStatus.OK);
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

        List<Master> masterData = service.selectMasterList(search);

        Map<String, Object> result = new HashMap<>();
        result.put("data", masterData);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @ApiOperation(value = "그룹 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get/{idx}/{flag}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> groupGetOne(@PathVariable("idx") String idx,@PathVariable("flag") String flag) throws Exception {
        Map<String, Object> result = new HashMap<>();
        logger.error("/system/group/get API REQUEST :: idx => {},flag => {}", idx,flag);
        result.put("data", service.selectMasterGroupOne(idx,flag));
        logger.error("/system/group/get API RESPONSE :: {} ", result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @ApiOperation(value = "상위그룹 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get/{idx}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> masterGetOn(@PathVariable("idx") String idx) throws Exception {
        Map<String, Object> result = new HashMap<>();
        logger.error("/system/master/get API REQUEST :: idx => {}", idx);
        result.put("data", service.selectMasterOne(idx));
        result.put("memberData", service.selectGroupMembers(idx));
        logger.error("/system/master/get API RESPONSE :: {} ", result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "상위그룹 계정 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Master> masterInsert(HttpServletRequest req, @RequestBody Master reqBody) throws Exception {
        service.insertMaster(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "상위그룹 계정 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Master> masterUpdate(HttpServletRequest req, @RequestBody Master reqBody) throws Exception {
        logger.error("reqBody ={}",reqBody.getIdx());
        service.updateMaster(reqBody, true);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "그룹 계정 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Master> masterDelete(HttpServletRequest req, @RequestBody Master reqBody) throws Exception {
        int index = 0;
        for (String s : reqBody.getChArr()) {
            reqBody.setResultCode(service.deleteMaster(s));
            if (reqBody.getResultCode() != 1) {
                reqBody.setCheckName(reqBody.getNameArr().get(index));
                break;
            }
            index++;
        }

        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "상위그룹 계정 비밀번호 변경 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/change/password", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> groupPwChg(HttpServletRequest req) throws Exception {
        Map<String, Object> result = new HashMap<>();

        Master masterInfo = new Master();
        masterInfo.setMasterIdx(req.getParameter("masterIdx"));
        masterInfo.setMasterPw(req.getParameter("password"));
        service.updateMaster(masterInfo, false);
        result.put("resultCode", CommonConstant.SUCCESS);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
