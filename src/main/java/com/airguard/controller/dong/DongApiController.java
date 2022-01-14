package com.airguard.controller.dong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;

import com.airguard.model.dong.AirGeoInfo;
import com.airguard.model.dong.SearchDong;
import com.airguard.service.dong.DongService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = CommonConstant.URL_API_DONG, produces = MediaType.APPLICATION_JSON_VALUE)
public class DongApiController {

  @Autowired
  private DongService service;

  // 시 데이터 조회
  @ApiOperation(value = "시 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/siList", method = RequestMethod.GET)
  public ResponseEntity<Object> siList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("data", service.siList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 군 데이터 조회
  @ApiOperation(value = "군 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/guList", method = RequestMethod.GET)
  public ResponseEntity<Object> guList(@RequestParam("searchSdcode") String searchSdcode) throws Exception {
    Map<String, Object> result = new HashMap<>();

    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);

    result.put("data", service.guList(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동 데이터 조회
  @ApiOperation(value = "동 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/dongList", method = RequestMethod.GET)
  public ResponseEntity<Object> dongList(@RequestParam("searchSdcode") String searchSdcode,
      @RequestParam("searchSggcode") String searchSggcode) throws Exception {

    Map<String, Object> result = new HashMap<>();

    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);
    search.setSearchSggcode(searchSggcode);

    result.put("data", service.dongList(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동별 중심 검색, DataTable 이용
  @ApiOperation(value = "동별 중심 검색, DataTable 이용", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/search/dong", method = RequestMethod.GET)
  public ResponseEntity<Object> dongSearchList(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();
    String searchDname = request.getParameter("searchDname") == null ? "" : request.getParameter("searchDname");

    SearchDong search = new SearchDong();
    search.setSearchDname(searchDname);

    result.put("data", service.selectDongSearch(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 시,구 별 검색, DataTable 이용
  @ApiOperation(value = "시,구 별 검색, DataTable 이용", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/search/sigungu", method = RequestMethod.GET)
  public ResponseEntity<Object> dongSigunguSearchList(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();

    String searchSdcode = request.getParameter("searchSdcode") == null ? "" : request.getParameter("searchSdcode");
    String searchSggcode = request.getParameter("searchSggcode") == null ? "" : request.getParameter("searchSggcode");
    String searchDongcode = request.getParameter("searchDongcode");
    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);
    search.setSearchSggcode(searchSggcode);
    search.setSearchDongcode(searchDongcode);

    result.put("data", service.selectSiGunGuSearch(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동 중심 좌표 aJax 호출 데이터 (feat. 지도)
  @ApiOperation(value = "동 중심 좌표 및 측정 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/geo/dongArea", method = RequestMethod.GET)
  public ResponseEntity<Object> dongAreaList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("dongCenterList", service.selectDongList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 징비(Air Equi) 좌표 aJax 호출 데이터 (feat. 지도 영역)
  @ApiOperation(value = "관측 징비(Air Equi) 좌표 및 측정 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/geo/airEqui", method = RequestMethod.GET)
  public ResponseEntity<Object> geoAirEquiList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("airEquiList", service.selectAirEquiList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 징비(OAQ Equi) 좌표 aJax 호출 데이터 (feat. 지도 영역)
  @ApiOperation(value = "관측 징비(OAQ Equi) 좌표 및 측정 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/geo/oaqEqui", method = RequestMethod.GET)
  public ResponseEntity<Object> geoOaqEquiList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("oaqEquiList", service.selectOaqEquiList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 장비(Air Equi) 현황 aJax 호출 데이터
  @ApiOperation(value = "관측 장비(Air Equi) 현황 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/cnt/airEqui", method = RequestMethod.GET)
  public ResponseEntity<Object> airCntList(@RequestParam("searchValue") String searchValue) throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("airCntdata", service.selectAirEquiCnt(searchValue));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 장비(OAQ Equi) 현황 aJax 호출 데이터
  @ApiOperation(value = "관측 장비(OAQ Equi) 현황 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/cnt/oaqEqui", method = RequestMethod.GET)
  public ResponseEntity<Object> oaqCntList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("oaqCntData", service.selectOaqEquiCnt());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 참조 관측망 검색
  @ApiOperation(value = "동별 참조 관측망 데이터 조회", tags = "동별 미세먼지 API")
  @RequestMapping(value = "/search/ref", method = RequestMethod.GET)
  public ResponseEntity<Object> refEquiSearch(@RequestParam("searchEquiType") String searchEquiType,
      @RequestParam("searchDongCode") String searchDongCode) throws Exception {

    SearchDong search = new SearchDong();
    search.setSearchDongcode(searchDongCode);

    Map<String, Object> result = new HashMap<>();

    if (searchEquiType.equals("A")) {
      result.put("refData", service.refAirEquiSearch(search));
    } else if (searchEquiType.equals("O")) {
      result.put("refData", service.refOaqEquiSearch(search));
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /*
   * ==============================================
   */

  // 시 데이터
  @ApiOperation(value = "시 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/siList/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> siListDev() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("data", service.siListDev());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 군 데이터
  @ApiOperation(value = "군 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/guList/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> guListDev(@RequestParam("searchSdcode") String searchSdcode) throws Exception {
    Map<String, Object> result = new HashMap<>();

    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);

    result.put("data", service.guListDev(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동 데이터
  @CrossOrigin
  @ApiOperation(value = "동 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/dongList/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> dongListDev(@RequestParam("searchSdcode") String searchSdcode,
      @RequestParam("searchSggcode") String searchSggcode) throws Exception {

    Map<String, Object> result = new HashMap<>();

    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);
    search.setSearchSggcode(searchSggcode);

    result.put("data", service.dongListDev(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동별 중심 검색, DataTable 이용
  @ApiOperation(value = "동별 중심 검색, DataTable 이용", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/search/dong/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> dongSearchListDev(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();
    String searchDname = request.getParameter("searchDname") == null ? "" : request.getParameter("searchDname");

    SearchDong search = new SearchDong();
    search.setSearchDname(searchDname);

    result.put("data", service.selectDongSearchDev(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 시,구 별 검색, DataTable 이용
  @ApiOperation(value = "시,구 별 검색, DataTable 이용", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/search/sigungu/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> dongSigunguSearchListDev(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();
    String searchSdcode = request.getParameter("searchSdcode") == null ? "" : request.getParameter("searchSdcode");
    String searchSggcode = request.getParameter("searchSggcode") == null ? "" : request.getParameter("searchSggcode");
    String searchDongcode = request.getParameter("searchDongcode");
    SearchDong search = new SearchDong();
    search.setSearchSdcode(searchSdcode);
    search.setSearchSggcode(searchSggcode);
    search.setSearchDongcode(searchDongcode);

    result.put("data", service.selectSiGunGuSearchDev(search));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동 중심 좌표 aJax 호출 데이터 (feat. 지도)
  @ApiOperation(value = "동 중심 좌표 및 측정 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/geo/dongArea/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> dongAreaListDev() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("dongCenterList", service.selectDongListDev());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 징비(Air Equi) 좌표 aJax 호출 데이터 (feat. 지도 영역)
  @ApiOperation(value = "관측 징비(Air Equi) 좌표 및 측정 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/geo/airEqui/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> geoAirEquiListDev() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("airEquiList", service.selectAirEquiListDev());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 징비(OAQ Equi) 좌표 aJax 호출 데이터 (feat. 지도 영역)
  @ApiOperation(value = "관측 징비(OAQ Equi) 좌표 및 측정 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/geo/oaqEqui/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> geoOaqEquiListDev() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("oaqEquiList", service.selectOaqEquiListDev());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 장비(Air Equi) 현황 aJax 호출 데이터
  @ApiOperation(value = "관측 장비(Air Equi) 현황 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/cnt/airEqui/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> airCntListDev(@RequestParam("searchValue") String searchValue) throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("airCntdata", service.selectAirEquiCntDev(searchValue));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 관측 장비(OAQ Equi) 현황 aJax 호출 데이터
  @ApiOperation(value = "관측 장비(OAQ Equi) 현황 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/cnt/oaqEqui/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> oaqCntListDev() throws Exception {
    Map<String, Object> result = new HashMap<>();

    result.put("oaqCntData", service.selectOaqEquiCntDev());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 참조 관측망 검색
  @ApiOperation(value = "동별 참조 관측망 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/search/ref/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> refEquiSearchDev(@RequestParam("searchEquiType") String searchEquiType,
      @RequestParam("searchDongCode") String searchDongCode) throws Exception {

    SearchDong search = new SearchDong();
    search.setSearchDongcode(searchDongCode);

    Map<String, Object> result = new HashMap<>();

    switch (searchEquiType) {
      case "A":
        result.put("refData", service.refAirEquiSearchDev(search));
        break;
      case "D":
      case "O":
        result.put("refData", service.refOaqEquiSearchDev(search));
        break;
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /*
   * ==============================================
   */

  // 참조 관측망 History 검색 (케이웨더 OAQ 장비) -- 플랫폼 API
  @ApiOperation(value = "참조 관측망 History 검색 (케이웨더 OAQ 장비)", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/ref/oaq/history/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> oaqRefHistoryDev(@RequestParam("searchEquiInfoKey") String searchEquiInfoKey,
      @RequestParam("mapType") String mapType,
      @RequestParam(value = "date", required = false, defaultValue = "2d-ago") String selectedDate) throws Exception {
    Map<String, Object> result = new HashMap<>();
    try {
      result.put("data", service.oaqRefHistoryDev(searchEquiInfoKey, mapType, selectedDate));
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      result.put("data", erMp);
      erMp.put("message", "There is no matching data .");

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 참조 관측망 History 검색 (국가 관측 장비) -- 플랫폼 API
  @ApiOperation(value = "참조 관측망 History 검색 (국가 관측 장비)", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/ref/air/history/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> airRefHistoryDev(@RequestParam("searchAirCode") String searchAirCode,
      @RequestParam(value = "date", required = false, defaultValue = "2d-ago") String selectedDate) throws Exception {
    Map<String, Object> result = new HashMap<>();
    try {
      result.put("data", service.airRefHistoryDev(searchAirCode, selectedDate));
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      result.put("data", erMp);
      erMp.put("message", "There is no matching data .");

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 해당 동 History 검색 -- 플랫폼 API
  @ApiOperation(value = "해당 동 History 검색 -- 플랫폼 API", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/ref/dong/history/dev", method = RequestMethod.GET)
  public ResponseEntity<Object> dongHistoryDev(@RequestParam("dcode") String dcode,
      @RequestParam(value = "date", required = false, defaultValue = "2d-ago") String selectedDate) throws Exception {
    Map<String, Object> result = new HashMap<>();
    try {
      result.put("data", service.dongHistoryDev(dcode, selectedDate));
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      result.put("data", erMp);
      erMp.put("message", "There is no matching data .");

      return new ResponseEntity<>(result, HttpStatus.OK);

    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
  /*
   * ==============================================
   */

  // 동별 미세먼지 데이터 목록
  @ApiOperation(value = "동별 미세먼지 데이터 목록", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/data/list", method = RequestMethod.GET)
  public ResponseEntity<Object> dongDataList() throws Exception {
    Map<String, Object> result = new HashMap<>();

    try {
      result.put("data", service.dataList());
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "There is no matching data .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 동별 미세먼지 데이터 상세, 에어코리아 데이터
  @ApiOperation(value = "동별 미세먼지 데이터 상세, 에어코리아 데이터", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/data/detail", method = RequestMethod.GET)
  public ResponseEntity<Object> dongDataDetail(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();

    try {
      String dcode = request.getParameter("dcode");
      String startTime = request.getParameter("startTime"); // 2020/04/21-00:00:00
      String endTime = request.getParameter("endTime"); // 2020/04/21-23:59:59
      String type = request.getParameter("type");
      result.put("data", service.historyData(dcode, startTime, endTime, "sum", type));
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "There is no matching data .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "미세먼지 보정 수치 조회 (목록)", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/pm/offset/list", method = RequestMethod.GET)
  public ResponseEntity<Object> pmOffsetList(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();

    try {
      result.put("data", service.selectDongPmList(request.getParameter("deviceType")));
    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "미세먼지 보정 수치 조회 (상세)", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/ref/pm/offset", method = RequestMethod.GET)
  public ResponseEntity<Object> pmOffsetRef(HttpServletRequest request) throws Exception {
    Map<String, Object> result = new HashMap<>();

    try {
      result.put("data",
          service.selectPmOffsetRef(request.getParameter("deviceType"), request.getParameter("serialNum")));
      result.put("type", request.getParameter("deviceType"));
    } catch (NullPointerException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Check it parameter .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "동별 미세먼지 GEO Data 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/geo/pm", method = RequestMethod.GET)
  public ResponseEntity<Object> selectOaqGeo() throws Exception {
    Map<String, Object> result = new HashMap<>();

    try {
      result.put("data", service.selectOaqGeo());
    } catch (Exception e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      erMp.put("message", "Server Error .");
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    result.put("resultCode", 1);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "동별 미세먼지 데이터 다운로드 (With, Excel File)", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/data/excel/download", method = RequestMethod.GET)
  public ResponseEntity<?> dataExcelDownload(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Map<String, Object> result = new HashMap<>();

    String startTime = request.getParameter("startTime"); // 2020/04/21-00:00:00
    String endTime = request.getParameter("endTime"); // 2020/04/21-23:59:59
    String standard = request.getParameter("standard"); // sum, 5m-avg-none, 1h-avg-none, 1d-avg-none, 1n-avg-none
    String sCode = request.getParameter("sCode");
    String gCode = request.getParameter("gCode");
    String dCode = request.getParameter("dCode");
    String type;
    String pmType = request.getParameter("pmType");

    try {
      if (!dCode.equals("")) {
        type = "dong";
      } else if (!gCode.equals("")) {
        type = "gu";
      } else {
        type = "si";
      }
      service.downloadDongHisDataMultiDownload(type, sCode, gCode, dCode, startTime, endTime, standard, pmType,
          response);
      result.put("resultCode", 1);
    } catch (HttpStatusCodeException e) {
      Map<String, Object> erMp = new HashMap<>();
      erMp.put("resultCode", 0);
      result.put("data", erMp);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "동별 미세먼지 에어코리아 데이터 조회", tags = "동별 미세먼지 API - 2차")
  @RequestMapping(value = "/list/airkor", method = RequestMethod.GET)
  public ResponseEntity<Object> collectionTotalSensorApiAirKor(@RequestParam(required = false) String dCode)
      throws Exception {
    List<AirGeoInfo> res = service.selectAirKorApi(dCode);
    Map<String, Object> result = new HashMap<>();
    result.put("data", res);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 오픈 API 서버 이용, 특정 동 정보 목록 API (양천구, 금천구, 구로구, 영등포구, 소사구, 광명시, 관악구, 동작구)
  @ApiOperation(value = "오픈 API 서버 이용, 특정 동 정보 목록 API", tags = "오픈 API 서버")
  @RequestMapping(value = "/open/kweather", method = RequestMethod.GET)
  public ResponseEntity<Object> selectOpenApiDongList(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();

    List<HashMap<String, Object>> datas = new ArrayList<>();
    long resultCode;

    try {

      datas = service.selectOpenApiDongList();
      resultCode = CommonConstant.R_SUCC_CODE;

    } catch (Exception e) {
      resultCode = CommonConstant.R_FAIL_CODE;
    }

    result.put("resultCode", resultCode);
    result.put("count", datas.size());
    result.put("response", datas);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
