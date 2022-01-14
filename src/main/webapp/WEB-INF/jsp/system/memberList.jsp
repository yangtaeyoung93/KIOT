<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/memberList.js"></script>
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
						<div class="search_area">
							<div class="search_bottom">
								<strong>아이디</strong>
								<input type="text" class="form-control ml15 mr10" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
								<strong>사용 여부</strong>
								<div class="custom-control custom-radio ml15" style="display:inline-block;">
									<input class="custom-control-input" type="radio" id="customRadio1" name="customRadio" r-data="Y" checked="" > 
									<label for="customRadio1" class="custom-control-label">Y</label>
								</div>
								<div class="custom-control custom-radio mr20"style="display:inline-block;">
									<input class="custom-control-input" type="radio" id="customRadio2" name="customRadio" r-data="N"> 
									<label for="customRadio2" class="custom-control-label">N</label>
								</div>
								
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="searchBtn" value="검 색">
							</div>
						</div>
						<table id="memberTable" class="table table-bordered table-hover text-center">
							 <thead>
                                <tr>
                                	<th class="bgGray1">
                                		<input type="checkbox" name="all_chk" id="all_chk" style="margin-left: 17px;" class="chBox" />
                                	</th>
                                	<th class="bgGray1">NO</th>
                                    <th class="bgGray1">사용자<br/> 아이디</th>
                                    <th class="bgGray1">예보지역</th>
                                    <th class="bgGray1">접속카운트</th>
                                    <th class="bgGray1">접속아이피</th>
                                    <th class="bgGray1">접속일시</th>
                                    <th class="bgGray1">등록일시</th>                                    
                                    <th class="bgGray1">스테이션<br/> 공유 여부</th>
                                    <th class="bgGray1">사용<br/> 여부</th>
                                </tr>
                            </thead>
						</table>
						<div class="row">
							<div class="col-3">
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="등록" onClick="goInsert();"/>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="삭제" id="deleteBtn"/>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px; float: right;" value="일괄 등록" id="excelShowBtn"/>
							</div>
						</div>
						<div id="excelUploadDiv" style="display: none;">
							<hr/>
							<form id="excelUpForm" method="post" action="" role="form"
								enctype="multipart/form-data">
								<div class="row">
									<div class="col-3">
										<label>엑셀 업로드</label> <input id="excel" name="excel"
											class="custom-select" style="background: white;" class="file"
											type="file" multiple data-show-upload="false"
											data-show-caption="true" />
									</div>
									<div class="col-2">
										<label><a href="/system/member/excel"><strong>양식 다운로드</strong></a></label>
										<br/>
										<button type="button" id="excelUp" onclick="check()"
											class="btn btn-primary ml3"style="min-width:100px;">등록</button>
									</div>
								</div>
							</form>
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
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->