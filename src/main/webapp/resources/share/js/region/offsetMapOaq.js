/**
 * 동별 미세먼지 > 동별 미세먼지 실황 (개발)
 */

var map;
var mapView;

var airVectorSource;
var airVectorLayer;

var pm25VectorSource;
var pm25VectorLayer;
var pm10VectorSource;
var pm10VectorLayer;

var dongPm10CenterSource;
var dongPm25CenterSource;

var dongPm10CenterLayer;
var dongPm25CenterLayer;
var popup;
var apiPosition = [];

var airMarkers = [];
var pm10ApiMarkers = [];
var pm25ApiMarkers = [];

var CenterMarkers = [];
var refLineList = [];
var refLineList2 = [];
var polyline;
var polyline2;
var lineFeature;
var lineFeature2;
var lineStyles;
var lineStyles2;
var routeFeature;
var routeFeature2;
var routeSource;
var routeSource2;
var CenterLinePoint;

var markerImg;

var centerPosition = ol.proj.transform([126.9601356, 37.5565158], 'EPSG:4326', 'EPSG:900913');
var coordinate;
var data;
var data2;
var changeFlag = false;

var station_list = [];
var CenterMarker = [];
var CenterFlag = false;
var CenterMakerImg;
var CenterPosition2 = [];

function oaqEquiInfoShow() {
	$('#refInfoOaqArea').show();
}

function modalChart(unique, gubun) {
	var equiType;
	var searchKey;

	$('#modalChart1').html("");
	$('#modalChart1').append("<div id= " + unique + "mChartDiv" + " ></div>");
	$('#modalChart1').children('div').css({"width" : "100%", "height":"700px"});
	$('#modalTitle').html(unique);

	if (gubun == "O") {
		equiType = "oaq";
		data = 
			{
				"searchEquiInfoKey" : unique,
				"mapType" : "O"
			};
	} else if (gubun == "D") {
		equiType = "oaq";
		data = 
			{
				"searchEquiInfoKey" : unique,
				"mapType" : "D"
			}
	} else if (gubun == "A") {
		equiType = "air";
		data = 
			{
				"searchAirCode" : unique
			}
	} else if (gubun == "DONG") {
		equiType = "dong";
		data = 
			{
				"dcode" : unique
			}
	}

	$.ajax({
	      method : "GET",
	      url : "/api/dong/ref/" + equiType + "/history/dev",
	      contentType : "application/json; charset=utf-8",
	      async: false,
	      data : data,
	      success : function(d) {
	    	  var data = d.data;

	    	  $.each(data, function(idx) {
	    		  var formatDate;

	    		  if (data[idx].regDate == null) return;

	    		  var fy = data[idx].regDate.substring(0, 4);
	    		  var fm = data[idx].regDate.substring(4, 6);
	    		  var fd = data[idx].regDate.substring(6, 8);
	    		  var fh = data[idx].regDate.substring(8, 10);
	    		  var fmi = data[idx].regDate.substring(10, 12);

	    		  data[idx].formatDate = new Date(fy, fm, fd, fh, fmi);
	    	  })

	    	  historyChart(data, unique + "mChartDiv");
	      }
	});
}

function refMapLineDraw(dn) {
	routeSource.clear(true);
	refLineList.push(refLineList[0]);
	lineFeature = new ol.Feature({
    	geometry : new ol.geom.Polygon([refLineList])
    });

    lineStyles = new ol.style.Style({
		stroke : new ol.style.Stroke({color : "red", width: "5"}),
        fill: new ol.style.Fill({color: 'rgba(0, 0, 255, 0.2)'})
	})

	lineFeature.setStyle(lineStyles);
	routeFeature = [lineFeature];
	lineFeature.dname = dn;
	routeSource.addFeatures(routeFeature);
}

function refMapLineDraw2() {
	routeSource2.clear(true);

	for (var i in refLineList) {
		refLineList2.push(CenterLinePoint);

		refLineList2.push(refLineList[i]);
		polyline2 = new ol.geom.LineString(refLineList2);

		refLineList2 = [];
		lineFeature2 = new ol.Feature({
	    	geometry : polyline2
	    });

		lineStyles = new ol.style.Style({
			stroke : new ol.style.Stroke({
				color : "blue", width: "3",
	            lineDash: [.1, 5],
	            opacity: 0.5
			})
		})

		lineFeature2.setStyle(lineStyles);
		routeFeature2 = [lineFeature2];
		refListList2 = [];

		routeSource2.addFeatures(routeFeature2);
	}

	refLineList = [];
}

