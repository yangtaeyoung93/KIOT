/**
 * 시스템관리 > 메뉴 목록
 */
function SearchEquiCnt(txt){
	$("#ic-rcir").removeClass("list-box-color");
	$("#ic-scir").removeClass("list-box-color");
	$("#ic-edit").removeClass("list-box-color");
	$("#ic-table").removeClass("list-box-color");
	$("#ic-th").removeClass("list-box-color");
	$("#ic-img").removeClass("list-box-color");
	$("#ic-sqa").removeClass("list-box-color");
	$("#ic-tac").removeClass("list-box-color");

	if		(txt == "ic-rcir") $('#menuTag').val("far fa-circle");
	else if (txt == "ic-scir") $('#menuTag').val("fas fa-circle");
	else if (txt == "ic-edit") $('#menuTag').val("fas fa-edit");
	else if (txt == "ic-table") $('#menuTag').val("fas fa-table");
	else if (txt == "ic-th") $('#menuTag').val("fas fa-th");
	else if (txt == "ic-img") $('#menuTag').val("far fa-image");
	else if (txt == "ic-sqa") $('#menuTag').val("far fa-plus-square");
	else if (txt == "ic-tac") $('#menuTag').val("fas fa-tachometer-alt");

	$("#"+txt).addClass("list-box-color");
}

function initDataTableCustom() {
	var searchUseYn = $('input[name="customRadio"]:checked').attr('r-data');
	var searchType = $("#searchType option:selected").val();
	var searchValue = $('#searchValue').val();
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
			 "url":"/system/admin/menu/get",
			 "type":"GET",
			 data : function (param) {
				 param.searchUseYn = searchUseYn;
				 param.searchValue = searchValue;
				 param.searchType = searchType;
             }
		},
        columns : [
        	{data: "rowNum"},
        	{data: "menuName"},
            {data: "fullMenuName"},
            {data: "menuTag"},
            {data: "menuUrl"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
					return `
	                <label style="width: 100%;">
	                    <input type="checkbox" name="boardIdx" value="` + 
	                    full.idx + `" class="chBox" data-cartNum="` + 
	                    full.idx + `">`  + `</label>`;
				},
        	},
        	{
	        	targets:   1,
	        	render: function(data, type, full, meta) {
	        		var txtHtml = "<strong>" + data + " (" + full.menuEng + ")</strong>";
					return txtHtml;
				},
	        },
	        {
	        	targets:   3,
	        	render: function(data, type, full, meta) {
	        		var txtHtml = "<i class='" + data + "'></i>";
					return txtHtml;
				},
	        },
	        {
	        	targets:   [2, 4],
	        	render: function(data, type, full, meta) {
	        		var txtHtml = "<strong>" + data + "</strong>";
					return txtHtml;
				},
	        }
        ],
	});

	$("#menuTable_filter").hide();
}

function insertMenu() {
	var menuName = $('#menuName').val();
	var menuEng = $('#menuEng').val();
	var menuOrder= $('#menuOrder').val();
	var menuUrl = $('#menuUrl').val();
	var menuTag = $('#menuTag').val();
	var highRankMenu = $('#highRankMenu').val();
	var menuLevel = parseInt($('#highRankMenu option:selected').attr("data-level")) + 1;

	var obj = {
		menuName : menuName,
		menuEng : menuEng,
		menuOrder : menuOrder,
		menuUrl : menuUrl,
		menuTag : menuTag,
		highRankMenu : highRankMenu,
		menuLevel : menuLevel
	};

	$.ajax({
		method : "POST",
		url : "/system/admin/menu/post",
		data : JSON.stringify(obj),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			alert("등록 되었습니다.");
			location.reload();
		}
	});
}

function deleteMenu(){
	var boardCnt = $("input[name='boardIdx']:checked").length;
	
	if(boardCnt == 0){
		alert("최소 1개 이상 삭제 가능합니다. 선택해주세요.");
		return false;
	}

	var confirmVal = confirm("정말 삭제하시겠습니까? \n * 하위 메뉴도 삭제 됩니다. *");
	if (confirmVal) {
		var checkArr = new Array();
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
		});
		$.ajax({
			method : "DELETE",
			url : "/system/admin/menu/delete",
			data : JSON.stringify({
				chArr : checkArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log('menu LOG : ', d.data);
				alert("삭제 되었습니다.");
				location.reload();
			}
		});
	}	
}

$().ready(function() {
	initDataTableCustom();

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})

	$('#insertBtn').click(function() {
		insertMenu();
	})

	$('#deleteBtn').click(function() {
		deleteMenu();
	})
})