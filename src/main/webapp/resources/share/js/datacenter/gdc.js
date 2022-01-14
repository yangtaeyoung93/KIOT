$().ready(function() {
	$("#id_area").hide();
	$("#id_area").text("");

	var getCookie = function(name) {
	    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return value? value[2] : null;
	};

	if (getCookie("GROUP_AUTH")) {
		$("#id_area").show();
	} else {
		$("#id_area").hide();
		$("#id_area").text("");
	}

	setInterval(getData, 60000);

	function reload(){
		location.href="/datacenter/list/";
	}

	getData();
})

function getData(){
	var listHtml = "";
	var serialArr = new Array();

	$.ajax({
		 url:"/api/datacenter/list",
		 type:"GET",
		 async : false,
		 success : function (param) {
			console.log(param);
			$("#id_area").text(param.groupId);
			listHtml = "<tr><tr height='40'><th>스테이션명</th><th>디바이스타입</th><th>날짜시각</th><th>미세먼지<br/>(㎍/㎥)</th><th>초미세먼지<br/>(㎍/㎥)</th><th>CO2<br/>(ppm)</th>	<th>VOCS<br/>(ppb)</th><th>소음<br/>(dB)</th>	<th>온도<br/>(℃)</th><th>습도<br/>(%)</th><th>통합쾌적지수<br />(실내/실외)</th><th>환기청정기</th></tr>";
			for(var i = 0; i < param.data.length; i++){
				param.data.sort(function(a, b) { // 내림차순
					if(a['deviceType'] === b['deviceType']) {
						var x = a['stationName'].toLowerCase();
						var y = b['stationName'].toLowerCase();
						return x < y ? -1 : x > y ? 1 : 0;
					}

					return b['deviceType'] - a['deviceType'];
				});

				var serial = param.data[i].serial;
				var deviceType = param.data[i].deviceType;
				var receiveFlag = param.data[i].receiveFlag;
				var tmDate = new Date(param.data[i].timestamp*1000);
				var tmYear = tmDate.getFullYear();
				var tmMonth = tmDate.getMonth()+1;
				var tmDay = tmDate.getDate();
				var tmHours = tmDate.getHours();
				var tmMinutes = tmDate.getMinutes();
				var tmSeconds = tmDate.getSeconds();

				if(tmMonth < 10) tmMonth = '0' + tmMonth;
				if(tmDay < 10) tmDay = '0' + tmDay;
				if(tmHours < 10) tmHours = '0' + tmHours;
				if(tmMinutes < 10) tmMinutes = '0' + tmMinutes;
				if(tmSeconds < 10) tmSeconds = '0' + tmSeconds;

				var tm = String(tmYear) + String(tmMonth) + String(tmDay) + String(tmHours) + String(tmMinutes) + String(tmSeconds);

				var pm10 = (param.data[i].sensor == null || param.data[i].sensor.pm10 == null) ? "" : parseInt(param.data[i].sensor.pm10).toFixed(0);
				var pm25 = (param.data[i].sensor == null || param.data[i].sensor.pm25 == null) ? "" : parseInt(param.data[i].sensor.pm25).toFixed(0);
				var temp = (param.data[i].sensor == null || param.data[i].sensor.temp == null) ? "" : parseInt(param.data[i].sensor.temp).toFixed(1);
				var humi = (param.data[i].sensor == null || param.data[i].sensor.humi == null) ? "" : parseInt(param.data[i].sensor.humi).toFixed(0);
				var co2 = (param.data[i].sensor == null || param.data[i].sensor.co2 == null) ? "" : parseInt(param.data[i].sensor.co2).toFixed(0);
				var voc = (param.data[i].sensor == null || param.data[i].sensor.voc == null) ? "" :parseInt(param.data[i].sensor.voc).toFixed(0); 
				var noise = (param.data[i].sensor == null || param.data[i].sensor.noise == null) ? "" : parseInt(param.data[i].sensor.noise).toFixed(0);
				var cici = (param.data[i].sensor == null || param.data[i].sensor.cici == null) ? "" : parseInt(param.data[i].sensor.cici).toFixed(0);
				var coci = (param.data[i].sensor == null || param.data[i].sensor.coci == null) ? "" : parseInt(param.data[i].sensor.coci).toFixed(0);
				var stationName = (param.data[i] == null || param.data[i].stationName == null) ? "" : param.data[i].stationName;
				var spaceName = (param.data[i] == null || param.data[i].spaceName == null) ? "" : param.data[i].spaceName;
				var parentSpaceName = (param.data[i] == null || param.data[i].parentSpaceName == null) ? "" : param.data[i].parentSpaceName;
				var productDt = param.data[i].productDt;
				var serial_slice = serial.slice(-5);

				var cici_pm10 = (param.data[i].sensor == null || param.data[i].sensor.cici_pm10 == null) ? "" : parseInt(param.data[i].sensor.cici_pm10).toFixed(0);
				var cici_pm25 = (param.data[i].sensor == null || param.data[i].sensor.cici_pm25 == null) ? "" : parseInt(param.data[i].sensor.cici_pm25).toFixed(0);
				var cici_co2 = (param.data[i].sensor == null || param.data[i].sensor.cici_co2 == null) ? "" : parseInt(param.data[i].sensor.cici_co2).toFixed(0);
				var cici_voc = (param.data[i].sensor == null || param.data[i].sensor.cici_voc == null) ? "" : parseInt(param.data[i].sensor.cici_voc).toFixed(0);
				var cici_temp = (param.data[i].sensor == null || param.data[i].sensor.cici_temp == null) ? "" : parseInt(param.data[i].sensor.cici_temp).toFixed(0);
				var cici_humi = (param.data[i].sensor == null || param.data[i].sensor.cici_humi == null) ? "" : parseInt(param.data[i].sensor.cici_humi).toFixed(0);
				var cici_noise = (param.data[i].sensor == null || param.data[i].sensor.cici_noise == null) ? "" : parseInt(param.data[i].sensor.cici_noise).toFixed(0);

				var coci_pm10 = (param.data[i].sensor == null || param.data[i].sensor.coci_pm10 == null) ? "" : parseInt(param.data[i].sensor.coci_pm10).toFixed(0);
				var coci_pm25 = (param.data[i].sensor == null || param.data[i].sensor.coci_pm25 == null) ? "" : parseInt(param.data[i].sensor.coci_pm25).toFixed(0);
				var coci_humi = (param.data[i].sensor == null || param.data[i].sensor.coci_humi == null) ? "" : parseInt(param.data[i].sensor.coci_humi).toFixed(0);
				var coci_temp = (param.data[i].sensor == null || param.data[i].sensor.coci_temp == null) ? "" : parseInt(param.data[i].sensor.coci_temp).toFixed(0);

				var year = tm.substring(0,4);
				var month = tm.substring(4,6);
				var day = tm.substring(6,8);
				var hour = tm.substring(8,10);
				var min = tm.substring(10,12);

				var createDt = year+"-"+month+"-"+day+" "+hour+":"+min
				var serial_substr = serial.substring(0,4);
				var tdColor = "";
				var tdColor2 = "";

				if (!stationName) {
					stationName = "스테이션명없음";
				}

				if(!co2){ co2 = ""; } else { 631-co2}
				if(!voc){ voc = ""; } else { 302-voc}
				if(!noise){ noise = ""; } else { 50-noise}

				if (receiveFlag == false) {
					tdColor = "#929498";
				} else {
					tdColor = "white";
				}

				listHtml += "<tr>";
				listHtml += "<td name='location' id='location_"+serial+"' data-serial='"+serial_slice+"' data-serial-all='"+serial+"' ";
				listHtml += " data-parentSpaceName='"+parentSpaceName+"' data-spaceName='"+spaceName+"' data-deviceType='"+deviceType+"' ";
				listHtml += " data-productDt='"+productDt+"' data-standard='sum' data-stationName='"+stationName+"' >";
				listHtml += "<span style='cursor:pointer; font-size:20px; font-weight:600;  color:"+tdColor+";'>"+stationName+"</span></td>";
				listHtml += "<td style='color:"+tdColor+"'>"+deviceType+"</td>";
				listHtml += "<td style='color:"+tdColor+"'>"+createDt+"</td>";

				if (deviceType == "IAQ") {
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_pm10,receiveFlag)+";'>"+pm10+"</td>"; //PM10
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_pm25,receiveFlag)+";'>"+pm25+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_co2,receiveFlag)+";'>"+co2+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_voc,receiveFlag)+";'>"+voc+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_noise,receiveFlag)+";'>"+noise+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_temp,receiveFlag)+";'>"+temp+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(cici_humi,receiveFlag)+";'>"+humi+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(cici,receiveFlag)+"'>"+cici+"</td>";

				} else {
					listHtml += "<td class='fbig' style='color:"+txtColor(coci_pm10,receiveFlag)+";'>"+pm10+"</td>"; //PM10
					listHtml += "<td class='fbig' style='color:"+txtColor(coci_pm25,receiveFlag)+";'>"+pm25+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtCO2Color(co2, receiveFlag)+"'>"+co2+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtVOCColor(voc, receiveFlag)+"'>"+voc+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtNOISEColor(noise, receiveFlag)+"'>"+noise+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(coci_temp,receiveFlag)+";'>"+temp+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(coci_humi,receiveFlag)+";'>"+humi+"</td>";
					listHtml += "<td class='fbig' style='color:"+txtColor(coci,receiveFlag)+"'>"+coci+"</td>";
				}

				if (deviceType == "IAQ" && param.data[i].sensor != null) {
					listHtml += "<td class='fbig' ><input type='button' class='top_btn vent_btn' id='vent_btn_" + serial 
					+ "' value='환기청정기' onClick='openVentPop(\"" + serial + "\");' style='cursor:pointer;'/></td>";
					serialArr[i] = serial;
				}

				listHtml += "</tr>";
			}

			listHtml += "<tr><td height='100%'></td></tr>";
			$("#web_table").html(listHtml);
			getVentData(serialArr);
		 }
	})

	var s_today = new Date();   
	var s_year = s_today.getFullYear();
	var s_month = s_today.getMonth()+1;
	var s_day = s_today.getDate();
	var s_hour = s_today.getHours();
	var s_minute = s_today.getMinutes();
	var s_hour_str ="";
	var s_updateDt = "";

	if (s_hour < 10) {
		s_hour = "0"+s_hour;
	}

	if (s_minute < 10) {
		s_minute = "0"+s_minute;
	}

	if (s_month < 10) {
		s_month = "0"+s_month;
	}

	if (s_day < 10) {
		s_day = "0"+s_day;
	}

	if (s_hour < 12) {
		s_hour_str = "오전";
	}else{
		s_hour_str = "오후";
	}

	s_updateDt = s_year + "-" + s_month + "-" + s_day + " " + s_hour_str + " " + s_hour + ":" + s_minute;

	$("#update_time").text(s_updateDt);
}

