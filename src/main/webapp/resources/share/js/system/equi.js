/**
 * 시스템관리 > 장비 관리
 */

function initDataTableCustom() {
	 var table = $('#deviceTable').DataTable({
		"language" : {
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
		autoWidth: false,
		//dom: 'Blrtifp',
		dom : 'Bfrtip',
		responsive: true,
		buttons : [ {
			extend : 'csvHtml5',
			text : '엑셀 다운로드',
			footer : true,
			className : 'btn-primary btn'
		} ]
	});

	$('#deviceTable_filter').prepend('<select id="select"></select>');
    $('#select').append('<option>TYPE</option><option>MODEL</option><option>SERIAL</option><option>USER</option>');

    $('.dataTables_filter input').unbind().bind('keyup', function () {
        var colIndex = document.querySelector('#select').selectedIndex;
        table.column(colIndex).search(this.value).draw();
    });

    $('#select').addClass('btn-primary');
    $('#select').css("padding-right", "10px");
}

function goInsert() {
	alert("INSERT");
}

function goUpdate() {
	alert("UPDATE");
}

$().ready(function() {
	initDataTableCustom();
})