/**
 * 테스트 장비 관리 > OAQ (TEST) 장비 
 */

function viewData2(serialNum,parentSpaceName,spaceName,productDt,stationName,standard){
	var winWidth = 400; 
	var winheight = 400; 
	
	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}
	
	newWindow = window.open('','collection_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='+winWidth+',height='+winHeight+',left=0,top=0');
	
	var target =	document.getElementById("detail_info_form");
	var act_url = "/system/device/oaq_popup";
	
	target.target =	"collection_detail_pop";
	target.action =	act_url;
	target.p_h_serialNum.value = serialNum;
	target.p_h_parentSpaceName.value = parentSpaceName;
	target.p_h_spaceName.value = spaceName;
	target.p_h_productDt.value = productDt;
	target.p_h_stationName.value = stationName;
	target.standard.value = standard;
	target.deviceType.value = "OAQ";
	
	target.submit();

	newWindow.focus();
	
}


function initDataTableCustom(){
	var table = $('#oaqTable').DataTable({
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
			 "url":"/api/collection/test/list/oaq",
			 "type":"GET",
			 data : function (param) {}
		},
        columns : [
        	{orderable: false },
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
	        	orderable: true,
	        	render: function(data, type, full, meta) {
	        		return meta.row + meta.settings._iDisplayStart + 1;
				},
        	},
        	{
	        	targets:   1, //데이터테이블 자체 RowNum
	        	orderable: true,
	        	render: function(data, type, full, meta) {
	        		var parentSpaceName = full.parentSpaceName;
	        		
	        		if(!parentSpaceName || parentSpaceName == null || parentSpaceName == "undefined"){
	        			parentSpaceName = "";
	        		}

	        		var spaceName = full.spaceName;

	        		if(!spaceName || spaceName == null || spaceName == "undefined"){
	        			spaceName = "";
	        		}
	        		
	        		var linkHtml = "";
	        		if(full.sensor == null){
	        			linkHtml += full.serial;
	        		}else{
	        			linkHtml += "<a href='#' onClick='viewData2(\""+full.serial+"\",\""+parentSpaceName+"\",\""+spaceName+"\",\""+full.productDt+"\",\""+full.stationName+"\");', 'sum'>"+full.serial+"</a>";
	        		}
	        		return linkHtml;
				},
        	},
        	{
	        	targets:   2, //데이터 시간
	        	orderable: true,
	        	render: function(data, type, full, meta) {
	        		//timestamp
	        		var date = "";
	        		var convertTime = "";

	        		if(full.service.timestamp == null){
	        			convertTime = "";
	        		}else{
	        			date = new Date(full.service.timestamp*1000);
						var year = date.getFullYear();
						
						//내일 날짜부분 고치기
						var month = date.getMonth()+1;
						var day = date.getDate();
						var hours = date.getHours();
						var minutes = date.getMinutes();
						var seconds = date.getSeconds();
						
						if(month <10) {	month = '0'+month; }
						if(day <10) {	day = '0'+day; }
						
						if(hours <10) {	hours = '0'+hours; }
						if(minutes <10) { minutes = '0'+minutes; }
						if(seconds <10) { seconds = '0'+seconds; }
						
						convertTime = year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
	        		}
	        		return convertTime;
				},
        	},
        	{
	        	targets:   3, //pm10
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var pm10 = "";
	        		if(full.sensor == null || full.sensor.pm10 == null){
	        			pm10 = "-";
	        			fontColor = "";
	        		}else{
	        			pm10 = full.sensor.pm10;
	        			fontColor = txtColor(full.sensor.coci_pm10, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+pm10+"</font>";
				},
        	},
        	{
	        	targets:   4, //pm25
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var pm25 = "";
	        		if(full.sensor == null || full.sensor.pm25 == null){
	        			pm25 = "-";
	        			fontColor = "";
	        		}else{
	        			pm25 = full.sensor.pm25;
	        			fontColor = txtColor(full.sensor.coci_pm25, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+pm25+"</font>";
				},
        	},
        	{
	        	targets:   5, //co2
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var co2 = "";
	        		if(full.sensor == null || full.sensor.co2 == null){
	        			co2 = "-";
	        			fontColor = "";
	        		}else{
	        			co2 = full.sensor.co2;
	        			fontColor = txtCO2Color(full.sensor.co2, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+co2+"</font>";
				},
        	},
        	{
	        	targets:   6, //VOCS
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var voc = "";
	        		if(full.sensor == null || full.sensor.voc == null){
	        			voc = "-";
	        			fontColor = "";
	        		}else{
	        			voc = full.sensor.voc;
	        			fontColor = txtVOCColor(full.sensor.voc, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+voc+"</font>";
				},
        	},
        	{
	        	targets:   7, //noise
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var noise = "";
	        		if(full.sensor == null || full.sensor.noise == null){
	        			noise = "-";
	        			fontColor = "";
	        		}else{
	        			noise = full.sensor.noise;
	        			fontColor = txtNOISEColor(full.sensor.noise, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+noise+"</font>";
				},
        	},
        	{
	        	targets:   8, //temp
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var temp = "";
	        		if(full.sensor == null || full.sensor.temp == null){
	        			temp = "-";
	        			fontColor = "";
	        		}else{
	        			temp = full.sensor.temp;
	        			fontColor = txtColor(full.sensor.coci_temp, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+temp+"</font>";
				},
        	},
        	{
	        	targets:   9, //humi
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var humi = "";
	        		if(full.sensor == null || full.sensor.humi == null){
	        			humi = "-";
	        			fontColor = "";
	        		}else{
	        			humi = full.sensor.humi;
	        			fontColor = txtColor(full.sensor.coci_humi, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+humi+"</font>";
				},
        	},
        	{
	        	targets:   10, //cici
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var coci = "";
	        		if(full.sensor == null || full.sensor.coci == null){
	        			coci = "-";
	        			fontColor = "";
	        		}else{
	        			coci = full.sensor.coci;
	        			fontColor = txtColor(full.sensor.coci, true);
	        		}
	        		return "<font style='font-weight:bold; font-size:13px; color:"+fontColor+"'>"+coci+"</font>";
				},
        	}
        ],
	});
	
	$('.search_bottom input').unbind().bind('keyup', function () {
        var colIndex = 1;
        
        //초기화
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
        	table.column(13).search("").draw();
        	table.column(14).search("").draw();
        	table.column(15).search("").draw();
        	table.column(16).search("").draw();
        	table.column(17).search("").draw();
        	
		}else if(colIndex == 1){ 
        	table.column(1).search(this.value).draw();
			//table.column(1).fnFilter("^"+$(this).val(),0,true,false);
        	
        }else if(colIndex == 2){
        	table.column(11).search(this.value).draw();
        }else if(colIndex == 3){
        	table.column(14).search(this.value).draw();
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
        	table.column(13).search("").draw();
        	table.column(14).search("").draw();
        	table.column(15).search("").draw();
        	table.column(16).search("").draw();
        	table.column(17).search("").draw();

        	$("#searchValue").val("");
	});
	
	$("#searchGroup").change(function() {
		table.column(15).search(this.value).draw();
	});
	
	$("#searchParentSpace").change(function() {
		var spaceVal = $(this).val();
		var spaceVal_arr = spaceVal.split("|");
		var spaceVal_idx = spaceVal_arr[0];
		var spaceVal_name = spaceVal_arr[1];
		var search_space_val = "";
		
		if(!spaceVal){
			search_space_val = "";
		}else{
			search_space_val = spaceVal_name;
		}
		
		table.column(16).search(search_space_val).draw();
	});
	
	//하위 카테고리 
	$("#searchSpace").change(function() {
		table.column(17).search(this.value).draw();
	});
	
	$('#searchTestYn').click(function() {
		if($("#searchTestYn").prop("checked") == true){
			$(this).val("Y");
			//table.column(8).search(this.value).draw();
		}else{
			$(this).val("N");
		}
		
		if($(this).val() == "Y"){
			table.column(13).search("Y").draw();
		}else{
			table.column(13).search("").draw();
		}
	})
	
	//table.column(13).search("N").draw();
	$("#oaqTable_filter").hide();
}

$().ready(function() {

	initDataTableCustom();
	
	$("#oaqTable_filter").hide();
	
	$("#searchTestYn").click(function(){
		var chkTestYn = $("#searchTestYn");		
		if(chkTestYn.prop("checked") == true){
			$(this).val("Y");
		}else{
			$(this).val("N");
		}
	})
	
	
})