/**
 * 미세먼지 데이터 
 */

function initDataTableCustom(){
	var table = $('#dustTable').DataTable({
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
        dom : '<"top"lfB>rt<"bottom"ip><"clear">',
        buttons : [ {
        	extend : 'csv',
        	charset : 'UTF-8',
        	text : '엑셀 다운로드',
        	footer : false,
        	bom : true,
        	filename : '동별_미세먼지_데이터_다운로드',
        	className : 'btn-primary btn excelDownBtn'
        } ],
		ajax : {
			 "url":"/api/dong/data/list",
			 "type":"GET"
		},
        columns : [
        	{data: "serial"},
        	{data: "serial"},
        	{data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
	        		return meta.row + meta.settings._iDisplayStart + 1;
				},
        	},
        	{
	        	targets:   1,
	        	render: function(data, type, full, meta) {
	        		var dcode = "";

	        		if (!full.dcode) dcode = "";
	        		else dcode = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.dcode+"</strong></a>";

	        		return dcode;
				},
        	},
        	{
	        	targets:   2,
	        	render: function(data, type, full, meta) {
	        		var dfname = "";

	        		if (!full.dfname) dfname = "";
	        		else dfname = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.dfname+"</strong></a>";

	        		return dfname;
				},
        	},
        	{
	        	targets:   3,
	        	render: function(data, type, full, meta) {
	        		var lat = "";

	        		if (!full.lat) lat = "";
	        		else lat = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.lat+"</strong></a>";

        			return lat;
				},
        	},
        	{
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		var lon = "";

	        		if (!full.lon) lon = "";
	        		else lon = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.lon+"</strong></a>";

	        		return lon;
				},
        	},
        	{
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		var regDate = "";

	        		if(!full.regDate) regDate = "";
	        		else {
	        			var year = full.regDate.substring(0,4);
		        		var month = full.regDate.substring(4,6);
		        		var day = full.regDate.substring(6,8);
		        		var hour = full.regDate.substring(8,10);
		        		var minute = full.regDate.substring(10,12);
		        		var second = full.regDate.substring(12,14);
		        		var convertTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

		        		regDate = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+convertTime+"</strong></a>";
	        		}

	        		return regDate;
				},
        	},
        	{
	        	targets:   6,
	        	render: function(data, type, full, meta) {
	        		var pm10Value = "";

	        		if (!full.pmInfo.pm10) pm10Value = "";
	        		else pm10Value = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.pmInfo.pm10+"</strong></a>";

	        		return pm10Value;
				},
        	},
        	{
	        	targets:   7,
	        	render: function(data, type, full, meta) {
	        		var pm10Grade = "";

	        		if (!full.pmInfo.pm10_grade) pm10Grade = "";
	        		else pm10Grade = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.pmInfo.pm10_grade+"</strong></a>";

        			return pm10Grade;
				},
        	},
        	{
	        	targets:   8,
	        	render: function(data, type, full, meta) {
	        		var pm25Value = "";

	        		if (!full.pmInfo.pm25) pm25Value = "";
	        		else pm25Value = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.pmInfo.pm25+"</strong></a>";

        			return pm25Value;
				},
        	},
        	{
	        	targets:   9,
	        	render: function(data, type, full, meta) {
	        		var pm25Grade = "";

	        		if (!full.pmInfo.pm25_grade) pm25Grade = "";
	        		else pm25Grade = "<a href='#' onClick='viewData(\""+full.dcode+"\",\""+full.dfname+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.pmInfo.pm25_grade+"</strong></a>";

	        		return pm25Grade;
				},
        	}
        ],
	});

	$('.search_bottom input').unbind().bind('keyup', function () {
        var colIndex = document.querySelector('#searchType').selectedIndex + 1;

        if (colIndex == 0) {
        	table.column(0).search("").draw();
        	table.column(1).search("").draw();
        	table.column(2).search("").draw();
        	table.column(3).search("").draw();
        	table.column(4).search("").draw();
        	table.column(5).search("").draw();
        	table.column(6).search("").draw();
        	table.column(7).search("").draw();
        	table.column(8).search("").draw();
        	table.column(9).search("").draw();

        }
        else if(colIndex == 1) table.column(1).search(this.value).draw();
        else if(colIndex == 2) table.column(2).search(this.value).draw();
    });

	$("#searchType").change(function() {
		var colIndex = document.querySelector('#searchType').selectedIndex + 1;
    	table.column(0).search("").draw();
    	table.column(1).search("").draw();
    	table.column(2).search("").draw();
    	table.column(3).search("").draw();
    	table.column(4).search("").draw();
    	table.column(5).search("").draw();
    	table.column(6).search("").draw();
    	table.column(7).search("").draw();
    	table.column(8).search("").draw();
    	table.column(9).search("").draw();

    	$("#searchValue").val("");
	});

	$(".dt-buttons").css("position", "relative");
	$(".excelDownBtn").css({
		"position" : "absolute",
		"right" : "0",
		"top" : "-40px"
	});

	$("#dustTable_filter").hide();
}

function viewData(dcode,dfname,lat,lon){
	var winWidth = 400; 
	var winheight = 400; 

	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}

	newWindow = window.open('','collection_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='
			+ winWidth + ',height=' + winHeight + ',left=0,top=0');

	var target =	document.getElementById("detail_info_form");
	var act_url = "/dong/region/data/popup";

	target.target =	"collection_detail_pop";
	target.action =	act_url;
	target.dcode.value = dcode;
	target.dfname.value = dfname;
	target.lat.value = lat;
	target.lon.value = lon;
	target.submit();

	newWindow.focus();
}

$().ready(function() {
	initDataTableCustom();
	$("#dustTable_filter").hide();
})