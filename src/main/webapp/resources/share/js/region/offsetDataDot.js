/**
 * 수집정보 > OAQ수집정보
 */

var historyData;

function viewData2(serialNum,parentSpaceName,spaceName,productDt,stationName,standard) {
	var winWidth = 400; 
	var winheight = 400; 

	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}

	newWindow = window.open('','collection_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='
			+ winWidth + ',height=' + winHeight + ',left=0,top=0');

	var target =	document.getElementById("detail_info_form");
	var act_url = "/dong/region/offset/popup";

	target.target =	"collection_detail_pop";
	target.action =	act_url;
	target.p_h_serialNum.value = serialNum;
	target.p_h_parentSpaceName.value = parentSpaceName;
	target.p_h_spaceName.value = spaceName;
	target.p_h_productDt.value = productDt;
	target.p_h_stationName.value = stationName;
	target.standard.value = standard;
	target.deviceType.value = "DOT";

	target.submit();

	newWindow.focus();
}

function initDataTableCustom() {
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
			 "url":"/api/collection/list/dot",
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
	        		var parentSpaceName = full.parentSpaceName;

	        		if (!parentSpaceName || parentSpaceName == null || parentSpaceName == "undefined") parentSpaceName = "";

	        		var spaceName = full.spaceName;
	        		
	        		if (!spaceName || spaceName == null || spaceName == "undefined") spaceName = "";
	        		
	        		var linkHtml = "";
	        		if (full.sensor == null) linkHtml += full.serial;
	        		else linkHtml += "<a href='#' onClick='viewData2(\""
	        			+ full.serial + "\",\"" + parentSpaceName + "\",\"" + spaceName + "\",\"" + full.productDt
	        			+ "\",\"" + full.stationName + "\");', 'sum'>" + full.serial + "</a>";

        			return linkHtml;
				},
        	},
        	{
	        	targets:   2,
	        	render: function(data, type, full, meta) {
	        		var date = "";
	        		var convertTime = "";

	        		if(full.timestamp == null) convertTime = "";
	        		else {
	        			date = new Date(full.timestamp*1000);
						var year = date.getFullYear();

						var month = date.getMonth()+1;
						var day = date.getDate();
						var hours = date.getHours();
						var minutes = date.getMinutes();
						var seconds = date.getSeconds();

						if (month < 10) month = '0' + month;
						if (hours < 10) hours = '0' + hours;
						if (minutes < 10) minutes = '0' + minutes;
						if (seconds < 10) seconds = '0' + seconds;

						convertTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
	        		}

	        		return convertTime;
				},
        	},
        	{
	        	targets:   3,
	        	render: function(data, type, full, meta) {
	        		var pm10 = "";

	        		if (full.sensor == null) pm10 = "";
	        		else pm10 = full.sensor.pm10;

	        		return pm10;
				},
        	},
        	{
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		var pm10_raw = "";

	        		if (full.sensor == null) pm10_raw = "";
	        		else pm10_raw = full.sensor.pm10_raw;

	        		return pm10_raw;
				},
        	},
        	{
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		var pm10_ratio = "";

	        		if (full.sensor == null) pm10_ratio = "";
	        		else pm10_ratio = full.sensor.pm10_ratio;

	        		return pm10_ratio;
				},
        	},
        	{
	        	targets:   6,
	        	render: function(data, type, full, meta) {
	        		var pm10_offset = "";

	        		if (full.sensor == null) pm10_offset = "";
	        		else pm10_offset = full.sensor.pm10_offset;

	        		return pm10_offset;
				},
        	},
        	{
	        	targets:   7,
	        	render: function(data, type, full, meta) {
	        		var pm25 = "";

	        		if (full.sensor == null) pm25 = "";
	        		else pm25 = full.sensor.pm25;

	        		return pm25;
				},
        	},
        	{
	        	targets:   8,
	        	render: function(data, type, full, meta) {
	        		var pm25_raw = "";

	        		if (full.sensor == null) pm25_raw = "";
	        		else pm25_raw = full.sensor.pm25_raw;

	        		return pm25_raw;
				},
        	},
        	{
	        	targets:   9,
	        	render: function(data, type, full, meta) {
	        		var pm25_ratio = "";

	        		if (full.sensor == null) pm25_ratio = "";
	        		else pm25_ratio = full.sensor.pm25_ratio;

	        		return pm25_ratio;
				},
        	},
        	{
	        	targets:   10,
	        	render: function(data, type, full, meta) {
	        		var pm25_offset = "";

	        		if (full.sensor == null) pm25_offset = "";
	        		else pm25_offset = full.sensor.pm25_offset;

	        		return pm25_offset;
				},
        	},
        	{
	        	targets:   11,
	        	render: function(data, type, full, meta) {
	        		return full.stationName;
				},
        	},
        	{
	        	targets:   12,
	        	render: function(data, type, full, meta) {
	        		return full.productDt;
				},
        	},
        	{
	        	targets:   13,
	        	render: function(data, type, full, meta) {
	        		return full.groupCompanyName;
				},
        	},
        	{
	        	targets:   14,
	        	render: function(data, type, full, meta) {
	        		var etc = full.etc;

	        		if (!etc || etc == null || etc == "undefined") etc = "";

	        		return etc;
				},
        	},
        	{
	        	targets:   15,
	        	render: function(data, type, full, meta) {
	        		return "<button class='btn btn-primary' style='font-size: small; height: 36px; padding: 7px; margin: 0px; width: 75px;' onclick='offsetUpdate(\"" + full.serial + "\")'>보정 적용</button>";
				},
        	},
        	{
	        	targets:   16,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.testYn;
				},
        	},
        	{
	        	targets:   17,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.userId;
				},
        	},
        	{
	        	targets:   18,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.groupId;
				},
        	},
        	{
	        	targets:   19,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		var parentSpaceName = full.parentSpaceName;

	        		if (!parentSpaceName || parentSpaceName == null || parentSpaceName == "undefined") parentSpaceName = "";

	        		return parentSpaceName;
				},
        	},
        	{
	        	targets:   20,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		var spaceName = full.spaceName;

	        		if (!spaceName || spaceName == null || spaceName == "undefined") spaceName = "";

	        		return spaceName;
				},
        	},
        	
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
        	table.column(10).search("").draw();
        	table.column(11).search("").draw();
        	table.column(12).search("").draw();
        	table.column(13).search("").draw();
        	table.column(14).search("").draw();
        	table.column(15).search("").draw();
        	table.column(16).search("").draw();
        	table.column(17).search("").draw();
        	table.column(18).search("").draw();
        	table.column(19).search("").draw();
        	table.column(20).search("").draw();
		}

        else if (colIndex == 1) table.column(1).search(this.value).draw();
        else if (colIndex == 2) table.column(11).search(this.value).draw();
        else if (colIndex == 3) table.column(17).search(this.value).draw();
        else if (colIndex == 4) table.column(14).search(this.value).draw();
        else if (colIndex == 5) table.column(13).search(this.value).draw();
    });
	
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
    	table.column(13).search("").draw();
    	table.column(14).search("").draw();
    	table.column(15).search("").draw();
    	table.column(16).search("").draw();
    	table.column(17).search("").draw();
    	table.column(18).search("").draw();
    	table.column(19).search("").draw();
    	table.column(20).search("").draw();

    	$("#searchValue").val("");
	});

	$("#searchGroup").change(function() {
		table.column(18).search(this.value).draw();
	});

	$("#searchParentSpace").change(function() {
		var spaceVal = $(this).val();
		var spaceVal_arr = spaceVal.split("|");
		var spaceVal_idx = spaceVal_arr[0];
		var spaceVal_name = spaceVal_arr[1];
		var search_space_val = "";

		if (!spaceVal) search_space_val = "";
		else search_space_val = spaceVal_name;

		table.column(19).search(search_space_val).draw();
	});

	$("#searchSpace").change(function() {
		table.column(20).search(this.value).draw();
	});

	$('#searchTestYn').click(function() {
		if ($("#searchTestYn").prop("checked") == true) $(this).val("Y");
		else $(this).val("N");

		if ($(this).val() == "Y") table.column(15).search("Y").draw();
		else table.column(16).search("").draw();
	})

	$("#dotTable_filter").hide();
}

