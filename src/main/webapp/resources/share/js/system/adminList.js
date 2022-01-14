/**
 * 시스템관리 > 운영자 계정 목록
 */
function initDataTableCustom() {
	var searchUseYn = $('input[name="customRadio"]:checked').attr('r-data');
	var searchType = $("#searchType option:selected").val();
	var searchValue = $('#searchValue').val();
	var table = $('#adminTable').DataTable({
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
			 "url":"/system/admin/get",
			 "type":"GET",
			 data : function (param) {
				 param.searchUseYn = searchUseYn;
				 param.searchValue = searchValue;
				 param.searchType = searchType;
             }
		},
        columns : [
        	{data: "rowNum"}, //0
        	{data: "idx"},
        	{data: "userId"},
            {data: "userName"},
            {data: "createDt"},
            {data: "useYn"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
	        		return '<label style="width: 100%;"><input type="checkbox" name="boardIdx" value='+full.idx + ' class="chBox" data-cartNum='+full.idx +' data-useyn = '+full.useYn+' > </label>';

				},
        	},
	        {
	        	targets:   1,
	        	render: function(data, type, full, meta) {
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='adminDetail(\""+full.idx+"\")'><strong>" + data + "</strong></a> ";
				},
	        },
	        {
	        	targets:   4,
	        	render: function(data, type, full, meta) {
	        		return full.createDt.substring(0,19);
				},
        	},
        ],
	});

	$("#adminTable_filter").hide();
}

function adminDetail(data) {
	location.href="/system/admin/detail/" + data;
}

function deleteAdmin(){
	var boardCnt = $("input[name='boardIdx']:checked").length;
	
	if(boardCnt == 0){
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
			url : "/system/admin/delete",
			data : JSON.stringify({
				chArr : checkArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log('admin LOG : ', d.data);
				alert("삭제 되었습니다.");
				location.reload();
			}
		});
	}	
}


function goInsert(){
	location.href="/system/admin/detail";
}


$().ready(function() {
	initDataTableCustom();
	
	$("#adminTable_filter").hide();
	
	$('#deleteBtn').click(function() {
		deleteAdmin();
	})

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})
})