function historyChart(data, chartDiv) {
	am4core.addLicense("CH205407412");
	// am4core.useTheme(am4themes_animated);

	var chart = am4core.create(chartDiv, am4charts.XYChart);
	chart.padding(0, 15, 0, 15);

	chart.leftAxesContainer.layout = "vertical";

	var dateAxis = chart.xAxes.push(new am4charts.DateAxis());
	dateAxis.renderer.grid.template.location = 0;
	dateAxis.renderer.ticks.template.length = 8;
	dateAxis.renderer.ticks.template.strokeOpacity = 0.1;
	dateAxis.renderer.grid.template.disabled = true;
	dateAxis.renderer.ticks.template.disabled = false;
	dateAxis.renderer.ticks.template.strokeOpacity = 0.2;
	dateAxis.renderer.minLabelPosition = 0.01;
	dateAxis.renderer.maxLabelPosition = 0.99;
	dateAxis.keepSelection = true;
	dateAxis.minHeight = 30;

	dateAxis.groupData = true;

	var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis.tooltip.disabled = true;
	valueAxis.height = am4core.percent(50);

	valueAxis.zIndex = 1;
	dateAxis.minZoomCount = 5;
	valueAxis.renderer.baseGrid.disabled = true;
	valueAxis.renderer.inside = true;
	valueAxis.renderer.labels.template.verticalCenter = "bottom";
	valueAxis.renderer.labels.template.padding(2, 2, 2, 2);
	valueAxis.renderer.fontSize = "0.8em"
	valueAxis.renderer.gridContainer.background.fill = am4core.color("#fff000");
	valueAxis.renderer.gridContainer.background.fillOpacity = 0.05;

	var series = chart.series.push(new am4charts.LineSeries());
	series.data = data;
	series.dataFields.dateX = "formatDate";
	series.dataFields.valueY = "pm10Value";
	series.tooltipText = "PM10 : {valueY.value}";
	series.name = "PM10";

	series.defaultState.transitionDuration = 0;

	var valueAxis2 = chart.yAxes.push(new am4charts.ValueAxis());
	valueAxis2.tooltip.disabled = true;
	valueAxis2.height = am4core.percent(50);

	valueAxis2.zIndex = 3;
	valueAxis2.marginTop = 30;
	valueAxis2.renderer.baseGrid.disabled = true;
	valueAxis2.renderer.inside = true;
	valueAxis2.renderer.labels.template.verticalCenter = "bottom";
	valueAxis2.renderer.labels.template.padding(2, 2, 2, 2);
	valueAxis2.renderer.fontSize = "0.8em"
	valueAxis2.renderer.gridContainer.background.fill = am4core.color("#000fff");
	valueAxis2.renderer.gridContainer.background.fillOpacity = 0.05;

	var series2 = chart.series.push(new am4charts.LineSeries());
	series2.data = data;
	series2.dataFields.dateX = "formatDate";
	series2.dataFields.valueY = "pm25Value";
	series2.yAxis = valueAxis2;
	series2.tooltipText = "PM2.5 : {valueY.value}";
	series2.name = "PM2.5";

	chart.cursor = new am4charts.XYCursor();

	var scrollbarX = new am4charts.XYChartScrollbar();
	scrollbarX.series.push(series);
	scrollbarX.marginBottom = 20;
	scrollbarX.scrollbarChart.xAxes.getIndex(0).minHeight = undefined;
	chart.scrollbarX = scrollbarX;
}

function oaqHisData(serial) {
	$('#dongAirChartDivArea').html("");
	$('#dongOaqChartDivArea').html("");

	var chartHtml = '<div class="card-header">' +
		'<h3 class="card-title"><a href="#"><strong data-toggle="modal" ' +
		'onclick="modalChart(\'' + serial + '\',\'O\');" ' + 
		'style="cursor: pointer;" data-target="#modal-chart">' + serial + 
		'</a></strong></h3></div><div class="card-body" id="pmListChart"><div style="height: 300px;" id="' + serial + 
		'chartDiv"></div></div>';

	$('#dongOaqChartDivArea').append(chartHtml);

	$.ajax({
	      method : "GET",
	      url : "/api/dong/ref/oaq/history/dev",
	      contentType : "application/json; charset=utf-8",
	      async: false,
	      data : {
	    	  "searchEquiInfoKey" : serial,
	    	  "mapType" : "O"
	      },
	      success : function(d) {
	    	  var data = d.data;

	    	  $.each(data, function(idx) {
	    		  var formatDate;
	    		  if (data[idx].regDate == null) return;
	    		  var fy = data[idx].regDate.substring(0, 4);
	    		  var fm = data[idx].regDate.substring(4, 6);
	    		  var fd = data[idx].regDate.substring(6, 8);
	    		  var fh = data[idx].regDate.substring(8, 10);
	    		  var fmi = data[idx].regDate.substring(10, 12);

	    		  data[idx].formatDate = new Date(fy, fm, fd, fh, fmi);
	    	  })

	    	  historyChart(data, serial + "chartDiv");
	      }
	})
}

