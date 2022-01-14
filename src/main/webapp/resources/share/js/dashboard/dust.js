/**
 * 대시보드 > 장비 현황
 */
var map;
var mapView;
var pm25VectorSource;
var pm25VectorLayer;
var pm10VectorSource;
var pm10VectorLayer;

var pm25VectorSource2;
var pm25VectorLayer2;
var pm10VectorSource2;
var pm10VectorLayer2;

var dongPm10CenterSource;
var dongPm25CenterSource;

var dongPm10CenterLayer;
var dongPm25CenterLayer;
var popup;
var apiPosition = [];//oaq
var pm10ApiMarkers = [];
var pm25ApiMarkers = [];

var CenterMarkers = []; //centerDong
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
var centerMarkerImg; //centerDong

var centerPosition = ol.proj.transform([126.9601356, 37.5565158], 'EPSG:4326', 'EPSG:900913');
var coordinate;
var data;
var data2;
var changeFlag = false;

//중심좌표용 마커//
var station_list = [];
var CenterMarker = [];
var CenterFlag = false;
var CenterMakerImg;
var CenterPosition2 = [];

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
				popupContent = "<strong>" + GradecolorOriginVersion(feature.gubun, feature.pmGrade, feature.pmValue) 
					+ '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
			} else if(feature.id == "Korea") {
				popupTitle = feature.aairCode;
				popupContent = "<strong>" + GradecolorOriginVersion(feature.gubun, feature.pmGrade, feature.pmValue) 
					+ '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
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
	document.getElementById("pm25ViewBtn").click();
	map.on("pointermove", function(e){
		if (e.dragging) {
			$(element).popover('dispose');
			return;
		}
	})

	mapView.setCenter(centerPosition);

	pm25VectorSource = new ol.source.Vector({
		features: pm25ApiMarkers
	});

	pm10VectorSource = new ol.source.Vector({
		features: pm25ApiMarkers
	});

	dongPm10CenterSource = new ol.source.Vector();
	dongPm25CenterSource = new ol.source.Vector();

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
	map.addLayer(pm25VectorLayer);
	map.addLayer(pm10VectorLayer);

	map.addLayer(dongPm25CenterLayer);
	map.addLayer(dongPm10CenterLayer);

	map.addOverlay(popup);

	centerGeo()

	pm10VectorLayer.setVisible(false);
	dongPm10CenterLayer.setVisible(false);
}

function initMap2() {
	var popupTitle = "";
	var popupContent = "";

	mapView = new ol.View({
		zoom : 10
	});

	var element = document.getElementById('popup2');

	popup = new ol.Overlay({
		element : element,
		positioning : 'bottom-center',
		stopEvent : false,
		offset : [ 0, -20 ]
	});

	map = new ol.Map({
		layers : [
			new ol.layer.Tile({
				source : new ol.source.XYZ({
					url : 'http://mt.google.com/vt/lyrs=m&x={x}&y={y}&z={z}'
				})
			})
		],
		target : 'map2',
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
				popupContent = "<strong>" + GradecolorOriginVersion(feature.gubun, feature.pmGrade, feature.pmValue) 
					+ '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
			} else if(feature.id == "Korea") {
				popupTitle = feature.aairCode;
				popupContent = "<strong>" + GradecolorOriginVersion(feature.gubun, feature.pmGrade, feature.pmValue) 
					+ '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
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
		} else {
			$(element).popover('dispose');
		}

		$("#popup-closer").click(function(){
			$(element).popover('dispose');
			return;
		})
	});
	document.getElementById("pm25ViewBtn2").click();
	map.on("pointermove", function(e){
		if(e.dragging){
			$(element).popover('dispose');
			return;
		}
	})

	mapView.setCenter(centerPosition);

	pm25VectorSource2 = new ol.source.Vector({
		features: pm25ApiMarkers
	});

	pm10VectorSource2 = new ol.source.Vector({
		features: pm25ApiMarkers
	});

	dongPm10CenterSource = new ol.source.Vector();
	dongPm25CenterSource = new ol.source.Vector();

	pm25VectorLayer2 = new ol.layer.Vector({
	    source: pm25VectorSource2
	});

	pm10VectorLayer2 = new ol.layer.Vector({
	    source: pm10VectorSource2
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
	
	map.addLayer(pm25VectorLayer2);
	map.addLayer(pm10VectorLayer2);

	map.addLayer(dongPm25CenterLayer);
	map.addLayer(dongPm10CenterLayer);

	map.addOverlay(popup);

	oaqData();
	AirData();

	pm10VectorLayer2.setVisible(false);
	dongPm10CenterLayer.setVisible(false);
}

function centerGeo() {
	$.ajax({
		method : "GET",
		url : "/api/dong/geo/dongArea",
		async : true,
		contentType : "application/json; charset=utf-8",

		success : function(d) {
			var pm10MarkerImg2 = [];
			var pm25MarkerImg2 = [];
			changeFlag = true;
			var pm10ApiMarkers2 = [];
			var pm25ApiMarkers2 = [];
			data = d.dongCenterList;

			$.each(data, function(idx) {

				pm10MarkerImg2 = "/resources/share/img/air/0_icon"
						+ data[idx].pm10Grade + ".png";
				pm25MarkerImg2 = "/resources/share/img/air/0_icon"
						+ data[idx].pm25Grade + ".png";

				pm10ApiMarkers2[idx] = new ol.Feature({
					geometry : new ol.geom.Point(0)
				});
				pm25ApiMarkers2[idx] = new ol.Feature({
					geometry : new ol.geom.Point(0)
				});

				pm10ApiMarkers2[idx].setStyle(new ol.style.Style({
					image : new ol.style.Icon({
						src : pm10MarkerImg2
					})
				}));

				pm25ApiMarkers2[idx].setStyle(new ol.style.Style({
					image : new ol.style.Icon({
						src : pm25MarkerImg2
					})
				}));

				apiPosition[idx] = ol.proj.transform([ data[idx].lon,
						data[idx].lat ], 'EPSG:4326', 'EPSG:900913');

				pm10ApiMarkers2[idx].setGeometry(new ol.geom.Point(
						apiPosition[idx]));
				pm10ApiMarkers2[idx].id = "Korea";
				pm10ApiMarkers2[idx].aairCode = data[idx].aairCode;
				pm10ApiMarkers2[idx].pm10Grade = data[idx].pm10Grade;
				pm10ApiMarkers2[idx].gubun = "pm10";
				pm10ApiMarkers2[idx].pmGrade = data[idx].pm10Grade;
				pm10ApiMarkers2[idx].pmValue = data[idx].pm10Value;

				pm25ApiMarkers2[idx].setGeometry(new ol.geom.Point(
						apiPosition[idx]));
				pm25ApiMarkers2[idx].id = "Korea";
				pm25ApiMarkers2[idx].aairCode = data[idx].aairCode;
				pm25ApiMarkers2[idx].pm25Grade = data[idx].pm25Grade;
				pm25ApiMarkers2[idx].gubun = "pm25";
				pm25ApiMarkers2[idx].pmGrade = data[idx].pm25Grade;
				pm25ApiMarkers2[idx].pmValue = data[idx].pm25Value;
			})

			pm10VectorSource.addFeatures(pm10ApiMarkers2);
			pm25VectorSource.addFeatures(pm25ApiMarkers2);
		}
	});
}

function oaqData() {
	$.ajax({
		method : "GET",
		async : false,
		url : "/api/dong/geo/oaqEqui",
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

			pm10VectorSource2.addFeatures(pm10ApiMarkers);
			pm25VectorSource2.addFeatures(pm25ApiMarkers);
		}
	});
}

