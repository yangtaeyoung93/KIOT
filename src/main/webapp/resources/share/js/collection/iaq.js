/**
 * 수집정보 > IAQ 수집정보
 */
var historyData;

function viewData2(
  serialNum,
  parentSpaceName,
  spaceName,
  productDt,
  stationName,
  memberIdx,
  deviceIdx
) {
  var winWidth = 400;
  var winheight = 400;

  if (screen) {
    winWidth = screen.width;
    winHeight = screen.height;
  }

  newWindow = window.open(
    "",
    "collection_detail_pop",
    "toolbar=no, location=no, scrollbars=yes,resizable=yes,width=" +
      winWidth +
      ",height=" +
      winHeight +
      ",left=0,top=0"
  );

  var target = document.getElementById("detail_info_form");
  var act_url = "/collection/popup";

  target.target = "collection_detail_pop";
  target.action = act_url;
  target.p_h_serialNum.value = serialNum;
  target.p_h_parentSpaceName.value = parentSpaceName;
  target.p_h_spaceName.value = spaceName;
  target.p_h_productDt.value = productDt;
  target.p_h_stationName.value = stationName;
  target.p_h_memberIdx.value = memberIdx;
  target.p_h_deviceIdx.value = deviceIdx;
  target.deviceType.value = "IAQ";

  target.submit();

  newWindow.focus();
}

