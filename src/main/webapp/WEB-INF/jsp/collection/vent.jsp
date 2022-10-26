<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="../common/header.jsp" %>
<script src="${pageContext.request.contextPath}/resources/share/js/collection/vent.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="${pageContext.request.contextPath}/resources/plugins/select2/js/select2.full.min.js"></script>
<style>
    .filter-cli {
        background: rebeccapurple;
        color: white;
        border-radius: 5px;
    }
.content-wrapper{
    width:1655px;
}

.main-sidebar{
    position : fixed !important;
    height : 1140px !important;
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
                        <li class="breadcrumb-item"><strong>${superItem }</strong></li>
                        <li class="breadcrumb-item active"><strong>${item }</strong></li>
                    </ol>
                </div>
            </div>
        </div>
    </section>
    <section class="content">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <div class="search_area" style="width:60%">
                        <div class="search_top" style="max-width: 100%;">
                                <label for="searchMaster">상위그룹</label>
									<select name="searchMaster" id="searchMaster" class="select2 custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:180px;height:38px;">
										<option value="">전체</option>
									</select>
                                <label for="searchGroup">그룹</label>
                                <select name="searchGroup" id="searchGroup"
                                        class="select2 custom-select custom-select-sm form-control form-control-sm ml5 mr10"
                                        style="max-width:180px;height:38px;">
                                    <option value="">전체</option>
                                </select>
                            <label for="searchVent"> VENT 모델 </label>
                            <select name="searchVent" id="searchVent"
                                    class="select2 custom-select custom-select-sm form-control form-control-sm ml5 mr10"
                                    style="max-width:180px;height:38px;">
                                <option value="">전체</option>
                            </select>
                        </div>
                            <div class="search_bottom mt10" style="max-width:900px;">

                                <select name="searchType" id="searchType"
                                        class="custom-select custom-select-sm form-control form-control-sm mr10"
                                        style="max-width:100px;height:38px;">
                                    <option value="">전체</option>
                                    <option value="sch_serialNum">시리얼번호</option>
                                    <option value="sch_stationName">스테이션명</option>
                                    <option value="sch_userId">사용자계정</option>
                                    <option value="sch_company">고객사</option>
                                </select>
                                <input type="text" class="form-control" id="searchValue"
                                       name="searchValue" placeholder="내용을 입력해 주세요"
                                       style="max-width:350px; display:inline; vertical-align: middle;">
                            </div>
                            <div style="display: inline-block; max-width:600px; float: right; margin-right: 30px;">
                                <div style="float: left; padding-right: 20px; cursor: pointer; padding-top: 5px; padding-bottom: 5px;"
                                     id="mttDiv" class="filterDiv filter-cli">
                                    <span><strong id="msgTxtTotal"
                                                  style="padding: 5px;"></strong></span>
                                </div>
                                <div style="float: left; padding-right: 20px; cursor: pointer; padding-top: 5px; padding-bottom: 5px;"
                                     id="mtrDiv" class="filterDiv">
                                    <span><strong id="msgTxtReceive" style="padding: 5px;"></strong></span>
                                </div>
                                <div style="float: left; padding-right: 20px; cursor: pointer; padding-top: 5px; padding-bottom: 5px;"
                                     id="mturDiv" class="filterDiv">
                                    <span><strong id="msgTxtUnReceive"
                                                  style="padding: 5px;"></strong></span>
                                </div>
                            </div>
                        </div>
                        <table id="ventTable" class="table table-bordered table-hover"
                               style="text-align: center;">
                            <thead>
                            <tr>
                                <th class="bgGray1">번호</th>
                                <th class="bgGray1">시리얼번호</th>
                                <th class="bgGray1">스테이션명</th>
                                <th class="bgGray1">고객사</th>
                                <th class="bgGray1">사용자 계정</th>
                                <th class="bgGray1">장비 등록 일자</th>
                                <th class="bgGray1">데이터 시간</th>
                                <th class="bgGray1">AI모드</th>
                                <th class="bgGray1">자동모드</th>
                                <th class="bgGray1">전원</th>
                                <th class="bgGray1">풍량</th>
                                <th class="bgGray1">배기모드</th>
                                <th class="bgGray1">공기청정기모드</th>
                                <th class="bgGray1">필터알림</th>
                                <th class="bgGray1">화재 경보</th>
                                <th class="bgGray1">수위 경보</th>
                                <th class="bgGray1">장비 상태</th>
                                <th class="bgGray1"></th>
                            </tr>
                            </thead>

                        </table>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <div class="modal fade" id="modal-lg" style="display: none;" aria-hidden="true">
        <div class="modal-dialog modal-lg" style="max-width: 80%;">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title"><strong><span>케이웨더 플랫폼운영개발실의 </span>상세정보</strong>
                    </h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form role="form">
                        <div class="card-body">
                            <input type="hidden" id="p_h_serialNum"/>
                            <input type="hidden" id="p_h_productDt"/>
                            <div class="row"
                                 style="border-top:3px solid #aaa; height:35px; line-height:35px;">
                                <div class="col-2"
                                     style="background-color:#e6edf9; border-bottom:1px solid #aaa;">
                                    시리얼번호
                                </div>
                                <div class="col-4" id="txtSerialNum"
                                     style="border-bottom:1px solid #aaa;"></div>
                                <div class="col-2"
                                     style="background-color:#e6edf9; border-bottom:1px solid #aaa;">
                                    등록일자
                                </div>
                                <div class="col-4" id="txtProductDt"
                                     style="border-bottom:1px solid #aaa;"></div>
                            </div>

                            <div class="form-group mt20">
                                <select name="" id=""
                                        class="custom-select custom-select-sm form-control form-control-sm mr10"
                                        style="width:120px;height:38px;">
                                    <option value="sum">전체</option>
                                    <option value="5m-avg-none">5분 평균</option>
                                    <option value="1h-avg-none">1시간 평균</option>
                                    <option value="1d-avg-none">1일 평균</option>
                                    <option value="1n-avg-none">1개월 평균</option>
                                </select>

                                <input type="text" class="form-control d-inline-block"
                                       name="startDt" id="startDt" placeholder="시작일자 클릭 후 선택"
                                       style="width:150px; vertical-align: middle;"> ~
                                <input type="text" class="form-control d-inline-block mr10"
                                       name="endDt" id="endDt" placeholder="종료일자 클릭 후 선택"
                                       style="width:150px; vertical-align: middle;">
                                <input type="button" class="btn btn-primary ml3 mr10"
                                       style="min-width:100px;" id="modalSearchBtn" value="검 색">
                                <select style="min-width:100px; height:38px; float: right;"
                                        id="chartTypeSelect">
                                    <option value="power">전원</option>
                                    <option value="air_volume">풍량</option>
                                    <option value="filter_alarm">배기모드</option>
                                    <option value="air_mode">필터알림</option>
                                    <option value="auto_mode">공기청정기 모드</option>
                                </select>
                            </div>

                            <div class="form-group mt10" id="timeChartDiv"
                                 style="height: 500px;"></div>

                            <div class="form-group mt10">
                                <h5 style="display:inline-block;">검색 결과</h5>
                                <input type="button" class="btn btn-primary ml3 mr10"
                                       style="min-width:100px; float:right;" id="modalDownloadBtn"
                                       value="다운로드">
                                <hr/>
                                <table id="popventTable"
                                       class="table table-bordered table-hover text-center">
                                    <thead>
                                    <tr>
                                        <th class="bgGray1">데이터 시간</th>
                                        <th class="bgGray1">자동모드</th>
                                        <th class="bgGray1">전원</th>
                                        <th class="bgGray1">풍량</th>
                                        <th class="bgGray1">배기모드</th>
                                        <th class="bgGray1">필터알림</th>
                                        <th class="bgGray1">공기청정기모드</th>
                                        <th class="bgGray1">화재 경보</th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                        </div>
                        <div class="card-footer" style="background-color:#fff;">
                            <button type="button" id="insertBtn" class="btn btn-primary"
                                    data-dismiss="modal" aria-label="Close">닫기
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <form id="detail_info_form" method="post">
        <input type="hidden" name="nowAll"/>
        <input type="hidden" name="param"/>
        <input type="hidden" name="p_h_serialNum" id="p_h_serialNum"/>
        <input type="hidden" name="p_h_parentSpaceName" id="p_h_parentSpaceName"/>
        <input type="hidden" name="p_h_spaceName" id="p_h_spaceName"/>
        <input type="hidden" name="p_h_productDt" id="p_h_productDt"/>
        <input type="hidden" name="p_h_stationName" id="p_h_stationName"/>
        <input type="hidden" name="standard" id="standard"/>
        <input type="hidden" name="deviceType" id="deviceType"/>
    </form>

</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp" %>
<!-- ##### Footer Area End ##### -->