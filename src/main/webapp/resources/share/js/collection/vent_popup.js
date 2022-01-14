let historyData;
let hisApiData = [];

function timeChartCreate(chartDiv, hisData, options) {
  am4core.addLicense("CH205407412");
  // am4core.useTheme(am4themes_animated);

  const chart = am4core.create(chartDiv, am4charts.XYChart);
  chart.data = hisApiData;
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
  categoryAxis.connect = false;

  for (const idx in options) {
    createSeries(options[idx]);
  }

  function createSeries(name) {
    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.renderer.inversed = false;
    valueAxis.title.text = name;
    valueAxis.renderer.minLabelPosition = 0.01;
    valueAxis.connect = false;

    const interfaceColors = new am4core.InterfaceColorSet();

    const series = chart.series.push(new am4charts.LineSeries());
    series.dataFields.valueY = name;
    series.dataFields.categoryX = "convertTime";
    series.yAxis = valueAxis;
    series.name = name;
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

function viewData(serialNum, productDt, standard) {
  let obj;
  let startTime = $("#startDt").val();
  let endTime = $("#endDt").val();

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
  $("#p_h_productDt").val(productDt);

  if (!standard) {
    standard = "sum";
  }

  obj = {
    deviceType: "vent",
    serial: $("#p_h_serialNum").val(),
    startTime: startTime,
    endTime: endTime,
    standard: standard,
    connect: "0"
  }

  const h_serialNum = $("#p_h_serialNum").val();
  const h_productDt = $("#p_h_productDt").val();

  $("#txtSerialNum").text(h_serialNum);
  $("#txtProductDt").text(h_productDt);

  $.ajax({
    method: "GET",
    url: "/api/collection/history",
    data: obj,
    success: function (d) {
      const apiStandard = $("#searchStandard option:selected").val();

      if (apiStandard !== "sum") {
        hisApiData = d.data;
      } else {
        hisApiData = formatData(d.data);
      }

      for (let i = 0; i < hisApiData.length; i++) {
        const formatTimestamp = hisApiData[i].timestamp;
        let date = "";
        let convertTime = "";

        if (hisApiData[i].timestamp == null) {
          convertTime = "";
        } else {
          date = new Date(hisApiData[i].timestamp * 1000);
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

          convertTime = year + "-" + month + "-" + day + "\n" + hours + ":"
              + minutes;
        }

        hisApiData[i].convertTime = convertTime;
      }

      chgChartData();

      $('#popTable').DataTable({
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
          filename: h_serialNum + "_" + $("#startDt").val() + "_" + $(
              "#endDt").val() + "_download",
          className: 'btn-primary btn excelDownBtn'
        }],
        destroy: true,
        processing: true,
        serverSide: false,
        data: hisApiData,
        columns: [
          {data: "timestamp"}, 	// 0
          {data: "auto_mode"}, 	// 1
          {data: "power"},		// 2
          {data: "air_volume"},	// 3
          {data: "exh_mode"},		// 4
          {data: "filter_alarm"},	// 5
          {data: "air_mode"},		// 6
          {data: "fire_alarm"},	// 7
          {data: "water_alarm"},  // 8
          {data: "dev_stat"}		// 9
        ],
        columnDefs: [
          {
            targets: 0,
            render: function (data, type, full, meta) {
              const date = new Date(full.timestamp * 1000);

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

              const convertTime = year + "-" + month + "-" + day + " " + hours
                  + ":" + minutes + ":" + seconds;

              return convertTime;
            },
          },
          {
            targets: 1,
            render: function (data, type, full, meta) {
              const auto_mode = full.auto_mode;
              let auto_mode_str = "";

              if (auto_mode == 0) {
                auto_mode_str = "Off";
              } else if (auto_mode == 1) {
                auto_mode_str = "On";
              }

              return auto_mode_str;
            },
          },
          {
            targets: 2,
            render: function (data, type, full, meta) {
              const power = full.power;
              let power_str = "";

              if (power == 0) {
                power_str = "Off";
              } else if (power == 1) {
                power_str = "On";
              }

              return power_str;
            },
          },
          {
            targets: 3,
            render: function (data, type, full, meta) {

              if (full.air_volume == null) {
                return "";
              }

              return parseInt(full.air_volume);
            },
          },
          {
            targets: 4,
            render: function (data, type, full, meta) {
              const exh_mode = full.exh_mode;
              let exh_mode_str = "";

              if (exh_mode == 0) {
                exh_mode_str = "Off";
              } else if (exh_mode == 1) {
                exh_mode_str = "On";
              }

              return exh_mode_str;
            },
          },
          {
            targets: 5,
            render: function (data, type, full, meta) {
              let air_mode = full.air_mode;
              let air_mode_str = "";

              if (air_mode == 0) {
                air_mode_str = "Off";
              } else if (air_mode == 1) {
                air_mode_str = "On";
              }

              return air_mode_str;
            },
          },
          {
            targets: 6,
            render: function (data, type, full, meta) {
              const filter_alarm = full.filter_alarm;
              let filter_alarm_str = "";

              if (filter_alarm == 0) {
                filter_alarm_str = "정상";
              } else if (filter_alarm == 1) {
                filter_alarm_str = "점검";
              } else if (filter_alarm == 2) {
                filter_alarm_str = "청소";
              } else if (filter_alarm == 3) {
                filter_alarm_str = "교환";
              } else {
                filter_alarm_str = "";
              }

              return filter_alarm_str;
            },
          },
          {
            targets: 7,
            render: function (data, type, full, meta) {
              let fire_alarm = full.fire_alarm;

              if (fire_alarm == 1) {
                fire_alarm = parseInt(full.fire_alarm)
              } else if (fire_alarm == 0) {
                fire_alarm = 0;
              } else {
                fire_alarm = "";
              }

              return fire_alarm;
            },
          },
          {
            targets: 8,
            render: function (data, type, full, meta) {
              let water_alarm = full.water_alarm;

              if (water_alarm == 1) {
                water_alarm = parseInt(full.water_alarm);
              } else if (water_alarm == 0) {
                water_alarm = "0";
              } else {
                water_alarm = "";
              }

              return water_alarm;
            },
          },
          {
            targets: 9,
            render: function (data, type, full, meta) {
              let dev_stat = full.dev_stat;

              if (dev_stat == 1) {
                dev_stat = parseInt(full.dev_stat);
              } else if (dev_stat == 0) {
                dev_stat = "0";
              } else {
                dev_stat = "";
              }

              return dev_stat;
            },
          },
        ]
      });

      $("#popTable_filter").hide();
      $($("#tableTitle")).insertBefore("#popTable_length");
      $("#popTable_length").css("display", "inline-block").css("margin-left",
          "20px");
      $("#modalDownloadBtn").insertAfter("#popTable_filter");
      $("#popTable_wrapper .top").first().css("border-bottom",
          "1px solid #E5E5E5").css("padding-bottom", "15px");
      $('.excelDownBtn').css("float", "right");
    }
  });
}