function AirData() {
	$.ajax({
		method : "GET",
		url : "/api/dong/geo/airEqui",
		async : false,
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			var pm10MarkerImg = [];
			var pm25MarkerImg = [];
			changeFlag = true;
			pm25ApiMarkers = [];
			data = d.airEquiList;
			$.each(data, function(idx) {
				// pm2.5인지 pm10인지 분기 타서 할것
				pm10MarkerImg = "/resources/share/img/air/1_icon"
						+ data[idx].pm10Grade + ".png";
				pm25MarkerImg = "/resources/share/img/air/1_icon"
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
				pm10ApiMarkers[idx].id = "Korea";
				pm10ApiMarkers[idx].aairCode = data[idx].aairCode;
				pm10ApiMarkers[idx].pm10Grade = data[idx].pm10Grade;
				pm10ApiMarkers[idx].gubun = "pm10";
				pm10ApiMarkers[idx].pmGrade = data[idx].pm10Grade;
				pm10ApiMarkers[idx].pmValue = data[idx].pm10Value;

				pm25ApiMarkers[idx].setGeometry(new ol.geom.Point(
						apiPosition[idx]));
				pm25ApiMarkers[idx].id = "Korea";
				pm25ApiMarkers[idx].aairCode = data[idx].aairCode;
				pm25ApiMarkers[idx].pm25Grade = data[idx].pm25Grade;
				pm25ApiMarkers[idx].gubun = "pm25";
				pm25ApiMarkers[idx].pmGrade = data[idx].pm25Grade;
				pm25ApiMarkers[idx].pmValue = data[idx].pm25Value;
			})

			pm10VectorSource2.addFeatures(pm10ApiMarkers);
			pm25VectorSource2.addFeatures(pm25ApiMarkers);
		}
	});
}
 
$().ready(function() {
	initMap();
	initMap2();

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

	$('#pm10ViewBtn2').click(function() {
		$("#pm25ViewBtn2").removeClass("list-box-color");
		$('#pm10ViewBtn2').addClass("list-box-color");

		$("#pm10ViewBtn2").removeClass("list-box-white");
		$('#pm25ViewBtn2').addClass("list-box-white");
		pm10VectorLayer2.setVisible(true);
		pm25VectorLayer2.setVisible(false);
		dongPm10CenterLayer.setVisible(true);
		dongPm25CenterLayer.setVisible(false);
	})

	$('#pm25ViewBtn2').click(function() {
		$("#pm10ViewBtn2").removeClass("list-box-color");
		$('#pm25ViewBtn2').addClass("list-box-color");

		$("#pm25ViewBtn2").removeClass("list-box-white");
		$('#pm10ViewBtn2').addClass("list-box-white");
		pm10VectorLayer2.setVisible(false);
		pm25VectorLayer2.setVisible(true);
		dongPm10CenterLayer.setVisible(false);
		dongPm25CenterLayer.setVisible(true);
	})
})
