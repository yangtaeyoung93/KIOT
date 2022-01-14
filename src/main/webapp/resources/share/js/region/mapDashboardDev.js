/**
 * 동별 미세먼지 > 동별 미세먼지 실황 (개발)
 */

let map;
let mapView;
let pm25VectorSource;
let pm25VectorLayer;
let pm10VectorSource;
let pm10VectorLayer;

let dongPm10CenterSource;
let dongPm25CenterSource;

let dongPm10CenterLayer;
let dongPm25CenterLayer;
let popup;
let apiPosition = [];
let pm10ApiMarkers = [];
let pm25ApiMarkers = [];

let CenterMarkers = [];
let refLineList = [];
let refLineList2 = [];
let polyline;
let polyline2;
let lineFeature;
let lineFeature2;
let lineStyles;
let lineStyles2;
let routeFeature;
let routeFeature2;
let routeSource;
let routeSource2;
let CenterLinePoint;

let markerImg;
let centerMarkerImg;

let dongDataList = [];
const pm25ColorSet = ["#274491", "#3a7d0b", "#cc4748", "#745d2f"];
const pm10ColorSet = ["#67b7dc", "#84b761", "#cf468d", "#d68f00"];

const centerPosition = ol.proj.transform(
  [126.9601356, 37.5565158],
  "EPSG:4326",
  "EPSG:900913"
);
let coordinate;
let data;
let data2;
let changeFlag = false;

//중심좌표용 마커//
let station_list = [];
let CenterMarker = [];
let CenterFlag = false;
let CenterMakerImg;
let CenterPosition2 = [];

let fieldDfname;
let modalParam = [];

function airEquiInfoShow() {
  $("#refInfoOaqArea").hide();
  $("#refInfoAirArea").show();
}

function oaqEquiInfoShow() {
  $("#refInfoAirArea").hide();
  $("#refInfoOaqArea").show();
}

function modalChart(unique, type) {
  let deviceType;
  let param;
  $("#modalChart1")
    .html("")
    .append("<div id= " + unique + "mChartDiv" + " ></div>")
    .children("div")
    .css({ width: "100%", height: "700px" });

  if (type === "DONG") {
    $("#modalTitle").html(fieldDfname);
    chartData = modalParam;
    dongDataList = [];
  } else {
    $("#modalTitle").html(unique);
    chartData = [{ unique: unique, mapType: type }];
  }

  for (let i in chartData) {
    const date = $("#num").val() + $("#unit").val() + "-ago";
    console.log(date);
    switch (chartData[i].mapType) {
      case "O":
        deviceType = "oaq";
        param = {
          searchEquiInfoKey: chartData[i].unique,
          mapType: "O",
          date: date,
        };
        break;
      case "D":
        deviceType = "oaq";
        param = {
          searchEquiInfoKey: chartData[i].unique,
          mapType: "D",
          date: date,
        };
        break;
      case "A":
        deviceType = "air";
        param = { searchAirCode: chartData[i].unique, date: date };
        break;
      case "DONG":
        deviceType = "dong";
        param = { dcode: chartData[i].unique, date: date };
        break;
    }

    $.ajax({
      method: "GET",
      url: "/api/dong/ref/" + deviceType + "/history/dev",
      contentType: "application/json; charset=utf-8",
      async: false,
      data: param,
      success: function (d) {
        if (deviceType == "dong") {
          beforeDrawing(d.data, chartData[i].dongName);
        } else {
          modalData = [];
          modalData.push({
            id: chartData[i].unique,
            data: beforeDrawing(d.data, chartData[i].unique),
          });
        }
      },
    });
  }
  if (type == "DONG") {
    drawChart(dongDataList, unique + "mChartDiv");
  } else {
    drawChart(modalData, unique + "mChartDiv");
  }

  $(document)
    .off("click", "#dateSearch")
    .on("click", "#dateSearch", function () {
      modalChart(unique, type);
    });
}

function refMapLineDraw(dn) {
  routeSource.clear(true);
  refLineList.push(refLineList[0]);
  lineFeature = new ol.Feature({
    geometry: new ol.geom.Polygon([refLineList]),
  });

  lineStyles = new ol.style.Style({
    stroke: new ol.style.Stroke({ color: "red", width: "5" }),
    fill: new ol.style.Fill({ color: "rgba(0, 0, 255, 0.2)" }),
  });

  lineFeature.setStyle(lineStyles);
  routeFeature = [lineFeature];
  lineFeature.dname = dn;
  routeSource.addFeatures(routeFeature);
}

function refMapLineDraw2() {
  routeSource2.clear(true);

  for (let i in refLineList) {
    refLineList2.push(CenterLinePoint);

    refLineList2.push(refLineList[i]);
    polyline2 = new ol.geom.LineString(refLineList2);

    refLineList2 = [];
    lineFeature2 = new ol.Feature({
      geometry: polyline2,
    });

    lineStyles = new ol.style.Style({
      stroke: new ol.style.Stroke({
        color: "blue",
        width: "3",
        lineDash: [0.1, 5],
        opacity: 0.5,
      }),
    });

    lineFeature2.setStyle(lineStyles);
    routeFeature2 = [lineFeature2];
    refListList2 = [];

    routeSource2.addFeatures(routeFeature2);
  }

  refLineList = [];
}

function beforeDrawing(dataList, id) {
  $.each(dataList, function (idx) {
    if (dataList[idx].regDate == null) {
      return;
    }

    const fy = dataList[idx].regDate.substring(0, 4);
    const fm = dataList[idx].regDate.substring(4, 6);
    const fd = dataList[idx].regDate.substring(6, 8);
    const fh = dataList[idx].regDate.substring(8, 10);
    const fmi = dataList[idx].regDate.substring(10, 12);

    dataList[idx].formatDate = new Date(fy, fm, fd, fh, fmi);
  });
  dongDataList.push({ id: id, data: dataList });

  return dataList;
}

