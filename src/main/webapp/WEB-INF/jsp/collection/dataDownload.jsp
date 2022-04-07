<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/collection/dataDownload.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="/resources/plugins/jquery/jquery.fileDownload.js "></script>
<!-- Select2 -->
<script src="/resources/plugins/select2/js/select2.full.min.js"></script>
<!-- ##### Header Area End ##### -->

<style>
.filter-cli {
	background: rebeccapurple;
	color: white;
	border-radius: 5px;
}
.tooltip {
  position: relative;
  display: inline-block;
  border-bottom: 1px dotted black;
}

.tooltiptext {
  visibility: hidden;
  width: 530px;
  background-color: black;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  top: 140px;
  padding: 5px 0;
  right: 3%;
  position: absolute;
  z-index: 1;
}

.data-download-btn:hover .tooltiptext {
  visibility: visible;
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
						<div class="search_area" style="width: 90%">
							<div class="mt10" style="max-width: 661px; margin: 0 auto;">
								<label for="searchGroup">그룹</label>
								<select name="searchGroup" id="searchGroup" class="select2 custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:420px; display:inline; vertical-align: middle;">
									<option value="">전체</option>
								</select><br/>
								<label for="searchDeviceType">장비 유형</label>
								<select name="searchDeviceType" id="searchDeviceType" class="custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:100px;height:38px;" onChange="getHighSpace(this.value)">
									<option value="" data-type-name="">전체</option>
									<option value="iaq" >IAQ</option>
									<option value="oaq" >OAQ</option>
									<option value="dot" >DOT</option>
								</select>
								<label for="searchParentSpace">상위 카테고리</label>
								<select name="searchParentSpace" id="searchParentSpace" disabled class="custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:100px;height:38px;" onChange="getSpace(this.value);">
									<option value="">전체</option>
								</select>
								<label for="searchParentSpace">하위 카테고리</label>
								<select name="searchSpace" id="searchSpace" disabled class="custom-select custom-select-sm form-control form-control-sm ml5 mr10" style="max-width:100px;height:38px;">
									<option value="">전체</option>
								</select>
							</div>
							<div class="search_bottom mt10" style="max-width: 510px;">
								<label for="searchValue">검색 </label> <br/>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:420px; display:inline; vertical-align: middle;">
								<br>
								데이터 다운로드 소요시간 : <span id="dataDownTime"></span> 초
							</div>
						</div>
						<div class="data-download-btn" style="display:inline-block; float: right;">
							<select name="searchStandard" id="searchStandard"
								class="custom-select custom-select-sm form-control form-control-sm mr10"
								style="width: 100px; height: 38px;">
								<option value="sum">전체</option>
								<option value="5m-avg-none">5분 평균</option>
								<option value="1h-avg-none">1시간 평균</option>
								<option value="1d-avg-none">1일 평균</option>
								<option value="1n-avg-none">1개월 평균</option>
							</select> 
							<input type="text" class="form-control d-inline-block" name="startDt" id="startDt" placeholder="시작일자 클릭 후 선택" style="width: 100px; vertical-align: middle;"> 
							~ 
							<input type="text" class="form-control d-inline-block mr10" name="endDt" id="endDt" placeholder="종료일자 클릭 후 선택" style="width: 100px; vertical-align: middle;"> 
							<input type="button" class="btn btn-primary ml3 mr10" id = "dataDownBtn" style="min-width: 100px; float: right;" onclick="dataDownload()" value="데이터 다운로드">
							<span class="tooltiptext">
								<br/>
								<strong>"대용량 다운로드는 최대 100개 장비대상, 기간은 최대 6개월입니다."<br/>
								"또한 여러장비 선택 다운로드 실행시 한대 장비씩 순차적으로 다운로드 파일이 생성됩니다."<br/>
								<p style="color: red;">"브라우저를 여러개 띄우셔도 하나씩만 수행됩니다"</p></strong>
							</span>
						</div>
						<table id="deviceTable" class="table table-bordered table-hover" style="text-align: center;">
							<thead>
								<tr>
									<th class="bgGray1">
 										<label style="width: 100%;">
											<input type="checkbox" name="chBox" id="allCh" style="margin-left: 17px;" class="chBox" />
										</label>
									</th>
									<th class="bgGray1">디바이스 종류 </th>
									<th class="bgGray1">시리얼 번호</th>
									<th class="bgGray1">스테이션명</th>
									<th class="bgGray1">그룹</th>
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
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<div title="데이터를 다운로드 중입니다." id="preparing-file-modal"
	style="display: none;">
	<div id="progressbar"
		style="width: 100%; height: 22px; margin-top: 20px;"></div>
</div>
<div title="Error" id="error-modal" style="display: none;">
	<p>파일 생성을 실패하셨습니다.</p>
</div>
<!-- ##### Footer Area End ##### -->