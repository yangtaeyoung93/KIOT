/**
 * 수집데이터 ==> 데이터 다운로드
 */

 let downtime=0.0;
 let interval;
var deviceSerials = new Array();
var deviceTypes = new Array();

function initDataTableCustom() {
	var searchGroupId = "";

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
			 "url":"/api/collection/list/device",
			 "type":"GET"
		},
        columns : [
        	{data: "serial"},
        	{data: "deviceType"},
        	{data: "serialNum"},
        	{data: "stationName"},
        	{data: "groupName"},
        	{data: "groupId"},
        	{data: "deviceType"},
        	{data: "spaceName"},
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	orderable: false,
	        	render: function(data, type, full, meta) {
	        		var deviceSerial = full.serialNum;
	        		var deviceType = "";
	        		if (full.deviceType == "IAQ") {
	        			deviceType = "iaq";
	        		} else if (full.deviceType == "OAQ") {
	        			deviceType = "oaq";
	        		} else if (full.deviceType == "DOT") {
	        			deviceType = "dot";
	        		} else {
	        			deviceType = "vent";
	        		}

	        		return '<label style="width: 100%;"><input type = "checkbox" name = "chBox" onclick = "chboxFunc(this)" class = "chBox" data-type = "' + deviceType + '" data-serial = "' + deviceSerial + '"> </label>';
				},
        	}, {
	        	targets: 1,
	        	orderable: true,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + ((data == null || data == "") ? "VENT" : data) + "</strong>"
				},
        	}, {
	        	targets: [ 2, 3, 4 ],
	        	orderable: true,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + ((data == null || data == "") ? "-" : data) + "</strong>"
				},
        	}, {
	        	targets: 5,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + ((data == null || data == "") ? "-" : "g_" + data) + "</strong>"
				},
        	},
        	{
	        	targets: 6,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + ((data == null || data == "") ? "-" : "g_" + data) + "</strong>"
				},
        	},
        	{
	        	targets: 7,
	        	visible:false,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + ((data == null || data == "") ? "-" : "g_" + data) + "</strong>"
				},
        	},
    	],
    	initComplete: function(settings) {
    		
	    },
	    drawCallback: function(settings) {
	    	deviceSerials = [];
	    	deviceTypes = [];

	    	$(".paginate_button").click(function() {
	    		if ($("#allCh").attr("checked")) {
	    			$("input[name=chBox]").attr("checked", false);
	    			$("#allCh").trigger("click");
	    		}
	    	})
	    }
	});

	$('.search_bottom input').unbind().bind('keyup', function () {
		//table.column(2).search(this.value).draw();
		table.search(this.value).draw();
    });

	$("#searchGroup").change(function() {
		if (this.value == "")
			table.column(5).search("").draw();
		else { 
			searchGroupId = "g_" + this.value;
			table.column(5).search(searchGroupId).draw();
		}
	});

	$("#searchDeviceType").change(function() {
		table.column(6).search(this.value).draw();
		table.column(7).search("").draw();
	});

	$("#searchSpace").change(function() {
		table.column(7).search(this.value).draw();
	});

	$(".dt-buttons").css("position", "relative");

	$("#deviceTable_filter").hide();
}

function chboxFunc(t) {
	console.log("ch :: ", $(t).attr("data-serial"));
	if (deviceSerials.includes($(t).attr("data-serial"))) {
		deviceSerials.splice(deviceSerials.indexOf($(t).attr("data-serial")), 1);
		deviceTypes.splice(deviceSerials.indexOf($(t).attr("data-type")), 1);
	} else {
		deviceSerials.push($(t).attr("data-serial"));
		deviceTypes.push($(t).attr("data-type"));
	}
}

function dataDownload() {
	if (deviceSerials.length == 0) {
		alert("장비를 선택하세요.");
		return;
	} 

	if (!confirm("다운로드가 전부 완료 전까지 페이지 이동 및 제어가 불가능합니다.\n다운로드 받으시겠습니까?")) {
		return;
	}

	var $preparingFileModal = $("#preparing-file-modal");
	$preparingFileModal.dialog({
		modal : true
	});

	$("#progressbar").progressbar({
		value : false
	});

	var param = "";
	var standard = $("#searchStandard option:selected").val();
	var startTime = $("#startDt").val();
	var endTime = $("#endDt").val();

	if (!startTime && !endTime) {
		var today = new Date();
		var year = today.getFullYear();
		var month = today.getMonth() + 1;
		var day = today.getDate();

		if (month < 10) month = "0" + month;

		if (day < 10) day = "0" + day;

		startTime = year + "/" + month + "/" + day + "-00:00:00";
		endTime = year + "/" + month + "/" + day + "-23:59:59";

	} else {
		startTime = startTime + "-00:00:00";
		endTime = endTime + "-23:59:59";
	}

	param = "startTime=" + startTime + "&endTime=" + endTime + "&standard=" + standard;
    downtime=0.0;
    plustime();
	dataMakeToExcel(0, param);

}

