/**
 * 시스템관리 > 사용자 계정 목록
 */
function initDataTableCustom() {
	var searchUseYn = $('input[name="customRadio"]:checked').attr('r-data');
	var searchValue = $('#searchValue').val();
	var table = $('#memberTable').DataTable({
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
			 "url":"/system/member/get",
			 "type":"GET",
			 data : function (param) {
				 param.useYn = searchUseYn;
				 param.userId = searchValue;
             }
		},
        columns : [
        	{data: "rowNum"}, //0
        	{data: "rowNum"},
        	{data: "userId"},
            {data: "regionName"},
            {data: "loginCount"},
            {data: "loginIp"},
            {data: "loginDt"},
            {data: "createDt"},
            {data: "stationShared"},
            {data: "useYn"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
					//return '<label style="width: 100%;"><input type="checkbox" name="boardIdx" value='+full.idx + ' class="chBox" data-cartNum='+full.idx +' data-useyn = '+full.useYn+' data-userId = '+full.userId+'>  + </label>';
	        		return '<label style="width: 100%;"><input type="checkbox" name="boardIdx" value='+full.idx + ' class="chBox" data-cartNum='+full.idx +' data-useyn = '+full.useYn+' data-userId = '+full.userId+'> </label>';
				},
        	},
	        {
	        	targets:   [1, 2, 3, 4, 8, 9],
	        	render: function(data, type, full, meta) {
					return "<a href='#' style='cursor: pointer; width: 100%;' onclick='memberDetail(\""+full.idx+"\")'><strong>" + data + "</strong></a> ";
				},
	        },
	        {
	        	targets:   5,
	        	render: function(data, type, full, meta) {
	        		var loginIp = full.loginIp;
	        		
	        		if(loginIp == "" || loginIp == "undefined" || loginIp == null ) {
	        			loginIp = "-";
	        			return loginIp;
	        		} else {
	        			//loginIp
	        			return "<a href='#' style='cursor: pointer; width: 100%;' onclick='memberDetail(\""+full.idx+"\")'><strong>" + loginIp + "</strong></a> ";
	        		}
	        		
	        		
				},
        	},
        	
	        {
	        	targets:   6,
	        	render: function(data, type, full, meta) {
	        		var loginDt = full.loginDt;
	        		
	        		if(loginDt) {
	        			loginDt = full.loginDt.substring(0,19);
	        			return "<a href='#' style='cursor: pointer; width: 100%;' onclick='memberDetail(\""+full.idx+"\")'><strong>" + loginDt + "</strong></a> ";
	        		} else {
	        			return "-";
	        		}

	        		return loginDt;
				},
        	},
	        {
	        	targets:   7,
	        	render: function(data, type, full, meta) {
	        		//full.createDt.substring(0,19)
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='memberDetail(\""+full.idx+"\")'><strong>" + full.createDt.substring(0,19) + "</strong></a> ";
				},
        	},
        ],
	});
	$("#memberTable_filter").hide();
}

function memberDetail(data) {
	location.href="/system/member/detail/" + data;
}

function deleteMember(){
	var boardCnt = $("input[name='boardIdx']:checked").length;
	var chkbox = document.getElementsByName('boardIdx');
	
	
	if(boardCnt == 0){
		alert("최소 1개 이상 삭제 가능합니다. 선택해주세요.");
		return false;
	}
	
	for(var i = 0; i < chkbox.length; i++){
		 if(chkbox[i].checked) {
			 var chkuseYn =  $("input[name='boardIdx']:checked").attr("data-useyn");
			 if(chkuseYn == "N"){
				 alert("이미 삭제 되어있습니다.");
				 return false;
				 break;
			 }
		 }
	}
	
	var confirmVal = confirm("선택된 아이디를 삭제하시겠습니까? ");
	if (confirmVal) {
		var checkArr = new Array();
		var nameArr = new Array(); //아이디
		
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
			nameArr.push($(this).attr("data-userId"));
		});
	
		$.ajax({
			method : "DELETE",
			url : "/system/member/delete",
			data : JSON.stringify({
				chArr : checkArr,
				nameArr : nameArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				/*console.log('deleteCategory LOG : ', d.data);
				alert("삭제 되었습니다.");
				location.reload();*/
				if(d.resultCode == 2){
					alert("등록된 장비가 존재합니다. \n 아이디:"+d.checkName);
					return false;
					
				}else if(d.resultCode == 3){
					alert("그룹에 포함된 계정이 존재합니다. \n 아이디:"+d.checkName);
					return false;
					
				}else{
					alert("삭제 되었습니다.");
					location.reload();
				}
				
			}
		});
	}else{
		return false;
	}	
}

function goInsert(){
	location.href="/system/member/detail";
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
		$("#excelUpForm").attr("action","/system/member/excelInsert");
		var options = {
				type: "POST",
				data : {
					"excelType" : fileType
				},
				success:function(data) {

					if 		(data == "SUCCESS") 			alert("업로드 완료");
					else if (data == "BLANK")				alert("값 이 비어 있습니다.");
					else if (data == "TYPE_CHECK") 			alert("입력 데이터 서식을 확인 해주세요 . (Only Text Format .)");
					else if (data == "ID_SIZE_CHECK") 		alert("사용자 아이디, 최대 50자리 까지 입력 가능 .");
					else if (data == "PW_SIZE_CHECK") 		alert("암호, 최대 10자리 까지 입력 가능 .");
					else if (data == "FAIL")  				alert("업로드 실패");
					else if (data == "EXT_CHECK")			alert("파일을 체크해주세요. (Only .xlsx files)");
					else 									alert("중복 계정, " + data);
					
					location.reload();
				}
			};
		$("form").ajaxSubmit(options);
	}
}

$().ready(function() {
	initDataTableCustom();
	$("#memberTable_filter").hide();
	
	$('#deleteBtn').click(function() {
		deleteMember();
	})

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})

	$('#excelShowBtn').click(function() {
		$('#excelUploadDiv').toggle();
	})
})