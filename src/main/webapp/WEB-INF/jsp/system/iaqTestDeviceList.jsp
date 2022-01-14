<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/iaqTestDeviceList.js"></script>

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
							<div class="search_bottom mt10" style="max-width:610px;">								
								<select name="searchType" id="searchType" class="custom-select custom-select-sm form-control form-control-sm mr10" style="max-width:100px;height:38px;">
									<option value ="sch_serialNum">시리얼번호</option>
								</select>
								<input type="text" class="form-control" id="searchValue" name= "searchValue" placeholder="내용을 입력해 주세요" style="max-width:350px; display:inline; vertical-align: middle;">
								<!--  <input type="button" class="btn btn-primary ml3 mr10" style="min-width:100px;" id="searchBtn" value="검 색">-->
							</div>
						</div>
						<table id="iaqTable" class="table table-bordered table-hover text-center">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">시리얼번호</th>
                                    <th class="bgGray1">데이터 시간</th>
                                    <th class="bgGray1">미세먼지<br/>(㎍/㎥) </th>
                                    <th class="bgGray1">초미세먼지<br/>(㎍/㎥)</th>
                                    <th class="bgGray1">이산화탄소<br/>(ppm)</th>
                                    <th class="bgGray1">VOCs<br/>(ppb)</th>
                                    <th class="bgGray1">소음<br/>(dB)</th>
                                    <th class="bgGray1">온도<br/>(℃ )</th>
                                    <th class="bgGray1">습도<br/>(%)</th>
                                    <th class="bgGray1">통합지수<br/>(CICI)</th>
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
	
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->