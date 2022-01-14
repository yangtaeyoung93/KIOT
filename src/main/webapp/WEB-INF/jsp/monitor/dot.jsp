<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/monitor/dot.js"></script>
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
							<div class="search_bottom mt10" style="max-width:480px;">								
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<option value ="sch_serialNum">시리얼번호</option>
									<option value ="sch_stationName">스테이션명</option>
									<option value ="sch_userId">사용자계정</option>
									<option value ="sch_firmWare">펌웨어버전</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:250px; display:inline; vertical-align: middle;">
								<!--  <input type="button" class="btn btn-primary ml3 mr10" style="min-width:100px;" id="searchBtn" value="검 색">-->
								( <input type="checkbox" name="searchTestYn" id="searchTestYn" value="N" style="width:15px;height:15px; vertical-align: middle;"/> <label for="searchTestYn">테스트장비</label> )
							</div>
						</div>
						<table id="dotTable" class="table table-bordered table-hover">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">시리얼번호</th>
                                    <th class="bgGray1">관제시간</th>
                                    <th class="bgGray1">스테이션명</th>
                                   	<th class="bgGray1">펌웨어버전</th>
                                   	<th class="bgGray1">부팅시각</th>
                                   	<th class="bgGray1">IMEI</th>
                                   	<th class="bgGray1">CTN</th>
                                </tr>
                            </thead>
							<!--  <tbody>
	                            <tr>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                                <td></td>
	                            </tr>
							</tbody>
							-->
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
<!-- ##### Footer Area End ##### -->