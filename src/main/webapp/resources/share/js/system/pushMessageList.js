/**
 * 시스템관리 > 푸시 메시지 관리
 */

var table;

function initDataTableCustom() {
	table = $('#pushMessageTable').DataTable({
				scrollCollapse : true,
				autoWidth : false,
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
				destroy : true,
				processing : true,
				serverSide : false,
				dom : '<"top"lfB>rt<"bottom"ip><"clear">',
				buttons : [ {
					extend : 'csv',
					charset : 'UTF-8',
					text : '엑셀 다운로드',
					footer : false,
					bom : true,
					filename : '알림메시지_다운로드',
					className : 'btn-primary btn excelDownBtn'
				} ],
				ajax : {
					"url" : "/system/push/message/list",
					"type" : "GET"
				},
				columns : [ {
					data : "idx"
				}, {
					data : "deviceType"
				}, {
					data : "element"
				}, {
					data : "preStep"
				}, {
					data : "curStep"
				}, {
					data : "message"
				} ],
				columnDefs : [
						{
							targets : 0,
							orderable : true,
							render : function(data, type, full, meta) {
								return "<strong>" + ((full.preStep == "0") ? "행동요령" : "메시지") + "</strong>";
							},
						},
						{
							targets : 1,
							orderable : true,
							render : function(data, type, full, meta) {
								return "<strong>" + ((data == 'IAQ') ? "실 내" : "실 외") + "</strong>";
							},
						}, {
							targets : 2,
							orderable : true,
							render : function(data, type, full, meta) {
								var elementName = "";
								switch (data) {
								case "pm10":
									elementName = "미세먼지";
									break;
								case "pm25":
									elementName = "초미세먼지";
									break;
								case "co2":
									elementName = "이산화탄소";
									break;
								case "voc":
									elementName = "휘발성유기화합물";
									break;
								case "temp":
									elementName = "온도";
									break;
								case "humi":
									elementName = "습도";
									break;
								default:
									elementName = "-";
									break;
								}

								return "<strong>" + elementName + "</strong>";
							},
						}, {
							targets : [ 3, 4 ],
							orderable : true,
							render : function(data, type, full, meta) {
								var stepName = "-";

								switch (full.element) {
								case "temp":
									if (data == 1) {
										stepName = "쾌적";
									} else if (data == 2) {
										stepName = "보통";
									} else if (data == 3) {
										stepName = "더움";
									} else if (data == 4) {
										stepName = "추움";
									} else if (data == 5) {
										stepName = "매우 더움";
									} else if (data == 6) {
										stepName = "매우 추움";
									}

									break;

								case "humi":
									if (data == 1) {
										stepName = "쾌적";
									} else if (data == 2) {
										stepName = "보통";
									} else if (data == 3) {
										stepName = "습함";
									} else if (data == 4) {
										stepName = "건조";
									} else if (data == 5) {
										stepName = "매우 습함";
									} else if (data == 6) {
										stepName = "매우 건조";
									}

									break;

								default:
									if (data == 1) {
										stepName = "좋음";
									} else if (data == 2) {
										stepName = "보통";
									} else if (data == 3) {
										stepName = "나쁨";
									} else if (data == 4) {
										stepName = "매우 나쁨";
									}

									break;
								}

								return "<strong>" + stepName + "</strong>";
							},
						}, {
							targets : 5,
							orderable : true,
							render : function(data, type, full, meta) {
								return "<strong>" + data + "</strong>";
							},
						}, ],
			});

	$("#pushMessageTable_filter").hide();
	$("#pushMessageTable_info").hide();
	$(".dt-buttons").css("position", "relative");
	$(".excelDownBtn").css({
		"position" : "absolute",
		"right" : "0",
		"top" : "-40px"
	});

	$('#searchValue').bind('keyup', function() {
		table.column(5).search(this.value).draw();
		console.log(this.valu);
	});

	$("#searchPushType").change(function() {
		var searchDeviceType = this.value;
		table.column(0).search(searchDeviceType).draw();
	})

	$("#searchDeviceType").change(function() {
		var searchDeviceType = this.value;
		table.column(1).search(searchDeviceType).draw();
	})

	$("#searchElementType").change(function() {
		var searchElementType = this.value;
		table.column(2).search(searchElementType).draw();
	})

	$('#pushMessageTable tbody').on('click', 'tr', function () {
        var trData = table.row(this).data();

        $("#deviceType").attr("disabled",true);
        $("#elementType").attr("disabled",true);
        $("#preLevel").attr("disabled",true);
        $("#curLevel").attr("disabled",true);

        $("#deviceType").val(trData.deviceType);
        $("#elementType").val(trData.element);
        $("#preLevel").val(trData.preStep);
        $("#curLevel").val(trData.curStep);
        $("#message").val(trData.message);

        $("#pushMessageIdx").val(trData.idx);
        $("#insertBtn").hide();
        $("#updateBtn").show();
        $('#modal-lg').modal();
    } );
}