function airHistory(uniqueIds, mapTypes) {
	$('#oaqChartDivArea').html("");

	for (var idx in uniqueIds) {
		var chartHtml = '<div class="card-header">' +
			'<h3 class="card-title"><a href="#"><strong data-toggle="modal" ' +
			'onclick="modalChart(\'' + uniqueIds[idx] + '\',\'A\');" ' + 
			'style="cursor: pointer;" data-target="#modal-chart">' + uniqueIds[idx] + 
			'</a></strong></h3></div><div class="card-body" id="pmListChart"><div style="height: 300px;" id="' + uniqueIds[idx] + 
			'chartDiv"></div></div>';

		$('#oaqChartDivArea').append(chartHtml);

		$.ajax({
		      method : "GET",
		      url : "/api/dong/ref/air/history/dev",
		      contentType : "application/json; charset=utf-8",
		      async: false,
		      data : {
		    	  "searchAirCode" : uniqueIds[idx]
		      },
		      success : function(d) {
		    	  var data = d.data;

		    	  $.each(data, function(idx) {
		    		  var formatDate;
		    		  if (data[idx].regDate == null) return;
		    		  var fy = data[idx].regDate.substring(0, 4);
		    		  var fm = data[idx].regDate.substring(4, 6);
		    		  var fd = data[idx].regDate.substring(6, 8);
		    		  var fh = data[idx].regDate.substring(8, 10);
		    		  var fmi = data[idx].regDate.substring(10, 12);

		    		  data[idx].formatDate = new Date(fy, fm, fd, fh, fmi);
		    	  })

		    	  historyChart(data, uniqueIds[idx] + "chartDiv");
		      }
		})
	}
}


function refCreatTb(serial, centerLocation, refLength, equiType, upDate, pm10Value, pm25Value) {
	var refInfoHtml = "<div class='row'><strong>("+ equiType +") "+ serial + ", ";
	refInfoHtml += "중심지점 좌표 ("+ centerLocation[0].toString().substring(0, 7) + ", " + centerLocation[1].toString().substring(0, 6)+") ";
	refInfoHtml += upDate +"</strong></div>";
	refInfoHtml += "<strong>미세먼지 : </strong>" + pm10Value + "<br/>";
	refInfoHtml += "<strong>초미세먼지 : </strong>" + pm25Value;

	refInfoHtml += 
		"<div class=''><strong style='color: red; font-size: small;'>인근지점 관측망 -- 총 " + 
		refLength + "개 (알고리즘에 사용한 지점 정보)</strong></div><hr/>";

	return refInfoHtml;
}