function drawChart(dataList, chartDiv) {
  let legendData = [];
  am4core.addLicense("CH205407412");

  // am4core.useTheme(am4themes_animated);
  const chart = am4core.create(chartDiv, am4charts.XYChart);

  chart.padding(0, 15, 0, 15);

  chart.leftAxesContainer.layout = "vertical";

  const dateAxis = chart.xAxes.push(new am4charts.DateAxis());
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

  const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
  valueAxis.tooltip.disabled = true;
  valueAxis.height = am4core.percent(50);
  valueAxis.zIndex = 1;
  dateAxis.minZoomCount = 5;
  valueAxis.renderer.baseGrid.disabled = true;
  valueAxis.renderer.inside = true;
  valueAxis.renderer.labels.template.verticalCenter = "bottom";
  valueAxis.renderer.labels.template.padding(2, 2, 2, 2);
  valueAxis.renderer.fontSize = "0.8em";
  valueAxis.renderer.gridContainer.background.fill = am4core.color("#fff000");
  valueAxis.renderer.gridContainer.background.fillOpacity = 0.05;

  const valueAxis2 = chart.yAxes.push(new am4charts.ValueAxis());
  valueAxis2.tooltip.disabled = true;
  valueAxis2.height = am4core.percent(50);
  valueAxis2.zIndex = 3;
  valueAxis2.marginTop = 30;
  valueAxis2.renderer.baseGrid.disabled = true;
  valueAxis2.renderer.inside = true;
  valueAxis2.renderer.labels.template.verticalCenter = "bottom";
  valueAxis2.renderer.labels.template.padding(2, 2, 2, 2);
  valueAxis2.renderer.fontSize = "0.8em";
  valueAxis2.renderer.gridContainer.background.fill = am4core.color("#000fff");
  valueAxis2.renderer.gridContainer.background.fillOpacity = 0.05;
  const scrollbarX = new am4charts.XYChartScrollbar();

  for (i = 0; i < dataList.length; i++) {
    const series = chart.series.push(new am4charts.LineSeries());
    series.data = dataList[i].data;
    series.dataFields.dateX = "formatDate";
    series.dataFields.valueY = "pm10Value";
    series.tooltipText = dataList[i].id + "  PM10 : {valueY.value}";

    series.name = dataList[i].id + " - PM10";
    series.fill = am4core.color(pm25ColorSet[i]);
    series.stroke = am4core.color(pm25ColorSet[i]);

    const series2 = chart.series.push(new am4charts.LineSeries());
    series2.data = dataList[i].data;
    series2.dataFields.dateX = "formatDate";
    series2.dataFields.valueY = "pm25Value";
    series2.yAxis = valueAxis2;
    series2.tooltipText = dataList[i].id + "  PM2.5 : {valueY.value}";

    series2.name = dataList[i].id + " - PM25";
    series2.fill = am4core.color(pm10ColorSet[i]);
    series2.stroke = am4core.color(pm10ColorSet[i]);
    scrollbarX.series.push(series);

    // legendData.push(
    //   { name: dataList[i].id + "pm10", fill: pm25ColorSet[i] },
    //   { name: dataList[i].id + "pm25", fill: pm10ColorSet[i] }
    // );
  }

  chart.legend = new am4charts.Legend();
  chart.legend.useDefaultMarker = true;
  // chart.legend.data = legendData;
  let markerTemplate = chart.legend.markers.template;
  markerTemplate.width = 10;
  markerTemplate.height = 10;
  chart.cursor = new am4charts.XYCursor();

  // scrollbarX.series.push(series);
  scrollbarX.marginBottom = 20;
  scrollbarX.scrollbarChart.xAxes.getIndex(0).minHeight = undefined;
  chart.scrollbarX = scrollbarX;
}

function dongHisData(dCode, dfname, mapType) {
  fieldDfname = dfname;
  const chartHtml =
    '<div class="card-header">' +
    '<h3 class="card-title"><a href="#"><strong data-toggle="modal" ' +
    "onclick=\"modalChart('" +
    dCode +
    "','DONG');\" " +
    'style="cursor: pointer;" data-target="#modal-chart">' +
    dfname +
    '</a></strong></h3></div><div class="card-body" id="pmListChart"><div style="height: 400px;" id="' +
    dfname +
    'chartDiv"></div></div>';

  if (mapType === "A") {
    $("#dongAirChartDivArea").html("").append(chartHtml);
  } else {
    $("#dongOaqChartDivArea").html("").append(chartHtml);
  }

  $.ajax({
    method: "GET",
    url: "/api/dong/ref/dong/history/dev",
    contentType: "application/json; charset=utf-8",
    async: false,
    data: {
      dcode: dCode,
    },
    success: function (d) {
      beforeDrawing(d.data, dfname);
    },
  });
}

function oaqHistory(uniqueIds, mapTypes) {
  for (const idx in uniqueIds) {
    const chartHtml =
      '<div class="card-header">' +
      '<h3 class="card-title"><a href="#"><strong data-toggle="modal" ' +
      "onclick=\"modalChart('" +
      uniqueIds[idx] +
      "','" +
      mapTypes[idx] +
      "');\" " +
      'style="cursor: pointer;" data-target="#modal-chart">' +
      uniqueIds[idx] +
      '</a></strong></h3></div><div class="card-body" id="pmListChart"><div style="height: 300px;" id="' +
      uniqueIds[idx] +
      'chartDiv"></div></div>';

    $("#oaqChartDivArea").append(chartHtml);

    $.ajax({
      method: "GET",
      url: "/api/dong/ref/oaq/history/dev",
      contentType: "application/json; charset=utf-8",
      async: false,
      data: {
        searchEquiInfoKey: uniqueIds[idx],
        mapType: mapTypes[idx],
      },
      success: function (d) {
        var oaqDataList = [];
        oaqDataList.push({
          id: uniqueIds[idx],
          data: beforeDrawing(d.data, uniqueIds[idx]),
        });
        drawChart(oaqDataList, uniqueIds[idx] + "chartDiv");
      },
    });
  }
}

