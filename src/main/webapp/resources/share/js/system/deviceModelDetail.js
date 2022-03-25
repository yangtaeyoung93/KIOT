let obj = {};
let attIdxs = [];
let deviceValues = [];
let eleIdxs = [];

function createData(fileExtYn) {

  const h_idx = $("#deviceModelIdx").val();
  const deviceModel = $('#deviceModel').val();
  const deviceTypeIdx = $('#deviceTypeIdx').val();
  const description = $('#description').val();
  const chk_model = $("#chk_model").val();

  const vent_opt1 = $("#29 option:selected").val();
  const vent_opt2 = $("#31 option:selected").val();
  const vent_opt3 = $("#32 option:selected").val();
  const vent_opt4 = $("#33 option:selected").val();
  const vent_opt5 = $("#34 option:selected").val();
  const vent_opt6 = $("#28 option:selected").val();
  let fileExt;

  if (fileExtYn === true) {
    if ($('#imageSelector')[0].files[0] === undefined) {
      fileExt = $('#deviceImage').attr("src").split('.').pop();
    } else {
      fileExt = $('#imageSelector').val().split('.').pop().toLowerCase();
    }
  } else {
    fileExt = "";
  }

  attIdxs = [];
  deviceValues = [];
  eleIdxs = [];

  if (!h_idx) {
    if (chk_model == 0) {
      alert("모델명 중복 체크를 하시기 바랍니다.");
      $("#chk_model").focus();
      return false;
    }
  }

  $(".model-attribute").each(function () {
    if ($(this).val() === '' || $(this).val() === 'N' || $(this).val()
        === '등급외') {
      return;
    }
    attIdxs.push($(this).attr("id"));
    deviceValues.push($(this).val());
  });

  $('.model-elements').each(function () {
    if ($(this).val() === 'N') {
      return;
    }
    eleIdxs.push($(this).attr("id"));
  })

  if (!deviceModel) {
    alert("장비모델명을 입력해주세요.");
    $("#deviceModel").focus()
    return false;
  }

  if (deviceModel.length > 11) {
    alert("10자리 이상 입력 할 수 없습니다.");
    $("#deviceModel").focus()
    return false;
  }

  if (deviceTypeIdx != "7" && (vent_opt1 === "Y" || vent_opt2 === "Y"
      || vent_opt3
      === "Y" || vent_opt4 === "Y" || vent_opt5 === "Y" || vent_opt6 === "Y")) {
    alert("VENT인 경우에만 해당 측정요소 선택 가능합니다.");
    return false;
  }

  return {
    deviceModel: deviceModel,
    deviceTypeIdx: deviceTypeIdx,
    description: description,
    attributeIdx: attIdxs,
    deviceValue: deviceValues,
    elementIdx: eleIdxs,
    imageFile: fileExt
  };
}

function chkModel() {
  const deviceModel = $("#deviceModel").val();
  const deviceTypeIdx = $('#deviceTypeIdx').val();

  if (!deviceModel) {
    $("#chk_txt").css("color", "red").css("font-weight", "bold").text(
        "모델명을 입력해주세요.");
    return false;
  }

  $.ajax({
    url: "/check/deviceModel",
    type: "GET",
    async: false,
    data: "deviceModel=" + deviceModel + "&deviceTypeIdx=" + deviceTypeIdx,
    success: function (param) {
      const result_val = param.resultCode;
      if (result_val == 1) {
        $("#chk_txt").css("color", "#28a745").css("font-weight", "bold").text(
            "사용가능한 모델명 입니다.");
        $("#chk_model").val("1");
        return false;
      } else {
        $("#chk_txt").css("color", "red").css("font-weight", "bold").text(
            "중복되는 모델명 입니다. 다시 확인하여 주세요.");
        $("#chk_model").val("0");
        return false;
      }
    }
  })
}

$().ready(function () {
  const h_idx = $("#deviceModelIdx").val();

  if (h_idx) {
    $("#deviceTypeIdx").attr("disabled", true);
    $("#deviceModel").attr("readonly", true);
    $("#chk_model_btn").hide();
  } else {
    $("#deviceTypeIdx").attr("disabled", false);
    $("#deviceModel").attr("readonly", false);
    $("#chk_model_btn").show();
    $("#deviceModel").blur(function () {
      chkModel();
    })
  }

  $('#chk_model_btn').click(function () {

  })

  $('#listBtn').click(function () {
    location.href = "/system/device/model/list";
  })

  $('#insertBtn').click(function () {
    obj = createData(true);

    $.ajax({
      method: "POST",
      url: "/system/device/model/post",
      data: JSON.stringify(obj),
      contentType: "application/json; charset=utf-8",
      success: function () {
        uploadImage();
        alert("등록 되었습니다.");
        location.reload();
      }
    });
  })

  $('#updateBtn').click(function () {
    obj = createData(true);
    obj.idx = $('#deviceModelIdx').val();
    if (obj !== false) { //false가 아니면
      $.ajax({
        method: "PUT",
        url: "/system/device/model/put",
        data: JSON.stringify(obj),
        contentType: "application/json; charset=utf-8",
        success: function () {
          uploadImage();
          alert("수정 되었습니다.");
          location.reload();
        }
      });
    }

  });

  function uploadImage() {

    if ($('#imageSelector')[0].files[0] !== undefined) {
      const formData = new FormData();

      formData.append("imageFile", $('#imageSelector')[0].files[0]);
      formData.append("fileName", $('#deviceModel').val());
      formData.append("filePath", "/NAS2_NFS/IOT_KITECH/DEVICE_MODEL/");

      $.ajax({
        method: "POST",
        url: "/api/custom/image",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
        }
      });
    }
  }

});

function deleteImage() {
  obj = createData(false);
  obj.idx = $('#deviceModelIdx').val();
  if (obj !== false) {
    $.ajax({
      method: "PUT",
      url: "/system/device/model/put",
      data: JSON.stringify(obj),
      contentType: "application/json; charset=utf-8",
      complete: function () {
        alert("삭제 되었습니다.")
        location.reload();
      }
    });
  }
}