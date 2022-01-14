package com.airguard.controller.system;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.swagger.annotations.ApiOperation;

import com.airguard.model.common.Search;
import com.airguard.model.system.Member;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.MemberService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_MEMBER)
public class MemberController {

    static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private PlatformService platformService;

    @Autowired
    private MemberService service;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 사용자 계정 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String memberPageList(Model model) throws Exception {

        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "사용자 계정 관리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "member");
        model.addAttribute("threeDepth", "user");
        return "/system/memberList";
    }

    @ApiOperation(value = "시스템 관리 => 사용자 계정 (등록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String memberPageInsert(Model model) throws Exception {

        model.addAttribute("col", "col-12");
        model.addAttribute("readDiv", "readDiv");
        model.addAttribute("btn", "id='insertBtn' value='등 록'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "사용자 계정 등록");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "member");
        model.addAttribute("threeDepth", "user");
        return "/system/memberDetail";
    }

    @ApiOperation(value = "시스템 관리 => 사용자 계정 (상세)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail/{idx}", method = RequestMethod.GET)
    public String memberPageDetail(@PathVariable("idx") String idx,
                                   Model model) throws Exception {

        model.addAttribute("memberData", service.selectMemberOne(idx));
        model.addAttribute("appDeviceDataList", service.selectMemberAppDevice(idx));

        model.addAttribute("col", "col-6");
        model.addAttribute("idx", idx);
        model.addAttribute("btn", "id='updateBtn' value='수 정'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "사용자 계정 상세");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "member");
        model.addAttribute("threeDepth", "user");
        return "/system/memberDetail";
    }

    @ApiOperation(value = "사용자 계정 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> memberGet(HttpServletRequest request)
            throws Exception {
        String reqUseYn = request.getParameter("useYn");
        String reqUserId = request.getParameter("userId");

        Search search = new Search();

        search.setSearchUseYn(reqUseYn);
        search.setSearchValue(reqUserId);

        List<Member> equiCatList = service.selectMemberList(search);

        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get/{idx}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> memberGetOne(@PathVariable("idx") String idx)
            throws Exception {
        logger.error("/system/member/get API REQUEST :: {} ", idx);
        Map<String, Object> result = new HashMap<>();
        result.put("data", service.selectMemberOne(idx));
        logger.error("/system/member/get API RESPONSE :: {} ", result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Member> memberInsert(@RequestBody Member reqBody) throws Exception {
        service.insertMember(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Member> memberUpdate(@RequestBody Member reqBody) throws Exception {
        service.updateMember(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Member> memberDelete(HttpServletRequest req, @RequestBody Member reqBody) throws Exception {

        int index = 0;
        for (String s : reqBody.getChArr()) {
            reqBody.setResultCode(service.deleteMember(s));
            if (reqBody.getResultCode() != 1) {
                reqBody.setCheckName(reqBody.getNameArr().get(index));
                break;
            }

            platformService.publisherPlatform(platformService.idxToUserId(s), "CLEAR", true,
                    req.getLocalName());
            platformService.publisherPlatform(platformService.idxToUserId(s), "USER", false,
                    req.getLocalName());
            platformService.publisherPlatform(platformService.memberIdxToGroupId(s), "CLEAR", true,
                    req.getLocalName());
            platformService.publisherPlatform(platformService.memberIdxToGroupId(s), "GROUP", false,
                    req.getLocalName());

            index++;
        }

        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 비밀번호 변경 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/change/password", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> memberPwChg(HttpServletRequest req)
            throws Exception {
        Map<String, Object> result = new HashMap<>();
        String memberIdx = req.getParameter("memberIdx");
        String password = req.getParameter("password");
        service.updateMemberPassword(memberIdx, password);
        result.put("resultCode", CommonConstant.SUCCESS);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 일괄 등록 API (with, Excel File)", tags = "시스템 관리 API")
    @RequestMapping(value = "/excelInsert", method = RequestMethod.POST)
    public ResponseEntity<String> deviceExcelInsert(MultipartHttpServletRequest req)
            throws Exception {
        String result;
        String excelType = req.getParameter("excelType");

        if (excelType.equals("xlsx")) {
            result = service.xlsxMemberExcelReader(req);
        } else {
            return new ResponseEntity<>("EXT_CHECK", HttpStatus.OK);
        }

        logger.info("sames serial : " + result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 계정 일괄 등록 양식 다운로드 API (With, Excel File)", tags = "시스템 관리 API")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public ResponseEntity<?> excelDownload() throws Exception {
        String filePath = "/NAS2_NFS/IOT_KITECH/DOC_TEMPLATE/KIOT_member_template.xlsx";
        String fileName = "KIOT_member_template.xlsx";

        Path path = Paths.get(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        if (new File(filePath).exists()) {
            return new ResponseEntity<>(new InputStreamResource(Files.newInputStream(path)), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<HashMap<String, Object>>(HttpStatus.PROCESSING);
        }
    }

    @ApiOperation(value = "사용자 에보지역 코드 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/region/get", method = RequestMethod.GET)
    public ResponseEntity<Object> insertRawData(HttpServletRequest req) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String lon = req.getParameter("lon");
        String lat = req.getParameter("lat");

        result.put("data", service.getRegionInfo(lon, lat));
        result.put("resultCode", 1);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
