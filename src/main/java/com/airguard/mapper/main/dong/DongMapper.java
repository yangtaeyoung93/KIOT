package com.airguard.mapper.main.dong;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.dong.AirCity;
import com.airguard.model.dong.AirEqui;
import com.airguard.model.dong.AirGeo;
import com.airguard.model.dong.Dong;
import com.airguard.model.dong.DongData;
import com.airguard.model.dong.DongGeo;
import com.airguard.model.dong.GuData;
import com.airguard.model.dong.OaqCity;
import com.airguard.model.dong.OaqEqui;
import com.airguard.model.dong.OaqGeo;
import com.airguard.model.dong.SearchDong;
import com.airguard.model.dong.SiData;

/**
 * @FileName : AdminMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 13.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DongMapper {

  List<Dong> selectDongOaqList();

  List<SiData> siList();

  List<GuData> guList(SearchDong search);

  List<DongData> dongList(SearchDong search);

  List<Dong> selectDongSearch(SearchDong search);

  List<Dong> selectSiGunGuSearch(SearchDong search);

  List<DongGeo> selectDongList();

  List<AirGeo> selectAirEquiList();

  List<OaqGeo> selectOaqEquiList();

  List<AirCity> selectAirEquiCnt(String value);

  List<OaqCity> selectOaqEquiCnt();

  List<AirEqui> refAirEquiSearch(SearchDong search);

  List<OaqEqui> refOaqEquiSearch(SearchDong search);
}
