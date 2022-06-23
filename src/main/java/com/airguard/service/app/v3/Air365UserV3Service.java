package com.airguard.service.app.v3;

import com.airguard.exception.ParameterException;
import com.airguard.exception.SQLException;
import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.mapper.main.system.GroupMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.mapper.main.system.MemberMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.common.Admin;
import com.airguard.model.common.Search;
import com.airguard.model.system.Group;
import com.airguard.model.system.Member;
import com.airguard.model.system.MemberDevice;
import com.airguard.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class Air365UserV3Service {

  private static final Logger logger = LoggerFactory.getLogger(Air365UserV3Service.class);

  @Value("${spring.profiles.active}")
  private String SERVER_PROFILE;


  @Autowired
  private FCMPushManageUtil pushUtil;

  @Autowired
  private UserMapper mapper;


  @Autowired
  private MemberMapper memberMapper;

  @Autowired
  private GroupMapper groupMapper;

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

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
                deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA
                    : Double.parseDouble(mdi.getLat()));
                deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA
                    : Double.parseDouble(mdi.getLon()));
                deviceInfo.put("country_nm", "대한민국");
                deviceInfo.put("sido_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[0]);
                deviceInfo.put("sg_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[1]);
                deviceInfo.put("emd_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[2]);
                deviceInfo.put("hang_cd", mdi.getDcode() == null ? CommonConstant.NULL_DATA
                        : mdi.getDcode());

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
              deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA
                  : Double.parseDouble(mdi.getLat()));
              deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA
                  : Double.parseDouble(mdi.getLon()));
              deviceInfo.put("country_nm", "대한민국");
              deviceInfo.put("sido_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                      : mdi.getDfname().split(" ")[0]);
              deviceInfo.put("sg_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                      : mdi.getDfname().split(" ")[1]);
              deviceInfo.put("emd_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                      : mdi.getDfname().split(" ")[2]);
              deviceInfo.put("hang_cd", mdi.getDcode() == null ? CommonConstant.NULL_DATA
                      : mdi.getDcode());

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

          }catch (RedisConnectionFailureException e) {
            //telegramMsg();
            throw new RedisConnectionFailureException("레디스 서버 접속오류.");

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

                deviceInfo.put("deviceIdx", Integer.parseInt(mdi.getDeviceIdx()));
                deviceInfo.put("serial",
                    mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
                deviceInfo.put("deviceType",
                    mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
                deviceInfo.put("stationName",
                    mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
                deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA
                    : Double.parseDouble(mdi.getLat()));
                deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA
                    : Double.parseDouble(mdi.getLon()));
                deviceInfo.put("country_nm", "대한민국");
                deviceInfo.put("sido_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[0]);
                deviceInfo.put("sg_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[1]);
                deviceInfo.put("emd_nm", mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[2]);
                deviceInfo.put("hang_cd", mdi.getDcode() == null ? CommonConstant.NULL_DATA
                        : mdi.getDcode());


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
    logger.error("======================v3 /login API  Encode Version Service ==============================");
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
                  deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null ? CommonConstant.NULL_DATA
                          : mdi.getLat()));
                  deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null ? CommonConstant.NULL_DATA
                          : mdi.getLon()));
                  deviceInfo.put("country_nm", AES256Util.encrypt("대한민국"));
                  deviceInfo.put("sido_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                          : mdi.getDfname().split(" ")[0]));
                  deviceInfo.put("sg_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                          : mdi.getDfname().split(" ")[1]));
                  deviceInfo.put("emd_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                          : mdi.getDfname().split(" ")[2]));
                  deviceInfo.put("hang_cd", AES256Util.encrypt(mdi.getDcode() == null ? CommonConstant.NULL_DATA
                          : mdi.getDcode()));

                  deviceList.add(deviceInfo);
                }

                memberData.put("deviceList", deviceList);

                memberDatas.add(memberData);
              }

              datas.put("memberList", memberDatas);

            } catch (Exception e) {
              e.printStackTrace();
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

              datas.put("idx", AES256Util.encrypt(findMemberData.getIdx() + ""));
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
                deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null ? CommonConstant.NULL_DATA
                        : mdi.getLat()));
                deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null ? CommonConstant.NULL_DATA
                        : mdi.getLon()));
                deviceInfo.put("country_nm", AES256Util.encrypt("대한민국"));
                deviceInfo.put("sido_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[0]));
                deviceInfo.put("sg_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[1]));
                deviceInfo.put("emd_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                        : mdi.getDfname().split(" ")[2]));
                deviceInfo.put("hang_cd", AES256Util.encrypt(mdi.getDcode() == null ? CommonConstant.NULL_DATA
                        : mdi.getDcode()));

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

            }catch (RedisConnectionFailureException e) {
              //telegramMsg();
              logger.error("EXCEPTION ============{}",e.getMessage(),e);
              throw new RedisConnectionFailureException("레디스 서버 접속오류.테스트입니다.");

            }catch (Exception e) {
              e.printStackTrace();
              throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
            }

          } else if (checkCode == 4) {
            logger.error("===============v3 /login Encode Version Service ===member===checkCode 4");
            throw new SQLException(SQLException.USER_LOGIN_ROCK_EXCEPTION);

          } else if (checkCode == 5) {
            logger.error("===============v3 /login Encode Version Service ===member===checkCode 5");
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

              res.put("idx", AES256Util.encrypt(findGroupData.getIdx() + ""));
              datas.put("idx", AES256Util.encrypt(findGroupData.getIdx() + ""));
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
                  deviceInfo.put("lat", AES256Util.encrypt(mdi.getLat() == null ? CommonConstant.NULL_DATA
                          : mdi.getLat()));
                  deviceInfo.put("lon", AES256Util.encrypt(mdi.getLon() == null ? CommonConstant.NULL_DATA
                          : mdi.getLon()));
                  deviceInfo.put("country_nm", AES256Util.encrypt("대한민국"));
                  deviceInfo.put("sido_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                          : mdi.getDfname().split(" ")[0]));
                  deviceInfo.put("sg_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                          : mdi.getDfname().split(" ")[1]));
                  deviceInfo.put("emd_nm", AES256Util.encrypt(mdi.getDfname() == null ? CommonConstant.NULL_DATA
                          : mdi.getDfname().split(" ")[2]));
                  deviceInfo.put("hang_cd", AES256Util.encrypt(mdi.getDcode() == null ? CommonConstant.NULL_DATA
                          : mdi.getDcode()));

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
            logger.error("===============v3 /login Encode Version Service ===group===checkCode 5");
            throw new SQLException(SQLException.NOT_USE_DATA_EXCEPION);
          }

          break;

        default:
          logger.error("===============v3 /login Encode Version Service ===switch default parameter error ");
          throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
      }

      if (checkCode == 1 || checkCode == 2) {
        logger.error("===============v3 /login Encode Version Service ===check Code :: {} ",checkCode);
        throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);

      } else {
        if (!"".equals(token)) {
          String cusUserId = "group".equals(userType) ? "g-".concat(userId) : userId;
          logger.error( "insert token into Redis Server. cusUserId : {}, token : {}", cusUserId, token);
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

  public void telegramMsg() {
    String telegram_msg = URLEncoder.encode(CommonConstant.TELEGRAM_MSG);
    URI uri = URI.create(new StringBuilder(CommonConstant.TELEGRAM_API)
            .append("?chat_id=").append(CommonConstant.CHAT_ID)
            .append("&text=").append(telegram_msg).toString());

    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(10 * 1000);
    factory.setReadTimeout(30 * 1000);
    RestTemplate restTemplate = new RestTemplate(factory);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", new StringBuilder(MediaType.APPLICATION_JSON_VALUE).append(";charset=UTF-8").toString());

    try {
      RequestEntity<Object> req = new RequestEntity<>(headers, HttpMethod.GET, uri);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);
      logger.error("res ={}",res.getBody());
    }catch (Exception e){
      e.printStackTrace();
    }



  }


}
