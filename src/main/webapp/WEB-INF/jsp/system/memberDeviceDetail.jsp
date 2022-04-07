<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ include file="../common/header.jsp" %>
<script src="${pageContext.request.contextPath}/resources/share/js/system/memberDeviceDetail.js"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<script src="${pageContext.request.contextPath}/resources/plugins/select2/js/select2.full.min.js"></script>
<style>
    .blah {
        max-height: 40px;
        min-height: 40px;
        max-width: 100%;
        height: auto;
        width: auto;
        display: block;
        margin-left: auto;
        margin-right: auto;
        padding: 5px;
    }

    .imgInp {
        width: 150px;
        margin-top: 10px;
        padding: 10px;
    }

    .imageFileArea:hover .blah {
        opacity: 0.5;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
        cursor: pointer;
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
                        <li class="breadcrumb-item"><strong>사용자 관리</strong></li>
                        <li class="breadcrumb-item"><a
                                href="${pageContext.request.contextPath}/system/member/device/list">
                            사용자 장비 관리</a></li>
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
                        <h3>
                            <strong>사용자 정보</strong>
                        </h3>
                        <div class="opt_wrap">
                            <hr/>
                            <div class="row">
                                <c:forEach var="memberDataList" items="${memberDataList }">
                                    <c:if test="${memberDataList.idx == idx }">
                                        <div class="col-3">
                                            <div class="row"><strong>사용자아이디</strong></div>
                                            <div id="txtUserId">${memberDataList.userId }</div>
                                        </div>
                                        <div class="col-3">
                                            <div class="row"><strong>스테이션 공유여부 </strong></div>
                                            <div id="txtSpaceShared">${memberDataList.stationShared }</div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                                <div class="col-2">
                                    <div class="row"><strong>등록된 장비현황</strong></div>
                                    <div>${memberDeviceDataCnt.deviceCount }</div>
                                </div>
                                <div class="col-4">
                                    <div class="row">
                                        <div class="col-6"><strong>IAQ 장비</strong>
                                            <div id="">${memberDeviceDataCnt.iaqDeviceCount }</div>
                                        </div>
                                        <div class="col-6"><strong>OAQ 장비</strong>
                                            <div>${memberDeviceDataCnt.oaqDeviceCount }</div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-6"><strong>DOT 장비</strong>
                                            <div>${memberDeviceDataCnt.dotDeviceCount }</div>
                                        </div>
                                        <div class="col-6"><strong>VENT 장비</strong>
                                            <div>${memberDeviceDataCnt.ventDeviceCount }</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="content">
        <div class="row">
            <div class="col-5">
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>장비 리스트 </strong>
                        </h3>
                        <div class="opt_wrap">
                            <hr>
                            <input type="hidden" id="popupDeviceIdx" value="${popupDeviceIdx}"/>
                            <input type="hidden" id="beforeIdx" value=""/>
                            <div class="col-12 mx-auto" id="Area1"
                                 style="height: 135px; overflow-x: hidden;">
                                <div class="row" id="deviceDiv"
                                     style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
                                    <c:forEach var="memberDeviceDataList"
                                               items="${memberDeviceDataList }">
                                        <div class="row col-12 mt10 device" style="cursor: pointer;"
                                             id="device${memberDeviceDataList.deviceIdx }"
                                             data-fileName="${memberDeviceDataList.fileName }"
                                             data-fileFlag="${memberDeviceDataList.fileFlag }"
                                             data-mainImageName="${memberDeviceDataList.mainImageName }"
                                             data-mainImageFlag="${memberDeviceDataList.mainImageFlag }"
                                             data-mainImageFilePath="${memberDeviceDataList.mainImageFilePath }"
                                             data-eastImageName="${memberDeviceDataList.eastImageName }"
                                             data-eastImageFlag="${memberDeviceDataList.eastImageFlag }"
                                             data-eastImageFilePath="${memberDeviceDataList.eastImageFilePath }"
                                             data-westImageName="${memberDeviceDataList.westImageName }"
                                             data-westImageFlag="${memberDeviceDataList.westImageFlag }"
                                             data-westImageFilePath="${memberDeviceDataList.westImageFilePath }"
                                             data-southImageName="${memberDeviceDataList.southImageName }"
                                             data-southImageFlag="${memberDeviceDataList.southImageFlag }"
                                             data-southImageFilePath="${memberDeviceDataList.southImageFilePath }"
                                             data-northImageName="${memberDeviceDataList.northImageName }"
                                             data-northImageFlag="${memberDeviceDataList.northImageFlag }"
                                             data-northImageFilePath="${memberDeviceDataList.northImageFilePath }"
                                             data-spaceFull="${memberDeviceDataList.space }"
                                             data-deviceTypeIdx="${memberDeviceDataList.deviceTypeIdx }"
                                             data-equiDt="${memberDeviceDataList.equipDt }"
                                             data-equiName="${memberDeviceDataList.equipName }"
                                             data-equiAddr="${memberDeviceDataList.equipAddr }"
                                             data-equiAddr2="${memberDeviceDataList.equipAddr2 }"
                                             data-spaceName="${memberDeviceDataList.stationName }"
                                             data-dCode="${memberDeviceDataList.dcode}"
                                             data-airMapUse="${memberDeviceDataList.airMapYn}"
                                             data-Lat="${memberDeviceDataList.lat }"
                                             data-lon="${memberDeviceDataList.lon }"
                                             data-deviceType="${memberDeviceDataList.deviceType }"
                                             data-createDt="${memberDeviceDataList.createDt }"
                                             data-spaceIdx="${memberDeviceDataList.spaceIdx }"
                                             data-etc="${memberDeviceDataList.etc }"
                                             data-departName="${memberDeviceDataList.departName }"
                                             data-departPhoneNumber="${memberDeviceDataList.departPhoneNumber }"
                                             data-salesName="${memberDeviceDataList.salesName }"
                                             data-set-temp="${memberDeviceDataList.setTemp }"
                                             data-related-device="${memberDeviceDataList.relatedDevice }"
                                        >
                                            <input type="radio" class="mr10" name="chkDevice"
                                                   id="chkDevice${memberDeviceDataList.deviceIdx }"
                                                   value="${memberDeviceDataList.deviceIdx }"
                                                   style='width: 20px; height: 20px; margin-top: 3px;'
                                                   onClick="getDeviceInfo(${memberDeviceDataList.deviceIdx });"/>
                                            <label for="chkDevice${memberDeviceDataList.deviceIdx }"
                                                   id="chkDeviceLabel${memberDeviceDataList.deviceIdx }">${memberDeviceDataList.deviceType }
                                                - ${memberDeviceDataList.serialNum }</label>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card">
                    <div class="card-body">
                        <h3>
                            <strong>공간 카테고리 </strong>
                        </h3>
                        <div class="opt_wrap">
                            <hr>
                            <div class="row ">
                                <div class="col-6">
                                    <div class="col-12 mx-auto" id="SpaceArea"
                                         style="height: 135px; overflow-x: hidden;">
                                        <div class="row" id="parentSpaceList"
                                             style="background-color:#e2e2e2;padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">

                                        </div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="col-12 mx-auto" id="SpaceArea2"
                                         style="height: 135px; overflow-x: hidden;">
                                        <div class="row" id="spaceList"
                                             style="background-color:#e2e2e2;padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-7">
                <div class="card">
                    <div class="card-body">
                        <div>
                            <div style="display: inline-block;">
                                <h3>
                                    <strong>장비 상세 정보</strong>
                                    <input type="button" id="updateBtn" class="btn btn-primary"
                                           value="수정"/>
                                </h3>
                            </div>
                            <div class="custom-control" id="fileDiv"
                                 style="display: inline-block; float: right;">
                                <label>
                                    <strong>설치 보고서 내려받기</strong><br/><a href="#"
                                                                        id="fileLink"><strong
                                        id="clientFileName"></strong></a>
                                </label>
                            </div>
                        </div>
                        <div class="opt_wrap" style="margin-bottom: 2rem;">
                            <input type="hidden" name="memberIdx" id="memberIdx" value="${idx }"/>
                            <hr>
                            <div class="row ">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="stationName">
                                            스테이션명 (<span style="color:red">*</span>)
                                        </label><br/>
                                        <input type="text" name="stationName" id="stationName"
                                               class="form-control"/>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <%--									<label style="margin-left:1.45rem">설치 위치 (<font style="color:red">*</font>)</label>--%>
                                    <div class="row col-12" style="padding: 0;">
                                        <div class="col-6">
                                            <div class="custom-control">
                                                <label for="Lat" style="font-size: 13px;">
                                                    위도(<span style="color:red">*</span>)
                                                </label>
                                                <input type="text" name="Lat" id="Lat"
                                                       class="form-control"/>
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <div class="custom-control">
                                                <label for="Lon" style="font-size: 13px;">
                                                    경도(<span style="color:red">*</span>)
                                                </label>
                                                <input type="text" name="Lon" id="Lon"
                                                       class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row mt20">
                                <div class="col-3">
                                    <div class="custom-control">
                                        <label for="airMapUse" style="font-size: 13px;">
                                            에어맵 사용 유무(<span style="color:red">*</span>)
                                        </label>
                                        <input type="checkbox" name="airMapUse" id="airMapUse"
                                               class="form-control" style="width: 1rem;
    height: 1rem;
    margin: 2% 3px 0 5px;
    vertical-align: middle;"/>
                                    </div>
                                </div>
                                <div class="col-3" id="SCodeContainer"
                                     style="margin-bottom: 0.5rem">
                                    <div class="custom-control">
                                        <label for="sCode" style="font-size: 13px;">
                                            행정시/도(<span style="color:red">*</span>)
                                        </label>
                                        <select name="sCode" id="sCode"
                                                class="form-control"></select>
                                    </div>
                                </div>
                                <div class="col-3" id="GCodeContainer"
                                     style="margin-bottom: 0.5rem">
                                    <div class="custom-control">
                                        <label for="gCode" style="font-size: 13px;">
                                            행정시/군/구(<span style="color:red">*</span>)
                                        </label>
                                        <select name="gCode" id="gCode" class="form-control">
                                            <option value="000">선택</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-3" id="DCodeContainer"
                                     style="margin-bottom: 0.5rem">
                                    <div class="custom-control">
                                        <label for="dCode" style="font-size: 13px;">
                                            행정읍/면/동(<span style="color:red">*</span>)
                                        </label>
                                        <select name="dCode" id="dCode" class="form-control">
                                            <option value="0000000000">선택</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="row mt20">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="departName">
                                            고객담당자(<span style="color:red">*</span>)
                                        </label>
                                        <input type="text" class="form-control" id="departName"
                                               name="departName" value="" placeholder="내용을 입력해 주세요">

                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="equip_dt">
                                            설치 일시(<span style="color:red">*</span>)
                                        </label><br/>
                                        <div class="input-group">
                                            <div class="input-group-prepend" id="datepicker_ic"
                                                 style="cursor:pointer;">
												<span class="input-group-text"> <i
                                                        class="far fa-calendar-alt"></i>
												</span>
                                            </div>
                                            <input type="text" class="form-control float-right"
                                                   name="equip_dt" id="equip_dt"
                                                   placeholder="클릭 후 선택"/>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row mt20">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="departPhoneNumber">
                                            고객연락처(<span style="color:red">*</span>)
                                        </label>
                                        <input type="text" class="form-control"
                                               id="departPhoneNumber" name="departPhoneNumber"
                                               value="" placeholder="내용을 입력해 주세요">
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="equipName">
                                            설치 담당자(<span style="color:red">*</span>)
                                        </label><br/>
                                        <input type="text" name="equipName" id="equipName"
                                               class="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row mt20">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="salesName">
                                            영업담당자 (<span style="color:red">*</span>)
                                        </label>
                                        <input type="text" class="form-control" id="salesName"
                                               name="salesName" value="" placeholder="내용을 입력해 주세요">
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="equipAddr">
                                            설치 주소(<span style="color:red">*</span>)
                                        </label><br/>
                                        <input type="text" name="equipAddr" id="equipAddr"
                                               class="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row mt20" style="margin-bottom:20px;">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="etc">비고</label><br/>
                                        <input type="text" name="etc" id="etc"
                                               class="form-control"/>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label for="equipAddr2">설치장소설명</label><br/>
                                        <input type="text" name="equipAddr2" id="equipAddr2"
                                               class="form-control" placeholder="(Ex, 신림동 주민센터 앞)"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row mt20" id="reportArea" style="display: none;margin-bottom:20px">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <form enctype="multipart/form-data" id="fileUploadForm">
                                            <div class="col-5"
                                                 style="display: inline-block; padding-left: 0;">
                                                <label>설치 보고서</label>
                                                <input id="targetFile" name="targetFile"
                                                       class="custom-select"
                                                       style="background: white; width: 170%;"
                                                       class="file"
                                                       type="file" multiple data-show-upload="false"
                                                       data-show-caption="true"/>
                                            </div>
                                            <div class="col-1"
                                                 style="display: inline-block; margin-left: 8rem;">
                                                <input type="button" id="fileUpdateBtn"
                                                       class="btn btn-primary" value="파일 업로드">
                                            </div>
                                        </form>
                                    </div>
                                </div>

                            </div>
                            <font id='NETboxTitle' style='font-size:1.25em; font-weight:bold;'>신기술인증 제어요소</font>
                             <div id="NETbox" style="border:2px solid #00a1ff;border-radius:0.25em;padding-bottom:30px;margin-top:5px;">
                            <div class="row mt20" id="setTempDiv">

                                 <div class="col-6" >
                                     <div class="custom-control">
                                         <label for="setTemp">설정 온도</label><br/>
                                         <input type="number" name="setTemp" id="setTemp"
                                                class="form-control"/>
                                     </div>
                                 </div>
                             </div>

                            <div class="row mt20" id="updateRelatedDiv">
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label>연동 OAQ 연동정보</label>
                                        <div class="col-12 mx-auto" id="relatedArea"
                                             style="overflow-x: hidden;">
                                            <div class="row" id="AddRelated"
                                                 style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="custom-control">
                                        <label>연동 OAQ 변경 가능 목록</label>
                                        <div class="col-12 mx-auto" id="relatedListArea"
                                             style="overflow-x: hidden;">
                                            <div style="display: inline-block; width: 70%;">
                                                <select class="form-control select2"
                                                        data-placeholder="선택"
                                                        id="relatedSelectArea">
                                                </select>
                                            </div>
                                            <div style="display: inline-block; width:20%;">
											<span class="btn btn-primary ml3"
                                                  onclick="insertRelatedDeviceBtn()"
                                                  style="cursor: pointer; padding-bottom: 0; padding-top: 0; margin-bottom: 15px;">
												변경
											</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                                </div>
                            <div class="row mt20" id="imageArea" style="display: none;">
                                <div class="col-12">
                                    <div class="custom-control">
                                        <label for="">설치 위치 사진 </label>
                                        <span style="color:red; display: none;" class="msg-point">&nbsp;&nbsp;
                                            (이미지를 클릭하면 다운로드 됩니다. )
                                        </span>
                                        <br/>
                                        <form enctype="multipart/form-data"
                                              id="imageFileUploadForm">
                                            <div class="col-2 imageFileArea" data-type="main"
                                                 style="display: inline-block;margin-right:5px;padding: 1px;border: 2px solid #00a1ff;border-radius: 10%;">
                                                <div class="alert" style="text-align: center;">
                                                    <strong>전면</strong>
                                                    <span style="top: 10px; right: 10px; position: absolute; cursor: pointer;">
														<strong style="color: red;"
                                                                onclick="deleteFile('Main')">X</strong>
													</span>
                                                </div>
                                                <img id="blahMain" class="blah" align='middle'
                                                     src="http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png"
                                                     alt="이미지를 선택해주세요" title=''/>
                                                <div>
                                                    <input type="file" id="inputImageFileMain"
                                                           name="inputImageFileMain" class="imgInp">
                                                </div>
                                            </div>
                                            <div class="col-2 imageFileArea" data-type="east"
                                                 style="display: inline-block;margin-right:5px;padding: 1px;border: 2px solid #00a1ff;border-radius: 10%;">
                                                <div class="alert" style="text-align: center;">
                                                    <strong>동</strong>
                                                    <span style="top: 10px; right: 10px; position: absolute; cursor: pointer;">
														<strong style="color: red;"
                                                                onclick="deleteFile('East')">X</strong>
													</span>
                                                </div>
                                                <img id="blahEast" class="blah" align='middle'
                                                     src="http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png"
                                                     alt="이미지를 선택해주세요" title=''/>
                                                <div>
                                                    <input type="file" id="inputImageFileEast"
                                                           name="inputImageFileEast" class="imgInp">
                                                </div>
                                            </div>
                                            <div class="col-2 imageFileArea" data-type="west"
                                                 style="display: inline-block;margin-right:5px;padding: 1px;border: 2px solid #00a1ff;border-radius: 10%;">
                                                <div class="alert" style="text-align: center;">
                                                    <strong>서</strong>
                                                    <span style="top: 10px; right: 10px; position: absolute; cursor: pointer;">
														<strong style="color: red;"
                                                                onclick="deleteFile('West')">X</strong>
													</span>
                                                </div>
                                                <img id="blahWest" class="blah" align='middle'
                                                     src="http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png"
                                                     alt="이미지를 선택해주세요" title=''/>
                                                <div>
                                                    <input type="file" id="inputImageFileWest"
                                                           name="inputImageFileWest" class="imgInp">
                                                </div>
                                            </div>
                                            <div class="col-2 imageFileArea" data-type="south"
                                                 style="display: inline-block;margin-right:5px;padding: 1px;border: 2px solid #00a1ff;border-radius: 10%;">
                                                <div class="alert" style="text-align: center;">
                                                    <strong>남</strong>
                                                    <span style="top: 10px; right: 10px; position: absolute; cursor: pointer;">
														<strong style="color: red;"
                                                                onclick="deleteFile('South')">X</strong>
													</span>
                                                </div>
                                                <img id="blahSouth" class="blah" align='middle'
                                                     src="http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png"
                                                     alt="이미지를 선택해주세요" title=''/>
                                                <div>
                                                    <input type="file" id="inputImageFileSouth"
                                                           name="inputImageFileSouth"
                                                           class="imgInp">
                                                </div>
                                            </div>
                                            <div class="col-2 imageFileArea" data-type="north"
                                                 style="display: inline-block;margin-right:5px;padding: 1px;border: 2px solid #00a1ff;border-radius: 10%;">
                                                <div class="alert" style="text-align: center;">
                                                    <strong>북</strong>
                                                    <span style="top: 10px; right: 10px; position: absolute; cursor: pointer;">
														<strong style="color: red;"
                                                                onclick="deleteFile('North')">X</strong>
													</span>
                                                </div>
                                                <img id="blahNorth" class="blah" align='middle'
                                                     src="http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png"
                                                     alt="이미지를 선택해주세요" title=''/>
                                                <div>
                                                    <input type="file" id="inputImageFileNorth"
                                                           name="inputImageFileNorth"
                                                           class="imgInp">
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <label class="mt20" id="txtVentTitle">환기청정기 연동정보</label>
                        <input type="button" id="VentupdateBtn" class="btn btn-primary" value="수정">
                        <div class="col-12 mx-auto mt10" id="ventArea"
                             style="height: 135px; overflow-x: hidden;">
                            <div class="row" id="VentDiv"
                                 style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
                            </div>
                        </div>
                        <div class="row mt20" id="updateVentDiv">
                            <div class="col-6">
                                <label>등록 환기청정기 목록</label>
                                <div class="col-12 mx-auto" id="ventListArea"
                                     style="height: 135px; overflow-x: hidden;">
                                    <div class="row" id="AddVentList"
                                         style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
                                    </div>
                                </div>
                            </div>
                            <div class="col-6">
                                <label>미등록 환기청정기 목록</label>
                                <div class="col-12 mx-auto" id="ventListArea2"
                                     style="height: 135px; overflow-x: hidden;">
                                    <div style="display: inline-block; width: 70%;">
                                        <select class="form-control select2" data-placeholder="선택"
                                                id="ventSelectArea">
                                        </select>
                                    </div>
                                    <div style="display: inline-block; width:20%;">
											<span class="btn btn-primary ml3"
                                                  onclick="insertVentDeviceBtn()"
                                                  style="cursor: pointer; padding-bottom: 0; padding-top: 0; margin-bottom: 15px;">
												등록
											</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <section class="btn_area" style="margin-left: 2%;">
                        <div class="row">
                            <div class="col-12">
                                <input type="button" class="btn btn-primary ml3"
                                       style="min-width:100px;" id="deleteBtn" value="삭제"/>
                                <input type="button" class="btn btn-primary ml3"
                                       style="min-width:100px;" id="listBtn" value="목록으로"/>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </section>
</div>
<%@ include file="../common/footer.jsp" %>