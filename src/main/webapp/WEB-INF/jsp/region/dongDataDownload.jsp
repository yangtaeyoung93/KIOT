<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/region/dongDataDownload.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="/resources/plugins/jquery/jquery.fileDownload.js "></script>
<!-- Select2 -->
<script src="/resources/plugins/select2/js/select2.full.min.js"></script>
<!-- ##### Header Area End ##### -->

<style>
.filter-cli {
	background: rebeccapurple;
	color: white;
	border-radius: 5px;
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
		<!-- /.container-fluid -->
	</section>
	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-12">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<div class="search_area" style="width: 90%">
							<div class="search_bottom mt10" style="max-width: 510px;">
								<div class="custom-control"style="width: 100%; padding-left:0px;">
						 			<label for="searchDfname1">μ/λ λΆλ₯</label>
						 			<div class="row" style="width:100%;margin-right: 0;margin-left: 0;">
										<select name="searchDfname1" id="searchDfname1" class="area-search custom-select custom-select-sm form-control form-control-sm mr10" style="width: 32%; display:inline-block;">
										</select>
										<select name="searchDfname2" id="searchDfname2" class="area-search custom-select custom-select-sm form-control form-control-sm mr10" style="width: 30%; display:inline-block;">
										</select>
										<select name="searchDfname3" id="searchDfname3" class="area-search custom-select custom-select-sm form-control form-control-sm" style="width: 30%; display:inline-block;">
										</select>
									</div>
								</div>
							</div>
						</div>
						<div style="display:inline-block; float: right;">
							<select name="searchPmType" id="searchPmType"
								class="custom-select custom-select-sm form-control form-control-sm mr10"
								style="width: 100px; height: 38px;">
								<option value="pm10">λ―ΈμΈλ¨Όμ§</option>
								<option value="pm25">μ΄λ―ΈμΈλ¨Όμ§</option>
							</select> 
							<select name="searchStandard" id="searchStandard"
								class="custom-select custom-select-sm form-control form-control-sm mr10"
								style="width: 100px; height: 38px;">
								<option value="sum">10λΆ νκ· </option>
								<option value="hour-standard">μ μ λ°μ΄ν°</option>
								<option value="1h-avg-none">1μκ° νκ· </option>
								<option value="1d-avg-none">1μΌ νκ· </option>
								<option value="1n-avg-none">1κ°μ νκ· </option>
							</select> 
							<input type="text" class="form-control d-inline-block" name="startDt" id="startDt" placeholder="μμμΌμ ν΄λ¦­ ν μ ν" style="width: 100px; vertical-align: middle;"> 
							~ 
							<input type="text" class="form-control d-inline-block mr10" name="endDt" id="endDt" placeholder="μ’λ£μΌμ ν΄λ¦­ ν μ ν" style="width: 100px; vertical-align: middle;"> 
							<input type="button" class="btn btn-primary ml3 mr10" style="min-width: 100px; float: right;" onclick="dataDownload()" value="λ°μ΄ν° λ€μ΄λ‘λ">
						</div>
						<table id="dongTable" class="table table-bordered table-hover" style="text-align: center;">
							<thead>
								<tr>
									<th class="bgGray1">νμ λλͺ</th>
									<th class="bgGray1">λ―ΈμΈλ¨Όμ§ </th>
									<th class="bgGray1">λ―ΈμΈλ¨Όμ§ λ±κΈ</th>
									<th class="bgGray1">μ΄λ―ΈμΈλ¨Όμ§ </th>
									<th class="bgGray1">μ΄λ―ΈμΈλ¨Όμ§ λ±κΈ</th>
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
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<div title="λ°μ΄ν°λ₯Ό λ€μ΄λ‘λ μ€μλλ€." id="preparing-file-modal"
	style="display: none;">
	<div id="progressbar"
		style="width: 100%; height: 22px; margin-top: 20px;"></div>
</div>
<div title="Error" id="error-modal" style="display: none;">
	<p>νμΌ μμ±μ μ€ν¨νμ¨μ΅λλ€.</p>
</div>
<!-- ##### Footer Area End ##### -->