function choiceOaqRow(rowSerial, rowDfname, rowDcode, rowLon, rowLat, pm10Value, pm25Value, pm10Grade, pm25Grade, mapType, upDate) {
	var refHtml;
	var dongLocation = [rowLon, rowLat];
	// map.getView().setCenter(ol.proj.transform(dongLocation, 'EPSG:4326', 'EPSG:900913'));
	// map.getView().setZoom(15);

	var rome = ol.proj.fromLonLat(dongLocation);
	var center = mapView.getCenter();
	mapView.animate({
		center : [ center[0] + (rome[0] - center[0]) / 2,
				center[1] + (rome[1] - center[1]) / 2 ],
		rotation : Math.PI,
		easing : ol.easing.aseIn,
	}, {
		center : rome,
		rotation : 2 * Math.PI,
		easing : ol.easing.easeOut,
		zoom : 15
	});

	var updateDtStr = upDate.toString();
	var updateDtStrYear = updateDtStr.substring(0,4);
	var updateDtStrMonth = updateDtStr.substring(4,6);
	var updateDtStrDay = updateDtStr.substring(6,8);
	var updateDtStrHour = updateDtStr.substring(8,10);
	var updateDtStrMinute = updateDtStr.substring(10,12);
	var trans_time = updateDtStrYear+"-"+updateDtStrMonth+"-"+updateDtStrDay+ " " +updateDtStrHour+":"+updateDtStrMinute;

	$('#dongAirChartDivArea').html("");

	$('#refInfoOaqArea').show();
	$('#refInfoOaqDiv').html("<strong style='color: red;' id='refInfoMsg'>데이터를 로딩중 입니다.</strong>");
	$('#oaqChartDivArea').html("");
	$('#dName').html("(" + rowSerial + ")");

	$.ajax({
	      method : "GET",
	      url : "/api/dong/ref/pm/offset",
	      contentType : "application/json; charset=utf-8",
	      data : {
	    	  "deviceType" : "a",
	    	  "serialNum" : rowSerial
	      },
	      success : function(d) {
	    	  var data = d.data;
	    	  var uniqueIds = [];
	    	  var mapTypes = [];

	    	  refHtml = refCreatTb(rowSerial, dongLocation, data.length, "OAQ", trans_time, pm10Value, pm25Value);

	    	  $.each(data, function(idx, qq) {
	    		  refLineList.push(ol.proj.transform([qq.lon, qq.lat], 'EPSG:4326', 'EPSG:900913'));

	    		  uniqueIds.push(qq.mapCode);
	    		  mapTypes.push(qq.mapType);

	    		  refHtml += 
	    			  "<div class='row'><strong>" + qq.mapCode + ":" + qq.aairName +
	    			  ", 중심지점 좌표 (" + qq.lon + ", " + qq.lat +"), " + qq.distance + ", " +
	    			  transTime2(qq.upDate) + 
	    			  "</strong></div>";

	    		  refHtml += "<strong>미세먼지 : </strong>" + qq.pm10Avg3;
	    		  refHtml += ",  <strong>초미세먼지 : </strong>" + qq.pm25Avg3;

	    		  refHtml += "<hr/>";
	    	  })

	    	  if (data.length != 0) refMapLineDraw(rowSerial);

	    	  oaqEquiInfoShow();
	    	  $('#refInfoOaqDiv').html(refHtml);
	    	  airHistory(uniqueIds, mapTypes);

	    	  choiceGeo(rowSerial, rowDcode, rowLon, rowLat, pm10Grade, pm25Grade, pm10Value, pm25Value);
	      }
	});

	oaqHisData(rowSerial);
}

function DateToString(pDate) {
	var yyyy = pDate.getFullYear();
	var mm = pDate.getMonth() < 9 ? "0" + (pDate.getMonth() + 1) : (pDate.getMonth() + 1);
	var dd  = pDate.getDate() < 10 ? "0" + pDate.getDate() : pDate.getDate();
	var hh = pDate.getHours() < 10 ? "0" + pDate.getHours() : pDate.getHours();
	var min = pDate.getMinutes() < 10 ? "0" + pDate.getMinutes() : pDate.getMinutes();
	var ss = pDate.getSeconds() < 10 ? "0" + pDate.getSeconds() : pDate.getSeconds() ;

	return "".concat(yyyy).concat("-").concat(mm).concat("-").concat(dd).concat(" ").concat(hh).concat(":").concat(min).concat(":").concat(ss);
};

function choiceGeo(rowDname, rowDcode, rowLon, rowLat, pm10Grade, pm25Grade, pm10Value, pm25Value) {
	dongPm10CenterSource.clear();
	dongPm25CenterSource.clear();
	idx = 0;
	var pm25MarkerImg = [];
	var pm10MarkerImg = [];
	changeFlag = true;
	pm10ApiMarkers = [];
	pm25ApiMarkers = [];

	CenterLinePoint = ol.proj.transform([rowLon, rowLat], 'EPSG:4326', 'EPSG:900913');

	pm10MarkerImg = "/resources/share/img/air/me_icon1.png";
	pm25MarkerImg = "/resources/share/img/air/me_icon1.png";

	pm10ApiMarkers[idx] = new ol.Feature({
		geometry: new ol.geom.Point(0)
    });
	pm25ApiMarkers[idx] = new ol.Feature({
		geometry: new ol.geom.Point(0)
    });

	pm10ApiMarkers[idx].setStyle(new ol.style.Style({
    	image: new ol.style.Icon({
    		src: pm10MarkerImg
        })
    }));
	pm25ApiMarkers[idx].setStyle(new ol.style.Style({
    	image: new ol.style.Icon({
    		src: pm25MarkerImg
        })
    }));

    apiPosition[idx] = ol.proj.transform([rowLon, rowLat], 'EPSG:4326', 'EPSG:900913');
    pm10ApiMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));
    pm25ApiMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));

    pm10ApiMarkers[idx].id = "dongArea";
    pm25ApiMarkers[idx].id = "dongArea";

    pm25ApiMarkers[idx].dname = rowDname;
    pm10ApiMarkers[idx].dname = rowDname;

    pm10ApiMarkers[idx].lat = rowLat;
    pm25ApiMarkers[idx].lat = rowLat;

    pm10ApiMarkers[idx].lon = rowLon;
    pm25ApiMarkers[idx].lon = rowLon;

	pm25ApiMarkers[idx].pmValue = "PM2.5 : " + pm25Value;
	pm10ApiMarkers[idx].pmValue = "PM10 : " + pm10Value;

    refMapLineDraw2();

    dongPm10CenterSource.addFeatures(pm10ApiMarkers);
    dongPm25CenterSource.addFeatures(pm25ApiMarkers);
}

