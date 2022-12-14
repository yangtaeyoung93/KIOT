const obj = {};
let fileFlag;
let mainImageFlag;
let eastImageFlag;
let westImageFlag;
let southImageFlag;
let northImageFlag;

function getDeviceInfo(val) {

  $("#clientFileName").html("");
  $("#updateVentDiv").hide();

  fileFlag = $("#device" + val).attr("data-fileFlag");
  mainImageFlag = $("#device" + val).attr("data-mainImageFlag");
  eastImageFlag = $("#device" + val).attr("data-eastImageFlag");
  westImageFlag = $("#device" + val).attr("data-westImageFlag");
  southImageFlag = $("#device" + val).attr("data-southImageFlag");
  northImageFlag = $("#device" + val).attr("data-northImageFlag");

  const fileName = $("#device" + val).attr("data-fileName");
  $("#device" + val).attr("data-mainImageName");
  $("#device" + val).attr("data-eastImageName");
  $("#device" + val).attr("data-westImageName");
  $("#device" + val).attr("data-southImageName");
  $("#device" + val).attr("data-northImageName");

  if (fileFlag === "1") {
    $("#fileDiv").show();
    $("#clientFileName").html(fileName);
    $("#fileLink").prop('href', "/system/member/device/ajax/fileDownload/"
        + $("input:radio[name='chkDevice']:checked").val());
  } else {
    $("#fileDiv").hide();
  }

  if (mainImageFlag === "1") {
    $("#blahMain").attr("src", "/system/member/device/ajax/fileDownload/" + $(
        "input:radio[name='chkDevice']:checked").val() + "?type=Main");
  } else {
    $("#blahMain").attr("src",
        "http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png");
  }

  if (eastImageFlag === "1") {
    $("#blahEast").attr("src", "/system/member/device/ajax/fileDownload/" + $(
        "input:radio[name='chkDevice']:checked").val() + "?type=East");
  } else {
    $("#blahEast").attr("src",
        "http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png");
  }

  if (westImageFlag === "1") {
    $("#blahWest").attr("src", "/system/member/device/ajax/fileDownload/" + $(
        "input:radio[name='chkDevice']:checked").val() + "?type=West");
  } else {
    $("#blahWest").attr("src",
        "http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png");
  }

  if (southImageFlag === "1") {
    $("#blahSouth").attr("src", "/system/member/device/ajax/fileDownload/" + $(
        "input:radio[name='chkDevice']:checked").val() + "?type=South");
  } else {
    $("#blahSouth").attr("src",
        "http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png");
  }

  if (northImageFlag === "1") {
    $("#blahNorth").attr("src", "/system/member/device/ajax/fileDownload/" + $(
        "input:radio[name='chkDevice']:checked").val() + "?type=North");
  } else {
    $("#blahNorth").attr("src",
        "http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png");
  }

  const equiName = $("#device" + val).attr("data-equiName");
  const equiAddr = $("#device" + val).attr("data-equiAddr");
  const equiAddr2 = $("#device" + val).attr("data-equiAddr2");
  const spaceName = $("#device" + val).attr("data-spaceName");
  const Lat = $("#device" + val).attr("data-Lat");
  const Lon = $("#device" + val).attr("data-Lon");
  const dCode = $("#device" + val).attr("data-dCode") ?
      $("#device" + val).attr("data-dCode") : "0000000000";
  const setTemp = $("#device" + val).attr("data-set-temp");
  const airMapUse = $("#device" + val).attr("data-airMapUse") === 'Y';
  const deviceType = $("#device" + val).attr("data-deviceType");
  const createDt = $("#device" + val).attr("data-createdt");
  const etc = $("#device" + val).attr("data-etc");

  const departName = $("#device" + val).attr("data-departName");
  const departPhoneNumber = $("#device" + val).attr("data-departPhoneNumber");
  const salesName = $("#device" + val).attr("data-salesName");

  const relatedDevice = $("#device" + val).attr("data-related-device");

  let txtVentHtml = "";

  $("VentDiv").html("");

  $("#equip_dt").val($("#device" + val).attr("data-equidt"));
  $("#equipName").val(equiName);
  $("#equipAddr").val(equiAddr);
  $("#equipAddr2").val(equiAddr2);
  $("#stationName").val(spaceName);

  getSiList(dCode);

  $("input:checkbox[id='airMapUse']").prop("checked", airMapUse);
  $("#Lat").val(Lat);
  $("#Lon").val(Lon);
  $("#createDt").val(createDt.substring(0, 19));
  $("#etc").val(etc);
  $('#setTemp').val(setTemp);
  $("#departName").val(departName);
  $("#departPhoneNumber").val(departPhoneNumber);
  $("#salesName").val(salesName);

  if (deviceType === "IAQ") {
    $('#setTempDiv').css('display', 'block');
    $.ajax({
      url: "/system/member/device/ajax/ventDevice",
      type: "GET",
      async: false,
      data: "idx=" + val,
      success: function (param) {
        for (let i = 0; i < param.data.length; i++) {
          const serialNum = param.data[i].serialNum;
          const aiMode = param.data[i].aiMode;
          let aiModeStr = "";
          const createDt = param.data[i].createDt;
          if (aiMode === "0") {
            aiModeStr = "AI Mode : Off";
          } else {
            aiModeStr = "AI Mode : On";
          }
          txtVentHtml += "<div class='row col-12 mt10'>" + serialNum + " - "
              + aiModeStr + " - ???????????? : " + createDt.substring(0, 19) + "</div>";
        }
        $("#txtVentTitle").show();
        $("#ventArea").show();
        $("#VentupdateBtn").show();
        $("#VentDiv").html(txtVentHtml);

      }
    });

    if (relatedDevice !== "") {
      $('#AddRelated').html(
          "<div class='row col-12 mt10' style='height: 38px;line-height: 35px;'>"
          + "<span style='float:left' id='related'>" + relatedDevice + "</span>" +
          "<input type='button' class='btn btn-primary ml10' value='??????' style='float:right;' onclick='deleteRelated()'/>" +
          "</div>");
    }

    $.ajax({
      url: "/system/device/oaq",
      type: "GET",
      async: false,
      success: function (data) {
        let relatedHtml = "";
        for (let i = 0; i < data.data.length; i++) {
          const serialNum = data.data[i].serialNum;
          relatedHtml += "<option value='" + serialNum + "'>" + serialNum
              + "</option>";
        }

        $("#relatedSelectArea").html(relatedHtml);
      }
    })
    $("#txtRelatedTitle").show();
    $('#NETbox').show();
    $('#NETboxTitle').show();
    //$("#updateRelatedDiv").show();
    //$('#setTempDiv').show();

  } else {
    $("#VentDiv").html("");
    $("#ventArea").hide();
    $("#txtVentTitle").hide();
    $("#txtRelatedTitle").hide();
    $("#VentupdateBtn").hide();
    //$("#updateRelatedDiv").hide();
    //$('#setTempDiv').hide();
    $('#NETbox').hide();
    $('#NETboxTitle').hide();
  }

  $("#beforeIdx").val(val);
  parentSpaceList(val);
  selecetParentSpace();
  autoheight();
  $("#reportArea").show();
  $("#imageArea").show();
}

