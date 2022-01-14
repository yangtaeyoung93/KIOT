/**
 * 시스템관리 > 장비 관리 목록
 */

function initDataTableCustom() {
	var searchUseYn = $('input[name="useYnRadio"]:checked').attr('r-data');
	var searchTestYn = $('input[name="testYnRadio"]:checked').attr('r-data');
	var searchSerialNum = $('#searchSerialNum').val();
	var searchType = $("#searchType").val();
	var searchValue2 = $("#searchValue2").val();
	var searchValue3 = $("#searchValue3").val();
	
	var table = $('#deviceTable').DataTable({
		scrollCollapse: true,
		autoWidth: false,
		searching : false,
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
			 "url":"/system/device/get",
			 "type":"GET",
			 data : function (param) {
				 console.log(param);
				 param.useYn = searchUseYn;
				 param.deviceType = $('#selectDeviecType').val();
				 param.testYn = searchTestYn;
				 param.serialNum = searchSerialNum;
				 param.searchValue2 = searchValue2;
				 param.searchValue3 =searchValue3;
				 param.searchType = searchType;
             }
		},
        columns : [
        	{data: "idx"},
        	{data: "rowNum"},
            {data: "deviceModel"},
            {data: "serialNum"},
            {data: "productDt"},
            {data: "createDt"},
            {data: "useYn"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
	        		return '<label style="width: 100%;"><input type="checkbox" name="boardIdx" value='+full.idx + ' class="chBox" data-cartNum='+full.idx +' data-useyn = '+full.useYn+' data-serialNum = '+full.serialNum+'> </label>';
	        		
				},
        	},
	        {
	        	targets:   [1, 2, 3, 4, 6],
	        	render: function(data, type, full, meta) {
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='deviceDetail(\""+full.idx+"\")'><strong>" + data + "</strong></a> ";

				},
	        },
	        {
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		//full.createDt.substring(0,19) 
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='deviceDetail(\""+full.idx+"\")'><strong>" + full.createDt.substring(0,19)  + "</strong></a> ";
	        				
				},
        	},
        ],
	});
}

function deviceDetail(idx) {
	location.href="/system/device/detail/" + $('#selectDeviecType').val() + "/" + idx;
}

function deleteDevice() {
	var confirmVal = confirm("정말 삭제하시겠습니까?");

	if (confirmVal) {
		var checkArr = new Array();
		var nameArr = new Array(); //사용자
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
			nameArr.push($(this).attr("data-serialNum"));
		});

		$.ajax({
			method : "DELETE",
			url : "/system/device/delete",
			data : JSON.stringify({
				chArr : checkArr,
				nameArr : nameArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log(d);
				if(d.resultCode == 2){
					alert("등록된 사용자가 존재합니다. \n 시리얼:"+d.checkName);
					return false;
					
				}else if(d.resultCode == 3){
					alert("등록된 환기청정기가 존재합니다. \n 시리얼:"+d.checkName);
					return false;
					
				}else if(d.resultCode == 4){
					alert("등록된 측정기가 존재합니다. \n 시리얼:"+d.checkName);
					return false;
					
				}else{
					alert("삭제 되었습니다.");
					location.reload();
				}
			}
		});
	}
}

function goInsert() {
	location.href="/system/device/detail/" + $('#selectDeviecType').val();
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

function check(){
	var file = $("#excel").val();
	if(file == "" || file == null){
		alert("파일을 선택");
		return false;
	} else if (!checkFileType(file)) {
		alert("엑셀 파일만 업로드");
		return false;
	}

	var fileFormat = file.split(".");
	var fileType = fileFormat[1];

	if(confirm("업로드 하시겠습니까?")) {
		$("#excelUpForm").attr("action","/system/device/excelInsert");
		var options = {
				type: "POST",
				data : {
					"excelType" : fileType,
					"deviceTypeIdx" : $("#deviceModelIdx option:selected").attr("type-idx"),
					"deviceModelIdx" : $("#deviceModelIdx option:selected").val()
				},
				success:function(data) {

					if 		(data == "SUCCESS") 			alert("업로드 완료");
					else if (data == "BLANK")				alert("값 이 비어 있습니다.");
					else if (data == "TYPE_CHECK") 			alert("입력 데이터 서식을 확인 해주세요 . (Only Text Format .)");
					else if (data == "SERIAL_SIZE_CHECK") 	alert("시리얼 번호, 최대 30자리 까지 입력 가능 .");
					else if (data == "PRODUCT_SIZE_CHECK") 	alert("생산 일자, 최대 10자리 까지 입력 가능 .");
					else if (data == "YN_SIZE_CHECK") 		alert("테스트 여부, 최대 1자리 까지 입력 가능 .");
					else if (data == "FAIL")  				alert("업로드 실패");
					else if (data == "EXT_CHECK")			alert("파일을 체크해주세요. (Only .xlsx files)");
					else 									alert("중복 시리얼 발생, " + data);
					
					location.reload();
				}
			};
		$("form").ajaxSubmit(options);
	}
}

$().ready(function() {
	initDataTableCustom();

	$('#deleteBtn').click(function() {
		deleteDevice();
	})

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})

	$('#excelShowBtn').click(function() {
		$('#excelUploadDiv').toggle();
	})
})