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
	["회원 상세 조회", "/system/member/get/1"]
];

function func1(name, url, index) {
	$.ajax({
		method : "GET",
		url : url,
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			console.log(d);
			succDataStr += (url + " , " + name + " , " + index);
			$("#succResDiv").html(succDataStr);
		}, 
		error : function() {
			failDataStr += (url + " , " + name + " , " + index);
			$("#failResDiv").html(failDataStr);
		}
	});
}

function funcStart(index) {
	var resHtml = "";

	for (var i = 0; i < index; i++) {
		func1(urlList[0][0], urlList[0][1], i)
		func1(urlList[1][0], urlList[1][1], i)
		func1(urlList[2][0], urlList[2][1], i)
/* 		func1(urlList[3][0], urlList[3][1], i) */
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
<h1>DataCenter Test</h1>
	<label>실행 횟 수</label>
	<input type="number" id="numberTxt">
	<button id="startBtn">실 행</button>

	<hr/>
	<div style="width: 1800px; height: 1000px; display: inline-block;">
		<div id="succResDiv" style="border: 1px solid black; width: 30%; height: 990px; overflow-y: auto; display: inline-block; background: aliceblue;"></div>
		<div id="failResDiv" style="border: 1px solid black; width: 30%; height: 990px; overflow-y: auto; display: inline-block; background: #ff000017;"></div>
	</div>
</body>
</html>