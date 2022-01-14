/**
 * 관제정보 > IAQ
 */
function getTimestampToDate(timestamp){
    var date = new Date(timestamp*1000);
    var chgTimestamp = date.getFullYear().toString()
        +addZero(date.getMonth()+1)
        +addZero(date.getDate().toString())
        +addZero(date.getHours().toString())
        +addZero(date.getMinutes().toString())
        +addZero(date.getSeconds().toString());
    return chgTimestamp;
}
 
function addZero(data){
    return (data<10) ? "0"+data : data;
}


function initDataTableCustom(){
	var table = $('#dotTable').DataTable({
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
			 "url":"/api/control/list/dot",
			 "type":"GET",
			 data : function (param) {
				 //param.useYn = searchUseYn;
				 //param.userId = searchValue;
             }
		},
        columns : [
        	{data: "serial"}, //0
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
	        	targets:   0, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		return meta.row + meta.settings._iDisplayStart + 1;
				},
        	},
        	{
	        	targets:   1, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + full.serial + "</strong>";
				},
        	},
        	{
	        	targets:   2, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null) return "-";
	        		var getTimeStamp = getTimestampToDate(full.control.servertime);
	        		var gts_year = getTimeStamp.substring(0,4);
	        		var gts_month = getTimeStamp.substring(4,6);
	        		var gts_day = getTimeStamp.substring(6,8);
	        		var gts_hour = getTimeStamp.substring(8,10);
	        		var gts_minute = getTimeStamp.substring(10,12);
	        		var gts_second = getTimeStamp.substring(12,14);
	        		
	        		var servertime = gts_year+"-"+gts_month+"-"+gts_day+" "+gts_hour+":"+gts_minute+":"+gts_second;
	        		
	        		return "<strong>" + servertime + "</strong>";
				},
        	},
        	{
	        	targets:   3, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + full.stationName + "</strong>";
				},
        	},
        	{
	        	targets:   4, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null) return "-";
	        		return "<strong>" + full.control.firmversion + "</strong>";
				},
        	},
        	{
	        	targets:   5, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null) return "-";
	        		var year = full.control.starttime.substring(0,4);
	        		var month = full.control.starttime.substring(4,6);
	        		var day = full.control.starttime.substring(6,8);
	        		var hour = full.control.starttime.substring(8,10);
	        		var minute = full.control.starttime.substring(10,12);
	        		
	        		var starttime = year+"-"+month+"-"+day+" "+hour+":"+minute;
	        		
	        		return "<strong>" + starttime + "</strong>";
	        		
				},
        	},
        	{
	        	targets:   6, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null || full.control.imei == null) return "-";
	        		return "<strong>" + full.control.imei + "</strong>";
				},
        	},
        	{
	        	targets:   7, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null || full.control.ctn == null) return "-";
	        		return "<strong>" + full.control.ctn + "</strong>";
				},
        	},
        	{
	        	targets:   8, //데이터테이블 자체 RowNum
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.testYn;
				},
        	},
        	{
	        	targets:   9, //데이터테이블 자체 RowNum
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.userId;
				},
        	},
        ],
	});
	
	$('.search_bottom input').unbind().bind('keyup', function () {
        var colIndex = document.querySelector('#searchType').selectedIndex;
        if(colIndex == 0){ 
        	table.column(colIndex + 1).search(this.value).draw();
        }else if(colIndex == 2){
        	table.column(colIndex + 7).search(this.value).draw();
        }else{
        	table.column(colIndex + 2).search(this.value).draw();
        }
    });
	
	$('#searchTestYn').click(function() {
		if($("#searchTestYn").prop("checked") == true) {
			$(this).val("Y");
		}else{
			$(this).val("N");
		}
		
		if($(this).val() == "Y"){
			table.column(8).search("Y").draw();
		}else{
			table.column(8).search("N").draw();
		}
	})

	$("#dotTable_filter").hide();
}

$().ready(function() {
	initDataTableCustom();
	$("#dotTable_filter").hide();
})