function airHistory(uniqueIds) {
  $("#airChartDivArea").html("");

  for (const idx in uniqueIds) {
    const chartHtml =
      '<div class="card-header">' +
      '<h3 class="card-title"><a href="#"><strong data-toggle="modal" ' +
      "onclick=\"modalChart('" +
      uniqueIds[idx] +
      "','A');\" " +
      'style="cursor: pointer;" data-target="#modal-chart">' +
      uniqueIds[idx] +
      '</a></strong></h3></div><div class="card-body"><div  style="height: 300px;" id="' +
      uniqueIds[idx] +
      'chartDiv"></div></div>';

    $("#airChartDivArea").append(chartHtml);

    $.ajax({
      method: "GET",
      url: "/api/dong/ref/air/history/dev",
      contentType: "application/json; charset=utf-8",
      async: false,
      data: {
        searchAirCode: uniqueIds[idx],
      },
      success: function (d) {
        var airDataList = [];
        airDataList.push({
          id: uniqueIds[idx],
          data: beforeDrawing(d.data, uniqueIds[idx]),
        });
        drawChart(airDataList, uniqueIds[idx] + "chartDiv");
      },
    });
  }
}

function refCreatTb(
  dfname,
  centerLocation,
  refLen,
  deviceType,
  upDate,
  pm10Value,
  pm25Value,
  pm10Grade,
  pm25Grade
) {
  let refInfoHtml =
    "<div class='row'><strong>(" +
    deviceType +
    ") " +
    dfname +
    ", " +
    "중심지점 좌표 (" +
    centerLocation[0].toString().substring(0, 7) +
    ", " +
    centerLocation[1].toString().substring(0, 6) +
    ") " +
    upDate +
    "</strong></div>";
  +"<strong>미세먼지 : </strong>" +
    setColor("PM10", pm10Value, "value") +
    " / " +
    setColor("PM10", pm10Grade, "grade") +
    ", <strong>초미세먼지 : </strong>" +
    setColor("PM25", pm25Value, "value") +
    " / " +
    setColor("PM25", pm25Grade, "grade") +
    "" +
    "<div class=''><strong style='color: red; font-size: small;'>인근지점 관측망 -- 총 " +
    refLen +
    "개 (알고리즘에 사용한 지점 정보)</strong></div><hr/>";

  return refInfoHtml;
}

function choiceDongRow(
  rowDname,
  rowDfname,
  rowDcode,
  rowLon,
  rowLat,
  pm10Value,
  pm25Value,
  pm10Grade,
  pm25Grade,
  mapType,
  upDate
) {
  let refHtml;
  ids = [];
  const dongLocation = [rowLon, rowLat];

  const rome = ol.proj.fromLonLat(dongLocation);

  const duration = 2000;
  const zoom = mapView.getZoom();
  let parts = 2;
  let called = false;

  function callback(complete) {
    --parts;
    if (called) {
      return;
    }
    if (parts === 0 || !complete) {
      called = true;
      done(complete);
    }
  }

  mapView.animate(
    {
      center: rome,
      duration: duration,
    },
    callback
  );
  mapView.animate(
    {
      zoom: zoom - 1,
      duration: duration / 2,
    },
    {
      zoom: 12,
      duration: duration / 2,
    },
    callback
  );

  const updateDtStr = upDate.toString();
  const updateDtStrYear = updateDtStr.substring(0, 4);
  const updateDtStrMonth = updateDtStr.substring(4, 6);
  const updateDtStrDay = updateDtStr.substring(6, 8);
  const updateDtStrHour = updateDtStr.substring(8, 10);
  const updateDtStrMinute = updateDtStr.substring(10, 12);
  const trans_time =
    updateDtStrYear +
    "-" +
    updateDtStrMonth +
    "-" +
    updateDtStrDay +
    " " +
    updateDtStrHour +
    ":" +
    updateDtStrMinute;

  $("#dongAirChartDivArea").html("");
  $("#dongOaqChartDivArea").html("");

  $("#dtTableArea").attr("class", "col-6");
  $("#refInfoOaqArea").attr("class", "col-6");
  $("#refInfoAirArea").attr("class", "col-6");
  $("#refInfoOaqArea").show();
  $("#refInfoOaqDiv").html(
    "<strong style='color: red;' id='refInfoMsg'>데이터를 로딩중 입니다.</strong>"
  );
  $("#oaqChartDivArea").html("");
  $("#dName").html("(" + rowDname + ")");
  dongDataList = [];
  modalParam = [];
  dongHisData(rowDcode, rowDfname, mapType);
  modalParam.push({ unique: rowDcode, mapType: "DONG", dongName: rowDfname });

  if (mapType === "O" || mapType === "D") {
    $.ajax({
      method: "GET",
      url: "/api/dong/search/ref/dev",
      contentType: "application/json; charset=utf-8",
      data: {
        searchEquiType: "O",
        searchDongCode: rowDcode,
      },
      success: function (d) {
        const dataList = d.refData;
        const uniqueIds = [];
        const mapTypes = [];

        refHtml = refCreatTb(
          rowDfname,
          dongLocation,
          dataList.length,
          "OAQ",
          trans_time,
          pm10Value,
          pm25Value,
          pm10Grade,
          pm25Grade
        );

        $.each(dataList, function (idx, qq) {
          refLineList.push(
            ol.proj.transform(
              [qq.refLon, qq.refLat],
              "EPSG:4326",
              "EPSG:900913"
            )
          );

          uniqueIds.push(qq.mapCode);
          mapTypes.push(qq.mapType);
          modalParam.push({ unique: qq.mapCode, mapType: qq.mapType });

          refHtml +=
            "<div><strong>" +
            qq.mapCode +
            " : " +
            ", (" +
            qq.refLon +
            ", " +
            qq.refLat +
            "), " +
            ChangeMeter(qq.distance) +
            "" +
            transTime(qq.refUpDate, 1) +
            "</strong></div>" +
            "<strong>미세먼지 : </strong>" +
            setColor("PM10", qq.refPm10Value, "value") +
            " / " +
            setColor("PM10", qq.refPm10Grade, "grade") +
            ", <strong>초미세먼지 : </strong>" +
            setColor("PM25", qq.refPm25Value, "value") +
            " / " +
            setColor("PM25", qq.refPm25Grade, "grade") +
            "<hr/>";
        });

        if (dataList.length !== 0) {
          refMapLineDraw(rowDname);
        }

        oaqEquiInfoShow();
        $("#refInfoOaqDiv").html(refHtml);
        oaqHistory(uniqueIds, mapTypes);
        drawChart(dongDataList, rowDfname + "chartDiv");

        choiceGeo(
          rowDname,
          rowDcode,
          rowLon,
          rowLat,
          pm10Grade,
          pm25Grade,
          pm10Value,
          pm25Value
        );
      },
    });
  } else if (mapType === "A") {
    $.ajax({
      method: "GET",
      async: false,
      url: "/api/dong/search/ref/dev",
      contentType: "application/json; charset=utf-8",
      data: {
        searchEquiType: "A",
        searchDongCode: rowDcode,
      },
      success: function (d) {
        const dataList = d.refData;
        const uniqueIds = [];

        refHtml = refCreatTb(
          rowDfname,
          dongLocation,
          dataList.length,
          "국가관측장비",
          trans_time,
          pm10Value,
          pm25Value,
          pm10Grade,
          pm25Grade
        );

        $.each(dataList, function (idx, qq) {
          refLineList.push(
            ol.proj.transform(
              [qq.refLon, qq.refLat],
              "EPSG:4326",
              "EPSG:900913"
            )
          );

          uniqueIds.push(qq.mapCode);
          modalParam.push({ unique: qq.mapCode, mapType: "A" });

          refHtml +=
            "<div><strong>" +
            qq.mapCode +
            " : " +
            qq.refName +
            ", (" +
            qq.refLon +
            ", " +
            qq.refLat +
            "), " +
            ChangeMeter(qq.distance) +
            " " +
            transTime(qq.refUpDate, 1) +
            "</strong></div>" +
            "<strong>미세먼지 : </strong>" +
            setColor("PM10", qq.refPm10Value, "value") +
            " / " +
            setColor("PM10", qq.refPm10Grade, "grade") +
            ", <strong>초미세먼지 : </strong>" +
            setColor("PM25", qq.refPm25Value, "value") +
            " / " +
            setColor("PM25", qq.refPm25Grade, "grade") +
            "<hr/>";
        });

        if (dataList.length !== 0) {
          refMapLineDraw(rowDname);
        }

        airEquiInfoShow();
        $("#refInfoAirDiv").html(refHtml);
        airHistory(uniqueIds);
        drawChart(dongDataList, rowDfname + "chartDiv");

        choiceGeo(
          rowDname,
          rowDcode,
          rowLon,
          rowLat,
          pm10Grade,
          pm25Grade,
          pm10Value,
          pm25Value
        );
      },
    });
  }
}

