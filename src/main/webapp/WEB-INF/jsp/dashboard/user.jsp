<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/dashboard/user.js"></script>
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
	</section>
	<section class="content">
		<div class="row">
			<div class="col-12">
				<div class="row">
					<!--  <div class="col-4">
						<div class="small-box" style="background-color:#2BC0EF; height:160px;">
							<div class="inner" style="color: white;">
								<h3 id="total_mem_cnt"></h3>
								<p><strong>전체 사용자</strong></p>
							</div>
							<i class="fa fa-user" style=" font-size: 4.5rem; float: right; position: absolute; top: 15px; right: 15px; color: white;"></i>
						</div>
					</div>-->
					<div class="col-6">
						<div class="small-box" style="background-color:#F39C13; height:160px;">
							<div class="inner"  style="color: white;">
								<!--  <h3><font id="mem_per_cnt"></font><sup style="font-size: 20px">% (<font id="mem_total_cnt"></font>)</sup></h3>-->
								<h3><font id="mem_total_cnt"></font></h3>
								<p><strong>사용자 계정</strong></p>
							</div>
							<i class="fa fa-user" style=" font-size: 4.5rem; float: right; position: absolute; top: 15px; right: 15px; color: white;"></i>
						</div>
					</div>
					<div class="col-6">
						<div class="small-box" style="background-color:#2BA659; height:160px;">
							<div class="inner"  style="color: white;">
								<!--  <h3><font id="group_per_cnt"></font><sup style="font-size: 20px">% (<font id="group_total_cnt"></font>)</sup></h3>-->
								<h3><font id="group_total_cnt"></font></h3>
								<p class="mb5"><strong>그룹 계정</strong></p>
								<p class="mb5"><strong id="group_member_cnt"></strong></p>
								<p class="mb5"><strong id="did_cnt"></strong></p>
							</div>
							<i class="fa fa-user" style=" font-size: 4.5rem; float: right; position: absolute; top: 15px; right: 15px; color: white;"></i>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- /.row -->
		<div class="row">
			<div class="col-6">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>사용자 장비 현황</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
							<button type="button" class="btn btn-tool"
								data-card-widget="remove">
								<i class="fas fa-remove"></i>
							</button>
						</div>
					</div>
					<div class="card-body" id="userDeviceChartDiv" style="height: 300px;">
					</div>
					<!-- /.card-body -->
				</div>
			</div>
			<div class="col-6">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>사용자 로그인 현황</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
							<button type="button" class="btn btn-tool"
								data-card-widget="remove">
								<i class="fas fa-remove"></i>
							</button>
						</div>
					</div>
					<div class="card-body" id="userLoginChartDiv" style="height: 300px;">
					</div>
					<!-- /.card-body -->
				</div>
			</div>
			<!-- /.col -->
		</div>
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->