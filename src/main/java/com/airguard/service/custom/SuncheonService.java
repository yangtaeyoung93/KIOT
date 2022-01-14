package com.airguard.service.custom;

import com.airguard.exception.ParameterException;
import com.airguard.exception.SQLException;
import com.airguard.mapper.main.custom.suncheon.SuncheonMapper;
import com.airguard.model.custom.suncheon.dto.DeviceDto;
import com.airguard.model.custom.suncheon.dto.MenuDto;
import com.airguard.model.custom.suncheon.dto.UserDto;
import com.airguard.util.FileProcessUtil;
import com.airguard.util.Sha256EncryptUtil;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SuncheonService {

  private static final Logger logger = LoggerFactory.getLogger(SuncheonService.class);

  @Value("${file-folder.suncheon}")
  private String FILE_PATH;

  @Autowired
  SuncheonMapper mapper;

  public String login(HttpServletResponse response, UserDto userDto) throws Exception {

    String result;
    UserDto userInfo = mapper.selectUserByUserId(userDto.getUserId());

    if (userInfo == null) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    } else {
      if (userInfo.getUserPw().equals(Sha256EncryptUtil.ShaEncoder(userDto.getUserPw()))) {

//        Cookie cookieUserId = new Cookie("_SC_AUTH_USER", userInfo.getUserId());
//        Cookie cookieUserRole = new Cookie("_SC_USER_CLASS", userInfo.getUserClass());
//        Cookie cookieUserName = new Cookie("_SC_USER_NAME",
//            URLEncoder.encode(userInfo.getUserName(), "UTF-8"));
//        cookieUserId.setPath("/");
//        cookieUserRole.setPath("/");
//        cookieUserName.setPath("/");
//        cookieUserId.setMaxAge(60 * 60 * 24 * 7);
//        cookieUserRole.setMaxAge(60 * 60 * 24 * 7);
//        cookieUserName.setMaxAge(60 * 60 * 24 * 7);
//        response.addCookie(cookieUserId);
//        response.addCookie(cookieUserRole);
//        response.addCookie(cookieUserName);

//        result = CommonConstant.R_SUCC_CODE;

        result = userInfo.getUserClass();

      } else {
        throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);
      }
    }

    return result;
  }

