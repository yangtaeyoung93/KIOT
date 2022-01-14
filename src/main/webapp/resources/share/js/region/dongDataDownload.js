/**
 * 동별 미세먼지&보정 ==> 동별 데이터 다운로드
 */

var dongTable;
function initDataTableCustom() {
	dongTable = $('#dongTable').DataTable({
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
        destroy: true,
        processing: true,
        serverSide: false,
		ajax : {
			 "url":"/api/dong/data/list",
			 "type":"GET",
		},
        columns : [
        	{data: "dfname"},
        	{data: "dfname"},
        	{data: "dfname"},
        	{data: "dfname"},
            {data: "dfname"}
        ],
        columnDefs: [ 
        	{
	        	targets: [0],
	        	render: function(data, type, full, meta) {
    				return "<strong>" + data + "</strong>";
	        	}
        	},
        	{
	        	targets: [1],
	        	render: function(data, type, full, meta) {
    				return "<strong>" + full.pmInfo.pm10 + "</strong>";
	        	}
        	},
        	{
	        	targets: [2],
	        	render: function(data, type, full, meta) {
    				return "<strong>" + gradeText(full.pmInfo.pm10_grade); + "</strong>";
	        	}
        	},
        	{
	        	targets: [3],
	        	render: function(data, type, full, meta) {
    				return "<strong>" + full.pmInfo.pm25 + "</strong>";;
	        	}
        	},
        	{
	        	targets: [4],
	        	render: function(data, type, full, meta) {
    				return "<strong>" + gradeText(full.pmInfo.pm25_grade); + "</strong>";;
	        	}
        	}
        ],
	});

	$(".dataTables_filter").hide();
}

function dataDownload() {
	var $preparingFileModal = $("#preparing-file-modal");
	$preparingFileModal.dialog({
		modal : true
	});

	$("#progressbar").progressbar({
		value : false
	});

	var param = "";
	var startTime = $("#startDt").val();
	var endTime = $("#endDt").val();
	var standard = $("#searchStandard option:selected").val();
	var sCode = $("#searchDfname1 option:selected").val();
	var gCode = $("#searchDfname2 option:selected").val();
	var dCode = $("#searchDfname3 option:selected").val();
	var pmType = $("#searchPmType option:selected").val();

	if (!startTime && !endTime) {
		var today = new Date();
		var year = today.getFullYear();
		var month = today.getMonth() + 1;
		var day = today.getDate();

		if (month < 10) month = "0" + month;

		if (day < 10) day = "0" + day;

		startTime = year + "/" + month + "/" + day + "-00:00:00";
		endTime = year + "/" + month + "/" + day + "-23:59:59";

	} else {
		startTime = startTime + "-00:00:00";
		endTime = endTime + "-23:59:59";
	}

	param = "startTime=" + startTime + "&endTime=" + endTime + "&standard=" + standard + "&sCode=" + sCode + "&gCode=" + gCode + "&dCode=" + dCode + "&pmType=" + pmType;

	$.fileDownload("/api/dong/data/excel/download?" + param, {
		successCallback : (url) => {
			$preparingFileModal.dialog('close');
		},
		failCallback : (responseHtml, url) => {
			$preparingFileModal.dialog('close');
			$("#error-modal").dialog({
				modal : true
			});
		}
	});

    return false;
}

function gradeText(grade) {
	var gradeTxt = "";

	if (grade == "1") 
		gradeTxt = "<font style='color:#44C7F5'>좋음</font>";
	else if (grade == "2") 
		gradeTxt = "<font style='color:#8DD538'>보통</font>";
	else if (grade == "3") 
		gradeTxt = "<font style='color:#F97B47'>나쁨</font>";
	else 
		gradeTxt = "<font style='color:#F93F3E'>매우나쁨</font>";

	return gradeTxt;
}