function datePickerSetting() {
  $.datepicker.regional['kr'] = {
    monthNames: ['1 ???', '2 ???', '3 ???', '4 ???', '5 ???', '6 ???', '7 ???', '8 ???', '9 ???',
      '10 ???', '11 ???', '12 ???'],
    monthNamesShort: ['1 ???', '2 ???', '3 ???', '4 ???', '5 ???', '6 ???', '7 ???', '8 ???',
      '9 ???', '10 ???', '11 ???', '12 ???'],
    dayNames: ['?????????', '?????????', '?????????', '?????????', '?????????', '?????????', '?????????'],
    dayNamesShort: ['???', '???', '???', '???', '???', '???', '???'],
    dayNamesMin: ['???', '???', '???', '???', '???', '???', '???'],
  };

  $.datepicker.setDefaults($.datepicker.regional['kr']);
}

function autoheight() {
  const viewVentList = $("#VentDiv > div").length;
  const parentSpaceList = $("#parentSpaceList > div").length;
  const spaceList = $("#spaceList > div").length;

  if (viewVentList < 4) {
    $("#ventArea").css("height", "auto").css("overflow-x", "none");
  } else {
    $("#ventArea").css("height", "135px").css("overflow-x", "hidden");
  }

  if (parentSpaceList < 4) {
    $("#SpaceArea").css("height", "auto").css("overflow-x", "none");
  } else {
    $("#SpaceArea").css("height", "135px").css("overflow-x", "hidden");
  }

  if (spaceList < 4) {
    $("#SpaceArea2").css("height", "auto").css("overflow-x", "none");
  } else {
    $("#SpaceArea2").css("height", "135px").css("overflow-x", "hidden");
  }
}

