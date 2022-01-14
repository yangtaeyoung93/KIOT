<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/memberDeviceList.js"></script>
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
							<div class="search_bottom"  style="max-width: 520px;">
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr20" style="max-width:130px;height:38px;">
									<option value ="userId">사용자 아이디</option>
									<option value ="serialNum">장비 시리얼 번호</option>
									<option value ="etc">비고</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="searchBtn" value="검 색">
							</div>
						</div>
						<table id="memberDeviceTable" class="table table-bordered table-hover text-center">
							 <thead>
                                <tr>
                                	<th class="bgGray1">NO</th>
                                    <th class="bgGray1">사용자 아이디</th>
                                    <th class="bgGray1">전체</th>
                                    <th class="bgGray1">IAQ</th>
                                    <th class="bgGray1">OAQ</th>
                                    <th class="bgGray1">DOT</th>
                                    <th class="bgGray1">VENT</th>
                                    <th class="bgGray1">설치보고서</th>
                                    <th class="bgGray1">스테이션 공유여부</th>                                    
                                </tr>
                            </thead>
						</table>
						<div class="row">
							<div class="col-3">
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="등록" onClick="goInsert();"/>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px; margin-left: 20px;" value="일괄 등록" id="excelShowBtn"/>
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
										<label><a href="/system/member/device/excel"><strong>양식 다운로드</strong></a></label>
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