<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/deviceList.js"></script>
<!-- ##### Header Area End ##### -->

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
		<!-- /.container-fluid -->
	</section>
	<!-- Main content -->
	<section class="content">
		<div class="row">
			<input type="hidden" value="${selectDeviecType }" id="selectDeviecType" name="selectDeviecType"/>
			<div class="col-12">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<div class="search_area" style="width:70%;">
							<div class="search_bottom" style="max-width: 630px;">
								<strong>모델</strong>
								<select name="searchValue2" id="searchValue2" class="custom-select custom-select-sm form-control form-control-sm ml15 mr20" style="max-width:90px;height:38px;">
									<option value="">전체</option>
									<c:forEach var="model" items="${modelList }">
										<option value="${model.deviceModel }" <c:if test="${deviceData.deviceModel eq model.deviceModel }"> selected </c:if> data-value="${model.deviceTypeIdx }">${model.deviceModel }</option>
									</c:forEach>
								</select>
								<strong>시리얼번호</strong>
								<input type="text" class="form-control mr10" id="searchValue3" name= "searchValue3" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
								
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="searchBtn" value="검 색">
							</div>
						</div>
						<div class="row mt10" style="float:right;">
							<strong>사용 여부</strong>
							<div class="custom-control custom-radio ml10 mr10" style="display:inline-block;">
								<input class="custom-control-input" type="radio" id="customRadio3" name="useYnRadio" r-data="Y" checked="" > 
								<label for="customRadio3" class="custom-control-label">Y</label>
							</div>
							<div class="custom-control custom-radio mr20"style="display:inline-block;">
								<input class="custom-control-input" type="radio" id="customRadio4" name="useYnRadio" r-data="N"> 
								<label for="customRadio4" class="custom-control-label">N</label>
							</div>
						</div>
						<table id="deviceTable" class="table table-bordered table-hover text-center">
							<thead>
                                <tr>
                                	<th class="bgGray1"><input type="checkbox" name="all_chk" id="all_chk" style="margin-left: 17px;" class="chBox" /></th>
                                	<th class="bgGray1">NO</th>
                                    <th class="bgGray1">장비 모델</th>
                                    <th class="bgGray1">시리얼 번호</th>
                                    <th class="bgGray1">생산 일자</th>
                                    <th class="bgGray1">등록 일시</th>
                                    <th class="bgGray1">사용 여부</th>
                                </tr>
							</thead>
						</table>
						<div class="row">
							<div class="col-3">
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="등록" onClick="goInsert();"/>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="삭제" id="deleteBtn"/>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px; float: right;" value="일괄 등록" id="excelShowBtn"/>
							</div>
						</div>
						<div id="excelUploadDiv" style="display: none;">
							<hr/>
							<form id="excelUpForm" method="post" action="" role="form"
								enctype="multipart/form-data">
								<div class="row">
									<div class="col-3">
										<label>장비 모델</label> <select name="deviceModelIdx"
											id="deviceModelIdx" class="custom-select">
											<c:forEach var="model" items="${modelList }">
												<option value="${model.idx }" type-idx="${model.deviceTypeIdx }"
													selected>${model.deviceModel }</option>
											</c:forEach>
										</select> <br />
									</div>
									<div class="col-3">
										<label>엑셀 업로드</label> <input id="excel" name="excel"
											class="custom-select" style="background: white;" class="file"
											type="file" multiple data-show-upload="false"
											data-show-caption="true" />
									</div>
									<div class="col-2">
										<label><a href="/system/device/excel"><strong>양식 다운로드</strong></a></label>
										<br/>
										<button type="button" id="excelUp" onclick="check()"
											class="btn btn-primary ml3"style="min-width:100px;">등록</button>
									</div>
								</div>
							</form>
						</div>
					</div>
					<!-- /.card-body -->
				</div>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->