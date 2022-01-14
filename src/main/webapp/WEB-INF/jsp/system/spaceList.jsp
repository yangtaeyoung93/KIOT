<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/spaceList.js"></script>
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
			<div class="col-12">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<div class="search_area" style="width:70%;">
							<div class="search_bottom" style="max-width: 820px;">
								<strong>유형</strong> 
								<select name="searchType" id="searchType" onchange="chageLangSelect()" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									<c:forEach var="deviceType" items="${deviceType }">
										<c:if test="${deviceType.idx != 7 }">
										<option value ="${deviceType.idx }">${deviceType.deviceType }</option>
										</c:if>
									</c:forEach>
								</select>
								<strong>상위 카테고리</strong>
								<select name="searchType2" id="searchType2" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									
								</select>
								<input type="text" class="form-control" id="sch_station_name" name= "sch_station_name" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
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
						<div style="clear:both"></div>
						<table id="spaceTable" class="table table-bordered table-hover">
							<thead>
								<tr>
									<th class="bgGray1">NO</th>
									<th class="bgGray1">장비 유형</th>
									<th class="bgGray1">카테고리명</th>
									<th class="bgGray1">등록일시</th>
									<th class="bgGray1" style="width: 100px;">사용 여부</th>
								</tr>
							</thead>
						</table>
						<div>
							<input class="btn-primary btn mt20" data-toggle="modal"
								id="openModalBtn" data-target="#modal-lg" type="button"
								value="등 록" />
							<hr />
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
	<!-- .Modal -->
	<div class="modal fade" id="modal-lg" style="display: none;"
		aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"><strong>설치 공간 카테고리 등록/수정</strong></h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<div class="form-group">
								<input type="hidden" id="stationIdx"/>
								<label for="exampleInputEmail1">장비 유형</label>
								<select class="form-control" id="deviceType" name="deviceType" onChange="stationChange(this.value);">
									<c:forEach var="deviceType" items="${deviceType }">
										<c:if test="${deviceType.idx != 7 }">
										<option class="deviceType" value="${deviceType.idx }">${deviceType.deviceType }</option>
										</c:if>
									</c:forEach>
								</select>
							</div>
							<div class="form-group">
								<label for="parentStation">상위 카테고리</label>
								<select class="form-control" id="parentStation" name="parentStation">
								</select>
							</div>
							<div class="form-group">
								<label for="stationName">설치 공간 카테고리명 (<font style="color:red">*</font>)</label> 
								<input type="text" class="form-control" id="stationName"
									placeholder="설치 공간 카테고리명" >
							</div>
							
							<div class="form-group">
								<label for="stationOrder">표출 순서</label> <input
									type="number" class="form-control" id="stationOrder"
									placeholder="표출 순서">
							</div>
						</div>
						<!-- /.card-body -->
						<div class="card-footer">
							<button type="button" id="insertBtn" class="btn btn-primary">등록</button>
							<button type="button" id="updateBtn" class="btn btn-primary">수정</button>
							<button type="button" id="deleteBtn"  class="btn btn-primary" >삭제</button>
						</div>
					</form>
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
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->