function DateToString(pDate) {
  const yyyy = pDate.getFullYear();
  const mm =
    pDate.getMonth() < 9 ? "0" + (pDate.getMonth() + 1) : pDate.getMonth() + 1;
  const dd = pDate.getDate() < 10 ? "0" + pDate.getDate() : pDate.getDate();
  const hh = pDate.getHours() < 10 ? "0" + pDate.getHours() : pDate.getHours();
  const min =
    pDate.getMinutes() < 10 ? "0" + pDate.getMinutes() : pDate.getMinutes();
  const ss =
    pDate.getSeconds() < 10 ? "0" + pDate.getSeconds() : pDate.getSeconds();

  return ""
    .concat(yyyy)
    .concat("-")
    .concat(mm)
    .concat("-")
    .concat(dd)
    .concat(" ")
    .concat(hh)
    .concat(":")
    .concat(min)
    .concat(":")
    .concat(ss);
}

function choiceGeo(
  rowDname,
  rowDcode,
  rowLon,
  rowLat,
  pm10Grade,
  pm25Grade,
  pm10Value,
  pm25Value
) {
  dongPm25CenterSource.clear();
  idx = 0;
  let pm25MarkerImg;
  let pm10MarkerImg;
  changeFlag = true;
  pm10ApiMarkers = [];
  pm25ApiMarkers = [];

  CenterLinePoint = ol.proj.transform(
    [rowLon, rowLat],
    "EPSG:4326",
    "EPSG:900913"
  );

  pm10MarkerImg = "/resources/share/img/air/me_icon" + pm10Grade + ".png";
  pm25MarkerImg = "/resources/share/img/air/me_icon" + pm25Grade + ".png";

  pm10ApiMarkers[idx] = new ol.Feature({
    geometry: new ol.geom.Point(0),
  });

  pm25ApiMarkers[idx] = new ol.Feature({
    geometry: new ol.geom.Point(0),
  });

  pm10ApiMarkers[idx].setStyle(
    new ol.style.Style({
      image: new ol.style.Icon({
        src: pm10MarkerImg,
      }),
    })
  );

  pm25ApiMarkers[idx].setStyle(
    new ol.style.Style({
      image: new ol.style.Icon({
        src: pm25MarkerImg,
      }),
    })
  );

  apiPosition[idx] = ol.proj.transform(
    [rowLon, rowLat],
    "EPSG:4326",
    "EPSG:900913"
  );
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

  pm25ApiMarkers[idx].pmValue =
    "PM2.5 : " + pm25Value + pmGradeValue(pm25Grade);
  pm10ApiMarkers[idx].pmValue = "PM10 : " + pm10Value + pmGradeValue(pm10Grade);

  refMapLineDraw2();

  dongPm10CenterSource.addFeatures(pm10ApiMarkers);
  dongPm25CenterSource.addFeatures(pm25ApiMarkers);
}

function pmGradeValue(grade) {
  let value;

  switch (grade) {
    case 1:
      value = " (좋음)";
      break;
    case 2:
      value = " (보통)";
      break;
    case 3:
      value = " (나쁨)";
      break;
    default:
      value = " (매우나쁨)";
  }

  return value;
}

