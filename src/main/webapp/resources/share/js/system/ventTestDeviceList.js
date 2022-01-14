/**
 * 테스트 장비 관리 > VENT (TEST) 장비 
 */
var historyData;

function timeChartCreate(chartDiv, hisData, option) {
	am4core.addLicense("CH205407412");
	// am4core.useTheme(am4themes_animated);

	hisData = hisData.data.sort(function (a, b) { 
		return a.timestamp < b.timestamp ? -1 : a.timestamp > b.timestamp ? 1 : 0;  
	});

	console.log("hisData : ", hisData);

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.data = hisData;

	var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "convertTime";
	categoryAxis.renderer.opposite = false;

	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis.renderer.inversed = false;

	//valueAxis.title.text = "TITLE";
	valueAxis.renderer.minLabelPosition = 0.01;

	createSeries(option);

	function createSeries(name) {
		var series = chart.series.push(new am4charts.LineSeries());
		series.dataFields.valueY = name;
		series.dataFields.categoryX = "convertTime";
		series.name = name;

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

		chart.cursor = new am4charts.XYCursor();
		chart.cursor.behavior = "panXY";

		chart.scrollbarY = new am4core.Scrollbar();
		chart.scrollbarY.parent = chart.leftAxesContainer;
		chart.scrollbarY.toBack();

		chart.scrollbarX = new am4charts.XYChartScrollbar();
		chart.scrollbarX.series.push(series);
		chart.scrollbarX.parent = chart.topAxesContainer;
	}
}

