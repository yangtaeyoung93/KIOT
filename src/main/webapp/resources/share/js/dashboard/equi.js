/**
 * 대시보드 > 장비 현황
 */

function barChartCreate(chartDiv, data) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.colors.step = 2;

	chart.numberFormatter.numberFormat = "#.#'대'";
	chart.data = data;

	var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "device";
	categoryAxis.renderer.grid.template.location = 0;
	categoryAxis.renderer.minGridDistance = 30;

	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis.title.text = "등록 / 미등록";
	valueAxis.title.fontWeight = 800;

	var series = chart.series.push(new am4charts.ColumnSeries());
	series.dataFields.valueY = "receive";
	series.dataFields.categoryX = "device";
	series.clustered = false;
	series.tooltipText = "{categoryX} (등록): [bold]{valueY}[/]";

	var series2 = chart.series.push(new am4charts.ColumnSeries());
	series2.dataFields.valueY = "unreceive";
	series2.dataFields.categoryX = "device";
	series2.clustered = false;
	series2.columns.template.width = am4core.percent(50);
	series2.tooltipText = "{categoryX} (미등록): [bold]{valueY}[/]";

	chart.cursor = new am4charts.XYCursor();
	chart.cursor.lineX.disabled = true;
	chart.cursor.lineY.disabled = true;
}

