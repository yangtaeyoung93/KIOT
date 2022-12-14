let historyData;
let previousSearchType=0;

function viewData2(serialNum, productDt, standard) {
  let winWidth = 400;
  let winHeight = 400;

  if (screen) {
    winWidth = screen.width;
    winHeight = screen.height;
  }

  newWindow = window.open('', 'collection_vent_detail_pop',
      'toolbar=no, location=no, scrollbars=yes,resizable=yes,width='
      + winWidth + ',height=' + winHeight + ',left=0,top=0');

  const target = document.getElementById("detail_info_form");
  const act_url = "/collection/vent_popup";

  target.target = "collection_vent_detail_pop";
  target.action = act_url;
  target.p_h_serialNum.value = serialNum;
  target.p_h_productDt.value = productDt;
  target.standard.value = standard;
  target.deviceType.value = "VENT";

  target.submit();

  newWindow.focus();
}

function setVentModels(table) {
  $("#searchVent .vent_model_item").remove();

  var data = table.rows({filter: 'applied'}).data();

  if (data.length > 0) {
    var ventModles = $.unique(($.map(data, function (o) {
      return o["deviceModel"];
    })).sort());

    $.each(ventModles, function (index, name) {
      $("#searchVent").append("<option class='vent_model_item' value='" + name + "'>" + name + "</option>");
    });
  }
}

