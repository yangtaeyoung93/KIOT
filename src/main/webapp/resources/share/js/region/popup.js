let historyData;
$().ready(function() {
	$("#startDt").val("");
	$("#endDt").val("");

	if(!$("#startDt").val()){
		const now_date = new Date();
		const now_year = now_date.getFullYear();
		let now_month = now_date.getMonth()+1;
		let now_day = now_date.getDate();

		if (now_month < 10) now_month = "0" + now_month;
		if (now_day < 10) now_day = "0" + now_day;

		$("#startDt").val(now_year + "/" + now_month + "/" + now_day);
	}

	if(!$("#endDt").val()){
		const now_date = new Date();
		const now_year = now_date.getFullYear();
		let now_month = now_date.getMonth() + 1;
		let now_day = now_date.getDate();

		if (now_month < 10) now_month = "0" + now_month;
		if (now_day < 10) now_day = "0" + now_day;

		$("#endDt").val(now_year+"/"+now_month+"/"+now_day);
	}

	const rangeDate = 31; 
	let setSdate, setEdate;
	
	$("#startDt").datepicker({
		dateFormat: 'yy/mm/dd',
	    onSelect: function(selectDate){
	        const stxt = selectDate.split("/");
            stxt[1] = stxt[1] - 1;
	        const sdate = new Date(stxt[0], stxt[1], stxt[2]);
	        const edate = new Date(stxt[0], stxt[1], stxt[2]);
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

	viewData(dcode,dfname,lat,lon);

	$("#modalSearchBtn").click(function() {
		const dcode = $("#p_h_dcode").val();
		const dfname = $("#p_h_dfname").val();
		const lat = $("#p_h_lat").val();
		const lon = $("#p_h_lon").val();

		viewData(dcode,dfname,lat,lon);
	})

	$('#chartTypeSelect').change(function() {
		timeChartCreate("timeChartDiv", historyData, $(this).val());
	})

	$('#modalDownloadBtn').click(function () {
		$('.excelDownBtn').trigger('click');
	})
})

function viewData(dcode,dfname,lat,lon) {
	let obj = new Object();
	let startTime = $("#startDt").val();
	let endTime = $("#endDt").val();
	
	if (!startTime && !endTime) {
		let today = new Date();
		const year = today.getFullYear();
		let month = today.getMonth() + 1;
		let day = today.getDate();

		if (month < 10) month = "0"+month;
		if (day < 10) day = "0"+day;

		startTime = year + "/" + month + "/" + day + "-00:00:00";
		endTime = year + "/" + month + "/" + day + "-23:59:59";

	} else {
		startTime = startTime+"-00:00:00";
		endTime =  endTime+"-23:59:59";
	} 

	$("#p_h_dcode").val(dcode);
	$("#p_h_dfname").val(dfname);
	$("#p_h_lat").val(lat);
	$("#p_h_lon").val(lon);

	$("#txtDcode").text(dcode);
	$("#txtDfname").text(dfname);
	$("#txtLat").text(lat);
	$("#txtLon").text(lon);

	obj = {
		dcode : $("#p_h_dcode").val(),
		startTime : startTime,
		endTime : endTime,
		type : "kWeather"
	}

	const table = $('#popTable').DataTable({
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
			filename : $("#p_h_dfname").val() +"_"+$("#startDt").val()+"_"+$("#endDt").val()+"_download",
			className : 'btn-primary btn excelDownBtn'
		} ],
		
		destroy: true,
        processing: true,
        serverSide: false,
		ajax : {
			"url":"/api/dong/data/detail",
			"type":"GET",
			data : obj,
		},
        columns : [
        	{data: "serial"},
        	{data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"},
            {data: "serial"}
        ],
        columnDefs: [ 
        	{
	        	targets:  0,
	        	render: function(data, type, full, meta) {
	        		return meta.row + meta.settings._iDisplayStart + 1;
				},
        	},
        	{
	        	targets:  1,
	        	render: function(data, type, full, meta) {
	        		const regDate = full.regDate;

	        		const year = regDate.substring(0,4);
	        		const month = regDate.substring(4,6);
	        		const day = regDate.substring(6,8);
	        		const hour = regDate.substring(8,10);
	        		const minute = regDate.substring(10,12);
	        		const second = regDate.substring(12,14);
	        		const convertTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

	        		return convertTime;
				},
        	},
        	{
	        	targets:  2,
	        	render: function(data, type, full, meta) {
	        		let pm10 = full.pm10;

	        		if (pm10) {
	        			pm10 = parseInt(full.pm10);
	        			pm10 = pm10.toFixed(0);
	        		} else pm10 = "";

	        		return pm10;
				},
        	},
        	{
	        	targets:  3,
	        	render: function(data, type, full, meta) {
	        		let pm10Grade = full.pm10_grade;

	        		if (pm10Grade) {
	        			pm10Grade = parseInt(full.pm10_grade);
	        			pm10Grade = pm10Grade.toFixed(0);
	        		} else pm10Grade = "";

	        		return pm10Grade;
				},
        	},
        	{
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		let pm25 = full.pm25;

	        		if (pm25) {
	        			pm25 = parseInt(full.pm25);
	        			pm25 = pm25.toFixed(0);
	        		} else pm25 = "";

	        		return pm25;
				},
        	},
        	{
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		let pm25Grade = full.pm25_grade;

	        		if(pm25Grade){
	        			pm25Grade = parseInt(full.pm25_grade);
	        			pm25Grade = pm25Grade.toFixed(0);
	        		} else pm25Grade = "";

	        		return pm25Grade;
				},
        	}
        ],
        initComplete: function (settings, data) {
        	historyData = data;
        	valueArr = new Array();

        	for (let i = 0; i < historyData.data.length; i++) {
				const regDate = historyData.data[i].regDate;
				let convertTime = "";

				const year = regDate.substring(0,4);
        		const month = regDate.substring(4,6);
        		const day = regDate.substring(6,8);
        		const hour = regDate.substring(8,10);
        		const minute = regDate.substring(10,12);
        		const second = regDate.substring(12,14);
        		convertTime = year + "-" + month + "-" + day + "\n" + hour + ":" + minute + ":" + second;

        		historyData.data[i].convertTime = convertTime;
			}

        	timeChartCreate("timeChartDiv", historyData, $('#chartTypeSelect').val());
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

function timeChartCreate(chartDiv, hisData, option) {
	let titleTxt = "";

	// if (option == "pm10Val") titleTxt = "㎍/㎥";
	// else if (option == "pm10Grade") titleTxt = "";
	// else if (option == "pm25Val") titleTxt = "㎍/㎥";
	// else if (option == "voc") titleTxt = "";

	am4core.addLicense("CH205407412");
	// am4core.useTheme(am4themes_animated);

	if (hisData) {
		hisData = hisData.data.sort(function (a, b) {
			return a.regDate < b.regDate ? -1 : a.regDate > b.regDate ? 1 : 0;
		});
	} else {
		alert("차트 표출중 에러가 발생했습니다.");
	}

	const chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.data = hisData;

	const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "convertTime";
	categoryAxis.renderer.opposite = false;

	const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis.renderer.inversed = false;

	valueAxis.title.text = titleTxt;
	valueAxis.renderer.minLabelPosition = 0.01;

	let options = ["pm10", "pm25", "pm10_grade", "pm25_grade"];
	let optionsName = {"pm10" : "미세먼지", "pm25" : "초미세먼지", "pm10_grade" : "미세먼지 등급", "pm25_grade" : "초미세먼지 등급"};
	// let options_grade = ["pm10_grade", "pm25_grade"];
	// let options_gradeName = ["미세먼지 등급", "초미세먼지 등급"];

	// if (option == "options") {
	options.forEach((data) => {
		createSeries(data);
	});
	// } else {
	// 	options_grade.forEach((data, index) => {
	// 		createSeries(data, options_gradeName[index]);
	// 	});
	// }

	chart.cursor = new am4charts.XYCursor();
	chart.cursor.behavior = "panXY";

	chart.scrollbarY = new am4core.Scrollbar();
	chart.scrollbarY.parent = chart.leftAxesContainer;
	chart.scrollbarY.toBack();

	function createSeries(option) {
		const series = chart.series.push(new am4charts.LineSeries());
		series.dataFields.valueY = option
		series.dataFields.categoryX = "convertTime";
		series.name = optionsName[option];
		const segment = series.segments.template;
		segment.interactionsEnabled = true;

		const hoverState = segment.states.create("hover");
		hoverState.properties.strokeWidth = 3;

		const dimmed = segment.states.create("dimmed");
		dimmed.properties.stroke = am4core.color("#dadada");

		series.tooltipText = "{name}: [bold]{valueY}[/]";
		series.tooltip.background.cornerRadius = 20;
		series.tooltip.background.strokeOpacity = 0;
		series.tooltip.pointerOrientation = "vertical";
		series.tooltip.label.minWidth = 40;
		series.tooltip.label.minHeight = 40;
		series.tooltip.label.textAlign = "middle";
		series.tooltip.label.textValign = "middle";

		chart.scrollbarX = new am4charts.XYChartScrollbar();
		chart.scrollbarX.series.push(series);
		chart.scrollbarX.parent = chart.topAxesContainer;
	}
}