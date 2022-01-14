<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/region/offsetDataOaq.js"></script>
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
						<div class="search_area" style="width:90%">
							<div class="search_top" style="max-width:610px;">
								<label for="searchGroup">그룹</label>
								<select name="searchGroup" id="searchGroup" class="custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:130px;height:38px;">
									<option value="">전체</option>
								</select>
								<label for="searchParentSpace">상위 카테고리</label>
								<select name="searchParentSpace" id="searchParentSpace" class="custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:100px;height:38px;">
									<option value="">전체</option>
								</select>
								<label for="searchParentSpace">하위 카테고리</label>
								<select name="searchSpace" id="searchSpace" class="custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:100px;height:38px;">
									<option value="">전체</option>
								</select>
							</div>
							<div class="search_bottom mt10" style="max-width:610px;">								
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<option value ="sch_serialNum">시리얼번호</option>
									<option value ="sch_stationName">스테이션명</option>
									<option value ="sch_userId">사용자계정</option>
									<option value ="sch_etc">비고</option>
									<option value = "sch_company">고객사</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:350px; display:inline; vertical-align: middle;">
								<!--  <input type="button" class="btn btn-primary ml3 mr10" style="min-width:100px;" id="searchBtn" value="검 색">-->
								( <input type="checkbox" name="searchTestYn" id="searchTestYn" value="N" style="width:15px;height:15px; vertical-align: middle;"/> <label for="searchTestYn">테스트장비</label> )
							</div>
						</div>
						<table id="oaqTable" class="table table-bordered table-hover">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">시리얼번호</th>
                                    <th class="bgGray1">데이터 시간</th>
                                    <th class="bgGray1">미세먼지</th>
                                    <th class="bgGray1">미세먼지<br/>원본</th>
                                    <th class="bgGray1">미세먼지<br/>보정비율</th>
                                    <th class="bgGray1">미세먼지<br/>보정옵셋</th>
                                    <th class="bgGray1">초미세먼지</th>
                                    <th class="bgGray1">초미세먼지<br/>원본</th>
                                    <th class="bgGray1">초미세먼지<br/>보정비율</th>
                                    <th class="bgGray1">초미세먼지<br/>보정옵셋</th>
                                    <th class="bgGray1">스테이션명</th>
                                    <th class="bgGray1">제품등록일</th>
                                    <th class="bgGray1">고객사</th>
                                    <th class="bgGray1">비고</th>
                                    <th class="bgGray1"></th>
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
		
<form id="detail_info_form" method="post">
<input type="hidden" name="nowAll" />
<input type="hidden" name="param" />
<input type="hidden" name="p_h_serialNum" id="p_h_serialNum"/>
<input type="hidden" name="p_h_parentSpaceName" id="p_h_parentSpaceName"/>
<input type="hidden" name="p_h_spaceName" id="p_h_spaceName"/>
<input type="hidden" name="p_h_productDt" id="p_h_productDt"/>
<input type="hidden" name="p_h_stationName" id="p_h_stationName"/>
<input type="hidden" name="standard" id="standard"/>	
<input type="hidden" name="deviceType" id="deviceType"/>						
</form>
	
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->