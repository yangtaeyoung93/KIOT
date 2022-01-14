<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/header.jsp"%>

<link rel="stylesheet" href="https://openlayers.org/en/v5.3.0/css/ol.css" type="text/css">
<link rel="stylesheet" href="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/css/ol.css">
<script src="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/build/ol.js"></script>

<script src="/resources/share/js/region/dataList.js"></script>
<!-- ##### Footer Area End ##### -->

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
							<div class="search_bottom mt10" style="max-width:610px;">								
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<!-- <option value = "">전체</option> -->
									<option value ="sch_dcode">행정동코드</option>
									<option value ="sch_dfName">행정동명</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:350px; display:inline; vertical-align: middle;">
							</div>
						</div>
						<table id="dustTable" class="table table-bordered table-hover text-center">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">행정동코드</th>
                                    <th class="bgGray1">행정동명</th>
                                    <th class="bgGray1">위도</th>
                                    <th class="bgGray1">경도</th>
                                    <th class="bgGray1">데이터시간</th>
		                            <th class="bgGray1">미세먼지</th>
                                    <th class="bgGray1">미세먼지등급</th>
                                    <th class="bgGray1">초미세먼지</th>
                                    <th class="bgGray1">초미세먼지등급</th>
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
<input type="hidden" name="dcode" id="dcode"/>
<input type="hidden" name="dfname" id="dfname"/>
<input type="hidden" name="lat" id="lat"/>
<input type="hidden" name="lon" id="lon"/>				
</form>
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->