function timeChartCreate(chartDiv, data, hisData) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);

	var dateAxis = chart.xAxes.push(new am4charts.DateAxis());
	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

	var dataArr = "";
	for (var i = 0; i < 4; i++) {
		if (data[i] == "VENT") dataArr = hisData.ventConnect;
		else dataArr = hisData.deviceConnect;

		createSeries("value" + i, data[i], dataArr);
	}

	function createSeries(s, name, dataArr) {
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

		segment.events.on("over", function(event) {
			processOver(event.target.parent.parent.parent);
		});

		segment.events.on("out", function(event) {
			processOut(event.target.parent.parent.parent);
		});

		var data = [];
		var s_today = new Date();   
		var year = s_today.getFullYear();
		var month_cnt = 0;

		for (var i = 0; i < 12; i++) {
			month_cnt++;

			if (month_cnt < 10) month_cnt = "0"+month_cnt;

			var dataItem = {
				date : new Date(year, month_cnt, 0)
			};

			var Cnt_total = 0;
			if (name == "VENT") {
				for (var j = 0; j < dataArr.length; j++) {
					var connectDate = dataArr[j].connectDate
					var s_year = connectDate.substring(0,4);
					var s_month = connectDate.substring(4,6);
					var s_day = connectDate.substring(6,8);
					var DeviceCnt = dataArr[j].ventDeviceCnt

					if (month_cnt == s_month) Cnt_total += DeviceCnt;

					dataItem["value" + s] = Cnt_total;
				}

			} else {
				for (var j = 0; j < dataArr.length; j++) {
					var connectDate = dataArr[j].connectDate
					var s_year = connectDate.substring(0,4);
					var s_month = connectDate.substring(4,6);
					var s_day = connectDate.substring(6,8);

					if (name == "IAQ") var DeviceCnt = dataArr[j].iaqDeviceCnt;
					else if(name == "OAQ") var DeviceCnt = dataArr[j].oaqDeviceCnt;
					else if(name == "DOT") var DeviceCnt = dataArr[j].dotDeviceCnt;

					if(month_cnt == s_month) Cnt_total += DeviceCnt;

					dataItem["value" + s] = Cnt_total;
				}
			}

			data.push(dataItem);
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

function getDeviceData() {
	$.ajax({
		 url:"/api/dashboard/device",
		 type:"GET",
		 async : false,
		 success : function (param) {
			 var conCnt = parseInt(param.data.cnt.conCnt); //사용자
			 var conIaqCnt = parseInt(param.data.cnt.conIaqCnt); //사용자연동 iaq 
			 var conOaqCnt = parseInt(param.data.cnt.conOaqCnt); //사용자연동 oaq 
			 var conDotCnt = parseInt(param.data.cnt.conDotCnt); //사용자연동 dot 
			 var conVentCnt = parseInt(param.data.cnt.conVentCnt); //사용자연동 vent 

			 var allCnt =  parseInt(param.data.cnt.allCnt); //재고 total
			 var allIaqCnt = parseInt(param.data.cnt.allIaqCnt); //재고 iaq 
			 var allOaqCnt = parseInt(param.data.cnt.allOaqCnt); //재고 iaq 
			 var allDotCnt = parseInt(param.data.cnt.allDotCnt); //재고 iaq 
			 var allVentCnt = parseInt(param.data.cnt.allVentCnt); //재고 

			 var allNotCnt = allCnt - conCnt;
			 var NotIaqCnt = allIaqCnt - conIaqCnt;
			 var NotOaqCnt = allOaqCnt - conOaqCnt;
			 var NotDotCnt = allDotCnt - conDotCnt;
			 var NotVentCnt = allVentCnt - conVentCnt;

			 var total_use_per = (conCnt / allCnt * 100).toFixed(1);
			 var total_not_per = (allNotCnt / allCnt * 100).toFixed(1);
			 var total_use_iaq_per = (conIaqCnt / allIaqCnt * 100).toFixed(1);
			 var total_not_iaq_per = (NotIaqCnt / allIaqCnt * 100).toFixed(1);
			 var total_use_oaq_per = (conOaqCnt / allOaqCnt * 100).toFixed(1);
			 var total_not_oaq_per = (NotOaqCnt / allOaqCnt * 100).toFixed(1);
			 var total_use_dot_per = (conDotCnt / allDotCnt * 100).toFixed(1);
			 var total_not_dot_per = (NotDotCnt / allDotCnt * 100).toFixed(1);
			 var total_use_vent_per = (conVentCnt / allVentCnt * 100).toFixed(1);
			 var total_not_vent_per = (NotVentCnt / allVentCnt * 100).toFixed(1);

			 var total_device_cnt = $("#total_device_cnt").text(allCnt+" 대");
			 var total_con_device_cnt = $("#total_con_device_cnt").text("등록 장비 : "+conCnt+" 대("+total_use_per+"%)");
			 var total_not_device_cnt = $("#total_not_device_cnt").text("미등록 장비 : "+allNotCnt+" 대("+total_not_per+"%)");

			 var total_iaq_cnt = $("#total_iaq_cnt").text(allIaqCnt+" 대");
			 var total_con_iaq_cnt = $("#total_con_iaq_cnt").text("등록 장비 : "+conIaqCnt+" 대("+total_use_iaq_per+"%)");
			 var total_not_iaq_cnt = $("#total_not_iaq_cnt").text("미등록 장비 : "+NotIaqCnt+" 대("+total_not_iaq_per+"%)");

			 var total_oaq_cnt = $("#total_oaq_cnt").text(allOaqCnt+" 대");
			 var total_con_oaq_cnt = $("#total_con_oaq_cnt").text("등록 장비 : "+conOaqCnt+" 대("+total_use_oaq_per+"%)");
			 var total_not_oaq_cnt = $("#total_not_oaq_cnt").text("미등록 장비 : "+NotOaqCnt+" 대("+total_not_oaq_per+"%)");

			 var total_dot_cnt = $("#total_dot_cnt").text(allDotCnt+" 대");
			 var total_con_dot_cnt = $("#total_con_dot_cnt").text("등록 장비 : "+conDotCnt+" 대("+total_use_dot_per+"%)");
			 var total_not_dot_cnt = $("#total_not_dot_cnt").text("미등록 장비 : "+NotDotCnt+" 대("+total_not_dot_per+"%)");

			 var total_vent_cnt = $("#total_vent_cnt").text(allVentCnt+" 대");
			 var total_con_vent_cnt = $("#total_con_vent_cnt").text("등록 장비 : "+conVentCnt+" 대("+total_use_vent_per+"%)");
			 var total_not_vent_cnt = $("#total_not_vent_cnt").text("미등록 장비 : "+NotVentCnt+" 대("+total_not_vent_per+"%)");

			 var barChartData = [ 
					{
						"device" : "전체",
						"receive" : conCnt,
						"unreceive" : allNotCnt
					}, {
						"device" : "IAQ",
						"receive" : conIaqCnt,
						"unreceive" : NotIaqCnt
					}, {
						"device" : "OAQ",
						"receive" : conOaqCnt,
						"unreceive" : NotOaqCnt
					}, {
						"device" : "DOT",
						"receive" : conDotCnt,
						"unreceive" : NotDotCnt
					}, {
						"device" : "VENT",
						"receive" : conVentCnt,
						"unreceive" : NotVentCnt
					}
				];

			 	barChartCreate("barChart", barChartData);

			 	var timeChartdata = ["IAQ", "OAQ", "DOT", "VENT"];
				var hisData = param.data.connect;
				timeChartCreate("timeChartdiv", timeChartdata, hisData);
		 }
	})
}

$().ready(function() {
	getDeviceData();
})