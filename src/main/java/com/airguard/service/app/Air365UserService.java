package com.airguard.service.app;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.mapper.main.system.MemberMapper;
import com.airguard.model.common.Admin;
import com.airguard.model.common.Search;
import com.airguard.model.system.Group;
import com.airguard.model.system.Member;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Vent;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.airguard.util.RedisManageUtil;
import com.airguard.util.RestApiCookieManageUtil;
import com.airguard.util.Sha256EncryptUtil;

@Service
public class Air365UserService {

  private static final Logger logger = LoggerFactory.getLogger(Air365UserService.class);

  @Value("${spring.profiles.active}")
  private String SERVER_PROFILE;

  @Autowired
  RedisManageUtil redisUtil;

  @Autowired
  UserMapper mapper;

  @Autowired
  StationMapper stationMapper;

  @Autowired
  MemberMapper memberMapper;

  @Autowired
  MemberDeviceMapper memberDeviceMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public String getUserIdToGroupId(String userId) {
    return readOnlyMapper.getUserIdToGroupId(userId);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> login(HashMap<String, String> user, HttpServletResponse response) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = user.get("userId");
    String password = Sha256EncryptUtil.ShaEncoder(user.get("password"));
    String userType = user.get("userType");
    String token = user.get("token");

    int checkCode = 0;

    LinkedHashMap<String, Object> datas = new LinkedHashMap<String, Object>();

    switch (userType) {
      case "admin":
        Admin admin = new Admin();
        admin.setUserId(userId);
        admin.setUserPw(password);
        checkCode = readOnlyMapper.loginCheckAdminId(admin);

        if (checkCode == 3) {
          List<LinkedHashMap<String, Object>> memberDatas = new ArrayList<LinkedHashMap<String,Object>>();

          Search search = new Search();
          search.setSearchUseYn("Y");
          search.setSearchValue("");
          for (Member memberInfo : readOnlyMapper.selectMemberList(search)) {
            LinkedHashMap<String, Object> memberData = new LinkedHashMap<String, Object>();
            memberData.put("memberId", memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
            memberData.put("region", memberInfo.getRegion() == null ? CommonConstant.NULL_DATA : memberInfo.getRegion());
            memberData.put("regionName", memberInfo.getRegionName() == null ? CommonConstant.NULL_DATA :memberInfo.getRegionName());
            memberData.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA :memberInfo.getUserName());
            memberData.put("phoneNumber", memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA : memberInfo.getPhoneNumber());
            memberData.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA : memberInfo.getUserEmail());
            memberData.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA : memberInfo.getGroupName());

            List<HashMap<String, Object>> deviceList = new ArrayList<HashMap<String,Object>>();
            for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(Integer.toString(memberInfo.getIdx()))) {
              HashMap<String, Object> deviceInfo = new LinkedHashMap<String, Object>();

              deviceInfo.put("serial", mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
              deviceInfo.put("deviceType", mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
              deviceInfo.put("stationName", mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
              deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
              deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

              deviceList.add(deviceInfo);
            }

            memberData.put("deviceList", deviceList.size() == 0 ? CommonConstant.NULL_DATA : deviceList);
            memberDatas.add(memberData);
          }

          datas.put("memberList", memberDatas.size() == 0 ? CommonConstant.NULL_DATA : memberDatas);
          res.put("data", datas);
        }

        break;
      case "member":
        Member member = new Member();
        member.setUserId(userId);
        member.setUserPw(password);
        checkCode = readOnlyMapper.loginCheckMemberId(member);
        if (checkCode == 3) {
          Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);
          datas.put("memberId", userId);
          datas.put("region", findMemberData.getRegion() == null ? CommonConstant.NULL_DATA : findMemberData.getRegion());
          datas.put("regionName", findMemberData.getRegionName() == null ? CommonConstant.NULL_DATA : findMemberData.getRegionName());
          datas.put("userName", findMemberData.getUserName() == null ? CommonConstant.NULL_DATA : findMemberData.getUserName());
          datas.put("phoneNumber", findMemberData.getPhoneNumber() == null ? CommonConstant.NULL_DATA : findMemberData.getPhoneNumber());
          datas.put("userEmail", findMemberData.getUserEmail() == null ? CommonConstant.NULL_DATA : findMemberData.getUserEmail());
          datas.put("groupName", findMemberData.getGroupName() == null ? CommonConstant.NULL_DATA : findMemberData.getGroupName());

          List<HashMap<String, Object>> deviceList = new ArrayList<HashMap<String,Object>>();
          for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
            HashMap<String, Object> deviceInfo = new LinkedHashMap<String, Object>();

            deviceInfo.put("serial", mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
            deviceInfo.put("deviceType", mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
            deviceInfo.put("stationName", mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
            deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
            deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

            deviceList.add(deviceInfo);
          }

          datas.put("deviceList", deviceList.size() == 0 ? CommonConstant.NULL_DATA : deviceList);
          res.put("data", datas);
        }

        break;

      case "group":
        Group group = new Group();
        group.setGroupId(userId);
        group.setGroupPw(password);
        checkCode = readOnlyMapper.loginCheckGroupId(group);
        if (checkCode == 3) {

          List<LinkedHashMap<String, Object>> memberDatas = new ArrayList<LinkedHashMap<String,Object>>();
          Group findGroupData = readOnlyMapper.findGroupByLoginId(userId);
          datas.put("groupId", userId);
          datas.put("groupName", findGroupData.getGroupName());

          for (Map<String, Object> memberInfo : readOnlyMapper.selectGroupMembers(Integer.toString(findGroupData.getIdx()))) {
            LinkedHashMap<String, Object> memberData = new LinkedHashMap<String, Object>();
            memberData.put("memberId", memberInfo.get("userId") == null ? CommonConstant.NULL_DATA : memberInfo.get("userId").toString());
            memberData.put("region", memberInfo.get("region") == null ? CommonConstant.NULL_DATA : memberInfo.get("region").toString());
            memberData.put("regionName", memberInfo.get("regionName") == null ? CommonConstant.NULL_DATA : memberInfo.get("regionName").toString());
            memberData.put("userName", memberInfo.get("userName") == null ? CommonConstant.NULL_DATA : memberInfo.get("userName").toString());
            memberData.put("phoneNumber", memberInfo.get("phoneNumber") == null ? CommonConstant.NULL_DATA : memberInfo.get("phoneNumber").toString());
            memberData.put("userEmail", memberInfo.get("userEmail") == null ? CommonConstant.NULL_DATA : memberInfo.get("userEmail").toString());

            List<HashMap<String, Object>> deviceList = new ArrayList<HashMap<String,Object>>();
            for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(memberInfo.get("memberIdx").toString())) {
              HashMap<String, Object> deviceInfo = new LinkedHashMap<String, Object>();

              deviceInfo.put("serial", mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
              deviceInfo.put("deviceType", mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
              deviceInfo.put("stationName", mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
              deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
              deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

              deviceList.add(deviceInfo);
            }

            memberData.put("deviceList", deviceList.size() == 0 ? CommonConstant.NULL_DATA : deviceList);
            memberDatas.add(memberData);
          }

          datas.put("memberList", memberDatas.size() == 0 ? CommonConstant.NULL_DATA : memberDatas);
          res.put("data", datas);
        }

        break;

      default:
        throw new NullPointerException();
    }

    if (checkCode == 1) {
      res.put("checkCode", checkCode);
      res.put("message", "check user ID .");
      res.put("result", 0);

    } else if (checkCode == 2) {
      res.put("checkCode", checkCode);
      res.put("message", "check user ID & PWD .");
      res.put("result", 0);

    } else {

      if (!"".equals(token))
        mapper.insertFcmTokenInfo(user);

      RestApiCookieManageUtil.makeAuthCookie(SERVER_PROFILE, userId, userType, response);

      res.put("result", 1);
    }

    return res;
  }

  public LinkedHashMap<String, Object> appLogOut(HashMap<String, String> req, HttpServletResponse res) throws Exception {
    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
    int resultCode = 1;
    String token = req.get("token");

    if (!"".equals(token)) {
      resultCode = mapper.deleteFcmTokenInfo(req);
      for (String key : redisUtil.getRedisDatas(token)) 
        redisUtil.delRedisData(key);
    }

    RestApiCookieManageUtil.deleteCookie(res);

    result.put("result", resultCode);

    return result;
  }

  private static String getTagValue(String tag, Element eElement) {
    NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = (Node) nlList.item(0);
    if (nValue == null)
      return null;
    return nValue.getNodeValue();
  }

  public LinkedHashMap<String, Object> findRegionInfo(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();

    Map<String, Object> data;
    HttpPost post = new HttpPost("https://was.kweather.co.kr/kapi/airguardk/getXML_lonlat_new.php");
    String resData = "";
    List<NameValuePair> urlParameters = new ArrayList<>();
    urlParameters.add(new BasicNameValuePair("lon", req.get("lon")));
    urlParameters.add(new BasicNameValuePair("lat", req.get("lat")));

    post.setEntity(new UrlEncodedFormEntity(urlParameters));

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post)) {
      resData = EntityUtils.toString(response.getEntity());

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(resData.toString())));

      NodeList nList = doc.getElementsByTagName("srch_pt");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        data = new LinkedHashMap<String, Object>();
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          data.put("region", getTagValue("region_id", eElement));
          data.put("regionName", getTagValue("city_ko", eElement) + " " + getTagValue("dong_ko", eElement));
        }