function getSiList(dCode) {
  let sCodeList = [];
  let html = "<option value='00'>??????</option>";
  $.ajax({
    url: "/api/dong/siList/dev",
    type: "get",
    success: function (param) {
      param.data.forEach((datas) => {
        sCodeList.push([datas.dname, datas.sdcode]);
      });
      sCodeList.forEach((sCodes) => {
        html += "<option value='" + sCodes[1] + "'>" + sCodes[0] + "</option>";
      });
      $("#sCode").html(html);
    },
    complete: function () {
      $('#sCode').val(dCode.substring(0, 2));
      getGuList($('#sCode > option:selected').val(), dCode);
    }
  });
}

function getGuList(Sdcode, dCode) {
  $("#gCode").children('option').remove();
  let gCodeList = [];
  let html = "<option value='000'>??????</option>";
  $.ajax({
    url: "/api/dong/guList/dev",
    type: "get",
    data: "searchSdcode=" + Sdcode,
    success: function (param) {
      param.data.forEach((datas) => {
        gCodeList.push([datas.dname, datas.sggcode]);
      });
      gCodeList.forEach((gCodes) => {
        html += "<option value='" + gCodes[1] + "'>" + gCodes[0] + "</option>";
      });
      $("#gCode").html(html);
    },
    complete: function () {
      $("#gCode").val(dCode.substring(2, 5));
      getDongList($('#sCode').val(), $('#gCode').val(), dCode);
    }
  });
}

function getDongList(Sdcode, Sggcode, dCode) {
  $("#dCode").children('option').remove();
  let dCodeList = [];
  let html = "<option value='0000000000'>??????</option>";
  $.ajax({
    url: "/api/dong/dongList/dev",
    type: "get",
    data: "searchSdcode=" + Sdcode + "&searchSggcode=" + Sggcode,
    success: function (param) {
      if (param) {
        param.data.forEach((datas) => {
          dCodeList.push([datas.dname, datas.dcode]);
        });
        dCodeList.forEach((dCodes) => {
          html += "<option value='" + dCodes[1] + "'>" + dCodes[0]
              + "</option>";
        });
      } else {
        html = "<option value='" + 0 + "'>????????? ?????? ????????????.</option>";
      }
      $("#dCode").html(html);
    },
    error: function () {
      dCode = "0000000000";
    },
    complete: function () {
      if (dCode == null || dCode.substring(5, 10) === "00000") {
        dCode = "0000000000"
      }
      $("#dCode").val(dCode);
    }
  });
}

function addVent(idx) {
  const ventDeviceIdx = idx;
  const memberIdx = $("#memberIdx").val();
  const iaqDeviceIdx = $("input:radio[name='chkDevice']:checked").val();

  const obj = {
    memberIdx: memberIdx,
    iaqDeviceIdx: iaqDeviceIdx,
    ventDeviceIdx: ventDeviceIdx
  }

  $.ajax({
    url: "/system/member/device/insert/vent",
    type: "POST",
    data: JSON.stringify(obj),
    contentType: "application/json; charset=utf-8",
    success: function () {
      alert("?????? ???????????????.");
      location.reload();
    }
  })

}

function deleteVent(idx) {
  const confirmVal = confirm("????????? ?????????????????? ????????????????????????? ");
  if (confirmVal) {
    $.ajax({
      url: "/system/member/device/delete/vent",
      type: "DELETE",
      data: JSON.stringify({
        ventDeviceIdx: idx,
        memberIdx: $("#memberIdx").val(),
        iaqDeviceIdx: $("input:radio[name='chkDevice']:checked").val()
      }),
      contentType: "application/json; charset=utf-8",
      success: function () {
        alert("?????? ???????????????.");
        location.reload();
      }
    })
  } else {
    return false;
  }
}

