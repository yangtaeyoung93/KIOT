package com.airguard.mapper.readonly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airguard.model.dong.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.app.AppGroupDid;
import com.airguard.model.app.AppStation;
import com.airguard.model.app.AppUser;
import com.airguard.model.app.AppVent;
import com.airguard.model.app.ResponseLoginModel;
import com.airguard.model.common.Admin;
import com.airguard.model.common.Search;
import com.airguard.model.system.Device;
import com.airguard.model.system.DeviceAttribute;
import com.airguard.model.system.DeviceElements;
import com.airguard.model.system.DeviceModel;
import com.airguard.model.system.DeviceType;
import com.airguard.model.system.FileVo;
import com.airguard.model.system.Group;
import com.airguard.model.system.GroupDid;
import com.airguard.model.system.Member;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Menu;
import com.airguard.model.system.PushMessage;
import com.airguard.model.system.Space;
import com.airguard.model.system.Vent;

@Mapper
public interface ReadOnlyMapper {

  // Admin
  Admin findAdminByLoginId(@Param("userId") String userId);

  List<Admin> selectAdminList(Search search);

  Admin selectAdminOne(String idx);

  int checkAdminUserId(String userId);

  // DeviceAttributes
  List<DeviceAttribute> selectDeviceAttributeAll();

  List<DeviceAttribute> selectDeviceAttribute();

  List<DeviceAttribute> selectDeviceAttributeVent();

  // DeviceElements
  List<DeviceElements> selectDeviceElements();

  List<DeviceElements> selectDeviceElementsVent();

  List<DeviceElements> selectDeviceElementsAll();

  // Device
  List<Device> selectDeviceList(Search search);

  Device selectDeviceOne(String idx);

  int checkSerialNum(String serialNum);

  int deleteDeviceMemberCheck(String deviceIdx);

  int deleteDeviceVentCheck(String deviceIdx);

  int deleteDeviceVentCheck2(String deviceidx);

  // DeviceModel
  List<DeviceModel> selectDeviceModel(Search search);

  DeviceModel selectDeviceModelOne(String idx);

  List<DeviceModel> selectTypeNameDeviceModel(String deviceType);

  List<DeviceAttribute> selectOneDeviceModelAttribute(String idx);

  List<DeviceAttribute> selectOneDeviceModelAttributeVent(String idx);

  List<DeviceElements> selectOneDeviceModelElements(String idx);

  List<DeviceElements> selectOneDeviceModelElementsVent(String idx);

  int checkDeviceModel(@Param("deviceModel") String deviceModel,
      @Param("deviceTypeIdx") String deviceTypeIdx);

  int deleteDeviceModelCheck(String deviceModeIdx);

  // DeviceType
  List<DeviceType> selectCategoryList(String useYn);

  int checkDeviceType(String deviceType);

  int checkDeviceTypeName(String deviceTypeName);

  // Group
  Group findGroupByLoginId(@Param("groupId") String groupId);

  List<Group> findGroupList();

  List<Group> selectGroupList(Search search);

  List<Group> selectGroupCustomList(String searchValue);

  Group selectGroupOne(String idx);

  List<Map<String, Object>> selectGroupMembers(String idx);

  List<Group> selectGroupMemberList();

  int checkGroupId(String groupId);

  int deleteGroupDidCheck(String groupIdx);

  int deleteGroupMemberCheck(String groupIdx);

  // GroupDid
  List<GroupDid> selectGroupDidList(Search search);

  List<GroupDid> selectGroupDidOne(String idx);

  GroupDid groupDidMemberCheck(GroupDid groupDid);

  List<GroupDid> selectInsertGroupList();

  List<GroupDid> selectInsertGroupMemberList(String idx);

  List<GroupDid> selectGroupDidMemberList(String idx);

  int checkDidCode(String didCode);

  // Member
  Member findMemberByLoginId(@Param("userId") String userId);

  int loginCheckMemberId(Member member);

  int loginCheckGroupId(Group group);