function openVentPop(serial) {
	var serial = $("#location_"+serial).attr("data-serial-all");
	var stationName =$("#location_"+serial).attr("data-stationName");

	winWidth = 1000;
	winHeight = 600;

	window.open('','vent_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width=' + winWidth + ',height=' + winHeight + ',left=0,top=0');

	var act_url = "/datacenter/vent";
	var target =	document.getElementById("detail_info_form");
	target.target =	"vent_pop";
	target.action =	act_url;
	target.serial.value =	serial;
	target.stationName.value = stationName;
	target.submit();
}

function getVentData(serial) {
	var r_serial = "";

	$.ajax({
		 url:"/api/datacenter/vent",
		 type:"GET",
		 async : false,
		 success : function (param) {
			 console.log(param.data.length);
			 console.log(param.data);
			 console.log(serial);
			 for (var j = 0; j < serial.length; j++) {
				 if (param.data.length != 0) {
					 for (var i = 0; i < param.data.length; i++) {
						 r_serial = param.data[i].iaqSerial;
						 $(".vent_btn").show();
					 }
				 }
			}
		 }
	})
}

function goDid() {
	location.href="/datacenter/did";
}

function goLogOut() {
	location.href="/datacenter/logout";
}

function txtColor(val, receiveFlag) {
	var colorCode = "";

	if (receiveFlag == false) {
		colorCode = "#929498";

	} else {
		if (val >= 0 && val <= 49) {
			colorCode = "#ff0000";

		} else if (val >= 50 && val <= 79) {
			colorCode = "#ff6700";

		} else if (val >= 80 && val <= 89) {
			colorCode = "#8dd638";

		} else if (val >= 90 && val <= 100) {
			colorCode = "#44c7f4";

		} else {
			colorCode = "#929498";
		}
	}
	return colorCode;
}