function parentSpaceList(val) {
  let parentSpaceHtml = "";
  const deviceTypeidx = $("#device" + val).attr("data-deviceTypeIdx")
  const parentSpaceName = $("#device" + val).attr("data-spaceFull");
  const parentSpaceName_split = parentSpaceName.split(">");
  let selected = "";

  $.ajax({
    url: "/system/member/device/ajax/parent/spaces",
    type: "GET",
    async: false,
    data: "deviceTypeIdx=" + deviceTypeidx,
    success: function (param) {

      parentSpaceHtml += "<div class='row col-12 mt10 parentSpaceDiv' id='parentSpaceDiv0' style='height: 20px;line-height: 20px;'>";
      parentSpaceHtml += "<input type='radio' class='parentSpace mr10' name='parentSpace' id='parentSpace0' value='0' style='width:20px; height:20px;' onClick='selecetParentSpace(this.value);' />";
      parentSpaceHtml += "<label for='parentSpace0' id='parentSpaceLabel0'>????????????</label>";
      parentSpaceHtml += "</div>";

      for (let i = 0; i < param.data.length; i++) {
        const idx = param.data[i].idx;
        const spaceName = param.data[i].spaceName;
        if (spaceName === parentSpaceName_split[0]) {
          selected = "checked";
        } else {
          selected = "";
        }

        parentSpaceHtml += "<div class='row col-12 mt10 parentSpaceDiv' id='parentSpaceDiv"
            + idx + "' style='height: 20px;line-height: 20px;'>";
        parentSpaceHtml += "<input type='radio' class='parentSpace mr10' name='parentSpace' id='parentSpace"
            + idx + "' value='" + idx
            + "' style='width:20px; height:20px;' onClick='selecetParentSpace(this.value);' "
            + selected + " />";
        parentSpaceHtml += "<label for='parentSpace" + idx
            + "' id='parentSpaceLabel" + idx + "'>" + spaceName + "</label>";
        parentSpaceHtml += "</div>";

      }
      $("#parentSpaceList").html(parentSpaceHtml);
    }
  })
}

function selecetParentSpace(val) {
  let spaceHtml = "";
  const parentSpaceIdx = $("input:radio[name='parentSpace']:checked").val();
  const deviceIdx = $("input:radio[name='chkDevice']:checked").val();
  const chkSpaceIdx = $("#device" + deviceIdx).attr("data-spaceIdx");
  let selected = "";

  if (val === 0) {
    $("#spaceList").html("");
  } else {
    $.ajax({
      url: "/system/member/device/ajax/spaces",
      type: "GET",
      async: false,
      data: "parentSpaceIdx=" + parentSpaceIdx,
      success: function (param) {
        for (let i = 0; i < param.data.length; i++) {
          const idx = param.data[i].idx;
          const spaceName = param.data[i].spaceName;

          if (chkSpaceIdx == idx) {
            selected = "checked";
          } else {
            selected = "";
          }

          spaceHtml += "<div class='row col-12 mt10 spaceDiv' id='spaceDiv"
              + idx + "' style='height: 20px;line-height: 20px;'>";
          spaceHtml += "<input type='radio' class='space mr10' name='space' id='space"
              + idx + "' value='" + idx
              + "' style='width:20px; height:20px;' onClick='' " + selected
              + " />";
          spaceHtml += "<label for='space" + idx + "' id='spaceLabel" + idx
              + "'>" + spaceName + "</label>";
          spaceHtml += "</div>";
        }
        $("#spaceList").html(spaceHtml);
      }
    })
  }
}

function chkStation() {
  const spaceName = $("#spaceName").val();

  if (!spaceName) {
    $("#chk_txt").css("color", "red").css("font-weight", "bold")
    .text("?????????????????? ????????? ?????????.");
    return false;
  }

  $.ajax({
    url: "/check/stationName",
    type: "GET",
    async: false,
    data: "stationName=" + spaceName + "&memberIdx=" + $(
        "input:radio[name='UserMembers']:checked").val(),
    success: function (param) {

      const result_val = param.resultCode;
      if (result_val === 1) {
        $("#chk_txt").css("color", "#28a745").css("font-weight", "bold")
        .text("??????????????? ??????????????? ?????????.");
        $("#chk_id").val("1");
        return false;
      } else {
        $("#chk_txt").css("color", "red").css("font-weight", "bold")
        .text("???????????? ??????????????? ?????????. ?????? ???????????? ?????????.");
        $("#chk_id").val("0");
        return false;
      }
    }
  });
}

function readURL(input, fileType, imgFlag) {
  if (input.files && input.files[0]) {
    const reader = new FileReader();
    const filename = $("#inputImageFile" + fileType).val();
    const fileExt = filename.slice(filename.indexOf(".") + 1).toLowerCase();
    if (fileExt !== "jpg" && fileExt !== "png" && fileExt !== "gif") {
      alert("????????? ????????? (jpg, png, gif, bmp) ????????? ?????? ???????????????.");
      $("#inputImageFile" + fileType).val("");
    } else {
      reader.onload = function (e) {
        $('#' + 'blah' + fileType).attr('src', e.target.result)
        .hide().fadeIn(500);

        fileUploadAndUpdate(fileType, "inputImageFile" + fileType, imgFlag);
      }

      reader.readAsDataURL(input.files[0]);
    }
  }
}

