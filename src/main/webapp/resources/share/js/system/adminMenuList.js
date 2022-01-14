/**
 * 시스템관리 > 메뉴 목록
 */
function initDataTableCustom() {
	var table = $('#menuTable').DataTable({
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
			 "url":"/system/admin/menu/auth/get",
			 "type":"GET"
		},
        columns : [
        	{data: "rowNum"}, //0
        	{data: "userId"},
            {data: "userName"},
            {data: "menuCnt"}
        ],
        columnDefs: [ 
	        {
	        	targets:   1,
	        	render: function(data, type, full, meta) {
	        		return "<strong><a href='#' onclick='goDetail(" + full.idx + ");'>" + data + "</a></strong>";
				},
	        },
	        {
	        	targets:   [0, 2],
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + data + "</strong>";
				},
	        },
	        {
	        	targets:   3,
	        	render: function(data, type, full, meta) {
	        		return "<strong>" + data + " / " + full.totalMenuCnt + "</strong>";
				},
	        }
        ],
	});

	$("#menuTable_filter").hide();
}

function goDetail(t) {
	location.href="/system/admin/menu/auth/detail/" + t;
}

$().ready(function() {
	initDataTableCustom();
})