/**
 * 대시보드 > 수신/미수신 조회
 */
var t_iaqData;
var t_oaqData;
var t_dotData;
var t_ventData;

var cntDataList;
var cntFilterDataList;

function pieChartCreate(chartDiv, data) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_kelly);
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.PieChart);

	var datas = data;

	chart.data = generateChartData();

	var pieSeries = chart.series.push(new am4charts.PieSeries());
	pieSeries.dataFields.value = "value";
	pieSeries.dataFields.category = "type";
	pieSeries.labels.template.disabled = true;

	function generateChartData() {
		var chartData = [];
		for (var i = 0; i < datas.length; i++) {
			chartData.push({
				type : datas[i].type,
				value : datas[i].value,
				color : datas[i].color
			});
		}

		return chartData;
	}
}

function barChartCreate(chartDiv, data) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.colors.step = 2;

	chart.numberFormatter.numberFormat = "#.#'%'";
	chart.data = data;

	var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "device";
	categoryAxis.renderer.grid.template.location = 0;
	categoryAxis.renderer.minGridDistance = 30;

	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis.title.text = "수신 / 미수신";
	valueAxis.title.fontWeight = 800;
	valueAxis.min = 0;
	valueAxis.max = 100;

	var series = chart.series.push(new am4charts.ColumnSeries());
	series.dataFields.valueY = "receive";
	series.dataFields.categoryX = "device";
	series.clustered = false;
	series.tooltipText = "{categoryX} (수신): [bold]{valueY}[/]";

	var series2 = chart.series.push(new am4charts.ColumnSeries());
	series2.dataFields.valueY = "unreceive";
	series2.dataFields.categoryX = "device";
	series2.clustered = false;
	series2.columns.template.width = am4core.percent(50);
	series2.tooltipText = "{categoryX} (미수신): [bold]{valueY}[/]";

	chart.cursor = new am4charts.XYCursor();
	chart.cursor.lineX.disabled = true;
	chart.cursor.lineY.disabled = true;
}

function timeChartCreate(chartDiv, type, iaqData, oaqData, dotData, ventData) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	var DataArr = [iaqData, oaqData, dotData, ventData];

	var dateAxis = chart.xAxes.push(new am4charts.DateAxis());
	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

	for (var i = 0; i < 4; i++) createSeries("value" + i, type[i], DataArr[i]);

	function createSeries(s, name, typeData) {
		var r_data = typeData.data.sort(function (a, b) { 
			return a.statDate < b.statDate ? -1 : a.statDate > b.statDate ? 1 : 0;  
		});

		var series = chart.series.push(new am4charts.LineSeries());
		series.dataFields.valueY = "value" + s;
		series.dataFields.dateX = "date";
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

		segment.events.on("over", function(event) {
			processOver(event.target.parent.parent.parent);
		});

		segment.events.on("out", function(event) {
			processOut(event.target.parent.parent.parent);
		});

		var data = []; 
		var value = [];
		var s_today = new Date();   
		var year = s_today.getFullYear();
		var month = s_today.getMonth()+1;
		var day = s_today.getDate();
		var hour = s_today.getHours();
		var hour_cnt = 0;
		var startDay = parseInt(day - 7);
		var dataItem;

		var lastWeek = new Date();
		lastWeek.setDate(lastWeek.getDate() - 7);

		var lastWeek_year = lastWeek.getFullYear();
		var lastWeek_month = lastWeek.getMonth()+1;
		var lastWeek_day = lastWeek.getDate();

		if (month < 10) month = "0"+month;

		if (lastWeek_month < 10) lastWeek_month = "0"+lastWeek_month;
		
		if (lastWeek_day < 10) lastWeek_day = "0"+lastWeek_day;

		var lastWeek_format = lastWeek_year + "-" + lastWeek_month + "-" + lastWeek_day;

		for (var j = 0; j < r_data.length; j++) {
			var statDate = r_data[j].statDate
			var s_year = statDate.substring(0,4);
			var s_month = statDate.substring(4,6);
			var s_day = statDate.substring(6,8);
			var s_hour = statDate.substring(8,10);
			var deviceCntOk  = r_data[j].deviceCntOk;
			var deviceType = r_data[j].deviceType;

			var s_date = s_year+"-"+s_month+"-"+s_day;

			if(s_date >= lastWeek_format){
				var dataItem = {
					date : new Date(s_year, s_month-1, s_day, s_hour)
				};

				dataItem["value" + s] = deviceCntOk;
				data.push(dataItem);
			}
		}

		series.data = data;

		return series;
	}

	chart.legend = new am4charts.Legend();
	chart.legend.position = "right";
	chart.legend.scrollable = true;
	chart.legend.itemContainers.template.events.on("over", function(event) {
		processOver(event.target.dataItem.dataContext);
	})

	chart.legend.itemContainers.template.events.on("out", function(event) {
		processOut(event.target.dataItem.dataContext);
	})

	function processOver(hoveredSeries) {
		hoveredSeries.toFront();

		hoveredSeries.segments.each(function(segment) {
			segment.setState("hover");
		})

		chart.series.each(function(series) {
			if (series != hoveredSeries) {
				series.segments.each(function(segment) {
					segment.setState("dimmed");
				})
				series.bulletsContainer.setState("dimmed");
			}
		});
	}

	function processOut(hoveredSeries) {
		chart.series.each(function(series) {
			series.segments.each(function(segment) {
				segment.setState("default");
			})
			series.bulletsContainer.setState("default");
		});
	}
}

