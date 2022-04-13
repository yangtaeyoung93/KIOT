const obj = {};

function selectMember(val) {
  const memberId = $("#MemberLabel" + val).text();
  $("#txtMember").text(memberId);
}

function getDeviceTypeList() {
  let deviceTypeHtml = "";
  $.ajax({
    url: "/system/member/device/ajax/deviceType",
    type: "GET",
    async: false,
    success: function (param) {
      deviceTypeHtml += "<option value=''>유형 선택</option>";
      for (let i = 0; i < param.data.length; i++) {
        const idx = param.data[i].deviceTypeIdx;
        const deviceType = param.data[i].deviceType;
        const deviceTypeName = param.data[i].deviceTypeName;

        if (deviceType === "IAQ" || deviceType === "OAQ" || deviceType
            === "DOT") {
          deviceTypeHtml += "<option value='" + idx + "'>" + deviceType + " ("
              + deviceTypeName + ")</option>";
        }
      }

      $("#deviceTypeSelect").html(deviceTypeHtml);
    }
  })
}

function seleceDeviceType(val) {
  let deviceModelHtml = "";

  parentSpaceList(val);

  $("#deviceSerialList").html("");

  $("#ventList").html("");
  $("#spaceList").html("");

  $.ajax({
    url: "/system/member/device/ajax/deviceModel",
    type: "GET",
    async: false,
    data: "deviceTypeIdx=" + val,
    success: function (param) {
      deviceModelHtml += "<option value=''>모델 선택</option>";
      for (let i = 0; i < param.data.length; i++) {
        const idx = param.data[i].deviceModelIdx;
        const deviceModelName = param.data[i].deviceModelName;

        deviceModelHtml += "<option value='" + idx + "'>" + deviceModelName
            + "</option>";
      }

      $("#deviceModelSelect").html(deviceModelHtml);
    }
  })
}

function seleceDeviceModel(val) {
  let deviceSerialHtml = "";
  $("#ventList").html("");

  $.ajax({
    url: "/system/member/device/ajax/deviceSerial",
    type: "GET",
    async: false,
    data: "deviceModelIdx=" + val,
    success: function (param) {
      deviceSerialHtml += "<option value=''>장비 선택</option>";
      for (let i = 0; i < param.data.length; i++) {
        const idx = param.data[i].deviceIdx;
        const serialNum = param.data[i].serialNum;

        deviceSerialHtml += "<option value='" + idx + "'>" + serialNum
            + "</option>";
      }

      $("#deviceSerialSelect").html(deviceSerialHtml);
    }
  })
}

function seleceDeviceSerial() {
  let ventHtml = "";
  const deviceType = $('#deviceTypeSelect').val();
  if (deviceType === "1") {
    $.ajax({
      url: "/system/member/device/ajax/deviceVent",
      type: "GET",
      async: false,
      success: function (param) {
        for (let i = 0; i < param.data.length; i++) {
          const idx = param.data[i].deviceIdx;
          const serialNum = param.data[i].serialNum;
          ventHtml += "<option value='" + idx + "'>" + serialNum + "</option>";
        }

        $("#ventSelect").html(ventHtml);
      }
    })
  }
}

function getSiList() {
  let sCodeList = [];
  $.ajax({
    url: "/api/dong/siList/dev",
    type: "get",
    success: function (param) {
      param.data.forEach((datas) => {
        sCodeList.push([datas.dname, datas.sdcode]);
      });
      sCodeList.forEach((sCodes) => {
        $("#sCode").append(
            "<option value='" + sCodes[1] + "'>" + sCodes[0] + "</option>");
      });
    }
  });
}

function getGuList(Sdcode) {
  $("gCode").children('option').remove();
  let gCodeList = [];
  let html = "<option value='000'>선택</option>";
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
    }
  });
}

function getDongList(Sdcode, Sggcode) {
  $("dCode").children('option').remove();
  let dCodeList = [];
  let html = "<option value='0000000000'>선택</option>";
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
        html = "<option value='" + 0 + "'>표시할 동이 없습니다.</option>";
      }
      $("#dCode").html(html);
    }
  });
}

