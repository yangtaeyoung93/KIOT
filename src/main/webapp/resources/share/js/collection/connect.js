let previousSearchType=0;

function replaceAll(str, searchStr, replaceStr) {
  return str.split(searchStr).join(replaceStr);
}

function viewData2(serialNum, parentSpaceName, spaceName, productDt,
    stationName, standard) {
  let winWidth = 400;
  let winHeight = 400;

  if (screen) {
    winWidth = screen.width;
    winHeight = screen.height;
  }

  const newWindow = window.open('', 'collection_detail_pop',
      'toolbar=no, location=no, scrollbars=yes,resizable=yes,width=' +
      winWidth + ',height=' + winHeight + ',left=0,top=0');

  const target = document.getElementById("detail_info_form");
  const act_url = "/collection/connect_popup";

  target.target = "collection_detail_pop";
  target.action = act_url;
  target.p_h_serialNum.value = serialNum;
  target.p_h_parentSpaceName.value = parentSpaceName;
  target.p_h_spaceName.value = spaceName;
  target.p_h_productDt.value = productDt;
  target.p_h_stationName.value = stationName;
  target.standard.value = standard;
  target.p_h_ventSerial.value = $("#serial_" + serialNum).val();
  target.deviceType.value = "IAQ";

  target.submit();

  newWindow.focus();
}