function getDataList(){
	var selectType = $("#sch_order").val();
	var timeChartdata = ["IAQ", "OAQ", "DOT", "VENT"];

	$.ajax({
		 url:"/api/dashboard/receive",
		 type:"GET",
		 data : "statType=" + selectType + "&deviceType=ALL",
		 async : false,
		 success : function (param) {
			var r_data = param.data.sort(function (a, b) { 
				return a.statDate > b.statDate ? -1 : a.statDate < b.statDate ? 1 : 0;  
			});
		 }
	})

	$.ajax({
		 url:"/api/dashboard/receive",
		 type:"GET",
		 data : "statType=" + selectType + "&deviceType=IAQ",
		 async : false,
		 success : function (param) {
			var r_data = param.data.sort(function (a, b) { 
				return a.statDate > b.statDate ? -1 : a.statDate < b.statDate ? 1 : 0;  
			});

			t_iaqData = param;
		 }
	})

	$.ajax({
		 url:"/api/dashboard/receive",
		 type:"GET",
		 data : "statType=" + selectType + "&deviceType=OAQ",
		 async : false,
		 success : function (param) {
			var r_data = param.data.sort(function (a, b) { 
				return a.statDate > b.statDate ? -1 : a.statDate < b.statDate ? 1 : 0;  
			});
			
			t_oaqData = param;
		 }
	})

	$.ajax({
		 url:"/api/dashboard/receive",
		 type:"GET",
		 data : "statType=" + selectType + "&deviceType=DOT",
		 async : false,
		 success : function (param) {
			var r_data = param.data.sort(function (a, b) { 
				return a.statDate > b.statDate ? -1 : a.statDate < b.statDate ? 1 : 0;  
			});

			t_dotData = param;
		 }
	})

	$.ajax({
		 url:"/api/dashboard/receive",
		 type:"GET",
		 data : "statType=" + selectType + "&deviceType=VENT",
		 async : false,
		 success : function (param) {
			var r_data = param.data.sort(function (a, b) { 
				return a.statDate > b.statDate ? -1 : a.statDate < b.statDate ? 1 : 0;  
			});

			t_ventData = param;
		 }
	})

	timeChartCreate("timeChartdiv", timeChartdata, t_iaqData, t_oaqData, t_dotData, t_ventData);
}

