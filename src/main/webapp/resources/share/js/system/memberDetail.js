/**
 * 시스템관리 > 사용자 계정 등록/수정
 */
var map;
var position;
var lon = 126.9380517322744;
var lat = 37.16792263658907;

var obj = new Object();
function chkMemberId(){
	var userId = $("#userId").val();
	
	if(!userId){
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("아이디를 입력하세요.");
		return false;
	}

	if (!re_email.test(userId)) {
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("이메일 형식이 아닙니다.");
		return false;
	}

	$.ajax({
		 url:"/check/userId",
		 type:"GET",
		 async : false,
		 data : "userId="+userId,
		 success : function (param) {
			var result_val = param.resultCode;
			if(result_val == 1){
				$("#chk_txt").css("color","#28a745");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("사용가능한 아이디 입니다.");
				$("#chk_id").val("1");
				return false;
			} else if (result_val == 2) {
				$("#chk_txt").css("color","red");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("중복되는 아이디 입니다. 다시 확인하여 주세요.");
				$("#chk_id").val("0");
				return false;
			} else {
				$("#chk_txt").css("color","red");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("삭제된 아이디 입니다. 다시 확인하여 주세요.");
				$("#chk_id").val("0");
				return false;
			}
		 }
	})
}

function regionPickFunc(t) {
	console.log("attr ", $(t).attr("data-name"));

}

function getRegionInfoFunc() {
	var optHtml = "";

	$.ajax({
		method : "GET",
		url : "/system/member/region/get?lon=" + $("#lon").val() + "&lat=" + $("#lat").val(),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			var datas = d.data;
			for (var idx in datas)
				optHtml += "<option value='" + datas[idx].region_id + "' data-name='" + datas[idx].address + "' data-code='" + datas[idx].region_id +"'>" + datas[idx].address + "</option>";

			$("#regionInfo").html(optHtml);
			$("#regionName").val(datas[0].address);
			$("#region").val(datas[0].region_id);
		}
	});
}

function initMap() {
	var marker = [];
	var markerSource = new ol.source.Vector();
	function markerDraw(lon, lat) {
		markerSource.clear(true);

		marker[0] = new ol.Feature();
		marker[0].setStyle(new ol.style.Style({
			image : new ol.style.Icon({
				src : "/resources/share/img/air/me_icon1.png",
			})
		}));
		marker[0].setGeometry(new ol.geom.Point(ol.proj.transform([lon, lat], "EPSG:4326", "EPSG:900913")));
		markerSource.addFeatures(marker);
	}

	function getLocation() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				var lon = position.coords.longitude
				var lat = position.coords.latitude
				$("#lon").val(lon);
				$("#lat").val(lat);
				markerDraw(lon, lat);
				map.getView().setCenter(ol.proj.transform([lon, lat], 'EPSG:4326', 'EPSG:900913'));
			}, function(error) {
				console.error(error);
			}, {
				enableHighAccuracy: false,
				maximumAge: 0,
				timeout: Infinity
			});
		}
	}

	const sor = new ol.source.XYZ({
		url : 'http://mt.google.com/vt/lyrs=m&x={x}&y={y}&z={z}'
	})
	var layer = new ol.layer.Tile({
		source : sor
	})
	var markerLayer = new ol.layer.Vector({
		source : markerSource
	});
	var mapView = new ol.View({
		center : ol.proj.transform([ lon, lat ], 'EPSG:4326', 'EPSG:900913'),
		zoom : 13
	});
	map = new ol.Map({
		controls : new ol.control.defaults()
				.extend([ new ol.control.FullScreen({
					source : 'fullScreen'
				}) ]),
		layers : [ new ol.layer.Tile({
			source : sor
		}) ],
		target : 'map',
		view : mapView,
	});

	map.on('click', function(evt) {
		markerSource.clear(true);

		position = ol.proj.transform(evt.coordinate, 'EPSG:3857', 'EPSG:4326');
		$("#lon").val(position[0]);
		$("#lat").val(position[1]);
		markerDraw(position[0], position[1]);
	})

	map.addLayer(markerLayer);
	getLocation();
}