function viewData(serialNum,productDt,standard){
	var obj = new Object();
	$('#modal-lg').modal();
	var startTime = $("#startDt").val();
	var endTime = $("#endDt").val();
	
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
	$("#p_h_productDt").val(productDt);
	
	if(!standard){
		standard = "sum";
	}
	
	obj = {
		deviceType : "vent", //사용자 계정
		serial : $("#p_h_serialNum").val(), //장비idx
		startTime : startTime, // 2020/04/23-00:00:00
		endTime : endTime,   // 2020/04/23-23:59:59
		standard : standard,
		connect : "0"
	}
	
	var h_serialNum = $("#p_h_serialNum").val();	
	var h_productDt  = $("#p_h_productDt").val();
	
	$("#txtSerialNum").text(h_serialNum);
	$("#txtProductDt").text(h_productDt);
	
	var table = $('#popventTable').DataTable({
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
		// dom: 'Blrtifp',
		dom : '<"top"Blf>rt<"bottom"ip><"clear">',
		responsive : true,
		buttons : [ {
			extend : 'csv',
			charset : 'UTF-8',
			text : '엑셀 다운로드',
			footer : false,
			bom : true,
			filename : h_serialNum + '_download',
			className : 'btn-primary btn excelDownBtn'
		} ],
        destroy: true,
        processing: true,
        serverSide: false,
		ajax : {
			 "url":"/api/collection/history",
			 "type":"GET",
			 data : obj
		},
        columns : [
    	    {data: "serial"},
        	{data: "serial"}, //0
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
	        	targets:   0, //데이터 시간
	        	orderable: true,
	        	render: function(data, type, full, meta) {
	        		//timestamp
					var date = new Date(full.timestamp*1000);

					var year = date.getFullYear();
					var month = date.getMonth()+1;
					var day = date.getDate();
					var hours = date.getHours();
					var minutes = date.getMinutes();
					var seconds = date.getSeconds();
					
					if(month <10) {	month = '0'+month; }
					if(hours <10) {	hours = '0'+hours; }
					if(minutes <10) { minutes = '0'+minutes; }
					if(seconds <10) { seconds = '0'+seconds; }
					
					var convertTime = year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
					
	        		return convertTime;
				},
        	},
        	{
	        	targets:   1, //pm10
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var auto_mode = full.auto_mode;
	        		var auto_mode_str = "";
	        		
	        		if(auto_mode == 0){
	        			auto_mode_str = "Off";
	        		}else{
	        			auto_mode_str = "On"
	        		}
	        		
	        		return auto_mode_str;
				},
        	},
        	{
	        	targets:   2, //pm10
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var power = full.power;
	        		var power_str = "";
	        		
	        		if(power == 0){
	        			power_str = "Off";
	        		}else{
	        			power_str = "On"
	        		}
	        		
	        		return power_str;
				},
        	},
        	{
	        	targets:   3, //pm25
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		return parseInt(full.air_volume);
				},
        	},
        	{
	        	targets:   4, //co2
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var exh_mode = full.exh_mode;
	        		var exh_mode_str = "";
	        		if(exh_mode == 0){
	        			exh_mode_str = "Off"
	        		}else if(exh_mode == 1){
	        			exh_mode_str = "On"
	        		}
	        		
	        		return exh_mode_str;
				},
        	},
        	{
	        	targets:   5, //VOCS
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var filter_alarm = full.filter_alarm;
	        		var filter_alarm_str = "";
	        		
	        		if(filter_alarm == 0){
	        			filter_alarm_str = "정상";
	        		}else if(filter_alarm == 1){
	        			filter_alarm_str = "점검";
	        		}else if(filter_alarm == 2){
	        			filter_alarm_str = "청소";
	        		}else{
	        			filter_alarm_str = "교환";
	        		}
	        		
	        		return filter_alarm_str;
				},
        	},
        	{
	        	targets:   6, //noise
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var air_mode = full.air_mode;
	        		var air_mode_str = "";
	        		if(air_mode == 0){
	        			air_mode_str = "Off"
	        		}else if(air_mode == 1){
	        			air_mode_str = "On"
	        		}
	        		
	        		return air_mode_str;
				},
        	},
        	{
	        	targets:   7,
	        	orderable: false,
	        	render: function(data, type, full, meta) {

	        		return full.fire_alarm;
				},
        	},
        	{
	        	targets:   8,
	        	orderable: false,
	        	render: function(data, type, full, meta) {

	        		return full.water_alarm;
				},
        	},
        	{
	        	targets:   9,
	        	orderable: false,
	        	render: function(data, type, full, meta) {

	        		return full.dev_stat;
				},
        	},
        ],
        initComplete: function (settings, data) {
        	historyData = data;
        	for(var i = 0; i < historyData.data.length; i++){
				var formatTimestamp = historyData.data[i].timestamp;
				var date = "";
        		var convertTime = "";
        		
        		if(historyData.data[i].timestamp == null){
        			convertTime = "";
        		}else{
        			date = new Date(historyData.data[i].timestamp*1000);
        			var year = date.getFullYear();
					var month = date.getMonth()+1;
					var day = date.getDate();
					
					var hours = date.getHours();
					var minutes = date.getMinutes();
					var seconds = date.getSeconds();
					
					if(month <10) { month = '0'+month; }
					if(day <10) { day = '0'+day; }
					
					if(hours <10) {	hours = '0'+hours; }
					if(minutes <10) { minutes = '0'+minutes; }
					if(seconds <10) { seconds = '0'+seconds; }
					
					convertTime =year+"-"+month+"-"+day+"\n"+hours+":"+minutes;
        		}
        		historyData.data[i].convertTime = convertTime;
			}
			timeChartCreate("timeChartDiv", historyData, $('#chartTypeSelect').val());
        }
	});
	//$('.excelDownBtn').hide();
	$("#popventTable_filter").hide();
	$("#popventTable_length").css("display","inline-block");
	$("#popventTable_length").css("margin-left","20px");
	$("#modalDownloadBtn").insertAfter("#popventTable_filter");
	$("#popventTable_wrapper .top").first().css("border-bottom", "1px solid #E5E5E5");
	$("#popventTable_wrapper .top").first().css("padding-bottom", "15px");
	$('.excelDownBtn').css("float","right");
}