function fileUploadAndUpdate(fileType, targetFile, imageFlag) {
  const form = $('#imageFileUploadForm')[0];
  const data = new FormData(form);
  let apiUrl;

  if (imageFlag == 1) {
    apiUrl = "fileUpdate/";
  } else {
    apiUrl = "fileUpload/";
  }

  $.ajax({
    method: "POST",
    url: "/system/member/device/ajax/"
        + apiUrl + $("#memberIdx").val() + "/" + $(
            "input:radio[name='chkDevice']:checked").val()
        + "?type=" + fileType + "&target=" + targetFile,
    processData: false,
    contentType: false,
    data: data,
    success: function (d) {
      if (d.resultCode == 3) {
        alert("jpg, jpeg, png ???????????? ?????? ??? ?????????.");
      } else {
        alert("?????? ???????????????.");

        switch (fileType) {
          case 'Main':
            mainImageFlag = 1;
            break;
          case 'East':
            eastImageFlag = 1;
            break;
          case 'West':
            westImageFlag = 1;
            break;
          case 'South':
            southImageFlag = 1;
            break;
          case 'North':
            northImageFlag = 1;
            break;
          default:
            break;
        }
      }
    }
  });
}

function deleteFile(fileType) {
  $.ajax({
    url: "/system/member/device/ajax/fileDelete/" + $(
        "input:radio[name='chkDevice']:checked").val() + "?type=" + fileType,
    type: "POST",
    contentType: "application/json; charset=utf-8",
    success: function (result) {
      if (result == 1) {
        alert("????????? ?????? ???????????????.");
        $("#blah" + fileType).attr("src",
            "http://www.clker.com/cliparts/c/W/h/n/P/W/generic-image-file-icon-hi.png");
      } else {
        alert("????????? ?????? ?????? ?????????.");
      }
    }
  })
}

function insertVentDeviceBtn() {
  const idx = $('#ventSelectArea option:selected').val();
  addVent(idx);
}

function insertRelatedDeviceBtn() {
  const serial = $('#relatedSelectArea option:selected').val();
  $('#AddRelated').html(
      "<div class='row col-12 mt10' style='height: 38px;line-height: 35px;'>"
      + "<span style='float:left' id='related'>" + serial
      + "</span>" +
      "<input type='button' class='btn btn-primary ml10' value='??????' style='float:right;' onclick='deleteRelated()'/>" +
      "</div>");

}

function deleteRelated() {
  $('#AddRelated').html("");
}

function setUnUseVentDeviceList() {
  let ventHtml = "";

  $.ajax({
    url: "/system/member/device/ajax/deviceVent",
    type: "GET",
    async: false,
    success: function (data) {
      for (let i = 0; i < data.data.length; i++) {
        const idx = data.data[i].deviceIdx;
        const serialNum = data.data[i].serialNum;
        ventHtml += "<option value='" + idx + "'>" + serialNum + "</option>";
      }

      $("#ventSelectArea").html(ventHtml);
    }
  })
}

