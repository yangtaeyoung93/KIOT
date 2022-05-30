/**
 * 시스템관리 > 그룹 게정 목록
 */
function initDataTableCustom() {
	var searchUseYn = $('input[name="customRadio"]:checked').val();
	var searchType = $("#searchType option:selected").val();
	var searchValue = $('#searchValue').val();
	
	if(!searchUseYn){
		searchUseYn = "Y";
	}
	
	var table = $('#masterTable').DataTable({
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
			 "url":"/system/master/get",
			 "type":"GET",
			 data : function (param) {
				 param.searchUseYn = searchUseYn;
				 param.searchValue = searchValue;
				 param.searchType = searchType;
             }
		},
        columns : [
        	{data: "rowNum"},
			{data: "rowNum"},
        	{data: "masterName"},
        	{data: "masterId"},
            {data: "masterCompanyName"},
            {data: "groupDepartName"},
            {data: "masterEmail"},
            {data: "masterPhoneNumber"},
            {data: "groupCnt"},
            {data: "createDt"},
            {data: "useYn"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
					return '<label style="width: 100%;"> <input type="checkbox" name="boardIdx" value='+full.idx +' class="chBox" data-cartNum='+full.idx+' data-masterId = '+full.masterId+'></label>';
				},
        	},
	        {
	        	targets:   [1, 2, 3, 4, 5, 6, 7, 8,10],
	        	render: function(data, type, full, meta) {
					console.log(data);
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='groupDetail(\""+full.idx+"\")'><strong>" + data + "</strong></a> ";
				},
	        },
	        {
	        	targets:   9,
	        	render: function(data, type, full, meta) {
	        		//full.createDt.substring(0,19)
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='groupDetail(\""+full.idx+"\")'><strong>" + full.createDt.substring(0,19) + "</strong></a> ";
				},
        	},
        ],
	});
	$("#masterTable_filter").hide();
	
	if(searchUseYn){
		$("#searchUseYn").attr("checked",true);
	}
}
function goInsert(){
	location.href="/system/master/detail";
}
function groupDetail(data) {
	location.href="/system/master/detail/" + data;
}
function deleteGroup(){
	var boardCnt = $("input[name='boardIdx']:checked").length;
	
	if(boardCnt == 0){
		alert("최소 1개 이상 삭제 가능합니다. 선택해주세요.");
		return false;
	}
	
	var confirmVal = confirm("정말 삭제하시겠습니까?");
	if (confirmVal) {
		var checkArr = new Array();
		var nameArr = new Array(); //사용자
		
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
			nameArr.push($(this).attr("data-groupId"));
		});
		$.ajax({
			method : "DELETE",
			url : "/system/master/delete",
			data : JSON.stringify({
				chArr : checkArr,
				nameArr : nameArr
			}),
			
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log(d);
				if(d.resultCode == 2){
					alert("등록된 DID가 존재합니다. \n 그룹아이디:"+d.checkName);
					return false;
					
				}else if(d.resultCode == 3){
					alert("그룹DID에 포함된 사용자가 존재합니다. \n 그룹아이디:"+d.checkName);
					return false;
					
				}else{
					alert("삭제 되었습니다.");
					location.reload();
				}
			}
		});
	}	
}


$().ready(function() {
	initDataTableCustom();
	$("#masterTable_filter").hide();
	
	$('#deleteBtn').click(function() {
		deleteGroup();
	})

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})
})