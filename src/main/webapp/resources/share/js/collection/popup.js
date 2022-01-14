var hisApiData = [];
var columsData = [];
var chart;
var startTime;
var endTime;

function pdfMakeTemFunc(serialNum, stationName, productDt, elementStr, chartImg, startDt, endDt) {
	var pdfMakeTem = {
		pageSize: "A4",
		pageOrientation: "landscape", // landscape, portrait
		content : [
				{
					columns : [ {
						image : chartImg,
						width : 200,
					}, [ {
						text : stationName,
						color : '#333333',
						width : '*',
						fontSize : 28,
						bold : true,
						alignment : 'right',
						margin : [ 0, 0, 0, 15 ],
					}, {
						stack : [ {
							columns : [ {
								text : '시리얼 번호',
								color : '#aaaaab',
								bold : true,
								width : '*',
								fontSize : 12,
								alignment : 'right',
							}, {
								text : serialNum,
								bold : true,
								color : '#333333',
								fontSize : 12,
								alignment : 'right',
								width : 200,
							}, ],
						}, {
							columns : [ {
								text : '등록 일자',
								color : '#aaaaab',
								bold : true,
								width : '*',
								fontSize : 12,
								alignment : 'right',
							}, {
								text : productDt,
								bold : true,
								color : '#333333',
								fontSize : 12,
								alignment : 'right',
								width : 200,
							}, ],
						}, {
							columns : [ {
								text : '체크 요소',
								color : '#aaaaab',
								bold : true,
								fontSize : 12,
								alignment : 'right',
								width : '*',
							}, {
								text : elementStr,
								bold : true,
								fontSize : 10,
								alignment : 'right',
								color : 'green',
								width : 200,
							}, ],
						}, ],
					}, ], ],
				},
				{
					columns : [ {
						text : 'From',
						color : '#aaaaab',
						bold : true,
						fontSize : 14,
						alignment : 'left',
						margin : [ 0, 20, 0, 5 ],
					}, {
						text : 'To',
						color : '#aaaaab',
						bold : true,
						fontSize : 14,
						alignment : 'left',
						margin : [ 0, 20, 0, 5 ],
					}, ],
				},
				{
					columns : [ {
						text : startDt,
						bold : true,
						color : '#333333',
						alignment : 'left',
					}, {
						text : endDt,
						bold : true,
						color : '#333333',
						alignment : 'left',
					}, ],
				},
				{
					columns : [ {
						text : 'Address',
						color : '#aaaaab',
						bold : true,
						margin : [ 0, 7, 0, 3 ],
					}],
				},
				{
					columns : [
							{
								text : ' - ',
								style : 'invoiceBillingAddress',
							},
						],
				}, 
				'', 
				{
					width : '100%',
					alignment : 'center',
					text : stationName,
					bold : true,
					margin : [ 0, 10, 0, 10 ],
					fontSize : 13,
				}, 
				{
					columns : [ {
						image : chartImg,
						width : 800,
					}]
				},
				'\n', {
					text : '케이웨더 ',
					style : 'notesTitle',
				}, {
					text : 'K.IoT DataCenter, ADMIN-SITE',
					style : 'notesText',
				}, ],
		styles : {
			notesTitle : {
				fontSize : 10,
				bold : true,
				margin : [ 0, 50, 0, 3 ],
			},
			notesText : {
				fontSize : 10,
			},
		},
		defaultStyle : {
			columnGap : 20,
		// font: 'Quicksand',
		},
	};
	return pdfMakeTem
}