function txtCO2Color(val, receiveFlag) {
	var colorCode = "";
	
	if(receiveFlag == false){
		colorCode = "#929498";

	} else {
		if (val >= 1501 && val <= 10000) {
			colorCode = "#ff0000";

		} else if (val >= 1001 && val <= 1500) {
			colorCode = "#ff6700";

		} else if (val >= 501 && val <= 1000) {
			colorCode = "#8dd638";

		} else if (val >= 0 && val <= 500) {
			colorCode = "#44c7f4";

		} else {
			colorCode = "#929498";
		}
	}

	return colorCode;
}

function txtVOCColor(val , receiveFlag) {
	var colorCode = "";

	if (receiveFlag == false) {
		colorCode = "#929498";

	} else {
		if(val >= 1001 && val <= 10000) {
			colorCode = "#ff0000";

		} else if (val >= 1001 && val <= 1500) {
			colorCode = "#ff6700";

		} else if (val >= 500 && val <= 1000) {
			colorCode = "#8dd638";

		} else if (val >= 0 && val <= 500) {
			colorCode = "#44c7f4";

		} else {
			colorCode = "#929498";
		}
	}

	return colorCode;
}

function txtNOISEColor(val, receiveFlag) {
	var colorCode = "";

	if (val >= 71 && val <= 100) {
		colorCode = "#ff0000";

	} else if (val >= 56 && val <= 70) {
		colorCode = "#ff6700";

	} else if (val >= 31 && val <= 55) {
		colorCode = "#8dd638";

	} else if (val >= 0 && val <= 30) {
		colorCode = "#44c7f4";

	} else {
		colorCode = "#929498";
	}

	return colorCode;
}