function oaqData() {
  $.ajax({
    method: "GET",
    url: "/api/dong/geo/oaqEqui/dev",
    contentType: "application/json; charset=utf-8",
    success: function (d) {
      let pm25MarkerImg = [];
      let pm10MarkerImg = [];
      changeFlag = true;
      pm10ApiMarkers = [];
      pm25ApiMarkers = [];

      data = d.oaqEquiList;
      $.each(data, function (idx) {
        pm10MarkerImg =
          "/resources/share/img/air/0_icon" + data[idx].pm10Grade + ".png";
        pm25MarkerImg =
          "/resources/share/img/air/0_icon" + data[idx].pm25Grade + ".png";

        pm10ApiMarkers[idx] = new ol.Feature({
          geometry: new ol.geom.Point(0),
        });
        pm25ApiMarkers[idx] = new ol.Feature({
          geometry: new ol.geom.Point(0),
        });

        pm10ApiMarkers[idx].setStyle(
          new ol.style.Style({
            image: new ol.style.Icon({
              src: pm10MarkerImg,
            }),
          })
        );
        pm25ApiMarkers[idx].setStyle(
          new ol.style.Style({
            image: new ol.style.Icon({
              src: pm25MarkerImg,
            }),
          })
        );

        apiPosition[idx] = ol.proj.transform(
          [data[idx].lon, data[idx].lat],
          "EPSG:4326",
          "EPSG:900913"
        );

        pm10ApiMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));
        pm10ApiMarkers[idx].id = "kweather";
        pm10ApiMarkers[idx].serial = data[idx].oserial;
        pm10ApiMarkers[idx].pm10Grade = data[idx].pm10Grade;
        pm10ApiMarkers[idx].gubun = "pm10";
        pm10ApiMarkers[idx].pmGrade = data[idx].pm10Grade;
        pm10ApiMarkers[idx].pmValue = data[idx].pm10Value;

        pm25ApiMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));
        pm25ApiMarkers[idx].id = "kweather";
        pm25ApiMarkers[idx].serial = data[idx].oserial;
        pm25ApiMarkers[idx].pm25Grade = data[idx].pm25Grade;
        pm25ApiMarkers[idx].gubun = "pm25";
        pm25ApiMarkers[idx].pmGrade = data[idx].pm25Grade;
        pm25ApiMarkers[idx].pmValue = data[idx].pm25Value;
      });

      pm10VectorSource.addFeatures(pm10ApiMarkers);
      pm25VectorSource.addFeatures(pm25ApiMarkers);
    },
  });
}

function AirData() {
  $.ajax({
    method: "GET",
    async: false,
    url: "/api/dong/geo/airEqui/dev",
    contentType: "application/json; charset=utf-8",
    success: function (d) {
      let pm10MarkerImg = [];
      let pm25MarkerImg = [];
      changeFlag = true;
      pm25ApiMarkers = [];
      data = d.airEquiList;
      $.each(data, function (idx) {
        pm10MarkerImg =
          "/resources/share/img/air/1_icon" + data[idx].pm10Grade + ".png";
        pm25MarkerImg =
          "/resources/share/img/air/1_icon" + data[idx].pm25Grade + ".png";

        pm10ApiMarkers[idx] = new ol.Feature({
          geometry: new ol.geom.Point(0),
        });
        pm25ApiMarkers[idx] = new ol.Feature({
          geometry: new ol.geom.Point(0),
        });

        pm10ApiMarkers[idx].setStyle(
          new ol.style.Style({
            image: new ol.style.Icon({
              src: pm10MarkerImg,
            }),
          })
        );
        pm25ApiMarkers[idx].setStyle(
          new ol.style.Style({
            image: new ol.style.Icon({
              src: pm25MarkerImg,
            }),
          })
        );

        apiPosition[idx] = ol.proj.transform(
          [data[idx].lon, data[idx].lat],
          "EPSG:4326",
          "EPSG:900913"
        );

        pm10ApiMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));
        pm10ApiMarkers[idx].id = "Korea";
        pm10ApiMarkers[idx].aairCode = data[idx].aairCode;
        pm10ApiMarkers[idx].pm10Grade = data[idx].pm10Grade;
        pm10ApiMarkers[idx].gubun = "pm10";
        pm10ApiMarkers[idx].pmGrade = data[idx].pm10Grade;
        pm10ApiMarkers[idx].pmValue = data[idx].pm10Value;

        pm25ApiMarkers[idx].setGeometry(new ol.geom.Point(apiPosition[idx]));
        pm25ApiMarkers[idx].id = "Korea";
        pm25ApiMarkers[idx].aairCode = data[idx].aairCode;
        pm25ApiMarkers[idx].pm25Grade = data[idx].pm25Grade;
        pm25ApiMarkers[idx].gubun = "pm25";
        pm25ApiMarkers[idx].pmGrade = data[idx].pm25Grade;
        pm25ApiMarkers[idx].pmValue = data[idx].pm25Value;
      });

      pm10VectorSource.addFeatures(pm10ApiMarkers);
      pm25VectorSource.addFeatures(pm25ApiMarkers);
    },
  });
}