function CalcData() {
	var h_total_cnt = parseInt($("#h_total_cnt").val());
	var h_total_ok_cnt = parseInt($("#h_total_ok_cnt").val());
	var h_total_nok_cnt = parseInt($("#h_total_nok_cnt").val());

	var h_iaq_cnt = parseInt($("#h_iaq_cnt").val());
	var h_iaq_ok_cnt = parseInt($("#h_iaq_ok_cnt").val());
	var h_iaq_nok_cnt = parseInt($("#h_iaq_nok_cnt").val());

	var h_oaq_cnt = parseInt($("#h_oaq_cnt").val());
	var h_oaq_ok_cnt = parseInt($("#h_oaq_ok_cnt").val());
	var h_oaq_nok_cnt = parseInt($("#h_oaq_nok_cnt").val());

	var h_dot_cnt = parseInt($("#h_dot_cnt").val());
	var h_dot_ok_cnt = parseInt($("#h_dot_ok_cnt").val());
	var h_dot_nok_cnt = parseInt($("#h_dot_nok_cnt").val());

	var h_vent_cnt = parseInt($("#h_vent_cnt").val());
	var h_vent_ok_cnt = parseInt($("#h_vent_ok_cnt").val());
	var h_vent_nok_cnt = parseInt($("#h_vent_nok_cnt").val());

	var iaq_per_result = (h_iaq_cnt / h_total_cnt * 100).toFixed(1);
	var oaq_per_result = (h_oaq_cnt / h_total_cnt * 100).toFixed(1);
	var dot_per_result = (h_dot_cnt / h_total_cnt * 100).toFixed(1);
	var vent_per_result = (h_vent_cnt / h_total_cnt * 100).toFixed(1);

	var total_ok_per_result =  (h_total_ok_cnt / h_total_cnt * 100).toFixed(1) ; //total 수신
	var total_nok_per_result =  (h_total_nok_cnt / h_total_cnt * 100).toFixed(1) ; //total 미수신
	
	var iaq_ok_per_result =  (h_iaq_ok_cnt / h_iaq_cnt * 100).toFixed(1) ; //iaq 수신
	var iaq_nok_per_result =  (h_iaq_nok_cnt / h_iaq_cnt * 100).toFixed(1) ; //iaq 미수신

	var oaq_ok_per_result =  (h_oaq_ok_cnt / h_oaq_cnt * 100).toFixed(1) ; //oaq 수신
	var oaq_nok_per_result =  (h_oaq_nok_cnt / h_oaq_cnt * 100).toFixed(1) ; //oaq 미수신

	var dot_ok_per_result =  (h_dot_ok_cnt / h_dot_cnt * 100).toFixed(1) ; //dot 수신
	var dot_nok_per_result =  (h_dot_nok_cnt / h_dot_cnt * 100).toFixed(1) ; //dot 미수신

	var vent_ok_per_result =  (h_vent_ok_cnt / h_vent_cnt * 100).toFixed(1) ; //vent 수신
	var vent_nok_per_result =  (h_vent_nok_cnt / h_vent_cnt * 100).toFixed(1) ; //vent 미수신

	var total_pieChartData = [{	type : "수신", value : total_ok_per_result }, { type : "미수신", value : total_nok_per_result	}]
	var iaq_pieChartData = [{	type : "수신", value : iaq_ok_per_result }, { type : "미수신", value : iaq_nok_per_result	}]
	var oaq_pieChartData = [{	type : "수신", value : oaq_ok_per_result }, { type : "미수신", value : oaq_nok_per_result	}]
	var dot_pieChartData = [{	type : "수신", value : dot_ok_per_result }, { type : "미수신", value : dot_nok_per_result	}]
	var vent_pieChartData = [{	type : "수신", value : vent_ok_per_result }, { type : "미수신", value : vent_nok_per_result	}]

	var barChartData = [ 
		{
			"device" : "전체",
			"receive" : total_ok_per_result,
			"unreceive" : total_nok_per_result
		}, {
			"device" : "IAQ",
			"receive" : iaq_ok_per_result,
			"unreceive" : iaq_nok_per_result
		}, {
			"device" : "OAQ",
			"receive" : oaq_ok_per_result,
			"unreceive" : oaq_nok_per_result
		}, {
			"device" : "DOT",
			"receive" : dot_ok_per_result,
			"unreceive" : dot_nok_per_result
		}, {
			"device" : "VENT",
			"receive" : vent_ok_per_result,
			"unreceive" : vent_nok_per_result
		}
	];

	pieChartCreate("pieChart", total_pieChartData);
	pieChartCreate("pieChart2", iaq_pieChartData);
	pieChartCreate("pieChart3", oaq_pieChartData);
	pieChartCreate("pieChart4", dot_pieChartData);
	pieChartCreate("pieChart5", vent_pieChartData);
	barChartCreate("barChart", barChartData)

	$("#total_cnt").text(NoneData(h_total_cnt)+"("+NoneData(total_ok_per_result)+"%)");
	$("#iaq_cnt").text(NoneData(h_iaq_cnt)+"("+NoneData(iaq_ok_per_result)+"%)");
	$("#oaq_cnt").text(NoneData(h_oaq_cnt)+"("+NoneData(oaq_ok_per_result)+"%)");
	$("#dot_cnt").text(NoneData(h_dot_cnt)+"("+NoneData(dot_ok_per_result)+"%)");
	$("#vent_cnt").text(NoneData(h_vent_cnt)+"("+NoneData(vent_ok_per_result)+"%)");

	$("#h_total_ok_per_result").val(total_ok_per_result);
	$("#h_iaq_ok_per_result").val(iaq_ok_per_result);
	$("#h_oaq_ok_per_result").val(oaq_ok_per_result);
	$("#h_dot_ok_per_result").val(dot_ok_per_result);
	$("#h_vent_ok_per_result").val(vent_ok_per_result);
}

