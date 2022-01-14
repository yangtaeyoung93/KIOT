<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ include file="../common/header.jsp" %>
<script src="${pageContext.request.contextPath}/resources/share/js/system/memberDeviceInsert.js"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<script src="${pageContext.request.contextPath}/resources/plugins/select2/js/select2.full.min.js"></script>

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
                        <li class="breadcrumb-item"><strong>사용자 관리</strong></li>
                        <li class="breadcrumb-item"><a href="/system/member/device/list">사용자 장비
                            관리</a></li>
                        <li class="breadcrumb-item active"><strong>${item }</strong></li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="row">
            <div class="col-3">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>사용자 아이디 선택 (<font style="color:red">*</font>)</strong><br/><font
                                style="font-size:22px;">(step.1)</font>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" data-placeholder="선택"
                                        id="memberSelect" style="width: 100%;">
                                    <c:forEach var="memberDataList" items="${memberDataList }">
                                        <option value="${memberDataList.idx }">${memberDataList.userId}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-3">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>장비유형 선택 (<font style="color:red">*</font>)</strong><br/><font
                                style="font-size:22px;">(step.2)</font>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" id="deviceTypeSelect"
                                        style="width: 100%;">
                                    <option value=''>장비 유형 선택</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-3">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>장비모델 선택 (<font style="color:red">*</font>)</strong><br/><font
                                style="font-size:22px;">(step.3)</font>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" data-placeholder="선택"
                                        id="deviceModelSelect" style="width: 100%;">
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-3">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>장비 시리얼 선택 (<font style="color:red">*</font>)</strong><br/><font
                                style="font-size:22px;">(step.4)</font>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" data-placeholder="선택"
                                        id="deviceSerialSelect" style="width: 100%;">
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-4">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>환기청정기 선택</strong><font style="font-size:22px;">(IAQ 선택 시)</font>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" data-placeholder="선택"
                                        multiple="multiple" data-dropdown-css-class="select2-purple"
                                        id="ventSelect" style="width: 100%;">
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-4">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>공간 카테고리(상위) (<font style="color:red">*</font>)</strong>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" data-placeholder="선택"
                                        id="parentSpaceSelect" style="width: 100%;">
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-4">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>공간 카테고리(하위) (<font style="color:red">*</font>)</strong>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <select class="form-control select2" data-placeholder="선택"
                                        id="spaceSelect" style="width: 100%;">
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="content">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title"><strong>고객 정보</strong></h3>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-12">
                                <div class="row">
                                    <input type="hidden" name="chk_station" id="chk_station"
                                           value="0"/>
                                    <input type="hidden" name="h_deviceIdx" id="h_deviceIdx"
                                           value="${memberDeviceDataList.deviceIdx }"/>
                                    <input type="hidden" name="h_serial_num" id="h_serial_num"
                                           value="${memberDeviceDataList.serialNum }"/>
                                    <input type="hidden" name="h_stationIdx" id="h_stationIdx"
                                           value="${memberDeviceDataList.stationIdx }"/>
                                    <input type="hidden" name="h_stationName" id="h_stationName"
                                           value="${memberDeviceDataList.deviceType }"/>
                                    <div class="col-6">
                                        <div class="custom-control"
                                             style="width: 75%; display: inline-block;">
                                            <label for="spaceName">스테이션명 (<font
                                                    style="color:red">*</font>)</label><br/>
                                            <input type="text" class="form-control col-8"
                                                   id="spaceName" name="spaceName" value=""
                                                   placeholder="내용을 입력해 주세요"
                                                   style="display:inline-block;"/>
                                            <div id="chk_txt" class="mt10"></div>
                                        </div>
                                        <div class="custom-control"
                                             style="width: 24%; display: inline-block;">
                                            <label for="airMapUse">에어맵 사용유무 (<font
                                                    style="color:red">*</font>)</label><br/>
                                            <input type="checkbox" id="airMapUse" name="airMapUse"
                                                   style="display:inline-block;"/>
                                            <div class="mt10" hidden>d</div>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label>설치 위치 (<font style="color:red">*</font>)</label>
                                            <div class="row col-12">
                                                <div style="width: 18%; margin: 0 1%;">
                                                    <label for="lat"
                                                           class="mt5 mr5">위도</label>
                                                    <input type="text"
                                                           class="form-control d-inline-block mr10"
                                                           id="lat" name="lat" value=""
                                                           placeholder="35.12752">
                                                </div>
                                                <div style="width: 18%; margin: 0 1%;">
                                                    <label for="lon"
                                                           class="mt5 mr5">경도</label>
                                                    <input type="text"
                                                           class="form-control d-inline-block mr10"
                                                           id="lon" name="lon" value=""
                                                           placeholder="127.1234">
                                                </div>
                                                <div style="width: 18%; margin: 0 1%;">
                                                    <label for="sCode"
                                                           class="mt5 mr5">행정시/도</label>
                                                    <select name="sCode" id="sCode"
                                                            class="form-control mr10"></select>
                                                </div>
                                                <div style="width: 18%; margin: 0 1%;">
                                                    <label for="gCode"
                                                           class="mt5 mr5">행정시/군/구</label>
                                                    <select name="gCode" id="gCode"
                                                            class="form-control mr10"></select>
                                                </div>
                                                <div style="width: 18%; margin: 0 1%;">
                                                    <label for="dCode"
                                                           class="mt5 mr5">행정읍/면/동</label>
                                                    <select name="dCode" id="dCode"
                                                            class="form-control mr10"></select>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row mt20">
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="departName">고객담당자(<font
                                                    style="color:red">*</font>)</label>
                                            <input type="text" class="form-control" id="departName"
                                                   name="departName" value=""
                                                   placeholder="내용을 입력해 주세요">
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="equipDt">설치 일자(<font
                                                    style="color:red">*</font>)</label>
                                            <div class="input-group">
                                                <div class="input-group-prepend" id="datepicker_ic"
                                                     style="cursor:pointer;">
													<span class="input-group-text"> <i
                                                            class="far fa-calendar-alt"></i>
													</span>
                                                </div>
                                                <input type="text" class="form-control float-right"
                                                       name="equipDt" id="equipDt"
                                                       value="${memberDeviceDataList.equipDt }"
                                                       placeholder="클릭 후 선택">
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row mt20">
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="equipDt">고객연락처(<font
                                                    style="color:red">*</font>)</label>
                                            <input type="text" class="form-control"
                                                   id="departPhoneNumber" name="departPhoneNumber"
                                                   value="" placeholder="내용을 입력해 주세요">
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="equipName">설치담당자(<font
                                                    style="color:red">*</font>)</label>
                                            <input type="text" class="form-control" id="equipName"
                                                   name="equipName" value=""
                                                   placeholder="내용을 입력해 주세요">
                                        </div>
                                    </div>
                                </div>

                                <div class="row mt20">
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="salesName">영업담당자 (<font
                                                    style="color:red">*</font>)</label>
                                            <input type="text" class="form-control" id="salesName"
                                                   name="salesName" value=""
                                                   placeholder="내용을 입력해 주세요">
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="equipAddr">설치 주소(<font
                                                    style="color:red">*</font>)</label>
                                            <input type="text" class="form-control" id="equipAddr"
                                                   name="equipAddr" value=""
                                                   placeholder="내용을 입력해 주세요">
                                        </div>
                                    </div>
                                </div>

                                <div class="row mt20">
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="etc">비고</label>
                                            <input type="text" class="form-control" id="etc"
                                                   name="etc" value="" placeholder="내용을 입력해 주세요">
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <label for="equipAddr2">설치장소설명</label>
                                            <input type="text" class="form-control" id="equipAddr2"
                                                   name="equipAddr2" value=""
                                                   placeholder="(Ex, 신림동 주민센터 앞)">
                                        </div>
                                    </div>
                                </div>
                                <div class="row mt20">
                                    <div class="col-6">
                                        <div class="custom-control">
                                            <form enctype="multipart/form-data" id="fileUploadForm">
                                                <label>첨부 파일</label>
                                                <input id="equiFile" name="equiFile"
                                                       class="custom-select"
                                                       style="background: white;" class="file"
                                                       type="file" multiple data-show-upload="false"
                                                       data-show-caption="true"/>
                                            </form>
                                        </div>
                                    </div>
                                    <div class="col-6" id="setTempDiv">
                                        <div class="custom-control">
                                            <label for="setTemp">
                                                <span style="color:red">*</span>설정 온도
                                            </label>
                                            <input type="number" class="form-control" id="setTemp"
                                                   name="setTemp" value="0">
                                        </div>
                                    </div>
                                </div>
                                <br/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="btn_area">
        <div class="row">
            <div class="col-12">
                <input type="button" class="btn btn-primary ml3" style="min-width:100px;" ${btn }/>
                <input type="button" class="btn btn-primary ml3" style="min-width:100px;"
                       id="listBtn" value="목록으로"/>
            </div>
        </div>
    </section>
</div>
<%@ include file="../common/footer.jsp" %>