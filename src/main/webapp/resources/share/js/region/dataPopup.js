var historyData;

$().ready(function() {
	$("#startDt").val("");
	$("#endDt").val("");

	if (deviceType == "IAQ") $("#lastOptVal").val("cici");
	else $("#lastOptVal").val("coci");

	if(!$("#startDt").val()){
		var now_date = new Date();
		var now_year = now_date.getFullYear();
		var now_month = now_date.getMonth()+1;
		var now_day = now_date.getDate();

		if (now_month < 10) now_month = "0"+now_month;
		if (now_day < 10) now_day = "0"+now_day;

		$("#startDt").val(now_year+"/"+now_month+"/"+now_day);
	}

	if (!$("#endDt").val()) {
		var now_date = new Date();
		var now_year = now_date.getFullYear();
		var now_month = now_date.getMonth()+1;
		var now_day = now_date.getDate();

		if (now_month < 10) now_month = "0" + now_month;
		if (now_day < 10) now_day = "0" + now_day;

		$("#endDt").val(now_year+"/"+now_month+"/"+now_day);
	}

	viewData(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType);

	$("#modalSearchBtn").click(function() {
		var serialNum = $("#p_h_serialNum").val();
		var parentSpaceName = $("#p_h_parentSpaceName").val();
		var spaceName = $("#p_h_spaceName").val();
		var productDt = $("#p_h_productDt").val();
		var stationName = $("#p_h_stationName").val();
		var standard = $("#searchStandard option:selected").val();
		
		viewData(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType);
	})

	var rangeDate = 31;
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

    timeChartCreate("timeChartDiv", historyData, chkArr);
}

function viewData(serialNum, parentSpaceName, spaceName, productDt, stationName, standard, deviceType){
	var obj = new Object();
	var startTime = $("#startDt").val();
	var endTime = $("#endDt").val();
	var r_deviceType = deviceType;

	if(!startTime && !endTime) {
		var today = new Date();   
		var year = today.getFullYear();
		var month = today.getMonth() + 1;
		var day = today.getDate();

		if (month < 10) month = "0" + month;
		if (day < 10) day = "0" + day;

		startTime = year+"/"+month+"/"+day+"-00:00:00";
		endTime = year+"/"+month+"/"+day+"-23:59:59";

	} else {

		startTime = startTime+"-00:00:00";
		endTime =  endTime+"-23:59:59";
	} 

	$("#p_h_serialNum").val(serialNum);
	$("#p_h_parentSpaceName").val(parentSpaceName);
	$("#p_h_spaceName").val(spaceName);
	$("#p_h_productDt").val(productDt);
	$("#p_h_stationName").val(stationName);

	if (!standard) standard = "sum";

	if (r_deviceType == "IAQ") r_deviceType = "iaq";
	else if (r_deviceType == "OAQ") r_deviceType = "oaq";
	else if (r_deviceType == "DOT") r_deviceType = "dot";

	obj = {
		deviceType : r_deviceType,
		serial : $("#p_h_serialNum").val(),
		startTime : startTime,
		endTime : endTime,
		standard : standard,
		connect : "0"
	}

	var h_serialNum = $("#p_h_serialNum").val();	
	var h_parentSpaceName = $("#p_h_parentSpaceName").val();
	var h_spaceName  = $("#p_h_spaceName").val();
	var h_productDt  = $("#p_h_productDt").val();
	var h_stationName  = $("#p_h_stationName").val();
	var spaceStr = "";

	if (!h_parentSpaceName && !h_spaceName) spaceStr = "없음";
	else if (h_parentSpaceName && !h_spaceName) spaceStr = h_parentSpaceName;
	else if (!h_parentSpaceName && h_spaceName) spaceStr = h_spaceName;

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
		ajax : {
			"url":"/api/collection/history",
			"type":"GET",
			data : obj,
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
            {data: "serial"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
					var date = new Date(full.timestamp*1000);

					var year = date.getFullYear();
					var month = date.getMonth()+1;
					var day = date.getDate();
					var hours = date.getHours();
					var minutes = date.getMinutes();
					var seconds = date.getSeconds();

					if (month < 10) month = '0' + month;
					if (day < 10) day = '0' + day;
					if (hours < 10) hours = '0' + hours;
					if (minutes < 10) minutes = '0' + minutes;
					if (seconds < 10) seconds = '0' + seconds;

					var convertTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;

					return convertTime;
				},
        	},
        	{
	        	targets:   1,
	        	render: function(data, type, full, meta) {
					var pm10 = full.pm10;

					if (!pm10) pm10 = "";

					return pm10;
				},
        	},
        	{
	        	targets:   2,
	        	render: function(data, type, full, meta) {
					var pm10_raw = full.pm10_raw;

					if (!pm10_raw) pm10_raw = "";

	        		return pm10_raw;
				},
        	},
        	{
	        	targets:   3,
	        	render: function(data, type, full, meta) {
					var pm10_ratio = full.pm10_ratio;

					if (!pm10_ratio) pm10_ratio = "";

	        		return pm10_ratio;
				},
        	},
        	{
	        	targets:   4,
	        	render: function(data, type, full, meta) {
					var pm10_offset = full.pm10_offset;

					if (!pm10_offset) pm10_offset = "";

	        		return pm10_offset;
				},
        	},
        	{
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		var pm25 = full.pm25;

	        		if (!pm25) pm25 = "";

	        		return pm25;
				},
        	},
        	{
	        	targets:   6,
	        	render: function(data, type, full, meta) {
	        		var pm25_raw = full.pm25_raw;

	        		if (!pm25_raw) pm25_raw = "";

        			return pm25_raw;
				},
        	},
        	{
	        	targets:   7,
	        	render: function(data, type, full, meta) {
	        		var pm25_ratio = full.pm25_ratio;
	        		
	        		if (!pm25_ratio) pm25_ratio = "";

	        		return pm25_ratio;
				},
        	},
        	{
	        	targets:   8,
	        	render: function(data, type, full, meta) {
	        		var pm25_offset = full.pm25_offset;

	        		if (!pm25_offset) pm25_offset = "";

	        		return pm25_offset;
				},
        	},
        ],
        initComplete: function (settings, data) {
        	historyData = data;
        	valueArr = new Array();
			for (var i = 0; i < historyData.data.length; i++) {
				var formatTimestamp = historyData.data[i].timestamp;
				var date = "";
        		var convertTime = "";

        		if (historyData.data[i].timestamp == null) {
        			convertTime = "";
        		} else {
        			date = new Date(historyData.data[i].timestamp * 1000);
        			var year = date.getFullYear();
					var month = date.getMonth() + 1;
					var day = date.getDate();

					var hours = date.getHours();
					var minutes = date.getMinutes();
					var seconds = date.getSeconds();

					if (month < 10) month = '0'+month;
					if (day < 10) day = '0'+day;

					if (hours < 10) hours = '0'+hours;
					if (minutes < 10) minutes = '0'+minutes;
					if (seconds < 10) seconds = '0'+seconds;

					convertTime = year + "-" + month + "-" + day + "\n" + hours + ":" + minutes;
        		}

        		historyData.data[i].convertTime = convertTime;

        		if ($('#chartTypeSelect').val() == "pm10") valueArr[i] = historyData.data[i].pm10;
        		else if ($('#chartTypeSelect').val() == "pm25") valueArr[i] = historyData.data[i].pm25;
        		else if ($('#chartTypeSelect').val() == "co2") valueArr[i] = historyData.data[i].co2;
        		else if ($('#chartTypeSelect').val() == "voc") valueArr[i] = historyData.data[i].voc;
        		else if ($('#chartTypeSelect').val() == "noise") valueArr[i] = historyData.data[i].noise;
        		else if ($('#chartTypeSelect').val() == "temp") valueArr[i] = historyData.data[i].temp;
        		else if ($('#chartTypeSelect').val() == "humi") valueArr[i] = historyData.data[i].humi;
        		else if ($('#chartTypeSelect').val() == "cici") valueArr[i] = historyData.data[i].cici;
        		else if ($('#chartTypeSelect').val() == "coci") valueArr[i] = historyData.data[i].coci;
			}

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

	hisData = hisData.data.sort(function (a, b) { 
		return a.timestamp < b.timestamp ? -1 : a.timestamp > b.timestamp ? 1 : 0;  
	});

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.data = hisData;
	chart.cursor = new am4charts.XYCursor();
	chart.cursor.behavior = "panXY";
	chart.scrollbarY = new am4core.Scrollbar();
	chart.scrollbarY.parent = chart.leftAxesContainer;
	chart.scrollbarY.toBack();
	chart.scrollbarX = new am4charts.XYChartScrollbar();
	chart.scrollbarX.parent = chart.topAxesContainer;

	var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "convertTime";
	categoryAxis.renderer.opposite = false;

	for (var idx in options) createSeries(options[idx]);

	function chgOptionNameFunc(name) {
		var chgName = "";

		if (name == "pm10") chgName = "미세먼지 (㎍/㎥)";
		else if (name == "pm25") chgName = "초미세먼지 (㎍/㎥)";
		else if (name == "pm10_raw") chgName = "미세먼지 원본 (㎍/㎥)";
		else if (name == "pm25_raw") chgName = "초미세먼지 원본 (㎍/㎥)";
		else if (name == "pm10_ratio") chgName = "초미세먼지 (보정비율)";
		else if (name == "pm25_ratio") chgName = "초미세먼지 (보정비율)";
		else if (name == "pm10_offset") chgName = "미세먼지 (보정옵셋)";
		else if (name == "pm25_offset") chgName = "초미세먼지 (보정옵셋)";
		else chgName = name;

		return chgName;
	}

	function createSeries(name) {
		var chgName = chgOptionNameFunc(name);

		var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
		valueAxis.renderer.inversed = false;
		valueAxis.title.text = chgName;
		valueAxis.renderer.minLabelPosition = 0.01;
		valueAxis.renderer.opposite = true;

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