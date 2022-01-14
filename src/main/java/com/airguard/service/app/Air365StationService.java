package com.airguard.service.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Vent;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;

@Service
public class Air365StationService {

  private static final Logger logger = LoggerFactory.getLogger(Air365StationService.class);

  @Autowired
  StationMapper mapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  @Autowired
  MemberDeviceMapper memberDeviceMapper;

  public LinkedHashMap<String, Object> selectStationList(String userId, String userType) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();

    Map<String, Object> deviceInfo;
    List<Map<String, Object>> ventDeviceList;
    Map<String, Object> ventDeviceInfo;

    switch (userType) {
      case "member":
        String memberIdx = readOnlyMapper.selectUserIdx(userId);
        for (MemberDevice md : readOnlyMapper.selectMemberDeviceOne(memberIdx)) {
          deviceInfo = new HashMap<String, Object>();
          deviceInfo.put("serial", md.getSerialNum() == null ? CommonConstant.NULL_DATA : md.getSerialNum());
          deviceInfo.put("deviceType", md.getDeviceType() == null ? CommonConstant.NULL_DATA : md.getDeviceType());
          deviceInfo.put("lon", md.getLon() == null ? CommonConstant.NULL_DATA : md.getLon());
          deviceInfo.put("lat", md.getLat() == null ? CommonConstant.NULL_DATA : md.getLat());

          if ("IAQ".equals(md.getDeviceType())) {
            ventDeviceList = new ArrayList<Map<String, Object>>();
            for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(md.getDeviceIdx())) {
              ventDeviceInfo = new HashMap<String, Object>();
              ventDeviceInfo.put("ventSerial", v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());
              ventDeviceInfo.put("deviceType", v.getDeviceType() == null ? CommonConstant.NULL_DATA : v.getDeviceType());
              ventDeviceList.add(ventDeviceInfo);
            }

            deviceInfo.put("vents", ventDeviceList.size() == 0 ? CommonConstant.NULL_DATA : ventDeviceList);
          }

          datas.add(deviceInfo);
        }

        res.put("memberId", userId);
        res.put("data", datas.size() == 0 ? CommonConstant.NULL_DATA : datas);
        break;

      case "group":
        String groupIdx = readOnlyMapper.getGroupIdToGroupIdx(userId);
        List<HashMap<String, Object>> groupData = new ArrayList<HashMap<String,Object>>();
        for (Map<String, Object> groupMember : readOnlyMapper.selectGroupMembers(groupIdx)) {
          datas = new ArrayList<Map<String,Object>>();
          String groupMemberIdx = groupMember.get("memberIdx").toString();
          String groupMemberId = groupMember.get("userId").toString();
          HashMap<String, Object> groupMemberInfo = new LinkedHashMap<String, Object>();

          for (MemberDevice md : readOnlyMapper.selectMemberDeviceOne(groupMemberIdx)) {
            deviceInfo = new HashMap<String, Object>();
            deviceInfo.put("serial", md.getSerialNum() == null ? CommonConstant.NULL_DATA : md.getSerialNum());
            deviceInfo.put("deviceType", md.getDeviceType() == null ? CommonConstant.NULL_DATA : md.getDeviceType());
            deviceInfo.put("lon", md.getLon() == null ? CommonConstant.NULL_DATA : md.getLon());
            deviceInfo.put("lat", md.getLat() == null ? CommonConstant.NULL_DATA : md.getLat());

            if ("IAQ".equals(md.getDeviceType())) {
              ventDeviceList = new ArrayList<Map<String, Object>>();
              for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(md.getDeviceIdx())) {
                ventDeviceInfo = new HashMap<String, Object>();
                ventDeviceInfo.put("ventSerial", v.getSerialNum() == null ? CommonConstant.NULL_DATA :v.getSerialNum());
                ventDeviceInfo.put("deviceType", v.getDeviceType() == null ? CommonConstant.NULL_DATA :v.getDeviceType());
                ventDeviceList.add(ventDeviceInfo);
              }

              deviceInfo.put("vents", ventDeviceList.size() == 0 ? CommonConstant.NULL_DATA : ventDeviceList);
            }

            datas.add(deviceInfo);
          }

          groupMemberInfo.put("memberId", groupMemberId);
          groupMemberInfo.put("memberData", datas.size() == 0 ? CommonConstant.NULL_DATA : datas);
          groupData.add(groupMemberInfo);
        }

        res.put("groupId", userId);
        res.put("data", groupData.size() == 0 ? CommonConstant.NULL_DATA : groupData);
        break;

      default:
        throw new NullPointerException();
    }

    res.put("result", 1);
    return res;
  }

  public LinkedHashMap<String, Object> insertStation(HashMap<String, Object> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    memberDeviceMapper.insertMemberDeviceApp(req);

    res.put("result", 1);
    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> deleteStation(String serial) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String deviceIdx = readOnlyMapper.getSerialToDeviceIdx(serial);

    mapper.delMemberDevice(deviceIdx);
    for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(deviceIdx))
      memberDeviceMapper.deleteMemberDeviceVent(v.getVentDeviceIdx());

    res.put("result", 1);
    return res;
  }

  public LinkedHashMap<String, Object> updateStationPosition(HashMap<String, Object> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    memberDeviceMapper.updateMemberDevice(req);

    res.put("result", 1);
    return res;
  }

  public LinkedHashMap<String, Object> insertVentConnect(HashMap<String, Object> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String memberIdx = readOnlyMapper.getUserIdToUserIdx(req.get("userId").toString());
    String deviceIdx = readOnlyMapper.getSerialToDeviceIdx(req.get("serial").toString());
    String ventDeviceIdx = readOnlyMapper.getSerialToDeviceIdx(req.get("ventSerial").toString());

    memberDeviceMapper.insertMemberDeviceVent(memberIdx, deviceIdx, ventDeviceIdx);

    res.put("result", 1);
    return res;
  }

  public LinkedHashMap<String, Object> deleteVentConnect(HashMap<String, Object> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String ventDeviceIdx = readOnlyMapper.getSerialToDeviceIdx(req.get("ventSerial").toString());
    memberDeviceMapper.deleteMemberDeviceVent(ventDeviceIdx);

    res.put("result", 1);
    return res;
  }
}
