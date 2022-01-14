<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/deviceAttributeList.js"></script>
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
						<li class="breadcrumb-item active">${item }</li>
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
						<div class="search_area" >
							<div class="search_bottom" style="max-width: 420px;">
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									<option value ="attributeName">장비 속성명</option>
									<option value ="attributeCode">장비 속성코드</option>
								</select>
								<input type="text" class="form-control" id="sch_station_name" name= "sch_station_name" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
								<!--  <input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="searchBtn" value="검 색">-->
							</div>
						</div>
						<table id="deviceTable" class="table table-bordered table-hover text-center">
							<thead>
								<tr>
									<th class="bgGray1" style="width:20px;"></th>
									<th class="bgGray1">장비 속성명</th>
									<th class="bgGray1">장비 속성코드</th>
									<th class="bgGray1">입력 타입</th>
								</tr>
							</thead>
						</table>
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
					<h4 class="modal-title"><strong>장비 유형 등록</strong></h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<div class="form-group">
								<input type="hidden" id="deviceTypeIdx"/>
								<label for="exampleInputEmail1">장비 유형</label> <input
									type="text" class="form-control" id="type"
									placeholder="Enter ..."> 
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">이 름</label> 
								<input type="text" class="form-control" id="typeName"
									placeholder="Enter ...">
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">설 명</label> <input
									type="text" class="form-control" id="description"
									placeholder="Enter ...">
							</div>
							<div class="form-group">
								<label>사용 여부</label> 
								<select class="form-control" id="useYn" name="useYn">
									<option id="Y">Y</option>
									<option id="N">N</option>
								</select>
							</div>
						</div>
						<!-- /.card-body -->
						<div class="card-footer">
							<button type="button" id="insertBtn" class="btn btn-primary">등록</button>
							<button type="button" id="updateBtn" class="btn btn-primary">수정</button>
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