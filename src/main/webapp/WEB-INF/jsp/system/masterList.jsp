<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/masterList.js"></script>
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
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm ml20 mr20" style="max-width:90px;height:38px;">
									<option value ="GN">상위그룹명</option>
									<option value ="GC">상위그룹아이디</option>
								</select>
								<input type="text" class="form-control mr20" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
								<strong>사용 여부</strong>
								<div class="custom-control custom-radio" style="display:inline-block;">
									<input class="custom-control-input" type="radio" id="customRadio1" name="customRadio" value="Y" r-data="Y" checked="" > 
									<label for="customRadio1" class="custom-control-label">Y</label>
								</div>
								<div class="custom-control custom-radio mr20"style="display:inline-block;">
									<input class="custom-control-input" type="radio" id="customRadio2" name="customRadio" value="N" r-data="N"> 
									<label for="customRadio2" class="custom-control-label">N</label>
								</div>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="searchBtn" value="검 색">
							</div>
						</div>
						<table id="masterTable" class="table table-bordered table-hover text-center">
							<thead>
                                <tr>
                                	<th class="bgGray1"><input type="checkbox" name="all_chk" id="all_chk" style="margin-left: 17px;" class="chBox" /></th>
                                	<th class="bgGray1">NO</th>
                                    <th class="bgGray1">상위그룹명</th>
                                    <th class="bgGray1">상위그룹아이디</th>
                                    <th class="bgGray1">고객사</th>
                                    <th class="bgGray1">고객담당자</th>
                                    <th class="bgGray1">이메일</th>
                                    <th class="bgGray1">연락처</th>
                                    <th class="bgGray1">등록 그룹 수</th>
                                    <th class="bgGray1">등록 일시</th>
                                    <th class="bgGray1">사용 여부</th>
                                    <th class="bgGray1">Air365 사용 유무</th>
                                </tr>
                            </thead>
						</table>
						<div class="row">
							<div class="col-3">
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="등록" onClick="goInsert();"/>
								<input type="button" class="btn btn-primary ml3" style="min-width:100px;" value="삭제" id="deleteBtn"/>
							</div>
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