function initDataTableCustom() {
	var table = $('#ventTable').DataTable({
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
			 "url":"/api/collection/test/list/vent",
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
	        		var linkHtml = "";
	        		if(full.sensor == null){
	        			linkHtml += full.serial;
	        		}else{
	        			linkHtml += "<a href='#' onClick='viewData(\""+full.serial+"\",\""+full.productDt+"\");', 'sum'>"+full.serial+"</a>";
	        		}
	        		
	        		return linkHtml;
				},
        	},
        	{
	        	targets:   2, //데이터 시간
	        	render: function(data, type, full, meta) {
	        		//timestamp
	        		var date = "";
	        		var convertTime = "";
	        		
	        		if(full.timestamp == null){
	        			convertTime = "";
	        		}else{
	        			date = new Date(full.timestamp*1000);
						var year = date.getFullYear();
						
						//내일 날짜부분 고치기
						var month = date.getMonth()+1;
						var day = date.getDate();
						var hours = date.getHours();
						var minutes = date.getMinutes();
						var seconds = date.getSeconds();
						
						if(month <10) {	month = '0'+month; }
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
	        	render: function(data, type, full, meta) {
	        		var ai_mode = full.aiMode;
	        		var ai_mode_str = "";
	        		
	        		if(ai_mode == 0){
	        			ai_mode_str = "Off";
	        		}else{
	        			ai_mode_str = "On"
	        		}
	        		
	        		return ai_mode_str;
				},
        	},
        	{
	        	targets:   4, //pm10
	        	render: function(data, type, full, meta) {
	        		//var auto_mode = full.sensor.auto_mode;
	        		var auto_mode = "";
	        		var auto_mode_str = "";
	        		
	        		if(full.sensor == null){
	        			auto_mode = "";
	        		}else{
	        			auto_mode = full.sensor.auto_mode;
	        			if(auto_mode == 0){
		        			auto_mode_str = "Off";
		        		}else{
		        			auto_mode_str = "On"
		        		}
		        		
	        		}
	        		
	        		
	        		return auto_mode_str;
				},
        	},
        	
        	{
	        	targets:   5, //pm10
	        	render: function(data, type, full, meta) {
	        		//var power = full.sensor.power;
	        		var power = "";
	        		var power_str = "";
	        		
	        		if(full.sensor == null){
	        			power = "";
	        		}else{
	        			power = full.sensor.power;
	        			if(power == 0){
		        			power_str = "Off";
		        		}else{
		        			power_str = "On"
		        		}
	        		}
	        		return power_str;
				},
        	},
        	{
	        	targets:   6, //pm25
	        	render: function(data, type, full, meta) {
	        		var air_volume = "";
	        		if(full.sensor == null || full.sensor.air_volume == null){
	        			air_volume = "-";
	        		}else{
	        			air_volume = full.sensor.air_volume;
	        		}
	        		
	        		return air_volume;
				},
        	},
        	{
	        	targets:   7, //co2
	        	render: function(data, type, full, meta) {
	        		//var exh_mode = full.sensor.exh_mode;
	        		var exh_mode = ""
	        		var exh_mode_str = "";
	        		
	        		if(full.sensor == null){
	        			exh_mode = "";
	        		}else{
	        			exh_mode = full.sensor.exh_mode;
	        			if(exh_mode == 0){
		        			exh_mode_str = "Off"
		        		}else if(exh_mode == 1){
		        			exh_mode_str = "On"
		        		}
	        		}
	        		
	        		return exh_mode_str;
				},
        	},
        	{
	        	targets:   8,
	        	render: function(data, type, full, meta) {
	        		//var filter_alarm = full.sensor.filter_alarm;
	        		var filter_alarm = "";
	        		var filter_alarm_str = "";
	        		
	        		if(full.sensor == null){
	        			filter_alarm = "";
	        		}else{
	        			filter_alarm = full.sensor.filter_alarm;
	        			if(filter_alarm == 0){
		        			filter_alarm_str = "정상";
		        		}else if(filter_alarm == 1){
		        			filter_alarm_str = "점검";
		        		}else if(filter_alarm == 2){
		        			filter_alarm_str = "청소";
		        		}else{
		        			filter_alarm_str = "교환";
		        		}
	        		}
	        		return filter_alarm_str;
				},
        	},
        	{
	        	targets:   9,
	        	render: function(data, type, full, meta) {
	        		//var air_mode = full.sensor.air_mode;
	        		var air_mode = "";
	        		var air_mode_str = "";
	        		
	        		if(full.sensor == null){
	        			air_mode = "";
	        		}else{
	        			air_mode = full.sensor.air_mode;
	        			if(air_mode == 0){
		        			air_mode_str = "Off"
		        		}else if(air_mode == 1){
		        			air_mode_str = "On"
		        		}
	        		}
	        		return air_mode_str;
				},
        	},
        	{
	        	targets:   10, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		var fire_alarm = full.sensor.fire_alarm;
	        		if (fire_alarm == null) fire_alarm = "";
	        		return fire_alarm;
				},
        	},
        	{
	        	targets:   11, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		var water_alarm = full.sensor.water_alarm;
	        		if (water_alarm == null) water_alarm = "";
	        		return water_alarm;
				},
        	},
        	{
	        	targets:   12, //데이터테이블 자체 RowNum
	        	render: function(data, type, full, meta) {
	        		var dev_stat = full.sensor.dev_stat;
	        		if (dev_stat == null) dev_stat = "";
	        		return dev_stat;
				},
        	},
        	{
	        	targets:   13, //데이터테이블 자체 RowNum
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.testYn;
				},
        	},
        	{
	        	targets:   14, //데이터테이블 자체 RowNum
	        	//searchable:false,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.userId;
				},
        	},
        	{
	        	targets:   15, //데이터테이블 자체 RowNum
	        	//searchable:false,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return full.groupId;
				},
        	},
        ],
	});
	$('.search_bottom input').unbind().bind('keyup', function () {
        var colIndex = 1;
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

	        }else if(colIndex == 1){
        		table.column(1).search(this.value).draw();
        	}else if(colIndex ==2){
        		table.column(10).search(this.value).draw();
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

        	$("#searchValue").val("");
	});
	
	$("#searchGroup").change(function() {
		table.column(13).search(this.value).draw();
	});
	
	$('#searchTestYn').click(function() {
		if($("#searchTestYn").prop("checked") == true){
			$(this).val("Y");
		}else{
			$(this).val("N");
		}
		
		if($(this).val() == "Y"){
			table.column(8).search("Y").draw();
		}else{
			table.column(8).search("").draw();
		}
	})
	
	$("#ventTable_filter").hide();
}

$().ready(function() {
	initDataTableCustom();
	$("#ventTable_filter").hide();
	
	$("#searchTestYn").click(function(){
		var chkTestYn = $("#searchTestYn");		
		if(chkTestYn.prop("checked") == true){
			$(this).val("Y");
		}else{
			$(this).val("N");
		}
	})
	
	$("#modalSearchBtn").click(function(){
		var serialNum = $("#p_h_serialNum").val();
		var productDt = $("#p_h_productDt").val();
		var standard = $("#searchStandard option:selected").val();
		
		viewData(serialNum, productDt, standard);
	})
	
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
	    }
	})
	
	$("#endDt").datepicker({
		dateFormat: 'yy/mm/dd',
		onSelect : function(selectDate){
	        setEdate = selectDate;
	        console.log(setEdate)
	    }
	})

	$('#chartTypeSelect').change(function() {
		timeChartCreate("timeChartDiv", historyData, $(this).val());
	})

	$('#modalDownloadBtn').click(function () {
		$('.excelDownBtn').trigger('click');
	})
})