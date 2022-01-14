<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/gdatacenter_head.jsp"%>
<script src="/resources/share/js/datacenter/gdc.js"></script>
<style>
/* 나눔고딕 */
@font-face {
	font-family: '나눔고딕';
	font-style: normal;
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.eot);
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.eot?#iefix) format('embedded-opentype'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.woff2) format('woff2'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.woff) format('woff'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.ttf) format('truetype');
}

@font-face {
	font-family: 'NanumGothic';
	font-style: normal;
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.eot);
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.eot?#iefix) format('embedded-opentype'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.woff2) format('woff2'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.woff) format('woff'),
	   url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.ttf) format('truetype');
}
</style>

<style>
	html { 
	background: url(/resources/share/img/datacenter/manager/images/bak_02_2.jpg) no-repeat center center fixed;
	-webkit-background-size: cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;}

	.vent_remote_box {width:250px; height:32px; padding: 4px 0px;}
	.vent_remote_box_off {width:250px; height:32px; padding: 4px 0px;}
	.line_b_gray {border-bottom:1px solid #919191;}

	/* auto control button */
	.btn_vent {width:68px; height:30px; float:left; cursor:pointer; margin-left:10px;}
	.vent_remote_box .btn_vent.on {background:url("/resources/share/img/datacenter/manager/images/vent_on.png");}
	.vent_remote_box .btn_vent.off {background:url("/resources/share/img/datacenter/manager/images/vent_off.png");}
	.vent_remote_box_off .btn_vent.on {background:url("/resources/share/img/datacenter/manager/images/vent_off.png");}
	.vent_remote_box_off .btn_vent.off {background:url("/resources/share/img/datacenter/manager/images/vent_off.png");}

	.vent_wind_box {width:75px; height:30px; background-color:#3f3e3e; margin-left:15px; float:left; border-radius:5px;}
	.btn_vent_left {width:10px; height:30px; float:left; cursor:pointer; margin-left:5px;}
	.btn_vent_right {width:10px; height:30px; float:left; cursor:pointer;}
	.vent_remote_box		.btn_vent_left{background:url("/resources/share/img/datacenter/manager/images/vent_left_on.png") no-repeat; background-position:50% 50%;}
	.vent_remote_box_off	.btn_vent_left{background:url("/resources/share/img/datacenter/manager/images/vent_left_off.png") no-repeat; background-position:50% 50%;}
	.vent_remote_box		.btn_vent_right{background:url("/resources/share/img/datacenter/manager/images/vent_right_on.png") no-repeat; background-position:50% 50%;}
	.vent_remote_box_off	.btn_vent_right{background:url("/resources/share/img/datacenter/manager/images/vent_right_off.png") no-repeat; background-position:50% 50%;}

	.databox_vent {width:45px; height:20px; float:left; margin-top:-2px; font-size:20px; padding-top:5px; text-align:center;}
	.vent_remote_box_off .databox_vent {color:#919191;}

	.btn_vent_ok {font-size:18px; padding:5px 10px; float:left; margin-left:15px; cursor:pointer; border-radius:5px;}
	.vent_remote_box .btn_vent_ok {background-color:#00aeef;}
	.vent_remote_box_off .btn_vent_ok {background-color:#919191;}
	
	.top_btn{ width:100px;height:25px;background-color:#32AEE5; color:white; border-radius:3px; font-size:14px; font-weight:normal; border:0; }
</style>
<div class="content">
	<ul class="title" style="margin:0">
		<ul style="float:right;">
			<li class="tv_mode" id="id_area">
			</li>
			<li class="tv_mode" style="margin-right:0;">
				<input type="button" class="top_btn" value="전광판모드" onClick= "goDid();" style="cursor:pointer;"/>
			</li>
			<li class="tv_mode">
				<input type="button" class="top_btn" value="로그아웃" onClick="goLogOut();" style="cursor:pointer;"/>
			</li>
		</ul>
		
		<ul style=" float:left; width:340px; margin-top:5px; margin-left:10px;">
			<li>* 업데이트 :  </li>
			<li id="update_time"><li>
		</ul>
		<ul style="float:left; margin-left: 244px;">
			<li><img src="/resources/share/img/datacenter/manager/logo_basic.png" alt="airguardk" style="width:200px;height:29px;"/></li>
		</ul>
	</ul>
	
	<table class="web_table" id="web_table" border="0" cellpadding="0" cellspacing="0" style="margin-top: 0; border-top-right-radius: 0; border-top-left-radius: 0; height:auto;">
		<colgroup>
			<col width="12%">
			<col width="14%">
			<col width="7%">
			<col width="7%">
			<col width="7%">
			<col width="7%">
			<col width="7%">
			<col width="7%">
			<col width="7%">
			<col width="13%">			
			<col width="5%">			
		</colgroup>
		<tr height="40">
			<th>스테이션명</th>
			<th>디바이스타입</th>
			<th>날짜시각</th>
			<th>미세먼지<br/>(㎍/㎥)</th>
			<th>초미세먼지<br/>(㎍/㎥)</th>
			<th>CO2<br/>(ppm)</th>
			<th>VOCS<br/>(ppb)</th>
			<th>소음<br/>(dB)</th>
			<th>온도<br/>(℃)</th>
			<th>습도<br/>(%)</th>
			<th>통합쾌적지수<br />(실내/실외)</th>
			<th>환기청정기</th>
		</tr>
	</table>
<script>
function vent_box_on(serial){
	var vid = serial + "_remote_box";
	var target = document.getElementById(vid);
	try{
		target.className = target.className.replace("_off", "");
	} catch(e){}
}

//	자동모드 변경
function vent_auto(serial){

	var vs = serial.replace(/_|\-/g, "");
	vent_box_on(vs);

	var vid = vs + "_auto";
	var target = document.getElementById(vid);
	try{
		var exp = target.className.split(" ");

		if(exp[1] == "on") var sw = "off";
		else var sw = "on";

		target.className = exp[0] + " " + sw;

		document.getElementById(vs + "_mode").value = "A";
	} catch(e){}
}

//	풍량 변경
function vent_wind(serial, key){

	var vs = serial.replace(/_|\-/g, "");
	vent_box_on(vs);

	var vid = vs + "_data_box";
	var vid2 = vs + "_wind";

	var target = document.getElementById(vid);
	var target2 = document.getElementById(vid2);

	try{
		var ws = parseInt(target2.value);
		var vpower = document.getElementById(vs + "_power").value;

		if(key == "l") ws -= 1;
		else if(key == "r") ws += 1;

		if(ws < 0) ws = 6;
		else if(ws > 6) ws = 0;

		if(ws == 0){
			if(vpower == 0) var ws_txt = "on";
			else var ws_txt = "off";
			document.getElementById(vs + "_mode").value = "P";
		}
		else{
			ws_txt = ws;
			document.getElementById(vs + "_mode").value = "W";
		}


		target.innerHTML = ws_txt;
		target2.value = ws;
	} catch(e){}
}

function get_vent_val(serial){
	//	(mode, value)
	//	기본은 OFF명령
	var rtn = new Array("P", "0");

	try{
		var vs = serial.replace(/_|\-/g, "");

		//설정정보
		var vpower = document.getElementById(vs + "_power").value;
		var vmode = document.getElementById(vs + "_mode").value;
		var vwind = document.getElementById(vs + "_wind").value;

		//자동여부는 class명으로 확인
		var vauto = String(document.getElementById(vs + "_auto").className).split(" ");

		rtn[0] = vmode;
		//파워 off (숫자) 이면 전원부터.
		if(vpower == "0"){
			rtn[0] = "P";
			rtn[1] = "1";
		}
		else{
			if(vmode == "A"){
				rtn[1] = (vauto[1] == "on") ? "1" : "0";
			}
			else if(vmode == "W"){
				rtn[1] = String(parseInt(vwind));

				//풍량 0이면 전원 off
				if(rtn[1] == "0"){
					rtn[0] = "P";
					rtn[1] = "0";
				}
			}
			else if(vmode == "P"){}
			else{
				rtn[0] = "";
			}
		}
	}
	catch(e){}

	return rtn;

}

function vent_send(serial){

	var vs = serial.replace(/_|\-/g, "");

	try{
		var remote = get_vent_val(serial);

		if(remote[0] == ""){
			alert("환기청정기 설정값 변경 후 시도해주세요.");
			return 0;
		}
		//vdate = vent_cookie_get(serial);

		var st_type = "";
		if(remote[0] == "P") st_type = "전원";
		else if(remote[0] == "A") st_type = "자동모드";
		else if(remote[0] == "W") st_type = "풍향설정";

		if(confirm(serial + " 기기의 " + st_type + " 상태를 변경하시겠습니까?")){

			jQuery.ajax({
				type: "POST", 
				dataType: "text",
				url: "ajax_vent_remote.php",
				data: {
					serial: serial,
					mode: remote[0],
					val: remote[1]
				},
				success: rtn_vent_ok
			});
		}
	} catch(e){}
}

var vent_cookie_get = function(name) {
	var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	return value? value[2] : null;
}
var vent_cookie_set = function(name, val){
	var ck = document.cookie;
	alert(ck);
}

function get_now_time(){

	var ntime = new Date();

	var nCookTime =	String(ntime.getFullYear()) + 
					String( add_zero(ntime.getMonth() + 1) ) + 
					String( add_zero(ntime.getDate()) ) + 
					String( add_zero(ntime.getHours()) ) + 
					String( add_zero(ntime.getMinutes()) ) + 
					String( add_zero(ntime.getSeconds()) );
	return nCookTime;
}

function get_cookie_time(){

	var ntime = new Date();
	ntime.setMinutes(ntime.getMinutes() + 1);

	var nCookTime =	String(ntime.getFullYear()) + 
					String( add_zero(ntime.getMonth() + 1) ) + 
					String( add_zero(ntime.getDate()) ) + 
					String( add_zero(ntime.getHours()) ) + 
					String( add_zero(ntime.getMinutes()) ) + 
					String( add_zero(ntime.getSeconds()) );
	return nCookTime;
}

function rtn_vent_ok(data){
	var exp = String(data).split("#");
	if(exp[0] == "1"){
		alert("설정 변경 명령을 보냈습니다. 재조작은 1분 뒤 가능합니다.");
	}
	else{
		if(exp[0] == "fail") alert("기기 시리얼 번호가 잘못되었습니다.");
		else if(exp[1] == "delay") alert("해당 기기의 상태를 변경한지 1분이 지나지 않았습니다.");
		else if(exp[1] == "mode") alert("설정은 전원, 자동모드, 풍량 변경만 가능합니다.");
		
	}
}

function add_zero(num){
	if(num == undefined) return "00";
	else if(num == null) return "00";
	else if(parseInt(num) < 10) return "0" + parseInt(num);
	else return num;
}
</script>
	
</div>
	
<form id="detail_info_form" method="post">
	<input type="hidden" name="serial" />
	<input type="hidden" name="email" value="test2@kweather.co.kr" />
	<input type="hidden" name="nowAll" />
	<input type="hidden" name="param" />
	<input type="hidden" name="member_level" value="0" />
	<input type="hidden" name="member_type" value="2" />
	<input type="hidden" name="parentSpaceName" />
	<input type="hidden" name="spaceName" />
	<input type="hidden" name="productDt" />
	<input type="hidden" name="stationName" />
	<input type="hidden" name="standard" />
	<input type="hidden" name="deviceType" />
		
</form>
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/datacenter_footer.jsp"%>
<!-- ##### Footer Area End ##### -->

<script type="text/javascript">
	window.onload=function(){
		//setInterval(reload, 60000);
	}

	function reload(){
		location.href="?view_type=";
	}

	//영업장명 클릭 시 데이터 분석창 팝업
	$(document).on("click", "td[name='location']", function(){
		var serial = $(this).attr("data-serial");
		var serialAll = $(this).attr("data-serial-all");
		var parentSpaceName = $(this).attr("data-parentSpaceName");
		var spaceName = $(this).attr("data-spaceName");
		var productDt = $(this).attr("data-productDt");
		var stationName = $(this).attr("data-stationName");
		var standard = $(this).attr("data-standard");
		var deviceType = $(this).attr("data-deviceType");
		var now = new Date();
		var nowAll = now.yyyymmddhhii();

		var param = serial+"|"+nowAll;

		var winWidth = 400; 
		var winheight = 400; 

		if (screen){ // weeds out older browsers who do not understand screen.width/screen.height
			winWidth = screen.width;
			winHeight = screen.height;
		}

		newWindow = window.open('','airguardk_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='+winWidth+',height='+winHeight+',left=0,top=0');

		
		//$s_ip =	explode(".", $_SERVER["REMOTE_ADDR"]);
		//if(substr($_SERVER["REMOTE_ADDR"], 0, 11) == "211.219.114" &&   ($s_ip[3] >= 100 && $s_ip[3] <= 120) ){	
		
		
		//이게 유료인가봄
		//var act_url =	"http://manage.airguardk.com/admin/Popup_detail_analysis.html";
		//var act_url = "http://manage.airguardk.com/admin/Popup_detail_payment.html";
		var act_url = "/datacenter/popup";
		
		var target =	document.getElementById("detail_info_form");
		target.target =	"airguardk_detail_pop";
		target.action =	act_url;
		target.serial.value =	serialAll;
		target.nowAll.value =	nowAll;
		target.param.value =	param;
		target.parentSpaceName.value = parentSpaceName;
		target.spaceName.value = spaceName;
		target.productDt.value = productDt;
		target.stationName.value = stationName;
		target.standard.value = standard;
		target.deviceType.value = deviceType;
		target.submit();

		newWindow.focus();

	});
	
	Date.prototype.yyyymmddhhii = function(){
		var yyyy = this.getFullYear().toString();
		var mm = (this.getMonth() + 1).toString();
		var dd = this.getDate().toString();
		var hh = this.getHours().toString();
		var ii = this.getMinutes().toString();

		return yyyy + (mm[1] ? mm : '0'+mm[0]) + (dd[1] ? dd : '0'+dd[0]) + (hh[1] ? hh : '0'+hh[0]) + (ii[1] ? ii : '0'+ii[0]);

	}

</script>