$().ready(
		function() {
			var elemetsData;
			var collectionColum = new Array();

			$('#p_h_memberIdx').val(memberIdx);
			$('#p_h_deviceIdx').val(deviceIdx);

			$("#startDt").val("");
			$("#endDt").val("");
			if (deviceType == "IAQ") {
				$("#lastOptVal").val("cici");
			} else {
				$("#lastOptVal").val("coci");
			}

			if (!$("#startDt").val()) {
				var now_date = new Date();
				var now_year = now_date.getFullYear();
				var now_month = now_date.getMonth() + 1;
				var now_day = now_date.getDate();

				if (now_month < 10) {
					now_month = "0" + now_month;
				}

				if (now_day < 10) {
					now_day = "0" + now_day;
				}

				$("#startDt").val(now_year + "/" + now_month + "/" + now_day);
			}

			if (!$("#endDt").val()) {
				var now_date = new Date();
				var now_year = now_date.getFullYear();
				var now_month = now_date.getMonth() + 1;
				var now_day = now_date.getDate();

				if (now_month < 10) {
					now_month = "0" + now_month;
				}

				if (now_day < 10) {
					now_day = "0" + now_day;
				}

				$("#endDt").val(now_year + "/" + now_month + "/" + now_day);
			}

			$.ajax({
				method : "GET",
				async : false,
				url : "/api/datacenter/elements?serial=" + serialNum,
				success : function(d) {
					elemetsData = d.data;
					if (elemetsData.length != 0) {
						columsData.push({
							"data" : "formatTimestamp"
						});

						for ( var key in elemetsData) {
							columsData.push({
								"data" : elemetsData[key].engName
							});
							collectionColum.push(elemetsData[key].engName);
						}

						if (deviceType == "IAQ")
							columsData.push({
								"data" : "cici"
							});
						else
							columsData.push({
								"data" : "coci"
							});
						columsData.push({
							"data" : "pm10_raw"
						});
						columsData.push({
							"data" : "pm25_raw"
						});
					}

					if (collectionColum.length != 0) {
						tableTrDraw(elemetsData, d, collectionColum);
					}

					selectHistory(serialNum, parentSpaceName, spaceName,
							productDt, stationName, standard, deviceType);
				}
			});

			$("#modalSearchBtn").click(
					function() {
						var serialNum = $("#p_h_serialNum").val();
						var parentSpaceName = $("#p_h_parentSpaceName").val();
						var spaceName = $("#p_h_spaceName").val();
						var productDt = $("#p_h_productDt").val();
						var stationName = $("#p_h_stationName").val();
						var standard = $("#searchStandard option:selected").val();

						selectHistory(serialNum, parentSpaceName, spaceName,
								productDt, stationName, standard, deviceType);
					})

			$('#detailBtn').click(function() {
				detailMove();
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
			        	sdate.setDate(sdate.getDate() + rangeDate);
					var s_year = sdate.getFullYear();
					var s_month = sdate.getMonth()+1;
					var s_day = sdate.getDate();
						
					if(s_month <10){
						s_month = "0"+s_month;
					}
					if(s_day <10){
						s_day = "0"+s_day;
					}

					$('#endDt').val(s_year+"/"+s_month+"/"+s_day);

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
					var stxt = selectDate.split("/");
			            stxt[1] = stxt[1] - 1;
			        var sdate = new Date(stxt[0], stxt[1], stxt[2]);
					sdate.setDate(sdate.getDate() - rangeDate);

					var s_year = sdate.getFullYear();
					var s_month = sdate.getMonth()+1;
					var s_day = sdate.getDate();
					if(s_month <10){
						s_month = "0"+s_month;
					}
					if(s_day <10){
						s_day = "0"+s_day;
					}
					

					$('#startDt').val(s_year+"/"+s_month+"/"+s_day);
			      
			    }
			})

			$('#chartTypes').click(function() {
				chgChartData();
			})

			$('#modalDownloadBtn').click(function() {
				$('.excelDownBtn').trigger('click');
			})

			$("#pdfDownBtn").click(function() {
				savePdf();
			})

			$("#winDownBtn").click(function() {
				var initBody = document.body.innerHTML;

				window.onbeforeprint = function () {
					document.body.innerHTML = document.getElementById("printDiv").innerHTML;
				}
				window.onafterprint = function () {
					document.body.innerHTML = initBody;
				}
				window.print();
			})
		})

function savePdf() {
    Promise.all([
		chart.exporting.getImage("png"),
	]).then(function(res) {
	    var doc = {
    		pageSize: "A2",
    		pageOrientation: "landscape", // landscape, portrait
    		pageMargins: [30, 30, 30, 30],
    		content: [],
    	};

	    doc.content.push({
	    	text: "( 한글 - " + $("#p_h_serialNum").val() + " ) INFO",
	    	fontSize: 20,
	    	fonts: am4fonts_notosans_kr,
	    	bold: true,
	    	margin: [0, 20, 0, 15]
	    });

	    doc.content.push({
	    	text: "Chart Data PDF",
	    	fontSize: 20,
	    	bold: true,
	    	margin: [0, 20, 0, 15]
	    });

	    doc.content.push({
	    	columns: [{
	    		image: res[0],
	    	}],
	    	columnGap: 30
	    });

	    var chkArr = Array();
	    var obj = document.getElementsByName("chartType");

	    for(var i=0; i < obj.length; i++) 
	    	if (document.getElementsByName("chartType")[i].checked == true) 
	    		chkArr.push($(obj[i]).attr("data-kor"));

	    var serailNum = $("#p_h_serialNum").val();
	    var stationname = $("#p_h_stationName").val();
	    var productDt = $("#p_h_productDt").val();
	    var elementStr = chkArr.toString();
	    pdfMake.createPdf(pdfMakeTemFunc(serailNum, stationname, productDt, elementStr, res[0], startTime, endTime)).download("report.pdf");
	});	
}

function detailMove() {
	var moveUrl = "/system/member/device/detail/" + memberIdx + "?deviceIdx="
			+ deviceIdx;
	opener.location.replace(moveUrl);
}

