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
import com.airguard.model.system.Device;
import com.airguard.service.system.DeviceModelService;
import com.airguard.service.system.DeviceService;
import com.airguard.util.CommonConstant;

@Controller
@RequestMapping(CommonConstant.URL_SYSTEM_DEVICE)
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    DeviceService service;

    @Autowired
    DeviceModelService mService;

    private static final String SUPER_ITEM = "시스템관리";

    @ApiOperation(value = "시스템 관리 => 장비 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list/{type}", method = RequestMethod.GET)
    public String devicePageList(@PathVariable("type") String type, Model model) throws Exception {
        type = type.toUpperCase();
        model.addAttribute("selectDeviecType", type);

        model.addAttribute("modelList", mService.selectTypeNameDeviceModel(type));
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", type + " 장비");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "device");
        model.addAttribute("threeDepth", type);

        return "/system/deviceList";
    }

    @ApiOperation(value = "시스템 관리 => 테스트 장비 관리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list/{type}/test", method = RequestMethod.GET)
    public String testDevicePageList(@PathVariable("type") String type, Model model) throws Exception {
        type = type.toUpperCase();
        model.addAttribute("selectDeviecType", type);

        model.addAttribute("superItem", "테스트 장비 관리");
        model.addAttribute("item", type.toUpperCase());
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "test");
        model.addAttribute("threeDepth", type);

        return "/system/" + type.toLowerCase() + "TestDeviceList";
    }

    @ApiOperation(value = "시스템 관리 => 테스트 장비 IAQ 팝업", tags = "웹 페이지 Url")
    @RequestMapping(value = "/iaq_popup", method = RequestMethod.POST)
    public String iaqDetailPopup() throws Exception {

        return "/system/iaq_popup";
    }

    @ApiOperation(value = "시스템 관리 => 테스트 장비 OAQ 팝업", tags = "웹 페이지 Url")
    @RequestMapping(value = "/oaq_popup", method = RequestMethod.POST)
    public String oaqDetailPopup() throws Exception {

        return "/system/oaq_popup";
    }

    @ApiOperation(value = "시스템 관리 => 테스트 장비 DOT 팝업", tags = "웹 페이지 Url")
    @RequestMapping(value = "/dot_popup", method = RequestMethod.POST)
    public String dotDetailPopup() throws Exception {

        return "/system/dot_popup";
    }

    @ApiOperation(value = "시스템 관리 => 장비 관리 (등록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail/{type}", method = RequestMethod.GET)
    public String devicePageInsert(@PathVariable("type") String type, Model model) throws Exception {

        type = type.toUpperCase();
        model.addAttribute("deviceType", type);
        model.addAttribute("modelList", mService.selectTypeNameDeviceModel(type));
        model.addAttribute("btn", "id='insertBtn' value='등 록'");
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", type + " 장비");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "device");
        model.addAttribute("threeDepth", type);

        return "/system/deviceDetail";
    }

    @ApiOperation(value = "시스템 관리 => 장비 관리 (상세)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/detail/{type}/{idx}", method = RequestMethod.GET)
    public String devicePageDetail(@PathVariable("type") String type, @PathVariable("idx") String idx, Model model) throws Exception {

        type = type.toUpperCase();
        model.addAttribute("deviceData", service.selectDeviceOne(idx));
        model.addAttribute("deviceType", type);
        model.addAttribute("idx", idx);
        model.addAttribute("modelList", mService.selectTypeNameDeviceModel(type));
        model.addAttribute("btn", "id='updateBtn' value='수 정'");
        model.addAttribute("superItem", SUPER_ITEM + "/" + type);
        model.addAttribute("item", type + " 장비");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "device");
        model.addAttribute("threeDepth", type);

        return "/system/deviceDetail";
    }

    @ApiOperation(value = "장비 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get/list", method = RequestMethod.GET)
    public ResponseEntity<Object> deviceListGet()
            throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("data", service.selectDataDownloadList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> deviceGet(HttpServletRequest request) throws Exception {

        String reqUseYn = request.getParameter("useYn");
        String reqTestYn = request.getParameter("testYn");
        String reqDeviceType = request.getParameter("deviceType");
        String reqSerialNum = request.getParameter("serialNum");
        String reqSearchType = request.getParameter("searchType");
        String reqSearchValue2 = request.getParameter("searchValue2");
        String reqSearchValue3 = request.getParameter("searchValue3");

        Search search = new Search();

        search.setSearchUseYn(reqUseYn);
        search.setSearchTestYn(reqTestYn);
        search.setSearchValue(reqDeviceType);
        search.setSearchValue2(reqSerialNum);
        search.setSearchType(reqSearchType);
        search.setSearchValue2(reqSearchValue2);
        search.setSearchValue3(reqSearchValue3);

        List<Device> equiCatList = service.selectDeviceList(search);

        Map<String, Object> result = new HashMap<>();
        result.put("data", equiCatList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Device> devicensert(@RequestBody Device reqBody) throws Exception {
        service.insertDevice(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Device> deviceUpdate(@RequestBody Device reqBody) throws Exception {
        service.updateDevice(reqBody);
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 삭제 API", tags = "시스템 관리 API")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Device> deviceDelete(@RequestBody Device reqBody) throws Exception {
        int index = 0;
        for (String s : reqBody.getChArr()) {
            reqBody.setResultCode(service.deleteDevice(s));
            if (reqBody.getResultCode() != 1) {
                reqBody.setCheckIdx(s);
                reqBody.setCheckName(reqBody.getNameArr().get(index));
                break;
            }
            index++;
        }

        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 일괄 등록 API (with, Excel File)", tags = "시스템 관리 API")
    @RequestMapping(value = "/excelInsert", method = RequestMethod.POST)
    public ResponseEntity<String> deviceExcelInsert(MultipartHttpServletRequest req)
            throws Exception {
        String result;
        String excelType = req.getParameter("excelType");

        if (excelType.equals("xlsx")) {
            result = service.xlsxDeviceExcelReader(req);
        } else {
            return new ResponseEntity<>("EXT_CHECK", HttpStatus.OK);

        }
        logger.info("sames serial : " + result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 일괄 등록 양식 다운로드 API (With, Excel File)", tags = "시스템 관리 API")
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public ResponseEntity<?> excelDownload() throws Exception {
        String filePath = "/NAS2_NFS/IOT_KITECH/DOC_TEMPLATE/KIOT_device_template_0_2.xlsx";
        String fileName = "KIOT_device_template_0_2.xlsx";

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

    @ApiOperation(value = "OAQ 장비 일괄 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/oaq", method = RequestMethod.GET)
    public ResponseEntity<?> oaqList() throws Exception {

        List<Device> list = service.selectOaqList();

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
