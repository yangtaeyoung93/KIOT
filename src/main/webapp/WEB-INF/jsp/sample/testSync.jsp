<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<script src="https://code.jquery.com/jquery-2.2.3.min.js"></script>
<script>
var succDataStr = "";
var failDataStr = "";
var urlList = [
	// ["AI365, 사용자 연동 장비 전체 조회", "/api/ai365/member/device/list"], 
	["그룹 별 연동 스테이션 조회", "/system/member/device/get/1"],
	["그룹 상세 조회 (그룹 내 사용자 목록 데이터)", "/system/group/get/1"],
	["회원 상세 조회", "/system/member/get/1"],
	["스테이션 요소 조회", "/api/ai365/elements?auth=group&userId=kw_group@korea.kr"],
];

function func1(name, url, index) {
	$.ajax({
		method : "GET",
		url : url,
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			succDataStr += ("<strong style='color: blue'> INDEX :: (" + index + ") </strong> ==> " + JSON.stringify(d) + "<br/><hr/>");
			$("#succResDiv").html(succDataStr);
		}, 
		error : function() {
			failDataStr += ("INDEX :: (" + index + ") ==> " + JSON.stringify(d) + "<br/><hr/>");
			$("#failResDiv").html(failDataStr);
		}
	});
}

function funcStart(index) {
	var resHtml = "";

	for (var i = 1; i <= index; i++) {
		for (var idx in urlList) {
			func1(urlList[idx][0], urlList[idx][1], i);
		}
	}

	console.log("API Call Count :: ", index);
}

$().ready(function() {
	$("#startBtn").click(function() {
		succDataStr = "";
		failDataStr = "";
		$("#succResDiv").html(succDataStr);
		$("#failResDiv").html(failDataStr);

		var idx = $("#numberTxt").val();
		funcStart(idx);
	})

	funcStart(1);
})
</script>
</head>
<body>
	<div style="text-align: center;">
		<h1>DataCenter Test</h1>
		<label>실행 횟 수</label>
		<input type="number" id="numberTxt" value="1">
		<button id="startBtn">실 행</button>
		<span style="margin-left: 30px;">
			<strong>통계 분석, 사용 API</strong>
		</span>
	</div>
	<hr/>
	<div style="width: 1800px; height: 1000px; display: inline-block;">
		<div id="succResDiv" style="border: 1px solid black; width: 49%; height: 990px; padding: 5px; overflow: auto; display: inline-block; background: aliceblue;"></div>
		<div id="failResDiv" style="border: 1px solid black; width: 49%; height: 990px; padding: 5px; overflow: auto; display: inline-block; background: #ff000017;"></div>
	</div>
</body>
</html>