function initDataTableCustom() {
  var fontColor = "";
  var searchGroupId = "";

  var table = $("#iaqTable").DataTable({
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
    dom: '<"top"lfB>rt<"bottom"ip><"clear">',
    buttons: [
      {
        extend: "csv",
        charset: "UTF-8",
        text: "엑셀 다운로드",
        footer: false,
        bom: true,
        filename: "기기정보_다운로드",
        className: "btn-primary btn excelDownBtn",
      },
    ],
    ajax: {
      url: "/api/collection/list/iaq",
      type: "GET"
    },
    columns: [
      { data: "" },
      { data: "serial" },
      { data: "dataTime" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
      { data: "" },
    ],
    createdRow: function (row, data, dataIndex) {
      $(row).attr("id", "row-" + data.serial);

      if (data.receiveFlag == false)
        $(row).attr("style", "color: #929498 !important");
    },
    columnDefs: [
      {
        targets: 0,
        orderable: false,
        render: function (data, type, full, meta) {
          return meta.row + meta.settings._iDisplayStart + 1;
        },
      },
      {
        targets: 1,
        orderable: true,
        render: function (data, type, full, meta) {
          var parentSpaceName = full.parentSpaceName;

          if (
            !parentSpaceName ||
            parentSpaceName == null ||
            parentSpaceName == "undefined"
          )
            parentSpaceName = "";

          var spaceName = full.spaceName;

          if (!spaceName || spaceName == null || spaceName == "undefined")
            spaceName = "";

          var linkHtml = "";
          if (full.sensor == null) linkHtml += full.serial;
          else
            linkHtml +=
              "<span style='cursor: pointer; color: blue;' onClick='viewData2(\"" +
              full.serial +
              '","' +
              parentSpaceName +
              '","' +
              spaceName +
              '","' +
              full.productDt +
              '","' +
              full.stationName +
              '","' +
              full.memberIdx +
              '","' +
              full.deviceIdx +
              "\");', 'sum'>" +
              full.serial +
              "</span>";

          return linkHtml;
        },
      },
      {
        targets: 2,
        orderable: true,
        render: function (data, type, full, meta) {
//          var date = "";
//          var convertTime = "";
          var fontWeight = "";
//
          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";
//
//          if (full.timestamp == null) {
//            convertTime = "";
//          } else {
//            date = new Date(full.timestamp * 1000);
//            var year = date.getFullYear();
//
//            var month = date.getMonth() + 1;
//            var day = date.getDate();
//            var hours = date.getHours();
//            var minutes = date.getMinutes();
//            var seconds = date.getSeconds();
//
//            if (month < 10) month = "0" + month;
//            if (day < 10) day = "0" + day;
//            if (hours < 10) hours = "0" + hours;
//            if (minutes < 10) minutes = "0" + minutes;
//            if (seconds < 10) seconds = "0" + seconds;
//
//            convertTime =
//              year +
//              "-" +
//              month +
//              "-" +
//              day +
//              " " +
//              hours +
//              ":" +
//              minutes +
//              ":" +
//              seconds;
//          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            "; '>" +
            full.dataTime +
            "</font>"
          );
        },
      },
      {
        targets: 3,
        orderable: false,
        render: function (data, type, full, meta) {
          var pm10 = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.pm10 == null) {
            pm10 = "";
            fontColor = "";
          } else {
            pm10 = full.sensor.pm10;
            fontColor = txtColor(full.sensor.cici_pm10, full.receiveFlag);
          }
          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            pm10 +
            "</font>"
          );
        },
      },
      {
        targets: 4,
        orderable: false,
        render: function (data, type, full, meta) {
          var pm25 = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.pm25 == null) {
            pm25 = "";
            fontColor = "";
          } else {
            pm25 = full.sensor.pm25;
            fontColor = txtColor(full.sensor.cici_pm25, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            pm25 +
            "</font>"
          );
        },
      },
      {
        targets: 5,
        orderable: false,
        render: function (data, type, full, meta) {
          var co2 = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.co2 == null) {
            co2 = "";
            fontColor = "";
          } else {
            co2 = full.sensor.co2;
            fontColor = txtColor(full.sensor.cici_co2, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            co2 +
            "</font>"
          );
        },
      },
      {
        targets: 6,
        orderable: false,
        render: function (data, type, full, meta) {
          var voc = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.voc == null) {
            voc = "";
            fontColor = "";
          } else {
            voc = full.sensor.voc;
            fontColor = txtColor(full.sensor.cici_voc, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            voc +
            "</font>"
          );
        },
      },
      {
        targets: 7,
        orderable: false,
        render: function (data, type, full, meta) {
          var noise = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.noise == null) {
            noise = "";
            fontColor = "";
          } else {
            noise = full.sensor.noise;
            fontColor = txtColor(full.sensor.cici_noise, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            noise +
            "</font>"
          );
        },
      },
      {
        targets: 8,
        orderable: false,
        render: function (data, type, full, meta) {
          var temp = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.temp == null) {
            temp = "";
            fontColor = "";
          } else {
            temp = full.sensor.temp;
            fontColor = txtColor(full.sensor.cici_temp, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            temp +
            "</font>"
          );
        },
      },
      {
        targets: 9,
        orderable: false,
        render: function (data, type, full, meta) {
          var humi = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.humi == null) {
            humi = "";
            fontColor = "";
          } else {
            humi = full.sensor.humi;
            fontColor = txtColor(full.sensor.cici_humi, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            humi +
            "</font>"
          );
        },
      },
      {
        targets: 10,
        orderable: false,
        render: function (data, type, full, meta) {
          var cici = "";
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.cici == null) {
            cici = "";
            fontColor = "";
          } else {
            cici = full.sensor.cici;
            fontColor = txtColor(full.sensor.cici, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            cici +
            "</font>"
          );
        },
      },
      {
        targets: 11,
        orderable: true,
        render: function (data, type, full, meta) {
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";'>" +
            full.stationName +
            "</font>"
          );
        },
      },
      {
        targets: 12,
        orderable: true,
        render: function (data, type, full, meta) {
          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          return (
            "<font style='font-weight:" +
            fontWeight +
            "; '>" +
            full.productDt +
            "</font>"
          );
        },
      },
      {
        targets: 13,
        render: function (data, type, full, meta) {
          var fontWeight = "";
          let createDt = "" + full.createDt;
          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          return (
            "<font style='font-weight:" +
            fontWeight +
            "; '>" +
            createDt.substring(0, 10) +
            "</font>"
          );
        },
      },
      {
        targets: 14,
        orderable: true,
        render: function (data, type, full, meta) {
          var groupCompanyName = full.groupCompanyName;

          if (!groupCompanyName || groupCompanyName == null ||groupCompanyName == "undefined")
        	  groupCompanyName = "";

          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";'>" +
            groupCompanyName +
            "</font>"
          );
        },
      },
      {
        targets: 15,
        orderable: true,
        render: function (data, type, full, meta) {
          var etc = full.etc;

          if (!etc || etc == null || etc == "undefined") etc = "";

          var fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          return (
            "<font style='font-weight:" + fontWeight + ";'>" + etc + "</font>"
          );
        },
      },
      {
        targets: 16,
        render: function (data, type, full, meta) {
          return (
            "<span style='cursor: pointer;' data-member-idx='" +
            full.memberIdx +
            "' onclick='goMemberDetailPage(" +
            full.memberIdx +
            ", " +
            full.deviceIdx +
            ")'><strong>상세 보기</strong></span>"
          );
        },
      },
      {
        targets: 17,
        visible: false,
        render: function (data, type, full, meta) {
          return full.userId;
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
          var parentSpaceName = full.parentSpaceName;

          if (
            !parentSpaceName ||
            parentSpaceName == null ||
            parentSpaceName == "undefined"
          ) {
            parentSpaceName = "";
          }

          return parentSpaceName;
        },
      },
      {
        targets: 20,
        visible: false,
        render: function (data, type, full, meta) {
          var spaceName = full.spaceName;

          if (!spaceName || spaceName == null || spaceName == "undefined")
            spaceName = "";

          return spaceName;
        },
      },
      {
        targets: 21,
        visible: false,
        render: function (data, type, full, meta) {
          var receiveFlag = full.receiveFlag;

          if (!receiveFlag || receiveFlag == null || receiveFlag == "undefined")
            receiveFlag = false;

          return receiveFlag;
        },
      },
    ],
    drawCallback: function (settings) {
      if (settings.json != null) {
        var js = settings.json.data;
        var reCnt = 0;
        var unReCnt = 0;
        for (var idx in js) {
          if (searchGroupId != "") {
            if (js[idx].receiveFlag && "g_" + js[idx].groupId == searchGroupId)
              reCnt++;
            else if (
              !js[idx].receiveFlag &&
              "g_" + js[idx].groupId == searchGroupId
            )
              unReCnt++;
          } else {
            if (js[idx].receiveFlag) reCnt++;
            else if (!js[idx].receiveFlag) unReCnt++;
          }
        }

        var txtGroupId =
          searchGroupId == ""
            ? "전체"
            : $("#select2-searchGroup-container").attr("title");
        $("#msgTxtTotal").html(
          txtGroupId +
            " " +
            (reCnt + unReCnt) +
            "개 (" +
            ((reCnt + unReCnt) / (reCnt + unReCnt)) * 100 +
            "%)"
        );
        $("#msgTxtReceive").html(
          "수신  " +
            reCnt +
            "개 (" +
            ((reCnt / (reCnt + unReCnt)) * 100).toFixed(1) +
            "%)"
        );
        $("#msgTxtUnReceive").html(
          "미수신  " +
            unReCnt +
            "개 (" +
            ((unReCnt / (reCnt + unReCnt)) * 100).toFixed(1) +
            "%)"
        );
      }
    },
  });

  $(".search_bottom input").unbind().bind("keyup", function () {
      var colIndex = document.querySelector("#searchType").selectedIndex;

      if (colIndex == 0) {
      table.search(this.value).draw();
//        table.column(0).search("").draw();
//        table.column(1).search("").draw();
//        table.column(2).search("").draw();
//        table.column(3).search("").draw();
//        table.column(4).search("").draw();
//        table.column(5).search("").draw();
//        table.column(6).search("").draw();
//        table.column(7).search("").draw();
//        table.column(8).search("").draw();
//        table.column(9).search("").draw();
//        table.column(10).search("").draw();
//        table.column(11).search("").draw();
//        table.column(12).search("").draw();
//        table.column(13).search("").draw();
//        table.column(14).search("").draw();
//        table.column(15).search("").draw();
//        table.column(16).search("").draw();
//        table.column(17).search("").draw();
//        table.column(18).search("").draw();
//        table.column(19).search("").draw();
//        table.column(20).search("").draw();
//        table.column(21).search("").draw();
      } else if (colIndex == 1) table.column(1).search(this.value).draw();
      else if (colIndex == 2) table.column(11).search(this.value).draw();
      else if (colIndex == 3) table.column(17).search(this.value).draw();
      else if (colIndex == 4) table.column(15).search(this.value).draw();
      else if (colIndex == 5) table.column(14).search(this.value).draw();
    });

  $("#searchType").change(function () {
    var colIndex = document.querySelector("#searchType").selectedIndex;

    table.column(1).search("").draw();
    table.column(2).search("").draw();
    table.column(3).search("").draw();
    table.column(4).search("").draw();
    table.column(5).search("").draw();
    table.column(6).search("").draw();
    table.column(7).search("").draw();
    table.column(8).search("").draw();
    table.column(9).search("").draw();
    table.column(10).search("").draw();
    table.column(11).search("").draw();
    table.column(12).search("").draw();
    table.column(13).search("").draw();
    table.column(14).search("").draw();
    table.column(15).search("").draw();
    table.column(16).search("").draw();
    table.column(17).search("").draw();
    table.column(18).search("").draw();
    table.column(19).search("").draw();
    table.column(20).search("").draw();
    table.column(21).search("").draw();

    $("#searchValue").val("");
  });

  $("#searchGroup").change(function () {
    searchGroupId = "g_" + this.value;

    $(".filterDiv").each(function (index, item) {
      $(item).removeClass("filter-cli");
    });
    $(".filterDiv").first().addClass("filter-cli");

    table.column(18).search(searchGroupId).draw();
    table.column(21).search("").draw();
  });

  $("#searchParentSpace").change(function () {
    var spaceVal = $(this).val();
    var spaceVal_arr = spaceVal.split("|");
    var spaceVal_idx = spaceVal_arr[0];
    var spaceVal_name = spaceVal_arr[1];
    var search_space_val = "";

    if (!spaceVal) search_space_val = "";
    else search_space_val = spaceVal_name;

    table.column(19).search(search_space_val).draw();
  });

  $("#searchSpace").change(function () {
    table.column(20).search(this.value).draw();
  });

  $("#searchTestYn").click(function () {
    if ($("#searchTestYn").prop("checked") == true) $(this).val("Y");
    else $(this).val("N");

    if ($(this).val() == "Y") table.column(16).search("Y").draw();
    else table.column(16).search("").draw();
  });

  $("#mttDiv").click(function () {
    table.column(21).search("").draw();
  });
  $("#mtrDiv").click(function () {
    table.column(21).search(true).draw();
  });
  $("#mturDiv").click(function () {
    table.column(21).search(false).draw();
  });

  table
    .on("order.dt search.dt", function () {
      table
        .column(0, { search: "applied", order: "applied" })
        .nodes()
        .each(function (cell, i) {
          cell.innerHTML = i + 1;
        });
    })
    .draw();

  $(".dt-buttons").css("position", "relative");
  $(".excelDownBtn").css({
    position: "absolute",
    right: "0",
    top: "-40px",
  });

  $("#iaqTable_filter").hide();
}