$().ready(async function () {
  datePickerSetting();
  autoheight();
  await getSiList('0000000000');

  $('.select2').select2();
  $('.select2bs4').select2({
    theme: 'bootstrap4'
  })

  $("#sCode").change(function () {
    getGuList($('#sCode > option:selected').val(), "0000000000")
  });
  $("#gCode").change(function () {
    getDongList($('#sCode > option:selected').val(),
        $('#gCode > option:selected').val(), "0000000000")
  });

  $("#fileDiv").hide();
  $("#VentDiv").html("");
  $("#ventArea").hide();
  $("#txtVentTitle").hide();
  $("#VentupdateBtn").hide();
  $("#updateVentDiv").hide();

  $("#listBtn").click(function () {
    location.href = "/system/member/device/list";
  })

  $("#VentupdateBtn").click(function () {
    const idx = $("input:radio[name='chkDevice']:checked").val();
    let html = "";
    $("#updateVentDiv").show();

    setUnUseVentDeviceList();

    $.ajax({
      url: "/system/member/device/ajax/ventDevice",
      type: "GET",
      async: false,
      data: "idx=" + idx,
      success: function (param) {
        for (let i = 0; i < param.data.length; i++) {
          const serialNum = param.data[i].serialNum;
          const ventDeviceIdx = param.data[i].ventDeviceIdx;

          html += "<div class='row col-12 mt10' style='height: 38px;line-height: 35px;'>";
          html += "<span style='float:left'>" + serialNum + "</span>";
          html += "<input type='button' class='btn btn-primary ml10' value='??????' style='float:right;' onclick='deleteVent("
              + ventDeviceIdx + ")'/>";
          html += "</div>";

        }
        $("#AddVentList").html(html);
      }
    });
  })

  $('#getAddressBtn').click(function(){
          $.ajax({
                 method: "GET",
                 url: "https://kwapi.kweather.co.kr/v1/gis/geo/loctoaddr?lat="+$('#Lat').val()+"&lon="+$('#Lon').val(),
                 async : false,
                 beforeSend: function (xhr) {
                     xhr.setRequestHeader("auth","a3dlYXRoZXItYXBwLWF1dGg=");
                 },
                 success: function (d) {
                   let data = d.data;
                     $('#sCode option:contains('+data.sido_nm+')').attr("selected","selected");

                     let func1 = new Promise((resolve,reject)=>{ //new Promise() ???????????? ???????????? ??????(pending) ??????
                             $("#gCode").children('option').remove();
                             let gCodeList = [];
                             let html = "<option value='000'>??????</option>";
                             $.ajax({
                                 url: "/api/dong/guList/dev",
                                 type: "get",
                                 async : false,
                                 data: "searchSdcode=" + $('#sCode option:contains('+data.sido_nm+')').val(),
                                 success: function (param) {
                                   param.data.forEach((datas) => {
                                     gCodeList.push([datas.dname, datas.sggcode]);
                                   });
                                   gCodeList.forEach((gCodes) => {
                                     html += "<option value='" + gCodes[1] + "'>" + gCodes[0] + "</option>";
                                   });
                                   $("#gCode").html(html);
                                   $('#gCode option:contains('+data.sg_nm+')').attr("selected","selected");
                                   resolve(data); //resolve ???????????? value ?????? ?????? ????????? ??????(fulfilled)??????
                                 },
                                 error : function(){
                                    $('#gCode').html("<option value='000'>??????</option>");
                                    reject();
                                 }
                             });
                     });

                     func1.then(function(data){ //promise.then : promise??? resolve ?????? ?????????.
                        $("#dCode").children('option').remove();
                          let dCodeList = [];
                          let html = "<option value='0000000000'>??????</option>";
                          $.ajax({
                            url: "/api/dong/dongList/dev",
                            type: "get",
                            async : false,
                            data: "searchSdcode=" + $('#sCode').val() + "&searchSggcode=" + $('#gCode').val(),
                            success: function (param) {
                              if (param) {
                                param.data.forEach((datas) => {
                                  dCodeList.push([datas.dname, datas.dcode]);
                                });
                                dCodeList.forEach((dCodes) => {
                                  html += "<option value='" + dCodes[1] + "'>" + dCodes[0]
                                      + "</option>";
                                });
                              } else {
                                html = "<option value='" + 0 + "'>????????? ?????? ????????????.</option>";
                              }
                              $("#dCode").html(html);
                              $('#dCode option:contains('+data.emd_nm+')').attr("selected","selected");
                            }
                          });
                     });
                 },
                 error(){
                    alert("????????? ?????? ??????????????????");
                 }
          });



 });

  $('#fileUpdateBtn').click(function () {
    const form = $('#fileUploadForm')[0];
    const data = new FormData(form);
    let apiUrl;

    if (fileFlag == 1) {
      apiUrl = "fileUpdate/";
    } else {
      apiUrl = "fileUpload/";
    }

    if ($('#targetFile').val()) {
      $.ajax({
        method: "POST",
        url: "/system/member/device/ajax/" + apiUrl + $("#memberIdx").val()
            + "/" + $("input:radio[name='chkDevice']:checked").val()
            + "?type=report&target=targetFile",
        processData: false,
        contentType: false,
        data: data,
        success: function () {
          alert("?????? ???????????????.");
          location.reload();
        }
      });
    }
  });



  $("#updateBtn").click(function () {
    const memberIdx = $("#memberIdx").val();
    const deviceIdx = $("input:radio[name='chkDevice']:checked").val();
    let stationIdx;
    const stationName = $("#stationName").val();
    const airMapUse = $('input:checkbox[id="airMapUse"]').is(":checked");
    const sCode = $('#sCode > option:selected').val();
    const gCode = $('#gCode > option:selected').val();
    const dCode = $('#dCode > option:selected').val();
    const Lat = $("#Lat").val();
    const Lon = $("#Lon").val();
    const equipDt = $("#equip_dt").val();
    const equipName = $("#equipName").val();
    const equipAddr = $("#equipAddr").val();
    const equipAddr2 = $("#equipAddr2").val();
    const etc = $("#etc").val();
    const chk_parentSpace = $("input:radio[name='parentSpace']:checked").val();
    const chk_space = $("input:radio[name='space']:checked").val();
    const setTemp = $('#setTemp').val();
    const relatedDevice = $('#related').text();
    const departName = $('#departName').val();
    const departPhoneNumber = $('#departPhoneNumber').val();
    const salesName = $('#salesName').val();

    if (chk_parentSpace != 0 && !chk_space) {
      alert("?????? ??????????????? ????????? ?????????.");
      return false;
    } else {
      stationIdx = $("input:radio[name='space']:checked").val();
    }

    if (!stationName) {
      alert("?????????????????? ???????????????.");
      $("#stationName").focus();
      return false;
    }

    if (sCode === "00" || !sCode) {
      alert("???????????? ????????? ?????? ?????? ???/??? ????????? ???????????????.");
      $('#dCode').focus();
      return false;
    }

    if (gCode === "000" || !gCode) {
      alert("???????????? ????????? ?????? ?????? ???/???/??? ????????? ???????????????.");
      $('#dCode').focus();
      return false;
    }

    if (dCode === "0000000000" || !dCode) {
      alert("???????????? ????????? ?????? ?????? ???/???/??? ????????? ???????????????.");
      $('#dCode').focus();
      return false;
    }

    if (!Lat) {
      alert("????????? ???????????????.");
      $("#lat").focus();
      return false;
    } else {
      if (!re_lat_lon.test(Lat)) {
        alert("?????? ?????? ????????? ?????????????????????.");
        $("#lat").val("");
        return false;
      }
    }

    if (!Lon) {
      alert("????????? ???????????????.");
      $("#lon").focus();
      return false;
    } else {
      if (!re_lat_lon.test(Lon)) {
        alert("?????? ?????? ????????? ?????????????????????.");
        $("#lon").val("");
        return false;
      }
    }

    if (!departName) {
      alert("?????? ??????????????? ???????????????.");
      $("#departName").focus();
      return false;
    }

    if (!equipDt) {
      alert("??????????????? ???????????????.");
      $("#equipDt").focus();
      return false;
    }

    if (!departPhoneNumber) {
      alert("?????? ???????????? ???????????????.");
      $("#departPhoneNumber").focus();
      return false;
    }

    if (!equipName) {
      alert("?????? ??????????????? ???????????????.");
      $("#equipName").focus();
      return false;
    }

    if (!salesName) {
      alert("?????? ??????????????? ???????????????.");
      $("#salesName").focus();
      return false;
    }

    if (!equipAddr) {
      alert("??????????????? ???????????????.");
      $("#equipAddr").focus();
      return false;
    }

    const obj = {
      memberIdx: memberIdx,
      deviceIdx: deviceIdx,
      spaceIdx: stationIdx,
      equipDt: equipDt,
      equipName: equipName,
      equipAddr: equipAddr,
      equipAddr2: equipAddr2,
      stationName: stationName,
      iaqDeviceIdx: deviceIdx,
      lat: Lat,
      lon: Lon,
      etc: etc,
      departName: departName,
      departPhoneNumber: departPhoneNumber,
      salesName: salesName,
      dcode: dCode,
      airMapYn: airMapUse ? 'Y' : 'N',
      setTemp: setTemp,
      relatedDevice: relatedDevice
    }

    $.ajax({
      method: "PUT",
      url: "/system/member/device/put",
      data: JSON.stringify(obj),
      contentType: "application/json; charset=utf-8",
      success: function () {
        alert("?????? ???????????????.");
        location.reload();
      },
      error: function () {
        alert("????????? ?????????????????????. ??????????????? ?????? ??????????????????.")
      }
    });

  })
  $("#deleteBtn").click(function () {
    const deviceIdx = $("input:radio[name='chkDevice']:checked").val();
    const deviceType = $("#device" + deviceIdx).attr("data-devicetype");
    const result = confirm("????????? ????????? ?????????????????????????");

    if (result) {
      if (!deviceIdx) {
        alert("???????????? ????????? ????????? ?????????.");
        return false;
      }

      $.ajax({
        url: "/system/member/device/delete",
        type: "DELETE",
        contentType: "application/json; charset=utf-8",
        async: false,
        data: JSON.stringify({
          memberIdx: $("#memberIdx").val(),
          deviceIdx: deviceIdx
        }),
        success: function () {
          if (deviceType !== "IAQ") {
            alert("?????? ???????????????.");
            location.reload();
          }
        }
      })

      if (deviceType === "IAQ") {
        $.ajax({
          url: "/system/member/device/delete/vent/all",
          type: "DELETE",
          async: false,
          contentType: "application/json; charset=utf-8",
          data: JSON.stringify({
            memberIdx: $("#memberIdx").val(),
            iaqDeviceIdx: deviceIdx
          }),
          success: function () {
            alert("?????? ???????????????.");
            location.reload();
          }
        })
      }
    } else {
      return false;
    }
  })

  $("#equip_dt").datepicker({
    dateFormat: 'yy-mm-dd'
  })

  $("#datepicker_ic").click(function () {
    $("#equip_dt").focus();
  })

  if ($('#popupDeviceIdx').val() !== "0") {
    await getDeviceInfo($('#popupDeviceIdx').val());
    $("input:radio[name='chkDevice']:radio[value='" + $('#popupDeviceIdx').val()
        + "']").prop('checked', true);
  }

  $("#inputImageFileMain").change(function () {
    readURL(this, "Main", mainImageFlag);
  });
  $("#inputImageFileEast").change(function () {
    readURL(this, "East", eastImageFlag);
  });
  $("#inputImageFileWest").change(function () {
    readURL(this, "West", westImageFlag);
  });
  $("#inputImageFileSouth").change(function () {
    readURL(this, "South", southImageFlag);
  });
  $("#inputImageFileNorth").change(function () {
    readURL(this, "North", northImageFlag);
  });
  $("#blahMain").click(function () {
    if (mainImageFlag === "1") {
      location.href = "/system/member/device/ajax/fileDownload/" + $(
          "input:radio[name='chkDevice']:checked").val() + "?type=Main"
    } else {
      alert("?????? ?????? ???????????? ?????? ??? ????????? . (??????)");
    }
  })
  $("#blahEast").click(function () {
    if (eastImageFlag === "1") {
      location.href = "/system/member/device/ajax/fileDownload/" + $(
          "input:radio[name='chkDevice']:checked").val() + "?type=East"
    } else {
      alert("?????? ?????? ???????????? ?????? ??? ????????? . (???)");
    }
  })
  $("#blahWest").click(function () {
    if (westImageFlag === "1") {
      location.href = "/system/member/device/ajax/fileDownload/" + $(
          "input:radio[name='chkDevice']:checked").val() + "?type=West"
    } else {
      alert("?????? ?????? ???????????? ?????? ??? ????????? . (???)");
    }
  })
  $("#blahSouth").click(function () {
    if (southImageFlag === "1") {
      location.href = "/system/member/device/ajax/fileDownload/" + $(
          "input:radio[name='chkDevice']:checked").val() + "?type=South"
    } else {
      alert("?????? ?????? ???????????? ?????? ??? ????????? . (???)");
    }
  })
  $("#blahNorth").click(function () {
    if (northImageFlag === "1") {
      location.href = "/system/member/device/ajax/fileDownload/" + $(
          "input:radio[name='chkDevice']:checked").val() + "?type=North"
    } else {
      alert("?????? ?????? ???????????? ?????? ??? ????????? . (???)");
    }
  })

  $('.imageFileArea').hover(function () {
    $('.msg-point').show();
    if ($("#device" + $("input:radio[name='chkDevice']:checked").val())
    .attr("data-" + $(this).attr("data-type") + "ImageFlag") == 1) {
      $('.msg-point').html("&nbsp;&nbsp;( ???????????? ???????????? ???????????? ?????????. )");
    } else {
      $('.msg-point').html("&nbsp;&nbsp;( ???????????? ????????? ????????? . )");
    }

  }, function () {
    $('.msg-point').hide();
  });

  getDeviceInfo($("input:radio[name='chkDevice']:checked").val());
});


