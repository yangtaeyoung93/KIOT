<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/deviceTypeList.js"></script>
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
						<table id="deviceTable" class="table table-bordered table-hover text-center">
							<thead>
								<tr>
									<th class="bgGray1" style="width:20px;">NO</th>
									<th class="bgGray1">장비 유형</th>
									<th class="bgGray1">이 름</th>
									<th class="bgGray1">설 명</th>
									<th class="bgGray1">등록 일시</th>
									<th class="bgGray1" style="width: 100px;">사용 여부</th>
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
								<input type="hidden" id="chk_type" value="0"/>
								<input type="hidden" id="chk_type_name" value="0"/>
								<input type="hidden" id="deviceTypeIdx"/>
								<label for="exampleInputEmail1">장비 유형 (<font style="color:red">*</font>)</label><br/> <input
									type="text" class="form-control col-9" id="type"
									placeholder="Enter ..." style="display:inline-block;"> 
								<input type="button" class="btn btn-primary ml3 d-inline-block;" id="chk_type_btn" style="min-width:100px; vertical-align: bottom;" value="중복체크"/>
								<div id="chk_txt" class="mt10"></div>
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">이 름 (<font style="color:red">*</font>)</label><br/> 
								<input type="text" class="form-control col-9" id="typeName"
									placeholder="Enter ..." style="display:inline-block;">
								<input type="button" class="btn btn-primary ml3 d-inline-block;" id="chk_type_name_btn" style="min-width:100px; vertical-align: bottom;" value="중복체크"/>
								<div id="chk_txt2" class="mt10"></div>
							</div>
							<div class="form-group">
								<label for="exampleInputEmail1">설 명</label> <input
									type="text" class="form-control" id="description"
									placeholder="Enter ...">
							</div>
						</div>
						<!-- /.card-body -->
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