function getReceiveCnt() {
	$.ajax({
		url:"/api/dashboard/receive/cnt/m",
		type:"GET",
		async : false,
		success : function (data) {
			$('#sch_order').show();
			cntDataList = data.data.all;
			cntFilterDataList = data.data.user;

			drawReceiveCnt(cntFilterDataList);
		 }
	})
}

function drawReceiveCnt(data) {
	var h_total_cnt = parseInt(data.all.allCnt);
	var h_total_ok_cnt = parseInt(data.all.receiveCnt);
	var h_total_nok_cnt = parseInt(data.all.unReceiveCnt);

	var h_iaq_cnt = parseInt(data.iaq.allCnt);
	var h_iaq_ok_cnt = parseInt(data.iaq.receiveCnt);
	var h_iaq_nok_cnt = parseInt(data.iaq.unReceiveCnt);

	var h_oaq_cnt = parseInt(data.oaq.allCnt);
	var h_oaq_ok_cnt = parseInt(data.oaq.receiveCnt);
	var h_oaq_nok_cnt = parseInt(data.oaq.unReceiveCnt);

	var h_dot_cnt = parseInt(data.dot.allCnt);
	var h_dot_ok_cnt = parseInt(data.dot.receiveCnt);
	var h_dot_nok_cnt = parseInt(data.dot.unReceiveCnt);

	var h_vent_cnt = parseInt(data.vent.allCnt);
	var h_vent_ok_cnt = parseInt(data.vent.receiveCnt);
	var h_vent_nok_cnt = parseInt(data.vent.unReceiveCnt);

	var iaq_per_result = (h_iaq_cnt / h_total_cnt * 100).toFixed(1);
	var oaq_per_result = (h_oaq_cnt / h_total_cnt * 100).toFixed(1);
	var dot_per_result = (h_dot_cnt / h_total_cnt * 100).toFixed(1);
	var vent_per_result = (h_vent_cnt / h_total_cnt * 100).toFixed(1);

	var total_ok_per_result =  (h_total_ok_cnt / h_total_cnt * 100).toFixed(1) ; //total 수신
	var total_nok_per_result =  (h_total_nok_cnt / h_total_cnt * 100).toFixed(1) ; //total 미수신

	var iaq_ok_per_result =  (h_iaq_ok_cnt / h_iaq_cnt * 100).toFixed(1) ; //iaq 수신
	var iaq_nok_per_result =  (h_iaq_nok_cnt / h_iaq_cnt * 100).toFixed(1) ; //iaq 미수신

	var oaq_ok_per_result =  (h_oaq_ok_cnt / h_oaq_cnt * 100).toFixed(1) ; //oaq 수신
	var oaq_nok_per_result =  (h_oaq_nok_cnt / h_oaq_cnt * 100).toFixed(1) ; //oaq 미수신

	var dot_ok_per_result =  (h_dot_ok_cnt / h_dot_cnt * 100).toFixed(1) ; //dot 수신
	var dot_nok_per_result =  (h_dot_nok_cnt / h_dot_cnt * 100).toFixed(1) ; //dot 미수신

	var vent_ok_per_result =  (h_vent_ok_cnt / h_vent_cnt * 100).toFixed(1) ; //vent 수신
	var vent_nok_per_result =  (h_vent_nok_cnt / h_vent_cnt * 100).toFixed(1) ; //vent 미수신

	$("#h_total_ok_per_result").val(total_ok_per_result);
	$("#h_iaq_ok_per_result").val(iaq_ok_per_result);
	$("#h_oaq_ok_per_result").val(oaq_ok_per_result);
	$("#h_dot_ok_per_result").val(dot_ok_per_result);
	$("#h_vent_ok_per_result").val(vent_ok_per_result);

	var total_per = $("#h_total_ok_per_result").val();
	var iaq_per = $("#h_iaq_ok_per_result").val();
	var oaq_per = $("#h_oaq_ok_per_result").val();
	var dot_per = $("#h_dot_ok_per_result").val();
	var vent_per = $("#h_vent_ok_per_result").val();

	$("#total_cnt").text(data.all.allCnt+"("+total_per+"%)");
	$("#h_total_cnt").val(data.all.allCnt);
	$("#total_ok").text("수신 : " + data.all.receiveCnt);
	$("#h_total_ok_cnt").val(data.all.receiveCnt);
	$("#total_nok").text("미수신 : " + data.all.unReceiveCnt);
	$("#h_total_nok_cnt").val(data.all.unReceiveCnt);

	$("#iaq_cnt").text(data.iaq.allCnt+"("+iaq_per+"%)");
	$("#h_iaq_cnt").val(data.iaq.allCnt);
	$("#iaq_ok").text("수신 : " + data.iaq.receiveCnt);
	$("#h_iaq_ok_cnt").val(data.iaq.receiveCnt);
	$("#iaq_nok").text("미수신 : " + data.iaq.unReceiveCnt);
	$("#h_iaq_nok_cnt").val(data.iaq.unReceiveCnt);

	$("#oaq_cnt").text(data.oaq.allCnt+"("+oaq_per+"%)");
	$("#h_oaq_cnt").val(data.oaq.allCnt);
	$("#oaq_ok").text("수신 : " + data.oaq.receiveCnt);
	$("#h_oaq_ok_cnt").val(data.oaq.receiveCnt);
	$("#oaq_nok").text("미수신 : " + data.oaq.unReceiveCnt);
	$("#h_oaq_nok_cnt").val(data.oaq.unReceiveCnt);

	$("#dot_cnt").text(data.dot.allCnt+"("+dot_per+"%)");
	$("#h_dot_cnt").val(data.dot.allCnt);
	$("#dot_ok").text("수신 : " + data.dot.receiveCnt);
	$("#h_dot_ok_cnt").val(data.dot.receiveCnt);
	$("#dot_nok").text("미수신 : " + data.dot.unReceiveCnt);
	$("#h_dot_nok_cnt").val(data.dot.unReceiveCnt);

	$("#vent_cnt").text(data.vent.allCnt+"("+vent_per+"%)");
	$("#h_vent_cnt").val(data.vent.allCnt);
	$("#vent_ok").text("수신 : " + data.vent.receiveCnt);
	$("#h_vent_ok_cnt").val(data.vent.receiveCnt);
	$("#vent_nok").text("미수신 : " + data.vent.unReceiveCnt);
	$("#h_vent_nok_cnt").val(data.vent.unReceiveCnt);

	var total_pieChartData = [{	type : "수신", value : total_ok_per_result }, { type : "미수신", value : total_nok_per_result	}]
	var iaq_pieChartData = [{	type : "수신", value : iaq_ok_per_result }, { type : "미수신", value : iaq_nok_per_result	}]
	var oaq_pieChartData = [{	type : "수신", value : oaq_ok_per_result }, { type : "미수신", value : oaq_nok_per_result	}]
	var dot_pieChartData = [{	type : "수신", value : dot_ok_per_result }, { type : "미수신", value : dot_nok_per_result	}]
	var vent_pieChartData = [{	type : "수신", value : vent_ok_per_result }, { type : "미수신", value : vent_nok_per_result	}]

	var barChartData = [ 
		{
			"device" : "전체",
			"receive" : total_ok_per_result,
			"unreceive" : total_nok_per_result
		}, {
			"device" : "IAQ",
			"receive" : iaq_ok_per_result,
			"unreceive" : iaq_nok_per_result
		}, {
			"device" : "OAQ",
			"receive" : oaq_ok_per_result,
			"unreceive" : oaq_nok_per_result
		}, {
			"device" : "DOT",
			"receive" : dot_ok_per_result,
			"unreceive" : dot_nok_per_result
		}, {
			"device" : "VENT",
			"receive" : vent_ok_per_result,
			"unreceive" : vent_nok_per_result
		}
	];

	pieChartCreate("pieChart", total_pieChartData);
	pieChartCreate("pieChart2", iaq_pieChartData);
	pieChartCreate("pieChart3", oaq_pieChartData);
	pieChartCreate("pieChart4", dot_pieChartData);
	pieChartCreate("pieChart5", vent_pieChartData);
	barChartCreate("barChart", barChartData)
}

function NoneData(val) {
	if (isNaN(val)) return 0;
	else return val;
}

$().ready(function() {
	getDataList();
	getReceiveCnt();
	CalcData();

	$('#sch_order').change(function() {
		if ($(this).val() == "ALL") drawReceiveCnt(cntDataList);
		else drawReceiveCnt(cntFilterDataList);
	});
})