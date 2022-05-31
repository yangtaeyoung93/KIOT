/**
 * 수집정보 > OAQ수집정보
 */
let historyData;
let previousSearchType=0;

function viewData2(
  serialNum,
  parentSpaceName,
  spaceName,
  productDt,
  stationName,
  memberIdx,
  deviceIdx
) {
  let winWidth = 400;
  let winHeight = 400;

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

  const target = document.getElementById("detail_info_form");
  const act_url = "/collection/popup";
  target.target = "collection_detail_pop";
  target.action = act_url;
  target.p_h_serialNum.value = serialNum;
  target.p_h_parentSpaceName.value = parentSpaceName;
  target.p_h_spaceName.value = spaceName;
  target.p_h_productDt.value = productDt;
  target.p_h_stationName.value = stationName;
  target.p_h_memberIdx.value = memberIdx;
  target.p_h_deviceIdx.value = deviceIdx;
  target.deviceType.value = "OAQ";

  target.submit();

  newWindow.focus();
}

function initDataTableCustom() {
  let fontColor = "";
  let searchGroupId = "";
  let searchMaster = document.getElementById('searchMaster');
  let masterIdx = searchMaster.value;
  const table = $("#oaqTable").DataTable({
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
        filename: "임시_기기정보_다운로드",
        className: "btn-primary btn excelDownBtn",
      },
    ],
    ajax: {
      url: "/api/collection/list/oaq",
      type: "GET",
    },
    columns: [
      { orderable: false },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" },
      { data: "serial" }
    ],
    createdRow: function (row, data, dataIndex) {
      $(row).attr("id", "row-" + data.serial);

      if (data.receiveFlag == false)
        $(row).attr("style", "color: #929498 !important");
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
        targets: 1, //데이터테이블 자체 RowNum
        orderable: true,
        render: function (data, type, full, meta) {
          let parentSpaceName = full.parentSpaceName;

          if (
            !parentSpaceName ||
            parentSpaceName == null ||
            parentSpaceName == "undefined"
          ) {
            parentSpaceName = "";
          }

          let spaceName = full.spaceName;

          if (!spaceName || spaceName == null || spaceName == "undefined") {
            spaceName = "";
          }

          let linkHtml = "";
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
              "\");'>" +
              full.serial +
              "</span>";

          return linkHtml;
        },
      },
      {
        targets: 2,
        orderable: true,
        render: function (data, type, full, meta) {
          let date = "";
          let convertTime = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.timestamp == null) {
            convertTime = "";
          } else {
            date = new Date(full.timestamp * 1000);
            const year = date.getFullYear();

            let month = date.getMonth() + 1;
            let day = date.getDate();
            let hours = date.getHours();
            let minutes = date.getMinutes();
            let seconds = date.getSeconds();

            if (month < 10) month = "0" + month;
            if (day < 10) day = "0" + day;
            if (hours < 10) hours = "0" + hours;
            if (minutes < 10) minutes = "0" + minutes;
            if (seconds < 10) seconds = "0" + seconds;

            convertTime =
              year +
              "-" +
              month +
              "-" +
              day +
              " " +
              hours +
              ":" +
              minutes +
              ":" +
              seconds;
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";'>" +
            convertTime +
            "</font>"
          );
        },
      },
      {
        targets: 3,
        orderable: false,
        render: function (data, type, full, meta) {
          let pm10 = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.pm10 == null) {
            pm10 = "";
            fontColor = "";
          } else {
            pm10 = full.sensor.pm10;
            fontColor = txtColor(full.sensor.coci_pm10, full.receiveFlag);
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
          let pm25 = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.pm25 == null) {
            pm25 = "";
            fontColor = "";
          } else {
            pm25 = full.sensor.pm25;
            fontColor = txtColor(full.sensor.coci_pm25, full.receiveFlag);
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
          let noise = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.noise == null) {
            noise = "";
            fontColor = "";
          } else {
            noise = full.sensor.noise;
            fontColor = txtNOISEColor(full.sensor.noise, full.receiveFlag);
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
        targets: 6,
        orderable: false,
        render: function (data, type, full, meta) {
          let temp = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.temp == null) {
            temp = "";
            fontColor = "";
          } else {
            temp = full.sensor.temp;
            fontColor = txtColor(full.sensor.coci_temp, full.receiveFlag);
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
        targets: 7,
        orderable: false,
        render: function (data, type, full, meta) {
          let humi = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.humi == null) {
            humi = "";
            fontColor = "";
          } else {
            humi = full.sensor.humi;
            fontColor = txtColor(full.sensor.coci_humi, full.receiveFlag);
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
        targets: 8,
        orderable: false,
        render: function (data, type, full, meta) {
          let coci = "";
          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          if (full.sensor == null || full.sensor.coci == null) {
            coci = "";
            fontColor = "";
          } else {
            coci = full.sensor.coci;
            fontColor = txtColor(full.sensor.coci, full.receiveFlag);
          }

          return (
            "<font style='font-weight:" +
            fontWeight +
            ";  color:" +
            fontColor +
            "'>" +
            coci +
            "</font>"
          );
        },
      },
      {
        targets: 9,
        orderable: true,
        render: function (data, type, full, meta) {
          let fontWeight = "";

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
        targets: 10,
        orderable: true,
        render: function (data, type, full, meta) {
          let fontWeight = "";

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
        targets: 11,
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
        targets: 12,
        orderable: true,
        render: function (data, type, full, meta) {
          let groupCompanyName = full.groupCompanyName;

          if (
            !groupCompanyName ||
            groupCompanyName == null ||
            groupCompanyName == "undefined"
          )
            groupCompanyName = "";

          let fontWeight = "";

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
        targets: 13,
        orderable: true,
        render: function (data, type, full, meta) {
          let etc = full.etc;

          if (!etc || etc == null || etc == "undefined") etc = "";

          let fontWeight = "";

          if (full.receiveFlag == true) fontWeight = "bold";
          else fontWeight = "normal";

          return (
            "<font style='font-weight:" + fontWeight + ";'>" + etc + "</font>"
          );
        },
      },
      {
        targets: 14,
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
        targets: 15,
        visible: false,
        render: function (data, type, full, meta) {
          return full.userId;
        },
      },
      {
        targets: 16,
        visible: false,
        render: function (data, type, full, meta) {
          return "g_" + full.groupId;
        },
      },
      {
        targets: 17,
        visible: false,
        render: function (data, type, full, meta) {
          let parentSpaceName = full.parentSpaceName;

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
        targets: 18,
        visible: false,
        render: function (data, type, full, meta) {
          let spaceName = full.spaceName;

          if (!spaceName || spaceName == null || spaceName == "undefined")
            spaceName = "";

          return spaceName;
        },
      },
      {
        targets: 19,
        visible: false,
        render: function (data, type, full, meta) {
          let receiveFlag = full.receiveFlag;

          if (!receiveFlag || receiveFlag == null || receiveFlag == "undefined")
            receiveFlag = false;

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
    ],
    drawCallback: function (settings) {
      if (settings.json != null) {
        const js = settings.json.data;
        let reCnt = 0;
        let unReCnt = 0;
        for (let idx in js) {
          if ($('#searchGroup').val() != "") { //선택한 그룹이 있는 경우
                if (js[idx].receiveFlag && "g_" + js[idx].groupId == searchGroupId) reCnt++;
                else if (  !js[idx].receiveFlag &&  "g_" + js[idx].groupId == searchGroupId) unReCnt++;
          }else { //선택한 그룹이 없는 경우
                if($('#searchMaster').val() != ""){ //선택한 상위그룹이 있는 경우
                    if(js[idx].receiveFlag && js[idx].masterIdx == $('#searchMaster').val()){ reCnt++;}
                    else if (!js[idx].receiveFlag && js[idx].masterIdx == $('#searchMaster').val()) unReCnt++;
                }else{
                    if (js[idx].receiveFlag) reCnt++;
                    else if (!js[idx].receiveFlag) unReCnt++;
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
            "개 (" +
            receivePercentage +
            "%)"
        );
        $("#msgTxtUnReceive").html(
          "미수신  " +
            unReCnt +
            "개 (" +
            unreceivePercentage +
            "%)"
        );
      }
    },
  });

  $(".search_bottom input")
    .unbind()
    .bind("keyup", function () {
      const colIndex = document.querySelector("#searchType").selectedIndex;

      if (colIndex == 0) {
        table.search(this.value).draw();
      } else if (colIndex == 1) table.column(1).search(this.value).draw();
      else if (colIndex == 2) table.column(9).search(this.value).draw();
      else if (colIndex == 3) table.column(15).search(this.value).draw();
      else if (colIndex == 4) table.column(13).search(this.value).draw();
      else if (colIndex == 5) table.column(12).search(this.value).draw();
    });

  $("#searchType").change(function () {
    const colIndex = document.querySelector("#searchType").selectedIndex;
    switch(previousSearchType){
           case 0 : table.search("").draw(); break;
           case 1 : table.column(1).search("").draw(); break;
           case 2 : table.column(9).search("").draw(); break;
           case 3 : table.column(15).search("").draw(); break;
           case 4 : table.column(13).search("").draw(); break;
           case 5 : table.column(12).search("").draw(); break;
           }
    previousSearchType = colIndex;
    $("#searchValue").val("");
  });

  $("#searchMaster").change(function () {
  $('#searchGroup').val('');
    table.column(16).search("").draw();
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

  });

  $("#searchGroup").change(function () {
    searchGroupId = "g_" + this.value;
    if(this.value == "") searchGroupId = "";
    $(".filterDiv").each(function (index, item) {
      $(item).removeClass("filter-cli");
    });
    $(".filterDiv").first().addClass("filter-cli");

    table.column(16).search(searchGroupId).draw();
    table.column(19).search("").draw();
  });

  $("#searchParentSpace").change(function () {
    const spaceVal = $(this).val();
    const spaceVal_arr = spaceVal.split("|");
    const spaceVal_idx = spaceVal_arr[0];
    const spaceVal_name = spaceVal_arr[1];
    let search_space_val = "";

    if (!spaceVal) search_space_val = "";
    else search_space_val = spaceVal_name;

    table.column(17).search(search_space_val).draw();
  });

  $("#searchSpace").change(function () {
    table.column(18).search(this.value).draw();
  });

  $("#searchTestYn").click(function () {
    if ($("#searchTestYn").prop("checked") == true) $(this).val("Y");
    else $(this).val("N");

    if ($(this).val() == "Y") table.column(14).search("Y").draw();
    else table.column(14).search("").draw();
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

  $("#mttDiv").click(function () {
    table.column(19).search("").draw();
  });
  $("#mtrDiv").click(function () {
    table.column(19).search(true).draw();
  });
  $("#mturDiv").click(function () {
    table.column(19).search(false).draw();
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

  $("#oaqTable_filter").hide();
}
function getMasterList() {
  var optHtml = "";
  $.ajax({
    method: "GET",
    url: "/system/master/get",
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&searchValue2=OAQ",
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
  let url;
  if(masterId == undefined || masterId == ""){
    url =  "/system/group/get";
  }else{
    url = `/system/master/get/${masterId}/m`;
  }
  $.ajax({
    method: "GET",
    url: url,
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&searchValue2=OAQ",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";

      for (let i = 0; i < param.data.length; i++)
        optHtml +=
          "<option value='" +
          param.data[i].groupId +
          "'>" +
          param.data[i].groupName +
          "</option>";

      $("#searchGroup").html(optHtml);
    },
  });
}

function getHighSpace() {
  let optHtml = "";
  $.ajax({
    method: "GET",
    url: "/system/space/high",
    contentType: "application/json; charset=utf-8",
    data: "searchUseYn=Y&deviceTypeIdx=2",
    success: function (param) {
      optHtml += "<option value=''>전체</option>";

      for (let i = 0; i < param.data.length; i++)
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
  let optHtml = "";
  const spaceVal = val;
  const spaceVal_arr = spaceVal.split("|");
  const spaceVal_idx = spaceVal_arr[0];
  const spaceVal_name = spaceVal_arr[1];

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
          const idx = param.data[i].idx;
          const spaceName = param.data[i].spaceName;
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

function goMemberDetailPage(memberIdx, deviceIdx) {
  var url =
    "/system/member/device/detail/" + memberIdx + "?deviceIdx=" + deviceIdx;
  window.open(url, "_blank");
}

$().ready(function () {
$(function() {
  var marginTop = parseInt( $(".main-sidebar").css('margin-top') );
  $(window).scroll(function(e) {
    $(".main-sidebar").css("margin-top", marginTop - $(this).scrollTop() );
  });
});

  $(".select2").select2();
  $(".select2bs4").select2({
    theme: "bootstrap4",
  });

  $(".select2-container").css("display", "inline-block");

  initDataTableCustom();
  getMasterList();
  getGroupList();
  getHighSpace();

  $("#oaqTable_filter").hide();

  $("#searchTestYn").click(function () {
    const chkTestYn = $("#searchTestYn");

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
