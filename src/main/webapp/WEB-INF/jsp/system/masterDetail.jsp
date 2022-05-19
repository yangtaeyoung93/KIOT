<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/masterDetail.js"></script>

<!-- Select2 -->
<script src="/resources/plugins/select2/js/select2.full.js"></script>
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
						<li class="breadcrumb-item"><strong>그룹 관리</strong></li>
						<li class="breadcrumb-item"><a href="/system/master/list">상위그룹 계정 관리</a></li>
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
			<div class="col-6">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>그룹 계정 </strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<div class="col-5 mx-auto">
									<input type="hidden" name="chk_id" id="chk_id" value=""/>
									<input type="hidden" name="masterIdx" id="masterIdx" value="${masterData.idx }"/>
									<label for="masterId">그룹아이디 (<font style="color:red">*</font>)</label>
									<input type="text" class="form-control group-attribute" name="masterId" id="masterId" value="${masterData.masterId }" placeholder="내용을 입력해 주세요">
									<div id="chk_txt" class="mt10"></div>
									
								</div>
								<div class="col-5 mx-auto">
									<label for="masterPw">비밀번호 (<font style="color:red">*</font>)</label>
									<input type="password" class="form-control group-attribute" name="masterPw" id="masterPw" value="${masterData.masterPw }" placeholder="내용을 입력해 주세요" autocomplete="new-password">
									<div class ="row mt10" id="chkpwDiv">
										<div class="custom-control custom-checkbox col-12 mx-auto" style="font-size:14px;">
											<input class="custom-control-input" type="checkbox" id="chkGroupPw" name="chkGroupPw" /> 
											<label for="chkGroupPw" class="custom-control-label">클릭 시 비밀번호 변경이 가능합니다.</label>
										</div>
									</div>
								</div>
							</div>
							<hr/>
							<div class ="row mt20">
								<div class="col-5 mx-auto">
									<label for="masterName">그룹명 (<font style="color:red">*</font>)</label>
									<input type="text" class="form-control group-attribute" name="masterName" id="masterName" value="${masterData.masterName }" placeholder="내용을 입력해 주세요">
								</div>
								<div class="col-5 mx-auto">
									<label for="masterCompanyName">고객사 (<font style="color:red">*</font>)</label>
									<input type="text" class="form-control group-attribute" name="masterCompanyName" id="masterCompanyName" value="${masterData.masterCompanyName }" placeholder="내용을 입력해 주세요">
								</div>
							</div>
							<hr/>
							<div class ="row mt20">
								<div class="col-5 mx-auto">
									<label for="groupDepartName">고객담당자 (<font style="color:red">*</font>)</label>
									<input type="text" class="form-control group-attribute" name="groupDepartName" id="groupDepartName" value="${masterData.groupDepartName }" placeholder="내용을 입력해 주세요">
								</div>
								<div class="col-5 mx-auto">
									<label for="groupEmail">이메일</label>
									<input type="text" class="form-control group-attribute" name="masterEmail" id="masterEmail" value="${masterData.masterEmail }" placeholder="내용을 입력해 주세요">
								</div>
							</div>
							<hr/>
							<div class ="row mt20">
								<div class="col-5 mx-auto">
									<label for="masterPhoneNumber">전화번호 (<font style="color:red">*</font>)</label>
									<input type="tel" maxlength="13" onkeypress="validate(event)" class="form-control group-attribute" name="masterPhoneNumber" id="masterPhoneNumber" value="${masterData.groupPhoneNumber }" placeholder="내용을 입력해 주세요">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-6">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>사용자 목록</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<div class="col-12">
					                <div class="form-group" style="height: 285px;">
					                	<Strong>미등록 사용자 / 등록 사용자</Strong>
					                  <select class="duallistbox" multiple="multiple" id="memberSelect">
					                  </select>
					                </div>
					                <!-- /.form-group -->
				            	</div>
							</div>
						</div>
					</div>
				</div>
				<!-- <div class="card">
					/.card-header
					<div class="card-body">
						<h3>
							<strong>미등록 사용자 목록</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<select class="form-control select2" id="unSelectMemberList" data-placeholder="검 색" multiple="multiple" data-dropdown-css-class="select2-purple" style="width: 100%;">
								</select>
								<div class="col-12 mx-auto" id="userArea1" style="height: 135px; overflow-x: hidden;">
									<div class="row" id="notUseMember" style="background-color:#e2e2e2;padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
									</div>						
								</div>
								<input type="button" class="btn btn-primary mt10 float-right" style="min-width:100px;" value="추 가" id="addGroupMember"/>
							</div>
						</div>
					</div>
				</div> -->
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