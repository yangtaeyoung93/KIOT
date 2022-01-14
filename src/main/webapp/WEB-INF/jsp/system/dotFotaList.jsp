<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/dotFota.js"></script>
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
						<div class="search_area" style="width:90%">							
							<div class="search_bottom mt10" style="max-width:610px;">								
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<option value="">전체</option>
									<option value ="sch_serialNum">시리얼번호</option>
									<option value ="sch_firmWare">펌웨어버전</option>
									<option value ="sch_stationName">스테이션명</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:350px; display:inline; vertical-align: middle;">
								( <input type="checkbox" name="searchTestYn" id="searchTestYn" value="N" style="width:15px;height:15px; vertical-align: middle;"/> <label for="searchTestYn">테스트장비</label> )
							</div>
						</div>
						<table id="dotTable" class="table table-bordered table-hover">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">시리얼번호</th>
                                    <th class="bgGray1">관제시간</th>
                                    <th class="bgGray1">스테이션명</th>
                                   	<th class="bgGray1">펌웨어버전</th>
                                   	<th class="bgGray1">부팅시각</th>
                                   	<th class="bgGray1">IMEI</th>
                                   	<th class="bgGray1">CTN</th>
                                   	<th class="bgGray1">리셋</th>
                                   	<th class="bgGray1" colspan="2">업그레이드</th>
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
		<div class="modal-dialog modal-lg" style="max-width: 40%;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"><strong>DOT Firmware Update</strong></h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<div class="row">
								<h5 class="mb20" style="font-size:14px;text-align:center;">
									<strong>펌웨어 업그레이드를 진행하시려면 버전 선택 후 업그레이드 버튼을 클릭하세요.</strong>
								</h5>
								<div class="col-6">
									<h6 class="mb10" id="device_cnt"></h6>
									<div style='background: #F0F8FF; width:220px; height: 200px; overflow-y: auto;' id="serialList">
										
									</div>										
								</div>
								<div class="col-6" style="margin-top: 65px;">
									<h5>펌웨어 버전</h5>
									<!--  <select name="firmwareList" id="firmwareList" style="width:180px;height:45px;">
										<option value="DOT_TEST_FW_V1.bin">DOT_TEST_FW_V1.bin</option>
									</select>-->
									<input type="text" name="firmwareList" id="firmwareList" style="width:180px;height:45px;"/>
								</div>
							</div>
						</div>
						<!-- /.card-body -->
						<hr/>
						<div class="card-footer" style="background-color:#fff;">
							<button type="button" id="firmWareUpgradeBtn" class="btn btn-primary">업데이트</button>
							<button type="button" id="closeBtn" class="btn btn-primary" data-dismiss="modal" aria-label="Close">닫기</button>
						</div>
					</form>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.Modal -->
	
	<!-- .Modal -->
	<div class="modal fade" id="modal-lg2" style="display: none;" aria-hidden="true">
		<div class="modal-dialog modal-lg" style="max-width: 40%;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"><strong>Device Reset</strong></h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<input type="hidden" name="resetSerialNum" id="resetSerialNum"/>
							<div class="row mb20" style="font-size:14px; text-align:center; display: block;">
								<strong id="txtResetSerial"></strong>
							</div>
							<div class="row mb20" style="font-size:14px; text-align:center; display: block; ">
								<strong>디바이스를 리셋 하시겠습니까?</strong>
							</div>
						</div>
						<!-- /.card-body -->
						<hr/>
						<h5 class="mb20" style="font-size:14px;text-align:center;">
							<strong>디바이스 리셋을 진행하시려면 리셋 버튼 선택</strong>
						</h5>
						<div class="card-footer" style="background-color:#fff; text-align: center">
							<button type="button" id="firmWareUpgradeBtn" class="btn btn-primary" onClick="goReset();">리셋</button>
							<button type="button" id="closeBtn" class="btn btn-primary" data-dismiss="modal" aria-label="Close">닫기</button>
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