function dataMakeToExcel(index, param) {
	var $preparingFileModal = $("#preparing-file-modal");
	$preparingFileModal.dialog({
		modal : true
	});

	var serial = "&serial=" + deviceSerials[index];
	var type = "&deviceType=" + deviceTypes[index];

	$.fileDownload("/api/collection/data/excel/download?" + param + serial + type, {
		successCallback : (url) => {
			$preparingFileModal.dialog('close');
			if (index != (deviceSerials.length - 1)) {
				dataMakeToExcel(++index, param);
			}else{
            clearInterval(interval);
			}
		},
		failCallback : (responseHtml, url) => {
			$preparingFileModal.dialog('close');
			if (index != deviceSerials.length) {
				dataMakeToExcel(++index, param);
			}
			$("#error-modal").dialog({
				modal : true
			});
		}
	});
}

function getGroupList() {
	var optHtml = "";

	$.ajax({
		method : "GET",
		url : "/system/group/get",
		contentType : "application/json; charset=utf-8",
		data : "searchUseYn=Y",
		success : function(param) {
			optHtml += "<option value=''>전체</option>";
			for(var i = 0; i < param.data.length; i++) {
				optHtml += "<option value='" + param.data[i].groupId + 
				"'>" + param.data[i].groupName + "</option>";
			}

			$("#searchGroup").html(optHtml);
		}
	});
}

function datePickerCustom(referenceDate) {
	$("#startDt").datepicker("destroy");
	$("#endDt").datepicker("destroy");

	$("#startDt").datepicker({
		dateFormat : 'yy/mm/dd',
		onSelect : (selectDate) => {
			var stxt = selectDate.split("/");
			stxt[1] = stxt[1] - 1;

			var sdate = new Date(stxt[0], stxt[1], stxt[2]);
			var edate = new Date(stxt[0], stxt[1], stxt[2]);
			edate.setDate(sdate.getDate() + referenceDate);
			sdate.setDate(sdate.getDate() + referenceDate - 1);

			var s_year = sdate.getFullYear();
			var s_month = sdate.getMonth() + 1;
			var s_day = sdate.getDate();

			if (s_month < 10)
				s_month = "0" + s_month;
			if (s_day < 10)
				s_day = "0" + s_day;

			$('#endDt').datepicker('option', {
				minDate : selectDate,
				beforeShow : () => {
					$("#endDt").datepicker("option", "maxDate", edate);
				}
			});

			$('#endDt').focus();
		}
	}).datepicker("setDate", new Date());

	$("#endDt").datepicker({dateFormat : 'yy/mm/dd'}).datepicker("setDate", new Date());

	$("#startDt").datepicker("refresh");
	$("#endDt").datepicker("refresh");
}

function getHighSpace(val) {
  var optHtml = "<option value=''>전체</option>";
  var paramIdx = "";
  if (val != "") {
	  if (val == "iaq") {
		  paramIdx = 1;
	  } else if (val == "oaq") {
		  paramIdx = 2;
	  } else if (val == "dot") {
		  paramIdx = 3;
	  }
	  document.getElementById("searchParentSpace").disabled = false;
	  document.getElementById("searchSpace").disabled = false;

	  $.ajax({
	    method: "GET",
	    url: "/system/space/high",
	    contentType: "application/json; charset=utf-8",
	    data: "searchUseYn=Y&deviceTypeIdx=" + paramIdx,
	    success: function (param) {
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
  } else {
	  $("#searchParentSpace").html("<option value=''>전체</option>");
	  $("#searchSpace").html("<option value=''>전체</option>");
	  document.getElementById("searchParentSpace").disabled = true;
	  document.getElementById("searchSpace").disabled = true;
  }
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


function plustime(){

    interval = setInterval(addtime,100);

}

function addtime(){

    downtime+=0.1;
    document.getElementById("dataDownTime").innerHTML = downtime.toFixed(1);
}

$().ready(() => {
	$('.select2').select2();
    $('.select2bs4').select2({
    	theme: 'bootstrap4'
    });

    datePickerCustom(182);

	getGroupList();

	initDataTableCustom();

	$("#startDt").one("click", () => {
		alert("최대 6개월치 데이터, 다운로드가 가능합니다.");
	})

	$("#allCh").click(function() {

		if($("#allCh").prop("checked")) {
			$("input[name=chBox]").attr("checked", $("#allCh").prop("checked"));
			$("input[name=chBox]:checked").each(function() {
				if ($(this).attr("data-serial") != null) {
					deviceSerials.push($(this).attr("data-serial"));
					deviceTypes.push($(this).attr("data-type"));
				} 
			});
		} else {
			deviceSerials = [];
			deviceTypes = [];
			$("input[name=chBox]").attr("checked", $("#allCh").prop("checked"));
		}

		console.log("deviceSerials :: ", deviceSerials);

	})
})