function chgChartData() {
    var chkArr = Array();
    var obj = document.getElementsByName("chartType");

    for(var i=0; i < obj.length; i++) if (document.getElementsByName("chartType")[i].checked == true) chkArr.push(obj[i].value);

    timeChartCreate("timeChartDiv", hisApiData, chkArr);
}

function tableTrDraw(datas, rawDatas, colums) {
	var ciType = "";

	if (deviceType == "IAQ") ciType = "cici";
	else ciType = "coci";

	var optionIdx = 0;
	var trHeadHTML = "";
	var trBodyHTML = "";
	var chartTypesHTML = "";
	var optionName = "";

	trHeadHTML += "<th style='width: 200px;' class='bgGray1'>데이터 시간</th>"
	trBodyHTML += "<td></td>";
	trBodyHTML += "<td></td>";

	for (var i = 0; i < colums.length; i++) {
		optionIdx++;
		trHeadHTML += "<th style='width: 200px;' class='bgGray1'>"
			+ datas[i].korName + "<br/>(" + datas[i].elementUnit + ")</th>";
		trBodyHTML += "<td></td>";
		chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' data-kor= '" + datas[i].korName + "'value='" 
			+ colums[i] + "'>" + datas[i].korName + "</label>";
		if (optionIdx % 10 == 0) chartTypesHTML += "<br/>";
	}

	trHeadHTML += "<th style='width: 200px;' class='bgGray1'>통합지수<br/>(" + ciType + ")</th>";
	trHeadHTML += "<th style='width: 200px;' class='bgGray1'>미세먼지 (원본)</th>";
	trHeadHTML += "<th style='width: 200px;' class='bgGray1'>초미세먼지 (원본)</th>";

	chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='" 
		+ ciType + "'>통합지수(" + ciType + ")</label>";

	$('#popTableHead').html(trHeadHTML);
	$('#popTableBody').html(trBodyHTML);
	$('#chartTypes').html(chartTypesHTML);
}

function selectHistory(serialNum, parentSpaceName, spaceName, productDt,
		stationName, standard, deviceType) {
	var obj = new Object();

	startTime = $("#startDt").val();
	endTime = $("#endDt").val();
	var r_deviceType = deviceType;

	if (!startTime && !endTime) {
		var today = new Date();
		var year = today.getFullYear(); // 년도
		var month = today.getMonth() + 1; // 월
		var day = today.getDate(); // 날짜

		if (month < 10) month = "0" + month;

		if (day < 10) day = "0" + day;

		startTime = year + "/" + month + "/" + day + "-00:00:00";
		endTime = year + "/" + month + "/" + day + "-23:59:59";

	} else {
		startTime = startTime + "-00:00:00";
		endTime = endTime + "-23:59:59";
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
		deviceType : r_deviceType, 			// 사용자 계정
		serial : $("#p_h_serialNum").val(), // 장비idx
		startTime : startTime, 				// 2020/04/23-00:00:00
		endTime : endTime, 					// 2020/04/23-23:59:59
		standard : standard,
		connect : "0"
	}

	$.ajax({
		method : "GET",
		url : "/api/collection/history",
		data : obj,
		success : function(d) {
			var apiStandard = $("#searchStandard option:selected").val();
			if ((r_deviceType == "dot") || (apiStandard != "sum")) 
				hisApiData = d.data;
			else hisApiData = formatData(d.data);

			for (var i = 0; i < columsData.length; i++) {
				var key = columsData[i].data;
				for (var j = 0; j < hisApiData.length; j++)
					if (hisApiData[j][key] == null) hisApiData[j][key] = null;
			}

			for (var i in hisApiData) {
				var convertTime = "";

				if (hisApiData[i].timestamp == null) {
					convertTime = "";
				} else {
					var date = new Date(hisApiData[i].timestamp * 1000);
					var year = date.getFullYear();
					var month = date.getMonth() + 1;
					var day = date.getDate();

					var hours = date.getHours();
					var minutes = date.getMinutes();
					var seconds = date.getSeconds();

					if (month < 10) month = '0' + month;
					if (day < 10) day = '0' + day;
					if (hours < 10) hours = '0' + hours;
					if (minutes < 10) minutes = '0' + minutes;
					if (seconds < 10) seconds = '0' + seconds;

					convertTime = year + "-" + month + "-" + day + "\n" + hours
							+ ":" + minutes;
				}

				hisApiData[i].convertTime = convertTime;
				hisApiData[i].ci = hisApiData[i].coci;
			}

			viewData(serialNum, parentSpaceName, spaceName, productDt,
					stationName, standard, deviceType);
		}
	});
}