function offsetUpdate(serial) {
	var confirmVal = confirm("보정적용 하시겠습니까?");

	if (confirmVal) {
		$.ajax({
			method : "GET",
			url : "/api/collection/offset/update?serial=" + serial,
			success : function(res) {
				var resultCode = res.data.result;
				var serial = res.data.serial;
	
				if (resultCode == "1") {
					alert("보정 적용 되었습니다. 시리얼 번호 (" + serial + ")");
				} else if (resultCode == "0") {
					alert("입력 값을 확인 해주세요. 시리얼 번호 (" + serial + ")");
				} else {
					alert("보정 실패 하였습니다. 시리얼 번호 (" + serial + ")");
				}
			}
		});

	} else {
		alert("취소 하셨습니다.");
	}
}

function getGroupList(){
	var optHtml = "";

	$.ajax({
		method : "GET",
		url : "/system/group/get",
		contentType : "application/json; charset=utf-8",
		data : "searchUseYn=Y",
		success : function(param) {
			optHtml += "<option value=''>전체</option>";

			for (var i = 0; i < param.data.length; i++) optHtml += "<option value='"+param.data[i].groupId+"'>"+param.data[i].groupName+"</option>";

			$("#searchGroup").html(optHtml);
		}
	});
}
function getHighSpace(){
	var optHtml = "";

	$.ajax({
		method : "GET",
		url : "/system/space/high",
		contentType : "application/json; charset=utf-8",
		data : "searchUseYn=Y&deviceTypeIdx=3",
		success : function(param) {
			console.log(param);
			optHtml += "<option value=''>전체</option>";

			for (var i = 0; i < param.data.length; i++) 
				optHtml += "<option value='"
					+ param.data[i].idx + "|" + param.data[i].spaceName 
					+ "'>" + param.data[i].spaceName + "</option>";

			$("#searchParentSpace").html(optHtml);
		}
	});
}

function getSpace(val){
	var optHtml = "";
	var spaceVal = val;
	var spaceVal_arr = spaceVal.split("|");
	var spaceVal_idx = spaceVal_arr[0];
	var spaceVal_name = spaceVal_arr[1];

	$("#searchSpace").html("");

	if(!val) $("#searchSpace").html("<option value=''>전체</option>");
	else {
		$.ajax({
			 url:"/system/member/device/ajax/spaces",
			 type:"GET",
			 async : false,
			 data : "parentSpaceIdx="+spaceVal_idx,
			 success : function (param) {
				console.log(param);
				optHtml += "<option value=''>전체</option>";

				for (var i = 0; i < param.data.length; i++) {
					var idx = param.data[i].idx;
					var spaceName = param.data[i].spaceName;
					optHtml += "<option value='" + param.data[i].spaceName + "'>" + spaceName + "</option>";
				}

				$("#searchSpace").html(optHtml);
			 }
		})
	}
}

$().ready(function() {
	initDataTableCustom();
	getGroupList();
	getHighSpace();

	$("#dotTable_filter").hide();

	$("#searchTestYn").click(function() {
		var chkTestYn = $("#searchTestYn");

		if (chkTestYn.prop("checked") == true) $(this).val("Y");
		else $(this).val("N");
	})
})