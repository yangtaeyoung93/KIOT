package com.airguard.controller.custom;

import com.airguard.exception.ParameterException;
import com.airguard.model.custom.DataVo;
import com.airguard.model.custom.ResultVo;
import com.airguard.model.custom.suncheon.dto.DeviceDto;
import com.airguard.model.custom.suncheon.dto.UserDto;
import com.airguard.service.custom.SuncheonService;
import com.airguard.util.CommonConstant;
import com.airguard.util.FileProcessUtil;
import com.airguard.util.Sha256EncryptUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping(value = "/api/custom/suncheon", produces = "application/json")
public class SuncheonController {

  Logger logger = LoggerFactory.getLogger(SuncheonController.class);
  ResultVo resultVo = new ResultVo();
  DataVo dataVo = new DataVo();
  UserDto userDto = new UserDto();
  DeviceDto deviceDto = new DeviceDto();

  @Autowired
  private SuncheonService service;

  @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
  public DataVo suncheonLogin(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String userPw = request.getParameter("userPw") == null ? "" : request.getParameter("userPw");

    if ("".equals(userId) || "".equals(userPw)) {
      throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);
    } else {
      userDto.setUserId(userId);
      userDto.setUserPw(userPw);
      dataVo.setResult(CommonConstant.R_SUCC_CODE);
      dataVo.setData(service.login(response, userDto));
    }

    return dataVo;
  }

