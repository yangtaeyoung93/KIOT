<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/header.jsp"%>
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
						<li class="breadcrumb-item"><a href="#">${gubun }</a></li>
						<li class="breadcrumb-item active">${item }</li>
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
					<div class="card-header">
						<h3 class="card-title">DataTable with minimal features &
							hover style</h3>
					</div>
					<!-- /.card-header -->
					<div class="card-body">
						<table id="example2" class="table table-bordered table-hover">
							 <thead>
                                <tr>
                                    <th style="">스테이션명</th>
                                    <th>시리얼 번호</th>
                                    <th>날짜시각</th>
                                    <th>미세먼지<br/>(㎍/m³)</th>
                                    <th>초미세먼지<br/>(㎍/m³)</th>
                                    <th>CO2<br/>(ppm)</th>
                                    <th>VOCS<br/>(ppb)</th>
                                    <th>소음<br/>(dB)</th>
                                    <th>온도<br/>(℃)</th>
                                    <th>습도<br/>(%)</th>
                                    <th>통합실내쾌적지수<br/>(CICI)</th>
                                    <th>비고</th>
                                    <!-- <th>설치위치</th>
                                    <th>분석보고서</th>
                                    <th>정보</th> -->
                                </tr>
                            </thead>
							<tbody>
								<c:forEach var = "list" items = "${categoryList }">
                                    <tr>
                                        <td>${list.staName }</td>
                                        <td>${list.equiInfoKey }</td>
                                        <td>${list.regDate }</td>
                                        <td>${list.pm10 }</td>
                                        <td>${list.pm25 }</td>
                                        <td>${list.co2 }</td>
                                        <td>${list.voc }</td>
                                        <td>${list.noise }</td>
                                        <td>${list.temp }</td>
                                        <td>${list.humi }</td>
                                        <td>${list.humi }</td>
                                        <td>${list.avgIndex }</td>
                                        
                                        <%-- <td>${list.staName }</td>
                                        <td>${list.staName }</td>
                                        <td>${list.staName }</td> --%>
                                    </tr>
                                </c:forEach>
							</tbody>
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