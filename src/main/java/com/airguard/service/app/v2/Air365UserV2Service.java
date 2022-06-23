package com.airguard.service.app.v2;

import com.airguard.exception.ParameterException;
import com.airguard.exception.SQLException;
import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.mapper.main.common.AdminMapper;
import com.airguard.mapper.main.system.GroupMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.mapper.main.system.MemberMapper;
import com.airguard.model.common.Admin;
import com.airguard.model.common.Search;
import com.airguard.model.system.Group;
import com.airguard.model.system.Member;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Vent;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class Air365UserV2Service {

  private static final Logger logger = LoggerFactory.getLogger(Air365UserV2Service.class);

  @Value("${spring.profiles.active}")
  private String SERVER_PROFILE;

  @Autowired
  private FCMPushManageUtil pushUtil;

  @Autowired
  private UserMapper mapper;

  @Autowired
  private GroupMapper groupMapper;

  @Autowired
  private StationMapper stationMapper;

  @Autowired
  private MemberMapper memberMapper;

  @Autowired
  private MemberDeviceMapper memberDeviceMapper;

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

  @Autowired
  private AdminMapper adminMapper;

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> login(HashMap<String, String> user, HttpServletResponse response) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
    List<LinkedHashMap<String, Object>> memberDatas;
    LinkedHashMap<String, Object> memberData;
    List<LinkedHashMap<String, Object>> deviceList;
    LinkedHashMap<String, Object> deviceInfo;
    List<String> deviceSerials;

    int checkCode;

    String userId = user.get("userId");
    String password = Sha256EncryptUtil.ShaEncoder(user.get("password"));
    String userType = user.get("userType");
    String token = user.get("token");
    String clientIp = user.get("clientIp");

    switch (userType) {
      case "admin":
        Admin admin = new Admin();

        admin.setUserId(userId);
        admin.setUserPw(password);
        checkCode = readOnlyMapper.loginCheckAdminId(admin);
        if (checkCode == 3) {

          try {

            memberDatas = new ArrayList<>();

            Search search = new Search();
            search.setSearchUseYn("Y");
            search.setSearchValue("");

            for (Member memberInfo : readOnlyMapper.selectMemberList(search)) {
              memberData = new LinkedHashMap<>();

              memberData.put("memberId", memberInfo.getUserId() == null ? CommonConstant.NULL_DATA
                      : memberInfo.getUserId());
              memberData.put("region", memberInfo.getRegion() == null ? CommonConstant.NULL_DATA
                      : memberInfo.getRegion());
              memberData.put("regionName",
                      memberInfo.getRegionName() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getRegionName());
              memberData.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA
                      : memberInfo.getUserName());
              memberData.put("phoneNumber",
                      memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getPhoneNumber());
              memberData.put("userEmail",
                      memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getUserEmail());
              memberData.put("groupName",
                      memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getGroupName());

              deviceList = new ArrayList<>();
              for (MemberDevice mdi : readOnlyMapper
                      .selectMemberDeviceOne(Integer.toString(memberInfo.getIdx()))) {
                deviceInfo = new LinkedHashMap<>();

                deviceInfo.put("serial",
                        mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
                deviceInfo.put("deviceType",
                        mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
                deviceInfo.put("stationName",
                        mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
                deviceInfo.put("lat", mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                        : Double.parseDouble(mdi.getLat()));
                deviceInfo.put("lon", mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                        : Double.parseDouble(mdi.getLon()));

                deviceList.add(deviceInfo);
              }

              memberData.put("deviceList", deviceList);

              memberDatas.add(memberData);
            }

            datas.put("memberList", memberDatas);

          } catch (Exception e) {
            throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
          }
          res.put("data", datas);
        }

        break;

      case "member":
        Member member = new Member();

        member.setUserId(userId);
        member.setUserPw(password);
        member.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        member.setLoginIp(clientIp);

        checkCode = readOnlyMapper.loginCheckMemberId(member);

        if (checkCode == 3) {

          try {

            deviceSerials = new ArrayList<>();
            Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);

            datas.put("idx", findMemberData.getIdx());
            datas.put("memberId", userId);
            datas.put("region", findMemberData.getRegion() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getRegion());
            datas.put("regionName",
                    findMemberData.getRegionName() == null ? CommonConstant.NULL_DATA
                            : findMemberData.getRegionName());
            datas.put("userName", findMemberData.getUserName() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getUserName());
            datas.put("phoneNumber",
                    findMemberData.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                            : findMemberData.getPhoneNumber());
            datas.put("telephone", findMemberData.getTelephone() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getTelephone());
            datas.put("userEmail", findMemberData.getUserEmail() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getUserEmail());
            datas.put("groupName", findMemberData.getGroupName() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getGroupName());

            deviceList = new ArrayList<>();
            for (MemberDevice mdi : readOnlyMapper
                    .selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
              deviceInfo = new LinkedHashMap<>();

              deviceInfo.put("deviceIdx", Integer.parseInt(mdi.getDeviceIdx()));
              deviceInfo.put("serial",
                      mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
              deviceInfo.put("deviceType",
                      mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
              deviceInfo.put("stationName",
                      mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
              deviceInfo.put("lat", mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                      : Double.parseDouble(mdi.getLat()));
              deviceInfo.put("lon", mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                      : Double.parseDouble(mdi.getLon()));

              deviceList.add(deviceInfo);

              deviceSerials.add(mdi.getSerialNum());
            }


            memberMapper.updateMemberLoginCount(userId, 1);
            member.setIdx(Integer.valueOf(datas.get("idx").toString()));
            memberMapper.memberLoginInfoUpdate(member);
            datas.put("deviceList", deviceList);
            res.put("data", datas);
            res.put("idx", findMemberData.getIdx());

            if (!"".equals(token)) {
              pushUtil.pushControlDataGenerateF(token, userType, userId, deviceSerials);
            }

          } catch (Exception e) {
            throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
          }

        } else if (checkCode == 4) {
          throw new SQLException(SQLException.USER_LOGIN_ROCK_EXCEPTION);

        } else if (checkCode == 5) {
          throw new SQLException(SQLException.NOT_USE_DATA_EXCEPION);

        } else {
          memberMapper.updateMemberLoginCount(userId, 0);
        }

        break;

      case "group":
        Group group = new Group();

        group.setGroupId(userId);
        group.setGroupPw(password);
        group.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        group.setLoginIp(clientIp);

        checkCode = readOnlyMapper.loginCheckGroupId(group);
        if (checkCode == 3) {

          try {

            memberDatas = new ArrayList<>();
            deviceSerials = new ArrayList<>();

            Group findGroupData = readOnlyMapper.findGroupByLoginId(userId);

            res.put("idx", findGroupData.getIdx());
            datas.put("idx", findGroupData.getIdx());
            datas.put("groupId", userId);
            datas.put("groupName", findGroupData.getGroupName() == null ? CommonConstant.NULL_DATA
                    : findGroupData.getGroupName());
            datas.put("groupDepartName",
                    findGroupData.getGroupDepartName() == null ? CommonConstant.NULL_DATA
                            : findGroupData.getGroupDepartName());
            datas.put("groupEmail", findGroupData.getGroupEmail() == null ? CommonConstant.NULL_DATA
                    : findGroupData.getGroupEmail());
            datas.put("phoneNumber",
                    findGroupData.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA
                            : findGroupData.getGroupPhoneNumber());
            datas.put("telephone",
                    findGroupData.getGroupTelephone() == null ? CommonConstant.NULL_DATA
                            : findGroupData.getGroupTelephone());

            for (Map<String, Object> memberInfo : readOnlyMapper
                    .selectGroupMembers(Integer.toString(findGroupData.getIdx()))) {
              memberData = new LinkedHashMap<>();

              memberData.put("memberIdx", Integer.parseInt(memberInfo.get("memberIdx").toString()));
              memberData.put("memberId", memberInfo.get("userId") == null ? CommonConstant.NULL_DATA
                      : memberInfo.get("userId").toString());
              memberData.put("region", memberInfo.get("region") == null ? CommonConstant.NULL_DATA
                      : memberInfo.get("region").toString());
              memberData.put("regionName",
                      memberInfo.get("regionName") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("regionName").toString());
              memberData.put("userName",
                      memberInfo.get("userName") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("userName").toString());
              memberData.put("phoneNumber",
                      memberInfo.get("phoneNumber") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("phoneNumber").toString());
              memberData.put("userEmail",
                      memberInfo.get("userEmail") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("userEmail").toString());

              deviceList = new ArrayList<>();
              for (MemberDevice mdi : readOnlyMapper
                      .selectMemberDeviceOne(memberInfo.get("memberIdx").toString())) {
                deviceInfo = new LinkedHashMap<>();
                System.out.println(" lat : lon ::::: " + mdi.getLat() + ", " + mdi.getLon());
                deviceInfo.put("deviceIdx", Integer.parseInt(mdi.getDeviceIdx()));
                deviceInfo.put("serial",
                        mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
                deviceInfo.put("deviceType",
                        mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
                deviceInfo.put("stationName",
                        mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
                deviceInfo.put("lat", mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                        : Double.parseDouble(mdi.getLat()));
                deviceInfo.put("lon", mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                        : Double.parseDouble(mdi.getLon()));

                deviceList.add(deviceInfo);

                deviceSerials.add(mdi.getSerialNum());
              }

              memberData.put("deviceList", deviceList);

              memberDatas.add(memberData);
            }

            groupMapper.groupLoginInfoUpdate(group);
            datas.put("memberList", memberDatas);

            res.put("data", datas);

            if (!"".equals(token)) {
              pushUtil.pushControlDataGenerateF(token, userType, userId, deviceSerials);
            }

          } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
          }

        } else if (checkCode == 5) {
          throw new SQLException(SQLException.NOT_USE_DATA_EXCEPION);
        }

        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    if (checkCode == 1 || checkCode == 2) {
      throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);

    } else {
      if (!"".equals(token)) {
        String cusUserId = "group".equals(userType) ? "g-".concat(userId) : userId;

        pushUtil.pushTokenDataGenerateF(cusUserId, token);

        if (0 == readOnlyMapper.checkFcmTokenUserId(cusUserId, token)) {
          HashMap<String, String> cusUser = new HashMap<>();
          cusUser.put("userId", cusUserId);
          cusUser.put("token", token);

          if (0 != readOnlyMapper.checkFcmToken(token)) {
            mapper.deleteFcmTokenInfo(cusUser);
          }

          mapper.insertFcmTokenInfo(cusUser);
        }
      }

      RestApiCookieManageUtil.makeAuthCookie(SERVER_PROFILE, userId, userType, response);

      res.put("result", CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> loginEncodeVersion(HashMap<String, String> user, HttpServletResponse response) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
    List<LinkedHashMap<String, Object>> memberDatas;
    LinkedHashMap<String, Object> memberData;
    List<LinkedHashMap<String, Object>> deviceList;
    LinkedHashMap<String, Object> deviceInfo;
    List<String> deviceSerials;

    int checkCode;

    String userId = user.get("userId");
    String password = Sha256EncryptUtil.ShaEncoder(user.get("password"));
    String userType = user.get("userType");
    String token = user.get("token");
    String clientIp = user.get("clientIp");

    switch (userType) {
      case "admin":
        Admin admin = new Admin();

        admin.setUserId(userId);
        admin.setUserPw(password);

        checkCode = readOnlyMapper.loginCheckAdminId(admin);
        if (checkCode == 3) {

          try {

            memberDatas = new ArrayList<>();

            Search search = new Search();
            search.setSearchUseYn("Y");
            search.setSearchValue("");

            for (Member memberInfo : readOnlyMapper.selectMemberList(search)) {
              memberData = new LinkedHashMap<>();

              memberData.put("memberId", AES256Util.encrypt(memberInfo.getUserId() == null ? CommonConstant.NULL_DATA
                      : memberInfo.getUserId()));
              memberData.put("region", AES256Util.encrypt(memberInfo.getRegion() == null ? CommonConstant.NULL_DATA
                      : memberInfo.getRegion()));
              memberData.put("regionName",
                      AES256Util.encrypt(memberInfo.getRegionName() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getRegionName()));
              memberData.put("userName", AES256Util.encrypt(memberInfo.getUserName() == null ? CommonConstant.NULL_DATA
                      : memberInfo.getUserName()));
              memberData.put("phoneNumber",
                      AES256Util.encrypt(memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getPhoneNumber()));
              memberData.put("userEmail",
                      AES256Util.encrypt(memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getUserEmail()));
              memberData.put("groupName",
                      AES256Util.encrypt(memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                              : memberInfo.getGroupName()));

              deviceList = new ArrayList<>();
              for (MemberDevice mdi : readOnlyMapper
                      .selectMemberDeviceOne(Integer.toString(memberInfo.getIdx()))) {
                deviceInfo = new LinkedHashMap<>();

                deviceInfo.put("serial",
                        AES256Util.encrypt(mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum()));
                deviceInfo.put("deviceType",
                        AES256Util.encrypt(mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType()));
                deviceInfo.put("stationName",
                        AES256Util.encrypt(mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName()));
                deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                        : mdi.getLat()));
                deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                        : mdi.getLon()));

                deviceList.add(deviceInfo);
              }

              memberData.put("deviceList", deviceList);

              memberDatas.add(memberData);
            }

            datas.put("memberList", memberDatas);

          } catch (Exception e) {
            throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
          }

          res.put("data", datas);
        }

        break;

      case "member":
        Member member = new Member();

        member.setUserId(userId);
        member.setUserPw(password);
        member.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        member.setLoginIp(clientIp);

        checkCode = readOnlyMapper.loginCheckMemberId(member);
        if (checkCode == 3) {

          try {

            deviceSerials = new ArrayList<>();
            Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);

            datas.put("idx", AES256Util.encrypt(findMemberData.getIdx()+""));
            datas.put("memberId", AES256Util.encrypt(userId));
            datas.put("region", AES256Util.encrypt(findMemberData.getRegion() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getRegion()));
            datas.put("regionName",
                    AES256Util.encrypt(findMemberData.getRegionName() == null ? CommonConstant.NULL_DATA
                            : findMemberData.getRegionName()));
            datas.put("userName", AES256Util.encrypt(findMemberData.getUserName() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getUserName()));
            datas.put("phoneNumber",
                    AES256Util.encrypt(findMemberData.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                            : findMemberData.getPhoneNumber()));
            datas.put("telephone", AES256Util.encrypt(findMemberData.getTelephone() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getTelephone()));
            datas.put("userEmail", AES256Util.encrypt(findMemberData.getUserEmail() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getUserEmail()));
            datas.put("groupName", AES256Util.encrypt(findMemberData.getGroupName() == null ? CommonConstant.NULL_DATA
                    : findMemberData.getGroupName()));

            deviceList = new ArrayList<>();
            for (MemberDevice mdi : readOnlyMapper
                    .selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
              deviceInfo = new LinkedHashMap<>();

              deviceInfo.put("deviceIdx", AES256Util.encrypt(mdi.getDeviceIdx()));
              deviceInfo.put("serial",
                      AES256Util.encrypt(mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum()));
              deviceInfo.put("deviceType",
                      AES256Util.encrypt(mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType()));
              deviceInfo.put("stationName",
                      AES256Util.encrypt(mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName()));
              deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                      : mdi.getLat()));
              deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                      : mdi.getLon()));

              deviceList.add(deviceInfo);

              deviceSerials.add(mdi.getSerialNum());
            }

            memberMapper.updateMemberLoginCount(userId, 1);
            member.setIdx(findMemberData.getIdx());
            memberMapper.memberLoginInfoUpdate(member);

            datas.put("deviceList", deviceList);
            res.put("data", datas);
            res.put("idx", findMemberData.getIdx());

            if (!"".equals(token)) {
              pushUtil.pushControlDataGenerateF(token, userType, userId, deviceSerials);
            }

          } catch (Exception e) {
            throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
          }

        } else if (checkCode == 4) {
          throw new SQLException(SQLException.USER_LOGIN_ROCK_EXCEPTION);

        } else if (checkCode == 5) {
          throw new SQLException(SQLException.NOT_USE_DATA_EXCEPION);

        } else {
          memberMapper.updateMemberLoginCount(userId, 0);
        }

        break;

      case "group":
        Group group = new Group();

        group.setGroupId(userId);
        group.setGroupPw(password);
        group.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        group.setLoginIp(clientIp);

        checkCode = readOnlyMapper.loginCheckGroupId(group);
        if (checkCode == 3) {

          try {

            memberDatas = new ArrayList<>();
            deviceSerials = new ArrayList<>();

            Group findGroupData = readOnlyMapper.findGroupByLoginId(userId);

            res.put("idx", AES256Util.encrypt(findGroupData.getIdx()+""));
            datas.put("idx", AES256Util.encrypt(findGroupData.getIdx()+""));
            datas.put("groupId", AES256Util.encrypt(userId));
            datas.put("groupName", AES256Util.encrypt(findGroupData.getGroupName() == null ? CommonConstant.NULL_DATA
                    : findGroupData.getGroupName()));
            datas.put("groupDepartName",
                    AES256Util.encrypt(findGroupData.getGroupDepartName() == null ? CommonConstant.NULL_DATA
                            : findGroupData.getGroupDepartName()));
            datas.put("groupEmail", AES256Util.encrypt(findGroupData.getGroupEmail() == null ? CommonConstant.NULL_DATA
                    : findGroupData.getGroupEmail()));
            datas.put("phoneNumber",
                    AES256Util.encrypt(findGroupData.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA
                            : findGroupData.getGroupPhoneNumber()));
            datas.put("telephone",
                    AES256Util.encrypt(findGroupData.getGroupTelephone() == null ? CommonConstant.NULL_DATA
                            : findGroupData.getGroupTelephone()));

            for (Map<String, Object> memberInfo : readOnlyMapper
                    .selectGroupMembers(Integer.toString(findGroupData.getIdx()))) {
              memberData = new LinkedHashMap<>();

              memberData.put("memberIdx", AES256Util.encrypt(memberInfo.get("memberIdx").toString()));
              memberData.put("memberId", AES256Util.encrypt(memberInfo.get("userId") == null ? CommonConstant.NULL_DATA
                      : memberInfo.get("userId").toString()));
              memberData.put("region", AES256Util.encrypt(memberInfo.get("region") == null ? CommonConstant.NULL_DATA
                      : memberInfo.get("region").toString()));
              memberData.put("regionName",
                      AES256Util.encrypt(memberInfo.get("regionName") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("regionName").toString()));
              memberData.put("userName",
                      AES256Util.encrypt(memberInfo.get("userName") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("userName").toString()));
              memberData.put("phoneNumber",
                      AES256Util.encrypt(memberInfo.get("phoneNumber") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("phoneNumber").toString()));
              memberData.put("userEmail",
                      AES256Util.encrypt(memberInfo.get("userEmail") == null ? CommonConstant.NULL_DATA
                              : memberInfo.get("userEmail").toString()));

              deviceList = new ArrayList<>();
              for (MemberDevice mdi : readOnlyMapper
                      .selectMemberDeviceOne(memberInfo.get("memberIdx").toString())) {
                deviceInfo = new LinkedHashMap<>();

                deviceInfo.put("deviceIdx", AES256Util.encrypt(mdi.getDeviceIdx()));
                deviceInfo.put("serial",
                        AES256Util.encrypt(mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum()));
                deviceInfo.put("deviceType",
                        AES256Util.encrypt(mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType()));
                deviceInfo.put("stationName",
                        AES256Util.encrypt(mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName()));
                deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                        : mdi.getLat()));
                deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                        : mdi.getLon()));

                deviceList.add(deviceInfo);
                deviceSerials.add(mdi.getSerialNum());
              }

              memberData.put("deviceList", deviceList);

              memberDatas.add(memberData);
            }

            groupMapper.groupLoginInfoUpdate(group);
            datas.put("memberList", memberDatas);

            res.put("data", datas);

            if (!"".equals(token)) {
              pushUtil.pushControlDataGenerateF(token, userType, userId, deviceSerials);
            }

          } catch (Exception e) {
            throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
          }

        } else if (checkCode == 5) {
          throw new SQLException(SQLException.NOT_USE_DATA_EXCEPION);
        }

        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    if (checkCode == 1 || checkCode == 2) {
      throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);

    } else {
      if (!"".equals(token)) {
        String cusUserId = "group".equals(userType) ? "g-".concat(userId) : userId;
        logger.error(
                "insert token into Redis Server. cusUserId : {}, token : {}",
                cusUserId,token);
        pushUtil.pushTokenDataGenerateF(cusUserId, token);

        if (0 == readOnlyMapper.checkFcmTokenUserId(cusUserId, token)) {
          HashMap<String, String> cusUser = new HashMap<>();
          cusUser.put("userId", cusUserId);
          cusUser.put("token", token);

          if (0 != readOnlyMapper.checkFcmToken(token)) {
            mapper.deleteFcmTokenInfo(cusUser);
          }

          mapper.insertFcmTokenInfo(cusUser);
        }
      }

      RestApiCookieManageUtil.makeAuthCookie(SERVER_PROFILE, userId, userType, response);

      res.put("result", CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  public LinkedHashMap<String, Object> appLogOut(HashMap<String, String> req,
                                                 HttpServletResponse res) throws Exception {
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();

    int resultCode = CommonConstant.R_SUCC_CODE;

    String token = req.get("token");

    if (!"".equals(token)) {
      pushUtil.pushTokenDataClearF(token);

      mapper.deleteFcmTokenInfo(req);

      RestApiCookieManageUtil.deleteCookie(res);
    }

    result.put("result", resultCode);

    return result;
  }

  private static String getTagValue(String tag, Element eElement) {
    NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = nlList.item(0);
    if (nValue == null) {
      return null;
    }

    return nValue.getNodeValue();
  }

  public LinkedHashMap<String, Object> findRegionInfo(HashMap<String, String> req)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    List<Map<String, Object>> datas = new ArrayList<>();
    List<NameValuePair> urlParameters = new ArrayList<>();
    Map<String, Object> data;

    HttpPost post = new HttpPost("https://was.kweather.co.kr/kapi/airguardk/getXML_lonlat_new.php");

    urlParameters.add(new BasicNameValuePair("lon", req.get("lon")));
    urlParameters.add(new BasicNameValuePair("lat", req.get("lat")));

    post.setEntity(new UrlEncodedFormEntity(urlParameters));

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(post)) {

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder
              .parse(new InputSource(new StringReader(EntityUtils.toString(response.getEntity()))));

      NodeList nList = doc.getElementsByTagName("srch_pt");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        data = new LinkedHashMap<>();
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;

          data.put("region", getTagValue("region_id", eElement));
          data.put("regionName",
                  getTagValue("city_ko", eElement) + " " + getTagValue("dong_ko", eElement));
        }

        datas.add(data);
      }
    }

    res.put("data", datas);
    res.put("result", CommonConstant.R_SUCC_CODE);

    return res;
  }

  public LinkedHashMap<String, Object> findRegionInfoEncodeVersion(HashMap<String, String> req)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    List<Map<String, Object>> datas = new ArrayList<>();
    List<NameValuePair> urlParameters = new ArrayList<>();
    Map<String, Object> data;

    HttpPost post = new HttpPost("https://was.kweather.co.kr/kapi/airguardk/getXML_lonlat_new.php");

    urlParameters.add(new BasicNameValuePair("lon", req.get("lon")));
    urlParameters.add(new BasicNameValuePair("lat", req.get("lat")));

    post.setEntity(new UrlEncodedFormEntity(urlParameters));

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(post)) {

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder
              .parse(new InputSource(new StringReader(EntityUtils.toString(response.getEntity()))));

      NodeList nList = doc.getElementsByTagName("srch_pt");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        data = new LinkedHashMap<>();
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;

          data.put("region", AES256Util.encrypt(getTagValue("region_id", eElement)));
          data.put("regionName",
                  AES256Util.encrypt(getTagValue("city_ko", eElement) + " " + getTagValue("dong_ko", eElement)));
        }

        datas.add(data);
      }
    }

    res.put("data", datas);
    res.put("result", CommonConstant.R_SUCC_CODE);

    return res;
  }

  public LinkedHashMap<String, Object> checkUserId(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    String userId = req.get("userId");
    String userType = req.get("userType");

    int checkResult;

    switch (userType) {
      case "member":
        checkResult = readOnlyMapper.checkUserId(userId);
        if (checkResult == 1) {
          checkResult = 0;
        }
        break;

      case "group":
        checkResult = readOnlyMapper.checkGroupId(userId);
        break;

      case "admin":
        checkResult = readOnlyMapper.checkAdminUserId(userId);
        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    if (checkResult != 0) {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    } else {
      res.put("result", CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  public LinkedHashMap<String, Object> sendSMSData(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    int resultCode;

    Random random = new Random();
    String randomNumber = Integer.toString(random.nextInt(9))
            .concat(Integer.toString(random.nextInt(9))).concat(Integer.toString(random.nextInt(9)))
            .concat(Integer.toString(random.nextInt(9)));

    String announceMent = URLEncoder.encode("[AIR365] 인증번호는 [".concat(randomNumber).concat("]입니다."),
            StandardCharsets.UTF_8.toString());

    resultCode = SmsSendUtil.mailSend(req.get("phoneNumber"), announceMent);

    res.put("random", randomNumber);
    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> sendSMSDataEncodeVersion(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    int resultCode;

    Random random = new Random();
    String randomNumber = Integer.toString(random.nextInt(9))
            .concat(Integer.toString(random.nextInt(9))).concat(Integer.toString(random.nextInt(9)))
            .concat(Integer.toString(random.nextInt(9)));

    String announceMent = URLEncoder.encode("[AIR365] 인증번호는 [".concat(randomNumber).concat("]입니다."),
            StandardCharsets.UTF_8.toString());

    resultCode = SmsSendUtil.mailSend(req.get("phoneNumber"), announceMent);

    res.put("random", AES256Util.encrypt(randomNumber));
    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> selectUserInfoAll(HashMap<String, String> user)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
    List<LinkedHashMap<String, Object>> groupUsers;
    List<LinkedHashMap<String, Object>> memberUsers;
    LinkedHashMap<String, Object> groupUser;
    LinkedHashMap<String, Object> memberUser;

    int resultCode;

    String userType = user.get("userType");

    Search search = new Search();
    search.setSearchUseYn("Y");
    search.setSearchValue("");

    List<Member> memberInfos = readOnlyMapper.selectMemberList(search);
    List<Group> groupInfos = readOnlyMapper.findGroupList();

    switch (userType) {
      case "all":
        groupUsers = new ArrayList<>();
        memberUsers = new ArrayList<>();
        for (Member memberInfo : memberInfos) {
          memberUser = new LinkedHashMap<>();

          memberUser.put("id",
                  memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
          memberUser.put("idx", memberInfo.getIdx());
          memberUser.put("type", "member");
          memberUser.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getGroupName());
          memberUser.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getUserName());
          memberUser.put("groupDepartName", "NA");
          memberUser.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getUserEmail());
          memberUser.put("phoneNumber",
                  memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : memberInfo.getPhoneNumber());
          memberUser.put("telephone", memberInfo.getTelephone() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getTelephone());
          memberUser.put("createDt", memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getCreateDt());

          memberUsers.add(memberUser);
        }

        for (Group groupInfo : groupInfos) {
          groupUser = new LinkedHashMap<>();

          groupUser.put("id",
                  groupInfo.getGroupId() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupId());
          groupUser.put("idx", groupInfo.getIdx());
          groupUser.put("type", "group");
          groupUser.put("groupName", groupInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                  : groupInfo.getGroupName());
          groupUser.put("userName", "NA");
          groupUser.put("groupDepartName",
                  groupInfo.getGroupDepartName() == null ? CommonConstant.NULL_DATA
                          : groupInfo.getGroupDepartName());
          groupUser.put("groupEmail", groupInfo.getGroupEmail() == null ? CommonConstant.NULL_DATA
                  : groupInfo.getGroupEmail());
          groupUser.put("phoneNumber",
                  groupInfo.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : groupInfo.getGroupPhoneNumber());
          groupUser.put("telephone",
                  groupInfo.getGroupTelephone() == null ? CommonConstant.NULL_DATA
                          : groupInfo.getGroupTelephone());
          groupUser.put("createDt",
                  groupInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : groupInfo.getCreateDt());

          groupUsers.add(groupUser);
        }

        datas.put("memberUsers", memberUsers);
        datas.put("groupUsers", groupUsers);
        res.put("data", datas);
        resultCode = CommonConstant.R_SUCC_CODE;

        break;

      case "group":
        groupUsers = new ArrayList<>();
        for (Group groupInfo : groupInfos) {
          groupUser = new LinkedHashMap<>();

          groupUser.put("id",
                  groupInfo.getGroupId() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupId());
          groupUser.put("idx", groupInfo.getIdx());
          groupUser.put("type", "group");
          groupUser.put("groupName", groupInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                  : groupInfo.getGroupName());
          groupUser.put("userName", "NA");
          groupUser.put("groupDepartName",
                  groupInfo.getGroupDepartName() == null ? CommonConstant.NULL_DATA
                          : groupInfo.getGroupDepartName());
          groupUser.put("groupEmail", groupInfo.getGroupEmail() == null ? CommonConstant.NULL_DATA
                  : groupInfo.getGroupEmail());
          groupUser.put("phoneNumber",
                  groupInfo.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : groupInfo.getGroupPhoneNumber());
          groupUser.put("telephone",
                  groupInfo.getGroupTelephone() == null ? CommonConstant.NULL_DATA
                          : groupInfo.getGroupTelephone());
          groupUser.put("createDt",
                  groupInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : groupInfo.getCreateDt());

          groupUsers.add(groupUser);
        }

        datas.put("groupUsers", groupUsers);
        res.put("data", datas);
        resultCode = CommonConstant.R_SUCC_CODE;
        break;

      case "member":
        memberUsers = new ArrayList<>();
        for (Member memberInfo : memberInfos) {
          memberUser = new LinkedHashMap<>();

          memberUser.put("id",
                  memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
          memberUser.put("idx", memberInfo.getIdx());
          memberUser.put("type", "member");
          memberUser.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getGroupName());
          memberUser.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getUserName());
          memberUser.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getUserEmail());
          memberUser.put("phoneNumber",
                  memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : memberInfo.getPhoneNumber());
          memberUser.put("telephone", memberInfo.getTelephone() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getTelephone());
          memberUser.put("createDt", memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA
                  : memberInfo.getCreateDt());

          memberUsers.add(memberUser);
        }

        datas.put("memberUsers", memberUsers);
        res.put("data", datas);
        resultCode = CommonConstant.R_SUCC_CODE;

        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> selectUserInfo(HashMap<String, String> user)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
    List<LinkedHashMap<String, Object>> memberDatas;
    List<HashMap<String, Object>> groupDeviceList;
    List<HashMap<String, Object>> deviceList;
    List<HashMap<String, Object>> ventDeviceList;
    HashMap<String, Object> deviceInfo;
    HashMap<String, Object> ventDeviceInfo;

    int resultCode = CommonConstant.R_SUCC_CODE;

    String userId = user.get("userId");
    String userType = user.get("userType");

    switch (userType) {
      case "admin":
        memberDatas = new ArrayList<>();

        Search search = new Search();
        search.setSearchUseYn("Y");
        search.setSearchValue("");

        try {

          List<Member> memberInfos = readOnlyMapper.selectMemberList(search);
          for (Member memberInfo : memberInfos) {
            LinkedHashMap<String, Object> memberData = new LinkedHashMap<>();

            memberData.put("idx", memberInfo.getIdx());
            memberData.put("memberId",
                    memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
            memberData.put("region",
                    memberInfo.getRegion() == null ? CommonConstant.NULL_DATA : memberInfo.getRegion());
            memberData.put("regionName",
                    memberInfo.getRegionName() == null ? CommonConstant.NULL_DATA
                            : memberInfo.getRegionName());
            memberData.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getUserName());
            memberData.put("phoneNumber",
                    memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                            : memberInfo.getPhoneNumber());
            memberData.put("telephone", memberInfo.getTelephone() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getTelephone());
            memberData.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getUserEmail());
            memberData.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getGroupName());
            memberData.put("createDt", memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getCreateDt());

            deviceList = new ArrayList<>();
            for (MemberDevice mdi : readOnlyMapper
                    .selectMemberDeviceOne(Integer.toString(memberInfo.getIdx()))) {
              deviceInfo = new LinkedHashMap<>();

              deviceInfo.put("deviceIdx", Integer.parseInt(mdi.getDeviceIdx()));
              deviceInfo.put("serial",
                      mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
              deviceInfo.put("deviceType",
                      mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
              deviceInfo.put("deviceModelName",
                      mdi.getDeviceModel() == null ? CommonConstant.NULL_DATA : mdi.getDeviceModel());
              deviceInfo.put("deviceModelImgPath",
                      mdi.getDeviceModelImgPath() == null ? CommonConstant.NULL_DATA :mdi.getDeviceModelImgPath());
              deviceInfo.put("stationName",
                      mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
              deviceInfo.put("country_nm","대한민국");
              if(mdi.getDfname() == null){
                deviceInfo.put("sido_nm",CommonConstant.NULL_DATA);
                deviceInfo.put("sg_nm",CommonConstant.NULL_DATA);
                deviceInfo.put("emd_nm",CommonConstant.NULL_DATA);
              }else{
                deviceInfo.put("sido_nm",mdi.getDfname().split(" ")[0]);
                deviceInfo.put("sg_nm",mdi.getDfname().split(" ")[1]);
                deviceInfo.put("emd_nm",mdi.getDfname().split(" ")[2]);
              }
              deviceInfo.put("hang_cd",readOnlyMapper.selectDcode(mdi.getSerialNum()));
              deviceInfo.put("lat", mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                      : Double.parseDouble(mdi.getLat()));
              deviceInfo.put("lon", mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                      : Double.parseDouble(mdi.getLon()));
              ventDeviceList = new ArrayList<>();
              if ("IAQ".equals(mdi.getDeviceType())) {
                for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx())) {
                  ventDeviceInfo = new LinkedHashMap<>();

                  ventDeviceInfo.put("ventDeviceIdx", Integer.parseInt(v.getVentDeviceIdx()));
                  ventDeviceInfo.put("ventSerial",
                          v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());
                  ventDeviceInfo.put("deviceModel",
                          v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel());
                  ventDeviceList.add(ventDeviceInfo);
                }

                deviceInfo.put("vents", ventDeviceList);

              } else {
                deviceInfo.put("vents", new ArrayList<>());
              }

              deviceList.add(deviceInfo);
            }

            memberData.put("deviceList", deviceList);
            memberDatas.add(memberData);

          }

          datas.put("memberList", memberDatas);

        } catch (Exception e) {
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        }

        res.put("data", datas);

        break;

      case "member":

        try {

          Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);

          datas.put("idx", findMemberData.getIdx());
          datas.put("memberId", userId);
          datas.put("region", findMemberData.getRegion() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getRegion());
          datas.put("regionName", findMemberData.getRegionName() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getRegionName());
          datas.put("userName", findMemberData.getUserName() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getUserName());
          datas.put("phoneNumber",
                  findMemberData.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : findMemberData.getPhoneNumber());
          datas.put("telephone", findMemberData.getTelephone() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getTelephone());
          datas.put("userEmail", findMemberData.getUserEmail() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getUserEmail());
          datas.put("groupName", findMemberData.getGroupName() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getGroupName());
          datas.put("createDt", findMemberData.getCreateDt() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getCreateDt());

          deviceList = new ArrayList<>();
          for (MemberDevice mdi : readOnlyMapper
                  .selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
            deviceInfo = new LinkedHashMap<>();

            deviceInfo.put("deviceIdx", Integer.parseInt(mdi.getDeviceIdx()));
            deviceInfo.put("serial",
                    mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
            deviceInfo.put("deviceType",
                    mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
            deviceInfo.put("deviceModelName",
                    mdi.getDeviceModel() == null ? CommonConstant.NULL_DATA : mdi.getDeviceModel());
            deviceInfo.put("deviceModelImgPath",
                    mdi.getDeviceModelImgPath() == null ? CommonConstant.NULL_DATA :mdi.getDeviceModelImgPath());
            deviceInfo.put("stationName",
                    mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
            deviceInfo.put("country_nm","대한민국");
            if(mdi.getDfname() == null){
              deviceInfo.put("sido_nm",CommonConstant.NULL_DATA);
              deviceInfo.put("sg_nm",CommonConstant.NULL_DATA);
              deviceInfo.put("emd_nm",CommonConstant.NULL_DATA);
            }else{
              deviceInfo.put("sido_nm",mdi.getDfname().split(" ")[0]);
              deviceInfo.put("sg_nm",mdi.getDfname().split(" ")[1]);
              deviceInfo.put("emd_nm",mdi.getDfname().split(" ")[2]);
            }
            deviceInfo.put("hang_cd",readOnlyMapper.selectDcode(mdi.getSerialNum()));
            deviceInfo.put("lat",
                    mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
            deviceInfo.put("lon",
                    mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

            ventDeviceList = new ArrayList<>();
            if ("IAQ".equals(mdi.getDeviceType())) {
              for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx())) {
                ventDeviceInfo = new LinkedHashMap<>();

                ventDeviceInfo.put("ventDeviceIdx", Integer.parseInt(v.getVentDeviceIdx()));
                ventDeviceInfo.put("ventSerial",
                        v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());
                ventDeviceInfo.put("deviceModel",
                        v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel());

                ventDeviceList.add(ventDeviceInfo);
              }

              deviceInfo.put("vents", ventDeviceList);

            } else {
              deviceInfo.put("vents", new ArrayList<>());
            }

            deviceList.add(deviceInfo);
          }

          datas.put("deviceList", deviceList);

        } catch (Exception e) {
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        }

        res.put("data", datas);
        break;

      case "group":

        try {

          Group findGroupData = readOnlyMapper.findGroupByLoginId(userId);

          datas.put("groupId", userId);
          datas.put("idx", findGroupData.getIdx());
          datas.put("groupName", findGroupData.getGroupName() == null ? CommonConstant.NULL_DATA
                  : findGroupData.getGroupName());
          datas.put("groupDepartName",
                  findGroupData.getGroupDepartName() == null ? CommonConstant.NULL_DATA
                          : findGroupData.getGroupDepartName());
          datas.put("groupEmail", findGroupData.getGroupEmail() == null ? CommonConstant.NULL_DATA
                  : findGroupData.getGroupEmail());
          datas.put("phoneNumber",
                  findGroupData.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : findGroupData.getGroupPhoneNumber());
          datas.put("telephone",
                  findGroupData.getGroupTelephone() == null ? CommonConstant.NULL_DATA
                          : findGroupData.getGroupTelephone());

          List<Map<String, Object>> memberInfos = readOnlyMapper
                  .selectGroupMembers(Integer.toString(findGroupData.getIdx()));

          memberDatas = new ArrayList<>();
          for (Map<String, Object> memberInfo : memberInfos) {
            LinkedHashMap<String, Object> memberData = new LinkedHashMap<>();

            memberData.put("memberIdx", Integer.parseInt(memberInfo.get("memberIdx").toString()));
            memberData.put("userId", memberInfo.get("userId") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("userId").toString());
            memberData.put("region", memberInfo.get("region") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("region").toString());
            memberData.put("regionName",
                    memberInfo.get("regionName") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("regionName").toString());
            memberData.put("userName", memberInfo.get("userName") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("userName").toString());
            memberData.put("phoneNumber",
                    memberInfo.get("phoneNumber") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("phoneNumber").toString());
            memberData.put("telephone",
                    memberInfo.get("telephone") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("telephone").toString());
            memberData.put("userEmail",
                    memberInfo.get("userEmail") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("userEmail").toString());
            memberData.put("createDt", memberInfo.get("createDt") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("createDt"));

            groupDeviceList = new ArrayList<>();
            for (MemberDevice mdi : readOnlyMapper
                    .selectMemberDeviceOne(memberInfo.get("memberIdx").toString())) {
              deviceInfo = new LinkedHashMap<>();

              deviceInfo.put("deviceIdx", Integer.parseInt(mdi.getDeviceIdx()));
              deviceInfo.put("serial",
                      mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
              deviceInfo.put("deviceType",
                      mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
              deviceInfo.put("deviceModelName",
                      mdi.getDeviceModel() == null ? CommonConstant.NULL_DATA : mdi.getDeviceModel());
              deviceInfo.put("deviceModelImgPath",
                      mdi.getDeviceModelImgPath() == null ? CommonConstant.NULL_DATA :mdi.getDeviceModelImgPath());
              deviceInfo.put("stationName",
                      mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
              deviceInfo.put("country_nm","대한민국");
              if(mdi.getDfname() == null){
                deviceInfo.put("sido_nm",CommonConstant.NULL_DATA);
                deviceInfo.put("sg_nm",CommonConstant.NULL_DATA);
                deviceInfo.put("emd_nm",CommonConstant.NULL_DATA);
              }else{
                deviceInfo.put("sido_nm",mdi.getDfname().split(" ")[0]);
                deviceInfo.put("sg_nm",mdi.getDfname().split(" ")[1]);
                deviceInfo.put("emd_nm",mdi.getDfname().split(" ")[2]);
              }

              deviceInfo.put("hang_cd",readOnlyMapper.selectDcode(mdi.getSerialNum()));
              deviceInfo.put("lat", mdi.getLat() == null || mdi.getLat().equals("")? CommonConstant.NULL_DATA
                      : Double.parseDouble(mdi.getLat()));
              deviceInfo.put("lon", mdi.getLon() == null || mdi.getLon().equals("")? CommonConstant.NULL_DATA
                      : Double.parseDouble(mdi.getLon()));

              ventDeviceList = new ArrayList<>();
              if ("IAQ".equals(mdi.getDeviceType())) {

                for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx())) {
                  ventDeviceInfo = new LinkedHashMap<>();

                  ventDeviceInfo.put("ventDeviceIdx", Integer.parseInt(v.getVentDeviceIdx()));
                  ventDeviceInfo.put("ventSerial",
                          v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());
                  ventDeviceInfo.put("deviceModel",
                          v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel());

                  ventDeviceList.add(ventDeviceInfo);
                }

                deviceInfo.put("vents", ventDeviceList);

              } else {
                deviceInfo.put("vents", new ArrayList<>());
              }

              groupDeviceList.add(deviceInfo);
            }

            memberData.put("deviceList", groupDeviceList);
            memberDatas.add(memberData);
          }

          datas.put("memberData", memberDatas);

        } catch (Exception e) {
          e.printStackTrace();
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        }

        res.put("data", datas);

        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> selectUserInfoEncodeVersion(HashMap<String, String> user)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
    List<LinkedHashMap<String, Object>> memberDatas;
    List<HashMap<String, Object>> groupDeviceList;
    List<HashMap<String, Object>> deviceList;
    List<HashMap<String, Object>> ventDeviceList;
    HashMap<String, Object> deviceInfo;
    HashMap<String, Object> ventDeviceInfo;

    int resultCode = CommonConstant.R_SUCC_CODE;

    String userId = user.get("userId");
    String userType = user.get("userType");

    switch (userType) {
      case "admin":
        memberDatas = new ArrayList<>();

        Search search = new Search();
        search.setSearchUseYn("Y");
        search.setSearchValue("");

        try {

          List<Member> memberInfos = readOnlyMapper.selectMemberList(search);
          for (Member memberInfo : memberInfos) {
            LinkedHashMap<String, Object> memberData = new LinkedHashMap<>();

            memberData.put("idx", AES256Util.encrypt(memberInfo.getIdx()+""));
            memberData.put("memberId",
                    AES256Util.encrypt(memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId()));
            memberData.put("region",
                    AES256Util.encrypt(memberInfo.getRegion() == null ? CommonConstant.NULL_DATA : memberInfo.getRegion()));
            memberData.put("regionName",
                    AES256Util.encrypt(memberInfo.getRegionName() == null ? CommonConstant.NULL_DATA
                            : memberInfo.getRegionName()));
            memberData.put("userName", AES256Util.encrypt(memberInfo.getUserName() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getUserName()));
            memberData.put("phoneNumber",
                    AES256Util.encrypt(memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                            : memberInfo.getPhoneNumber()));
            memberData.put("telephone", AES256Util.encrypt(memberInfo.getTelephone() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getTelephone()));
            memberData.put("userEmail", AES256Util.encrypt(memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getUserEmail()));
            memberData.put("groupName", AES256Util.encrypt(memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getGroupName()));
            memberData.put("createDt", AES256Util.encrypt(memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA
                    : memberInfo.getCreateDt()));

            deviceList = new ArrayList<>();
            for (MemberDevice mdi : readOnlyMapper
                    .selectMemberDeviceOne(Integer.toString(memberInfo.getIdx()))) {
              deviceInfo = new LinkedHashMap<>();

              deviceInfo.put("deviceIdx", AES256Util.encrypt(mdi.getDeviceIdx()));
              deviceInfo.put("serial",
                      AES256Util.encrypt(mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum()));
              deviceInfo.put("deviceType",
                      AES256Util.encrypt(mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType()));
              deviceInfo.put("deviceModelName",
                      AES256Util.encrypt(mdi.getDeviceModel() == null ? CommonConstant.NULL_DATA : mdi.getDeviceModel()));
              deviceInfo.put("deviceModelImgPath",
                      AES256Util.encrypt(mdi.getDeviceModelImgPath() == null ? CommonConstant.NULL_DATA :mdi.getDeviceModelImgPath()));
              deviceInfo.put("stationName",
                      AES256Util.encrypt(mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName()));
              deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null ? CommonConstant.NULL_DATA
                      : mdi.getLat()));
              deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null ? CommonConstant.NULL_DATA
                      : mdi.getLon()));

              ventDeviceList = new ArrayList<>();
              if ("IAQ".equals(mdi.getDeviceType())) {
                for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx())) {
                  ventDeviceInfo = new LinkedHashMap<>();

                  ventDeviceInfo.put("ventDeviceIdx", AES256Util.encrypt(v.getVentDeviceIdx()));
                  ventDeviceInfo.put("ventSerial",
                          AES256Util.encrypt(v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum()));
                  ventDeviceInfo.put("deviceModel",
                          AES256Util.encrypt(v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel()));
                  ventDeviceList.add(ventDeviceInfo);
                }

                deviceInfo.put("vents", ventDeviceList);

              } else {
                deviceInfo.put("vents", new ArrayList<>());
              }

              deviceList.add(deviceInfo);
            }

            memberData.put("deviceList", deviceList);
            memberDatas.add(memberData);

          }

          datas.put("memberList", memberDatas);

        } catch (Exception e) {
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        }

        res.put("data", datas);

        break;

      case "member":

        try {

          Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);

          datas.put("idx", AES256Util.encrypt(findMemberData.getIdx()+""));
          datas.put("memberId", AES256Util.encrypt(userId));
          datas.put("region", AES256Util.encrypt(findMemberData.getRegion() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getRegion()));
          datas.put("regionName", AES256Util.encrypt(findMemberData.getRegionName() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getRegionName()));
          datas.put("userName", AES256Util.encrypt(findMemberData.getUserName() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getUserName()));
          datas.put("phoneNumber",
                  AES256Util.encrypt(findMemberData.getPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : findMemberData.getPhoneNumber()));
          datas.put("telephone", AES256Util.encrypt(findMemberData.getTelephone() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getTelephone()));
          datas.put("userEmail", AES256Util.encrypt(findMemberData.getUserEmail() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getUserEmail()));
          datas.put("groupName", AES256Util.encrypt(findMemberData.getGroupName() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getGroupName()));
          datas.put("createDt", AES256Util.encrypt(findMemberData.getCreateDt() == null ? CommonConstant.NULL_DATA
                  : findMemberData.getCreateDt()));

          deviceList = new ArrayList<>();
          for (MemberDevice mdi : readOnlyMapper
                  .selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
            deviceInfo = new LinkedHashMap<>();

            deviceInfo.put("deviceIdx", AES256Util.encrypt(mdi.getDeviceIdx()));
            deviceInfo.put("serial",
                    AES256Util.encrypt(mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum()));
            deviceInfo.put("deviceType",
                    AES256Util.encrypt(mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType()));
            deviceInfo.put("deviceModelName",
                    AES256Util.encrypt(mdi.getDeviceModel() == null ? CommonConstant.NULL_DATA : mdi.getDeviceModel()));
            deviceInfo.put("deviceModelImgPath",
                    AES256Util.encrypt(mdi.getDeviceModelImgPath() == null ? CommonConstant.NULL_DATA :mdi.getDeviceModelImgPath()));
            deviceInfo.put("stationName",
                    AES256Util.encrypt(mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName()));
            deviceInfo.put("lat",
                    AES256Util.encrypt(mdi.getLat() == null ? CommonConstant.NULL_DATA : mdi.getLat()));
            deviceInfo.put("lon",
                    AES256Util.encrypt(mdi.getLon() == null ? CommonConstant.NULL_DATA : mdi.getLon()));

            ventDeviceList = new ArrayList<>();
            if ("IAQ".equals(mdi.getDeviceType())) {
              for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx())) {
                ventDeviceInfo = new LinkedHashMap<>();

                ventDeviceInfo.put("ventDeviceIdx", AES256Util.encrypt(v.getVentDeviceIdx()));
                ventDeviceInfo.put("ventSerial",
                        AES256Util.encrypt(v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum()));
                ventDeviceInfo.put("deviceModel",
                        AES256Util.encrypt(v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel()));

                ventDeviceList.add(ventDeviceInfo);
              }

              deviceInfo.put("vents", ventDeviceList);

            } else {
              deviceInfo.put("vents", new ArrayList<>());
            }

            deviceList.add(deviceInfo);
          }

          datas.put("deviceList", deviceList);

        } catch (Exception e) {
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        }

        res.put("data", datas);
        break;

      case "group":

        try {

          Group findGroupData = readOnlyMapper.findGroupByLoginId(userId);

          datas.put("groupId",AES256Util.encrypt(userId));
          datas.put("idx", AES256Util.encrypt(findGroupData.getIdx()+""));
          datas.put("groupName", AES256Util.encrypt(findGroupData.getGroupName() == null ? CommonConstant.NULL_DATA
                  : findGroupData.getGroupName()));
          datas.put("groupDepartName",
                  AES256Util.encrypt(findGroupData.getGroupDepartName() == null ? CommonConstant.NULL_DATA
                          : findGroupData.getGroupDepartName()));
          datas.put("groupEmail", AES256Util.encrypt(findGroupData.getGroupEmail() == null ? CommonConstant.NULL_DATA
                  : findGroupData.getGroupEmail()));
          datas.put("phoneNumber",
                  AES256Util.encrypt(findGroupData.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA
                          : findGroupData.getGroupPhoneNumber()));
          datas.put("telephone",
                  AES256Util.encrypt(findGroupData.getGroupTelephone() == null ? CommonConstant.NULL_DATA
                          : findGroupData.getGroupTelephone()));

          List<Map<String, Object>> memberInfos = readOnlyMapper
                  .selectGroupMembers(Integer.toString(findGroupData.getIdx()));

          memberDatas = new ArrayList<>();
          for (Map<String, Object> memberInfo : memberInfos) {
            LinkedHashMap<String, Object> memberData = new LinkedHashMap<>();

            memberData.put("memberIdx", AES256Util.encrypt(memberInfo.get("memberIdx").toString()));
            memberData.put("userId", AES256Util.encrypt(memberInfo.get("userId") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("userId").toString()));
            memberData.put("region", AES256Util.encrypt(memberInfo.get("region") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("region").toString()));
            memberData.put("regionName",
                    AES256Util.encrypt(memberInfo.get("regionName") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("regionName").toString()));
            memberData.put("userName", AES256Util.encrypt(memberInfo.get("userName") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("userName").toString()));
            memberData.put("phoneNumber",
                    AES256Util.encrypt(memberInfo.get("phoneNumber") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("phoneNumber").toString()));
            memberData.put("telephone",
                    AES256Util.encrypt(memberInfo.get("telephone") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("telephone").toString()));
            memberData.put("userEmail",
                    AES256Util.encrypt(memberInfo.get("userEmail") == null ? CommonConstant.NULL_DATA
                            : memberInfo.get("userEmail").toString()));
            memberData.put("createDt", AES256Util.encrypt(memberInfo.get("createDt") == null ? CommonConstant.NULL_DATA
                    : memberInfo.get("createDt")+""));

            groupDeviceList = new ArrayList<>();
            for (MemberDevice mdi : readOnlyMapper
                    .selectMemberDeviceOne(memberInfo.get("memberIdx").toString())) {
              deviceInfo = new LinkedHashMap<>();

              deviceInfo.put("deviceIdx", AES256Util.encrypt(mdi.getDeviceIdx()));
              deviceInfo.put("serial",
                      AES256Util.encrypt(mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum()));
              deviceInfo.put("deviceType",
                      AES256Util.encrypt(mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType()));
              deviceInfo.put("deviceModelName",
                      AES256Util.encrypt(mdi.getDeviceModel() == null ? CommonConstant.NULL_DATA : mdi.getDeviceModel()));
              deviceInfo.put("deviceModelImgPath",
                      AES256Util.encrypt(mdi.getDeviceModelImgPath() == null ? CommonConstant.NULL_DATA :mdi.getDeviceModelImgPath()));
              deviceInfo.put("stationName",
                      AES256Util.encrypt(mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName()));
              deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null ? CommonConstant.NULL_DATA
                      : mdi.getLat()));
              deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null ? CommonConstant.NULL_DATA
                      : mdi.getLon()));

              ventDeviceList = new ArrayList<>();
              if ("IAQ".equals(mdi.getDeviceType())) {

                for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx())) {
                  ventDeviceInfo = new LinkedHashMap<>();

                  ventDeviceInfo.put("ventDeviceIdx", AES256Util.encrypt(v.getVentDeviceIdx()));
                  ventDeviceInfo.put("ventSerial",
                          AES256Util.encrypt(v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum()));
                  ventDeviceInfo.put("deviceModel",
                          AES256Util.encrypt(v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel()));

                  ventDeviceList.add(ventDeviceInfo);
                }

                deviceInfo.put("vents", ventDeviceList);

              } else {
                deviceInfo.put("vents", new ArrayList<>());
              }

              groupDeviceList.add(deviceInfo);
            }

            memberData.put("deviceList", groupDeviceList);
            memberDatas.add(memberData);
          }

          datas.put("memberData", memberDatas);

        } catch (Exception e) {
          throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
        }

        res.put("data", datas);

        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> insertUserInfo(HashMap<String, String> req)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    int resultCode;

    try {

      resultCode = mapper.insertUserInfo(req);

    } catch (Exception e) {
      e.printStackTrace();
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    }

    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> updateUserInfo(HashMap<String, String> req)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    int resultCode;

    if (req.get("userType").equals("member")) {
      if (!"".equals(req.get("legacyUserPw"))) {
        Member member = new Member();

        member.setUserId(req.get("userId"));
        member.setUserPw(Sha256EncryptUtil.ShaEncoder(req.get("legacyUserPw")));

        if (readOnlyMapper.loginCheckMemberId(member) != 3) {
          throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);

        }
      }

      try {

        resultCode = mapper.updateUserInfo(req);
        memberMapper.updateMemberLoginCount(req.get("userId"), 1);

        res.put("result", resultCode);

      } catch (NullPointerException e) {
        throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);

      } catch (Exception e) {
        throw new SQLException(SQLException.SQL_EXCEPTION);
      }

    } else if (req.get("userType").equals("group")) {
      if (!"".equals(req.get("legacyUserPw"))) {
        Group group = new Group();

        group.setUserId(req.get("userId"));
        group.setGroupPw(Sha256EncryptUtil.ShaEncoder(req.get("legacyUserPw")));

        if (readOnlyMapper.loginCheckGroupId(group) != 3) {
          throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);
        }
      }

      try {

        Group groupInfo = new Group();

        groupInfo.setIdx(Integer.parseInt(readOnlyMapper.selectGroupUserIdx(req.get("userId"))));
        groupInfo.setGroupId(req.get("userId"));
        groupInfo.setGroupPw(req.get("userPw"));
        groupInfo.setGroupName(req.get("userName"));
        groupInfo.setGroupDepartName(req.get("groupDepartName"));
        groupInfo.setGroupPhoneNumber(req.get("phoneNumber"));
        groupInfo.setGroupEmail(req.get("userEmail"));
        groupInfo.setGroupTelephone(req.get("telephone"));

        resultCode = groupMapper.updateGroup(groupInfo);

        res.put("result", resultCode);

      } catch (NullPointerException e) {
        throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);

      } catch (Exception e) {
        throw new SQLException(SQLException.SQL_EXCEPTION);
      }

    } else {
      throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    if (resultCode == CommonConstant.R_FAIL_CODE) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    return res;
  }



  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> deleteUserInfo(String userId, String password)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    int resultCode;

    Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);
    String passwordEnc = Sha256EncryptUtil.ShaEncoder(password);

    if (findMemberData == null) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);

    } else if (!findMemberData.getUserPw().equals(passwordEnc)) {
      throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);

    } else {
      resultCode = memberMapper.deleteMember(Integer.toString(findMemberData.getIdx()));
      for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
        stationMapper.delMemberDevice(mdi.getDeviceIdx());

        List<Vent> vs = readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx());
        for (Vent v : vs) {
          memberDeviceMapper.deleteMemberDeviceVent(v.getVentDeviceIdx());
        }
      }

      res.put("result", resultCode);

      return res;
    }
  }

  public LinkedHashMap<String, Object> findUserId(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    List<LinkedHashMap<String, String>> datas = new LinkedList<>();
    LinkedHashMap<String, String> data;

    int resultCode = CommonConstant.R_SUCC_CODE;

    String userName = req.get("userName");
    String phoneNumber = req.get("phoneNumber");
    List<HashMap<String, String>> memberIdList = readOnlyMapper
            .findMemberIdList(userName, phoneNumber);
    List<HashMap<String, String>> groupIdList = readOnlyMapper
            .findGroupIdList(userName, phoneNumber);

    if (!memberIdList.isEmpty() && !groupIdList.isEmpty()) {
      memberIdList.addAll(groupIdList);
    } else if (memberIdList.isEmpty() && groupIdList.isEmpty()) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    for (HashMap<String, String> userInfo : memberIdList) {
      data = new LinkedHashMap<>();

      data.put("userId",
              userInfo.get("user_id") == null ? CommonConstant.NULL_DATA : userInfo.get("user_id"));
      data.put("userType",
              userInfo.get("user_type") == null ? CommonConstant.NULL_DATA : userInfo.get("user_type"));
      data.put("createDt",
              userInfo.get("create_dt") == null ? CommonConstant.NULL_DATA : userInfo.get("create_dt"));

      datas.add(data);
    }

    res.put("data", datas);
    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> findUserIdEncodeVersion(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    List<LinkedHashMap<String, String>> datas = new LinkedList<>();
    LinkedHashMap<String, String> data;

    int resultCode = CommonConstant.R_SUCC_CODE;

    String userName = req.get("userName");
    String phoneNumber = req.get("phoneNumber");
    List<HashMap<String, String>> memberIdList = readOnlyMapper
            .findMemberIdList(userName, phoneNumber);
    List<HashMap<String, String>> groupIdList = readOnlyMapper
            .findGroupIdList(userName, phoneNumber);

    if (!memberIdList.isEmpty() && !groupIdList.isEmpty()) {
      memberIdList.addAll(groupIdList);
    } else if (memberIdList.isEmpty() && groupIdList.isEmpty()) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    for (HashMap<String, String> userInfo : memberIdList) {
      data = new LinkedHashMap<>();

      data.put("userId",
              AES256Util.encrypt(userInfo.get("user_id") == null ? CommonConstant.NULL_DATA : userInfo.get("user_id")));
      data.put("userType",
              AES256Util.encrypt(userInfo.get("user_type") == null ? CommonConstant.NULL_DATA : userInfo.get("user_type")));
      data.put("createDt",
              AES256Util.encrypt(userInfo.get("create_dt") == null ? CommonConstant.NULL_DATA : userInfo.get("create_dt")));

      datas.add(data);
    }

    res.put("data", datas);
    res.put("result", resultCode);

    return res;
  }

  public LinkedHashMap<String, Object> getAdminStatisticsData() throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    HashMap<String, Object> result = new LinkedHashMap<>();
    HashMap<String, Object> statisticsData;

    int resultCode = CommonConstant.R_SUCC_CODE;

    try {

      statisticsData = readOnlyMapper.getAdminStatisticsData();

      result.put("memberCnt", statisticsData.get("member_cnt"));
      result.put("groupMemberCnt", statisticsData.get("group_member_cnt"));
      result.put("groupCnt", statisticsData.get("group_cnt"));
      result.put("iaqCnt", statisticsData.get("iaq_cnt"));
      result.put("oaqCnt", statisticsData.get("oaq_cnt"));
      result.put("ventCnt", statisticsData.get("vent_cnt"));
      result.put("memberIaqCnt", statisticsData.get("member_iaq_cnt"));
      result.put("memberOaqCnt", statisticsData.get("member_oaq_cnt"));
      result.put("memberVentCnt", statisticsData.get("member_vent_cnt"));

      res.put("data", result);
      res.put("result", resultCode);

    } catch (Exception e) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    return res;
  }
}
