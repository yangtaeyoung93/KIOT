<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ include file="../common/header.jsp" %>
<script src="/resources/share/js/system/deviceModelDetail.js"></script>
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
                        <li class="breadcrumb-item"><a
                                href="${pageContext.request.contextPath}/system/device/model/list">장비
                            모델</a></li>
                        <li class="breadcrumb-item active"><strong>${item }</strong></li>
                    </ol>
                </div>
            </div>
        </div>
    </section>
    <form name="boardForm" method="get">
        <section class="content">
            <div class="row">
                <div class="col-12">
                    <div class="card">
                        <div class="card-body">
                            <div class="search_area" style="width: 75%;">
                                <div class="search_top" style="max-width:815px;">
                                    <input type="hidden" name="chk_model" id="chk_model" value=""/>
                                    <input type="hidden" id="deviceModelIdx"
                                           value="${deviceModelIdx }"/>
                                    <label for="deviceTypeIdx">장비 유형</label>

                                    <select name="deviceTypeIdx" id="deviceTypeIdx"
                                            class="custom-select custom-select-sm form-control form-control-sm ml15 mr20"
                                            style="max-width:90px;height:38px;">
                                        <c:forEach var="deviceType" items="${deviceType }">
                                            <option value="${deviceType.idx }"
                                                    <c:if test="${deviceType.idx == data.deviceTypeIdx}">
                                                        selected
                                                    </c:if>
                                            >${deviceType.deviceType }</option>
                                        </c:forEach>
                                    </select>

                                    <label for="deviceModel">모델명 (<span style="color:red">*</span>)</label>
                                    <input type="text" class="form-control mr10" name="deviceModel"
                                           id="deviceModel" value="${data.deviceModel }"
                                           placeholder="내용을 입력해 주세요"
                                           style="max-width:250px; display:inline; vertical-align: middle;">
                                    <label for="description">설명</label>
                                    <input type="text" class="form-control" id="description"
                                           value="${data.description }" placeholder="내용을 입력해 주세요"
                                           style="max-width:250px; display:inline; vertical-align: middle;">
                                </div>
                                <div id="chk_txt" class="mt10 mr10"
                                     style="margin-left: 363px; width: 340px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-6">
                    <div class="card">
                        <div class="card-body">
                            <h3>
                                <strong>장비 속성</strong>
                            </h3>
                            <div class="opt_wrap"
                                 style="height: 440px; overflow-y: auto; overflow-x: hidden;padding-right: 10px;">
                                <hr/>
                                <div class="row">
                                    <c:forEach var="deviceAt" items="${deviceAt }">
                                        <div class="opt_div col-4">
                                            <h5>${deviceAt.attributeName }</h5>
                                            <c:choose>
                                                <c:when test="${deviceAt.inputType eq 'T'}">
                                                    <input type="text"
                                                           class="form-control model-attribute"
                                                           name="${deviceAt.idx }"
                                                           <c:if test="${deviceAt.gubun == 1}">style="font-weight: bolder;background-color:#20c997; color:white;"</c:if>
                                                           id="${deviceAt.idx }"
                                                           value="${deviceAt.attributeValue }"
                                                           placeholder="내용을 입력해 주세요"/>
                                                </c:when>
                                                <c:when test="${deviceAt.inputType eq 'S'}">
                                                    <select name="${deviceAt.idx }"
                                                            id="${deviceAt.idx }"
                                                            class="model-attribute custom-select custom-select-sm form-control"
                                                            <c:if test="${deviceAt.gubun == 1}">style="font-weight: bolder;background-color:#20c997;color:white;"</c:if> >
                                                        <option value="1등급"
                                                                <c:if test="${deviceAt.attributeValue eq '1등급'}">
                                                                    selected
                                                                </c:if>
                                                        >1등급
                                                        </option>
                                                        <option value="2등급"
                                                                <c:if test="${deviceAt.attributeValue eq '2등급'}">
                                                                    selected
                                                                </c:if>
                                                        >2등급
                                                        </option>
                                                        <option value="3등급"
                                                                <c:if test="${deviceAt.attributeValue eq '3등급'}">
                                                                    selected
                                                                </c:if>
                                                        >3등급
                                                        </option>
                                                        <option value="등급외"
                                                                <c:if test="${deviceAt.attributeValue ne '1등급' && deviceAt.attributeValue ne '2등급' && deviceAt.attributeValue ne '3등급'}">
                                                                    selected
                                                                </c:if>
                                                        >등급외
                                                        </option>
                                                    </select>
                                                </c:when>
                                                <c:otherwise>
                                                    <select name="${deviceAt.idx }"
                                                            id="${deviceAt.idx }"
                                                            class="model-attribute custom-select custom-select-sm form-control"
                                                            <c:if test="${deviceAt.gubun == 1}">style="font-weight: bolder;background-color:#20c997;color:white;"</c:if> >
                                                        <option value="Y">Y</option>
                                                        <option value="N" ${selected }
                                                                <c:if test="${deviceAt.gubun == 0}">
                                                                    selected
                                                                </c:if>
                                                        >N
                                                        </option>
                                                    </select>
                                                </c:otherwise>
                                            </c:choose>
                                            <hr/>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <div class="card-body">
                            <h3>
                                <strong>장비 속성(VENT)</strong>
                            </h3>
                            <div class="opt_wrap"
                                 style="height: 120px; overflow-y: auto; overflow-x: hidden;padding-right: 10px;">
                                <hr/>
                                <div class="row">
                                    <c:forEach var="deviceAtVent" items="${deviceAtVent }">
                                        <div class="opt_div col-4">
                                            <h5>${deviceAtVent.attributeName }</h5>
                                            <c:choose>
                                                <c:when test="${deviceAtVent.inputType eq 'T'}">
                                                    <input type="text"
                                                           class="form-control model-attribute"
                                                           name="${deviceAtVent.idx }"
                                                           <c:if test="${deviceAtVent.gubun == 1}">style="font-weight: bolder;background-color:#20c997; color:white;"</c:if>
                                                           id="${deviceAtVent.idx }"
                                                           value="${deviceAtVent.attributeValue }"
                                                           placeholder="내용을 입력해 주세요"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <select name="${deviceAtVent.idx }"
                                                            id="${deviceAtVent.idx }"
                                                            class="model-attribute custom-select custom-select-sm form-control"
                                                            <c:if test="${deviceAtVent.gubun == 1}">style="font-weight: bolder;background-color:#20c997;color:white;"</c:if> >
                                                        <option value="Y">Y</option>
                                                        <option value="N" ${selected }
                                                                <c:if test="${deviceAtVent.gubun == 0}">
                                                                    selected
                                                                </c:if>
                                                        >N
                                                        </option>
                                                    </select>
                                                </c:otherwise>
                                            </c:choose>
                                            <hr/>
                                        </div>
                                    </c:forEach>

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <div class="card-body">
                            <h3>
                                <strong>장비 이미지 파일</strong>
                            </h3>
                            <div class="opt_wrap"
                                 style="height: 223px; overflow-y: auto; overflow-x: hidden;padding-right: 10px;">
                                <hr/>
                                <div class="row">
                                    <div class="opt_div col-12">
                                        <input type="file" accept="image/*" id="imageSelector">
                                        <button style="width: 50px; height: 30px; background: #fff; border: 1px solid #ccc; margin-right: 20px; margin-left: -41px;"
                                                onclick="deleteImage()">
                                            삭제
                                        </button>
                                        <img src="/NAS2_NFS/IOT_KITECH/DEVICE_MODEL/${data.deviceModel}.${data.imageFile}"
                                             value="${data.imageFile}" id="deviceImage"
                                             style="height: 10rem; width: 10rem; float: right; margin-right: 6rem;">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="card">
                        <div class="card-body">
                            <h3>
                                <strong>측정 요소</strong>
                            </h3>
                            <div class="opt_wrap"
                                 style="height: 440px; overflow-y: auto; overflow-x: hidden;padding-right: 10px;">
                                <hr/>
                                <div class="row">
                                    <c:forEach var="deviceEl" items="${deviceEl }">
                                        <div class="opt_div col-4">
                                            <h5>${deviceEl.korName }</h5>
                                            <select name="${deviceEl.idx }" id="${deviceEl.idx }"
                                                    class="model-elements custom-select custom-select-sm form-control"
                                                    <c:if test="${deviceEl.gubun == 1}">style="font-weight: bolder;background-color:#20c997;color:white;"</c:if>>
                                                <option value="Y">Y</option>
                                                <option value="N" ${selected }
                                                        <c:if test="${deviceEl.gubun == 0}">
                                                            selected
                                                        </c:if>
                                                >N
                                                </option>
                                            </select>
                                            <hr/>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <div class="card-body">
                            <h3>
                                <strong>측정 요소(VENT)</strong>
                            </h3>
                            <div class="opt_wrap"
                                 style="height: 440px; overflow-y: auto; overflow-x: hidden;padding-right: 10px;">
                                <hr/>
                                <div class="row">
                                    <c:forEach var="deviceElVent" items="${deviceElVent }">
                                        <div class="opt_div col-4">
                                            <h5>${deviceElVent.korName }</h5>
                                            <select name="${deviceElVent.idx }"
                                                    id="${deviceElVent.idx }"
                                                    class="model-elements custom-select custom-select-sm form-control"
                                                    <c:if test="${deviceElVent.gubun == 1}">style="font-weight: bolder;background-color:#20c997;color:white;"</c:if>>
                                                <option value="Y">Y</option>
                                                <option value="N" ${selected }
                                                        <c:if test="${deviceElVent.gubun == 0}">
                                                            selected
                                                        </c:if>
                                                >N
                                                </option>
                                            </select>
                                            <hr/>
                                        </div>
                                    </c:forEach>
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
                    <input type="button" class="btn btn-primary ml3"
                           style="min-width:100px;" ${btn }/>
                    <input type="button" class="btn btn-primary ml3" style="min-width:100px;"
                           id="listBtn" value="목록으로"/>
                </div>
            </div>
        </section>
    </form>
</div>

<%@ include file="../common/footer.jsp" %>