function oaqData() {
	$.ajax({
		method : "GET",
		url : "/api/dong/geo/oaqEqui/dev",
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			var pm25MarkerImg = [];
			var pm10MarkerImg = [];
			changeFlag = true;
			pm10ApiMarkers = [];
			pm25ApiMarkers = [];

			data = d.oaqEquiList;
			$.each(data, function(idx) {
				pm10MarkerImg = "/resources/share/img/air/0_icon"
						+ data[idx].pm10Grade + ".png";
				pm25MarkerImg = "/resources/share/img/air/0_icon"
						+ data[idx].pm25Grade + ".png";

				pm10ApiMarkers[idx] = new ol.Feature({
					geometry : new ol.geom.Point(0)
				});
				pm25ApiMarkers[idx] = new ol.Feature({
					geometry : new ol.geom.Point(0)
				});
				pm10ApiMarkers[idx].setStyle(new ol.style.Style({
					image : new ol.style.Icon({
						src : pm10MarkerImg
					})
				}));

				pm25ApiMarkers[idx].setStyle(new ol.style.Style({
					image : new ol.style.Icon({
						src : pm25MarkerImg
					})
				}));

				apiPosition[idx] = ol.proj.transform([ data[idx].lon,
						data[idx].lat ], 'EPSG:4326', 'EPSG:900913');

				pm10ApiMarkers[idx].setGeometry(new ol.geom.Point(
						apiPosition[idx]));
				pm10ApiMarkers[idx].id = "kweather";
				pm10ApiMarkers[idx].serial = data[idx].oserial;
				pm10ApiMarkers[idx].pm10Grade = data[idx].pm10Grade;
				pm10ApiMarkers[idx].gubun = "pm10";
				pm10ApiMarkers[idx].pmGrade = data[idx].pm10Grade;
				pm10ApiMarkers[idx].pmValue = data[idx].pm10Value;

				pm25ApiMarkers[idx].setGeometry(new ol.geom.Point(
						apiPosition[idx]));
				pm25ApiMarkers[idx].id = "kweather";
				pm25ApiMarkers[idx].serial = data[idx].oserial;
				pm25ApiMarkers[idx].pm25Grade = data[idx].pm25Grade;
				pm25ApiMarkers[idx].gubun = "pm25";
				pm25ApiMarkers[idx].pmGrade = data[idx].pm25Grade;
				pm25ApiMarkers[idx].pmValue = data[idx].pm25Value;
			})

			pm10VectorSource.addFeatures(pm10ApiMarkers);
			pm25VectorSource.addFeatures(pm25ApiMarkers);
		}
	});
}

function airData() {
	$.ajax({
	    method : "GET",
	    async: false,
	    url : "/api/dong/geo/pm",
	    contentType : "application/json; charset=utf-8",
	    success : function(d) {
	    	var pmMarkerImg = [];
	    	changeFlag = true;
	    	data = d.data;
	    	$.each(data, function(idx) {
	        	pmMarkerImg = data[idx].useYn == 'N' ? "/resources/share/img/air/1_icon0.png" : "/resources/share/img/air/1_icon1.png";

	        	airMarkers[idx] = new ol.Feature({
	                geometry: new ol.geom.Point(0)
	            });

	        	airMarkers[idx].setStyle(new ol.style.Style({
	                image: new ol.style.Icon({
	                   src: pmMarkerImg
	                })
	            }));

	        	apiPosition[idx] = ol.proj.transform([data[idx].lon, data[idx].lat], 'EPSG:4326', 'EPSG:900913');

	        	airMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));
	            airMarkers[idx].id = "Korea";
	            airMarkers[idx].aairCode = data[idx].aairCode;
	            airMarkers[idx].aairName = data[idx].aairName;
	            airMarkers[idx].pm10Grade = data[idx].pm10Grade;
	            airMarkers[idx].gubun = "pm10";
	            airMarkers[idx].pmGrade = data[idx].pm10Grade;
	            airMarkers[idx].pmValue = data[idx].pm10Value;
	        })

	        airVectorSource.addFeatures(airMarkers);
	     }
	  });
}

