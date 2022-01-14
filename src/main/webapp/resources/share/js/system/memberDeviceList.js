/**
 * 시스템관리 > 사용자 장비 목록
 */
function initDataTableCustom() {
	var searchType = $("#searchType option:selected").val();
	var searchValue = $('#searchValue').val();
	var pointColor = "";
	var ClickTxt = "";
	var table = $('#memberDeviceTable').DataTable({
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
        dom : '<"top"Blf>rt<"bottom"ip><"clear">',
		responsive : false,
		buttons : [ {
			extend : 'csv',
			charset : 'UTF-8',
			text : '엑셀 다운로드',
			footer : false,
			bom : true,
			filename : "TEST_download",
			className : 'btn-primary btn excelDownBtn'
		} ],
		ajax : {
			 "url":"/system/member/device/get",
			 "type":"GET",
			 data : function (param) {
				 param.searchValue = searchValue;
				 param.searchType = searchType;
             }
		},
        columns : [
        	{data: "rowNum"},
        	{data: "userId"},
        	{data: "deviceCount"},
        	{data: "iaqDeviceCount"},
        	{data: "oaqDeviceCount"},
        	{data: "dotDeviceCount"},
        	{data: "ventDeviceCount"},
        	{data: "fileCnt"},
        	{data: "stationShared"}
           
        ],
        columnDefs: [ 
	        {
	        	targets:   [0, 1, 2, 3, 4, 5, 6, 8],
	        	render: function(data, type, full, meta) {
	        		if (full.deviceCount != 0) {
	        			pointColor = "color:#007bff; cursor: pointer;";
	        			ClickTxt = "onclick='memberDeviceDetail("+full.idx+", "+full.deviceCount+");'";
	        		}else{
	        			pointColor = "";
	        			ClickTxt = "";
	        		}

	        		ClikHtml = "<div style='width: 100%; "+pointColor+" ' id='memberDeviceIdx"+full.idx+"' "+ClickTxt+"><strong>"+data+"</strong></div>";

	        		return ClikHtml; 
				},
	        },
	        {
	        	targets:   7,
	        	render: function(data, type, full, meta) {
	        		if ( (parseInt(full.iaqDeviceCount) + parseInt(full.oaqDeviceCount) + parseInt(full.dotDeviceCount)) != parseInt(data) ) 
	        			return "<div style='width: 100%; color: red; cursor: pointer; ' id='memberDeviceIdx"+full.idx+"' "+"onclick='memberDeviceDetail("+full.idx+", "+full.deviceCount+");'"+"><strong>"+data+"</strong></div>";
	        		else 
	        			return "<div style='width: 100%; color: #007bff; cursor: pointer; ' id='memberDeviceIdx"+full.idx+"' "+"onclick='memberDeviceDetail("+full.idx+", "+full.deviceCount+");'"+"><strong>"+data+"</strong></div>";
				},
	        },
	        {
	        	targets:   [1, 2, 3, 4, 5, 6, 7, 8],
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + data + "</strong>";
				},
	        }
        ],
	});
	$("#memberDeviceTable_filter").hide();
	$('.excelDownBtn').css("float", "right");
}
function goInsert(){
	location.href="/system/member/device/insert";
}
function memberDeviceDetail(data,cnt) {
	if(cnt == 0){
		return false;
	}else{
		location.href="/system/member/device/detail/" + data;
	}
}

function deletememberDevice() {
	var boardCnt = $("input[name='boardIdx']:checked").length;
	
	if(boardCnt == 0) {
		alert("최소 1개 이상 삭제 가능합니다. 선택해주세요.");
		return false;
	}
	
	var confirmVal = confirm("정말 삭제하시겠습니까?");
	if (confirmVal) {
		var checkArr = new Array();
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
		});
		$.ajax({
			method : "DELETE",
			url : "/system/member/device/delete",
			data : JSON.stringify({
				chArr : checkArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log('deleteGroup LOG : ', d.data);
				alert("삭제 되었습니다.");
				location.reload();
			}
		});
	}	
}

function checkFileType(filePath){
	var fileFormat = filePath.split(".");
	if(fileFormat.indexOf("xls") > -1){
		return true;
	} else if (fileFormat.indexOf("xlsx") > -1) {
		return true;
	} else {
		return false;
	}
}

function check() {
	var file = $("#excel").val();
	if(file == "" || file == null) {
		alert("파일을 선택해주세요");
		return false;
	} else if (!checkFileType(file)) {
		alert("엑셀 파일만 선택해주세요");
		return false;
	}

	var fileFormat = file.split(".");
	var fileType = fileFormat[1];

	if(confirm("업로드 하시겠습니까?")) {
		$("#excelUpForm").attr("action","/system/member/device/excelInsert");
		var options = {
				type: "POST",
				data : {
					"excelType" : fileType
				},
				success:function(data) {
					console.log(data);
					if 		(data == "SUCCESS") 			alert("업로드 완료");
					else if (data == "FAIL")  				alert("업로드 실패.");
					else if (data == "EXT_CHECK")			alert("파일을 체크해주세요. (Only .xlsx files)");
					else 									alert(data);

					location.reload();
				}
			};
		$("form").ajaxSubmit(options);
	}
}

$().ready(function() {
	initDataTableCustom();
	$("#memberDeviceTable_filter").hide();

	$('#deleteBtn').click(function() {
		deletememberDevice();
	})

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})

	$('#excelShowBtn').click(function() {
		$('#excelUploadDiv').toggle();
	})
})