function pushMessageUpdateF() {
	pmIdx = $("#pushMessageIdx").val();

	$.ajax({
		method : "PUT",
		url : "/system/push/message/update",
		data : JSON.stringify({
			idx : pmIdx,
			message : $("#message").val()
		}),
		contentType : "application/json; charset=utf-8",
		success : function(res) {
			if (res.resultCode)
				alert("수정 되었습니다.");
			else
				alert("수정 실패 하였습니다.");

			table.ajax.reload();
		}
	});
}

function pushMessageSaveF() {
    $.ajax({
		method : "POST",
		url : "/system/push/message/save",
		data : JSON.stringify({
			element : $('#elementType option:selected').val(),
			preStep : $('#preLevel option:selected').val(),
			curStep : $('#curLevel option:selected').val(),
			deviceType : $("#deviceType option:selected").val(),
			message : $("#message").val()
		}),
		contentType : "application/json; charset=utf-8",
		success : function(res) {
			if (res.resultCode)
				alert("등록 되었습니다.");
			else
				alert("등록 실패 하였습니다.");

			table.ajax.reload();
		}
	});
}

function setLevelSetting(setType) {
	console.log("set :: " + setType);
	var preLevelHtml = "";
	if (setType == "1") {
		preLevelHtml = '<option value ="1">좋음</option><option value ="2">보통</option><option value ="3">나쁨</option><option value ="4">매우 나쁨</option>'
	} else if (setType == "2") {
		preLevelHtml = '<option value ="1">쾌적</option><option value ="2">보통</option><option value ="3">더움</option><option value ="4">추움</option><option value ="5">매우 더움</option><option value ="6">매우 추움</option>'
	} else if (setType == "3") {
		preLevelHtml = '<option value ="1">쾌적</option><option value ="2">보통</option><option value ="3">습함</option><option value ="4">건조</option><option value ="5">매우 습함</option><option value ="6">매우 건조</option>'
	}

	$("#preLevel").html(preLevelHtml);
	$("#curLevel").html(preLevelHtml);
}

$().ready(function() {
	initDataTableCustom();

	$("#insertBtn").click(function() {
		pushMessageSaveF();
	})

	$("#updateBtn").click(function() {
		pushMessageUpdateF();
	})

	$("#openModalBtn").click(function() {
	    $("#deviceType").attr("disabled", false);
	    $("#elementType").attr("disabled", false);
	    $("#preLevel").attr("disabled", false);
	    $("#curLevel").attr("disabled", false);

	    $("#deviceType").val("IAQ");
	    $("#elementType").val("pm10");
	    $("#preLevel").val("1");
	    $("#curLevel").val("1");
	    $("#message").val("");

	    $("#insertBtn").show();
	    $("#updateBtn").hide();
	})

	$("#elementType").change(function() {
		setLevelSetting($("#elementType option:selected").attr("data-type"));
	})
})