  int loginCheckAdminId(Admin admin);

  List<Member> selectMemberList(Search search);

  Member selectMemberOne(String idx);

  List<Member> selectMemberAppDevice(String idx);

  int checkUserId(String userId);

  int memberDeviceCheck(@Param("memberIdx") String memberIdx, @Param("deviceIdx") String deviceIdx);

  int deleteMemberGroupCheck(String memberidx);

  // MemberDevice
  List<MemberDevice> selectMemberDeviceList(Search search);

  List<HashMap<String, Object>> selectMemberDeviceListAi365();

  List<MemberDevice> selectMemberDeviceOne(String idx);

  MemberDevice selectMemberDeviceCnt(String idx);

  List<MemberDevice> selectDeviceTypeList();

  List<MemberDevice> selectDeviceModelList(String idx);

  List<MemberDevice> selectDeviceSerialList(String idx);

  List<MemberDevice> selectDeviceVentList();

  List<Space> selectSpaceList(String idx);

  List<Space> selectParantSpaceList(String idx);

  List<Vent> selectMemberDeviceVentOne(String idx);
  int checkStationName(@Param("memberIdx") String memberIdx,
      @Param("stationName") String stationName);

  String selectVentSerialIdx(String serial);

  FileVo selectFileInfo(@Param("deviceIdx") String deviceIdx, @Param("fileType")String fileType);

  // Menu
  List<Menu> selectMenuList(String str);

  List<Menu> selectHighRankMenuList();

  List<Menu> selectMenuAuthList(String str);

  Menu selectMenuOne(String idx);

  List<Admin> selectAdminMenuLIst();

  List<Menu> selectAdminMenuOne(String adminIdx);

  // Space
  List<Space> selectSpaceSearchList(Search search);

  int selectLowSpaceAuth(String idx);

  List<Space> selectHighSpace(String search);

  int checkSpaceName(Space space);

  // App, Station
  int stationNameCheck(@Param("stationName") String stationName,
      @Param("memberIdx") String memberIdx);

  AppStation selectDeviceIdxs(AppStation station);

  AppStation selectInfoSave(String oldDeviceIdx);

  AppStation getStationInfoOne(AppStation station);

  List<AppStation> getStationInfoList();

  String getDeviceType(String serial);

  Device getDeviceInfoBySerial(String serial);

  AppStation getStationInfo(String serial);

  List<AppGroupDid> getGroupDid(String groupNo);

  // App, User
  List<AppUser> selectUserList(String userId);

  AppUser findByUsername(String userId);

  int userIdCheck(String userId);

  int serialCheck(String serialNum);

  int userStationNameCheck(@Param("stationName") String stationName,
      @Param("userId") String userId);

  int userMatchCheck(AppUser user);

  int groupUserMatchCheck(AppUser user);

  AppUser userSerialMatchCheck(AppUser user);

  String selectUserIdx(String userId);

  String selectGroupUserIdx(String userId);

  ResponseLoginModel selectResLoginInfo(String userId);

  String findId(String serialNum);

  int findPw(AppUser appUser);

  // App, Vent
  AppVent selectObjectIdxs(AppVent vent);

  Integer selectVentConnectCheck(String ventIdx);

  String ventForIaq(String ventSerial);

  String getVentAiMode(String ventSerial);

  String getUserIdToGroupId(String userId);

  // Dong Data
  List<Dong> selectDongOaqList();

  List<SiData> siList();

  List<GuData> guList(SearchDong search);

  List<DongData> dongList(@Param("search") SearchDong search, @Param("isCode") Integer isCode);

  List<Dong> selectDongSearch(SearchDong search);

  List<Dong> selectSiGunGuSearch(SearchDong search);

  List<DongGeo> selectDongList();

  List<AirGeo> selectAirEquiList();

  List<OaqGeo> selectOaqEquiList();

  List<AirCity> selectAirEquiCnt(String value);

  List<OaqCity> selectOaqEquiCnt();

  List<AirEqui> refAirEquiSearch(SearchDong search);

