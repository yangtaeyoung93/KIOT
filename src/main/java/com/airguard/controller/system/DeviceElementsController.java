
package com.airguard.controller.system;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airguard.util.ExcelCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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

import com.airguard.model.system.DeviceElements;
import com.airguard.service.system.DeviceElementsService;
import com.airguard.util.CommonConstant;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping(CommonConstant.URL_SYSTEM_DEVICE_ELEMENTS)
public class DeviceElementsController {

    @Autowired
    DeviceElementsService service;

    private static final String SUPER_ITEM = "시스템관리";
    private static final String[] CSVHEADER = {"측정 요소명","측정 요소코드","단위","변환식","표출명","유효자릿수","데이터 허용범위","데이터 전처리"};

    @ApiOperation(value = "시스템 관리 => 장비 측정요소 카테고리 (목록)", tags = "웹 페이지 Url")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String deviceElementsList(Model model) throws Exception {

        List<DeviceElements> deviceElementsList = service.selectDeviceElementsAll();

        model.addAttribute("list", deviceElementsList);
        model.addAttribute("superItem", SUPER_ITEM);
        model.addAttribute("item", "장비 측정요소 카테고리");
        model.addAttribute("oneDepth", "system");
        model.addAttribute("twoDepth", "elements");

        return "/system/deviceElementsList";
    }

    @ApiOperation(value = "장비 측정요소 목록 조회 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<Object> deviceElementsGet() throws Exception {
        List<DeviceElements> deviceElementsList = service.selectDeviceElementsAll();
        Map<String, Object> result = new HashMap<>();
        result.put("data", deviceElementsList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 측정요소 등록 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<DeviceElements> deviceElementsInsert(@RequestBody DeviceElements reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        service.insertDeviceElements(reqBody);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @ApiOperation(value = "장비 측정요소 수정 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<DeviceElements> deviceElementsUpdate(@RequestBody DeviceElements reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        service.updateDeviceElements(reqBody);

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @ApiOperation(value = "장비 측정요소 삭제 API", tags = "시스템 관리 API")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<DeviceElements> deviceElementsDelete(@RequestBody DeviceElements reqBody) throws Exception {
        reqBody.setRestApiMessage(CommonConstant.SUCCESS);
        for (String s : reqBody.getChArr()) {
            service.deleteDeviceElements(s);
        }

        return new ResponseEntity<>(reqBody, HttpStatus.OK);
    }


    private static final char UTF_8_WITHOUT_BOM = '\ufeff';
    @ApiOperation(value = "장비 측정요소 엑셀 다운로드", tags = "시스템 관리 API")
    @RequestMapping(value = "/down", method = RequestMethod.GET)
    public void deviceElementsDown (HttpServletResponse res) throws Exception {
        List<DeviceElements> deviceElementsList = service.selectDeviceElementsAll();
        ByteArrayInputStream byteArrayOutputStream = null;

        String fileName = "측정요소 다운로드";
        SXSSFWorkbook workbook = new SXSSFWorkbook(Integer.MAX_VALUE);
        workbook.setCompressTempFiles(true);
        ExcelCommonUtil.sheetMaker(workbook,deviceElementsList,CSVHEADER);

        res.setContentType("application/vnd.ms-excel");
        res.setHeader("Set-Cookie", "fileDownload=true; path=/");
        res.setHeader("Content-Disposition",
                "attachment;fileName="+new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)+".xlsx");



        try {
            OutputStream out = new BufferedOutputStream(res.getOutputStream());
            workbook.write(out);
            out.flush();
            out.close();
            log.info("SUCCESS");

        } catch (Exception e) {
            log.error("ERROR?");
            res.setHeader("Set-Cookie", "fileDownload=false; path=/");
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            res.setHeader("Content-Type", "text/html; charset=utf-8");
            OutputStream out = null;

            try {

                out = res.getOutputStream();
                byte[] data = "fail..".getBytes();
                out.write(data, 0, data.length);

            } catch (Exception e2) {
                e2.printStackTrace();

            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception ignore) {
                        log.error("ERROR EXCEL DOWNLOAD");
                    }
                }
            }
        } finally {
            workbook.dispose();
            workbook.close();
        }
    }
}
