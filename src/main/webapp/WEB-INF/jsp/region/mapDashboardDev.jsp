<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/header.jsp" %>

<link rel="stylesheet" href="https://openlayers.org/en/v5.3.0/css/ol.css" type="text/css">
<link rel="stylesheet"
      href="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/css/ol.css">
<script src="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/build/ol.js"></script>

<script src="/resources/share/js/region/mapDashboardDev.js"></script>
<!-- ##### Footer Area End ##### -->
<style>
  .popover-body {
    color: #212529;
  }

  .popover-body {
    min-width: 180px;
  }

  .ol-popup-closer {
    text-decoration: none;
    position: absolute;
    top: 2px;
    right: 8px;
  }

  .ol-popup-closer:after {
    content: "✖";
  }
</style>
<div class="content-wrapper">
    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6">
                    <h1>
                        <strong>${item }</strong>
                    </h1>
                </div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><strong>${gubun }</strong></li>
                        <li class="breadcrumb-item active"><strong>${item }</strong></li>
                    </ol>
                </div>
            </div>
        </div>
        <!-- /.container-fluid -->
    </section>
    <!-- Main content -->
    <section class="content">
        <div class="row">
            <div class="col-3">
                <div class="card" style="height:386px;">
                    <div class="card-header">
                        <h3 class="card-title"><strong>행정동 검색</strong></h3>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="custom-control" style="width: 100%; padding-left:0px;">
                                <label for="searchDfname1" style="font-size:1.1rem;">시/도 분류</label>
                                <div class="row" style="width:100%;margin-right: 0;margin-left: 0;">
                                    <select name="searchDfname1" id="searchDfname1"
                                            class="custom-select custom-select-sm form-control form-control-sm mr10"
                                            style="width: 32%;height:50px; display:inline-block;">
                                    </select>
                                    <select name="searchDfname2" id="searchDfname2"
                                            class="custom-select custom-select-sm form-control form-control-sm mr10"
                                            style="width: 30%;height:50px; display:inline-block;">
                                    </select>
                                    <select name="searchDfname3" id="searchDfname3"
                                            class="custom-select custom-select-sm form-control form-control-sm"
                                            style="width: 30%;height:50px; display:inline-block;">

                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row mt15">
                            <div class="custom-control" style="width: 100%;  padding-left:0px;">
                                <label for="searchDname" style="display: block; font-size:1.1rem;">행정동
                                    검색</label>
                                <input type="text" class="form-control" id="searchDname"
                                       name="searchDname"
                                       placeholder="내용을 입력해 주세요"
                                       style="display:inline; vertical-align: middle; width:100%; height:50px;">
                                <button type="button" id="sch_btn" class="btn btn-primary mt10"
                                        style="float:right;height:50px;">조회
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-3">
                <div class="card" style="height:386px;">
                    <div class="card-header">
                        <h3 class="card-title"><strong>관측망 현황</strong></h3>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="list-box" id="category_list1">
                                <ul>
                                    <li class="" id="n_ALL" onclick="SearchEquiCnt('n_ALL');">전체
                                    </li>
                                    <li id="n_SE" onclick="SearchEquiCnt('n_SE');">서울</li>
                                    <li id="n_BS" onclick="SearchEquiCnt('n_BS');">부산</li>
                                    <li id="n_DG" onclick="SearchEquiCnt('n_DG');">대구</li>
                                    <li id="n_IC" onclick="SearchEquiCnt('n_IC');">인천</li>
                                </ul>
                                <ul>
                                    <li id="n_GJ" onclick="SearchEquiCnt('n_GJ');">광주</li>
                                    <li id="n_DJ" onclick="SearchEquiCnt('n_DJ');">대전</li>
                                    <li id="n_US" onclick="SearchEquiCnt('n_US');">울산</li>
                                    <li id="n_GG" onclick="SearchEquiCnt('n_GG');">경기</li>
                                    <li id="n_GW" onclick="SearchEquiCnt('n_GW');">강원</li>
                                </ul>
                                <ul>
                                    <li id="n_CB" onclick="SearchEquiCnt('n_CB');">충북</li>
                                    <li id="n_CN" onclick="SearchEquiCnt('n_CN');">충남</li>
                                    <li id="n_JB" onclick="SearchEquiCnt('n_JB');">전북</li>
                                    <li id="n_JN" onclick="SearchEquiCnt('n_JN');">전남</li>
                                    <li id="n_GB" onclick="SearchEquiCnt('n_GB');">경북</li>
                                </ul>
                                <ul>
                                    <li id="n_GN" onclick="SearchEquiCnt('n_GN');">경남</li>
                                    <li id="n_JJ" onclick="SearchEquiCnt('n_JJ');">제주</li>
                                    <li id="n_SJ" onclick="SearchEquiCnt('n_SJ');">세종</li>
                                    <li id="None-One" onclick=""></li>
                                    <li id="None-Two" onclick=""></li>
                                </ul>
                            </div>
                        </div>
                        <div class="row mt10">국가관측망 대수&nbsp;:&nbsp;<span id="nation_cnt"></span>대
                        </div>
                        <div class="row mt5">
                            케이웨더 관측망 대수&nbsp;:&nbsp;<span id="kweather_cnt"></span>대
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title"><strong>참조 관측 지점 <span id="dName"
                                                                      style="color: red; font-size: small;"></span>
                        </strong></h3>
                        <div class="list-box"
                             style="width: 210px; float: right; position: absolute; top: 70px; right: 15px; z-index:50">
                            <ul>
                                <li id="pm25ViewBtn"
                                    style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;"
                                    class="list-box-color">
                                    PM2.5
                                </li>
                                <li id="pm10ViewBtn"
                                    style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;"
                                    class="list-box-white">
                                    PM10
                                </li>
                            </ul>
                        </div>
                        <!-- 						<span style="color:red; font-weight:600;">PM2.5 기준</span> -->
                    </div>
                    <div class="card-body">
                        <div id="map" style="height: 300px;"></div>
                        <div id="popup"></div>
                    </div>
                </div>

            </div>

            <!-- /.col -->
        </div>
        <div class="row">
            <div id="dtTableArea" class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title"><strong>검색 결과</strong></h3>
                    </div>
                    <div class="card-body">
                        <table id="dustTable" class="table table-bordered table-hover">
                            <colgroup>
                                <col style="width:15%"></col>
                                <col style="width:23%"></col>
                                <col style="width:23%"></col>
                                <col style="width:26%"></col>
                                <col style="width:15%"></col>
                            </colgroup>
                            <thead>
                            <tr>
                                <th class="bgGray1">지역</th>
                                <th class="bgGray1">농도</th>
                                <th class="bgGray1">등급(WHO기준)</th>
                                <th class="bgGray1">업데이트 시간</th>
                                <th class="bgGray1">위치</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
            <div class="col-12" id="refInfoOaqArea">
                <div class="row col-12" style="padding: 0; margin-left: 0;">
                    <div class="card" style="width:100%;">
                        <div id="refInfoOaqDiv" class="card-body">
                        </div>
                    </div>
                </div>
                <div class="card" style="width:100%;" id="dongOaqChartDivArea">
                </div>
                <div class="row col-12" style="padding: 0; margin-left: 0;">
                    <div class="card" style="width:100%;" id="oaqChartDivArea">
                    </div>
                </div>
            </div>
            <div class="col-12" id="refInfoAirArea">
                <div class="row col-12" style="padding: 0; margin-left: 0;">
                    <div class="card" style="width:100%;">
                        <div id="refInfoAirDiv" class="card-body">
                        </div>
                    </div>
                </div>
                <div class="card" style="width:100%;" id="dongAirChartDivArea">
                </div>
                <div class="row col-12" style="padding: 0; margin-left: 0;">
                    <div class="card" style="width:100%;" id="airChartDivArea">
                    </div>
                </div>
            </div>
        </div>
        <!-- /.row -->
    </section>
    <!-- /.content -->
    <!-- .Modal -->
    <div class="modal fade" id="modal-chart" style="display: none;"
         aria-hidden="true">
        <div class="modal-dialog modal-xl modal-chart">
            <div class="modal-content">
                <div class="modal-header" id="modalHeader">
                    <h4 class="modal-title" id="modalTitle" style="width:50%"></h4>
                    <div style="width:45%; text-align:right;">
                        <font size="4em">기간 검색
                        <input type="number" id="num" value="2" style="margin-top:6px;width:48px;"/>
                        <select id="unit" style="margin-left: 3px;height:33px;">
                            <option value="n">개월 전</option>
                            <option value="w" >주 전</option>
                            <option value="d" selected>일 전</option>
                            <option value="h">시간 전</option>
                        </select>
                        <button type="button" id="dateSearch" style="margin-top: 6px;margin-left: 3px;">조회</button></font>
                    </div>
                    
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
                </div>
                <div class="modal-body">
                    <div id="modalChart1">
                    </div>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.Modal -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp" %>
<!-- ##### Footer Area End ##### -->