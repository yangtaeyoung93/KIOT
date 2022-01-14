/**
 * 에어코리아 관측 미세먼지 데이터
 */

function initDataTableCustom(){
	const table = $('#airKorTable').DataTable({
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
        	filename : '에어코리아_데이터_다운로드',
        	className : 'btn-primary btn excelDownBtn'
        } ],
		ajax : {
			 "url":"/api/dong/list/airkor",
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
	        		let aAirCode = "";

	        		if (!full.aairCode) aAirCode = "";
	        		else aAirCode = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.aairCode+"</strong></a>";

	        		return aAirCode;
				},
        	},
        	{
	        	targets:   2,
	        	render: function(data, type, full, meta) {
	        		let aAirName = "";

	        		if (!full.aairName) aAirName = "";
	        		else aAirName = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.aairName+"</strong></a>";

	        		return aAirName;
				},
        	},
        	{
	        	targets:   3,
	        	render: function(data, type, full, meta) {
	        		let aAirCity = "";

	        		if (!full.aairCity) aAirCity = "";
	        		else aAirCity = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.aairCity+"</strong></a>";

	        		return aAirCity;
				},
        	},
        	{
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		let dCode = "";

	        		if (!full.dcode) dCode = "";
	        		else dCode = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.dcode+"</strong></a>";

	        		return dCode;
				},
        	},
        	{
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		let aAirAddr = "";

	        		if (!full.aairCity) aAirAddr = "";
	        		else aAirAddr = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.aairAddr+"</strong></a>";

	        		return aAirAddr;
				},
        	},
        	{
	        	targets:   6,
	        	render: function(data, type, full, meta) {
	        		let lat = "";

	        		if (!full.lat) lat = "";
	        		else lat = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.lat+"</strong></a>";

        			return lat;
				},
        	},
        	{
	        	targets:   7,
	        	render: function(data, type, full, meta) {
	        		let lon = "";

	        		if (!full.lon) lon = "";
	        		else lon = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.lon+"</strong></a>";

	        		return lon;
				},
        	},
        	{
	        	targets:   8,
	        	render: function(data, type, full, meta) {
	        		let regDate = "";

	        		if(!full.timeStamp) regDate = "";
	        		else {
	        			const dateTime = new Date(full.timeStamp * 1000);
	        			const year = dateTime.getFullYear();
	        			const month = dateTime.getMonth() + 1 < 10? '0' + (dateTime.getMonth() + 1) : (dateTime.getMonth() + 1);
	        			const day = dateTime.getDate() < 10? '0' + dateTime.getDate() : dateTime.getDate() ;
	        			const hour = dateTime.getHours() < 10? '0' + dateTime.getHours() : dateTime.getHours();
	        			const minute = dateTime.getMinutes() < 10? '0' + dateTime.getMinutes() : dateTime.getMinutes();
		        		const second = dateTime.getSeconds() < 10? '0' + dateTime.getSeconds() : dateTime.getSeconds();
		        		const convertTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

		        		regDate = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+convertTime+"</strong></a>";
	        		}

	        		return regDate;
				},
        	},
        	{
	        	targets:   9,
	        	render: function(data, type, full, meta) {
	        		let pm10Value = "";

	        		if (!full.sensor) pm10Value = "";
	        		else pm10Value = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.sensor.pm10+"</strong></a>";

	        		return pm10Value;
				},
        	},
        	{
	        	targets:   10,
	        	render: function(data, type, full, meta) {
	        		let pm10Grade = "";

	        		if (!full.sensor) pm10Grade = "";
	        		else pm10Grade = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.sensor.pm10_grade+"</strong></a>";

        			return pm10Grade;
				},
        	},
        	{
	        	targets:   11,
	        	render: function(data, type, full, meta) {
	        		let pm25Value = "";

	        		if (!full.sensor) pm25Value = "";
	        		else pm25Value = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.sensor.pm25+"</strong></a>";

        			return pm25Value;
				},
        	},
        	{
	        	targets:   12,
	        	render: function(data, type, full, meta) {
	        		let pm25Grade = "";

	        		if (!full.sensor) pm25Grade = "";
	        		else pm25Grade = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.sensor.pm25_grade+"</strong></a>";

	        		return pm25Grade;
				},
        	},
        	{
	        	targets:   13,
	        	render: function(data, type, full, meta) {
	        		let useYn = "";

	        		if (!full.useYn) useYn = "";
	        		else useYn = "<a href='#' onClick='viewData(\""+full.aairCode+"\",\""+full.aairName+"\",\""+full.lat+"\",\""+full.lon+"\")'><strong>"+full.useYn+"</strong></a>";

	        		return useYn;
				},
        	}
        ],
	});

	$('.search_bottom input').unbind().bind('keyup', function () {
        const colIndex = document.querySelector('#searchType').selectedIndex + 1;

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
        else if(colIndex == 3) table.column(3).search(this.value).draw();
        else if(colIndex == 4) table.column(4).search(this.value).draw();
        else if(colIndex == 5) table.column(5).search(this.value).draw();
    });

	$("#searchType").change(function() {
		const colIndex = document.querySelector('#searchType').selectedIndex + 1;
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

	$("#airKorTable_filter").hide();
}

function viewData(aAirCode, aAirName, lat, lon){
	let winWidth = 400;
	let winHeight = 400;

	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}

	let newWindow = window.open('','collection_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='
			+ winWidth + ',height=' + winHeight + ',left=0,top=0');

	const target =	document.getElementById("detail_info_form");
	const act_url = "/dong/region/airkor/popup";

	target.target =	"collection_detail_pop";
	target.action =	act_url;
	target.aAirCode.value = aAirCode;
	target.aAirName.value = aAirName;
	target.lat.value = lat;
	target.lon.value = lon;
	target.submit();

	newWindow.focus();
}

$().ready(function() {
	initDataTableCustom();
	$("#airKorTable_filter").hide();
})