        datas.add(data);
      }

      httpClient.close();
    }

    res.put("result", 1);
    res.put("data", datas);
    return res;
  }

  public LinkedHashMap<String, Object> checkUserId(HashMap<String, String> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    Map<String, Object> data = new HashMap<String, Object>();

    String userId = req.get("userId");
    String userType = req.get("userType");

    int checkResult;

    switch (userType) {
      case "member":
        checkResult = readOnlyMapper.checkUserId(userId);
        if (checkResult == 1) 
          data.put("checkCode", 1);
        else
          data.put("checkCode", 0);

        break;
      case "group":
        checkResult = readOnlyMapper.checkGroupId(userId);
        if (checkResult == 0) 
          data.put("checkCode", 1);
        else
          data.put("checkCode", 0);
        break;
      default:
        throw new NullPointerException();
    }

    res.put("result", 1);
    res.put("data", data);
    return res;
  }

  public LinkedHashMap<String, Object> sendSMSData (HashMap<String, String> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 0;

    Random random = new Random();
    String randomNumber = 
        Integer.toString(random.nextInt(9)).concat(
        Integer.toString(random.nextInt(9))).concat(
        Integer.toString(random.nextInt(9))).concat(
        Integer.toString(random.nextInt(9)));

    HttpPost post = new HttpPost(CommonConstant.SMS_SERVER_URL + CommonConstant.SMS_SERVER_API_NAME);
    List<NameValuePair> urlParameters = new ArrayList<>();
    urlParameters.add(new BasicNameValuePair("key", "air365_service"));
    urlParameters.add(new BasicNameValuePair("sendPhone", "023602200"));
    urlParameters.add(new BasicNameValuePair("receiveName", "air365_customer"));
    urlParameters.add(new BasicNameValuePair("receivePhone", req.get("phoneNumber")));
    urlParameters.add(new BasicNameValuePair("message", URLEncoder.encode("[AIR365] 인증번호는 [".concat(randomNumber).concat("]입니다."))));

    try {

      CloseableHttpClient httpClient = HttpClients.createDefault();
      post.setEntity(new UrlEncodedFormEntity(urlParameters));
      httpClient.execute(post);

      resultCode = 1;
      httpClient.close();

    } catch (Exception e) {
      logger.error("=== SMS Send message ERROR {} ===", e.getMessage());
      resultCode = 0;
    }

    res.put("random", randomNumber);
    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> selectUserInfoAll(HashMap<String, String> user) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<>();
    List<LinkedHashMap<String, Object>> memberDatas;
    int resultCode = 0;

    String userType = user.get("userType");

    Search search = new Search();
    search.setSearchUseYn("Y");
    search.setSearchValue("");

    List<LinkedHashMap<String, Object>> memberUsers = new ArrayList<>();
    List<LinkedHashMap<String, Object>> groupUsers = new ArrayList<>();
    switch (userType) {
      case "all":
        for (Member memberInfo : readOnlyMapper.selectMemberList(search)) {
          LinkedHashMap<String, Object> memberUser = new LinkedHashMap<String, Object>();
          memberUser.put("id", memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
          memberUser.put("type", "member");
          memberUser.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA : memberInfo.getGroupName());
          memberUser.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA : memberInfo.getUserName());
          memberUser.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA : memberInfo.getUserEmail());
          memberUser.put("phoneNumber", memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA : memberInfo.getPhoneNumber());
          memberUser.put("createDt", memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : memberInfo.getCreateDt());
          memberUsers.add(memberUser);
        }
        for (Group groupInfo : readOnlyMapper.findGroupList()) {
          LinkedHashMap<String, Object> groupUser = new LinkedHashMap<String, Object>();
          groupUser.put("id", groupInfo.getGroupId() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupId());
          groupUser.put("type", "group");
          groupUser.put("groupName", groupInfo.getGroupName() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupName());
          groupUser.put("userName", "NA");
          groupUser.put("userEmail", groupInfo.getGroupEmail() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupEmail());
          groupUser.put("phoneNumber", groupInfo.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupPhoneNumber());
          groupUser.put("createDt", groupInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : groupInfo.getCreateDt());
          groupUsers.add(groupUser);
        }
        datas.put("memberUsers", memberUsers.size() == 0 ? CommonConstant.NULL_DATA : memberUsers);
        datas.put("groupUsers", groupUsers.size() == 0 ? CommonConstant.NULL_DATA : groupUsers);
        res.put("data", datas);
        resultCode = 1;
        break;
      case "group":
        for (Group groupInfo : readOnlyMapper.findGroupList()) {
          LinkedHashMap<String, Object> groupUser = new LinkedHashMap<String, Object>();
          groupUser.put("id", groupInfo.getGroupId() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupId());
          groupUser.put("type", "group");
          groupUser.put("groupName", groupInfo.getGroupName() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupName());
          groupUser.put("userName", " ");
          groupUser.put("userEmail", groupInfo.getGroupEmail() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupEmail());
          groupUser.put("phoneNumber", groupInfo.getGroupPhoneNumber() == null ? CommonConstant.NULL_DATA : groupInfo.getGroupPhoneNumber());
          groupUser.put("createDt", groupInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : groupInfo.getCreateDt());
          groupUsers.add(groupUser);
        }
        datas.put("groupUsers", groupUsers.size() == 0 ? CommonConstant.NULL_DATA : groupUsers);
        res.put("data", datas);
        resultCode = 1;
        break;
      case "member":
        for (Member memberInfo : readOnlyMapper.selectMemberList(search)) {
          LinkedHashMap<String, Object> memberUser = new LinkedHashMap<String, Object>();
          memberUser.put("id", memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
          memberUser.put("type", "member");
          memberUser.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA : memberInfo.getGroupName());
          memberUser.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA : memberInfo.getUserName());
          memberUser.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA : memberInfo.getUserEmail());
          memberUser.put("phoneNumber", memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA : memberInfo.getPhoneNumber());
          memberUser.put("createDt", memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : memberInfo.getCreateDt());
          memberUsers.add(memberUser);
        }
        datas.put("memberUsers", memberUsers.size() == 0 ? CommonConstant.NULL_DATA : memberUsers);
        res.put("data", datas);
        resultCode = 1;
        break;

    }
    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> selectUserInfo(HashMap<String, String> user) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> datas = new LinkedHashMap<String, Object>();
    List<LinkedHashMap<String, Object>> memberDatas;
    int resultCode = 0;

    String userId = user.get("userId");
    String userType = user.get("userType");

    switch (userType) {
      case "admin":
        memberDatas = new ArrayList<LinkedHashMap<String,Object>>();

        Search search = new Search();
        search.setSearchUseYn("Y");
        search.setSearchValue("");
        for (Member memberInfo : readOnlyMapper.selectMemberList(search)) {
          LinkedHashMap<String, Object> memberData = new LinkedHashMap<String, Object>();
          memberData.put("memberId", memberInfo.getUserId() == null ? CommonConstant.NULL_DATA : memberInfo.getUserId());
          memberData.put("region", memberInfo.getRegion() == null ? CommonConstant.NULL_DATA : memberInfo.getRegion());
          memberData.put("regionName", memberInfo.getRegionName() == null ? CommonConstant.NULL_DATA :memberInfo.getRegionName());
          memberData.put("userName", memberInfo.getUserName() == null ? CommonConstant.NULL_DATA :memberInfo.getUserName());
          memberData.put("phoneNumber", memberInfo.getPhoneNumber() == null ? CommonConstant.NULL_DATA : memberInfo.getPhoneNumber());
          memberData.put("userEmail", memberInfo.getUserEmail() == null ? CommonConstant.NULL_DATA : memberInfo.getUserEmail());
          memberData.put("groupName", memberInfo.getGroupName() == null ? CommonConstant.NULL_DATA : memberInfo.getGroupName());
          memberData.put("createDt",  memberInfo.getCreateDt() == null ? CommonConstant.NULL_DATA : memberInfo.getCreateDt());

          List<HashMap<String, Object>> deviceList = new ArrayList<HashMap<String,Object>>();
          for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(Integer.toString(memberInfo.getIdx()))) {
            HashMap<String, Object> deviceInfo = new LinkedHashMap<String, Object>();

            deviceInfo.put("serial", mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
            deviceInfo.put("deviceType", mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
            deviceInfo.put("stationName", mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
            deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
            deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

            deviceList.add(deviceInfo);
          }

          memberData.put("deviceList", deviceList.size() == 0 ? CommonConstant.NULL_DATA : deviceList);
          memberDatas.add(memberData);
        }

        datas.put("memberList", memberDatas.size() == 0 ? CommonConstant.NULL_DATA : memberDatas);
        res.put("data", datas);
        break;
      case "member":
        Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);
        datas.put("memberId", userId);
        datas.put("region", findMemberData.getRegion() == null ? CommonConstant.NULL_DATA : findMemberData.getRegion());
        datas.put("regionName", findMemberData.getRegionName() == null ? CommonConstant.NULL_DATA : findMemberData.getRegionName());
        datas.put("userName", findMemberData.getUserName() == null ? CommonConstant.NULL_DATA : findMemberData.getUserName());
        datas.put("phoneNumber", findMemberData.getPhoneNumber() == null ? CommonConstant.NULL_DATA : findMemberData.getPhoneNumber());
        datas.put("userEmail", findMemberData.getUserEmail() == null ? CommonConstant.NULL_DATA : findMemberData.getUserEmail());
        datas.put("groupName", findMemberData.getGroupName() == null ? CommonConstant.NULL_DATA : findMemberData.getGroupName());
        datas.put("createDt",  findMemberData.getCreateDt() == null ? CommonConstant.NULL_DATA : findMemberData.getCreateDt());

        List<HashMap<String, Object>> deviceList = new ArrayList<HashMap<String,Object>>();
        for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
          HashMap<String, Object> deviceInfo = new LinkedHashMap<String, Object>();

          deviceInfo.put("serial", mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
          deviceInfo.put("deviceType", mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
          deviceInfo.put("stationName", mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
          deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
          deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

          deviceList.add(deviceInfo);
        }

        datas.put("deviceList", deviceList.size() == 0 ? CommonConstant.NULL_DATA : deviceList);
        res.put("data", datas);

        break;

      case "group":
        memberDatas = new ArrayList<LinkedHashMap<String,Object>>();
        Group findGroupData = readOnlyMapper.findGroupByLoginId(userId);
        datas.put("groupId", userId);
        datas.put("groupName", findGroupData.getGroupName());

        for (Map<String, Object> memberInfo : readOnlyMapper.selectGroupMembers(Integer.toString(findGroupData.getIdx()))) {
          LinkedHashMap<String, Object> memberData = new LinkedHashMap<String, Object>();
          memberData.put("memberId", memberInfo.get("userId") == null ? CommonConstant.NULL_DATA : memberInfo.get("userId").toString());
          memberData.put("region", memberInfo.get("region") == null ? CommonConstant.NULL_DATA : memberInfo.get("region").toString());
          memberData.put("regionName", memberInfo.get("regionName") == null ? CommonConstant.NULL_DATA : memberInfo.get("regionName").toString());
          memberData.put("userName", memberInfo.get("userName") == null ? CommonConstant.NULL_DATA : memberInfo.get("userName").toString());
          memberData.put("phoneNumber", memberInfo.get("phoneNumber") == null ? CommonConstant.NULL_DATA : memberInfo.get("phoneNumber").toString());
          memberData.put("userEmail", memberInfo.get("userEmail") == null ? CommonConstant.NULL_DATA : memberInfo.get("userEmail").toString());
          memberData.put("createDt",  memberInfo.get("createDt") == null ? CommonConstant.NULL_DATA : memberInfo.get("createDt"));

          List<HashMap<String, Object>> groupDeviceList = new ArrayList<HashMap<String,Object>>();
          for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(memberInfo.get("memberIdx").toString())) {
            HashMap<String, Object> deviceInfo = new LinkedHashMap<String, Object>();

            deviceInfo.put("serial", mdi.getSerialNum() == null ? CommonConstant.NULL_DATA : mdi.getSerialNum());
            deviceInfo.put("deviceType", mdi.getDeviceType() == null ? CommonConstant.NULL_DATA : mdi.getDeviceType());
            deviceInfo.put("stationName", mdi.getStationName() == null ? CommonConstant.NULL_DATA : mdi.getStationName());
            deviceInfo.put("lat", mdi.getLat() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLat()));
            deviceInfo.put("lon", mdi.getLon() == null ? CommonConstant.NULL_DATA : Double.parseDouble(mdi.getLon()));

            groupDeviceList.add(deviceInfo);
          }

          memberData.put("deviceList", groupDeviceList.size() == 0 ? CommonConstant.NULL_DATA : groupDeviceList);
          memberDatas.add(memberData);
        }

        datas.put("memberList", memberDatas.size() == 0 ? CommonConstant.NULL_DATA : memberDatas);
        res.put("data", datas);

        break;

      default:
        throw new NullPointerException();
    }

    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> insertUserInfo(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 0;
    resultCode = mapper.insertUserInfo(req);

    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> updateUserInfo(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 0;
    resultCode = mapper.updateUserInfo(req);

    res.put("result", resultCode);
    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> deleteUserInfo(String userId) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 0;
    Member findMemberData = readOnlyMapper.findMemberByLoginId(userId);

    resultCode = memberMapper.deleteMember(Integer.toString(findMemberData.getIdx()));

    for (MemberDevice mdi : readOnlyMapper.selectMemberDeviceOne(Integer.toString(findMemberData.getIdx()))) {
      stationMapper.delMemberDevice(mdi.getDeviceIdx());
      for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(mdi.getDeviceIdx()))
        memberDeviceMapper.deleteMemberDeviceVent(v.getVentDeviceIdx());
    }

    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> findUserId(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    List<LinkedHashMap<String, String>> datas = new LinkedList<LinkedHashMap<String,String>>();
    LinkedHashMap<String, String> data;

    int resultCode = 1;

    String userName = req.get("userName");
    String phoneNumber = req.get("phoneNumber");

    List<HashMap<String, String>> memberIdList = readOnlyMapper.findMemberIdList(userName, phoneNumber);
    List<HashMap<String, String>> groupIdList = readOnlyMapper.findGroupIdList(userName, phoneNumber);

    if (!memberIdList.isEmpty() && !groupIdList.isEmpty()) 
      memberIdList.addAll(groupIdList);

    for (HashMap<String, String> userInfo : memberIdList) {
      data = new LinkedHashMap<String, String>();
      data.put("userId", userInfo.get("user_id") == null ? CommonConstant.NULL_DATA : userInfo.get("user_id"));
      data.put("userType", userInfo.get("user_type") == null ? CommonConstant.NULL_DATA : userInfo.get("user_type"));
      data.put("createDt", userInfo.get("create_dt") == null ? CommonConstant.NULL_DATA : userInfo.get("create_dt"));

      datas.add(data);
    }
    
    res.put("data", datas);
    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> getAdminStatisticsData() throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    HashMap<String, Object> result = new LinkedHashMap<String, Object>();
    HashMap<String, Object> statisticsData;
    int resultCode = 1;

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
    return res;
  }
}
