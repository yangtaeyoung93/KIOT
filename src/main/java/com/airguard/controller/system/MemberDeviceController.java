package com.airguard.controller.system;

import com.airguard.model.common.Search;
import com.airguard.model.system.FileVo;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Space;
import com.airguard.model.system.Vent;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.DeviceService;
import com.airguard.service.system.GroupService;
import com.airguard.service.system.MemberDeviceService;
import com.airguard.service.system.MemberService;
import com.airguard.util.CommonConstant;
import com.airguard.util.FileProcessUtil;
import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_MEMBER_DEVICE)
public class MemberDeviceController {

  @Value("${file-folder.path}")
  private String FILE_PATH;

  private static final Logger logger = LoggerFactory.getLogger(MemberDeviceController.class);

  @Autowired
  private MemberDeviceService service;

  @Autowired
  private GroupService groupService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private PlatformService platformService;

  @Autowired
  private DeviceService deviceService;

  @Autowired
  private FileProcessUtil fileProcessUtil;

  private static final String SUPER_ITEM = "시스템관리";
  private static final String SUCCESS = "SUCCESS";

  @ApiOperation(value = "시스템 관리 => 사용자 장비 관리 (목록)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String memberPageList(Model model) throws Exception {
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "사용자 장비 관리");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "member");
    model.addAttribute("threeDepth", "device");
    return "/system/memberDeviceList";
  }

  @ApiOperation(value = "시스템 관리 => 사용자 장비 관리 (등록)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/insert", method = RequestMethod.GET)
  public String memberPageInsert(Model model) throws Exception {
    Search search = new Search();
    search.setSearchUseYn("Y");
    search.setSearchValue("");

    model.addAttribute("memberDataList", memberService.selectMemberList(search));

    model.addAttribute("btn", "id='insertBtn' value='등 록'");
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "사용자 장비 등록");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "member");
    model.addAttribute("threeDepth", "device");
    return "/system/memberDeviceInsert";
  }

  @ApiOperation(value = "시스템 관리 => 사용자 장비 관리 (상세)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/detail/{idx}", method = RequestMethod.GET)
  public String memberPageDetail(@PathVariable("idx") String idx, HttpServletRequest request,
      Model model) throws Exception {
    Search search = new Search();
    search.setSearchUseYn("Y");
    search.setSearchValue("");

    model.addAttribute("memberDataList", memberService.selectMemberList(search));
    model.addAttribute("memberDeviceDataList", service.selectMemberDeviceOne(idx));
    model.addAttribute("memberDeviceDataCnt", service.selectMemberDeviceCnt(idx));

    model.addAttribute("popupDeviceIdx",
        request.getParameter("deviceIdx") == null ? 0 : request.getParameter("deviceIdx"));
    model.addAttribute("idx", idx);
    model.addAttribute("btn", "id='updateBtn' value='수 정'");
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "사용자 장비 상세");
    model.addAttribute("oneDepth", "system");
    model.addAttribute("twoDepth", "member");
    model.addAttribute("threeDepth", "device");
    return "/system/memberDeviceDetail";
  }

  @ApiOperation(value = "사용자 장비 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/get/{idx}", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> memberDeviceGetOne(@PathVariable("idx") String idx,
      HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();
    List<MemberDevice> deviceData = new ArrayList<>();
    List<Vent> ventDeviceData;

    String type = request.getParameter("type") == null ? "member" : request.getParameter("type");

    logger.error("/system/member/device/get API REQUEST :: idx => {}, type => {} ", idx, type);

    if (type.equals("member")) {
      deviceData = service.selectMemberDeviceOne(idx);
      ventDeviceData = service.selectVentOne(deviceData);
      result.put("deviceData", deviceData);
      result.put("ventDeviceData", ventDeviceData);
    } else if (type.equals("group")) {
      List<Map<String, Object>> memberDatas = groupService.selectGroupMembers(idx);
      for (Map<String, Object> member : memberDatas) {
        deviceData.addAll(service.selectMemberDeviceOne((member.get("memberIdx")).toString()));
      }
      ventDeviceData = service.selectVentOne(deviceData);
      result.put("deviceData", deviceData);
      result.put("ventDeviceData", ventDeviceData);
    }

    logger.error("/system/member/device/get API RESPONSE :: {}", result);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 연동 장비(측정기) 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/memberDevice", method = RequestMethod.GET)
  public ResponseEntity<Object> deviceMemberAjax(HttpServletRequest request) throws Exception {

    Map<String, Object> result = new HashMap<>();
    result.put("data", service.selectMemberDeviceOne(request.getParameter("idx")));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 연동 장비(환기청정기) 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/ventDevice", method = RequestMethod.GET)
  public ResponseEntity<Object> deviceVentAjax(HttpServletRequest request) throws Exception {

    Map<String, Object> result = new HashMap<>();
    result.put("data", service.selectMemberDeviceVentOne(request.getParameter("idx")));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public ResponseEntity<Object> memberGet(HttpServletRequest request) throws Exception {
    String reqSearchType = request.getParameter("searchType");
    String reqSearchValue = request.getParameter("searchValue");

    Search search = new Search();

    search.setSearchValue(reqSearchValue);
    search.setSearchType(reqSearchType);

    List<MemberDevice> equiCatList = service.selectMemberDeviceList(search);

    Map<String, Object> result = new HashMap<>();
    result.put("data", equiCatList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 등록 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/post", method = RequestMethod.POST)
  public ResponseEntity<MemberDevice> memberInsert(HttpServletRequest req,
      @RequestBody MemberDevice reqBody) throws Exception {

    service.insertMemberDevice(reqBody);

    platformService.publisherPlatform(platformService.idxToUserId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());

    reqBody.setRestApiMessage(SUCCESS);
    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 수정 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/put", method = RequestMethod.PUT)
  public ResponseEntity<MemberDevice> memberUpdate(HttpServletRequest req,
      @RequestBody MemberDevice reqBody) throws Exception {

    service.updateMemberDevice(reqBody);

    platformService.postPlatformRequestConnect(
        deviceService.selectDeviceOne(reqBody.getDeviceIdx()).getSerialNum(), req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());

    reqBody.setRestApiMessage(SUCCESS);
    return new ResponseEntity<>(reqBody, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 삭제 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
  public ResponseEntity<?> deviceDelete(HttpServletRequest req, @RequestBody MemberDevice reqBody)
      throws Exception {

    service.deleteMemberDevice(reqBody);

    for (String type : new String[]{"report", "Main", "East", "West", "South", "North"}) {
      FileVo oldFileInfo = service.selectFileInfo(reqBody.getDeviceIdx(), type);
      if (!oldFileInfo.getServerFilePath().isEmpty()) {
        FileProcessUtil.fileDelete(oldFileInfo.getServerFilePath()
            .concat(oldFileInfo.getServerFileName()).concat(".").concat(oldFileInfo.getFileExt()));
        service.deleteFile(oldFileInfo.getIdx());
      }
    }

    platformService.publisherPlatform(platformService.idxToUserId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(reqBody.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // =======================================================================================================
  // New) 2020. 04. 09 * Dong Gi, Kim * -- 환기청정기 관리 기능(API) 추가
  // =======================================================================================================
  @ApiOperation(value = "사용자 장비, 등록 API - 환기청정기", tags = "시스템 관리 API")
  @RequestMapping(value = "/insert/vent", method = RequestMethod.POST)
  public ResponseEntity<?> deviceVentInsert(HttpServletRequest req, @RequestBody Vent vent)
      throws Exception {
    service.insertMemberDeviceVent(vent);
    String iaqSerial = service.selectVentSerialIdx(vent.getIaqDeviceIdx());
    platformService.postPlatformRequestConnect(iaqSerial,
        req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 수정 API - 환기청정기", tags = "시스템 관리 API")
  @RequestMapping(value = "/update/vent", method = RequestMethod.PUT)
  public ResponseEntity<?> deviceVentUpdate(HttpServletRequest req, @RequestBody Vent vent)
      throws Exception {
    service.updateMemberDeviceVent(vent);
    String iaqSerial = service.selectVentSerialIdx(vent.getIaqDeviceIdx());
    platformService.postPlatformRequestConnect(iaqSerial,
        req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 전체 삭제 API - 환기청정기", tags = "시스템 관리 API")
  @RequestMapping(value = "/delete/vent/all", method = RequestMethod.DELETE)
  public ResponseEntity<?> deviceVentDeleteAll(HttpServletRequest req, @RequestBody Vent vent)
      throws Exception {
    service.deleteMemberDeviceVentAll(vent);
    String iaqSerial = service.selectVentSerialIdx(vent.getIaqDeviceIdx());
    platformService.postPlatformRequestConnect(iaqSerial,
        req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 삭제 API - 환기청정기", tags = "시스템 관리 API")
  @RequestMapping(value = "/delete/vent", method = RequestMethod.DELETE)
  public ResponseEntity<?> deviceVentDelete(HttpServletRequest req, @RequestBody Vent vent)
      throws Exception {
    service.deleteMemberDeviceVent(vent.getVentDeviceIdx());
    String iaqSerial = service.selectVentSerialIdx(vent.getIaqDeviceIdx());
    platformService.postPlatformRequestConnect(iaqSerial,
        req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, true, req.getLocalName());
    platformService.publisherPlatform(platformService.idxToUserId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_USER, false, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, true, req.getLocalName());
    platformService.publisherPlatform(platformService.memberIdxToGroupId(vent.getMemberIdx()),
        CommonConstant.PUBLISHER_GROUP, false, req.getLocalName());
    return new ResponseEntity<>(HttpStatus.OK);
  }


  // =======================================================================================================
  // New) 2020. 03. 24 * Dong Gi, Kim *
  // =======================================================================================================

  @ApiOperation(value = "사용자 장비, 장비 유형 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/deviceType", method = RequestMethod.GET)
  public ResponseEntity<Object> deviceTypeList() throws Exception {
    List<MemberDevice> deviceTypeList = service.selectDeviceTypeList();
    Map<String, Object> result = new HashMap<>();
    result.put("data", deviceTypeList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 장비 모델 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/deviceModel", method = RequestMethod.GET)
  public ResponseEntity<Object> deviceModelList(HttpServletRequest request) throws Exception {
    String reqDeviceTypeIdx = request.getParameter("deviceTypeIdx");
    List<MemberDevice> deviceModelList = service.selectDeviceModelList(reqDeviceTypeIdx);
    Map<String, Object> result = new HashMap<>();
    result.put("data", deviceModelList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 장비 (측정기) 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/deviceSerial", method = RequestMethod.GET)
  public ResponseEntity<Object> deviceSerialList(HttpServletRequest request) throws Exception {
    String reqDeviceModelIdx = request.getParameter("deviceModelIdx");
    List<MemberDevice> deviceSerialList = service.selectDeviceSerialList(reqDeviceModelIdx);
    Map<String, Object> result = new HashMap<>();
    result.put("data", deviceSerialList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 장비 (환기청정기) 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/deviceVent", method = RequestMethod.GET)
  public ResponseEntity<Object> deviceVentList() throws Exception {
    List<MemberDevice> deviceVentList = service.selectDeviceVentList();
    Map<String, Object> result = new HashMap<>();
    result.put("data", deviceVentList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 설치 장소 카테고리(상위) 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/parent/spaces", method = RequestMethod.GET)
  public ResponseEntity<Object> parentSpaceList(HttpServletRequest request) throws Exception {
    String reqDeviceTypeIdx = request.getParameter("deviceTypeIdx");
    List<Space> parentSpaceList = service.selectSpaceList(reqDeviceTypeIdx);
    Map<String, Object> result = new HashMap<>();
    result.put("data", parentSpaceList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 설치 장소 카테고리(하위) 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/spaces", method = RequestMethod.GET)
  public ResponseEntity<Object> spaceList(HttpServletRequest request) throws Exception {
    String reqParentSpaceIdx = request.getParameter("parentSpaceIdx");
    List<Space> spaceList = service.selectParantSpaceList(reqParentSpaceIdx);
    Map<String, Object> result = new HashMap<>();
    result.put("data", spaceList);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 첨부 파일 등록 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/fileUpload/{memberIdx}/{deviceIdx}", method = RequestMethod.POST)
  public ResponseEntity<Object> fileUpload(MultipartHttpServletRequest req,
      @PathVariable("memberIdx") String memberIdx, @PathVariable("deviceIdx") String deviceIdx)
      throws Exception {
    Map<String, Object> result = new HashMap<>();
    String fileType = req.getParameter("type") == null ? "report" : req.getParameter("type");
    String target = req.getParameter("target") == null ? "targetFile" : req.getParameter("target");
    MultipartFile file = req.getFile(target);

    if (!memberIdx.matches("[+-]?\\d*(\\.\\d+)?") || !deviceIdx.matches("[+-]?\\d*(\\.\\d+)?")) {
      result.put("resultCode", CommonConstant.R_FAIL_CODE);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    assert file != null;
    if (!file.isEmpty()) {
      String clientFileName = file.getOriginalFilename();
      assert clientFileName != null;

      String serverFilePath =
          FILE_PATH.concat("AIR_MAP_IMAGE").concat("/").concat(memberIdx).concat("/");
      if (!"report".equals(fileType)) {
        serverFilePath = serverFilePath.concat(fileType).concat("/");
      }
      String serverFileName = deviceIdx.concat("_").concat(encFileName(clientFileName));
      String fileExt = clientFileName.substring(clientFileName.lastIndexOf(".") + 1);

      if (FileProcessUtil.fileSave(file, serverFilePath,
          serverFilePath.concat(serverFileName).concat(".").concat(fileExt))) {
        FileVo vo = new FileVo();
        vo.setDeviceIdx(deviceIdx);
        vo.setClientFileName(clientFileName);
        vo.setServerFileName(serverFileName);
        vo.setServerFilePath(serverFilePath);
        vo.setFileExt(fileExt);
        vo.setFileType(fileType);

        service.fileUpload(vo);
      }

      result.put("resultCode", CommonConstant.R_SUCC_CODE);

    } else {
      result.put("resultCode", CommonConstant.R_FAIL_CODE);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 첨부 파일 변경 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/fileUpdate/{memberIdx}/{deviceIdx}", method = RequestMethod.POST)
  public ResponseEntity<?> fileUpdate(MultipartHttpServletRequest req,
      @PathVariable("memberIdx") String memberIdx, @PathVariable("deviceIdx") String deviceIdx)
      throws Exception {
    Map<String, Object> result = new HashMap<>();
    String fileType = req.getParameter("type") == null ? "report" : req.getParameter("type");
    String target = req.getParameter("target") == null ? "targetFile" : req.getParameter("target");

    if (!memberIdx.matches("[+-]?\\d*(\\.\\d+)?") || !deviceIdx.matches("[+-]?\\d*(\\.\\d+)?")) {
      result.put("resultCode", CommonConstant.R_FAIL_CODE);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    FileVo oldFileInfo = service.selectFileInfo(deviceIdx, fileType);
    if (FileProcessUtil.fileDelete(oldFileInfo.getServerFilePath()
        .concat(oldFileInfo.getServerFileName()).concat(".").concat(oldFileInfo.getFileExt()))) {
      MultipartFile file = req.getFile(target);

      assert file != null;
      if (!file.isEmpty()) {
        String clientFileName = file.getOriginalFilename();
        assert clientFileName != null;

        String serverFilePath =
            FILE_PATH.concat("AIR_MAP_IMAGE").concat("/").concat(memberIdx).concat("/");
        if (!"report".equals(fileType)) {
          serverFilePath = serverFilePath.concat(fileType).concat("/");
        }
        String serverFileName = deviceIdx.concat("_").concat(encFileName(clientFileName));
        String fileExt = clientFileName.substring(clientFileName.lastIndexOf(".") + 1);

        if (FileProcessUtil.fileSave(file, serverFilePath,
            serverFilePath.concat(serverFileName).concat(".").concat(fileExt))) {
          FileVo newFileInfo = new FileVo();
          newFileInfo.setIdx(oldFileInfo.getIdx());
          newFileInfo.setDeviceIdx(deviceIdx);
          newFileInfo.setClientFileName(clientFileName);
          newFileInfo.setServerFileName(serverFileName);
          newFileInfo.setServerFilePath(serverFilePath);
          newFileInfo.setFileExt(fileExt);
          newFileInfo.setFileType(fileType);
          service.updateFile(newFileInfo);
        }

        result.put("resultCode", CommonConstant.R_SUCC_CODE);

      } else {
        result.put("resultCode", CommonConstant.R_FAIL_CODE);
      }

      result.put("resultCode", CommonConstant.R_SUCC_CODE);

    } else {
      result.put("resultCode", CommonConstant.R_FAIL_CODE);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 첨부 파일 다운로드 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/fileDownload/{deviceIdx}", method = RequestMethod.GET)
  public ResponseEntity<?> fileDownload(HttpServletRequest req,
      @PathVariable("deviceIdx") String deviceIdx) throws Exception {

    String fileType = req.getParameter("type") == null ? "report" : req.getParameter("type");

    FileVo fileInfo = service.selectFileInfo(deviceIdx, fileType);
    String fileClientName = fileInfo.getClientFileName();
    String filePath = fileInfo.getServerFilePath().concat(fileInfo.getServerFileName()).concat(".")
        .concat(fileInfo.getFileExt());
    String fileName = fileInfo.getSerialNum();

    switch (fileType.toUpperCase()) {
      case "REPORT":
        fileName = URLEncoder.encode(fileClientName, "UTF-8");
        break;
      case "MAIN":
        fileName = fileName.concat("_main.").concat(fileInfo.getFileExt());
        break;
      case "EAST":
        fileName = fileName.concat("_sub_e.").concat(fileInfo.getFileExt());
        break;
      case "WEST":
        fileName = fileName.concat("_sub_w.").concat(fileInfo.getFileExt());
        break;
      case "SOUTH":
        fileName = fileName.concat("_sub_s.").concat(fileInfo.getFileExt());
        break;
      case "NORTH":
        fileName = fileName.concat("_sub_n.").concat(fileInfo.getFileExt());
        break;
      default:
        fileName = URLEncoder.encode(fileClientName, "UTF-8");
        break;
    }

    Path path = Paths.get(filePath);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path));
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(fileName));

    if (new File(filePath).exists()) {
      return new ResponseEntity<>(new InputStreamResource(Files.newInputStream(path)), headers,
          HttpStatus.OK);
    } else {
      return new ResponseEntity<HashMap<String, Object>>(HttpStatus.PROCESSING);
    }
  }

  @ApiOperation(value = "사용자 장비, 첨부 파일 삭제 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/ajax/fileDelete/{deviceIdx}", method = RequestMethod.POST)
  public ResponseEntity<Integer> fileDelete(HttpServletRequest req,
      @PathVariable("deviceIdx") String deviceIdx) throws Exception {
    int result = 0;

    String fileType = req.getParameter("type") == null ? "report" : req.getParameter("type");

    FileVo oldFileInfo = service.selectFileInfo(deviceIdx, fileType);
    if (!oldFileInfo.getServerFilePath().isEmpty()) {
      FileProcessUtil.fileDelete(oldFileInfo.getServerFilePath()
          .concat(oldFileInfo.getServerFileName()).concat(".").concat(oldFileInfo.getFileExt()));
      service.deleteFile(oldFileInfo.getIdx());
      result = 1;
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  public String encFileName(String fileName) {
    int asciCode = 0;
    for (char c : fileName.toCharArray()) {
      asciCode += (byte) c;
    }
    return (System.currentTimeMillis() / 1000) + String.format("%02X", (asciCode % 256));
  }

  @ApiOperation(value = "사용자 장비, 일괄 등록 API (With, Excel File)", tags = "시스템 관리 API")
  @RequestMapping(value = "/excelInsert", method = RequestMethod.POST)
  public ResponseEntity<String> deviceExcelInsert(MultipartHttpServletRequest req)
      throws Exception {
    String result;
    String excelType = req.getParameter("excelType");

    if (excelType.equals("xlsx")) {
      result = service.xlsxMemberDeviceExcelReader(req);
    } else {
      return new ResponseEntity<>("EXT_CHECK", HttpStatus.OK);
    }
    logger.error(result);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 일괄 수정 API (With, Excel File)", tags = "시스템 관리 API")
  @RequestMapping(value = "/excelUpdate", method = RequestMethod.GET)
  public ResponseEntity<String> deviceExcelUpdate() throws Exception {
    String result = "";

    service.xlsxMemberDeviceExcelReaderUpdate();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "사용자 장비, 일괄 등록 양식 다운로드 API (With, Excel File)", tags = "시스템 관리 API")
  @RequestMapping(value = "/excel", method = RequestMethod.GET)
  public ResponseEntity<?> excelDownload() throws Exception {
    String filePath = "/NAS2_NFS/IOT_KITECH/DOC_TEMPLATE/KIOT_member_device_template.xlsx";
    String fileName = "KIOT_member_device_template.xlsx";

    Path path = Paths.get(filePath);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path));
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

    if (new File(filePath).exists()) {
      return new ResponseEntity<>(new InputStreamResource(Files.newInputStream(path)), headers,
          HttpStatus.OK);
    } else {
      return new ResponseEntity<HashMap<String, Object>>(HttpStatus.PROCESSING);
    }
  }

  @ApiOperation(value = "사용자 장비, 파일 일괄 업로드 API (임시) (With, Excel File)", tags = "시스템 관리 API")
  @RequestMapping(value = "/fileAllUpload", method = RequestMethod.GET)
  public ResponseEntity<?> fileAllUpload() throws Exception {
    String result = CommonConstant.SUCCESS;
    String serial = "";

    for (FileVo vo : fileProcessUtil.findAllFilesInFolder()) {

      try {

        serial = vo.getSerialNum();
        service.fileUpload(vo);

      } catch (DuplicateKeyException e) {
        logger.error("DuplicateKey :: serial :: {}", serial);

      } catch (Exception e) {
        result = "FAIL";
      }
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
