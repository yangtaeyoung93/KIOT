let hisApiData = [];
let columsData = [];
$().ready(function () {
  let elemetsData;
  const collectionColum = [];

  $("#startDt").val("");
  $("#endDt").val("");
  if (deviceType === "IAQ") {
    $("#lastOptVal").val("cici");
  } else {
    $("#lastOptVal").val("coci");
  }

  if (!$("#startDt").val()) {
    const now_date = new Date();
    const now_year = now_date.getFullYear();
    let now_month = now_date.getMonth() + 1;
    let now_day = now_date.getDate();

    if (now_month < 10) {
      now_month = "0" + now_month;
    }

    if (now_day < 10) {
      now_day = "0" + now_day;
    }

    $("#startDt").val(now_year + "/" + now_month + "/" + now_day);
  }

  if (!$("#endDt").val()) {
    const now_date = new Date();
    const now_year = now_date.getFullYear();
    let now_month = now_date.getMonth() + 1;
    let now_day = now_date.getDate();

    if (now_month < 10) {
      now_month = "0" + now_month;
    }
    if (now_day < 10) {
      now_day = "0" + now_day;
    }

    $("#endDt").val(now_year + "/" + now_month + "/" + now_day);
  }

  $.ajax({
    method: "GET",
    async: false,
    url: "/api/datacenter/elements?serial=" + serialNum,
    success: function (d) {
      elemetsData = d.data;
      if (elemetsData.length !== 0) {
        columsData.push({
          "data": "formatTimestamp"
        });

        for (let key in elemetsData) {
          columsData.push({
            "data": elemetsData[key].engName
          });
          collectionColum.push(elemetsData[key].engName);
        }

        if (deviceType === "IAQ") {
          columsData.push({
            "data": "cici"
          });
        } else {
          columsData.push({
            "data": "coci"
          });
        }
        columsData.push({
          "data": "cmd_p"
        });
        columsData.push({
          "data": "cmd_w"
        });
        columsData.push({
          "data": "cmd_m"
        });

      }

      if (collectionColum.length !== 0) {
        tableTrDraw(elemetsData, d, collectionColum);
      }

      selectHistory(serialNum, parentSpaceName, spaceName, productDt,
          stationName, standard, deviceType);
    }
  });

  $("#modalSearchBtn").click(function () {
    const serialNum = $("#p_h_serialNum").val();
    const parentSpaceName = $("#p_h_parentSpaceName").val();
    const spaceName = $("#p_h_spaceName").val();
    const productDt = $("#p_h_productDt").val();
    const stationName = $("#p_h_stationName").val();
    const standard = $("#searchStandard option:selected").val();

    selectHistory(serialNum, parentSpaceName, spaceName, productDt, stationName,
        standard, deviceType);
  })

  const rangeDate = 31;
  let setSdate, setEdate;

  $("#startDt").datepicker({
    dateFormat: 'yy/mm/dd',
    onSelect: function (selectDate) {
      let stxt = selectDate.split("/");
      stxt[1] = stxt[1] - 1;
      const sdate = new Date(stxt[0], stxt[1], stxt[2]);
      const edate = new Date(stxt[0], stxt[1], stxt[2]);
      edate.setDate(sdate.getDate() + rangeDate);

      $('#endDt').datepicker('option', {
        minDate: selectDate,
        beforeShow: function () {
          $("#endDt").datepicker("option", "maxDate", edate);
          setSdate = selectDate;
        }
      });
    }
  })

  $("#endDt").datepicker({
    dateFormat: 'yy/mm/dd',
    onSelect: function (selectDate) {
      setEdate = selectDate;
    }
  })

  $('#chartTypes').click(function () {
    chgChartData();
  })

  $('#modalDownloadBtn').click(function () {
    $('.excelDownBtn').trigger('click');
  })
})