function formatData(oriData) {
	if (oriData.length == 0)
		return oriData;

	var tsTime;
	var forData = new Array();
	var tsFormatTime = oriData[0].formatTimestamp;

	if (tsFormatTime.substring(11) == "00:00:00")
		tsTime = oriData[0].timestamp;
	else 
		tsTime = Date.parse(tsFormatTime.substring(0, 11) + "00:00:00") / 1000;

	for (i in oriData) {
		if (oriData[i].timestamp > tsTime) {
			var loFlag = true;
			while (loFlag) {
				var obj = {
					timestamp : tsTime,
					formatTimestamp :tsToDate(tsTime),
					tm : ""
				}
				forData.push(obj);
				tsTime = parseInt(tsTime) + 60;
				if (oriData[i].timestamp == tsTime)
					loFlag = false;
			}
		}

		forData.push(oriData[i]);
		tsTime = parseInt(tsTime) + 60;
	}

	return forData;
}

function tsToDate(tsTime) {
	var date = new Date(tsTime * 1000);
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();

	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();

	if (month < 10) 	month = '0' + month;
	if (day < 10) 		day = '0' + day;
	if (hours < 10) 	hours = '0' + hours;
	if (minutes < 10) 	minutes = '0' + minutes;
	if (seconds < 10) 	seconds = '0' + seconds;

	convertTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;

	return convertTime;
}

function viewData(serialNum, parentSpaceName, spaceName, productDt,
		stationName, standard, deviceType) {
	var h_serialNum = $("#p_h_serialNum").val();
	var h_parentSpaceName = $("#p_h_parentSpaceName").val();
	var h_spaceName = $("#p_h_spaceName").val();
	var h_productDt = $("#p_h_productDt").val();
	var h_stationName = $("#p_h_stationName").val();

	$("#txtSerialNum").text(h_serialNum);
	$("#txtProductDt").text(h_productDt);
	$("#txtStationName").text(h_stationName + "의 ");

	var table = $('#popTable').DataTable(
			{
				scrollCollapse : true,
				autoWidth : false,
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
					filename : h_serialNum + "_" + $("#startDt").val() + "_"
							+ $("#endDt").val() + "_download",
					className : 'btn-primary btn excelDownBtn'
				} ],
				destroy : true,
				processing : true,
				serverSide : false,
				data : hisApiData,
				columns : columsData,
				columnDefs : [
			      {
			        targets: [columsData.length-2, columsData.length-1],
			        visible: false,
			        render: function (data, type, full, meta) {
			          return data;
			        },
			      },
		        ],
				initComplete : function(settings, data) {
					chgChartData();
				}
			});
	$("#popTable_filter").hide();
	$($("#tableTitle")).insertBefore("#popTable_length");
	$("#popTable_length").css("display", "inline-block");
	$("#popTable_length").css("margin-left", "20px");
	$("#modalDownloadBtn").insertAfter("#popTable_filter");
	$("#popTable_wrapper .top").first().css("border-bottom",
			"1px solid #E5E5E5");
	$("#popTable_wrapper .top").first().css("padding-bottom", "15px");
	$('.excelDownBtn').css("float", "right");
}

function timeChartCreate(chartDiv, hisData, options) {
	am4core.addLicense("CH205407412");

	chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.data = hisData;
	chart.cursor = new am4charts.XYCursor();
	chart.cursor.behavior = "panXY";
    chart.scrollbarX = new am4charts.XYChartScrollbar();
    chart.scrollbarX.parent = chart.topAxesContainer;

    var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
	categoryAxis.dataFields.category = "convertTime";
	categoryAxis.renderer.opposite = false;
	categoryAxis.connect = false;

	for (var idx in options) createSeries(options[idx]);

	function chgOptionNameFunc(name) {
		var chgName = "";

		if (name == "pm10") chgName = "미세먼지 (㎍/㎥)";
		else if (name == "pm25") chgName = "초미세먼지 (㎍/㎥)";
		else if (name == "co2") chgName = "이산화탄소 (ppm)";
		else if (name == "voc") chgName = "휘발성유기화합물 (ppb)";
		else if (name == "noise") chgName = "소음 (dB)";
		else if (name == "temp") chgName = "온도 (℃ )";
		else if (name == "humi") chgName = "습도 (%)";
		else if (name == "cici") chgName = "통합지수 (cici)";
		else if (name == "coci") chgName = "통합지수 (coci)";
		else chgName = name;

		return chgName;
	}

	function createSeries(name) {
		var chgName = chgOptionNameFunc(name);

		var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
		valueAxis.renderer.inversed = false;
		valueAxis.title.text = chgName;
		valueAxis.renderer.minLabelPosition = 0.01;
		valueAxis.connect = false;
        valueAxis.renderer.line.strokeOpacity = 1;
        valueAxis.renderer.line.strokeWidth = 2;

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

		if (name != 'visitor') series.connect = false;

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