function datePickerCustom(referenceDate) {
	$("#startDt").datepicker("destroy");
	$("#endDt").datepicker("destroy");

	$("#startDt").datepicker({
		dateFormat : 'yy/mm/dd',
		onSelect : (selectDate) => {
			var stxt = selectDate.split("/");
			stxt[1] = stxt[1] - 1;

			var sdate = new Date(stxt[0], stxt[1], stxt[2]);
			var edate = new Date(stxt[0], stxt[1], stxt[2]);
			edate.setDate(sdate.getDate() + referenceDate);
			sdate.setDate(sdate.getDate() + referenceDate - 1);

			var s_year = sdate.getFullYear();
			var s_month = sdate.getMonth() + 1;
			var s_day = sdate.getDate();

			if (s_month < 10)
				s_month = "0" + s_month;
			if (s_day < 10)
				s_day = "0" + s_day;

			$('#endDt').datepicker('option', {
				minDate : selectDate,
				beforeShow : () => {
					$("#endDt").datepicker("option", "maxDate", edate);
				}
			});

			$('#endDt').focus();
		}
	}).datepicker("setDate", new Date());

	$("#endDt").datepicker({dateFormat : 'yy/mm/dd'}).datepicker("setDate", new Date());

	$("#startDt").datepicker("refresh");
	$("#endDt").datepicker("refresh");
}


function changeDo() {
	var doHtml = "";

	$.ajax({
		 url:"/api/dong/siList/dev",
		 type:"GET",
		 success : function (param) {
			 for (var i = 0; i < param.data.length; i++) 
				 doHtml += "<option data-dname='" + param.data[i].dname + "' value='" + param.data[i].sdcode + "'>" + param.data[i].dname + "</option>";

			 $("#searchDfname1").html(doHtml);
		 }
	})
}

function changeSi(sdcode) {
	var siHtml = "";

	$.ajax({
		 url:"/api/dong/guList/dev",
		 type:"GET",
		 data: "searchSdcode=" + sdcode,
		 success : function (param) {
			 siHtml += "<option data-dname='' value=''>전체</option>";
			 for (var i = 0; i < param.data.length; i++) 
				 siHtml += "<option data-dname='" + param.data[i].dname + "' value='" + param.data[i].sggcode + "'>" + param.data[i].dname + "</option>";

			 $("#searchDfname2").html(siHtml);
			 changeDong(sdcode, param.data[0].sggcode);
		 }
	})
}

function changeDong(sdcode, sggcode) {
	var dongHtml = "";

	$.ajax({
		 url:"/api/dong/dongList/dev",
		 type:"GET",
		 data: "searchSdcode=" + sdcode + "&searchSggcode=" + sggcode,
		 success : function (param) {
			 dongHtml += "<option data-dname='' value=''>전 체</option>";

			 for (var i = 0; i < param.data.length; i++) 
				 dongHtml += "<option data-dname='" + param.data[i].dname + "' value='" + param.data[i].dcode + "'>" + param.data[i].dname + "</option>";

			 $("#searchDfname3").html(dongHtml);
			 $("#searchDname").val("");

			 var searchValue = 
				$("#searchDfname1 option:selected").attr("data-dname") + " " + 
				$("#searchDfname2 option:selected").attr("data-dname") + " " + 
				$("#searchDfname3 option:selected").attr("data-dname");

			dongTable.column(0).search(searchValue).draw();
		 }
	})
}

$().ready(() => {
	$('.select2').select2();
    $('.select2bs4').select2({
    	theme: 'bootstrap4'
    });

    changeDo();
	changeSi(11);
	changeDong(11,110);

	datePickerCustom(365);

	initDataTableCustom();

	$("#startDt").one("click", () => {
		alert("최대 1년치 데이터, 다운로드 가능합니다.");
	})

	$("#searchDfname1").change(function(){
		var sdcode = $(this).val();
		changeSi(sdcode);
	})

	$("#searchDfname2").change(function(){
		var sdcode = $("#searchDfname1").val();
		var sggcode = $(this).val();
		changeDong(sdcode, sggcode);
	})

	$("#searchDfname3").change(function(){
		var sdcode = $("#searchDfname1").val();
		var sggcode = $(this).val();
		var dongName = $("#searchDfname3 option:selected").text();

		var searchValue = 
			$("#searchDfname1 option:selected").attr("data-dname") + " " + 
			$("#searchDfname2 option:selected").attr("data-dname") + " " + 
			$("#searchDfname3 option:selected").attr("data-dname");

		dongTable.column(0).search(searchValue).draw();
	})
})