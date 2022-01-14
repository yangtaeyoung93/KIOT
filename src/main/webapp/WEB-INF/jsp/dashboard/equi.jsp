<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/dashboard/equi.js"></script>
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
			<div class="col-md-2 col-md-offset-1" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box infographic-box bg-info">
					<div class="inner">
						<i class="fa fa-power-off dashboard_power_ic"></i>
						<h3 id="total_device_cnt"></h3>
						<p class="mb5"><strong>전체 장비</strong></p>
						<p class="mb5"><strong id="total_con_device_cnt"></strong></p>
						<p class="mb5"><strong id="total_not_device_cnt"></strong></p>
					</div>
					
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-success">
					<div class="inner">
						<i class="fa fa-power-off dashboard_power_ic"></i>
						<h3 id="total_iaq_cnt"></h3>
						<p class="mb5"><strong>IAQ</strong></p>
						<p class="mb5"><strong id="total_con_iaq_cnt"></strong></p>
						<p class="mb5"><strong id="total_not_iaq_cnt"></strong></p>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-purple">
					<div class="inner">
						<i class="fa fa-power-off dashboard_power_ic"></i>
						<h3 id="total_oaq_cnt"></h3>
						<p class="mb5"><strong>OAQ</strong></p>
						<p class="mb5"><strong id="total_con_oaq_cnt"></strong></p>
						<p class="mb5"><strong id="total_not_oaq_cnt"></strong></p>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-danger">
					<div class="inner">
						<i class="fa fa-power-off dashboard_power_ic"></i>
						<h3 id="total_dot_cnt"></h3>
						<p class="mb5"><strong>DOT</strong></p>
						<p class="mb5"><strong id="total_con_dot_cnt"></strong></p>
						<p class="mb5"><strong id="total_not_dot_cnt"></strong></p>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-warning" style="color:white !important;">
					<div class="inner">
						<i class="fa fa-power-off dashboard_power_ic"></i>
						<h3 id="total_vent_cnt"></h3>
						<p class="mb5"><strong>VENT</strong></p>
						<p class="mb5"><strong id="total_con_vent_cnt"></strong></p>
						<p class="mb5"><strong id="total_not_vent_cnt"></strong></p>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-4">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>유형별/장비현황</strong></h3>
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
					<!-- /.card-header -->
					<div class="card-body" id="barChart" style="height:300px;">
					</div>
				</div>
			</div>
			<div class="col-8">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>시간별/장비현황</strong></h3>
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
					<!-- /.card-header -->
					<div class="card-body" id="timeChartdiv" style="height:300px;">
					</div>
				</div>
			</div>
		</div>
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->