function initMap() {
  let popupTitle = "";
  let popupContent = "";

  mapView = new ol.View({
    zoom: 10,
  });

  const element = document.getElementById("popup");

  popup = new ol.Overlay({
    element: element,
    positioning: "bottom-center",
    stopEvent: false,
    offset: [0, -20],
  });

  map = new ol.Map({
    layers: [
      new ol.layer.Tile({
        source: new ol.source.XYZ({
          url: "http://mt.google.com/vt/lyrs=m&x={x}&y={y}&z={z}",
        }),
      }),
    ],
    target: "map",
    view: mapView,
  });

  map.on("click", function (evt) {
    const feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
      return feature;
    });

    $(element).popover("dispose");

    if (feature) {
      if (changeFlag) {
        $(element).popover("dispose");
        changeFlag = false;
      }

      coordinate = evt.coordinate;
      popup.setPosition(coordinate);

      switch (feature.id) {
        case "dongArea":
          popupTitle =
            feature.dname +
            " 중심좌표 <br/>(" +
            feature.lon +
            ")<br/>(" +
            feature.lat +
            ")";
          popupContent = "<strong>" + feature.pmValue + "</strong>";
          break;
        case "kweather":
          popupTitle = feature.serial;
          popupContent =
            "<strong>" +
            setColorOriginVersion(
              feature.gubun,
              feature.pmGrade,
              feature.pmValue,
              "grade"
            ) +
            '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
          break;
        case "Korea":
          popupTitle = feature.aairCode;
          popupContent =
            "<strong>" +
            setColorOriginVersion(
              feature.gubun,
              feature.pmGrade,
              feature.pmValue,
              "grade"
            ) +
            '</strong></span><a href="#" id="popup-closer" class="ol-popup-closer"></a>';
          break;
        default:
          popupTitle = "참조 영역";
          popupContent = "<strong>" + feature.dname + "</strong>";
          break;
      }

      $(element).attr("title", "<strong>" + popupTitle + "</strong>");
      $(element).popover({
        placement: "top",
        html: true,
        animation: false,
        content: popupContent,
      });
      $(element).popover("show");
    } else {
      $(element).popover("dispose");
    }

    $("#popup-closer").click(function () {
      $(element).popover("dispose");
    });
  });

  document.getElementById("pm25ViewBtn").click();
  map.on("pointermove", function (e) {
    if (e.dragging) {
      $(element).popover("dispose");
    }
  });

  mapView.setCenter(centerPosition);

  pm25VectorSource = new ol.source.Vector({
    features: pm25ApiMarkers,
  });

  pm10VectorSource = new ol.source.Vector({
    features: pm25ApiMarkers,
  });

  dongPm10CenterSource = new ol.source.Vector();
  dongPm25CenterSource = new ol.source.Vector();

  pm25VectorLayer = new ol.layer.Vector({
    source: pm25VectorSource,
  });

  pm10VectorLayer = new ol.layer.Vector({
    source: pm10VectorSource,
  });

  dongPm10CenterLayer = new ol.layer.Vector({
    source: dongPm10CenterSource,
  });

  dongPm25CenterLayer = new ol.layer.Vector({
    source: dongPm25CenterSource,
  });

  routeSource = new ol.source.Vector();

  routeSource2 = new ol.source.Vector();

  routeLayer = new ol.layer.Vector({
    source: routeSource,
  });

  routeLayer2 = new ol.layer.Vector({
    source: routeSource2,
  });

  map.addLayer(routeLayer);

  map.addLayer(routeLayer2);
  map.addLayer(pm25VectorLayer);
  map.addLayer(pm10VectorLayer);

  map.addLayer(dongPm25CenterLayer);
  map.addLayer(dongPm10CenterLayer);

  map.addOverlay(popup);

  oaqData();
  AirData();

  pm10VectorLayer.setVisible(false);
  dongPm10CenterLayer.setVisible(false);
}

function initDataTableCustom() {
  let searchDfname1 = $("#searchDfname1 option:selected").val();
  let searchDfname2 = $("#searchDfname2 option:selected").val();

  if (!searchDfname1) {
    searchDfname1 = 11;
  }

  if (!searchDfname2) {
    searchDfname2 = 110;
  }

  const table = $("#dustTable").DataTable({
    scrollCollapse: true,
    autoWidth: false,
    language: {
      emptyTable: "데이터가 없습니다.",
      lengthMenu: "페이지당 _MENU_ 개씩 보기",
      info: "현재 _START_ - _END_ / _TOTAL_건",
      infoEmpty: "데이터 없음",
      infoFiltered: "( _MAX_건의 데이터에서 필터링됨 )",
      search: "",
      zeroRecords: "일치하는 데이터가 없습니다.",
      loadingRecords: "로딩중...",
      processing: "잠시만 기다려 주세요.",
      paginate: {
        next: "다음",
        previous: "이전",
      },
    },
    destroy: true,
    processing: true,
    serverSide: false,
    ajax: {
      url: "/api/dong/search/sigungu/dev",
      type: "GET",
      data: function (param) {
        param.searchSdcode = searchDfname1;
        param.searchSggcode = searchDfname2;
        param.searchDongcode = $("#searchDfname3 option:selected").val();
      },
    },
    columns: [
      { data: "dname" },
      { data: "pm10Value" },
      { data: "pm25Value" },
      { data: "pm10Grade" },
      { data: "pm25Grade" },
    ],
    columnDefs: [
      {
        targets: 0,
        render: function (data, type, full) {
          let innerHtml;
          const dfname = full.dfname;
          const dfnameArr = dfname.split(" ");
          innerHtml =
            "<a href='#'><strong " +
            "onclick='choiceDongRow(\"" +
            full.dname +
            '"' +
            ',"' +
            full.dfname +
            '"' +
            ',"' +
            full.dcode +
            '", ' +
            full.lon +
            ", " +
            full.lat +
            ", " +
            full.pm10Value +
            ", " +
            full.pm25Value +
            ", " +
            full.pm10Grade +
            ", " +
            full.pm25Grade +
            ', "' +
            full.mapType +
            '","' +
            full.upDate +
            "\");'>" +
            data +
            "</strong><br/><font style='color:black;'>(" +
            dfnameArr[0].substring(0, 2) +
            ")</font></a>";

          return innerHtml;
        },
      },
      {
        targets: 1,
        render: function (data, type, full) {
          let pmValstr = "";
          const pm10Val = full.pm10Value;
          const pm25Val = full.pm25Value;

          pmValstr +=
            "<div><strong>PM10</strong> : <strong>" +
            setColor("PM10", pm10Val, "value") +
            "</strong></div>" +
            "<div><strong>PM2.5</strong> : <strong>" +
            setColor("PM25", pm25Val, "value") +
            "</strong></div>";
          return pmValstr;
        },
      },
      {
        targets: 2,
        render: function (data, type, full) {
          let pmGradeStr = "";

          pmGradeStr +=
            "<div><strong>PM10</strong> : <strong>" +
            setColor("PM10", full.pm10Grade, "grade") +
            "</strong></div>" +
            "</div><strong>PM2.5</strong> : <strong>" +
            setColor("PM25", full.pm25Grade, "grade") +
            "</strong></div>";

          return pmGradeStr;
        },
      },
      {
        targets: 3,
        render: function (data, type, full) {
          return transTime(full.upDate, 0);
        },
      },
      {
        targets: 4,
        render: function (data, type, full) {
          const lat = full.lat;
          const lon = full.lon;
          const latStr = lat.toString();
          const lonStr = lon.toString();
          const latSubStr = latStr.substring(0, 8);
          const lonSubStr = lonStr.substring(0, 9);

          return latSubStr + "<br/>" + lonSubStr;
        },
      },
    ],
  });
  $(".dataTables_filter").hide();
}

function changeDo() {
  let html = "";

  $.ajax({
    url: "/api/dong/siList/dev",
    type: "GET",
    success: function (param) {
      for (let i = 0; i < param.data.length; i++) {
        html +=
          "<option value='" +
          param.data[i].sdcode +
          "'>" +
          param.data[i].dname +
          "</option>";
      }

      $("#searchDfname1").html(html);
    },
  });
}

