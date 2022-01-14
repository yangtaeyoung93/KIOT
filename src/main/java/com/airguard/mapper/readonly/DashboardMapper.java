package com.airguard.mapper.readonly;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.common.Search;
import com.airguard.model.dashboard.DashboardDeviceCntDto;
import com.airguard.model.dashboard.DashboardDeviceHisDto;
import com.airguard.model.dashboard.DashboardDeviceVentHisDto;
import com.airguard.model.dashboard.DashboardReceiveCntDto;
import com.airguard.model.dashboard.DashboardReceiveDto;
import com.airguard.model.dashboard.DashboardUserDto;
import com.airguard.model.dashboard.DashboardUserLoginDto;

@Mapper
public interface DashboardMapper {

  /*
   * 수신/미수신
   */
  List<DashboardReceiveDto> selectReceiveDashboard(Search search);

  List<DashboardReceiveCntDto> selectReceiveCntDashboard();

  /*
   * 사용자/그룹 현황
   */
  DashboardUserDto selectUserDashboardCnt();

  List<DashboardUserLoginDto> selectUserDashboardLoginCnt();

  /*
   * 장비 현황
   */
  DashboardDeviceCntDto selectDeviceDashboardCnt();

  List<DashboardDeviceHisDto> selectDeviceDashboardHisCnt();

  List<DashboardDeviceVentHisDto> selectDeviceDashboardHisVentCnt();
}
