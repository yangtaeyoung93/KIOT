let historyData;
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

  viewData(aCode, aName, lat, lon);

  $("#modalSearchBtn").click(function () {
    const aCode = $("#p_h_aCode").val();
    const aName = $("#p_h_aName").val();
    const lat = $("#p_h_lat").val();
    const lon = $("#p_h_lon").val();

    viewData(aCode, aName, lat, lon);
  });

  $('#chartTypes').change(function () {
    timeChartBefore();
  });

  $('#chartTypeSelect').change(function () {
    timeChartBefore();
  });

  $('#modalDownloadBtn').click(function () {
    $('.excelDownBtn').trigger('click');
  });
})

function viewData(aCode, aName, lat, lon) {
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

  $("#p_h_aCode").val(aCode);
  $("#p_h_aName").val(aName);
  $("#p_h_lat").val(lat);
  $("#p_h_lon").val(lon);

  $("#txtACode").text(aCode);
  $("#txtAName").text(aName);
  $("#txtLat").text(lat);
  $("#txtLon").text(lon);

  obj = {
    dcode: $("#p_h_aCode").val(),
    startTime: startTime,
    endTime: endTime,
    type: "airKor"
  }

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
      filename: $("#p_h_aName").val() + "_" + $("#startDt").val() + "_" +
          $("#endDt").val() + "_download",
      className: 'btn-primary btn excelDownBtn'
    }],

    destroy: true,
    processing: true,
    serverSide: false,
    ajax: {
      "url": "/api/dong/data/detail",
      "type": "GET",
      data: obj,
    },
    columns: [
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
      {data: "serial"},
    ],
    columnDefs: [
      {
        targets: 0,
        render: function (data, type, full, meta) {
          return meta.row + meta.settings._iDisplayStart + 1;
        },
      },
      {
        targets: 1,
        render: function (data, type, full, meta) {
          const regDate = full.regDate;

          const year = regDate.substring(0, 4);
          const month = regDate.substring(4, 6);
          const day = regDate.substring(6, 8);
          const hour = regDate.substring(8, 10);
          const minute = regDate.substring(10, 12);
          const second = regDate.substring(12, 14);
          const convertTime = year + "-" + month + "-" + day + " " + hour + ":"
              + minute + ":" + second;

          return convertTime;
        },
      },
      {
        targets: 2,
        render: function (data, type, full, meta) {
          let pm10 = full.pm10;

          if (pm10) {
            pm10 = parseInt(full.pm10);
            pm10 = pm10.toFixed(0);
          } else {
            pm10 = "";
          }

          return pm10;
        },
      },
      {
        targets: 3,
        render: function (data, type, full, meta) {
          let pm10_grade = full.pm10_grade;

          if (pm10_grade) {
            pm10_grade = parseInt(full.pm10_grade);
            pm10_grade = pm10_grade.toFixed(0);
          } else {
            pm10_grade = "";
          }

          return pm10_grade;
        },
      },
      {
        targets: 4,
        render: function (data, type, full, meta) {
          let pm25 = full.pm25;

          if (pm25) {
            pm25 = parseInt(full.pm25);
            pm25 = pm25.toFixed(0);
          } else {
            pm25 = "";
          }

          return pm25;
        },
      },
      {
        targets: 5,
        render: function (data, type, full, meta) {
          let pm25_grade = full.pm25_grade;

          if (pm25_grade) {
            pm25_grade = parseInt(full.pm25_grade);
            pm25_grade = pm25_grade.toFixed(0);
          } else {
            pm25_grade = "";
          }

          return pm25_grade;
        },
      },
      {
        targets: 6,
        render: function (data, type, full, meta) {
          let co = full.co;

          if (co) {
            co = full.co;
          } else {
            co = "";
          }

          return co;
        },
      },
      {
        targets: 7,
        render: function (data, type, full, meta) {
          let co_grade = full.co_grade;

          if (co_grade) {
            co_grade = parseInt(full.co_grade);
            co_grade = co_grade.toFixed(0);
          } else {
            co_grade = "";
          }

          return co_grade;
        },
      },
      {
        targets: 8,
        render: function (data, type, full, meta) {
          let o3 = full.o3;

          if (o3) {
            o3 = full.o3;
          } else {
            o3 = "";
          }

          return o3;
        },
      },
      {
        targets: 9,
        render: function (data, type, full, meta) {
          let o3_grade = full.o3_grade;

          if (o3_grade) {
            o3_grade = parseInt(full.o3_grade);
            o3_grade = o3_grade.toFixed(0);
          } else {
            o3_grade = "";
          }

          return o3_grade;
        },
      },
      {
        targets: 10,
        render: function (data, type, full, meta) {
          let no2 = full.no2;

          if (no2) {
            no2 = full.no2;
          } else {
            no2 = "";
          }

          return no2;
        },
      },
      {
        targets: 11,
        render: function (data, type, full, meta) {
          let no2_grade = full.no2_grade;

          if (no2_grade) {
            no2_grade = parseInt(full.no2_grade);
            no2_grade = no2_grade.toFixed(0);
          } else {
            no2_grade = "";
          }

          return no2_grade;
        },
      },
      {
        targets: 12,
        render: function (data, type, full, meta) {
          let so2 = full.so2;

          if (so2) {
            so2 = full.so2;
          } else {
            so2 = "";
          }

          return so2;
        },
      },
      {
        targets: 13,
        render: function (data, type, full, meta) {
          let so2_grade = full.so2_grade;

          if (so2_grade) {
            so2_grade = parseInt(full.so2_grade);
            so2_grade = so2_grade.toFixed(0);
          } else {
            so2_grade = "";
          }

          return so2_grade;
        },
      },
      {
        targets: 14,
        render: function (data, type, full, meta) {
          let khai = full.khai;

          if (khai) {
            khai = full.khai;
          } else {
            khai = "";
          }

          return khai;
        },
      },
      {
        targets: 15,
        render: function (data, type, full, meta) {
          let khai_grade = full.khai_grade;

          if (khai_grade) {
            khai_grade = parseInt(full.khai_grade);
            khai_grade = khai_grade.toFixed(0);
          } else {
            khai_grade = "";
          }

          return khai_grade;
        },
      }
    ],
    initComplete: function (settings, data) {
      historyData = data;
      valueArr = new Array();

      for (let i = 0; i < historyData.data.length; i++) {
        const regDate = historyData.data[i].regDate;
        let convertTime = "";

        const year = regDate.substring(0, 4);
        const month = regDate.substring(4, 6);
        const day = regDate.substring(6, 8);
        const hour = regDate.substring(8, 10);
        const minute = regDate.substring(10, 12);
        const second = regDate.substring(12, 14);
        convertTime = year + "-" + month + "-" + day + "\n" + hour + ":"
            + minute + ":" + second;

        historyData.data[i].convertTime = convertTime;
      }

      timeChartBefore();
    }
  });

  $("#popTable_filter").hide();
  $($("#tableTitle")).insertBefore("#popTable_length");
  $("#popTable_length").css("display", "inline-block");
  $("#popTable_length").css("margin-left", "20px");
  $("#modalDownloadBtn").insertAfter("#popTable_filter");
  $("#popTable_wrapper .top").first().css("border-bottom", "1px solid #E5E5E5");
  $("#popTable_wrapper .top").first().css("padding-bottom", "15px");
  $('.excelDownBtn').css("float", "right");
}