function initMap() {
	var popupTitle = "";
	var popupContent = "";

	mapView = new ol.View({
		zoom : 10
	});

	var element = document.getElementById('popup');

	popup = new ol.Overlay({
		element : element,
		positioning : 'bottom-center',
		stopEvent : false,
		offset : [ 0, -20 ]
	});

	map = new ol.Map({
		controls: new ol.control.defaults().extend([
		    new ol.control.FullScreen({
		    	source: 'fullscreen'
		    })
		]),
		layers : [
			new ol.layer.Tile({
				source : new ol.source.XYZ({
					url : 'http://mt.google.com/vt/lyrs=m&x={x}&y={y}&z={z}'
				})
			})
		],
		target : 'map',
		view : mapView,
	});

	map.on('click', function(evt) {
		var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature) {
			return feature;
		});
		$(element).popover('dispose');

		if (feature) {
			if (changeFlag) {
				$(element).popover('dispose');
				changeFlag = false;
			}

			coordinate = evt.coordinate;
			popup.setPosition(coordinate);
			
			if(feature.id == "dongArea") {
				popupTitle = feature.dname+" 중심좌표 <br/>("+feature.lon+")<br/>("+feature.lat+")";
				popupContent = "<strong>" + feature.pmValue + "</strong>"
			} else if(feature.id == "kweather") {
				popupTitle = feature.serial;
				popupContent = "<strong>" + GradecolorOriginVersion(feature.gubun, feature.pmGrade, feature.pmValue) + '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
			} else if(feature.id == "Korea") {
				popupTitle = feature.aairCode;
				popupContent = "<strong>" + feature.aairName + '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
			} else {
				popupTitle = "참조 영역";
				popupContent = "<strong>" + feature.dname + "</strong>";
			}

			$(element).attr('title', "<strong>" + popupTitle + "</strong>");
			$(element).popover({
				placement: 'top',
				html: true,
				animation: false,
				content: popupContent
			});
			$(element).popover('show');
		} else $(element).popover('dispose');

		$("#popup-closer").click(function(){
			$(element).popover('dispose');
			return;
		})
	});

	map.on("pointermove", function(e){
		if(e.dragging){
			$(element).popover('dispose');
			return;
		}
	})

	mapView.setCenter(centerPosition);

	airVectorSource = new ol.source.Vector({
		features: airMarkers
	});

	pm25VectorSource = new ol.source.Vector({
		features: pm25ApiMarkers
	});

	pm10VectorSource = new ol.source.Vector({
		features: pm25ApiMarkers
	});

	dongPm10CenterSource = new ol.source.Vector();
	dongPm25CenterSource = new ol.source.Vector();

	airVectorLayer = new ol.layer.Vector({
	    source: airVectorSource
	});

	pm25VectorLayer = new ol.layer.Vector({
	    source: pm25VectorSource
	});

	pm10VectorLayer = new ol.layer.Vector({
	    source: pm10VectorSource
	});

	dongPm10CenterLayer = new ol.layer.Vector({
	    source: dongPm10CenterSource
	});

	dongPm25CenterLayer = new ol.layer.Vector({
	    source: dongPm25CenterSource
	});

	routeSource = new ol.source.Vector();
	routeSource2 = new ol.source.Vector();

	routeLayer = new ol.layer.Vector({
	  source: routeSource
	});

	routeLayer2 = new ol.layer.Vector({
		  source: routeSource2
	});

	map.addLayer(routeLayer);
	map.addLayer(routeLayer2);
	map.addLayer(pm10VectorLayer);
	map.addLayer(pm25VectorLayer);

	map.addLayer(airVectorLayer);
	map.addLayer(dongPm10CenterLayer);
	map.addLayer(dongPm25CenterLayer);

	map.addOverlay(popup);

	oaqData();
	airData();
}

