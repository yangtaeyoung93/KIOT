<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<link rel="stylesheet" href="https://openlayers.org/en/v5.3.0/css/ol.css" type="text/css">
<!-- ##### Header Area End ##### -->
<html>
<head><style type="text/css">.swal-icon--error{border-color:#f27474;-webkit-animation:animateErrorIcon .5s;animation:animateErrorIcon .5s}.swal-icon--error__x-mark{position:relative;display:block;-webkit-animation:animateXMark .5s;animation:animateXMark .5s}.swal-icon--error__line{position:absolute;height:5px;width:47px;background-color:#f27474;display:block;top:37px;border-radius:2px}.swal-icon--error__line--left{-webkit-transform:rotate(45deg);transform:rotate(45deg);left:17px}.swal-icon--error__line--right{-webkit-transform:rotate(-45deg);transform:rotate(-45deg);right:16px}@-webkit-keyframes animateErrorIcon{0%{-webkit-transform:rotateX(100deg);transform:rotateX(100deg);opacity:0}to{-webkit-transform:rotateX(0deg);transform:rotateX(0deg);opacity:1}}@keyframes animateErrorIcon{0%{-webkit-transform:rotateX(100deg);transform:rotateX(100deg);opacity:0}to{-webkit-transform:rotateX(0deg);transform:rotateX(0deg);opacity:1}}@-webkit-keyframes animateXMark{0%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}50%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}80%{-webkit-transform:scale(1.15);transform:scale(1.15);margin-top:-6px}to{-webkit-transform:scale(1);transform:scale(1);margin-top:0;opacity:1}}@keyframes animateXMark{0%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}50%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}80%{-webkit-transform:scale(1.15);transform:scale(1.15);margin-top:-6px}to{-webkit-transform:scale(1);transform:scale(1);margin-top:0;opacity:1}}.swal-icon--warning{border-color:#f8bb86;-webkit-animation:pulseWarning .75s infinite alternate;animation:pulseWarning .75s infinite alternate}.swal-icon--warning__body{width:5px;height:47px;top:10px;border-radius:2px;margin-left:-2px}.swal-icon--warning__body,.swal-icon--warning__dot{position:absolute;left:50%;background-color:#f8bb86}.swal-icon--warning__dot{width:7px;height:7px;border-radius:50%;margin-left:-4px;bottom:-11px}@-webkit-keyframes pulseWarning{0%{border-color:#f8d486}to{border-color:#f8bb86}}@keyframes pulseWarning{0%{border-color:#f8d486}to{border-color:#f8bb86}}.swal-icon--success{border-color:#a5dc86}.swal-icon--success:after,.swal-icon--success:before{content:"";border-radius:50%;position:absolute;width:60px;height:120px;background:#fff;-webkit-transform:rotate(45deg);transform:rotate(45deg)}.swal-icon--success:before{border-radius:120px 0 0 120px;top:-7px;left:-33px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-transform-origin:60px 60px;transform-origin:60px 60px}.swal-icon--success:after{border-radius:0 120px 120px 0;top:-11px;left:30px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-transform-origin:0 60px;transform-origin:0 60px;-webkit-animation:rotatePlaceholder 4.25s ease-in;animation:rotatePlaceholder 4.25s ease-in}.swal-icon--success__ring{width:80px;height:80px;border:4px solid hsla(98,55%,69%,.2);border-radius:50%;box-sizing:content-box;position:absolute;left:-4px;top:-4px;z-index:2}.swal-icon--success__hide-corners{width:5px;height:90px;background-color:#fff;padding:1px;position:absolute;left:28px;top:8px;z-index:1;-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}.swal-icon--success__line{height:5px;background-color:#a5dc86;display:block;border-radius:2px;position:absolute;z-index:2}.swal-icon--success__line--tip{width:25px;left:14px;top:46px;-webkit-transform:rotate(45deg);transform:rotate(45deg);-webkit-animation:animateSuccessTip .75s;animation:animateSuccessTip .75s}.swal-icon--success__line--long{width:47px;right:8px;top:38px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-animation:animateSuccessLong .75s;animation:animateSuccessLong .75s}@-webkit-keyframes rotatePlaceholder{0%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}5%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}12%{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}to{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}}@keyframes rotatePlaceholder{0%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}5%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}12%{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}to{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}}@-webkit-keyframes animateSuccessTip{0%{width:0;left:1px;top:19px}54%{width:0;left:1px;top:19px}70%{width:50px;left:-8px;top:37px}84%{width:17px;left:21px;top:48px}to{width:25px;left:14px;top:45px}}@keyframes animateSuccessTip{0%{width:0;left:1px;top:19px}54%{width:0;left:1px;top:19px}70%{width:50px;left:-8px;top:37px}84%{width:17px;left:21px;top:48px}to{width:25px;left:14px;top:45px}}@-webkit-keyframes animateSuccessLong{0%{width:0;right:46px;top:54px}65%{width:0;right:46px;top:54px}84%{width:55px;right:0;top:35px}to{width:47px;right:8px;top:38px}}@keyframes animateSuccessLong{0%{width:0;right:46px;top:54px}65%{width:0;right:46px;top:54px}84%{width:55px;right:0;top:35px}to{width:47px;right:8px;top:38px}}.swal-icon--info{border-color:#c9dae1}.swal-icon--info:before{width:5px;height:29px;bottom:17px;border-radius:2px;margin-left:-2px}.swal-icon--info:after,.swal-icon--info:before{content:"";position:absolute;left:50%;background-color:#c9dae1}.swal-icon--info:after{width:7px;height:7px;border-radius:50%;margin-left:-3px;top:19px}.swal-icon{width:80px;height:80px;border-width:4px;border-style:solid;border-radius:50%;padding:0;position:relative;box-sizing:content-box;margin:20px auto}.swal-icon:first-child{margin-top:32px}.swal-icon--custom{width:auto;height:auto;max-width:100%;border:none;border-radius:0}.swal-icon img{max-width:100%;max-height:100%}.swal-title{color:rgba(0,0,0,.65);font-weight:600;text-transform:none;position:relative;display:block;padding:13px 16px;font-size:27px;line-height:normal;text-align:center;margin-bottom:0}.swal-title:first-child{margin-top:26px}.swal-title:not(:first-child){padding-bottom:0}.swal-title:not(:last-child){margin-bottom:13px}.swal-text{font-size:16px;position:relative;float:none;line-height:normal;vertical-align:top;text-align:left;display:inline-block;margin:0;padding:0 10px;font-weight:400;color:rgba(0,0,0,.64);max-width:calc(100% - 20px);overflow-wrap:break-word;box-sizing:border-box}.swal-text:first-child{margin-top:45px}.swal-text:last-child{margin-bottom:45px}.swal-footer{text-align:right;padding-top:13px;margin-top:13px;padding:13px 16px;border-radius:inherit;border-top-left-radius:0;border-top-right-radius:0}.swal-button-container{margin:5px;display:inline-block;position:relative}.swal-button{background-color:#7cd1f9;color:#fff;border:none;box-shadow:none;border-radius:5px;font-weight:600;font-size:14px;padding:10px 24px;margin:0;cursor:pointer}.swal-button:not([disabled]):hover{background-color:#78cbf2}.swal-button:active{background-color:#70bce0}.swal-button:focus{outline:none;box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(43,114,165,.29)}.swal-button[disabled]{opacity:.5;cursor:default}.swal-button::-moz-focus-inner{border:0}.swal-button--cancel{color:#555;background-color:#efefef}.swal-button--cancel:not([disabled]):hover{background-color:#e8e8e8}.swal-button--cancel:active{background-color:#d7d7d7}.swal-button--cancel:focus{box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(116,136,150,.29)}.swal-button--danger{background-color:#e64942}.swal-button--danger:not([disabled]):hover{background-color:#df4740}.swal-button--danger:active{background-color:#cf423b}.swal-button--danger:focus{box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(165,43,43,.29)}.swal-content{padding:0 20px;margin-top:20px;font-size:medium}.swal-content:last-child{margin-bottom:20px}.swal-content__input,.swal-content__textarea{-webkit-appearance:none;background-color:#fff;border:none;font-size:14px;display:block;box-sizing:border-box;width:100%;border:1px solid rgba(0,0,0,.14);padding:10px 13px;border-radius:2px;transition:border-color .2s}.swal-content__input:focus,.swal-content__textarea:focus{outline:none;border-color:#6db8ff}.swal-content__textarea{resize:vertical}.swal-button--loading{color:transparent}.swal-button--loading~.swal-button__loader{opacity:1}.swal-button__loader{position:absolute;height:auto;width:43px;z-index:2;left:50%;top:50%;-webkit-transform:translateX(-50%) translateY(-50%);transform:translateX(-50%) translateY(-50%);text-align:center;pointer-events:none;opacity:0}.swal-button__loader div{display:inline-block;float:none;vertical-align:baseline;width:9px;height:9px;padding:0;border:none;margin:2px;opacity:.4;border-radius:7px;background-color:hsla(0,0%,100%,.9);transition:background .2s;-webkit-animation:swal-loading-anim 1s infinite;animation:swal-loading-anim 1s infinite}.swal-button__loader div:nth-child(3n+2){-webkit-animation-delay:.15s;animation-delay:.15s}.swal-button__loader div:nth-child(3n+3){-webkit-animation-delay:.3s;animation-delay:.3s}@-webkit-keyframes swal-loading-anim{0%{opacity:.4}20%{opacity:.4}50%{opacity:1}to{opacity:.4}}@keyframes swal-loading-anim{0%{opacity:.4}20%{opacity:.4}50%{opacity:1}to{opacity:.4}}.swal-overlay{position:fixed;top:0;bottom:0;left:0;right:0;text-align:center;font-size:0;overflow-y:auto;background-color:rgba(0,0,0,.4);z-index:10000;pointer-events:none;opacity:0;transition:opacity .3s}.swal-overlay:before{content:" ";display:inline-block;vertical-align:middle;height:100%}.swal-overlay--show-modal{opacity:1;pointer-events:auto}.swal-overlay--show-modal .swal-modal{opacity:1;pointer-events:auto;box-sizing:border-box;-webkit-animation:showSweetAlert .3s;animation:showSweetAlert .3s;will-change:transform}.swal-modal{width:478px;opacity:0;pointer-events:none;background-color:#fff;text-align:center;border-radius:5px;position:static;margin:20px auto;display:inline-block;vertical-align:middle;-webkit-transform:scale(1);transform:scale(1);-webkit-transform-origin:50% 50%;transform-origin:50% 50%;z-index:10001;transition:opacity .2s,-webkit-transform .3s;transition:transform .3s,opacity .2s;transition:transform .3s,opacity .2s,-webkit-transform .3s}@media (max-width:500px){.swal-modal{width:calc(100% - 20px)}}@-webkit-keyframes showSweetAlert{0%{-webkit-transform:scale(1);transform:scale(1)}1%{-webkit-transform:scale(.5);transform:scale(.5)}45%{-webkit-transform:scale(1.05);transform:scale(1.05)}80%{-webkit-transform:scale(.95);transform:scale(.95)}to{-webkit-transform:scale(1);transform:scale(1)}}@keyframes showSweetAlert{0%{-webkit-transform:scale(1);transform:scale(1)}1%{-webkit-transform:scale(.5);transform:scale(.5)}45%{-webkit-transform:scale(1.05);transform:scale(1.05)}80%{-webkit-transform:scale(.95);transform:scale(.95)}to{-webkit-transform:scale(1);transform:scale(1)}}</style>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="shortcut icon" href="/resources/share/img/k_fav.ico" type="image/x-icon">
<title>Air365 플랫폼 관리자</title>
<!-- Tell the browser to be responsive to screen width -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Font Awesome -->
<link rel="stylesheet" href="/resources/plugins/fontawesome-free/css/all.min.css">
<!-- DataTables -->
<link rel="stylesheet" href="/resources/plugins/datatables-bs4/css/dataTables.bootstrap4.css">
<!-- daterange picker -->
<link rel="stylesheet" href="/resources/plugins/daterangepicker/daterangepicker.css">
<!-- iCheck for checkboxes and radio inputs -->
<link rel="stylesheet" href="/resources/plugins/icheck-bootstrap/icheck-bootstrap.min.css">
<!-- Theme style -->
<link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">
<!-- common css -->
<link rel="stylesheet" href="/resources/docs/assets/css/common.css">
<!-- Select2 -->
<link rel="stylesheet" href="/resources/plugins/select2/css/select2.min.css">
<link rel="stylesheet" href="/resources/plugins/select2-bootstrap4-theme/select2-bootstrap4.min.css">
<!-- Bootstrap4 Duallistbox -->
<link rel="stylesheet" href="/resources/plugins/bootstrap4-duallistbox/bootstrap-duallistbox.min.css">

<!-- jQuery -->
<script src="/resources/plugins/jquery/jquery.min.js"></script>
<script src="/resources/plugins/jquery/jquery.form.js"></script>
<!-- InputMask -->
<script src="/resources/plugins/moment/moment.min.js"></script>
<script src="/resources/plugins/inputmask/min/jquery.inputmask.bundle.min.js"></script>
<!-- Bootstrap 4 -->
<script src="/resources/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- DataTables -->
<script src="/resources/plugins/datatables/jquery.dataTables.js"></script>
<script src="/resources/plugins/datatables-bs4/js/dataTables.bootstrap4.js"></script>
<script src="https://cdn.datatables.net/buttons/1.5.2/js/dataTables.buttons.min.js"></script>
<script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.html5.min.js"></script>
<script src="https://cdn.datatables.net/responsive/2.2.3/js/dataTables.responsive.js"></script>
<!-- Bootstrap4 Duallistbox -->
<script src="/resources/plugins/bootstrap4-duallistbox/jquery.bootstrap-duallistbox.min.js"></script>
<!-- ADD -->
<link rel="stylesheet" href="/resources/docs/assets/css/common.css">
<!-- amchart -->
<script src="//www.amcharts.com/lib/4/core.js"></script>
<script src="//www.amcharts.com/lib/4/charts.js"></script>
<script src="https://www.amcharts.com/lib/4/themes/dark.js"></script>
<script src="https://www.amcharts.com/lib/4/themes/kelly.js"></script>
<script src="//www.amcharts.com/lib/4/themes/animated.js"></script>
<script src="/resources/share/js/common/common.js"></script>

<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<script>
	function goFota(){
		var url = "http://fotasystem.kweather.co.kr/KETIKIOT";
		var name = "Fota FirmWare";
		var option = "width=1100, height=800, top=100, left=200";
		window.open(url, name, option);
	} 
</script>
</head>

<script>
var serialNum = '<%=request.getParameter("serial")%>';
var parentSpaceName = '<%=request.getParameter("parentSpaceName")%>';
var spaceName = '<%=request.getParameter("spaceName")%>';
var productDt = '<%=request.getParameter("productDt")%>';
var stationName = '<%=request.getParameter("stationName")%>';
var standard = '<%=request.getParameter("standard")%>';
var deviceType = '<%=request.getParameter("deviceType")%>';
</script>
<body>
	<div class="content-wrapper" style="margin-left:0px !important;">
		<section class="content-header">
			<div class="container-fluid">
				<div class="row mb-2">
					<h4 class="modal-title"><strong><span id="txtStationName"></span>상세정보</strong></h4>
				</div>
			</div>
			<!-- /.container-fluid -->
		</section>
		<section class="content">
		<div class="row">
			<div class="col-12">
			<div class="card">
					<form role="form">
						<div class="card-body">
							<!--  serialNum,parentSpaceName,spaceName,productDt,stationName-->
							<input type="hidden" id="p_h_serialNum"/>
							<input type="hidden" id="p_h_parentSpaceName"/>
							<input type="hidden" id="p_h_spaceName"/>
							<input type="hidden" id="p_h_productDt"/>
							<input type="hidden" id="p_h_stationName"/> 
							<div class="row" style="border-top:3px solid #aaa; height:35px; line-height:35px;">
								<div class="col-2" style="background-color:#e6edf9; border-bottom:1px solid #aaa;">시리얼번호</div>
								<div class="col-4" id="txtSerialNum" style="border-bottom:1px solid #aaa;"></div>
								<div class="col-2" style="background-color:#e6edf9; border-bottom:1px solid #aaa;">공간카테고리</div>
								<div class="col-4" id="txtSpace" style="border-bottom:1px solid #aaa;"></div>
							</div>
							<div class="row" style="height:35px; line-height:35px;margin-top: 4px;">
								<div class="col-2" style="border-bottom:3px solid #aaa; background-color:#e6edf9;">등록일자</div>
								<div class="col-4" id="txtProductDt" style="border-bottom:3px solid #aaa;"></div>
								<div class="col-2" style="border-bottom:3px solid #aaa;"></div>
								<div class="col-4" style="border-bottom:3px solid #aaa;"></div>
							</div>
							
							<div class="form-group mt20">
								<!-- sum, 5m-avg-none, 1h-avg-none, 1d-avg-none, 1n-avg-none -->
								<!--  <select name="searchStandard" id="searchStandard" class="custom-select custom-select-sm form-control form-control-sm mr10" style="width:120px;height:38px;">
									<option value= "sum">전체</option>
									<option value= "5m-avg-none">5분 평균</option>
									<option value= "1h-avg-none">1시간 평균</option>
									<option value= "1d-avg-none">1일 평균</option>
									<option value= "1n-avg-none">1개월 평균</option>
								</select>-->
								
								<input type="text" class="form-control d-inline-block" name="startDt" id="startDt" placeholder="시작일자 클릭 후 선택" style="width:150px; vertical-align: middle;"> ~ 
								<input type="text" class="form-control d-inline-block mr10" name="endDt" id="endDt" placeholder="종료일자 클릭 후 선택" style="width:150px; vertical-align: middle;">
								<input type="button" class="btn btn-primary ml3 mr10" style="min-width:100px;" id="modalSearchBtn" value="검 색">
									
								<!--<select style="min-width:100px; height:38px; float: right;" id="chartTypeSelect">
								</select>-->
								<div id="chartTypes" style="display: inline-block; float: right;">
								</div>
							</div>
							
							<div class="form-group mt10" id="timeChartDiv" style="height: 500px;"></div>
							<div class="form-group mt10">
							 	<h5 style="display:inline-block;margin-bottom: 0.25rem; vertical-align: middle;" id="tableTitle">검색 결과</h5>
							 	<!--  <input type="button" class="btn btn-primary ml3 mr10" style="min-width:100px; float:right; margin-bottom: 12px;" id="modalDownloadBtn" value="다운로드">-->
							 	<table id="popTable" class="table table-bordered table-hover text-center" style="margin-top: 20px !important;">
							 		<thead>
		                             	<tr id="popTableHead">
		                             	</tr>
	                             	</thead>
	                             	<tbody>
                                		<tr id="popTableBody">
                                		</tr>
                                	</tbody>
	                             </table>
							</div>
							
						</div>
						<!-- /.card-body -->
						
					</form>
				</div>
			</div>
		</div>
		</section>
	</div>
</body>
<script>
var hisApiData = [];
var columsData = [];

$().ready(function() {
	var elemetsData;
	var collectionColum = new Array();
	
	if(deviceType == "IAQ"){
		$("#lastOptVal").val("cici");
	}else{
		$("#lastOptVal").val("coci");
	}
	
	if(!$("#startDt").val()){
		var now_date = new Date();
		var now_year = now_date.getFullYear();
		var now_month = now_date.getMonth()+1;
		var now_day = now_date.getDate();
		
		if(now_month < 10){
			now_month = "0"+now_month;
		}
		if(now_day < 10){
			now_day = "0"+now_day;
		}
		
		$("#startDt").val(now_year+"/"+now_month+"/"+now_day);
	}
	if(!$("#endDt").val()){
		var now_date = new Date();
		var now_year = now_date.getFullYear();
		var now_month = now_date.getMonth()+1;
		var now_day = now_date.getDate();
		
		if(now_month < 10){
			now_month = "0"+now_month;
		}
		if(now_day < 10){
			now_day = "0"+now_day;
		}
		
		$("#endDt").val(now_year+"/"+now_month+"/"+now_day);
	}
	
	
	$.ajax({
		method : "GET",
		async : false,
		url : "/api/datacenter/elements?serial=" + serialNum,
		success : function(d) {
			elemetsData = d.data;
			if (elemetsData.length != 0) {
				columsData.push({"data" : "formatTimestamp"});

				for (var key in elemetsData) {
					columsData.push({"data" : elemetsData[key].engName});
					collectionColum.push(elemetsData[key].engName);
				}

				if (deviceType == "IAQ") columsData.push({"data" : "cici"});
				else columsData.push({"data" : "coci"});
			}

			if (collectionColum.length != 0) {
				tableTrDraw(elemetsData, d, collectionColum);
			}

			selectHistory(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType);
		}
	});
	
	
	$("#modalSearchBtn").click(function() {
		var serialNum = $("#p_h_serialNum").val();
		var parentSpaceName = $("#p_h_parentSpaceName").val();
		var spaceName = $("#p_h_spaceName").val();
		var productDt = $("#p_h_productDt").val();
		var stationName = $("#p_h_stationName").val();
		var standard = "sum";
		
		selectHistory(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType);
	})
	
	var rangeDate = 30; // set limit day
	var setSdate, setEdate;
	
	var rangeDate = 31; // set limit day
	var setSdate, setEdate;
	
	$("#startDt").datepicker({
		dateFormat: 'yy/mm/dd',
	    onSelect: function(selectDate){
	        var stxt = selectDate.split("/");
	        stxt[1] = stxt[1] - 1;
	        var sdate = new Date(stxt[0], stxt[1], stxt[2]);
	        var edate = new Date(stxt[0], stxt[1], stxt[2]);
	            edate.setDate(sdate.getDate() + rangeDate);
	        
	        $('#endDt').datepicker('option', {
	            minDate: selectDate,
	            beforeShow : function () {
	                $("#endDt").datepicker( "option", "maxDate", edate );                
	                setSdate = selectDate;
	        }});

	        $('#endDt').focus();
	    }
	})

	$("#endDt").datepicker({
		dateFormat: 'yy/mm/dd',
		onSelect : function(selectDate){
	        setEdate = selectDate;
	    }
	})

	$('#chartTypes').click(function() {
		chgChartData();
	})

	$('#modalDownloadBtn').click(function () {
		$('.excelDownBtn').trigger('click');
	})
})

function chgChartData() {
    var chkArr = Array();
    var obj = document.getElementsByName("chartType");

    for(var i=0; i < obj.length; i++) if (document.getElementsByName("chartType")[i].checked == true) chkArr.push(obj[i].value);

    timeChartCreate("timeChartDiv", hisApiData, chkArr);
}
	
function tableTrDraw(datas, rawDatas, colums) {
	var ciType = "";

	if (deviceType == "IAQ") ciType = "cici";
	else ciType = "coci";

	var optionIdx = 0;
	var trHeadHTML = "";
	var trBodyHTML = "";
	var chartTypesHTML = "";
	var optionName = "";

	trHeadHTML += "<th style='width: 200px;' class='bgGray1'>데이터 시간</th>"
	trBodyHTML += "<td></td>";
	trBodyHTML += "<td></td>";

	for (var i = 0; i < colums.length; i++) {
		optionIdx++;
		trHeadHTML += "<th style='width: 200px;' class='bgGray1'>"
			+ datas[i].korName + "<br/>(" + datas[i].elementUnit + ")</th>";
		trBodyHTML += "<td></td>";
		chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='" 
			+ colums[i] + "'>" + datas[i].korName + "</label>";
		if (optionIdx % 10 == 0) chartTypesHTML += "<br/>";
	}

	trHeadHTML += "<th style='width: 200px;' class='bgGray1'>통합지수<br/>("
		+ ciType + ")</th>";
	chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='" 
		+ ciType + "'>통합지수(" + ciType + ")</label>";

	$('#popTableHead').html(trHeadHTML);
	$('#popTableBody').html(trBodyHTML);
	$('#chartTypes').html(chartTypesHTML);
}

function selectHistory(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType) {
	var obj = new Object();

	var startTime = $("#startDt").val();
	var endTime = $("#endDt").val();
	var r_deviceType = deviceType;

	if(!startTime && !endTime) {
		var today = new Date();   
		var year = today.getFullYear(); // 년도
		var month = today.getMonth() + 1;  // 월
		var day = today.getDate();  // 날짜
		
		if(month<10){ month = "0"+month; }
		if(day<10){ day = "0"+day; }
		
		startTime = year+"/"+month+"/"+day+"-00:00:00";
		endTime = year+"/"+month+"/"+day+"-23:59:59";
	}else{
		startTime = startTime+"-00:00:00";
		endTime =  endTime+"-23:59:59";
	} 

	$("#p_h_serialNum").val(serialNum);
	$("#p_h_parentSpaceName").val(parentSpaceName);
	$("#p_h_spaceName").val(spaceName);
	$("#p_h_productDt").val(productDt);
	$("#p_h_stationName").val(stationName);

	if(!standard){
		standard = "sum";
	}

	if(r_deviceType == "IAQ"){
		r_deviceType = "iaq";
	}else if(r_deviceType == "OAQ"){
		r_deviceType = "oaq";
	}else if(r_deviceType == "DOT"){
		r_deviceType = "dot"
	}

	obj = {
		deviceType : r_deviceType, //사용자 계정
		serial : $("#p_h_serialNum").val(), //장비idx
		startTime : startTime, // 2020/04/23-00:00:00
		endTime : endTime,   // 2020/04/23-23:59:59
		standard : standard,
		connect : "0"
	}

	$.ajax({
		method : "GET",
		url : "/api/collection/history",
		data : obj,
		success : function(d) {
			hisApiData = d.data;
			
			for (var i = 0; i < columsData.length; i++) {
				var key = columsData[i].data;
				for (var j = 0; j < hisApiData.length; j++) 
					if (hisApiData[j][key] == null) {
						hisApiData[j][key] = null;//"-";
					}
			}

			for (var i in hisApiData) {
				var convertTime = "";

				if (hisApiData[i].timestamp == null) {
					convertTime = "";
				} else {
					var date = new Date(hisApiData[i].timestamp * 1000);
					var year = date.getFullYear();
					var month = date.getMonth() + 1;
					var day = date.getDate();

					var hours = date.getHours();
					var minutes = date.getMinutes();
					var seconds = date.getSeconds();

					if (month < 10) {
						month = '0' + month;
					}
					if (day < 10) {
						day = '0' + day;
					}
					if (hours < 10) {
						hours = '0' + hours;
					}
					if (minutes < 10) {
						minutes = '0' + minutes;
					}
					if (seconds < 10) {
						seconds = '0' + seconds;
					}

					convertTime = year + "-" + month + "-" + day + "\n" + hours
							+ ":" + minutes;
				}

				hisApiData[i].convertTime = convertTime;
				hisApiData[i].ci = hisApiData[i].coci;
			}

			viewData(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType);
		}
	});
}


function viewData(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType) {
	var h_serialNum = $("#p_h_serialNum").val();
	var h_parentSpaceName = $("#p_h_parentSpaceName").val();
	var h_spaceName  = $("#p_h_spaceName").val();
	var h_productDt  = $("#p_h_productDt").val();
	var h_stationName  = $("#p_h_stationName").val();
	var spaceStr = "";
	
	if(!h_parentSpaceName && !h_spaceName){
		spaceStr = "없음";
	}else if(h_parentSpaceName && !h_spaceName){
		spaceStr = h_parentSpaceName;
	}else if(!h_parentSpaceName && h_spaceName){
		spaceStr = h_spaceName;
	}
	
	$("#txtSerialNum").text(h_serialNum);
	$("#txtSpace").text(spaceStr);
	$("#txtProductDt").text(h_productDt);
	$("#txtStationName").text(h_stationName+"의 ");

	var table = $('#popTable').DataTable({
		scrollCollapse: true,
		autoWidth: false,
		language : {
			"emptyTable" : "데이터가 없습니다.",
			"lengthMenu" : "페이지당 _MENU_ 개씩 보기",
			"info" : "현재 _START_ - _END_ / _TOTAL_건",
			"infoEmpty" : "데이터 없음",
			"infoFiltered" : "( _MAX_건의 데이터에서 필터링됨 )",
			"search" : "",
			"zeroRecords" : "일치하는 데이터가 없습니다.",
			"loadingRecords" : "로딩중...",
			"processing" : "잠시만 기다려 주세요.",
			"paginate" : {
				"next" : "다음",
				"previous" : "이전"
			}
		},
		dom : '<"top"Blf>rt<"bottom"ip><"clear">',
		responsive : false,
		buttons : [ {
			extend : 'csv',
			charset : 'UTF-8',
			text : '엑셀 다운로드',
			footer : false,
			bom : true,
			filename : h_serialNum +"_"+$("#startDt").val()+"_"+$("#endDt").val()+"_download",
			className : 'btn-primary btn excelDownBtn'
		} ],
		destroy: true,
        processing: true,
        serverSide: false,
		data : hisApiData,
        columns : columsData,
        initComplete: function (settings, data) {
        	//timeChartCreate("timeChartDiv", hisApiData, $('#chartTypeSelect').val());
			chgChartData();
        }
	});
	$("#popTable_filter").hide();
	$($("#tableTitle")).insertBefore("#popTable_length");
	$("#popTable_length").css("display","inline-block");
	$("#popTable_length").css("margin-left","20px");
	$("#modalDownloadBtn").insertAfter("#popTable_filter");
	$("#popTable_wrapper .top").first().css("border-bottom", "1px solid #E5E5E5");
	$("#popTable_wrapper .top").first().css("padding-bottom", "15px");
	$('.excelDownBtn').css("float","right");
}

function timeChartCreate(chartDiv, hisData, options) {
	am4core.addLicense("CH205407412");
	// am4core.useTheme(am4themes_animated);
	
	if (hisData == "undefined" || hisData == null || !hisData) {
		alert("해당 장비에 수집된 이력이 없습니다.");
		return;
	}

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.data = hisData;
	chart.cursor = new am4charts.XYCursor();
	chart.cursor.behavior = "panXY";
    chart.scrollbarX = new am4charts.XYChartScrollbar();
    chart.scrollbarX.parent = chart.topAxesContainer;

    var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "convertTime";
	categoryAxis.renderer.opposite = false;
	categoryAxis.connect = false;

	for (var idx in options) createSeries(options[idx]);

	function chgOptionNameFunc(name) {
		var chgName = "";

		if (name == "pm10") chgName = "미세먼지 (㎍/㎥)";
		else if (name == "pm25") chgName = "초미세먼지 (㎍/㎥)";
		else if (name == "co2") chgName = "이산화탄소 (ppm)";
		else if (name == "voc") chgName = "휘발성유기화합물 (ppb)";
		else if (name == "noise") chgName = "소음 (dB)";
		else if (name == "temp") chgName = "온도 (℃ )";
		else if (name == "humi") chgName = "습도 (%)";
		else if (name == "cici") chgName = "통합지수 (cici)";
		else if (name == "coci") chgName = "통합지수 (coci)";
		else chgName = name;

		return chgName;
	}

	function createSeries(name) {
		var chgName = chgOptionNameFunc(name);

		var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
		valueAxis.renderer.inversed = false;
		valueAxis.title.text = chgName;
		valueAxis.renderer.minLabelPosition = 0.01;
		valueAxis.connect = false;
        valueAxis.renderer.line.strokeOpacity = 1;
        valueAxis.renderer.line.strokeWidth = 2;

        var interfaceColors = new am4core.InterfaceColorSet();

		var series = chart.series.push(new am4charts.LineSeries());
        series.dataFields.valueY = name;
        series.dataFields.categoryX = "convertTime";
        series.yAxis = valueAxis;
        series.name = chgName;
        series.tensionX = 0.8;
        series.showOnInit = true;

        var segment = series.segments.template;
		segment.interactionsEnabled = true;

		var hoverState = segment.states.create("hover");
		hoverState.properties.strokeWidth = 3;

		var dimmed = segment.states.create("dimmed");
		dimmed.properties.stroke = am4core.color("#dadada");

		series.tooltip.background.cornerRadius = 20;
		series.tooltip.background.strokeOpacity = 0;
		series.tooltip.pointerOrientation = "vertical";
		series.tooltip.label.minWidth = 40;
		series.tooltip.label.minHeight = 40;
		series.tooltip.label.textAlign = "middle";
		series.tooltip.label.textValign = "middle";
		series.tooltipText = "{name}: [bold]{valueY}[/]";

		series.strokeWidth = 3;

	    chart.scrollbarX.series.push(series);

		if (name != 'visitor') series.connect = false;

        valueAxis.renderer.line.stroke = series.stroke;
        valueAxis.renderer.labels.template.fill = series.stroke;
        valueAxis.renderer.opposite = true;

        valueAxis.renderer.line.strokeOpacity = 1;
        valueAxis.renderer.line.strokeWidth = 2;
        valueAxis.renderer.line.stroke = series.stroke;
        valueAxis.renderer.labels.template.fill = series.stroke;
        valueAxis.renderer.opposite = true;
	}

	chart.legend = new am4charts.Legend();
}


</script>

</html>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<!-- ##### Footer Area End ##### -->