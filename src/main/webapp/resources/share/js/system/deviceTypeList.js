/**
 * 시스템관리 > 장비 유형 카테고리
 */

function initDataTableCustom() {
	var table = $('#deviceTable').DataTable({
		scrollCollapse: true,
		autoWidth: false,
		language : {
			"emptyTable" : "데이터가 없습니다.",
			"lengthMenu" : "페이지당 _MENU_ 개씩 보기",
			"info" : "현재 _START_ - _END_ / _TOTAL_건",
			"infoEmpty" : "데이터 없음",
			"infoFiltered" : "( _MAX_건의 데이터에서 필터링됨 )",
			"search" : "",
			"zeroRecords" : "일치하는 데이터가 없습니다.",
			"loadingRecords" : "로딩중...",
			"processing" : "잠시만 기다려 주세요.",
			"paginate" : {
				"next" : "다음",
				"previous" : "이전"
			}
		},
        destroy: true,
        processing: true,
        serverSide: false,
		ajax : {
			 "url":"/system/device/type/get",
			 "type":"GET",
			 data : function (param) {
				 param.useYn = "Y";
             }
		},
        columns : [
        	
        	{data: "rowNum"},
            {data: "deviceType"},
            {data: "deviceTypeName"},
            {data: "description"},
            {data: "createDt"},
            {data: "useYn"}
        ],
        columnDefs: [ 
	        {
	        	targets:   [0, 1, 2, 3, 5],
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + data + "</strong>";
				},
	        },
	        {
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + full.createDt.substring(0,19) + "</strong>";
				},
        	},
        ],
	});

	$('#deviceTable_filter').prepend('<select id="select"></select>');
    $('#select').append('<option>장비 유형</option><option>이 름</option><option>설 명</option>');

    $('.dataTables_filter input').unbind().bind('keyup', function () {
        var colIndex = document.querySelector('#select').selectedIndex;
        table.column(colIndex + 1).search(this.value).draw();
    });

    $('#select').addClass('btn-primary');
    $('#select').css("padding-right", "10px");
}

function insertCategory() {
	var device_type = $("#type").val();
	var device_name = $("#typeName").val();
	var Description = $("#description").val();

	if (!device_type) {
		alert("장비유형을 입력해 주세요.");
		$("#type").focus()
		return false;
	}

	if (!device_name) {
		alert("이름을 입력해 주세요.");
		$("#typeName").focus()
		return false;
	}

	$.ajax({
		method : "POST",
		url : "/system/device/type/post",
		data : JSON.stringify({
			deviceType : $('#type').val(),
			deviceTypeName : $('#typeName').val(),
			description : $('#description').val()
			//useYn : $('#useYn').val()
		}),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			console.log('insertCategory LOG : ', d.data);
			alert("등록 되었습니다.");
			location.reload();
		}
	});
}

function updateCategory() {
	var device_type = $("#type").val();
	var device_name = $("#typeName").val();
	var Description = $("#description").val();

	if (!device_type) {
		alert("장비유형을 입력해 주세요.");
		$("#type").focus();

		return false;
	}

	if (!device_name) {
		alert("이름을 입력해 주세요.");
		$("#typeName").focus();

		return false;
	}

	$.ajax({
		method : "PUT",
		url : "/system/device/type/put",
		data : JSON.stringify({
			idx : $('#deviceTypeIdx').val(),
			deviceType : $('#type').val(),
			deviceTypeName : $('#typeName').val(),
			description : $('#description').val()
			//useYn : $('#useYn').val()
		}),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			console.log('updateCategory LOG : ', d.data);
			alert("수정 되었습니다.");
			location.reload();
		}
	});
}

function deleteCategory() {
	var confirmVal = confirm("정말 삭제하시겠습니까?");

	if (confirmVal) {
		var checkArr = new Array();
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
		});

		$.ajax({
			method : "DELETE",
			url : "/system/device/type/delete",
			data : JSON.stringify({
				chArr : checkArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log('deleteCategory LOG : ', d.data);
				alert("삭제 되었습니다.");
				location.reload();
			}
		});
	}
}

function equiCatDetail(paramIdx, paramType, paramTypeName, paramEtc, paramUseYn) {
	var idx = paramIdx;
	var type = paramType;
	var typeName = paramTypeName;
	var etc = paramEtc;
	var useYn = paramUseYn;

	if (etc == "" || etc == "null" || etc == "undefined" || etc == "0" || etc == "NaN" ) etc = "";

	$('#insertBtn').hide();
	$('#updateBtn').show();
	$('#useYn').attr('disabled', true);
	$('#modal-lg').modal();

	$('#deviceTypeIdx').val(paramIdx);
	$('#type').val(type);
	$('#typeName').val(typeName);
	$('#description').val(etc);
	$('#useYn').val(useYn);
}

$().ready(function() {
	initDataTableCustom();

	$('#openModalBtn').click(function() {
		$('#insertBtn').show();
		$('#updateBtn').hide();
		$('#useYn').attr('disabled', false);

		$('#deviceTypeIdx').val("");
		$('#type').val("");
		$('#typeName').val("");
		$('#description').val("");
		$('#useYn').val("Y");
	})

	$("#chk_type_btn").click(function() {
		var deviceType = $("#type").val();

		if (!deviceType) {
			alert("장비유형을 입력해주세요.");
			$("#type").focus();

			return false;
		}

		$.ajax({
			 url:"/check/deviceType",
			 type:"GET",
			 async : false,
			 data : "deviceType="+deviceType,
			 success : function (param) {
				var result_val = param.resultCode;
				if (result_val == 1) {
					$("#chk_txt").css("color","#28a745");
					$("#chk_txt").css("font-weight","bold");
					$("#chk_txt").text("사용가능한 장비유형 입니다.");
					$("#chk_type").val("1");
					$("#type").focus();

					return false;

				} else {
					$("#chk_txt").css("color","red");
					$("#chk_txt").css("font-weight","bold");
					$("#chk_txt").text("중복되는 장비유형 입니다. 다시 확인하여 주세요.");
					$("#chk_type").val("0");
					$("#type").focus();

					return false;
				}
			 }
		})
	});

	$("#chk_type_name_btn").click(function() {
		var deviceTypeName = $("#typeName").val();

		if(!deviceTypeName){
			alert("이름을 입력해주세요.");
			$("#typeName").focus();
			return false;
		}

		$.ajax({
			 url:"/check/deviceTypeName",
			 type:"GET",
			 async : false,
			 data : "deviceTypeName="+deviceTypeName,
			 success : function (param) {
				//console.log(param);
				var result_val = param.resultCode;
				//alert(result_val);
				if (result_val == 1) {
					$("#chk_txt2").css("color","#28a745");
					$("#chk_txt2").css("font-weight","bold");
					$("#chk_txt2").text("사용가능한 이름 입니다.");
					$("#chk_type_name").val("1");
					$("#typeName").focus();

					return false;

				} else {
					$("#chk_txt2").css("color","red");
					$("#chk_txt2").css("font-weight","bold");
					$("#chk_txt2").text("중복되는 이름 입니다. 다시 확인하여 주세요.");
					$("#chk_type_name").val("0");
					$("#typeName").focus();

					return false;
				}
			 }
		})
	});

	$('#insertBtn').click(function() {
		insertCategory();
	})

	$('#updateBtn').click(function() {
		updateCategory();
	})

	$('#deleteBtn').click(function() {
		deleteCategory();
	})
})