function initDataTableCustom() {
  let fontWeight = "";
  let searchGroupId = "";
  let searchVentId = "";
  let searchMaster = document.getElementById('searchMaster');
  let masterIdx = searchMaster.value;
  const table = $('#ventTable').DataTable({
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
    destroy: true,
    processing: true,
    serverSide: false,
    ajax: {
      url : "/api/collection/list/vent",
      type : "GET"
    },
    columns: [
      {orderable: false},
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
      {data: "serial"},
      {data: "serial"},
      {data: "serial"}
    ],
    createdRow: function (row, data) {
      $(row).attr('id', 'row-' + data.serial);

      if (data.receiveFlag === false) {
        $(row).attr('style',
            'color: #929498 !important');
      }
    },
    columnDefs: [
      {
        targets: 0,
        orderable: true,
        render: function (data, type, full, meta) {
          return meta.row + meta.settings._iDisplayStart + 1;
        },
      },
      {
        targets: 1,
        orderable: true,
        render: function (data, type, full, meta) {
          let linkHtml = "";

          if (full.sensor == null) {
            linkHtml += full.serial;
          } else {
            linkHtml += "<span style='cursor: pointer; color: blue;' onClick='viewData2(\""
                + full.serial + "\",\"" + full.productDt + "\");', 'sum'>"
                + full.serial + "</span>";
          }

          return linkHtml;
        },
      },
      {
	    targets: 2,
	    orderable: true,
	    render: function (data, type, full, meta) {
	      const fontWeight = "bold";
		
	      return "<span style='font-weight:" + fontWeight + ";'>"
            + full.stationName + "</span>";
        },
	  },
      {
        targets: 3,
        orderable: true,
        render: function (data, type, full, meta) {
          let groupCompanyName = full.groupCompanyName;

          if (!groupCompanyName || groupCompanyName === "undefined") {
            groupCompanyName = "";
          }

          const fontWeight = "bold";

          return "<span style='font-weight:" + fontWeight + ";'>"
            + groupCompanyName + "</font>";
        },
      },
      {
        targets: 4,
        visible: true,
        render: function (data, type, full, meta) {
          return full.userId;
        },
      },
      {
        targets: 5,
        visible: true,
        render: function (data, type, full, meta) {
          return full.productDt;
        },
      },
      {
        targets: 6,
        orderable: true,
        render: function (data, type, full, meta) {
          let date = "";
          let convertTime;

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          if (full.timestamp == null) {
            convertTime = "";
          } else {
            date = new Date(full.timestamp * 1000);
            let year = date.getFullYear();

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

            convertTime = year + "-" + month + "-" + day + " " + hours + ":"
                + minutes + ":" + seconds;
          }

          return "<span style='font-weight:" + fontWeight + "; '>" + convertTime
              + "</span>";
        },
      },
      {
        targets: 7,
        orderable: false,
        render: function (data, type, full, meta) {
          const ai_mode = full.aiMode;
          let ai_mode_str;

          if (ai_mode === "0") {
            ai_mode_str = "Off";
          } else {
            ai_mode_str = "On";
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>" + ai_mode_str
              + "</span>";
        },
      },
      {
        targets: 8,
        orderable: false,
        render: function (data, type, full, meta) {

          let auto_mode = "";
          let auto_mode_str = "";

          if (full.sensor != null) {
            auto_mode = full.sensor.auto_mode;
            if (auto_mode === "0") {
              auto_mode_str = "Off";
            } else {
              auto_mode_str = "On"
            }
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + auto_mode_str + "</span>";
        },
      },
      {
        targets: 9,
        orderable: false,
        render: function (data, type, full, meta) {

          let power = "";
          let power_str = "";

          if (full.sensor != null) {
            power = full.sensor.power;

            if (power === "0") {
              power_str = "Off";
            } else {
              power_str = "On"
            }
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>" + power_str
              + "</span>";
        },
      },
      {
        targets: 10,
        orderable: false,
        render: function (data, type, full, meta) {
          let air_volume;
          if (full.sensor == null) {
            air_volume = "";
          } else {
            air_volume = full.sensor.air_volume;
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>" + air_volume
              + "</span>";
        },
      },
      {
        targets: 11,
        orderable: false,
        render: function (data, type, full, meta) {
          let exh_mode
          let exh_mode_str = "";

          if (full.sensor != null) {
            exh_mode = full.sensor.exh_mode;
            if (exh_mode === "0") {
              exh_mode_str = "Off";
            } else if (exh_mode === "1") {
              exh_mode_str = "On";
            }
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + exh_mode_str + "</span>";
        },
      },
      {
        targets: 12,
        orderable: false,
        render: function (data, type, full, meta) {

          let air_mode = "";
          let air_mode_str = "";

          if (full.sensor != null) {
            air_mode = full.sensor.air_mode;
            if (air_mode === "0") {
              air_mode_str = "Off"
            } else if (air_mode === "1") {
              air_mode_str = "On"
            }
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + air_mode_str + "</span>";
        },
      },
      {
        targets: 13,
        orderable: false,
        render: function (data, type, full, meta) {

          let filter_alarm;
          let filter_alarm_str = "";

          if (full.sensor != null) {
            filter_alarm = full.sensor.filter_alarm;

            if (filter_alarm === "0") {
              filter_alarm_str = "정상";
            } else if (filter_alarm === "1") {
              filter_alarm_str = "점검";
            } else if (filter_alarm === "2") {
              filter_alarm_str = "필터";
            } else if (filter_alarm === "4"){
              filter_alarm_str = "교환";
            }else if (filter_alarm === "8"){
                filter_alarm_str = "소자 교환";
            }
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + filter_alarm_str + "</span>";
        },
      },
      {
        targets: 14,
        orderable: false,
        render: function (data, type, full, meta) {
          let fire_alarm_str;

          if (full.sensor == null || full.sensor.fire_alarm == null) {
            fire_alarm_str = "";
          } else {
            fire_alarm_str = full.sensor.fire_alarm;
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + fire_alarm_str + "</span>";
        },
      },
      {
        targets: 15,
        orderable: false,
        render: function (data, type, full, meta) {
          let water_alarm_str;

          if (full.sensor == null || full.sensor.water_alarm
              == null) {
            water_alarm_str = "";
          } else {
            water_alarm_str = full.sensor.water_alarm;
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + water_alarm_str + "</span>";
        },
      },
      {
        targets: 16,
        orderable: false,
        render: function (data, type, full, meta) {
          let dev_stat_str;

          if (full.sensor == null || full.sensor.dev_stat == null) {
            dev_stat_str = "";
          } else {
            dev_stat_str = full.sensor.dev_stat;
          }

          if (full.receiveFlag === true) {
            fontWeight = "bold";
          } else {
            fontWeight = "normal";
          }

          return "<span style='font-weight:" + fontWeight + "; '>"
              + dev_stat_str + "</span>";
        },
      },
      {
        targets: 17,
        render: function (data, type, full, meta) {
          return "<span style='font-weight:" + fontWeight + "; '>"
              + full.deviceModel + "</span>";
        },
      },
      {
        targets: 18,
        visible: false,
        render: function (data, type, full, meta) {
          return "g_" + full.groupId;
        },
      },
      {
        targets: 19,
        visible: false,
        render: function (data, type, full, meta) {
          let receiveFlag = full.receiveFlag;

          if (!receiveFlag || receiveFlag === "undefined") {
            receiveFlag = false;
          }

          return receiveFlag;
        },
      },
      {
        targets: 20,
        visible: false,
        render: function (data, type, full, meta) {
          return full.masterName;
        },
      },
      {
        targets: 21,
        visible: false,
        render: function (data, type, full, meta) {
          return full.testYn;
        },
      },
    ],
    drawCallback: function (settings) {
      if (settings.json != null) {
        const js = settings.json.data;
        let reCnt = 0;
        let unReCnt = 0;

        const matchVent = function(data) {
          const ventModel = $('#searchVent > option:selected').val();
          return ventModel == "" || (ventModel != "" && ventModel == data.deviceModel);
        }

        for (let idx = 0; idx < js.length; idx++) {
          if ($('#searchGroup').val() != "") { //선택한 그룹이 있는 경우
                if (js[idx].receiveFlag && "g_" + js[idx].groupId == searchGroupId && matchVent(js[idx])) reCnt++;
                else if (  !js[idx].receiveFlag &&  "g_" + js[idx].groupId == searchGroupId && matchVent(js[idx])) unReCnt++;
          }else { //선택한 그룹이 없는 경우
                if($('#searchMaster').val() != ""){ //선택한 상위그룹이 있는 경우
                    if(js[idx].receiveFlag && js[idx].masterIdx == $('#searchMaster').val() && matchVent(js[idx])){ reCnt++;}
                    else if (!js[idx].receiveFlag && js[idx].masterIdx == $('#searchMaster').val() && matchVent(js[idx])) unReCnt++;
                }else{
                    if (js[idx].receiveFlag && matchVent(js[idx])) reCnt++;
                    else if (!js[idx].receiveFlag && matchVent(js[idx])) unReCnt++;
                }
          }
        }


        let groupSelect = document.querySelector("#searchGroup");
        var txtGroupId;
        if($('#searchMaster').val() != "" && $('#searchGroup').val() == ""){
            txtGroupId = $('#searchMaster option:selected').text();
        }else{
            txtGroupId = searchGroupId == "" ? "전체"  : groupSelect.options[groupSelect.selectedIndex].text;
        }
        let receivePercentage;
        let unreceivePercentage;
        if(reCnt + unReCnt ==0){
            receivePercentage = 0;
            unreceivePercentage = 0;
        }else{
            receivePercentage = (( reCnt / (reCnt + unReCnt) ) * 100).toFixed(1);
            unreceivePercentage = (( unReCnt / (reCnt + unReCnt) ) * 100).toFixed(1);
        }
        $("#msgTxtTotal").html(
          txtGroupId +
            " " +
            (reCnt + unReCnt) +
            "개 (" +
            receivePercentage +
            "%)"
        );
        $("#msgTxtReceive").html(
          "수신  " +
            reCnt +
            "개 (" + receivePercentage +
            "%)"
        );
        $("#msgTxtUnReceive").html(
          "미수신  " +
            unReCnt +
            "개 (" +
            unreceivePercentage +
            "%)"
        );      }
    },
    initComplete: function (settings, data) {
      console.log(data);
      setVentModels(table);

      $("#searchVent").on("change", function() {
        searchVentId = $("option:selected", this).attr("value");
        console.log(searchVentId);
        table.column(17).search(searchVentId).draw();
      });
    },
  });

  $('.search_bottom input').unbind().bind('keyup', function () {
    const colIndex = document.querySelector('#searchType').selectedIndex;
    if (colIndex === 0) {
      table.search(this.value).draw();
    } else if (colIndex === 1) {
      table.column(1).search(this.value).draw();
    } else if (colIndex === 2) {
      table.column(2).search(this.value).draw();
    } else if (colIndex === 3) {
      table.column(4).search(this.value).draw();
    } else if (colIndex === 4) {
      table.column(3).search(this.value).draw();
    }
  });

  $("#searchType").change(function () {
   const colIndex = document.querySelector("#searchType").selectedIndex;
   switch(previousSearchType){
              case 0 : table.search("").draw(); break;
              case 1 : table.column(1).search("").draw(); break;
              case 2 : table.column(2).search("").draw(); break;
              case 3 : table.column(4).search("").draw(); break;
              case 4 : table.column(3).search("").draw(); break;
   }
   previousSearchType = colIndex;

    $("#searchValue").val("");
  });

  $("#searchMaster").change(function () {
    $('#searchGroup').val('');
    $('#searchVent').val('');
    table.column(18).search("").draw();
    table.column(19).search("").draw();
    const masterIdx = $('#searchMaster').val();
    searchMasterId = $('#searchMaster option:selected').text();
    if(this.value == "") searchMasterId = "";
    $(".filterDiv").each(function (index, item) {
         $(item).removeClass("filter-cli");
    });
    $(".filterDiv").first().addClass("filter-cli");

    getGroupList(masterIdx);
    table.column(20).search(searchMasterId).draw();
    table.column(19).search("").draw();
    table.column(17).search("").draw();
    setVentModels(table);
  });

  $("#searchGroup").change(function () {
    $('#searchVent').val('');
    searchGroupId = "g_" + this.value;
    if(this.value == "") searchGroupId = "";
    $(".filterDiv").each(function (index, item) {
      $(item).removeClass("filter-cli");
    }).first().addClass("filter-cli");

    table.column(17).search("").draw();
    table.column(18).search(searchGroupId).draw();
    table.column(19).search("").draw();
    setVentModels(table);
  });

  $('#searchTestYn').click(function () {
    if ($("#searchTestYn").prop("checked") === true) {
      $(this).val("Y");
    } else {
      $(this).val("N");
    }

    if ($(this).val() === "Y") {
      table.column(10).search("Y").draw();
    } else {
      table.column(10).search("").draw();
    }
  })

  table.on('order.dt search.dt', function () {
    table.column(0, {search: 'applied', order: 'applied'}).nodes().each(
        function (cell, i) {
          cell.innerHTML = i + 1;
        });
  }).draw();

  $("#mttDiv").click(function () {
    table.column(19).search("").draw();
  });
  $("#mtrDiv").click(function () {
    table.column(19).search(true).draw();
  });
  $("#mturDiv").click(function () {
    table.column(19).search(false).draw();
  });

  table.on('order.dt search.dt', function () {
    table.column(0, {search: 'applied', order: 'applied'}).nodes().each(
        function (cell, i) {
          cell.innerHTML = i + 1;
        });
  }).draw();

  $("#ventTable_filter").hide();
}

function getMasterList() {
  var optHtml = "";
  $.ajax({
    method: "GET",
    url: "/system/master/get",
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&searchValue2=IAQ",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";
      for (var i = 0; i < param.data.length; i++) {
        optHtml +=
          "<option value='" +
          param.data[i].idx +
          "'>" +
          param.data[i].masterName +
          "</option>";
      }
      $("#searchMaster").html(optHtml);
    },
  });
}

function getGroupList(masterId) {
  let optHtml = "";
  if(masterId == undefined || masterId == ""){
    url =  "/system/group/get";
  }else{
    url = `/system/master/get/${masterId}/m`;
  }
  $.ajax({
    method: "GET",
    url: url,
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&searchValue2=IAQ",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";

      for (let i = 0; i < param.data.length; i++) {
        optHtml += "<option value='" + param.data[i].groupId + "'>"
            + param.data[i].groupName + "</option>";
      }

      $("#searchGroup").html(optHtml);
    }
  });
}

$().ready(function () {
$(function() {
  var marginTop = parseInt( $(".main-sidebar").css('margin-top') );
  $(window).scroll(function(e) {
    $(".main-sidebar").css("margin-top", marginTop - $(this).scrollTop() );
  });
});
  $('.select2').select2();
  $('.select2bs4').select2({
    theme: 'bootstrap4'
  })

  $(".select2-container").css("display", "inline-block");

  initDataTableCustom();
  getMasterList();
  getGroupList();
  $("#ventTable_filter").hide();

  $("#searchTestYn").click(function () {
    const chkTestYn = $("#searchTestYn");

    if (chkTestYn.prop("checked") === true) {
      $(this).val("Y");
    } else {
      $(this).val("N");
    }
  })

  $(".filterDiv").click(function () {
    $(".filterDiv").each(function (index, item) {
      $(item).removeClass("filter-cli");
    })

    $(this).addClass("filter-cli");
  })
})