  List<OaqEqui> refOaqEquiSearch(SearchDong search);

  List<Dong> selectDongDataList();

  List<OaqGeo> selectDongPmList(String deviceType);

  List<OaqGeo> refOaqEquiPmSearch(String serialNum);

  List<AirGeo> refAirEquiPmSearch(String serialNum);

  List<AirGeoInfo> selectOaqGeo(@Param("dCode") String dCode);

  List<HashMap<String, Object>> selectOpenApiDongList();

  // Excel Upload Check
  int excelUploadCheckUserId(String userId);

  int excelUploadCheckSerialNum(String serialNum);

  int excelUploadCheckVentSerialNum(String serialNum);

  int excelUploadGetParentSpaceIdx(@Param("spaceName") String spaceName,
      @Param("deviceIdx") String deviceIdx);

  int excelUploadGetSpaceIdx(@Param("spaceName") String spaceName,
      @Param("parentSpaceIdx") String parentSpaceIdx);

  // login Group
  List<String> appSelectGroupMember(String groupId);

  List<HashMap<String, Object>> appSelectMemberDevice(String userId);

  List<String> appSelectMemberVentDevice(String iaqSerialNum);

  List<Device> selectDataDownloadList();

  Long findAdminIdx(String adminId);

  Long findGroupIdx(String groupId);

  Long findMemberIdx(String memberId);

  List<Dong> selectExcelDownDongList(@Param("scode") String scode, @Param("gcode") String gcode,
      @Param("dcode") String dcode, @Param("searchType") String searchType);

  String getUserIdToUserIdx(String userId);

  String getSerialToDeviceIdx(String serialNum);

  String getGroupIdToGroupIdx(String groupId);

  List<String> getGroupIdxToUserIdxs(String groupIdx);

  String getDeviceIdxToUserIdx(@Param("deviceIdx") String deviceIdx);

  List<HashMap<String, String>> getSerialListToFcmToken(@Param("token") String token, @Param("userId") String userId);

  HashMap<String, Object> getAdminStatisticsData();

  List<HashMap<String, String>> findMemberIdList(@Param("userName") String userName,
      @Param("phoneNumber") String phoneNumbers);

  List<HashMap<String, String>> findGroupIdList(@Param("userName") String userName,
      @Param("phoneNumber") String phoneNumbers);

  List<HashMap<String, Object>> selectPushHistoryList(@Param("userId") String userId,
      @Param("serial") String serial, @Param("hisIdx") String hisIdx);

  int selectPushHistoryListCnt(@Param("userId") String userId,
      @Param("serial") String serial);

  List<PushMessage> selectPushMessageList();

  PushMessage selectPushMessageSearch(@Param("idx") String idx);

  int checkFcmToken(@Param("token") String token);

  int checkFcmTokenUserId(@Param("userId") String userId, @Param("token") String token);

  HashMap<String, String> selectDeviceToSerial(@Param("deviceIdx") String idx);

  MemberDevice getMemberDeviceInfoBySerial(@Param("serialNum") String serialNum);

  HashMap<String, String> selectMemberDeviceInfo(@Param("serialNum") String serialNum);

  HashMap<String, String> selectAirMapDeviceInfoKweather(@Param("serialNum") String serialNum);
  HashMap<String, String> selectAirMapDeviceInfoAirKorea(@Param("airCode") String airCode);

  List<Device> selectOaqList();

  int selectGroupPushHistoryCnt(@Param("groupId") String groupId);

  List<HashMap<String, Object>> selectGroupPushHistory(@Param("groupId") String groupId);

  List<HashMap<String, Object>> selectSeochoNoticeList();

  HashMap<String ,Object> selectElementInfo(@Param("element") String element);

  String selectSetTemp(@Param("iaqSerial") String iaqSerial);

  HashMap<String,Object> selectIaqRelatedOaq(@Param("iaqSerial") String iaqSerial);

  List<HashMap<String,Object>> selectNearByOaqs(HashMap<String,Object> LatLon );
}