function initDataTableCustom() {
	var searchDfname1 = $("#searchDfname1 option:selected").val();
	var searchDfname2 = $("#searchDfname2 option:selected").val();

	if (!searchDfname1) searchDfname1 = 11;

	if (!searchDfname2) searchDfname2 = 110;

	var table = $('#dustTable').DataTable({
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
			 "url":"/api/dong/pm/offset/list",
			 "type":"GET",
			 data : function (param) {
				 param.deviceType = 'O';
             }
		},
        columns : [
        	{data: "oserial"},
        	{data: "pm10Value"},
        	{data: "pm25Value"},
        	{data: "pm10Grade"},
            {data: "pm25Grade"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
	        		var innerHtml = "";
	        		innerHtml = "<a href='#'><strong " +
	        				"onclick='choiceOaqRow(\"" + full.oserial + "\"" + ",\"" + full.oserial + "\"" + ",\"" + full.oserial + "\", " + full.lon + ", " + full.lat + 
	        					",\""+ ("보간값-" + full.pm10Idw + ", 3h평균-" + full.pm10Avg3 + ", 보정비율-" + full.pm10Ratio + ", 보정옵셋-" + full.pm10Offset + ", 보정비율-" + full.pm10RatioRaw + "(원)") + 
	        					"\",\"" + ("보간값-" + full.pm25Idw + ", 3h평균-" + full.pm25Avg3 + ", 보정비율-" + full.pm25Ratio + ", 보정옵셋-" + full.pm25Offset + ", 보정비율-" + full.pm25RatioRaw + "(원)") + 
	        					"\",\""+full.oserial+"\",\""+full.oserial+"\", \""+full.mapType+"\",\""+full.upDate+"\");'>" + 
	        				data + "</strong></a>";

    				return innerHtml;
	        	}
        	},
        	{
	        	targets:   1,
	        	render: function(data, type, full, meta) {
	        		var pmValstr = "";

	        		pmValstr += "<div><strong>보간값</strong> : <strong>" + full.pm10Idw + "</strong></div>";
	        		pmValstr += "<div><strong>3h평균</strong> : <strong>" + full.pm10Avg3 + "</strong></div>";
	        		pmValstr += "<div><strong>보정비율</strong> : <strong>" + full.pm10Ratio + "</strong></div>";
	        		pmValstr += "<div><strong>보정옵셋</strong> : <strong>" + full.pm10Offset + "</strong></div>";
	        		pmValstr += "<div><strong>보정비율(원)</strong> : <strong>" + full.pm10RatioRaw + "</strong></div>";

					return pmValstr;
				},
        	},
	        {
	        	targets:   2,
	        	render: function(data, type, full, meta) {
	        		var pmValstr = "";

	        		pmValstr += "<div><strong>보간값</strong> : <strong>" + full.pm25Idw + "</strong></div>";
	        		pmValstr += "<div><strong>3h평균</strong> : <strong>" + full.pm25Avg3 + "</strong></div>";
	        		pmValstr += "<div><strong>보정비율</strong> : <strong>" + full.pm25Ratio + "</strong></div>";
	        		pmValstr += "<div><strong>보정옵셋</strong> : <strong>" + full.pm25Offset + "</strong></div>";
	        		pmValstr += "<div><strong>보정비율(원)</strong> : <strong>" + full.pm25RatioRaw + "</strong></div>";
	        		
					return pmValstr;
				},
	        },
	        {
	        	targets:   3,
	        	render: function(data, type, full, meta) {	   
					return transTime(full.upDate);
				},
	        },
	        {
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		var lat = full.lat;
	        		var lon = full.lon;
	        		var latStr = lat.toString();
	        		var lonStr = lon.toString();
	        		var latSubStr = latStr.substring(0,8);
	        		var lonSubStr = lonStr.substring(0,9);
	        		var coord = "";

	        		coord += latSubStr+"<br/>";
	        		coord += lonSubStr;

	        		return coord;
				},
	        } 
        ],
	});

	$('#searchText').unbind().bind('keyup', function () {
        var colIndex = 0;
        table.column(colIndex).search(this.value).draw();
    });
}

function changeDo() {
	var html = "";
	
	$.ajax({
		 url:"/api/dong/siList/dev",
		 type:"GET",
		 success : function (param) {
			 for (var i = 0; i < param.data.length; i++) html += "<option value='"
				 + param.data[i].sdcode + "'>" + param.data[i].dname + "</option>";

			 $("#searchDfname1").html(html);
		 }
	})
}

function valueColor(type,value) {
	var valueColorHtml = "";

	if (type == "PM10") {
		if (value < 31) valueColorHtml = "<font style='color:#44C7F5'>"+value+"</font>";
		else if (value < 51) valueColorHtml = "<font style='color:#8DD538'>"+value+"</font>";
		else if (value < 101) valueColorHtml = "<font style='color:#F97B47'>"+value+"</font>";
		else valueColorHtml = "<font style='color:#F93F3E'>"+value+"</font>";

	} else {
		if (value < 16) valueColorHtml = "<font style='color:#44C7F5'>"+value+"</font>";
		else if (value < 26) valueColorHtml = "<font style='color:#8DD538'>"+value+"</font>";
		else if (value < 51) valueColorHtml = "<font style='color:#F97B47'>"+value+"</font>";
		else valueColorHtml = "<font style='color:#F93F3E'>"+value+"</font>";
	}

	return valueColorHtml;
}

