<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ include file="../common/header.jsp" %>
<script src="/resources/share/js/system/deviceModelList.js"></script>

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
                        <form name="boardForm" method="get">
                            <div class="search_area" style="width:70%;">
                                <div class="search_bottom" style="max-width: 760px;">
                                    <strong>장비 유형</strong>
                                    <select name="searchDeviceType" id="searchDeviceType"
                                            class="custom-select custom-select-sm form-control form-control-sm ml15 mr20"
                                            style="max-width:90px;height:38px;">
                                        <c:forEach var="deviceType" items="${deviceType }">
                                            <option value="${deviceType.idx }">${deviceType.deviceType }</option>
                                        </c:forEach>
                                    </select>
                                    <input type="text" class="form-control mr10" id="searchValue"
                                           name="searchValue" placeholder="내용을 입력해 주세요"
                                           style="max-width:250px; display:inline; vertical-align: middle;">
                                    <strong>사용 여부</strong>
                                    <div class="custom-control custom-radio"
                                         style="display:inline-block;">
                                        <input class="custom-control-input" type="radio"
                                               id="customRadio1" name="customRadio" r-data="Y"
                                               checked="">
                                        <label for="customRadio1"
                                               class="custom-control-label">Y</label>
                                    </div>
                                    <div class="custom-control custom-radio mr20"
                                         style="display:inline-block;">
                                        <input class="custom-control-input" type="radio"
                                               id="customRadio2" name="customRadio" r-data="N">
                                        <label for="customRadio2"
                                               class="custom-control-label">N</label>
                                    </div>
                                    <input type="button" class="btn btn-primary ml3"
                                           style="min-width:100px;" id="searchBtn" value="검 색">
                                </div>
                            </div>
                            <table id="deviceTable"
                                   class="table table-bordered table-hover text-center">
                                <thead>
                                <tr>
                                    <th class="bgGray1"><input type="checkbox" name="all_chk"
                                                               id="all_chk"
                                                               style="margin-left: 17px;"
                                                               class="chBox"/></th>
                                    <th class="bgGray1">NO</th>
                                    <th class="bgGray1">장비유형</th>
                                    <th class="bgGray1">장비모델</th>
                                    <th class="bgGray1">설명</th>
                                    <th class="bgGray1">등록일시</th>
                                    <th class="bgGray1">장비사진</th>
                                    <th class="bgGray1">사용 여부</th>
                                </tr>
                                </thead>
                            </table>
                            <div class="row">
                                <div class="col-3">
                                    <input type="button" class="btn btn-primary ml3"
                                           style="min-width:100px;" value="등록"
                                           onclick="goInsert()"/>
                                    <input type="button" class="btn btn-primary ml3"
                                           style="min-width:100px;" value="삭제" id="deleteBtn"/>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>

<%@ include file="../common/footer.jsp" %>