function getGroupList() {
  var optHtml = "";
  $.ajax({
    method: "GET",
    url: "/system/group/get",
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&searchValue2=IAQ",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";
      for (var i = 0; i < param.data.length; i++) {
        optHtml +=
          "<option value='" +
          param.data[i].groupId +
          "'>" +
          param.data[i].groupName +
          "</option>";
      }
      $("#searchGroup").html(optHtml);
    },
  });
}

function getHighSpace() {
  var optHtml = "";
  $.ajax({
    method: "GET",
    url: "/system/space/high",
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&deviceTypeIdx=1",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";
      for (var i = 0; i < param.data.length; i++)
        optHtml +=
          "<option value='" +
          param.data[i].idx +
          "|" +
          param.data[i].spaceName +
          "'>" +
          param.data[i].spaceName +
          "</option>";

      $("#searchParentSpace").html(optHtml);
    },
  });
}

function getSpace(val) {
  var optHtml = "";
  var spaceVal = val;
  var spaceVal_arr = spaceVal.split("|");
  var spaceVal_idx = spaceVal_arr[0];
  var spaceVal_name = spaceVal_arr[1];

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

        for (var i = 0; i < param.data.length; i++) {
          var idx = param.data[i].idx;
          var spaceName = param.data[i].spaceName;
          optHtml +=
            "<option value='" +
            param.data[i].spaceName +
            "'>" +
            spaceName +
            "</option>";
        }

        $("#searchSpace").html(optHtml);
      },
    });
  }
}

function trCrolor(val) {
  var serialColor = "";
  if (val == false) serialColor = "#929498";
  else serialColor = "";

  return serialColor;
}

function goMemberDetailPage(memberIdx, deviceIdx) {
  var url =
    "/system/member/device/detail/" + memberIdx + "?deviceIdx=" + deviceIdx;
  window.open(url, "_blank");
}

$().ready(function () {
  $(".select2").select2();
  $(".select2bs4").select2({
    theme: "bootstrap4",
  });

  $(".select2-container").css("display", "inline-block");

  initDataTableCustom();
  getGroupList();
  getHighSpace();

  $("#iaqTable_filter").hide();

  $("#searchTestYn").click(function () {
    var chkTestYn = $("#searchTestYn");
    if (chkTestYn.prop("checked") == true) $(this).val("Y");
    else $(this).val("N");
  });

  $(".filterDiv").click(function () {
    $(".filterDiv").each(function (index, item) {
      $(item).removeClass("filter-cli");
    });

    $(this).addClass("filter-cli");
  });
});