function Gradecolor(type, Grade){
	var GradeColorHtml = "";

	if (type == "PM10") {
		if (Grade == 1) GradeColorHtml = "<font style='color:#44C7F5'>좋음</font>";
		else if (Grade == 2) GradeColorHtml = "<font style='color:#8DD538'>보통</font>";
		else if (Grade == 3) GradeColorHtml = "<font style='color:#F97B47'>나쁨</font>";
		else GradeColorHtml = "<font style='color:#F93F3E'>매우나쁨</font>";

	} else {
		if (Grade == 1) GradeColorHtml = "<font style='color:#44C7F5'>좋음</font>";
		else if (Grade == 2) GradeColorHtml = "<font style='color:#8DD538'>보통</font>";
		else if (Grade == 3) GradeColorHtml = "<font style='color:#F97B47'>나쁨</font>";
		else GradeColorHtml = "<font style='color:#F93F3E'>매우나쁨</font>";
	}

	return GradeColorHtml;
}


function GradecolorOriginVersion(type, Grade, value) {
	var GradeColorHtml = "";

	if (type == "pm10") {
		if (Grade == 1) GradeColorHtml = "<span>PM10 : " + value;
		else if (Grade == 2) GradeColorHtml = "<span>PM10 : " + value;
		else if (Grade == 3) GradeColorHtml = "<span>PM10 : " + value;
		else GradeColorHtml = "<span>PM10 : " + value;

	} else {
		if (Grade == 1) GradeColorHtml = "<span>PM2.5 : " + value;
		else if (Grade == 2) GradeColorHtml = "<span>PM2.5 : " + value;
		else if (Grade == 3)GradeColorHtml = "<span>PM2.5 : " + value;
		else GradeColorHtml = "<span>PM2.5 : " + value;
	}

	return GradeColorHtml;
}

function transTime(dt) {
	var dateDt = dt;
	var dateDtStr = dateDt.toString();
	var dateDtStrYear = dateDtStr.substring(0,4);
	var dateDtStrMonth = dateDtStr.substring(4,6);
	var dateDtStrDay = dateDtStr.substring(6,8);
	var dateDtStrHour = dateDtStr.substring(8,10);
	var dateDtStrMinute = dateDtStr.substring(10,12);
	var trans_time = dateDtStrYear + "-" + dateDtStrMonth + "-" + dateDtStrDay + "<br/>" + dateDtStrHour + ":" + dateDtStrMinute;

	return trans_time;
}

function transTime2(dt) {
	var dateDt = dt;
	var dateDtStr = dateDt.toString();
	var dateDtStrYear = dateDtStr.substring(0,4);
	var dateDtStrMonth = dateDtStr.substring(4,6);
	var dateDtStrDay = dateDtStr.substring(6,8);
	var dateDtStrHour = dateDtStr.substring(8,10);
	var dateDtStrMinute = dateDtStr.substring(10,12);
	var trans_time = dateDtStrYear + "-" + dateDtStrMonth + "-" + dateDtStrDay + " " + dateDtStrHour + ":" + dateDtStrMinute;

	return trans_time;
}

function ChangeMeter(val) {
	var MeterStr = "";
	var MeterVal = val / 1000;
	MeterStr = MeterVal.toFixed(1) + "KM, ";

	return MeterStr;
}

$().ready(function() {
	initMap();

	initDataTableCustom();

	$('#refInfoOaqArea').hide();

	$(".dataTables_filter").hide();

	pm10VectorLayer.setVisible(true);
	dongPm10CenterLayer.setVisible(true);

	$('#pm10ViewBtn').click(function() {
		$("#pm25ViewBtn").removeClass("list-box-color");
		$('#pm10ViewBtn').addClass("list-box-color");

		$("#pm10ViewBtn").removeClass("list-box-white");
		$('#pm25ViewBtn').addClass("list-box-white");
		pm10VectorLayer.setVisible(true);
		pm25VectorLayer.setVisible(false);
		dongPm10CenterLayer.setVisible(true);
		dongPm25CenterLayer.setVisible(false);
	})

	$('#pm25ViewBtn').click(function() {
		$("#pm10ViewBtn").removeClass("list-box-color");
		$('#pm25ViewBtn').addClass("list-box-color");

		$("#pm25ViewBtn").removeClass("list-box-white");
		$('#pm10ViewBtn').addClass("list-box-white");
		pm10VectorLayer.setVisible(false);
		pm25VectorLayer.setVisible(true);
		dongPm10CenterLayer.setVisible(false);
		dongPm25CenterLayer.setVisible(true);
	})

	pm10VectorLayer.setVisible(false);
	pm25VectorLayer.setVisible(true);
	dongPm10CenterLayer.setVisible(false);
	dongPm25CenterLayer.setVisible(true);
});