function seleceVent(val) {
  const vent_cnt = $("input:checkbox[name='vent']:checked").length;
  if (vent_cnt > 4) {
    alert("4개 이상은 선택 할 수 없습니다.");
    $("#vent" + val).attr("checked", false);
    return false;
  }
}

function parentSpaceList(val) {
  let parentSpaceHtml = "";

  $.ajax({
    url: "/system/member/device/ajax/parent/spaces",
    type: "GET",
    async: false,
    data: "deviceTypeIdx=" + val,
    success: function (param) {
      parentSpaceHtml += "<option value=''>설치공간 선택</option>";
      for (let i = 0; i < param.data.length; i++) {
        const idx = param.data[i].idx;
        const spaceName = param.data[i].spaceName;
        parentSpaceHtml += "<option value='" + idx + "'>" + spaceName
            + "</option>";
      }

      $("#parentSpaceSelect").html(parentSpaceHtml);
    }
  })
}

function selecetParentSpace(val) {
  let spaceHtml = "";

  if (val === 0) {
    $("#spaceList").html("");
  } else {
    $.ajax({
      url: "/system/member/device/ajax/spaces",
      type: "GET",
      async: false,
      data: "parentSpaceIdx=" + val,
      success: function (param) {
        spaceHtml += "<option value=''>설치공간 선택</option>";
        for (let i = 0; i < param.data.length; i++) {
          const idx = param.data[i].idx;
          const spaceName = param.data[i].spaceName;

          spaceHtml += "<option value='" + idx + "'>" + spaceName + "</option>";
        }

        $("#spaceSelect").html(spaceHtml);
      }
    })
  }
}

function autoheight() {
  const useMember = $("#userList > .userDiv").length;
  const deviceTypeDiv = $("#deviceTypeList > .deviceTypeDiv").length;

  if (useMember < 4) {
    $("#Area1").css("height", "auto").css("overflow-x", "none");
  } else {
    $("#Area1").css("height", "135px").css("overflow-x", "hidden");
  }

  if (deviceTypeDiv < 4) {
    $("#Area2").css("height", "auto").css("overflow-x", "none");
  } else {
    $("#Area2").css("height", "135px").css("overflow-x", "hidden");
  }

}

function datePickerSetting() {
  $.datepicker.regional['kr'] = {
    monthNames: ['1 월', '2 월', '3 월', '4 월', '5 월', '6 월', '7 월', '8 월', '9 월',
      '10 월', '11 월', '12 월'],
    monthNamesShort: ['1 월', '2 월', '3 월', '4 월', '5 월', '6 월', '7 월', '8 월',
      '9 월', '10 월', '11 월', '12 월'],
    dayNames: ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'],
    dayNamesShort: ['월', '화', '수', '목', '금', '토', '일'],
    dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
  };

  $.datepicker.setDefaults($.datepicker.regional['kr']);
}

function chkStation() {
  const spaceName = $("#spaceName").val();
  const deviceIdx = $("#deviceSerialSelect").val();

  if (!deviceIdx) {
    $("#chk_txt").css("color", "red").css("font-weight", "bold")
    .text("장비시리얼을 선택해 주세요.");
    return false;
  }

  if (!spaceName) {
    $("#chk_txt").css("color", "red").css("font-weight", "bold")
    .text("스테이션명을 입력해 주세요.");
    return false;
  }

  $.ajax({
    url: "/check/stationName",
    type: "GET",
    async: false,
    data: "stationName=" + spaceName + "&memberIdx=" + $("#memberSelect").val(),
    success: function (param) {

      const result_val = param.resultCode;
      if (result_val === 1) {
        $("#chk_txt").css("color", "#28a745").css("font-weight", "bold")
        .text("사용가능한 스테이션명 입니다.");
        $("#chk_id").val("1");
        return false;
      } else {
        $("#chk_txt").css("color", "red").css("font-weight", "bold")
        .text("중복되는 스테이션명 입니다. 다시 확인하여 주세요.");
        $("#chk_id").val("0");
        return false;
      }
    }
  })
}

