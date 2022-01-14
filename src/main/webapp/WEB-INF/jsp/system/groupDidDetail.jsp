<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/groupDidDetail.js"></script>
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
						<li class="breadcrumb-item"><a href="/system/group/did/list">그룹 DID 관리</a></li>
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
						<h3>
							<strong>그룹정보</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<input type="hidden" name="h_idx" id="h_idx" value="${groupData.idx }"/>
								<div class="col-3">
									<div class="row"><strong>그룹아이디</strong></div>
									<div id="">${groupData.groupId }</div>
								</div>
								<div class="col-3">
									<div class="row"><strong>그룹명 </strong></div>
									<div id="">${groupData.groupName }</div>
								</div>
								<div class="col-3">
									<div class="row"><strong>전화번호</strong></div>
									<div id="">${groupData.groupPhoneNumber }</div>
								</div>
								<div class="col-3">
									<div class="row"><strong>이메일</strong></div> 
									<div id="">${groupData.groupEmail }</div>
								</div>
							</div>
							<div class ="row mt20">
								<div class="col-3">
									<div class="row"><strong>등록 DID 수</strong></div>
									<div id="txtDidCnt"></div>
								</div>
								<div class="col-3">
									<div class="row"><strong>등록 사용자 수</strong></div>
									<div id="txtMemberCnt"></div>
								</div> 
								<div class="col-3">
									<div class="row"><strong>등록일시</strong></div>
									<div id="">${groupData.createDt }</div>
								</div>
								<div class="col-3">
									<div class="row"><strong>사용여부</strong></div> 
									<div id="">${groupData.useYn }</div>
								</div>
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /.row -->
	</section>
	<section class="content">
		<div class="row">
			<div class="col-6">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>DID 리스트 </strong>
						</h3>
						<div class="opt_wrap">
							<hr>
							<input type="hidden" id="beforeIdx" value=""/>
							<div class="col-12 mx-auto" id="Area1" style="height: 135px; overflow-x: hidden;">
								<div class="row" id="didDiv" style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
									<c:forEach var="groupDidList" items="${groupDidList }">
										<div class="row col-12 mt10 did" style="cursor:pointer;" id="did${groupDidList.idx }" >
											<input type="radio" class="mr10" name="chkDid" id="chkDid${groupDidList.idx }" value="${groupDidList.idx }" style='width:20px; height:20px; margin-top:3px;' data-memberCnt="${groupDidList.didMemberCnt }" data-didName="${groupDidList.didName }" data-didCode="${groupDidList.didCode }" data-useYn="${groupDidList.useYn }" onClick="getDidinfo(${groupDidList.idx },${groupDidList.groupIdx })"/>
											<label for="chkDid${groupDidList.idx }" id="chkDidLabel${groupDidList.idx }">${groupDidList.didName }</label>
										</div>
									</c:forEach>
								</div>
							</div>
							<div class="col-12 mt20" style="padding:0;">
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="groupDeleteBtn" value="DID 삭제"/>
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
							<strong>DID상세 정보</strong>
							<input type="button" class="btn btn-primary ml3" style="min-width:100px; float: right;" onclick="updateMode()" id="didUpdateBtn" value="수정"/>
						</h3>
						<input type="hidden" name="chk_code" id="chk_code" value=""/>
						<div class="opt_wrap">
							<hr>
							<div class="row">
								<div class="col-6">
									<label>DID이름</label>
									<div>
										<span id="txtDidName"></span>
									</div>
								</div>
								<div class="col-6">
									<label>DID코드</label>
									<div>
										<span id="txtDidCode"></span>
									</div>
								</div>
							</div>
							<div class="row mt20">
								<div class="col-6">
									<label>등록된 사용자 수</label>
									<div>
										<div id="txt_memberCnt"></div>
									</div>
								</div>
								<div class="col-6">
									<div id="checkArea">
										<input type="button" class="btn btn-primary ml3" style="min-width:100px; display: none;" 
											onclick="updateSave()" id="updateSave" value="저장하기"/>
										<div id="chk_txt" class="mt10"></div>
									</div>
								</div>
							</div>
							
							<div class="col-12 mt20" style="padding:0;">
							</div>
							<hr/>
							<div class="row">
								<div class="col-6">
									<label class="mt20" id="txtmemberTitle">등록 사용자리스트</label>
									<div class="col-12 mx-auto" id="memberArea" style="height: 135px; overflow-x: hidden;">
										<div class="row" id="memberDiv" style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
											
										</div>
									</div>
								</div>
								<div class="col-6">
									<label class="mt20" id="txtmemberTitle">그룹 사용자리스트</label>
									<div class="col-12 mx-auto" id="memberArea2" style="height: 135px; overflow-x: hidden;">
										<div class="row" id="memberDiv2" style="background-color:#e2e2e2; padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
											
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>
	<section class="btn_area">
		<div class="row">
			<div class="col-12">
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