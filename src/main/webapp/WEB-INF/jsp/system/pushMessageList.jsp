<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/pushMessageList.js"></script>
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
							<div class="search_bottom" style="max-width: 1048px;">
								<strong>알림 유형</strong> 
								<select name="searchPushType" id="searchPushType" onchange="" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									<option value = "">전체</option>
									<option value = "메시지">메시지</option>
									<option value = "행동요령">행동요령</option>
								</select>
								<strong>장소 유형</strong> 
								<select name="searchDeviceType" id="searchDeviceType" onchange="" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									<option value = "">전체</option>
									<option value = "실 내">실 내</option>
									<option value = "실 외">실 외</option>
								</select>
								<strong>측정 요소</strong>
								<select name="searchElementType" id="searchElementType" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									<option value = "">전체</option>
									<option value = "미세먼지">미세먼지</option>
									<option value = "초미세먼지">초미세먼지</option>
									<option value = "이산화탄소">이산화탄소</option>
									<option value = "휘발성유기화합물">휘발성유기화합물</option>
									<option value = "온도">온도</option>
									<option value = "습도">습도</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
							</div>
						</div>
						<div style="clear: both"></div>
						<table id="pushMessageTable" class="table table-bordered table-hover">
							<thead>
								<tr>
									<th class="bgGray1">알림 유형</th>
									<th class="bgGray1">장소 유형</th>
									<th class="bgGray1">측정 요소</th>
									<th class="bgGray1">이전 상태</th>
									<th class="bgGray1">현재 상태</th>																		
									<th class="bgGray1">메시지</th>
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
					<h4 class="modal-title"><strong>알림 메시지 등록/수정</strong></h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<div class="form-group">
								<input type="hidden" id="pushMessageIdx"/>
								<label for="deviceType">장비 유형</label>
								<select class="form-control" id="deviceType" name="deviceType" onChange="">
									<option value ="IAQ">실 내</option>
									<option value ="OAQ">실 외</option>
								</select>
							</div>
							<div class="form-group">
								<label for="elementType">측정 요소</label>
								<select class="form-control" id="elementType" name="elementType">
									<option data-type = "1" value = "pm10">미세먼지</option>
									<option data-type = "1" value = "pm25">초미세먼지</option>
									<option data-type = "1" value = "co2">이산화탄소</option>
									<option data-type = "1" value = "voc">휘발성유기화홥물</option>
									<option data-type = "2" value = "temp">온도</option>
									<option data-type = "3" value = "humi">습도</option>
								</select>
							</div>
							<div class="form-group">
								<label for="preLevel">이전 상태</label>
								<select class="form-control" id="preLevel" name="preLevel">
									<option value ="1">좋음</option><option value ="2">보통</option><option value ="3">나쁨</option><option value ="4">매우 나쁨</option>
								</select>
							</div>
							<div class="form-group">
								<label for="curLevel">알림 상태</label>
								<select class="form-control" id="curLevel" name="curLevel">
									<option value ="1">좋음</option>
									<option value ="2">보통</option>
									<option value ="3">나쁨</option>
									<option value ="4">매우 나쁨</option>
								</select>
							</div>
							<div class="form-group">
								<label for="message">알림 메시지 (<font style="color:red">*</font>)</label> 
								<input type="text" class="form-control" id="message" name="message" placeholder="알림 메시지" >
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