function ChangeSi(sdcode) {
  let si_html = "";

  $.ajax({
    url: "/api/dong/guList/dev",
    type: "GET",
    data: "searchSdcode=" + sdcode,
    success: function (param) {
      for (let i = 0; i < param.data.length; i++) {
        si_html +=
          "<option value='" +
          param.data[i].sggcode +
          "'>" +
          param.data[i].dname +
          "</option>";
      }

      $("#searchDfname2").html(si_html);
      ChangeDong(sdcode, param.data[0].sggcode);
    },
  });
}

function ChangeDong(sdcode, sggcode) {
  let dong_html = "";

  $.ajax({
    url: "/api/dong/dongList/dev",
    type: "GET",
    data: "searchSdcode=" + sdcode + "&searchSggcode=" + sggcode,
    success: function (param) {
      dong_html += "<option value=''>전 체</option>";

      for (let i = 0; i < param.data.length; i++) {
        dong_html +=
          "<option value='" +
          param.data[i].dcode +
          "'>" +
          param.data[i].dname +
          "</option>";
      }

      $("#searchDfname3").html(dong_html);
      initDataTableCustom();
      $("#searchDname").val("");
    },
  });
}

function selectDong(dongName) {
  searchDong(dongName);
}

function SearchEquiCnt(txt) {
  let cityName;

  $("#n_ALL").removeClass("list-box-color");
  $("#n_SE").removeClass("list-box-color");
  $("#n_BS").removeClass("list-box-color");
  $("#n_DG").removeClass("list-box-color");
  $("#n_IC").removeClass("list-box-color");
  $("#n_GJ").removeClass("list-box-color");
  $("#n_DJ").removeClass("list-box-color");
  $("#n_US").removeClass("list-box-color");
  $("#n_GG").removeClass("list-box-color");
  $("#n_GW").removeClass("list-box-color");
  $("#n_CB").removeClass("list-box-color");
  $("#n_CN").removeClass("list-box-color");
  $("#n_JB").removeClass("list-box-color");
  $("#n_JN").removeClass("list-box-color");
  $("#n_GB").removeClass("list-box-color");
  $("#n_GN").removeClass("list-box-color");
  $("#n_JJ").removeClass("list-box-color");
  $("#n_SJ").removeClass("list-box-color");

  if (txt === "n_ALL") {
    cityName = "";
  } else {
    cityName = $("#" + txt).text();
  }
  $("#" + txt).addClass("list-box-color");

  if (txt !== "n_ALL") {
    $.ajax({
      url: "/api/dong/cnt/airEqui/dev",
      type: "GET",
      data: "searchValue=" + cityName,
      success: function (param) {
        let n_total = 0;

        for (let i = 0; i < param.airCntdata.length; i++) {
          n_total = n_total + param.airCntdata[i].cnt;
        }

        $("#nation_cnt").text(n_total);
      },
    });

    if (txt === "n_ALL" || txt === "n_SE") {
      $.ajax({
        url: "/api/dong/cnt/oaqEqui/dev",
        type: "GET",
        data: "searchValue=" + cityName,
        success: function (param) {
          $("#kweather_cnt").text(param.oaqCntData[0].cnt);
        },
      });
    } else {
      $("#kweather_cnt").text("0");
    }
  } else {
    $.ajax({
      url: "/api/dong/cnt/airEqui/dev",
      type: "GET",
      data: "searchValue=" + cityName,
      success: function (param) {
        let n_total = 0;
        for (let i = 0; i < param.airCntdata.length; i++) {
          n_total = n_total + param.airCntdata[i].cnt;
        }
        $("#nation_cnt").text(n_total);
      },
    });

    $.ajax({
      url: "/api/dong/cnt/oaqEqui/dev",
      type: "GET",
      data: "searchValue=" + cityName,
      success: function (param) {
        $("#kweather_cnt").text(param.oaqCntData[0].cnt);
      },
    });
  }
}

function searchDong(searchDname) {
  const table = $("#dustTable").DataTable({
    scrollCollapse: true,
    autoWidth: false,
    language: {
      emptyTable: "데이터가 없습니다.",
      lengthMenu: "페이지당 _MENU_ 개씩 보기",
      info: "현재 _START_ - _END_ / _TOTAL_건",
      infoEmpty: "데이터 없음",
      infoFiltered: "( _MAX_건의 데이터에서 필터링됨 )",
      search: "",
      zeroRecords: "일치하는 데이터가 없습니다.",
      loadingRecords: "로딩중...",
      processing: "잠시만 기다려 주세요.",
      paginate: {
        next: "다음",
        previous: "이전",
      },
    },
    destroy: true,
    processing: true,
    serverSide: false,
    ajax: {
      url: "/api/dong/search/dong/dev",
      type: "GET",
      data: function (param) {
        param.searchDname = searchDname;
      },
    },
    columns: [
      { data: "dname" },
      { data: "pm10Value" },
      { data: "pm25Value" },
      { data: "pm10Grade" },
      { data: "pm25Grade" },
    ],
    columnDefs: [
      {
        targets: 0,
        render: function (data, type, full) {
          let innerHtml;
          const dfname = full.dfname;
          const dfnameArr = dfname.split(" ");
          innerHtml =
            "<a href='#'><strong " +
            "onclick='choiceDongRow(\"" +
            full.dname +
            '"' +
            ',"' +
            full.dfname +
            '"' +
            ',"' +
            full.dcode +
            '", ' +
            full.lon +
            ", " +
            full.lat +
            ", " +
            full.pm10Value +
            ", " +
            full.pm25Value +
            ", " +
            full.pm10Grade +
            ", " +
            full.pm25Grade +
            ', "' +
            full.mapType +
            '", "' +
            full.upDate +
            "\");'>" +
            data +
            "</strong><br/><font style='color:black;'>(" +
            dfnameArr[0].substring(0, 2) +
            ")</font></a>";

          return innerHtml;
        },
      },
      {
        targets: 1,
        render: function (data, type, full) {
          let pmValstr = "";
          const pm10Val = full.pm10Value;
          const pm25Val = full.pm25Value;

          pmValstr +=
            "<div><strong>PM10</strong> : <strong>" +
            setColor("PM10", pm10Val, "value") +
            "</strong></div>" +
            "<div><strong>PM2.5</strong> : <strong>" +
            setColor("PM25", pm25Val, "value") +
            "</strong></div>";
          return pmValstr;
        },
      },
      {
        targets: 2,
        render: function (data, type, full) {
          let pmGradeStr = "";

          pmGradeStr +=
            "<div><strong>PM10</strong> : <strong>" +
            setColor("PM10", full.pm10Grade, "grade") +
            "</strong></div>" +
            "</div><strong>PM2.5</strong> : <strong>" +
            setColor("PM25", full.pm25Grade, "grade") +
            "</strong></div>";

          return pmGradeStr;
        },
      },
      {
        targets: 3,
        render: function (data, type, full) {
          return transTime(full.upDate, 0);
        },
      },
      {
        targets: 4,
        render: function (data, type, full) {
          const lat = full.lat;
          const lon = full.lon;
          const latStr = lat.toString();
          const lonStr = lon.toString();
          const latSubStr = latStr.substring(0, 8);
          const lonSubStr = lonStr.substring(0, 9);

          return latSubStr + "<br/>" + lonSubStr;
        },
      },
    ],
  });

  $(".dataTables_filter").hide();
}

