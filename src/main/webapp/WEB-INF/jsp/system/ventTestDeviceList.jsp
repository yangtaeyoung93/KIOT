<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/ventTestDeviceList.js"></script>

<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">


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
						<div class="search_area" style="width:60%">
							<div class="search_bottom mt10" style="max-width:780px;">
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<option value ="sch_serialNum">시리얼번호</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:350px; display:inline; vertical-align: middle;">
							</div>
						</div>
						<table id="ventTable" class="table table-bordered table-hover text-center">
							 <thead>
							 	<!-- 시리얼, 수집시간, 버전, 부팅시간, IMEI, CTN 이게 맞나 -->
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">시리얼번호</th>
                                    <th class="bgGray1">데이터 시간</th>
                                    <th class="bgGray1">AI모드</th>                                    
                                    <th class="bgGray1">자동모드</th>
                                   	<th class="bgGray1">전원</th>
                                    <th class="bgGray1">풍량</th>
                                    <th class="bgGray1">배기모드</th>
                                    <th class="bgGray1">필터알림</th>
                                    <th class="bgGray1">공기청정기모드</th>
                                    <th class="bgGray1">화재 경보</th>
                                    <th class="bgGray1">수위 경보</th>
                                    <th class="bgGray1">장비 상태</th>
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
	<div class="modal fade" id="modal-lg" style="display: none;" aria-hidden="true">
		<div class="modal-dialog modal-lg" style="max-width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"><strong><span id="">케이웨더 플랫폼운영개발실의 </span>상세정보</strong></h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<input type="hidden" id="p_h_serialNum"/>
							<input type="hidden" id="p_h_productDt"/>
							<div class="row" style="border-top:3px solid #aaa; height:35px; line-height:35px;">
								<div class="col-2" style="background-color:#e6edf9; border-bottom:1px solid #aaa;">시리얼번호</div>
								<div class="col-4" id="txtSerialNum" style="border-bottom:1px solid #aaa;"></div>
								<div class="col-2" style="background-color:#e6edf9; border-bottom:1px solid #aaa;">등록일자</div>
								<div class="col-4" id="txtProductDt" style="border-bottom:1px solid #aaa;"></div>
							</div>
							
							<div class="form-group mt20">
								<select name="" id="" class="custom-select custom-select-sm form-control form-control-sm mr10" style="width:120px;height:38px;">
									<option value= "sum">전체</option>
									<option value= "5m-avg-none">5분 평균</option>
									<option value= "1h-avg-none">1시간 평균</option>
									<option value= "1d-avg-none">1일 평균</option>
									<option value= "1n-avg-none">1개월 평균</option>
								</select>
								
								<input type="text" class="form-control d-inline-block" name="startDt" id="startDt" placeholder="시작일자 클릭 후 선택" style="width:150px; vertical-align: middle;"> ~ 
								<input type="text" class="form-control d-inline-block mr10" name="endDt" id="endDt" placeholder="종료일자 클릭 후 선택" style="width:150px; vertical-align: middle;">
								<input type="button" class="btn btn-primary ml3 mr10" style="min-width:100px;" id="modalSearchBtn" value="검 색">
								<select style="min-width:100px; height:38px; float: right;" id="chartTypeSelect">
									<option value="power">전원</option>
									<option value="air_volume">풍량</option>
									<option value="filter_alarm">배기모드</option>
									<option value="air_mode">필터알림</option>
									<option value="auto_mode">공기청정기 모드</option>
									<option value="fire_alarm">화재 경보</option>
									<option value="water_alarm">수위 경보</option>
									<option value="dev_stat">장비 상태</option>
								</select>
							</div>
							
							<div class="form-group mt10" id="timeChartDiv" style="height: 500px;"></div>
							
							<div class="form-group mt10">
							 	<h5 style="display:inline-block;margin-bottom: 0.25rem; vertical-align: middle;" id="tableTitle">검색 결과</h5>
							 	<table id="popventTable" class="table table-bordered table-hover text-center"  style="margin-top: 20px !important;">
							 		<thead>
                                		<tr>
                                			<th class="bgGray1">데이터 시간</th>
                                			<th class="bgGray1">자동모드</th>
                                			<th class="bgGray1">전원</th>
		                                    <th class="bgGray1">풍량</th>
		                                    <th class="bgGray1">배기모드</th>
		                                    <th class="bgGray1">필터알림</th>
		                                    <th class="bgGray1">공기청정기모드</th>
                          		            <th class="bgGray1">화재 경보</th>
                          		            <th class="bgGray1">수위 경보</th>
                          		            <th class="bgGray1">장비 상태</th>
                                		</tr>
                                	</thead>
                                </table>
							</div>
							
						</div>
						<!-- /.card-body -->
						<div class="card-footer" style="background-color:#fff;">
							<button type="button" id="insertBtn" class="btn btn-primary" data-dismiss="modal" aria-label="Close">닫기</button>
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