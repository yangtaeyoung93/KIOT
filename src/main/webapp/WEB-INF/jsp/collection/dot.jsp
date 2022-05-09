<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/collection/dot.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<!-- Select2 -->
<script src="/resources/plugins/select2/js/select2.full.min.js"></script>
<!-- ##### Header Area End ##### -->
<style>
.filter-cli {
	background: rebeccapurple;
	color: white;
	border-radius: 5px;
}
.content-wrapper{
    width:1655px;
}

.main-sidebar{
    position : fixed !important;
    height : 1000px !important;
}
</style>
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
								<select name="searchGroup" id="searchGroup" 
									class="select2 custom-select custom-select-sm form-control form-control-sm ml5 mr10" 
									style="max-width:180px;height:38px;">
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
							<div class="search_bottom mt10" style="max-width:510px;">								
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<option value="">전체</option>
									<option value ="sch_serialNum">시리얼번호</option>
									<option value ="sch_stationName">스테이션명</option>
									<option value ="sch_userId">사용자계정</option>
									<option value ="sch_etc">비고</option>
									<option value = "sch_company">고객사</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:350px; display:inline; vertical-align: middle;">
							</div>
							<div style="display: inline-block; float: right; margin-right: 100px;">
								<div style="float: left; padding-right: 20px; cursor: pointer; padding-top: 5px; padding-bottom: 5px;" id="mttDiv" class="filterDiv filter-cli">
									<span><strong id="msgTxtTotal" style="padding: 5px;"></strong></span>
								</div>
								<div style="float: left; padding-right: 20px; cursor: pointer; padding-top: 5px; padding-bottom: 5px;" id="mtrDiv" class="filterDiv">
									<span><strong id="msgTxtReceive" style="padding: 5px;"></strong></span>
								</div>
								<div style="float: left; padding-right: 20px; cursor: pointer; padding-top: 5px; padding-bottom: 5px;" id="mturDiv" class="filterDiv">
									<span><strong id="msgTxtUnReceive" style="padding: 5px;"></strong></span>
								</div>
							</div>
						</div>
						<table id="dotTable" class="table table-bordered table-hover" style="text-align: center;">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">시리얼번호</th>
                                    <th class="bgGray1">데이터 시간</th>
                                    <th class="bgGray1">미세먼지<br/>(㎍/㎥) </th>
                                    <th class="bgGray1">초미세먼지<br/>(㎍/㎥)</th>
                                    <th class="bgGray1">소음<br/>(dB)</th>
                                    <th class="bgGray1">온도<br/>(℃ )</th>
                                    <th class="bgGray1">습도<br/>(%)</th>
                                    <th class="bgGray1">조도<br/>(lx)</th>
		                            <th class="bgGray1">자외선<br/>(UVI)</th>
                                    <th class="bgGray1">통합지수<br/>(COCI)</th>
                                    <th class="bgGray1">스테이션명</th>
                                    <th class="bgGray1">제품등록일</th>
									<th class="bgGray1">계정생성일</th>
                                    <th class="bgGray1">고객사</th>
                                    <th class="bgGray1">비고</th>
                                    <th class="bgGray1">장비 정보</th>
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
<input type="hidden" name="p_h_memberIdx" id="p_h_memberIdx"/>
<input type="hidden" name="p_h_deviceIdx" id="p_h_deviceIdx"/>
<input type="hidden" name="deviceType" id="deviceType"/>						
</form>

</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->