function setColor(pm, value, type) {
  let html;
  let sub = 1;

  if (pm !== "PM10") {
    sub = 2;
  }
  if (type !== "grade") {
    if (value < 31 / sub) {
      html = "<font style='color:#44C7F5'>" + value + "</font>";
    } else if (value < 51 / sub) {
      html = "<font style='color:#8DD538'>" + value + "</font>";
    } else if (value < 101 / sub) {
      html = "<font style='color:#F97B47'>" + value + "</font>";
    } else {
      html = "<font style='color:#F93F3E'>" + value + "</font>";
    }
  } else {
    if (value == 1) {
      html = "<font style='color:#44C7F5'>좋음</font>";
    } else if (value == 2) {
      html = "<font style='color:#8DD538'>보통</font>";
    } else if (value == 3) {
      html = "<font style='color:#F97B47'>나쁨</font>";
    } else {
      html = "<font style='color:#F93F3E'>매우나쁨</font>";
    }
  }
  return html;
}

function setColorOriginVersion(type, Grade, value) {
  let GradeColorHtml;
  let pmType = 10;

  if (type !== "pm10") {
    pmType = 25;
  }

  switch (Grade) {
    case "1":
      GradeColorHtml = "<span>PM" + pmType + " : " + value + " (좋음)";
      break;
    case "2":
      GradeColorHtml = "<span>PM" + pmType + " : " + value + " (보통)";
      break;
    case "3":
      GradeColorHtml = "<span>PM" + pmType + " : " + value + " (나쁨)";
      break;
    default:
      GradeColorHtml = "<span>PM" + pmType + " : " + value + " (매우나쁨)";
      break;
  }

  return GradeColorHtml;
}

function transTime(dt, type) {
  const dateDtStr = dt.toString();
  const dateDtStrYear = dateDtStr.substring(0, 4);
  const dateDtStrMonth = dateDtStr.substring(4, 6);
  const dateDtStrDay = dateDtStr.substring(6, 8);
  const dateDtStrHour = dateDtStr.substring(8, 10);
  const dateDtStrMinute = dateDtStr.substring(10, 12);

  if (type === 0) {
    return (
      dateDtStrYear +
      "-" +
      dateDtStrMonth +
      "-" +
      dateDtStrDay +
      "<br/>" +
      dateDtStrHour +
      ":" +
      dateDtStrMinute
    );
  } else {
    return (
      dateDtStrYear +
      "-" +
      dateDtStrMonth +
      "-" +
      dateDtStrDay +
      " " +
      dateDtStrHour +
      ":" +
      dateDtStrMinute
    );
  }
}

function ChangeMeter(val) {
  let MeterStr;
  const MeterVal = val / 1000;
  MeterStr = MeterVal.toFixed(1) + "KM, ";

  return MeterStr;
}

$().ready(function () {
  initMap();
  changeDo();
  ChangeSi(11);
  ChangeDong(11, 110);
  SearchEquiCnt("n_ALL");

  $("#refInfoOaqArea").hide();
  $("#refInfoAirArea").hide();

  $(".dataTables_filter").hide();

  $("#sch_btn").click(function () {
    const searchDname = $("#searchDname").val();

    if (searchDname.length < 2) {
      alert("최소 2글자 이상 입력해주세요. ex)00동");
      $("#searchDname").focus();
      return false;
    }

    searchDong(searchDname);
  });

  $("#searchDfname1").change(function () {
    const sdcode = $(this).val();
    ChangeSi(sdcode);
  });

  $("#searchDfname2").change(function () {
    const sdcode = $("#searchDfname1").val();
    const sggcode = $(this).val();

    ChangeDong(sdcode, sggcode);
  });

  $("#searchDfname3").change(function () {
    initDataTableCustom();
  });

  $("#searchDname").keypress(function (e) {
    if (e.keyCode == 13) {
      const searchDname = $("#searchDname").val();
      if (searchDname.length < 2) {
        alert("최소 2글자 이상 입력해주세요. ex)00동");
        $("#searchDname").focus();
        return false;
      }

      searchDong(searchDname);
    }
  });

  $("#pm10ViewBtn").click(function () {
    $("#pm25ViewBtn").removeClass("list-box-color").addClass("list-box-white");
    $("#pm10ViewBtn").addClass("list-box-color").removeClass("list-box-white");

    pm10VectorLayer.setVisible(true);
    pm25VectorLayer.setVisible(false);
    dongPm10CenterLayer.setVisible(true);
    dongPm25CenterLayer.setVisible(false);
  });

  $("#pm25ViewBtn").click(function () {
    $("#pm10ViewBtn").removeClass("list-box-color").addClass("list-box-white");
    $("#pm25ViewBtn").addClass("list-box-color").removeClass("list-box-white");

    pm10VectorLayer.setVisible(false);
    pm25VectorLayer.setVisible(true);
    dongPm10CenterLayer.setVisible(false);
    dongPm25CenterLayer.setVisible(true);
  });
});
