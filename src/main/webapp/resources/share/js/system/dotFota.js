/**
 * 관제정보 > dot
 */
var obj = new Object();

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
	        		return full.serial;
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
	        		
	        		return servertime;
				},
        	},
        	{
	        	targets:   3, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		return full.stationName;
				},
        	},
        	{
	        	targets:   4, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null) return "-";
	        		return full.control.firmversion;
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
	        		
	        		return starttime;
	        		
				},
        	},
        	{
	        	targets:   6, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null) return "-";
	        		return full.control.imei;
				},
        	},
        	{
	        	targets:   7, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		if (full.control == null) return "-";
	        		return full.control.ctn;
				},
        	},
        	{
	        	targets:   8, //데이터테이블 자체 RowNum
	        	//searchable:false,
	        	render: function(data, type, full, meta) {
	        		return "<input type='button' class='btn btn-primary' onCLick='openResetPop(\""+full.serial+"\");' style='min-width:80px;' value='리 셋'>";
				},
        	},
        	{
	        	targets:   9, //데이터테이블 자체 RowNum
	        	//searchable:false,
	        	render: function(data, type, full, meta) {
	        		return "<input type='checkbox' style='width:20px;height:20px;' name='chkSerialnum' value='"+full.serial+"'/>";
				},
        	},
        	{
	        	targets:   10, //데이터테이블 자체 RowNum
	        	//searchable:false,
	        	render: function(data, type, full, meta) {
	        		return "<input type='button' class='btn btn-primary UpgradeBtn' style='min-width:10px;' value='업그레이드' onClick='openModal();'>";
				},
        	},
        	{
	        	targets:   11, //데이터테이블 자체 RowNum
	        	//searchable:false,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.testYn;
				},
        	},
        	{
	        	targets:   12, //데이터테이블 자체 RowNum
	        	//searchable:false,
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
        	table.column(10).search("").draw();
        	table.column(11).search("").draw();
        	table.column(12).search("").draw();
        	
        }else if(colIndex == 1){ 
        	table.column(1).search(this.value).draw();
        }else if(colIndex == 2){
        	table.column(4).search(this.value).draw();
        }else if(colIndex == 3){
        	table.column(3).search(this.value).draw();
        }
        
        
    });
	
	//옵션바꿀때마다 초기화
	$("#searchType").change(function() {
		 var colIndex = document.querySelector('#searchType').selectedIndex;
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
        	table.column(10).search("").draw();
        	table.column(11).search("").draw();
        	table.column(12).search("").draw();
        	
		
		$("#searchValue").val("");
	});
	
	$('#searchTestYn').click(function() {
		if($("#searchTestYn").prop("checked") == true){
			$(this).val("Y");
			//table.column(8).search(this.value).draw();
		}else{
			$(this).val("N");
		}
		
		if($(this).val() == "Y"){
			table.column(11).search("Y").draw();
		}else{
			table.column(11).search("").draw();
		}
	})
	//table.column(8).search("N").draw();
	$("#dotTable_filter").hide();
}

function openModal(){
	var checkArr = new Array();
	var boardCnt = $("input[name='chkSerialnum']:checked").length;
	var serialHtml = "";
	var serialCnt = "";
	
	checkArr = [];
	serialHtml = "";
	$("#serialList").html("");
	$("#device_cnt").text("");
	
	if(boardCnt == 0){
		alert("최소 1개 이상 삭제 가능합니다. 선택해주세요.");
		return false;
	}
	
	$("input[name='chkSerialnum']:checked").each(function() {
		checkArr.push($(this).val());
		serialHtml += "<input type='hidden' name='h_p_serial' value='"+$(this).val()+"'/><span>"+$(this).val()+"</span><br/>";
	});
	
	
	
	
	$('#modal-lg').modal();	
	
	$("#serialList").html(serialHtml);
	serialCnt = $("#serialList span").length;
	$("#device_cnt").text("대상 디바이스 : "+serialCnt+"대");
	
}
function upgradeFirmware(){
	var firmwareVal = $("#firmwareList").val();
	var chkSerial = $("input[name='h_p_serial']").length;
	var checkArr = new Array();
	var Serialdata= new FormData();
	
	
	
	$("input[name='h_p_serial']").each(function() {
		Serialdata.append ('serial', $(this).val());
	})
	
	if(!firmwareVal){
		alert("펌웨어 버전을 선택해 주세요.");
		return false;
	}else{
		Serialdata.append ('firmVersion', firmwareVal);
	}
	
	/*for (var key of Serialdata.keys()) {
	  console.log(key);
	}
	for (var value of Serialdata.values()) {
	  console.log(value);
	}
	return false;*/
	
	$.ajax({
		method : "POST",
		url : "/api/platform/fota/u",
		cache: false,
	    processData: false,
	    contentType: false,
		data : Serialdata,
		success : function(d) {
			if(d == 1){
				alert("업그레이드 되었습니다.");
				location.reload();
			}else{
				alert("실패");
			}
		}
	});
	
}

function openResetPop(val){
	$('#modal-lg2').modal();
	$("#txtResetSerial").text("");
	$("#resetSerialNum").val("");
	
	$("#txtResetSerial").text(val);
	$("#resetSerialNum").val(val);
}

function goReset(){
	var serial = $("#resetSerialNum").val();
	$.ajax({
		method : "POST",
		url : "/api/platform/fota/r",
		data : "serial="+serial,
		success : function(d) {
			if(d == 1){
				alert("리셋 되었습니다.");
				location.reload();
			}else{
				alert("실패");
			}
		}
	});
	
}

$().ready(function() {
	initDataTableCustom();
	$("#dotTable_filter").hide();
	
	/*$('.UpgradeBtn').click(function() {
		alert();
		openModal();
	})*/
	
	$("#firmWareUpgradeBtn").click(function() {
		upgradeFirmware();
	})
	
	
})