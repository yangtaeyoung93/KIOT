<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>

<link rel="stylesheet" href="https://openlayers.org/en/v5.3.0/css/ol.css" type="text/css">
<link rel="stylesheet" href="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/css/ol.css">
<script src="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/build/ol.js"></script>

<script src="/resources/share/js/system/memberDetail.js"></script>
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
						<li class="breadcrumb-item"><strong>사용자 관리</strong></li>
						<li class="breadcrumb-item"><a href="/system/member/list">사용자 계정 관리</a></li>						
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
			<div class="${col }">
				<div class="card">
					<!-- /.card-header --> 
					<div class="card-body">
						<h3>
							<strong>사용자 계정</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<!-- hidden -->
							<input type="hidden" name="chk_id" id="chk_id" value=""/>
							<input type="hidden" name="userIdx" id="userIdx" value="${memberData.idx }"/>
							<div class ="row">
								<div class="opt_div col-7 mx-auto">
									<label for="userId" style="margin-bottom: 0;vertical-align: middle;">사용자 아이디 (<font style="color:red">*</font>)</label><br/>
									<input type="text" class="form-control model-attribute col-8 d-inline-block;" name="userId" id="userId" value="${memberData.userId }" placeholder="내용을 입력해 주세요"  style="vertical-align: top; margin-top: 7px; display: inline-block;" tabindex="0"/>
									<div id="chk_txt" class="mt10"></div>
								</div>
							</div>
							<div class ="row mt10">
								<div class="opt_div col-7 mx-auto">
									<label for="userPw" style="margin-bottom: 0;vertical-align: middle;">비밀번호  (<font style="color:red">*</font>)</label><br/>
									<input type="password" class="form-control model-attribute col-8 d-inline-block;" name="userPw" id="userPw" value="" placeholder="내용을 입력해 주세요" style="vertical-align: top; margin-top: 7px;" tabindex="1" autocomplete="new-password">
								</div>
							</div>
							<div class ="row mt10" id="chkpwDiv">
								<div class="custom-control custom-checkbox col-7 mx-auto">
									<input class="custom-control-input" type="checkbox" id="chkUserPw" name="chkUserPw" tabindex="2"/> 
									<label for="chkUserPw" class="custom-control-label">클릭 시 비밀번호 변경이 가능합니다.</label>
								</div>
							</div>
							<div class="row mt10" style="display: block;text-align: center;">
                                <input type="button" class="btn btn-primary ml3" style="min-width:100px; font-size:14px;" id="loginCntReset"  value="로그인 횟수 초기화" />
                            </div>
							<hr />
							<div class ="row">
								<div class="col-5 mx-auto">
									<label for="region">예보지역코드</label>
									<input type="text" class="form-control model-attribute" name="region" readonly="readonly" id="region" value="${memberData.region }" placeholder="내용을 입력해 주세요" tabindex="3">
								</div>
								<div class="col-5 mx-auto">
									<label for="regionName">예보지역명</label>
									<input type="text" class="form-control model-attribute" name="regionName" readonly="readonly" id="regionName" value="${memberData.regionName }" placeholder="내용을 입력해 주세요" tabindex="4">
								</div>
							</div>
							<div class ="row mt20">
								<div class="col-2 mx-auto">
									<label for="stationShared">스테이션 공유 여부 ${memberData.stationShared }</label>
									<select name="stationShared" id="stationShared" class="member-attribute custom-select custom-select-sm form-control" style="height:38px">
										<option value="Y" ${selected } <c:if test="${memberData.stationShared == 'Y'}">selected</c:if>>Y</option>
										<option value="N" ${selected } <c:if test="${memberData.stationShared == 'N'}">selected</c:if>>N</option>
									</select>
								</div>
								<div class="col-2 mx-auto">
									<label for="lat">위도</label>
									<input type="text" class="form-control model-attribute" name="lat" id="lat" value="0" placeholder="내용을 입력해 주세요" tabindex="3">
								</div>
								<div class="col-2 mx-auto">
									<label for="lon">경도</label>
									<input type="text" class="form-control model-attribute" name="lon" id="lon" value="0" placeholder="내용을 입력해 주세요" tabindex="3">
								</div>
								<div class="col-2 mx-auto">
									<label for="lon">예보지역 목록</label>
									<select id="regionInfo" class="member-attribute custom-select custom-select-sm form-control" style="height:38px">
										<option value="">예보지역을 조회하여 주세요.</option>
									</select>
								</div>
								<div class="col-2 mx-auto">
								<label for=""></label>
									<input type="button" class="btn btn-primary ml3 form-control" style="min-width:100px;" onclick="getRegionInfoFunc()" value="예보지역 조회"/>
								</div>
							</div>
							<hr class="mt25" />
							<div class ="row mt20" style="height: 300px;">
								<div id="fullScreen" style="width: 100%; height: 100%;">
									<div id="map" style = "height:100%; width:100%; display: inline; float: left;"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-6" id="${readDiv }">
				<div class="card" style="height: 525px;">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>접속 정보</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<div class="opt_div col-4">
									<h5>접속 횟수</h5> 
									<span id="loginCount"><c:if test="${memberData.loginCount != 0}">${memberData.loginCount } 회</c:if></span>
									<hr>
								</div>
								<div class="opt_div col-4">
									<h5>접속 IP</h5> 
									<span id="loginIp">${memberData.loginIp }</span>
									<hr>
								</div>
								<div class="opt_div col-4">
									<h5>접속 일자</h5> 
									<span id="loginDt">${memberData.loginDt }</span>
									<hr>
								</div>
							</div>
						</div>
					</div>
					<div class="card-body">
						<h3>
							<strong>App 디바이스 정보</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row" style="overflow-y: auto; height: 220px; display: block;">
								<div class="opt_div col-6" style="display: inline-block;">
									<h5>디바이스 코드</h5>
									<div id="appDeviceCode">
										<c:forEach var="appDeviceDataList" items="${appDeviceDataList }">
											<div class="mt10">${appDeviceDataList.appDeviceIdx }</div>
										</c:forEach>
									</div>
									<hr>
								</div>
								<div class="opt_div col-6" style="display: inline-block; float: right;">
									<h5>디바이스 타입</h5> 
									<div id="appDeviceType">
										<c:forEach var="appDeviceDataList" items="${appDeviceDataList }">
											<div class="mt10">${appDeviceDataList.appDeviceType }</div>
										</c:forEach>
									</div>
									<hr>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.row -->
	</section>
	<section class="btn_area">
		<div class="row">
			<div class="col-12">
				<input type="button" class="btn btn-primary ml3" style="min-width:100px;" ${btn }/>
				<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="listBtn" value="목록으로" />
			</div>
		</div>
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->