<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>

<script src="/resources/share/js/system/deviceDetail.js"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
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
						<li class="breadcrumb-item"><a href="/system/device/list/${deviceType }">${superItem }</a></li>						
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
			<input type="hidden" value="${deviceType }" id="deviceType"/>
			<input type="hidden" value="${idx }" id="deviceIdx"/>
			<input type="hidden" name="chk_serial" id="chk_serial" value=""/>
			<div class="col-12">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<table id="WirteTable" class="table table-bordered WirteTable">
							<colgroup>
								<col width="20%"></col>
								<col width="80%"></col>
							</colgroup>
							<tr>
								<th class="title">
									<label for="deviceModelIdx">장비모델</label>
								</th>
								<td>
									<select name="deviceModelIdx" id="deviceModelIdx" class="custom-select">
										<c:forEach var="model" items="${modelList }">
											<option value="${model.idx }" <c:if test="${deviceData.deviceModel eq model.deviceModel }"> selected </c:if> data-value="${model.deviceTypeIdx }">${model.deviceModel }</option>
										</c:forEach>
									</select>
								</td>
							</tr>
							<tr>
								<th class="title">
									<label for="serialNum">시리얼번호 (<font style="color:red">*</font>)</label>
								</th>
								<td>
									 <input type="text" class="form-control col-6" name="serialNum" id="serialNum" placeholder="내용을 입력해 주세요" value="${deviceData.serialNum }" style="display:inline-block;"/>
									 <!--  <input type="button" class="btn btn-primary d-inline-block;" id="chk_serial_btn" style="min-width:100px; vertical-align: bottom;" value="중복체크"/>-->
									 <div id="chk_txt" class="mt10"></div>
								</td>
							</tr>
							<tr>
								<th class="title">
									<label for="productDt">생산일자</label>
								</th>
								<td>
								<div class="form-group">
									<div class="input-group">
										<div class="input-group-prepend" id="datepicker_ic" style="cursor:pointer;">
											<span class="input-group-text"> <i class="far fa-calendar-alt"></i>
											</span>
										</div> 
										<input type="text" class="form-control float-right" id="productDt" value="${deviceData.productDt }">
									</div>
									</div>
								</td>
							</tr>
						</table>
					</div>
					<!-- /.card-body -->
				</div>
				<!-- /.card -->
			</div>
			<!-- /.row -->
		</div>
		<!--  /.row -->
	</section>
	<!-- /.content -->
	<section class="btn_area">
		<div class="row">
			<div class="col-12">
				<input type="button" class="btn btn-primary ml3" style="min-width:100px;" ${btn }/>
				<input type="button" class="btn btn-primary ml3" style="min-width:100px;" onClick="goList();" value="목록으로"/>
			</div>
		</div>
	</section>
</div>

<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->