function initDataTableCustom() {
  let fontColor;
  const table = $('#connectTable').DataTable(
      {
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
          "url": "/api/collection/list/connect",
          "type": "GET"
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
          {data: "serial"},
          {data: "serial"},
          {data: "serial"}
        ],
        createdRow: function (row, data) {
          $(row).attr('id', 'row-' + data.serial);
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
              let parentSpaceName = full.parentSpaceName;

              if (!parentSpaceName || parentSpaceName === "undefined") {
                parentSpaceName = "";
              }

              let spaceName = full.spaceName;

              if (!spaceName || spaceName === "undefined") {
                spaceName = "";
              }

              let ventStr = "";
              let cnt = 0;
              let ventCnt = full.vents.length;
              let subStr = ",";
              for (let i = 0; i < ventCnt; i++) {
                if (i === ventCnt - 1) {
                  subStr = "";
                }
                ventStr += full.vents[i].vent + subStr;
                cnt++;
              }

              let linkHtml = "<input type='hidden' id='serial_" + full.serial
                  + "' name='serial_" + full.serial + "' value='" + ventStr
                  + "'>";
              linkHtml += "<span style='cursor: pointer; color: blue;' onClick='viewData2(\""
                  + full.serial + "\",\"" + parentSpaceName
                  + "\",\"" + spaceName + "\",\""
                  + full.productDt + "\",\""
                  + full.stationName + "\");', 'sum'>"
                  + full.serial + "</span>";
              return "<strong>" + linkHtml + "</strong>";
            },
          },
          {
            targets: 2,
            orderable: true,
            render: function (data, type, full, meta) {

              return "<div style='background: #F0F8FF; width:220px; height: 45px; overflow-y: auto;'><strong>"
                  + full.ventsStr + "</strong></div>";
            },
          },
          {
            targets: 3,
            orderable: true,
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

              const convertTime = year + "-" + month + "-"
                  + day + " " + hours + ":" + minutes
                  + ":" + seconds;

              return "<strong>" + convertTime + "<strong>";
            },
          },
          {
            targets: 4,
            orderable: false,
            render: function (data, type, full, meta) {
              let pm10;

              if (full.sensor == null || full.sensor.pm10 == null) {
                pm10 = "";
                fontColor = "";
              } else {
                pm10 = full.sensor.pm10;
                fontColor = txtColor(full.sensor.cici_pm10, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + pm10 + "</span>";
            },
          },
          {
            targets: 5,
            orderable: false,
            render: function (data, type, full, meta) {
              let pm25;

              if (full.sensor == null || full.sensor.pm25 == null) {
                pm25 = "";
                fontColor = "";
              } else {
                pm25 = full.sensor.pm25;
                fontColor = txtColor(full.sensor.cici_pm25, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + pm25 + "</span>";
            },
          },
          {
            targets: 6,
            orderable: false,
            render: function (data, type, full, meta) {
              let co2;

              if (full.sensor == null || full.sensor.co2 == null) {
                co2 = "";
                fontColor = "";
              } else {
                co2 = full.sensor.co2;
                fontColor = txtColor(full.sensor.cici_co2, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + co2 + "</span>";
            },
          },
          {
            targets: 7,
            orderable: false,
            render: function (data, type, full, meta) {
              let voc;

              if (full.sensor == null || full.sensor.voc == null) {
                voc = "";
                fontColor = "";
              } else {
                voc = full.sensor.voc;
                fontColor = txtColor(full.sensor.cici_voc, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + voc + "</span>";
            },
          },
          {
            targets: 8,
            orderable: false,
            render: function (data, type, full, meta) {
              let noise;

              if (full.sensor == null || full.sensor.noise == null) {
                noise = "";
                fontColor = "";
              } else {
                noise = full.sensor.noise;
                fontColor = txtColor(full.sensor.cici_noise, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + noise + "</span>";
            },
          },
          {
            targets: 9,
            orderable: false,
            render: function (data, type, full, meta) {
              let temp;

              if (full.sensor == null || full.sensor.temp == null) {
                temp = "";
                fontColor = "";
              } else {
                temp = full.sensor.temp;
                fontColor = txtColor(full.sensor.cici_temp, true);
              }

              return "<span style='font-weight:bold; color:" + fontColor + "'>"
                  + temp + "</span>";
            },
          },
          {
            targets: 10,
            orderable: false,
            render: function (data, type, full, meta) {
              let humi;

              if (full.sensor == null || full.sensor.humi == null) {
                humi = "";
                fontColor = "";
              } else {
                humi = full.sensor.humi;
                fontColor = txtColor(full.sensor.cici_humi, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + humi + "</span>";
            },
          },
          {
            targets: 11,
            orderable: false,
            render: function (data, type, full, meta) {
              let cici;

              if (full.sensor == null || full.sensor.cici == null) {
                cici = "";
                fontColor = "";
              } else {
                cici = full.sensor.cici;
                fontColor = txtColor(full.sensor.cici, true);
              }

              return "<span style='font-weight:bold;  color:" + fontColor + "'>"
                  + cici + "</span>";
            },
          },
          {
            targets: 12,
            orderable: true,
            render: function (data, type, full, meta) {
              return "<span style='font-weight:bold;'>" + full.stationName
                  + "</span>";
            },
          },
          {
            targets: 13,
            orderable: true,
            render: function (data, type, full, meta) {
              return "<span style='font-weight:bold;'>" + full.sensor.cmd_p
                  + "</span>";
            },
          },
          {
            targets: 14,
            orderable: false,
            render: function (data, type, full, meta) {
              return "<span style='font-weight:bold;'>" + full.sensor.cmd_w
                  + "</span>";
            },
          },
          {
            targets: 15,
            orderable: false,
            render: function (data, type, full, meta) {
              let driveMode = full.sensor.cmd_m;
              if (!driveMode || driveMode === "undefined") {
                driveMode = null;
              } else if (driveMode === "1") {
                driveMode = "1 (환기모드)"
              } else if (driveMode === "2") {
                driveMode = "2 (공기청정모드)"
              } else if (driveMode === "3") {
                driveMode = "3 (바이패스모드)"
              }
              return "<span style='font-weight:bold;'>" + driveMode
                  + "</span>";
            },
          },
          {
            targets: 16,
            orderable: false,
            render: function (data, type, full, meta) {
              let groupName = full.groupName;

              if (!groupName || groupName === "undefined") {
                groupName = "";
              }

              return "<span style='font-weight:bold;'>" + groupName + "</span>";
            },
          },
          {
            targets: 17,
            orderable: true,
            render: function (data, type, full, meta) {
              let etc = full.etc;
              if (!etc || etc === "undefined") {
                etc = "";
              }
              return "<span style='font-weight:bold;'>" + etc + "</span>";
            },
          },
          {
            targets: 18,
            visible: false,
            render: function (data, type, full, meta) {
              return full.userId;
            },
          },
          {
            targets: 19,
            visible: false,
            render: function (data, type, full, meta) {
              return "g_" + full.groupId;
            },
          },
          {
            targets: 20,
            visible: false,
            render: function (data, type, full, meta) {
              return full.testYn;
            },
          },
          {
            targets: 21,
            visible: false,
            render: function (data, type, full, meta) {
              let parentSpaceName = full.parentSpaceName;
              if (!parentSpaceName || parentSpaceName === "undefined") {
                parentSpaceName = "";
              }
              return parentSpaceName;
            },
          },
          {
            targets: 22,
            visible: false,
            render: function (data, type, full, meta) {
              let spaceName = full.spaceName;
              if (!spaceName || spaceName === "undefined") {
                spaceName = "";
              }
              return spaceName;
            },
          },
{
            targets: 23,
            visible: false,
            render: function (data, type, full, meta) {

              return full.masterName;
            },
          },
        ],
      });
  $('.search_bottom input').unbind().bind('keyup', function () {
    const colIndex = document.querySelector('#searchType').selectedIndex;
    if (colIndex === 0) {
      table.search(this.value).draw();
    } else if (colIndex === 1) {
      table.column(1).search(this.value).draw();
    } else if (colIndex === 2) {
      table.column(12).search(this.value).draw();
    } else if (colIndex === 3) {
      table.column(18).search(this.value).draw();
    } else if (colIndex === 4) {
      table.column(17).search(this.value).draw();
    } else if (colIndex === 5) {
      table.column(16).search(this.value).draw();
    }
  });

  $("#searchType").change(function () {
    const colIndex = document.querySelector("#searchType").selectedIndex;
       switch(previousSearchType){
                  case 0 : table.search("").draw(); break;
                  case 1 : table.column(1).search("").draw(); break;
                  case 2 : table.column(12).search("").draw(); break;
                  case 3 : table.column(18).search("").draw(); break;
                  case 4 : table.column(17).search("").draw(); break;
                  case 5 : table.column(16).search("").draw(); break;
       }
       previousSearchType = colIndex;
    $("#searchValue").val("");
  });

  $("#searchMaster").change(function () {
      $('#searchGroup').val('');
      table.column(19).search("").draw();
      table.column(21).search("").draw();
      const masterIdx = $('#searchMaster').val();
      searchMasterId = $('#searchMaster option:selected').text();
      if(this.value == "") searchMasterId = "";
      $(".filterDiv").each(function (index, item) {
        $(item).removeClass("filter-cli");
      });
      $(".filterDiv").first().addClass("filter-cli");

      getGroupList(masterIdx);
      table.column(23).search(searchMasterId).draw();
      table.column(21).search("").draw();

  });

  $("#searchGroup").change(function () {
    table.column(19).search("g_" + this.value).draw();
  });

  $("#searchParentSpace").change(function () {
    const spaceVal = $(this).val();
    const spaceVal_arr = spaceVal.split("|");
    const spaceVal_name = spaceVal_arr[1];
    let search_space_val;

    if (!spaceVal) {
      search_space_val = "";
    } else {
      search_space_val = spaceVal_name;
    }

    table.column(21).search(search_space_val).draw();
  });

  $("#searchSpace").change(function () {
    table.column(22).search(this.value).draw();
  });

  $('#searchTestYn').click(function () {
    if ($("#searchTestYn").prop("checked") === true) {
      $(this).val("Y");
    } else {
      $(this).val("N");
    }

    if ($(this).val() === "Y") {
      table.column(20).search("Y").draw();
    } else {
      table.column(20).search("").draw();
    }
  })

  table.on('order.dt search.dt', function () {
    table.column(0, {search: 'applied', order: 'applied'}).nodes().each(
        function (cell, i) {
          cell.innerHTML = i + 1;
        });
  }).draw();

  $("#connectTable_filter").hide();
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

function getHighSpace() {
  let optHtml = "";
  $.ajax({
    method: "GET",
    url: "/system/space/high",
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&deviceTypeIdx=1",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";
      for (let i = 0; i < param.data.length; i++) {
        optHtml += "<option value='" + param.data[i].idx + "|"
            + param.data[i].spaceName + "'>" + param.data[i].spaceName
            + "</option>";
      }
      $("#searchParentSpace").html(optHtml);
    }
  });
}

function getSpace(val) {
  let optHtml = "";
  const spaceVal_arr = val.split("|");
  const spaceVal_idx = spaceVal_arr[0];
  $("#searchSpace").html("");

  if (!val) {
    $("#searchSpace").html("<option value=''>전체</option>");
  } else {
    $.ajax({
      url: "/system/member/device/ajax/spaces",
      type: "GET",
      async: false,
      data: "parentSpaceIdx=" + spaceVal_idx,
      success: function (param) {
        optHtml += "<option value=''>전체</option>";

        for (let i = 0; i < param.data.length; i++) {
          const spaceName = param.data[i].spaceName;
          optHtml += "<option value='" + param.data[i].spaceName + "'>"
              + spaceName + "</option>";
        }

        $("#searchSpace").html(optHtml);
      }
    })
  }
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
  getHighSpace();

  $("#searchTestYn").click(function () {
    const chkTestYn = $("#searchTestYn");
    if (chkTestYn.prop("checked") === true) {
      $(this).val("Y");
    } else {
      $(this).val("N");
    }
  })

})
