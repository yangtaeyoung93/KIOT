<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/header.jsp"%>

<link rel="stylesheet" href="https://openlayers.org/en/v5.3.0/css/ol.css" type="text/css">
<link rel="stylesheet" href="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/css/ol.css">
<script src="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/build/ol.js"></script>

<script src="/resources/share/js/region/offsetMapDot.js"></script>
<!-- ##### Footer Area End ##### -->
<style>
.popover-body {
	color: #212529;
}

.popover-body {
	min-width: 180px;
}

.ol-popup-closer {
	text-decoration: none;
	position: absolute;
	top: 2px;
	right: 8px;
}

.ol-popup-closer:after {
	content: "✖";
}

.map {
	width: 100%;
	height: 400px;
}

.fullscreen:-webkit-full-screen {
	height: 100%;
	margin: 0;
}

.fullscreen:-ms-fullscreen {
	height: 100%;
}

.fullscreen:fullscreen {
	height: 100%;
}

.fullscreen {
	margin-bottom: 10px;
	width: 100%;
	height: 500px;
}

.ol-rotate {
	top: 3em;
}

.map {
	width: 100%;
	height: 100%;
	float: left;
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
						<li class="breadcrumb-item"><strong>${gubun }</strong></li>
						<li class="breadcrumb-item active"><strong>${item }</strong></li>
					</ol>
				</div>
			</div>
		</div>
		<!-- /.container-fluid -->
	</section>
	<!-- Main content -->
		<section class="content" style="overflow: auto;">
		<div class="row col-6" style="float: right; padding-left: 0px;">
			<div class="col-12">
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>참조 관측 지점 <span id="dName"style="color: red; font-size: small;"></span> </strong> </h3>
						<div class="list-box" style="width: 210px; float: right; position: absolute; top: 70px; right: 50px; z-index:50">
							<ul>
								<li id="pm25ViewBtn" style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;" class="list-box-color" >
									PM2.5
								</li>
								<li id="pm10ViewBtn" style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;" class="list-box-white">
									PM10
								</li>
							</ul>
						</div>
					</div>
					<div class="card-body">
						<div id="fullscreen" class="fullscreen">
							<div id="map"class="map"></div>
							<div id="popup"></div>
						</div>
						<div style="margin-top: 13px; padding-right: 3px;">
							<div id="" class="" style="float: right; margin-right: 5px;">
								<img src="/resources/share/img/air/1_icon0.png" style="max-width: 20px; height:auto;">&nbsp;
								<strong>국가관측망 (미수신)</strong>
								<span id="empty_border" class="spanSelectBorder" style="width: 0; transition: 0.4s; left: 50%;"></span>
							</div>
							<div id="" class="" style="float: right; margin-right: 5px;">
								<img src="/resources/share/img/air/1_icon1.png" style="max-width: 20px; height:auto;">&nbsp;
								<strong>국가관측망 (수신)</strong>
								<span id="empty_border" class="spanSelectBorder" style="width: 0; transition: 0.4s; left: 50%;"></span>
							</div>
						</div>
						<div style="margin-top: 8px; padding-right: 3px;">
							<div id="" class="" style="float: left; margin-right: 5px;">
								<img src="/resources/share/img/air/0_icon1.png" style="max-width: 20px; height:auto;">&nbsp;
								<strong>좋음</strong>
								<span id="empty_border" class="spanSelectBorder" style="width: 0; transition: 0.4s; left: 50%;"></span>
							</div>
							<div id="" class="" style="float: left; margin-right: 5px;">
								<img src="/resources/share/img/air/0_icon2.png" style="max-width: 20px; height:auto;">&nbsp;
								<strong>보통</strong>
								<span id="empty_border" class="spanSelectBorder" style="width: 0; transition: 0.4s; left: 50%;"></span>
							</div>
							<div id="" class="" style="float: left; margin-right: 5px;">
								<img src="/resources/share/img/air/0_icon3.png" style="max-width: 20px; height:auto;">&nbsp;
								<strong>나쁨</strong>
								<span id="empty_border" class="spanSelectBorder" style="width: 0; transition: 0.4s; left: 50%;"></span>
							</div>
							<div id="" class="" style="float: left; margin-right: 5px;">
								<img src="/resources/share/img/air/0_icon4.png" style="max-width: 20px; height:auto;">&nbsp;
								<strong>매우나쁨</strong>
								<span id="empty_border" class="spanSelectBorder" style="width: 0; transition: 0.4s; left: 50%;"></span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-12" id="refInfoOaqArea">
				<div class="row col-12" style="padding: 0; margin-left: 0;">
					<div class="card" style="width:100%;">
						<div id="refInfoOaqDiv" class="card-body">
						</div>					
					</div>
				</div>
				<div class="card" style="width:100%;" id="dongOaqChartDivArea">
				</div>
				<div class="row col-12" style="padding: 0; margin-left: 0;">
					<div class="card" style="width:100%;" id="oaqChartDivArea">
					</div>
				</div>
			</div>
		</div>
		<div class="row col-6" style="float: left; padding-right: 0px;">
			<div id="dtTableArea" class="col-12" >
				<div class="card">
					<div class="card-header">
						<h3 class="card-title"><strong>관측망</strong></h3>
					</div>
					<div class="card-body">
						<div style="float: right">
							<span><strong>시리얼 번호 : </strong></span>
							<input type="text" id="searchText" placeholder="Serial Number ..."/>
						</div>
						<table id="dustTable" class="table table-bordered table-hover">
							<colgroup>
								<col style="width:15%"></col>
								<col style="width:23%"></col>
								<col style="width:23%"></col>
								<col style="width:26%"></col>
								<col style="width:15%"></col>
							</colgroup>
							<thead>
                                <tr>
                                	<th class="bgGray1">시리얼 번호</th>
                                    <th class="bgGray1">미세먼지</th>
                                    <th class="bgGray1">초미세먼지</th>
                                    <th class="bgGray1">업데이트 시간</th>
                                    <th class="bgGray1">위치</th>
                                </tr>
                            </thead>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- /.row -->
	</section>
	<!-- /.content -->
	<!-- .Modal -->
	<div class="modal fade" id="modal-chart" style="display: none;"
		aria-hidden="true">
		<div class="modal-dialog modal-xl modal-chart">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="modalTitle"></h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<div id="modalChart1">
					</div>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.Modal -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->