//  public int logout(HttpServletResponse response) throws Exception {
//
//    int result;
//
//    try {
//      Cookie cookieUserId = new Cookie("_SC_AUTH_USER", null);
//      Cookie cookieUserRole = new Cookie("_SC_USER_CLASS", null);
//      Cookie cookieUserName = new Cookie("_SC_USER_NAME", null);
//      cookieUserId.setPath("/");
//      cookieUserRole.setPath("/");
//      cookieUserName.setPath("/");
//      cookieUserId.setMaxAge(0);
//      cookieUserRole.setMaxAge(0);
//      cookieUserName.setMaxAge(0);
//      response.addCookie(cookieUserId);
//      response.addCookie(cookieUserRole);
//      response.addCookie(cookieUserName);
//
//      result = CommonConstant.R_SUCC_CODE;
//    } catch (Exception e) {
//      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_EXCEPTION);
//    }
//
//    return result;
//  }

  public int join(UserDto userDto) throws Exception {

    int result;

    if (mapper.selectUserByUserId(userDto.getUserId()) == null) {
      userDto.setUserPw(Sha256EncryptUtil.ShaEncoder(userDto.getUserPw()));
      result = mapper.insertUser(userDto);
    } else {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    }

    return result;
  }

  public List<UserDto> getUserAll() throws Exception {
    List<UserDto> result = mapper.selectAllUser();
    if (result.size() < 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }
    return result;
  }

  public UserDto getUser(String userId) throws Exception {
    try {
      UserDto userDto = mapper.selectUserByUserId(userId);
      userDto.setUserPw(null);
      return userDto;
    } catch (Exception e) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }
  }

  public int updateUser(UserDto userDto) throws Exception {

    int result;
    if (mapper.updateUserByIdx(userDto) == 0) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    } else {
      result = mapper.updateUserByIdx(userDto);
    }

    return result;
  }

  public int deleteUserByIdx(int idx) throws Exception {
    int result;

    if (mapper.deleteUserByIdx(idx) == 0) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    } else {
      result = mapper.deleteUserByIdx(idx);
    }

    return result;
  }

  public int register(DeviceDto deviceDto) throws Exception {

    int result;
    mapper.insertDevice(deviceDto);
    int idx = mapper.selectAllDevice(null, null).get(mapper.selectAllDevice(null, null).size() - 1)
        .getIdx();
    result = mapper.insertDeviceUpdate(idx, deviceDto.getEquipDt(), "V1.0 생성 - 기기 생성");

    return result;
  }

  public List<DeviceDto> getDeviceAllByValue(String searchKey, String searchValue)
      throws Exception {
    List<DeviceDto> result = mapper.selectAllDevice(searchKey, searchValue);
    if (result.size() < 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }
    return result;
  }

  public HashMap<String, Object> getDevice(int idx) throws Exception {

    HashMap<String, Object> result = new HashMap<>();

    try {
      result.put("deviceInfo", mapper.selectDeviceByIdx(idx));
      result.put("updateInfo", mapper.selectDeviceUpdateByIdx(idx));
    } catch (Exception e) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    return result;
  }

  public int updateDevice(DeviceDto deviceDto, String[] updateDt, String[] updateInfo)
      throws Exception {

    int result;
    if (mapper.updateDeviceByIdx(deviceDto) == 0) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    } else {
      result = mapper.updateDeviceByIdx(deviceDto);

      if (updateDt != null) {
        for (int i = 0; i < updateDt.length; i++) {
          mapper.insertDeviceUpdate(deviceDto.getIdx(), updateDt[i], updateInfo[i]);
        }
      }
    }

    return result;
  }

  public int deleteDeviceByIdx(int idx) throws Exception {
    int result;

    if (mapper.deleteDeviceByIdx(idx) == 0) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    } else {
      result = mapper.deleteDeviceByIdx(idx);
    }

    return result;
  }

  public int uploadFile(int idx, String serialNum, MultipartFile photoF, MultipartFile photoB,
      MultipartFile photoE, MultipartFile photoW, MultipartFile photoS, MultipartFile photoN)
      throws Exception {

    int result;
    DeviceDto deviceDto = new DeviceDto();
    String filePath = FILE_PATH;
    String serverFileName;
    String clientFileName;
    String fileExt;
    deviceDto.setIdx(idx);

    if (photoF != null) {
      clientFileName = photoF.getOriginalFilename();
      assert clientFileName != null;
      fileExt = ".".concat(clientFileName.substring(clientFileName.lastIndexOf(".") + 1));
      deviceDto.setPhotoF(fileExt);
      serverFileName = filePath.concat(serialNum + "/photoF");

      FileProcessUtil.fileSave(photoF, filePath, serverFileName.concat(fileExt));
    }
    if (photoB != null) {
      clientFileName = photoB.getOriginalFilename();
      assert clientFileName != null;
      fileExt = ".".concat(clientFileName.substring(clientFileName.lastIndexOf(".") + 1));
      deviceDto.setPhotoB(fileExt);
      serverFileName = filePath.concat(serialNum + "/photoB");

      FileProcessUtil.fileSave(photoB, filePath, serverFileName.concat(fileExt));
    }
    if (photoE != null) {
      clientFileName = photoE.getOriginalFilename();
      assert clientFileName != null;
      fileExt = ".".concat(clientFileName.substring(clientFileName.lastIndexOf(".") + 1));
      deviceDto.setPhotoE(fileExt);
      serverFileName = filePath.concat(serialNum + "/photoE");

      FileProcessUtil.fileSave(photoE, filePath, serverFileName.concat(fileExt));
    }
    if (photoW != null) {
      clientFileName = photoW.getOriginalFilename();
      assert clientFileName != null;
      fileExt = ".".concat(clientFileName.substring(clientFileName.lastIndexOf(".") + 1));
      deviceDto.setPhotoW(fileExt);
      serverFileName = filePath.concat(serialNum + "/photoW");

      FileProcessUtil.fileSave(photoW, filePath, serverFileName.concat(fileExt));
    }
    if (photoS != null) {
      clientFileName = photoS.getOriginalFilename();
      assert clientFileName != null;
      fileExt = ".".concat(clientFileName.substring(clientFileName.lastIndexOf(".") + 1));
      deviceDto.setPhotoS(fileExt);
      serverFileName = filePath.concat(serialNum + "/photoS");

      FileProcessUtil.fileSave(photoS, filePath, serverFileName.concat(fileExt));
    }
    if (photoN != null) {
      clientFileName = photoN.getOriginalFilename();
      assert clientFileName != null;
      fileExt = ".".concat(clientFileName.substring(clientFileName.lastIndexOf(".") + 1));
      deviceDto.setPhotoN(fileExt);
      serverFileName = filePath.concat(serialNum + "/photoN");

      FileProcessUtil.fileSave(photoN, filePath, serverFileName.concat(fileExt));
    }

    result = updateDevice(deviceDto, null, null);

    return result;
  }

  public List<MenuDto> getMenu() {
    return mapper.selectMenu();
  }
}