function formatData(oriData) {
  if (oriData.length == 0) {
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

  for (i in oriData) {
    if (oriData[i].timestamp > tsTime) {
      let loFlag = true;
      while (loFlag) {
        console.log("d");
        const obj = {
          timestamp: tsTime,
          formatTimestamp: tsToDate(tsTime),
          tm: ""
        }
        forData.push(obj);
        tsTime = parseInt(tsTime) + 60;
        if (oriData[i].timestamp == tsTime) {
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

  convertTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes
      + ":" + seconds;

  return convertTime;
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

$().ready(function () {
  $("#startDt").val("");
  $("#endDt").val("");

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

  viewData(serialNum, productDt, standard);

  $("#modalSearchBtn").click(function () {
    const serialNum = $("#p_h_serialNum").val();
    const productDt = $("#p_h_productDt").val();
    const standard = $("#searchStandard option:selected").val();

    viewData(serialNum, productDt, standard);
  })

  const rangeDate = 31;
  let setSdate, setEdate;

  $("#startDt").datepicker({
    dateFormat: 'yy/mm/dd',
    onSelect: function (selectDate) {
      const stxt = selectDate.split("/");
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

  $('#modalDownloadBtn').click(function () {
    $('.excelDownBtn').trigger('click');
  })

  $('#chartTypes').click(function () {
    chgChartData();
  })
})