//  @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
//  public ResultVo suncheonLogout(HttpServletResponse response) throws Exception {
//
//    resultVo.setResult(service.logout(response));
//
//    return resultVo;
//  }

  @RequestMapping(value = "/userAll", method = {RequestMethod.GET})
  public DataVo suncheonGetUserAll() throws Exception {

    dataVo.setData(service.getUserAll());
    dataVo.setResult(CommonConstant.R_SUCC_CODE);

    return dataVo;
  }

  @RequestMapping(value = "/user", method = {RequestMethod.GET})
  public DataVo suncheonGetUser(String userId) throws Exception {

    dataVo.setData(service.getUser(userId));
    dataVo.setResult(CommonConstant.R_SUCC_CODE);

    return dataVo;
  }

  @RequestMapping(value = "/user", method = {RequestMethod.POST})
  public ResultVo suncheonInsertUser(HttpServletRequest request) throws Exception {

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String userPw = request.getParameter("userPw") == null ? "" : request.getParameter("userPw");
    String department =
        request.getParameter("department") == null ? "" : request.getParameter("department");
    String userName =
        request.getParameter("userName") == null ? "" : request.getParameter("userName");
    String userEmail =
        request.getParameter("userEmail") == null ? "" : request.getParameter("userEmail");
    String phoneNumber =
        request.getParameter("phoneNumber") == null ? "" : request.getParameter("phoneNumber");
    String userClass =
        request.getParameter("userClass") == null ? "" : request.getParameter("userClass");
    String remark = request.getParameter("remark") == null ? "" : request.getParameter("remark");

    if ("".equals(userId) || "".equals(userPw) || "".equals(department) || "".equals(userName) || ""
        .equals(userEmail) || "".equals(phoneNumber) || "".equals(userClass)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      userDto.setUserId(userId);
      userDto.setUserPw(userPw);
      userDto.setDepartment(department);
      userDto.setUserName(userName);
      userDto.setUserEmail(userEmail);
      userDto.setPhoneNumber(phoneNumber);
      userDto.setUserClass(userClass);
      userDto.setRemark(remark);
      resultVo.setResult(service.join(userDto));
    }

    return resultVo;
  }


  @RequestMapping(value = "/user", method = {RequestMethod.PUT})
  public ResultVo suncheonUpdateUser(HttpServletRequest request) throws Exception {

    int idx =
        request.getParameter("idx") == null ? 0 : Integer.parseInt(request.getParameter("idx"));
    String userId = request.getParameter("userId") == null ? null : request.getParameter("userId");
    String userPw = request.getParameter("userPw") == null ? null
        : Sha256EncryptUtil.ShaEncoder(request.getParameter("userPw"));
    String department =
        request.getParameter("department") == null ? null : request.getParameter("department");
    String userName =
        request.getParameter("userName") == null ? null : request.getParameter("userName");
    String userEmail =
        request.getParameter("userEmail") == null ? null : request.getParameter("userEmail");
    String phoneNumber =
        request.getParameter("phoneNumber") == null ? null : request.getParameter("phoneNumber");
    String userClass =
        request.getParameter("userClass") == null ? null : request.getParameter("userClass");
    String remark = request.getParameter("remark") == null ? null : request.getParameter("remark");

    if (idx == 0 || "".equals(userId) || "".equals(department) || "".equals(userName) ||
        "".equals(userEmail) || "".equals(phoneNumber) || "".equals(userClass)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      userDto.setIdx(idx);
      userDto.setUserId(userId);
      userDto.setUserPw(userPw);
      userDto.setDepartment(department);
      userDto.setUserName(userName);
      userDto.setUserEmail(userEmail);
      userDto.setPhoneNumber(phoneNumber);
      userDto.setUserClass(userClass);
      userDto.setRemark(remark);
      resultVo.setResult(service.updateUser(userDto));
    }

    return resultVo;
  }

  @RequestMapping(value = "/user", method = {RequestMethod.DELETE})
  public ResultVo suncheonDeleteUser(HttpServletRequest request) throws Exception {

    String idx = request.getParameter("idx") == null ? "" : request.getParameter("idx");

    if ("".equals(idx)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      resultVo.setResult(service.deleteUserByIdx(Integer.parseInt(idx)));
    }

    return resultVo;
  }

  @RequestMapping(value = "/deviceAll", method = {RequestMethod.GET})
  public DataVo suncheonGetDeviceAll(@RequestParam(required = false) String searchKey,
      @RequestParam(required = false) String searchValue) throws Exception {

    dataVo.setData(service.getDeviceAllByValue(searchKey, searchValue));
    dataVo.setResult(CommonConstant.R_SUCC_CODE);

    return dataVo;
  }


  @RequestMapping(value = "/device", method = {RequestMethod.GET})
  public DataVo suncheonGetDevice(int idx) throws Exception {

    dataVo.setData(service.getDevice(idx));
    dataVo.setResult(CommonConstant.R_SUCC_CODE);

    return dataVo;
  }

  @RequestMapping(value = "/device", method = {RequestMethod.POST})
  public ResultVo suncheonInsertDevice(HttpServletRequest request) throws Exception {

    String emplacement =
        request.getParameter("emplacement") == null ? "" : request.getParameter("emplacement");
    String deviceName =
        request.getParameter("deviceName") == null ? "" : request.getParameter("deviceName");
    String serialNum =
        request.getParameter("serialNum") == null ? "" : request.getParameter("serialNum");
    String lat =
        request.getParameter("lat") == null ? "" : request.getParameter("lat");
    String lon =
        request.getParameter("lon") == null ? "" : request.getParameter("lon");
    String dName =
        request.getParameter("dName") == null ? "" : request.getParameter("dName");
    String equipDt =
        request.getParameter("equipDt") == null ? "" : request.getParameter("equipDt");
    String remark = request.getParameter("remark") == null ? "" : request.getParameter("remark");

    if ("".equals(emplacement) || "".equals(deviceName) || "".equals(serialNum) || "".equals(lat)
        || "".equals(lon) || "".equals(dName) || "".equals(equipDt)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      deviceDto.setEmplacement(emplacement);
      deviceDto.setDeviceName(deviceName);
      deviceDto.setSerialNum(serialNum);
      deviceDto.setLat(lat);
      deviceDto.setLon(lon);
      deviceDto.setDName(dName);
      deviceDto.setEquipDt(equipDt);
      deviceDto.setRemark(remark);

      resultVo.setResult(service.register(deviceDto));
    }

    return resultVo;
  }


  @RequestMapping(value = "/device", method = {RequestMethod.PUT})
  public ResultVo suncheonUpdateDevice(HttpServletRequest request) throws Exception {

    int idx =
        request.getParameter("idx") == null ? 0 : Integer.parseInt(request.getParameter("idx"));
    String emplacement =
        request.getParameter("emplacement") == null ? null : request.getParameter("emplacement");
    String remark = request.getParameter("remark") == null ? null : request.getParameter("remark");
    String photoF = request.getParameter("photoF") == null ? null : request.getParameter("photoF");
    String photoB = request.getParameter("photoB") == null ? null : request.getParameter("photoB");
    String photoE = request.getParameter("photoE") == null ? null : request.getParameter("photoE");
    String photoW = request.getParameter("photoW") == null ? null : request.getParameter("photoW");
    String photoS = request.getParameter("photoS") == null ? null : request.getParameter("photoS");
    String photoN = request.getParameter("photoN") == null ? null : request.getParameter("photoN");
    String[] updateDt = request.getParameterValues("updateDt") == null ? null
        : request.getParameterValues("updateDt");
    String[] updateInfo = request.getParameterValues("updateInfo") == null ? null
        : request.getParameterValues("updateInfo");

    if (idx == 0) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      deviceDto.setIdx(idx);
      deviceDto.setEmplacement(emplacement);
      deviceDto.setRemark(remark);
      deviceDto.setPhotoF(photoF);
      deviceDto.setPhotoB(photoB);
      deviceDto.setPhotoE(photoE);
      deviceDto.setPhotoW(photoW);
      deviceDto.setPhotoS(photoS);
      deviceDto.setPhotoN(photoN);

      resultVo.setResult(service.updateDevice(deviceDto, updateDt, updateInfo));
    }

    resultVo.setResult(1);

    return resultVo;
  }

  @RequestMapping(value = "/device", method = {RequestMethod.DELETE})
  public ResultVo suncheonDeleteDevice(HttpServletRequest request) throws Exception {

    String idx = request.getParameter("idx") == null ? "" : request.getParameter("idx");

    if ("".equals(idx)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      resultVo.setResult(service.deleteDeviceByIdx(Integer.parseInt(idx)));
    }

    return resultVo;
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public ResultVo fileUpdate(MultipartHttpServletRequest request) throws Exception {

    int idx = Integer.parseInt(request.getParameter("idx"));
    String serialNum =
        request.getParameter("serialNum") == null ? "" : request.getParameter("serialNum");

    MultipartFile photoF = request.getFile("F");
    MultipartFile photoB = request.getFile("B");
    MultipartFile photoE = request.getFile("E");
    MultipartFile photoW = request.getFile("W");
    MultipartFile photoS = request.getFile("S");
    MultipartFile photoN = request.getFile("N");

    resultVo.setResult(
        service.uploadFile(idx, serialNum, photoF, photoB, photoE, photoW, photoS, photoN));

    return resultVo;
  }

  @RequestMapping(value = "/menu", method = RequestMethod.GET)
  public DataVo suncheonMenu() {
    dataVo.setResult(CommonConstant.R_SUCC_CODE);
    dataVo.setData(service.getMenu());

    return dataVo;
  }
}