$().ready(function () {

  getSiList();
  getGuList(11);
  getDongList(11, 110);

  $("#sCode").change(function () {
    getGuList($('#sCode > option:selected').val())
  });
  $("#gCode").change(function () {
    getDongList($('#sCode > option:selected').val(),
        $('#gCode > option:selected').val())
  });

  $('.select2').select2();
  datePickerSetting();
  $('.select2bs4').select2({
    theme: 'bootstrap4'
  })

  $("#spaceName").blur(function () {
    chkStation();
  })

  $('#insertBtn').click(function () {
    const memberIdx = $("#memberSelect").val();
    const deviceType = $('#deviceTypeSelect').val();
    const deviceModel = $('#deviceModelSelect').val();
    const deviceIdx = $('#deviceSerialSelect').val();
    let stationIdx;
    const VentArr = $('#ventSelect').val();

    const equipDt = $("#equipDt").val();
    const equipName = $("#equipName").val();
    const equipAddr = $("#equipAddr").val();
    const equipAddr2 = $("#equipAddr2").val();

    const spaceName = $("#spaceName").val();
    const Lat = $("#lat").val();
    const Lon = $("#lon").val();
    const airMapUse = $('input:checkbox[id="airMapUse"]').is(":checked");
    const dCode = $('#dCode > option:selected').val();
    const setTemp = $('#setTemp').val();

    const departName = $('#departName').val();
    const departPhoneNumber = $('#departPhoneNumber').val();
    const salesName = $('#salesName').val();

    const etc = $("#etc").val();

    if (!memberIdx) {
      alert("사용자 아이디를 선택해 주세요.");
      return false;
    }

    if (!deviceType) {
      alert("장비유형을 선택해 주세요.");
      return false;
    }

    if (!deviceModel) {
      alert("장비모델을 선택해 주세요.");
      return false;
    }

    if (!deviceIdx) {
      alert("장비시리얼을 선택해 주세요.");
      return false;
    }

    if ($('#spaceSelect').val() === "") {
      alert("공간 카테고리를 선택해 주세요.");
      return false;
    } else {
      stationIdx = $("#spaceSelect").val();
    }

    if (!spaceName) {
      alert("스테이션명을 입력하세요.");
      $("#spaceName").focus();
      return false;
    }

    if (dCode === "0000000000" || !dCode) {
      alert("에어맵에 사용할 실제 행정동 정보가 필요합니다.");
      $('#dCode').focus();
      return false;
    }

    if (!Lat) {
      alert("위도를 입력하세요.");
      $("#lat").focus();
      return false;
    } else {
      if (!re_lat_lon.test(Lat)) {
        alert("위도 입력 형식이 잘못되었습니다.");
        $("#lat").val("");
        return false;
      }
    }

    if (!Lon) {
      alert("경도를 입력하세요.");
      $("#lon").focus();
      return false;
    } else {
      if (!re_lat_lon.test(Lon)) {
        alert("경도 입력 형식이 잘못되었습니다.");
        $("#lon").val("");
        return false;
      }
    }

    if (!departName) {
      alert("고객 담당자명을 입력하세요.");
      $("#departName").focus();
      return false;
    }

    if (!equipDt) {
      alert("설치일자를 입력하세요.");
      $("#equipDt").focus();
      return false;
    }

    if (!departPhoneNumber) {
      alert("고객 연락처를 입력하세요.");
      $("#departPhoneNumber").focus();
      return false;
    }

    if (!equipName) {
      alert("설치 담당자명을 입력하세요.");
      $("#equipName").focus();
      return false;
    }

    if (!salesName) {
      alert("영업 담당자명을 입력하세요.");
      $("#salesName").focus();
      return false;
    }

    if (!equipAddr) {
      alert("설치주소를 입력하세요.");
      $("#equipAddr").focus();
      return false;
    }

    if (!setTemp && $('#deviceTypeSelect').val() === '1') {
      alert("설정온도를 입력하세요.");
      $("#setTemp").focus();
      return false;
    } else if (setTemp > 40 && $('#deviceTypeSelect').val() === '1'
        || setTemp < 0 && $('#deviceTypeSelect').val() === '1') {
      alert("정확한 온도대를 선택하여 주십시오.");
      $('#setTemp').focus();
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
      stationName: spaceName,
      iaqDeviceIdx: deviceIdx,
      lat: Lat,
      lon: Lon,
      ventDeviceIdxs: VentArr,
      etc: etc,
      departName: departName,
      departPhoneNumber: departPhoneNumber,
      salesName: salesName,
      dcode: dCode,
      airMapYn: airMapUse ? 'Y' : 'N',
      setTemp: setTemp
    }

    $.ajax({
      method: "POST",
      url: "/system/member/device/post",
      data: JSON.stringify(obj),
      contentType: "application/json; charset=utf-8",
      success: function () {
        const form = $('#fileUploadForm')[0];
        const data = new FormData(form);
        if ($('#equiFile').val()) {
          $.ajax({
            method: "POST",
            url: "/system/member/device/ajax/fileUpload/" + memberIdx + "/"
                + deviceIdx,
            processData: false,
            contentType: false,
            data: data,
            success: function () {
              alert("등록 되었습니다.");
              location.reload();
            }
          });
        } else {
          alert("등록 되었습니다.");
          location.reload();
        }
      }
    });
  })

  $("#equipDt").datepicker({
    dateFormat: 'yy-mm-dd'
  })

  $('#listBtn').click(function () {
    location.href = "/system/member/device/list";
  })

  getDeviceTypeList();

  $("#deviceTypeSelect").change(function () {
    seleceDeviceType($(this).val());

    if ($('#deviceTypeSelect').val() === '1') {
      $('#setTempDiv').css('display', 'block');
    } else {
      $('#setTempDiv').css('display', 'none');
    }
  });

  $('#deviceModelSelect').change(function () {
    seleceDeviceModel($(this).val());
  })

  $('#deviceSerialSelect').change(function () {
    seleceDeviceSerial($(this).val());
  })

  $('#ventSelect').change(function () {
    console.log($('#ventSelect').val());
  })

  $('#parentSpaceSelect').change(function () {
    selecetParentSpace($(this).val());
  })


  $('#getAddressBtn').click(function(){
            $.ajax({
                   method: "GET",
                   url: "https://kwapi.kweather.co.kr/v1/gis/geo/loctoaddr?lat="+$('#lat').val()+"&lon="+$('#lon').val(),
                   async : false,
                   beforeSend: function (xhr) {
                       xhr.setRequestHeader("auth","a3dlYXRoZXItYXBwLWF1dGg=");
                   },
                   success: function (d) {
                     let data = d.data;
                       $('#sCode option:contains('+data.sido_nm+')').attr("selected","selected");

                       let func1 = new Promise((resolve,reject)=>{ //new Promise() 메서드로 생성되면 대기(pending) 상태
                               $("#gCode").children('option').remove();
                               let gCodeList = [];
                               let html = "<option value='000'>선택</option>";
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
                                     resolve(data); //resolve 콜백으로 value 결과 값을 보내면 이행(fulfilled)상태
                                   },
                                   error : function(){
                                      $('#gCode').html("<option value='000'>선택</option>");
                                      reject();
                                   }
                               });
                       });

                       func1.then(function(data){ //promise.then : promise의 resolve 값을 다룬다.
                          $("#dCode").children('option').remove();
                            let dCodeList = [];
                            let html = "<option value='0000000000'>선택</option>";
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
                                  html = "<option value='" + 0 + "'>표시할 동이 없습니다.</option>";
                                }
                                $("#dCode").html(html);
                                $('#dCode option:contains('+data.emd_nm+')').attr("selected","selected");
                              }
                            });
                       });
                   },
                   error(){
                      alert("위경도 값을 확인해주세요");
                   }
            });
   });

})