function timeChartBefore() {
  let chkArr = Array();
  const obj = document.getElementsByName("chartType");

  for (let i = 0; i < obj.length; i++) {
    if (document.getElementsByName(
        "chartType")[i].checked == true) {
      chkArr.push(obj[i].value);
    }
  }
  const isGrade = $('#chartTypeSelect').val() == 'options' ? false : true;
  timeChartCreate("timeChartDiv", historyData, chkArr, isGrade);
}

function timeChartCreate(chartDiv, hisData, options, isGrade) {
  let titleTxt = "";

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

  let optionsName = {
    "pm10": "미세먼지",
    "pm25": "초미세먼지",
    "co": "co",
    "o3": "o3",
    "so2": "so2",
    "no2": "no2",
    "khai": "khai"
  };

  if (options) {
    options.forEach((option) => {
      createSeries(option, isGrade);
    });
  }

  chart.cursor = new am4charts.XYCursor();
  chart.cursor.behavior = "panXY";

  chart.scrollbarY = new am4core.Scrollbar();
  chart.scrollbarY.parent = chart.leftAxesContainer;
  chart.scrollbarY.toBack();

  function createSeries(option, isGrade) {
    const series = chart.series.push(new am4charts.LineSeries());
    if (isGrade) {
      series.dataFields.valueY = option + "_grade";
      series.dataFields.valueY.label = false;
    } else {
      series.dataFields.valueY = option;
    }
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