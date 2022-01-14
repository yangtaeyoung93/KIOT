/**
 * 대시보드 > 사용자/그룹 조회
 */

function barChartCreate(chartDiv, data) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.colors.step = 2;

	chart.numberFormatter.numberFormat = "#.#";
	chart.data = data;

	var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "device";
	categoryAxis.renderer.grid.template.location = 0;
	categoryAxis.renderer.minGridDistance = 30;

	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis.title.text = "사용자 연동 장비";
	valueAxis.title.fontWeight = 800;

	var series = chart.series.push(new am4charts.ColumnSeries());
	series.dataFields.valueY = "totalCnt";
	series.dataFields.categoryX = "device";
	series.clustered = false;
	series.tooltipText = "재고 ({categoryX}) : [bold]{valueY}[/]";

	var series2 = chart.series.push(new am4charts.ColumnSeries());
	series2.dataFields.valueY = "statusCnt";
	series2.dataFields.categoryX = "device";
	series2.clustered = false;
	series2.columns.template.width = am4core.percent(50);
	series2.tooltipText = "사용자 연동 ({categoryX}) : [bold]{valueY}[/]";

	chart.cursor = new am4charts.XYCursor();
	chart.cursor.lineX.disabled = true;
	chart.cursor.lineY.disabled = true;
}

function timeChartCreate(chartDiv, userData) {
	am4core.addLicense("CH205407412");
	am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);

	var data = [];
	var value = 0;
	var s_today = new Date();   
	var year = s_today.getFullYear();
	var month_cnt = 0;
	var c_today = [];

	for (var i = 0; i < 12; i++) {
		month_cnt++;
		if (month_cnt < 10) month_cnt = "0"+month_cnt;
		
		c_today = new Date(year, month_cnt, 0);
		var loginCnt_total = 0;
		for(var j = 1; j < userData.data.userLogin.length; j++){
			var loginCnt = userData.data.userLogin[j].loginCnt;
			var lastLoginDate = userData.data.userLogin[j].lastLoginDate;
			var s_year = lastLoginDate.substring(0,4);
			var s_month = lastLoginDate.substring(4,6);
			var s_day = lastLoginDate.substring(6,8);
			if(month_cnt == s_month){
				loginCnt_total += loginCnt;
			}
		}

		data.push({"date":c_today, "value": loginCnt_total});
		
	}

	chart.data = data;

	var dateAxis = chart.xAxes.push(new am4charts.DateAxis());
	dateAxis.renderer.minGridDistance = 0;

	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

	var series = chart.series.push(new am4charts.LineSeries());
	series.dataFields.valueY = "value";
	series.dataFields.dateX = "date";
	series.tooltipText = "{value}"

	series.tooltip.pointerOrientation = "vertical";

	chart.cursor = new am4charts.XYCursor();
	chart.cursor.snapToSeries = series;
	chart.cursor.xAxis = dateAxis;
	
}

function getUserData(){
	$.ajax({
		 url:"/api/dashboard/user",
		 type:"GET",
		 async : false,
		 success : function (param) {
			 var memberCnt = parseInt(param.data.cnt.memberCnt); //사용자
			 var groupCnt = parseInt(param.data.cnt.groupCnt); //그룹
			 var totalCnt = memberCnt+groupCnt; //종합
			 var didCnt = parseInt(param.data.cnt.didCnt); //did
			 var groupMemberCnt = parseInt(param.data.cnt.groupMemberCnt); //등록계정 
			 
			 var conIaqCnt = parseInt(param.data.cnt.conIaqCnt); //사용자연동 iaq 
			 var conOaqCnt = parseInt(param.data.cnt.conOaqCnt); //사용자연동 oaq 
			 var conDotCnt = parseInt(param.data.cnt.conDotCnt); //사용자연동 dot 
			 var conVentCnt = parseInt(param.data.cnt.conVentCnt); //사용자연동 vent 
			 var allIaqCnt = parseInt(param.data.cnt.allIaqCnt); //재고 iaq 
			 var allOaqCnt = parseInt(param.data.cnt.allOaqCnt); //재고 iaq 
			 var allDotCnt = parseInt(param.data.cnt.allDotCnt); //재고 iaq 
			 var allVentCnt = parseInt(param.data.cnt.allVentCnt); //재고 iaq 
						 
			 var memberPer = ( memberCnt / totalCnt * 100).toFixed(1); //개인 백분율
			 var groupPer = ( groupCnt / totalCnt * 100).toFixed(1); //그룹 백분율

			 $("#mem_total_cnt").text(memberCnt);
			 $("#group_total_cnt").text(groupCnt);
			 $("#did_cnt").text("등록DID : "+didCnt);
			 $("#group_member_cnt").text("등록계정 : "+groupMemberCnt);

			 var barChartData = [ 
				{
					"device" : "IAQ",
					"totalCnt" : allIaqCnt,
					"statusCnt" : conIaqCnt
				}, 
				{
					"device" : "OAQ",
					"totalCnt" : allOaqCnt,
					"statusCnt" : conOaqCnt
				}, 
				{
					"device" : "DOT",
					"totalCnt" : allDotCnt,
					"statusCnt" : conDotCnt
				}, 
				{
					"device" : "VENT",
					"totalCnt" : allVentCnt,
					"statusCnt" : conVentCnt
				}
			];

			barChartCreate("userDeviceChartDiv", barChartData);
			timeChartCreate("userLoginChartDiv", param);	
		 }
	})
}

$().ready(function() {
	getUserData();	
})