<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/dashboard/receive.js"></script>
<!-- ##### Header Area End ##### -->

<div class="content-wrapper">
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1 style="display:inline-block;">
						<strong>${item }</strong>
					</h1>
					<select name="sch_order" id="sch_order" class="ml10" onChange="getDataList();" style="width:120px;height:35px; display: none;">
						<option value="USER" selected>사용자</option>
						<option value="ALL">전체</option>
					</select>
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
			<input type="hidden" name="h_total_cnt" id="h_total_cnt" />
			<input type="hidden" name="h_total_ok_cnt" id="h_total_ok_cnt" />
			<input type="hidden" name="h_total_nok_cnt" id="h_total_nok_cnt" />
			
			<input type="hidden" name="h_iaq_cnt" id="h_iaq_cnt" />
			<input type="hidden" name="h_iaq_ok_cnt" id="h_iaq_ok_cnt" />
			<input type="hidden" name="h_iaq_nok_cnt" id="h_iaq_nok_cnt" />
			
			<input type="hidden" name="h_oaq_cnt" id="h_oaq_cnt" />
			<input type="hidden" name="h_oaq_ok_cnt" id="h_oaq_ok_cnt" />
			<input type="hidden" name="h_oaq_nok_cnt" id="h_oaq_nok_cnt" />
			
			<input type="hidden" name="h_dot_cnt" id="h_dot_cnt" />
			<input type="hidden" name="h_dot_ok_cnt" id="h_dot_ok_cnt" />
			<input type="hidden" name="h_dot_nok_cnt" id="h_dot_nok_cnt" />
			
			<input type="hidden" name="h_vent_cnt" id="h_vent_cnt" />
			<input type="hidden" name="h_vent_ok_cnt" id="h_vent_ok_cnt" />
			<input type="hidden" name="h_vent_nok_cnt" id="h_vent_nok_cnt" />
			
			<input type="hidden" name="h_total_ok_per_result" id="h_total_ok_per_result" value=""/>
			<input type="hidden" name="h_iaq_ok_per_result" id="h_iaq_ok_per_result" value=""/>
			<input type="hidden" name="h_oaq_ok_per_result" id="h_oaq_ok_per_result" value=""/>
			<input type="hidden" name="h_dot_ok_per_result" id="h_dot_ok_per_result" value=""/>
			<input type="hidden" name="h_vent_ok_per_result" id="h_vent_ok_per_result" value=""/>
			
			
			<div class="col-md-2 col-md-offset-1" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-info">
					<div class="inner">
						<p class="mb5"><strong>전체</strong></p>
						<h3 id="total_cnt"><font></font><!-- 수량(수신율) --><sup style="font-size: 20px"> <font id=""><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></font></sup></h3>						
						<p class="mb5"><strong id="total_ok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
						<p class="mb5"><strong id="total_nok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
					</div>
					
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-success">
					<div class="inner">
						<!--  <h3><font id="iaq_Calc"></font><sup style="font-size: 20px">% (<font id="iaq_cnt"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></font>)</sup></h3>-->
						<p class="mb5"><strong>IAQ</strong></p>
						<h3><font id="iaq_cnt"><span style="color: #696969; backgroud: white; font-size:20px;"><strong>데이터 호출중</strong></span></font></h3>
						<p class="mb5"><strong id="iaq_ok"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></strong></p>
						<p class="mb5"><strong id="iaq_nok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-warning">
					<div class="inner">
						<!--  <h3><font id="oaq_Calc"></font><sup style="font-size: 20px">% (<font id="oaq_cnt"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></font>)</sup></h3>-->
						<p class="mb5"><strong>OAQ</strong></p>
						<h3><font id="oaq_cnt"><span style="color: #696969; backgroud: white; font-size:20px;"><strong>데이터 호출중</strong></span></font></h3>
						<p class="mb5"><strong id="oaq_ok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
						<p class="mb5"><strong id="oaq_nok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-danger">
					<div class="inner">
						<!--  <h3><font id="dot_Calc"></font><sup style="font-size: 20px">% (<font id="dot_cnt"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></font>)</sup></h3>-->
						<p class="mb5"><strong>DOT</strong></p>
						<h3><font id="dot_cnt"><span style="color: #696969; backgroud: white; font-size:20px;"><strong>데이터 호출중</strong></span></font></h3>
						<p class="mb5"><strong id="dot_ok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
						<p class="mb5"><strong id="dot_nok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="small-box bg-blue">
					<div class="inner">
						<!--  <h3><font id="vent_Calc"></font><sup style="font-size: 20px">% (<font id="vent_cnt"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></font>)</sup></h3>-->
						<p class="mb5"><strong>VENT</strong></p>
						<h3><font id="vent_cnt"><span style="color: #696969; backgroud: white; font-size:20px;"><strong>데이터 호출중</strong></span></font></h3>
						<p class="mb5"><strong id="vent_ok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
						<p class="mb5"><strong id="vent_nok"><span style="color: #696969;"><strong>데이터 호출중</strong></span></strong></p>
					</div>
				</div>
			</div>
		</div>
		<!-- Dashboard-Chart -->
		<div class="row">
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>전체</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
						</div>
					</div>
					<div class="card-body">
						<div id="pieChart"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></div>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>IAQ</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
						</div>
					</div>
					<div class="card-body">
						<div id="pieChart2"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></div>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>OAQ</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
						</div>
					</div>
					<div class="card-body">
						<div id="pieChart3"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></div>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>DOT</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
						</div>
					</div>
					<div class="card-body">
						<div id="pieChart4"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></div>
					</div>
				</div>
			</div>
			<div class="col-md-2" style="flex: 0 0 20%; max-width: 20%;">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>VENT</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
						</div>
					</div>
					<div class="card-body">
						<div id="pieChart5"><span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span></div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-4">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>유형별/수신현황</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
							</button>
						</div>
					</div>
					<!-- /.card-header -->
					<div class="card-body" id="barChart" style="height:300px;">
						<span style="color: #696969; backgroud: white;"><strong>데이터 호출중</strong></span>
					</div>
				</div>
			</div>
			<div class="col-8">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>시간별/수신현황</strong></h3>
						<div class="card-tools">
							<button type="button" class="btn btn-tool"
								data-card-widget="collapse">
								<i class="fas fa-minus"></i>
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