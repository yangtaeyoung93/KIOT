/**
 * 대시보드 > DID 그룹 관리
 */
function initDataTableCustom() {
	var pointColor = "";
	var ClickTxt = "";
	var searchUseYn = $('input[name="customRadio"]:checked').val();
	var searchType = $("#searchType option:selected").val();
	var searchValue = $('#searchValue').val();
	if(!searchUseYn){
		searchUseYn = "Y";
	}
	
	var table = $('#didgroupTable').DataTable({
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
			 "url":"/system/group/did/get",
			 "type":"GET",
			 data : function (param) {
				 param.searchType = searchType;
				 param.searchValue = searchValue;
				 param.searchUseYn = searchUseYn;
             }
		},
        columns : [
        	{data: "rowNum"},
        	{data: "rowNum"},
        	{data: "groupName"},
        	{data: "groupId"},
            {data: "didCnt"},
            {data: "groupDidCode"},
            {data: "didMemberCnt"},
            {data: "createDt"}
        ],
        columnDefs: [ 
        	{
	        	targets:   0,
	        	render: function(data, type, full, meta) {
	        		return '<label style="width: 100%;"> <input type="checkbox" name="boardIdx" value='+full.idx +' class="chBox" data-cartNum='+full.idx+' ></label>';
				},
        	},
	        {
	        	targets:   [1, 2, 3, 4, 5, 6],
	        	render: function(data, type, full, meta) {
	        		if (full.didCnt != 0) {
	        			pointColor = "color:#007bff; cursor: pointer;";
	        			ClickTxt = "onclick='didgroupDetail("+full.idx+");'";
	        		}else{
	        			pointColor = "";
	        			ClickTxt = "";
	        		}
	        		
	        		ClikHtml = "<div style='width: 100%; "+pointColor+" ' id='memberDeviceIdx"+full.idx+"' "+ClickTxt+"><strong>"+data+"</strong></div>";
	        		
					return ClikHtml;
				},
	        },
	        {
	        	targets:   6,
	        	render: function(data, type, full, meta) {
	        		if (full.didCnt != 0) {
	        			pointColor = "color:#007bff; cursor: pointer;";
	        			ClickTxt = "onclick='didgroupDetail("+full.idx+");'";
	        		}else{
	        			pointColor = "";
	        			ClickTxt = "";
	        		}

	        		ClikHtml = "<div style='width: 100%; "+pointColor+" ' id='memberDeviceIdx"+full.idx+"' "+ClickTxt+"><strong>"+full.createDt.substring(0,19)+"</strong></div>";
	        		
					return ClikHtml;
				},
        	},
        ],
	});
	$("#didgroupTable_filter").hide();
	if(searchUseYn){
		$("#searchUseYn").attr("checked",true);
	}
}
function goInsert(){
	location.href="/system/group/did/insert";
}
function didgroupDetail(data) {
	location.href="/system/group/did/detail/" + data;
}
function deleteDidGroup(){
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
			url : "/system/group/did/delete",
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


$().ready(function() {
	initDataTableCustom();
	$("#didgroupTable_filter").hide();
	
	$('#deleteBtn').click(function() {
		deleteDidGroup();
	})

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})
})