function selectHistory(serialNum, parentSpaceName, spaceName, productDt,
    stationName, standard, deviceType) {

  let startTime = $("#startDt").val();
  let endTime = $("#endDt").val();
  const r_deviceType = "IAQ";

  if (!startTime && !endTime) {
    const today = new Date();
    const year = today.getFullYear();
    let month = today.getMonth() + 1;
    let day = today.getDate();

    if (month < 10) {
      month = "0" + month;
    }

    if (day < 10) {
      day = "0" + day;
    }

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

  if (!standard) {
    standard = "sum";
  }

  let obj = {
    deviceType: "iaq",
    serial: $("#p_h_serialNum").val(),
    startTime: startTime,
    endTime: endTime,
    standard: standard,
    connect: "0"
  }

  $.ajax({
    method: "GET",
    url: "/api/collection/history",
    data: obj,
    success: function (d) {
      const apiStandard = $("#searchStandard option:selected").val();
      if ((r_deviceType === "dot") || (apiStandard !== "sum")) {
        hisApiData = d.data;
      } else {
        hisApiData = d.data;
      }

      for (let i = 0; i < columsData.length; i++) {
        const key = columsData[i].data;
        for (let j = 0; j < hisApiData.length; j++) {
          if (hisApiData[j][key] == null) {
            hisApiData[j][key] = null;
          }
        }
      }

      for (let i in hisApiData) {
        let convertTime = "";
        let convertCmdP = "";
        let convertCmdW = "";
        let convertCmdM = "";

        if (hisApiData[i].timestamp == null) {
          convertTime = "";
        } else {
          const date = new Date(hisApiData[i].timestamp * 1000);
          const year = date.getFullYear();
          let month = date.getMonth() + 1;
          let day = date.getDate();

          let hours = date.getHours();
          let minutes = date.getMinutes();
          let seconds = date.getSeconds();

          if (month < 10) {
            month = '0' + month;
          }
          if (day < 10) {
            day = '0' + day;
          }
          if (hours < 10) {
            hours = '0' + hours;
          }
          if (minutes < 10) {
            minutes = '0' + minutes;
          }
          if (seconds < 10) {
            seconds = '0' + seconds;
          }

          convertTime = year + "-" + month + "-" + day + "\n" + hours
              + ":" + minutes;
        }

        if (hisApiData[i].cmd_p != null) {
          convertCmdP = parseInt(hisApiData[i].cmd_p);
        }

        if (hisApiData[i].cmd_w != null) {
          convertCmdW = parseInt(hisApiData[i].cmd_w);
        }
        if (hisApiData[i].cmd_m != null) {
          switch(parseInt(hisApiData[i].cmd_m)) {
            case 1:
              convertCmdM = "1 (환기모드)"
              break;
            case 2:
              convertCmdM = "2 (공기 청정모드)"
              break;
            case 3:
              convertCmdM = "3 (바이패스모드)"
              break;
            default:
              convertCmdM = ""
              break;
          }
        }

        hisApiData[i].convertTime = convertTime;
        hisApiData[i].ci = hisApiData[i].coci;
        hisApiData[i].cmd_p = convertCmdP;
        hisApiData[i].cmd_w = convertCmdW;
        hisApiData[i].cmd_m = convertCmdM;
      }

      viewData(serialNum, parentSpaceName, spaceName, productDt, stationName,
          standard, deviceType);
    }
  });
}

function viewData(serialNum, parentSpaceName, spaceName, productDt, stationName,
    standard) {
  let obj;
  let startTime = $("#startDt").val();
  let endTime = $("#endDt").val();

  if (!startTime && !endTime) {
    let today = new Date();
    let year = today.getFullYear();
    let month = today.getMonth() + 1;
    let day = today.getDate() - 1;

    if (month < 10) {
      month = "0" + month;
    }
    if (day < 10) {
      day = "0" + day;
    }

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

  if (!standard) {
    standard = "sum";
  }

  obj = {
    deviceType: "iaq",
    serial: $("#p_h_serialNum").val(),
    startTime: startTime,
    endTime: endTime,
    standard: standard,
    connect: "1"
  }

  const h_serialNum = $("#p_h_serialNum").val();
  const h_parentSpaceName = $("#p_h_parentSpaceName").val();
  const h_spaceName = $("#p_h_spaceName").val();
  const h_productDt = $("#p_h_productDt").val();
  const h_stationName = $("#p_h_stationName").val();
  const h_ventSerial_split = ventSerial.split(",");
  let spaceStr = "";
  let h_ventStr = "";
  let h_ventBR = "<br/>";
  let h_ventCnt = 0;
  for (let i = 0; i < h_ventSerial_split.length; i++) {
    h_ventCnt++;
    if (i === h_ventSerial_split.length - 1) {
      h_ventBR = "";
    }
    h_ventStr += "<span class='font-weight-bold'>VENT" + h_ventCnt + " : "
        + h_ventSerial_split[i] + "</span>" + h_ventBR;
  }

  $("#popVentTxt").html(h_ventStr);

  if (!h_parentSpaceName && !h_spaceName) {
    spaceStr = "공간카테고리 : 없음";
  } else if (h_parentSpaceName && !h_spaceName) {
    spaceStr = "공간카테고리 : " + h_parentSpaceName;
  } else if (!h_parentSpaceName && h_spaceName) {
    spaceStr = "공간카테고리 : " + h_spaceName;
  }

  $("#txtSerialNum").text("시리얼번호 : " + h_serialNum);
  $("#txtSpace").text(spaceStr);
  $("#txtProductDt").text("등록일자 : " + h_productDt);
  $("#txtStationName").text(h_stationName + "의 ");

  const table = $('#popTable').DataTable({
    scrollCollapse: true,
    autoWidth: false,
    language: {
      "emptyTable": "데이터가 없습니다.",
      "lengthMenu": "페이지당 _MENU_ 개씩 보기",
      "info": "현재 _START_ - _END_ / _TOTAL_건",
      "infoEmpty": "데이터 없음",
      "infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
      "search": "",
      "zeroRecords": "일치하는 데이터가 없습니다.",
      "loadingRecords": "로딩중...",
      "processing": "잠시만 기다려 주세요.",
      "paginate": {
        "next": "다음",
        "previous": "이전"
      }
    },
    dom: '<"top"Blf>rt<"bottom"ip><"clear">',
    responsive: false,
    buttons: [{
      extend: 'csv',
      charset: 'UTF-8',
      text: '엑셀 다운로드',
      footer: false,
      bom: true,
      filename: h_serialNum + "_" + $("#startDt").val() + "_" +
          $("#endDt").val() + "_download",
      className: 'btn-primary btn excelDownBtn'
    }],
    destroy: true,
    processing: true,
    serverSide: false,
    data: hisApiData,
    columns: columsData,
    initComplete: function () {
      chgChartData();
    },
    createdRow: function (row, data) {
      $(row).attr('id', 'row-' + data.serial);

      if (data.receiveFlag === false) {
        $(row).attr('style', 'color: #929498 !important');
      }
    }
  });

  $("#popTable_filter").hide();
  $($("#tableTitle")).insertBefore("#popTable_length");
  $("#popTable_length").css("display", "inline-block")
  .css("margin-left", "20px");
  $("#modalDownloadBtn").insertAfter("#popTable_filter");
  $("#popTable_wrapper .top").first().css("border-bottom", "1px solid #E5E5E5")
  .css("padding-bottom", "15px");
  $('.excelDownBtn').css("float", "right");
}

function timeChartCreate(chartDiv, hisData, options) {
  am4core.addLicense("CH205407412");
  // am4core.useTheme(am4themes_animated);

  const chart = am4core.create(chartDiv, am4charts.XYChart);
  chart.data = hisData;
  chart.cursor = new am4charts.XYCursor();
  chart.cursor.behavior = "panXY";
  chart.scrollbarY = new am4core.Scrollbar();
  chart.scrollbarY.parent = chart.leftAxesContainer;
  chart.scrollbarY.toBack();
  chart.scrollbarX = new am4charts.XYChartScrollbar();
  chart.scrollbarX.parent = chart.topAxesContainer;

  const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
  categoryAxis.dataFields.category = "convertTime";
  categoryAxis.renderer.opposite = false;

  for (let idx in options) {
    createSeries(options[idx]);
  }

  function chgOptionNameFunc(name) {
    let chgName;

    if (name === "pm10") {
      chgName = "미세먼지 (㎍/㎥)";
    } else if (name === "pm25") {
      chgName = "초미세먼지 (㎍/㎥)";
    } else if (name === "co2") {
      chgName = "이산화탄소 (ppm)";
    } else if (name === "voc") {
      chgName = "휘발성유기화합물 (ppb)";
    } else if (name === "noise") {
      chgName = "소음 (dB)";
    } else if (name === "temp") {
      chgName = "온도 (℃ )";
    } else if (name === "humi") {
      chgName = "습도 (%)";
    } else if (name === "cici") {
      chgName = "통합지수 (cici)";
    } else if (name === "cmd_p") {
      chgName = "전원";
    } else if (name === "cmd_w") {
      chgName = "풍향";
    } else if (name === "cmd_m") {
      chgName = "운전모드";
    } else {
      chgName = name;
    }

    return chgName;
  }

  function createSeries(name) {
    const chgName = chgOptionNameFunc(name);

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.renderer.inversed = false;
    valueAxis.title.text = chgName;
    valueAxis.renderer.minLabelPosition = 0.01;
    valueAxis.connect = false;
    valueAxis.renderer.line.strokeOpacity = 1;
    valueAxis.renderer.line.strokeWidth = 2;

    const interfaceColors = new am4core.InterfaceColorSet();

    const series = chart.series.push(new am4charts.LineSeries());
    series.dataFields.valueY = name;
    series.dataFields.categoryX = "convertTime";
    series.yAxis = valueAxis;
    series.name = chgName;
    series.tensionX = 0.8;
    series.showOnInit = true;

    const segment = series.segments.template;
    segment.interactionsEnabled = true;

    const hoverState = segment.states.create("hover");
    hoverState.properties.strokeWidth = 3;

    const dimmed = segment.states.create("dimmed");
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
    series.connect = false;

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

function formatData(oriData) {
  if (oriData.length === 0) {
    return oriData;
  }

  let tsTime;
  const forData = [];
  const tsFormatTime = oriData[0].formatTimestamp;

  if (tsFormatTime.substring(11) === "00:00:00") {
    tsTime = oriData[0].timestamp;
  } else {
    tsTime = Date.parse(tsFormatTime.substring(0, 11) + "00:00:00") / 1000;
  }

  for (let i in oriData) {
    if (oriData[i].timestamp > tsTime) {
      let loFlag = true;
      while (loFlag) {
        const obj = {
          timestamp: tsTime,
          formatTimestamp: tsToDate(tsTime),
          tm: ""
        }
        forData.push(obj);
        tsTime = parseInt(tsTime) + 60;
        if (oriData[i].timestamp === tsTime) {
          loFlag = false;
        }
      }
    }

    forData.push(oriData[i]);
    tsTime = parseInt(tsTime) + 60;
  }

  return forData;
}

function tsToDate(tsTime) {
  const date = new Date(tsTime * 1000);
  const year = date.getFullYear();
  let month = date.getMonth() + 1;
  let day = date.getDate();

  let hours = date.getHours();
  let minutes = date.getMinutes();
  let seconds = date.getSeconds();

  if (month < 10) {
    month = '0' + month;
  }
  if (day < 10) {
    day = '0' + day;
  }
  if (hours < 10) {
    hours = '0' + hours;
  }
  if (minutes < 10) {
    minutes = '0' + minutes;
  }
  if (seconds < 10) {
    seconds = '0' + seconds;
  }

  let convertTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes
      + ":" + seconds;

  return convertTime;
}

function tableTrDraw(datas, rawDatas, colums) {
  let ciType;

  if (deviceType === "IAQ") {
    ciType = "cici";
  } else {
    ciType = "coci";
  }

  let trHeadHTML = "";
  let trBodyHTML = "";
  let chartTypesHTML = "";

  trHeadHTML += "<th style='width: 200px;' class='bgGray1'>데이터 시간</th>"
  trBodyHTML += "<td></td>";
  trBodyHTML += "<td></td>";

  for (let i = 0; i < colums.length; i++) {
    trHeadHTML += "<th style='width: 200px;' class='bgGray1'>"
        + datas[i].korName + "<br/>(" + datas[i].elementUnit + ")</th>";
    trBodyHTML += "<td></td>";
    chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='"
        + colums[i] + "'>" + datas[i].korName + "</label>";
  }

  trHeadHTML += "<th style='width: 200px;' class='bgGray1'>통합지수<br/>(" + ciType
      + ")</th>";
  trHeadHTML += "<th style='width: 200px;' class='bgGray1'>전원</th>";
  trHeadHTML += "<th style='width: 200px;' class='bgGray1'>풍량</th>";
  trHeadHTML += "<th style='width: 200px;' class='bgGray1'>운전모드</th>";

  chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='cmd_p'>전원</label>";
  chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='cmd_w'>풍량</label>";
  chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='cmd_m'>운전모드</label>";

  $('#popTableHead').html(trHeadHTML);
  $('#popTableBody').html(trBodyHTML);
  $('#chartTypes').html(chartTypesHTML);
}

function chgChartData() {
  const chkArr = Array();
  const obj = document.getElementsByName("chartType");

  for (let i = 0; i < obj.length; i++) {
    if (document.getElementsByName(
        "chartType")[i].checked === true) {
      chkArr.push(obj[i].value);
    }
  }

  timeChartCreate("timeChartDiv", hisApiData, chkArr);
}