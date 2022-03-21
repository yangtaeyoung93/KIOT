/**
 * 시스템관리 > 장비 측정요소 카테고리
 */

function initDataTableCustom() {
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
		dom : '<"top"lfB>rt<"bottom"ip><"clear">',
        buttons : [ {
        	extend : 'csv',
        	charset : 'UTF-8',
        	text : '엑셀 다운로드',
        	footer : false,
        	bom : true,
        	filename : '측정요소_다운로드',
        	className : 'btn-primary btn excelDownBtn'
        } ],
        destroy: true,
        processing: true,
        serverSide: false,
		ajax : {
			 "url":"/system/device/elements/get",
			 "type":"GET",
			 data : function (param) {
				 param.useYn = "Y";
             }
		},
        columns : [
        	{data: "rowNum"},
            {data: "korName"},
            {data: "engName"},
            {data: "elementUnit"},
            {data: "elementConvert"},
            {data: "viewName"},
            {data: "validDigits"},
            {data: "dataMin"}
        ],
        columnDefs: [ 
        	{
	        	targets:   [0, 1 , 2, 3, 4, 5],
	        	render: function(data, type, full, meta) {
					return '<label style="width: 100%;"><strong>' + (data == null ? "-" : data) + '</strong></label>';
				},
        	},
        	{
	        	targets:   [6],
	        	render: function(data, type, full, meta) {
	        		var resData = "";

	        		if (data == null) {
	        			resData = "-";

	        		} else {
	        			resData = (data == "0") ? "정수" : "소수점 " + data + "자리";
	        		}

	        		return '<label style="width: 100%;"><strong>' + resData + '</strong></label>';
				},
        	},
        	{
            	targets:   7,
            	render: function(data, type, full, meta) {
            	    let result;
            	    let min = full.dataMin;
            	    let max = full.dataMax;
            	    if(min == null && max!= null){
            	        result = '<strong>' + max + '</strong>';
            	    }else if(min != null && max == null){
            	        result = '<strong>' + min + '</strong>';
            	    }else if(min ==null && max ==null){
            	        result = '<strong>-</strong>';
            	    }else{
            	        result = '<strong>'+min+' ~ '+max+'</strong>';
            	    }

            	    return result;
            	},
            },
        ],
	});

	$(".dt-buttons").css("position", "relative");
	$(".excelDownBtn").css({
		"position" : "absolute",
		"right" : "0",
		"top" : "-40px"
	});

	$('.search_bottom input').unbind().bind('keyup', function () {
        var colIndex = document.querySelector('#searchType').selectedIndex;
        table.column(colIndex + 1).search(this.value).draw();
    });
    
    $(".dataTables_filter").hide();
    
}

function insertCategory() {
	$.ajax({
		method : "POST",
		url : "/system/device/type/post",
		data : JSON.stringify({
			deviceType : $('#type').val(),
			deviceTypeName : $('#typeName').val(),
			description : $('#description').val(),
			useYn : $('#useYn').val()
		}),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			console.log('insertCategory LOG : ', d.data);
			alert("등록 되었습니다.");
			location.reload();
		}
	});
}

function updateCategory() {
	$.ajax({
		method : "PUT",
		url : "/system/device/type/put",
		data : JSON.stringify({
			idx : $('#deviceTypeIdx').val(),
			deviceType : $('#type').val(),
			deviceTypeName : $('#typeName').val(),
			description : $('#description').val(),
			useYn : $('#useYn').val()
		}),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			console.log('updateCategory LOG : ', d.data);
			alert("수정 되었습니다.");
			location.reload();
		}
	});
}

function deleteCategory() {
	var confirmVal = confirm("정말 삭제하시겠습니까?");

	if (confirmVal) {
		var checkArr = new Array();
		$("input[class='chBox']:checked").each(function() {
			checkArr.push($(this).attr("data-cartNum"));
		});
		$.ajax({
			method : "DELETE",
			url : "/system/device/type/delete",
			data : JSON.stringify({
				chArr : checkArr
			}),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log('deleteCategory LOG : ', d.data);
				alert("삭제 되었습니다.");
				location.reload();
			}
		});
	}
}

function equiCatDetail(paramIdx, paramType, paramTypeName, paramEtc, paramUseYn) {
	var idx = paramIdx;
	var type = paramType;
	var typeName = paramTypeName;
	var etc = paramEtc;
	var useYn = paramUseYn;

	$('#insertBtn').hide();
	$('#updateBtn').show();
	$('#useYn').attr('disabled', true);
	$('#modal-lg').modal();

	$('#deviceTypeIdx').val(paramIdx);
	$('#type').val(type);
	$('#typeName').val(typeName);
	$('#description').val(paramEtc);
	$('#useYn').val(useYn);
}

$().ready(function() {
	initDataTableCustom();
	$(".dataTables_filter").hide();
	
	$('#openModalBtn').click(function() {
		$('#insertBtn').show();
		$('#updateBtn').hide();
		$('#useYn').attr('disabled', false);

		// Defaullt 설정 
		$('#deviceTypeIdx').val("");
		$('#type').val("");
		$('#typeName').val("");
		$('#description').val("");
		$('#useYn').val("Y");
	})
	
	$('#searchBtn').click(function() {
		initDataTableCustom();
	})
	
	$('#insertBtn').click(function() {
		insertCategory();
	})

	$('#updateBtn').click(function() {
		updateCategory();
	})

	$('#deleteBtn').click(function() {
		deleteCategory();
	})
})