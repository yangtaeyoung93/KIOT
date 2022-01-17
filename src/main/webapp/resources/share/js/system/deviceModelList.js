function initDataTableCustom() {
  const searchUseYn = $('input[name="customRadio"]:checked').attr('r-data');
  const searchValue = $('#searchValue').val();
  const searchDeviceType = $('#searchDeviceType').val();

  $('#deviceTable').DataTable({
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
      "url": "/system/device/model/get",
      "type": "GET",
      data: function (param) {
        param.useYn = searchUseYn;
        param.value = searchValue;
        param.deviceType = searchDeviceType;
      }
    },
    columns: [
      {data: "idx"},
      {data: "rowNum"},
      {data: "deviceType"},
      {data: "deviceModel"},
      {data: "description"},
      {data: "createDt"},
      {data: "imageFile"},
      {data: "useYn"}
    ],
    columnDefs: [
      {
        targets: 0,
        render: function (data, type, full) {
          return '<label style="width: 100%;"><input type="checkbox" name="boardIdx" value='
              + full.idx + ' class="chBox" data-cartNum=' + full.idx
              + ' data-useyn = ' + full.useYn + '   data-deviceModel ='
              + full.deviceModel + ' > </label>';

        },
      },
      {
        targets: [1, 2, 3, 4, 7],
        render: function (data, type, full) {
          return "<a href='#' style='cursor: pointer; width: 100%;' onclick='deviceModelDetail(\""
              + full.idx + "\")'><strong>" + data + "</strong></a> ";
        },
      },
      {
        targets: 5,
        render: function (data, type, full) {
          return "<a href='#' style='cursor: pointer; width: 100%;' onclick='deviceModelDetail(\""
              + full.idx + "\")'><strong>" + full.createDt.substring(0, 19)
              + "</strong></a> ";

        },
      },
      {
        targets: 6,
        orderable: false,
        render: function (data, type, full) {
          return "<img src='https://suncheon.kweather.co.kr/IMAGES/deviceModel/"
              + full.deviceModel + "." + data + "' alt='이미지없음'"
              + "style='width: 50px; height: 50px;'/>";
        },
      },
    ],
  });

  $("#deviceTable_filter").hide();
}

function deviceModelDetail(data) {
  location.href = "/system/device/model/detail/" + data;
}

function goInsert() {
  location.href = "/system/device/model/detail";
}

function deleteCategory() {
  const confirmVal = confirm("선택된 모델을 삭제 하시겠습니까?");

  if (confirmVal) {
    const checkArr = [];
    const nameArr = []; //모델명

    $("input[class='chBox']:checked").each(function () {
      checkArr.push($(this).attr("data-cartNum"));
      nameArr.push($(this).attr("data-deviceModel"));
    });

    $.ajax({
      method: "DELETE",
      url: "/system/device/model/delete",
      data: JSON.stringify({
        chArr: checkArr,
        nameArr: nameArr
      }),
      contentType: "application/json; charset=utf-8",
      success: function (d) {
        if (d.resultCode == 2) {
          alert("사용장 계정에 등록된 장비가 존재합니다. \n 모델명:" + d.checkName);
          return false;
        } else {
          alert("삭제 되었습니다.");
          location.reload();
        }
      }
    });
  }
}

$().ready(function () {
  initDataTableCustom();
  $("#deviceTable_filter").hide();
  $('#deleteBtn').click(function () {
    deleteCategory();
  })

  $('#searchBtn').click(function () {
    initDataTableCustom();
  })
})
