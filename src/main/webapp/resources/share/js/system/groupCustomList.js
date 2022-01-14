/**
 * 시스템관리 > 커스텀 웹페이지
 */
function initDataTableCustom() {
	var searchValue = $('#searchValue').val();
	var table = $('#groupTable')
			.DataTable(
					{
						scrollCollapse : true,
						autoWidth : false,
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
						destroy : true,
						processing : true,
						serverSide : false,
						ajax : {
							"url" : "/system/group/custom/get",
							"type" : "GET",
							data : function(param) {
								param.searchValue = searchValue;
							}
						},
						columns : [ {
							data : "rowNum"
						}, {
							data : "groupCustomUrl"
						}, {
							data : "groupName"
						}, {
							data : "groupId"
						}, {
							data : "createDt"
						}, ],
						columnDefs : [
								{
									targets : [ 0, 2, 3, 4 ],
									render : function(data, type, full, meta) {
										return "<a href='#' style='cursor: pointer; width: 100%;' onclick='groupDetail(\""
												+ full.idx
												+ "\")'><strong>"
												+ data + "</strong></a>";
									},
								},
								{
									targets : 1,
									render : function(data, type, full, meta) {
										var groupCustomList = data.split(",");
										var resultGroupCustomList = "<div style='background: #F0F8FF; width:100%; height: 35px; overflow-y: auto;'>";

										for (var idx in groupCustomList) {
											resultGroupCustomList += 
													"<a href='#' style='cursor: pointer; width: 100%;' onclick='goCustomPage(\""
													+ groupCustomList[idx]
													+ "\",\""
													+ full.groupId
													+ "\")'><strong>"
													+ groupCustomList[idx]
													+ "</strong></a><br/>";
										}
										resultGroupCustomList += "</div>";

										return resultGroupCustomList;
									},
								} ],
					});

	$("#groupTable_filter").hide();
}

function goCustomPage(url, groupId) {
	$.ajax({
		method : "POST",
		url : "/api/client/master/auth",
		data : {
			groupId : groupId,
		},
		success : function(d) {
			alert(groupId + " 계정으로 접근하셨습니다.");
			window.open(url);
		}
	});
}

function groupDetail(data) {
	location.href = "/system/group/detail/" + data;
}

$().ready(function() {
	initDataTableCustom();
	$("#groupTable_filter").hide();

	$('#searchBtn').click(function() {
		initDataTableCustom();
	})
})