$().ready(function() {
	var h_idx = $("#userIdx").val();
	if(h_idx){
		$("#userId").attr("readonly",true);
		$("#userPw").attr("disabled",true);
		$("#chkpwDiv").show();
		$("#chk_userId").hide();
		if($("#region").val() == 0){
			$("#region").val("");
		}
	}else{
		$("#userId").attr("readonly",false);
		$("#userPw").attr("disabled",false);
		$("#chkpwDiv").hide();
		$("#chk_userId").show();
		$("#chk_id").val("0");
		
		$("#userId").blur(function(){
			chkMemberId();
		})
	}

	$('#readDiv').hide();

	$('#listBtn').click(function() {
		location.href="/system/member/list";
	})
	
	$('#insertBtn').click(function() {
		var userId = $("#userId").val();
		var userPw = $("#userPw").val();
		var region = $("#region").val();
		var regionName = $("#regionName").val();
		var stationShared = $("#stationShared option:selected").val();
		var useYn = "";
		var chkId = $("#chk_id").val();
		
		if(!userId){
			alert("아이디를 입력해주세요.");
			$("#userId").focus();
			return false;
		}
		
		if(chkId == 0){
			alert("아이디 중복 체크를 하시기 바랍니다.");
			$("#userId").focus();
			return false;
		}
		
		if(!userPw){
			alert("비밀번호를 입력해주세요.");
			$("#userPw").focus();
			return false;
		}else{
			if(re_script.test(userPw)){
				alert("스크립트는 사용할 수 없습니다.");
				userPw = userPw.replace(re_script,"");
				$("#userPw").focus();
				return false;
			}

			if(re_tag.test(userPw)){
				alert("태그를 사용할 수 없습니다.");
				userPw = userPw.replace(re_tag,"");
				$("#userPw").focus();
				return false;
			}
			
			if(re_blank.test(userPw)) {
				alert("공백만 사용할 수 없습니다.");
				$("#userPw").focus();
				return false;
			}
			
			if(re_blank1.test(userPw)) {
				alert("공백을 사용할 수 없습니다.");
				$("#userPw").focus();
				return false;
			}
		}

		if (region == "") {
			region = 0;
		}
		var obj = {
			userId : userId,
			userPw : userPw,
			region : region,
			regionName : regionName,
			stationShared : stationShared,
			useYn : "Y"
		}
		
		$.ajax({
			method : "POST",
			url : "/system/member/post",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("등록 되었습니다.");
				location.reload();
			}
		});
	})
	
	$('#updateBtn').click(function() {
		var userId = $("#userId").val();
		var userPw = $("#userPw").val();
		var region = $("#region").val();
		var regionName = $("#regionName").val();
		var stationShared = $("#stationShared option:selected").val();
		var useYn = "";
		var chkPw = $("input:checkbox[id='chkUserPw']").is(":checked");
		
		if(chkPw == true){
			if(!userPw){
				alert("비밀번호를 입력해주세요.");
				$("#userPw").focus();
				return false;
			}else{
				if(re_script.test(userPw)){
					alert("스크립트는 사용할 수 없습니다.");
					userPw = userPw.replace(re_script,"");
					$("#userPw").focus();
					return false;
				}

				if(re_tag.test(userPw)){
					alert("태그를 사용할 수 없습니다.");
					userPw = userPw.replace(re_tag,"");
					$("#userPw").focus();
					return false;
				}
				
				if(re_blank.test(userPw)) {
					alert("공백만 사용할 수 없습니다.");
					$("#userPw").focus();
					return false;
				}
				
				if(re_blank1.test(userPw)) {
					alert("공백을 사용할 수 없습니다.");
					$("#userPw").focus();
					return false;
				}
			}
		}

		if (region == "") {
			region = 0;
		}

		var obj = {
			idx : $("#userIdx").val(),
			userId : userId,
			userPw : userPw,
			region : region,
			regionName : regionName,
			stationShared : stationShared,
			useYn : "Y"
		}
		console.log("obj : ", obj.idx);
		$.ajax({
			method : "PUT",
			url : "/system/member/put",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("수정 되었습니다.");
				location.reload();
			}
		});
	})

	$("#chkUserPw").click(function(){
		var chkPw = $("input:checkbox[id='chkUserPw']").is(":checked");
		if(chkPw == true){
			$("#userPw").attr("disabled",false);
		}else{
			$("#userPw").attr("disabled",true);
		}
	})

	initMap();

    $('#regionInfo').change(function() {
    	regionPickFunc(this);
    	$("#regionName").val($(this).find("option:selected").data("name"));
    	$("#region").val($(this).find("option:selected").data("code"));
    })

	const resetBtn = document.getElementById("loginCntReset");

	resetBtn.addEventListener("click", () => {
		const xhr = new XMLHttpRequest();
		const userId = document.getElementById("userId").value;
		const object = new Object();
		object.userId = userId;

		xhr.open('POST','/system/member/reset',true);
		xhr.responseType = "json";
		xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
		xhr.send(JSON.stringify(object));

		xhr.onload = function () {
			if(xhr.status == 200){
				alert(`${userId}님의 로그인 실패 횟수를 초기화 하였습니다.`);
				location.reload();
			}else{
				alert("실패하였습니다.");
			}
		}
	});
})

