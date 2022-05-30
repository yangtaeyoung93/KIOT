<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="../common/header.jsp" %>
<script src="${pageContext.request.contextPath}/resources/share/js/collection/connect.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="${pageContext.request.contextPath}/resources/plugins/select2/js/select2.full.min.js"></script>
<style>
.content-wrapper{
    width:1655px;
}

.main-sidebar{
    position : fixed !important;
    height : 1000px !important;
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
                        <div class="search_area" style="width:90%">
                            <div class="search_top" style="max-width:100%;">
                            <div style="float: left; width: 50%;">
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
                                </div>
                                <label for="searchParentSpace">상위 카테고리</label>
                                <select name="searchParentSpace" id="searchParentSpace"
                                        class="custom-select custom-select-sm form-control form-control-sm ml5 mr10"
                                        style="max-width:100px;height:38px;"
                                        onChange="getSpace(this.value);">
                                    <option value="">전체</option>
                                </select>
                                <label for="searchParentSpace">하위 카테고리</label>
                                <select name="searchSpace" id="searchSpace"
                                        class="custom-select custom-select-sm form-control form-control-sm ml5 mr10"
                                        style="max-width:100px;height:38px;">
                                    <option value="">전체</option>
                                </select>
                            </div>
                            <div class="search_bottom mt10" style="max-width:510px;">
                                <select name="searchType" id="searchType"
                                        class="custom-select custom-select-sm form-control form-control-sm mr10"
                                        style="max-width:100px;height:38px;">
                                    <option value="">전체</option>
                                    <option value="sch_serialNum">시리얼번호</option>
                                    <option value="sch_stationName">스테이션명</option>
                                    <option value="sch_userId">사용자계정</option>
                                    <option value="sch_etc">비고</option>
                                    <option value="sch_company">고객사</option>
                                </select>
                                <input type="text" class="form-control" id="searchValue"
                                       name="searchValue" placeholder="내용을 입력해 주세요"
                                       style="max-width:350px; display:inline; vertical-align: middle;">
                            </div>
                            <div style="display: inline-block; max-width:510px; float: right; margin-right: 30px;">
                                <div style="float: left; padding-right: 20px; cursor: pointer;"
                                     id="mttDiv">
                                    <span><strong id="msgTxtTotal"></strong></span>
                                </div>
                                <div style="float: left; padding-right: 20px; cursor: pointer;"
                                     id="mtrDiv">
                                    <span><strong id="msgTxtReceive"></strong></span>
                                </div>
                                <div style="float: left; padding-right: 20px; cursor: pointer;"
                                     id="mturDiv">
                                    <span><strong id="msgTxtUnReceive"></strong></span>
                                </div>
                            </div>
                        </div>
                        <table id="connectTable"
                               class="table table-bordered table-hover text-center"
                               style="text-align: center;">
                            <thead>
                            <tr>
                                <th class="bgGray1">번호</th>
                                <th class="bgGray1">시리얼번호</th>
                                <th class="bgGray1">환기청정기</th>
                                <th class="bgGray1">연동 시간</th>
                                <th class="bgGray1">미세먼지<br/>(㎍/㎥)</th>
                                <th class="bgGray1">초미세먼지<br/>(㎍/㎥)</th>
                                <th class="bgGray1">이산화탄소<br/>(ppm)</th>
                                <th class="bgGray1">VOCs<br/>(ppb)</th>
                                <th class="bgGray1">소음<br/>(dB)</th>
                                <th class="bgGray1">온도<br/>(℃ )</th>
                                <th class="bgGray1">습도<br/>(%)</th>
                                <th class="bgGray1">통합지수<br/>(CICI)</th>
                                <th class="bgGray1">스테이션명</th>
                                <th class="bgGray1">전원</th>
                                <th class="bgGray1">풍량</th>
                                <th class="bgGray1">운전모드</th>
                                <th class="bgGray1">고객사</th>
                                <th class="bgGray1">비고</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <form id="detail_info_form" method="post">
        <input type="hidden" name="nowAll"/>
        <input type="hidden" name="param"/>
        <input type="hidden" name="p_h_serialNum" id="p_h_serialNum"/>
        <input type="hidden" name="p_h_parentSpaceName" id="p_h_parentSpaceName"/>
        <input type="hidden" name="p_h_spaceName" id="p_h_spaceName"/>
        <input type="hidden" name="p_h_productDt" id="p_h_productDt"/>
        <input type="hidden" name="p_h_stationName" id="p_h_stationName"/>
        <input type="hidden" name="p_h_ventSerial" id="p_h_ventSerial"/>
        <input type="hidden" name="standard" id="standard"/>
        <input type="hidden" name="deviceType" id="deviceType"/>
    </form>
</div>

<%@ include file="../common/footer.jsp" %>