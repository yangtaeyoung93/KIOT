<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/groupDidInsert.js"></script>
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
			<div class="col-6">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>그룹 선택 (<font style="color:red">*</font>)</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<input type="hidden" name="h_groupIdx" id="h_groupidx" value=""/>
								<div class="col-12 mx-auto" id="userArea1" style="height: auto; overflow-x: hidden;">
									<div class="row" id="useGroup" style="background-color:#e2e2e2;padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
										
									</div>						
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
							<strong>DID 설정</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row"  style="/*padding:57px 0px;*/">
								<div class="col-12 mx-auto">
									<input type="hidden" name="idx" id="idx" value=""/>
									<input type="hidden" name="chk_code" id="chk_code" value=""/>
									<label for="didName">DID명 (<font style="color:red">*</font>)</label>
									<input type="text" class="form-control group-attribute" name="didName" id="didName" value="" placeholder="내용을 입력해 주세요">
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>미등록 사용자 목록</strong>
						</h3>
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<div class="col-12 mx-auto" id="userArea2" style="height: 135px; overflow-x: hidden;">
									<div class="row" id="notUseGroupMember" style="background-color:#e2e2e2;padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
										
									</div>						
								</div>
								<input type="button" class="btn btn-primary mt10 float-right" style="min-width:100px;" value="추 가" id="addGroupDidMember"/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>등록 사용자 목록</strong>
						</h3> 
						<div class="opt_wrap">
							<hr/>
							<div class ="row">
								<div class="col-12 mx-auto" id="userArea3" style="height: 135px; overflow-x: hidden;">
									<div class="row" id="UseGroupDidMember" style="background-color:#e2e2e2;padding:5px 15px 15px 15px; height:auto; padding-left: 10px;">
								
									</div>						
								</div>
								<input type="button" class="btn btn-primary mt10 float-right" style="min-width:100px;" value="제 외" id="delGroupDidMember"/>
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