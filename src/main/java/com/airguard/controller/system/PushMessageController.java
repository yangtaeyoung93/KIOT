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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

import com.airguard.model.system.PushMessage;
import com.airguard.service.system.PushMessageService;

@Controller
@RequestMapping("/system/push/message")
public class PushMessageController {

  private static final String SUPER_ITEM = "시스템관리";

  @Autowired
  private PushMessageService service;

  @ApiOperation(value = "시스템 관리 => 알림 메시지 양식 카테고리 (목록)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/page", method = RequestMethod.GET)
  public String pagePushMessage(Model model) throws Exception {
    model.addAttribute("superItem", SUPER_ITEM);
    model.addAttribute("item", "푸시알람관리");
    model.addAttribute("oneDepth", "air365");
    model.addAttribute("twoDepth", "pushMessage");
    return "/system/pushMessageList";
  }

  @ApiOperation(value = "알림 메시지 양식 목록 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> selectPushMessageList() throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    List<PushMessage> data = service.selectPushMessageList();
    result.put("data", data);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "알림 메시지 양식 조회 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> pushMessageSearch(HttpServletRequest req) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();

    String idx = req.getParameter("idx");
    if (idx != null) {
      PushMessage data = service.selectPushMessageSearch(idx);
      result.put("data", data);
      result.put("resultCode", 1L);

    } else {
      result.put("resultCode", 0);

    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "알림 메시지 양식 등록 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> pushMessageSave(@RequestBody PushMessage req) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    int resultCode = service.pushMessageSave(req);
    result.put("resultCode", resultCode);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "알림 메시지 양식 수정 API", tags = "시스템 관리 API")
  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Map<String, Object>> pushMessageUpdate(@RequestBody PushMessage req) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    int resultCode = service.pushMessageUpdate(req);
    result.put("resultCode", resultCode);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
