
function view_detail(){
	var winWidth = 400; 
	var winheight = 400; 
	var serial = $("#serial").val();
	var parentSpaceName = $("#parentSpaceName").val();
	var spaceName = $("#spaceName").val();
	var productDt = $("#productDt").val();
	var stationName = $("#stationName").val();
	var standard = "sum";
	var deviceType = $("#deviceType").val();
	
	if(!serial){
		return false;
	}
	
	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}
	
	newWindow = window.open('','airguardk_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='+winWidth+',height='+winHeight+',left=0,top=0');
		
	var target =	document.getElementById("detail_info_form");
	var act_url = "/datacenter/popup";
	
	var param = target.serial.value.substr(target.serial.value.length-5, 5)+"|"+target.nowAll.value;
	target.target =	"airguardk_detail_pop";
	target.action =	act_url;
	target.param.value =	param;
	target.serial.value = serial;
	target.parentSpaceName.value = parentSpaceName;
	target.spaceName.value = spaceName;
	target.productDt.value = productDt;
	target.stationName.value = stationName;
	target.standard.value = standard;
	target.deviceType.value = deviceType;
	
	target.submit();

	newWindow.focus();
}

/*window.onload=function() {
	setInterval(reload, 3000);
}
*/
function reload() {
	var page = parseInt($("#page").val()) + 1;
	var cnt = parseInt($("#cnt").val());
	if(page >= cnt){
		page = 0;
	}
}

Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
	var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
	var d = this;
	return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
			case "YY": return d.getFullYear();
			case "yy": return (d.getFullYear() % 1000).zf(2);
			case "MM": return (d.getMonth() + 1).zf(2);
			case "dd": return d.getDate().zf(2);
			case "E": return weekName[d.getDay()];
			case "HH": return d.getHours().zf(2);
			case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
			case "mm": return d.getMinutes().zf(2);
			case "ss": return d.getSeconds().zf(2);
			case "a/p": return d.getHours() < 12 ? "오전" : "오후";
			default: return $1;
		}
	});
};

String.prototype.string = function(len) {
	var s = '', i = 0; while (i++ < len) { 
		s += this;
	} return s;
};

String.prototype.zf = function(len) {
	return "0".string(len - this.length) + this;
};

Number.prototype.zf = function(len) {
	return this.toString().zf(len);
};

Number.prototype.format = function(){
	if(this==0) return 0;
 
	var reg = /(^[+-]?\d+)(\d{3})/;
	var n = (this + '');
 
	while (reg.test(n)) n = n.replace(reg, '$1' + ',' + '$2');
 
	return n;
};

String.prototype.format = function(){
	var num = parseFloat(this);
	if( isNaN(num) ) return "0";
	return num.format();
};


var arrayIdx = 0;
var sensorDatas;
function getSensorData() {
	$.ajax({
		method:"GET",
		url: "/api/datacenter/list",
		contentType : "application/json; charset=utf-8",
		success : function(d) {

			var getCookie = function(name) {
			    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
			    return value? value[2] : null;
			};

			if (getCookie("_USER_AUTH") == "group") {
				$("#id_area").text(d.groupId);
			} else if (getCookie("_USER_AUTH") == "member") {
				$("#id_area").text(d.memberId);
			}

			sensorDatas = d.data;
			console.log(sensorDatas.length);
			drawSensorData();
			setInterval(drawSensorData, 10000);
			setInterval(dataReload, 60000);
		}
	});
}

function dataReload() {
	$.ajax({
		method:"GET",
		url: "/api/datacenter/list",
		contentType : "application/json; charset=utf-8",
		success : function(d){
			sensorDatas = d.data;
		}
	});
}

function drawSensorData() {
	if (sensorDatas.length <= arrayIdx) arrayIdx = 0;
	
	$("#serial").val(sensorDatas[arrayIdx].serial);
	$("#email").val(sensorDatas[arrayIdx].userId);
	$("#parentSpaceName").val(sensorDatas[arrayIdx].parentSpaceName);
	$("#spaceName").val(sensorDatas[arrayIdx].spaceName);
	$("#productDt").val(sensorDatas[arrayIdx].productDt);
	$("#stationName").val(sensorDatas[arrayIdx].stationName);
	$("#deviceType").val(sensorDatas[arrayIdx].deviceType);

	var SpaceName = sensorDatas[arrayIdx].spaceName;
	var stationName = sensorDatas[arrayIdx].stationName;

	if(!SpaceName){
		SpaceName = "없음";
	}

	if(!stationName){
		stationName = "없음";
	}

	if(sensorDatas[arrayIdx].deviceType == "IAQ"){
		$("#locationSpan2").text(SpaceName);
		$("#stationNameSpan2").text(stationName);
		$("#serialNumSpan2").text(sensorDatas[arrayIdx].serial);
		
	}else{
		$("#locationSpan").text(SpaceName);
		$("#stationNameSpan").text(stationName);
		$("#serialNumSpan").text(sensorDatas[arrayIdx].serial);
		
	}
	
	var time = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.tm == null) ? "000000000000" : sensorDatas[arrayIdx].sensor.tm;
	var y = time.substring(0,4);
	var m = time.substring(4,6) -1;
	var d = time.substring(6,8);
	var h = time.substring(8,10);
	var i = time.substring(10,12);
	var update = "";

	update = new Date(y, m, d, h, i).format("YY.MM.dd a/p hh:mm");

	$("#update_time").text(update);
	$("#update_time2").text("*기준 : " + update + "  *제공 : 케이웨더");

	var cici = "";
	var temp = "";
	var humi = "";
	var pm10 = "";
	var pm25 = "";
	var vocs = "";
	var co2 = "";
	var noise = "";
	var coci = "";

	var cici_pm10  =  "";
	var cici_pm25  =  "";
	var cici_co2  =   "";
	var cici_voc  =   "";
	var cici_temp  =  "";
	var cici_humi  =  "";
	var cici_noise  = "";

	var coci_pm10 = "";
	var coci_pm25 = "";
	var coci_humi = "";
	var coci_temp = "";

	var ciciTxt = ""; //실내통합쾌적지수 관련 txt
	var ciciTxt2 = ""; //요소 txt

	$("#49").text("");
	$("#79").text("");
	$("#89").text("");
	$("#100").text("");

	$("#behavior").html("");

	if (sensorDatas[arrayIdx].deviceType == "IAQ") {
		$('#oaqDiv').hide();
		$('#iaqDiv').show();

		cici = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici == null) ? "" : sensorDatas[arrayIdx].sensor.cici;
		temp = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.temp == null) ? "" : sensorDatas[arrayIdx].sensor.temp;
		humi = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.humi == null) ? "" : sensorDatas[arrayIdx].sensor.humi;
		pm10 = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.pm10 == null) ? "" : sensorDatas[arrayIdx].sensor.pm10;
		pm25 = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.pm25 == null) ? "" : sensorDatas[arrayIdx].sensor.pm25;
		voc = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.voc == null) ? "" : sensorDatas[arrayIdx].sensor.voc;
		co2 = 			(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.co2 == null) ? "" : sensorDatas[arrayIdx].sensor.co2;
		noise = 		(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.noise == null) ? "" : sensorDatas[arrayIdx].sensor.noise;

		cici_pm10  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_pm10 == null) ? "" :sensorDatas[arrayIdx].sensor.cici_pm10;
		cici_pm25  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_pm25 == null) ? "" : sensorDatas[arrayIdx].sensor.cici_pm25;
		cici_co2  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_co2 == null) ? "" : sensorDatas[arrayIdx].sensor.cici_co2;
		cici_voc  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_voc == null) ? "" : sensorDatas[arrayIdx].sensor.cici_voc;
		cici_temp  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_temp == null) ? "" : sensorDatas[arrayIdx].sensor.cici_temp;
		cici_humi  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_humi == null) ? "" : sensorDatas[arrayIdx].sensor.cici_humi;
		cici_noise  = 	(sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_noise == null) ? "" : sensorDatas[arrayIdx].sensor.cici_noise;

		if(!pm10){ cici_pm10 = ""; }
		if(!pm25){ cici_pm25 = ""; }
		if(!co2){ cici_co2 = ""; }
		if(!voc){ cici_voc = ""; }
		if(!temp){ cici_temp = ""; }
		if(!humi){ cici_humi = ""; }
		if(!noise){ cici_noise = ""; }

		if(cici_pm10 != ""){
			$("#temp_title2").text(temp +"℃");
			$("#temp_index2").text(jisuTxt(cici_temp));
			$("#temp_index_val2").text(cici_temp);
			$("#temp_index2").css("color",txtColor(cici_temp));
			$("#temp_title2").css("color",txtColor(cici_temp));
			$("#temp_index_val2").css("color",txtColor(cici_temp));
			$("#temp_2_total").text("/100");
			AmCharts.makeChart("temp_2", MakeObj("temp",cici_temp,txtColor(cici_temp)));
		}else{
			$("#temp_2_total").text("");
		}
		
		if(cici_humi != ""){
			$("#humi_title2").text(humi +"%");
			$("#humi_index2").text(jisuTxt(cici_humi));
			$("#humi_index_val2").text(cici_humi);
			$("#humi_index2").css("color",txtColor(cici_humi));
			$("#humi_title2").css("color",txtColor(cici_humi));
			$("#humi_index_val2").css("color",txtColor(cici_humi));
			$("#humi_2_total").text("/100");
			AmCharts.makeChart("hum_2", MakeObj("humi",cici_humi,txtColor(cici_humi)));
		}else{
			$("#humi_2_total").text("");
		}
		
		if(cici_pm10 != ""){
			$("#pmat_title2").text(pm10 + "㎍/㎥");
			$("#pmat_index2").text(jisuTxt(cici_pm10));
			$("#pmat_index_val2").text(cici_pm10);
			$("#pmat_index2").css("color",txtColor(cici_pm10));
			$("#pmat_title2").css("color",txtColor(cici_pm10));
			$("#pmat_index_val2").css("color",txtColor(cici_pm10));
			$("#pmat_2_total").text("/100");
			AmCharts.makeChart("pmat_2", MakeObj("pm10",cici_pm10,txtColor(cici_pm10)));
		}else{
			$("#pmat_2_total").text("");
		}
		
		if(cici_voc != ""){
			$("#vocs_title2").text(voc + "ppb");
			$("#vocs_index2").text(jisuTxt(cici_voc));
			$("#vocs_index_val2").text(cici_voc);
			$("#vocs_index2").css("color",txtColor(cici_voc));
			$("#vocs_title2").css("color",txtColor(cici_voc));
			$("#vocs_index_val2").css("color",txtColor(cici_voc));
			$("#vocs_2_total").text("/100");
			AmCharts.makeChart("vocs_2", MakeObj("voc",cici_voc,txtColor(cici_voc)));
		}else{
			$("#vocs_2_total").text("");
		}
		
		if(cici_co2 != ""){
			$("#co2_title2").text(co2 + "ppm");
			$("#co2_index2").text(jisuTxt(cici_co2));
			$("#co2_index_val2").text(cici_co2);
			$("#co2_index2").css("color",txtColor(cici_co2));
			$("#co2_title2").css("color",txtColor(cici_co2));
			$("#co2_index_val2").css("color",txtColor(cici_co2));
			$("#co2_2_total").text("/100");
			AmCharts.makeChart("co2_2", MakeObj("co2",cici_co2,txtColor(cici_co2)));
		}else{
			$("#co2_2_total").text("");
		}
		
		if(cici_pm25 != ""){
			$("#pm25_title2").text(pm25 + "㎍/㎥");
			$("#pm25_index2").text(jisuTxt(cici_pm25));
			$("#pm25_index_val2").text(cici_pm25);
			$("#pm25_index2").css("color",txtColor(cici_pm25));
			$("#pm25_title2").css("color",txtColor(cici_pm25));
			$("#pm25_index_val2").css("color",txtColor(cici_pm25));
			$("#pm25_2_total").text("/100");
			AmCharts.makeChart("pm25_2", MakeObj("pm25",cici_pm25,txtColor(cici_pm25)));
		}else{
			$("#pm25_2_total").text("");
		}
		
		if(cici != ""){
			$("#cici_index2").text(jisuTxt(cici));
			$("#cici_index_val2").text(cici);
			$("#cici_index2").css("color",txtColor(cici));
			$("#cici_title2").css("color",txtColor(cici));
			$("#cici_index_val2").css("color",txtColor(cici));		
			$("#cici_2_total").text("/100");
			AmCharts.makeChart("cici_2", MakeObj("cici",cici,txtColor(cici)));
			
			//0~49 50~79 80~89 90~100
			//매우나쁨 나쁨 보통 좋음
			var ciciTxt2Arr = new Array(cici_temp,cici_humi,cici_pm10,cici_voc,cici_co2,cici_pm25);
			var ciciTxt2Arr2 = new Array("미세먼지","초미세먼지","이산화탄소","온도","습도","VOCs");
			var ciciTxtStr = new Array();
			var ciciTxtStr2 = "";
			var ciciAttrTxt = "";

			if(cici >= 0 && cici <= 49) {
				for (var z = 0; z < ciciTxt2Arr.length; z++){
					if(ciciTxt2Arr[z] >= 0 && ciciTxt2Arr[z] <= 49){
						ciciTxtStr.push(ciciTxt2Arr2[z]);
						if(ciciTxt2Arr2[z] == "온도"){
							ciciAttrTxt = "실내온도 조절이 즉시 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "습도"){
							ciciAttrTxt = "습도로 인해 건강이 위협받을 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요";
						}else if(ciciTxt2Arr2[z] == "미세먼지"){
							ciciAttrTxt = "실내 미세먼지 농도가 매우 높아 어린이, 노약자 및 호흡기 질환자의 건강에 유해한 수준입니다. 공기청정기를 작동시키거나 창문을 열어 환기시키고 실내 미세먼지 농도를 낮추기 위한 조치가 필요합니다.";
						}else if(ciciTxt2Arr2[z] == "VOCs"){
							ciciAttrTxt = "실내 VOCs 농도가 매우 높으니 창문을 열어 환기를 시키거나 공기청정기를 작동시켜 주세요. 이 단계가 장시간 지속되면 전문가에게 상담을 요청하세요.";
						}else if(ciciTxt2Arr2[z] == "이산화탄소"){
							ciciAttrTxt = "실내 CO2 농도가 매우 높아 업무효율과 학습능력이 떨어질 수 있으니 즉시 창문을 열어 환기시켜 지속적으로 관리해 주세요.";
						}else if(ciciTxt2Arr2[z] == "소음"){
							ciciAttrTxt = "소음공해가 심각합니다. 즉각적인 조치가 필요하니 소음원인을 제거하거나, 필요시 귀마개를 착용해 주세요.";
						}else if(ciciTxt2Arr2[z] == "초미세먼지"){
							//ciciAttrTxt += "";
							ciciAttrTxt = "";
						}
						
						ciciTxtStr2 += "["+ciciTxt2Arr2[z]+" - "+jisuTxt(ciciTxt2Arr[z])+"] "+ciciAttrTxt+"<br/>";
						
					}else if(ciciTxt2Arr[z] >= 50 && ciciTxt2Arr[z] <= 79){
						ciciTxtStr.push(ciciTxt2Arr2[z]);
						if(ciciTxt2Arr2[z] == "온도"){
							ciciAttrTxt = "실내온도 조절이 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "습도"){
							ciciAttrTxt = "습도로 인해 불쾌감을 느낄 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요.";
						}else if(ciciTxt2Arr2[z] == "미세먼지"){
							ciciAttrTxt = "실내 미세먼지 농도가 높아 어린이나 노약자는 불편함을 느낄 수 있습니다. 공기청정기를 작동시키거나 창문을 열어 환기하시기 바랍니다.";
						}else if(ciciTxt2Arr2[z] == "VOCs"){
							ciciAttrTxt = "실내 VOCs 농도가 높으니 창문을 열어 환기를 시키거나 공기청정기를 작동시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "이산화탄소"){
							ciciAttrTxt = "실내 CO2 농도가 높아 업무효율과 학습능력이 떨어질 수 있으니 창문을 열어 환기시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "소음"){
							ciciAttrTxt = "소음공해가 발생되고 있습니다. 소음원인을 확인하고 원인을 제거하기 바랍니다.";
						}else if(ciciTxt2Arr2[z] == "초미세먼지"){
							//ciciAttrTxt += "";
							ciciAttrTxt = "";
						}
						ciciTxtStr2 += "["+ciciTxt2Arr2[z]+" - "+jisuTxt(ciciTxt2Arr[z])+"] "+ciciAttrTxt+"<br/>";
					}
				}
				if(ciciTxtStr2 != ""){
					BRtag = "<br/>";
				}
				ciciTxt = ciciTxtStr+"의 지수가 낮아 누구나 불쾌감을 느낄 수 있습니다."+BRtag;
				ciciTxt += ciciTxtStr2;
				$("#behavior").html(ciciTxt);
			}else if(cici >= 50 && cici <= 79) {
				//요소명의
				for (var z = 0; z < ciciTxt2Arr.length; z++){
					if(ciciTxt2Arr[z] >= 0 && ciciTxt2Arr[z] <= 49){
						ciciTxtStr.push(ciciTxt2Arr2[z]);
						if(ciciTxt2Arr2[z] == "온도"){
							ciciAttrTxt = "실내온도 조절이 즉시 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "습도"){
							ciciAttrTxt = "습도로 인해 건강이 위협받을 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요";
						}else if(ciciTxt2Arr2[z] == "미세먼지"){
							ciciAttrTxt = "실내 미세먼지 농도가 매우 높아 어린이, 노약자 및 호흡기 질환자의 건강에 유해한 수준입니다. 공기청정기를 작동시키거나 창문을 열어 환기시키고 실내 미세먼지 농도를 낮추기 위한 조치가 필요합니다.";
						}else if(ciciTxt2Arr2[z] == "VOCs"){
							ciciAttrTxt = "실내 VOCs 농도가 매우 높으니 창문을 열어 환기를 시키거나 공기청정기를 작동시켜 주세요. 이 단계가 장시간 지속되면 전문가에게 상담을 요청하세요.";
						}else if(ciciTxt2Arr2[z] == "이산화탄소"){
							ciciAttrTxt = "실내 CO2 농도가 매우 높아 업무효율과 학습능력이 떨어질 수 있으니 즉시 창문을 열어 환기시켜 지속적으로 관리해 주세요.";
						}else if(ciciTxt2Arr2[z] == "소음"){
							ciciAttrTxt = "소음공해가 심각합니다. 즉각적인 조치가 필요하니 소음원인을 제거하거나, 필요시 귀마개를 착용해 주세요.";
						}else if(ciciTxt2Arr2[z] == "초미세먼지"){
							//ciciAttrTxt += "";
							ciciAttrTxt = "";
						}
						
						ciciTxtStr2 += "["+ciciTxt2Arr2[z]+" - "+jisuTxt(ciciTxt2Arr[z])+"] "+ciciAttrTxt+"<br/>";
						
					}else if(ciciTxt2Arr[z] >= 50 && ciciTxt2Arr[z] <= 79){
						ciciTxtStr.push(ciciTxt2Arr2[z]);
						
						if(ciciTxt2Arr2[z] == "온도"){
							ciciAttrTxt = "실내온도 조절이 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "습도"){
							ciciAttrTxt = "습도로 인해 불쾌감을 느낄 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요.";
						}else if(ciciTxt2Arr2[z] == "미세먼지"){
							ciciAttrTxt = "실내 미세먼지 농도가 높아 어린이나 노약자는 불편함을 느낄 수 있습니다. 공기청정기를 작동시키거나 창문을 열어 환기하시기 바랍니다.";
						}else if(ciciTxt2Arr2[z] == "VOCs"){
							ciciAttrTxt = "실내 VOCs 농도가 높으니 창문을 열어 환기를 시키거나 공기청정기를 작동시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "이산화탄소"){
							ciciAttrTxt = "실내 CO2 농도가 높아 업무효율과 학습능력이 떨어질 수 있으니 창문을 열어 환기시켜 주세요.";
						}else if(ciciTxt2Arr2[z] == "소음"){
							ciciAttrTxt = "소음공해가 발생되고 있습니다. 소음원인을 확인하고 원인을 제거하기 바랍니다.";
						}else if(ciciTxt2Arr2[z] == "초미세먼지"){
							//ciciAttrTxt += "";
							ciciAttrTxt = "";
						}
						
						ciciTxtStr2 += "["+ciciTxt2Arr2[z]+" - "+jisuTxt(ciciTxt2Arr[z])+"] "+ciciAttrTxt+"<br/>";
					}
				}
				if(ciciTxtStr2 != ""){
					BRtag = "<br/>";
				}
				ciciTxt = ciciTxtStr + "의 지수가 낮아 어린이나 노약자가 불쾌감을 느낄 수 있습니다."+BRtag;
				ciciTxt += ciciTxtStr2;
				
				$("#behavior").html(ciciTxt);
			}else if(cici >= 80 && cici <= 89) { 
				
				for (var z = 0; z < ciciTxt2Arr2.length; z++){
					if(ciciTxt2Arr[z] >= 0 && ciciTxt2Arr[z] <= 79){
						
						if(ciciTxt2Arr2[z] == "온도"){
							if(temp < 24.2){
								ciciAttrTxt = "온도를 높여 쾌적한 환경을 조성하세요. <br/>";
							}else{
								ciciAttrTxt = "온도를 낮추어 쾌적한 환경을 조성하세요. <br/>";
							}
							
						}else if(ciciTxt2Arr2[z] == "습도"){
							if(humi < 75.2){
								ciciAttrTxt = "습도가 낮아 불쾌감을 느낄 수 있으니 가습 장치 작동을 권장합니다. <br/>";
							}else{
								ciciAttrTxt = "습도가 높아 불쾌감을 느낄 수 있으니 제습 장치 작동을 권장합니다. <br/>";
							}
						}else if(ciciTxt2Arr2[z] == "미세먼지"){
							ciciAttrTxt = "실내 미세먼지 농도가 다소 높아 환기가 필요합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "VOCs"){
							ciciAttrTxt = "VOCs가 농도가 다소 높아 환기가 필요합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "이산화탄소"){
							ciciAttrTxt = "CO₂ 농도가 다소 높아 환기가 필요합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "소음"){
							ciciAttrTxt = "소음으로 민감 계층이 일상생활에 불편함을 느낄 수 있으니 소음 조절을 권장합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "초미세먼지"){
							//ciciAttrTxt += "";
							ciciAttrTxt = "";
						}
					}
				}
				ciciTxt = "실내공기가 대체로 쾌적하게 유지되고 있습니다.";
				if(ciciAttrTxt != ""){
					ciciTxt += "<br/>다만, 더욱 쾌적한 환경을 위해서 아래의 행동 요령을 참고해주세요. <br/>"+ciciAttrTxt+" ";
				}
				
				$("#behavior").html(ciciTxt);
			}else if(cici >= 90 && cici <= 100) {
				for (var z = 0; z < ciciTxt2Arr2.length; z++){
					if(ciciTxt2Arr[z] >= 0 && ciciTxt2Arr[z] <= 79){
						
						if(ciciTxt2Arr2[z] == "온도"){
							if(temp < 24.2){
								ciciAttrTxt = "온도를 높여 쾌적한 환경을 조성하세요. <br/>";
							}else{
								ciciAttrTxt = "온도를 낮추어 쾌적한 환경을 조성하세요. <br/>";
							}
							
						}else if(ciciTxt2Arr2[z] == "습도"){
							if(humi < 75.2){
								ciciAttrTxt = "습도가 낮아 불쾌감을 느낄 수 있으니 가습 장치 작동을 권장합니다. <br/>";
							}else{
								ciciAttrTxt = "습도가 높아 불쾌감을 느낄 수 있으니 제습 장치 작동을 권장합니다. <br/>";
							}
						}else if(ciciTxt2Arr2[z] == "미세먼지"){
							ciciAttrTxt = "실내 미세먼지 농도가 다소 높아 환기가 필요합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "VOCs"){
							ciciAttrTxt = "VOCs가 농도가 다소 높아 환기가 필요합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "이산화탄소"){
							ciciAttrTxt = "CO₂ 농도가 다소 높아 환기가 필요합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "소음"){
							ciciAttrTxt = "소음으로 민감 계층이 일상생활에 불편함을 느낄 수 있으니 소음 조절을 권장합니다. <br/>";
						}else if(ciciTxt2Arr2[z] == "초미세먼지"){
							//ciciAttrTxt += "";
							ciciAttrTxt = "";
						}
					}
				}
				ciciTxt = "실내공기가 매우 쾌적하게 유지되고 있습니다";
				if(ciciAttrTxt != ""){
					ciciTxt += "<br/>다만, 더욱 쾌적한 환경을 위해서 아래의 행동 요령을 참고해주세요.<br/> "+ciciAttrTxt+" ";
				}
				
				$("#behavior").html(ciciTxt);
			}
			
			
			if(cici >= 0 && cici <= 49) {
				$("#49").text(cici);
				$("#79").text("");
				$("#89").text("");
				$("#100").text("");
				$("#arrow1").addClass("info_arrow_05");
				$("#arrow2").removeClass("info_arrow_04");
				$("#arrow4").removeClass("info_arrow_02");
				$("#arrow5").removeClass("info_arrow_01");
				
			}else if(cici >= 50 && cici <= 79) {
				$("#49").text("");
				$("#79").text(cici);
				$("#89").text("");
				$("#100").text("");
				$("#arrow1").removeClass("info_arrow_05");
				$("#arrow2").addClass("info_arrow_04");
				$("#arrow4").removeClass("info_arrow_02");
				$("#arrow5").removeClass("info_arrow_01");
			}else if(cici >= 80 && cici <= 90) {
				$("#49").text("");
				$("#79").text("");
				$("#89").text(cici);
				$("#100").text("");
				$("#arrow1").removeClass("info_arrow_05");
				$("#arrow2").removeClass("info_arrow_04");
				$("#arrow4").addClass("info_arrow_02");
				$("#arrow5").removeClass("info_arrow_01");
			}else if(cici >= 90 && cici <= 100){
				$("#49").text("");
				$("#79").text("");
				$("#89").text("");
				$("#100").text(cici);
				$("#arrow1").removeClass("info_arrow_05");
				$("#arrow2").removeClass("info_arrow_04");
				$("#arrow4").removeClass("info_arrow_02");
				$("#arrow5").addClass("info_arrow_01");
			}
		}else{
			
			$("#cici_2_total").text("");
		
		}
		

	} else {
		$('#iaqDiv').hide();
		$('#oaqDiv').show();

		coci = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.coci == null) ? "" : sensorDatas[arrayIdx].sensor.coci;
		temp = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.temp == null) ? "" : sensorDatas[arrayIdx].sensor.temp;
		humi = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.humi == null) ? "" : sensorDatas[arrayIdx].sensor.humi;
		pm10 = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.pm10 == null) ? "" : sensorDatas[arrayIdx].sensor.pm10;
		pm25 = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.pm25 == null) ? "" : sensorDatas[arrayIdx].sensor.pm25;
		voc = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.voc == null) ? "" : sensorDatas[arrayIdx].sensor.voc;
		co2 = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.co2 == null) ? "" : sensorDatas[arrayIdx].sensor.co2;
		noise = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.noise == null) ? "" : sensorDatas[arrayIdx].sensor.noise;

		coci_pm10  = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.coci_pm10 == null) ? "" : sensorDatas[arrayIdx].sensor.coci_pm10;
		coci_pm25  = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.coci_pm25 == null) ? "" : sensorDatas[arrayIdx].sensor.coci_pm25;
		coci_temp  = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.coci_temp == null) ? "" : sensorDatas[arrayIdx].sensor.coci_temp;
		coci_humi  = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.coci_humi == null) ? "" : sensorDatas[arrayIdx].sensor.coci_humi;
		cici_co2  =  (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_co2 == null) ? "" : sensorDatas[arrayIdx].sensor.cici_co2;
		cici_voc  =  (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.cici_voc == null) ? "" : sensorDatas[arrayIdx].sensor.cici_voc;
		cici_noise = (sensorDatas[arrayIdx].sensor == null || sensorDatas[arrayIdx].sensor.noise == null) ? "" : sensorDatas[arrayIdx].sensor.noise;

		if(!pm10){ coci_pm10 = ""; }
		if(!pm25){ coci_pm25 = ""; }
		if(!temp){ coci_temp = ""; }
		if(!humi){ coci_humi = ""; }

		if(!co2){ cici_co2 = ""; }
		if(!voc){ cici_voc = ""; }
		if(!noise){ cici_noise = ""; }

		if(coci_temp != ""){
			$("#temp_title").text(temp +"℃");
			$("#temp_index").text(jisuTxt(coci_temp));
			$("#temp_index_val").text(coci_temp);
			$("#temp_index").css("color",txtColor(coci_temp));
			$("#temp_title").css("color",txtColor(coci_temp));
			$("#temp_index_val").css("color",txtColor(coci_temp));
			$("#temp_total").text("/100");
			AmCharts.makeChart("temp", MakeObj("temp",coci_temp,txtColor(coci_temp)));
		}else{
			$("#temp_total").text("");
		}
		
		if(coci_humi != ""){
			$("#humi_title").text(humi +"%");
			$("#humi_index").text(jisuTxt(coci_humi));
			$("#humi_index_val").text(coci_humi);
			$("#humi_index").css("color",txtColor(coci_humi));
			$("#humi_title").css("color",txtColor(coci_humi));
			$("#humi_index_val").css("color",txtColor(coci_humi));
			$("#humi_total").text("/100");
			AmCharts.makeChart("humi", MakeObj("humi",coci_humi,txtColor(coci_humi)));
		}else{
			$("#humi_total").text("");
		}
		
		if(coci_pm10 != ""){
			$("#pmat_title").text(pm10 + "㎍/㎥");
			$("#pmat_index").text(jisuTxt(coci_pm10));
			$("#pmat_index_val").text(coci_pm10);
			$("#pmat_index").css("color",txtColor(coci_pm10));
			$("#pmat_title").css("color",txtColor(coci_pm10));
			$("#pmat_index_val").css("color",txtColor(coci_pm10));
			$("#pmat_total").text("/100");
			AmCharts.makeChart("pmat", MakeObj("pm10",coci_pm10,txtColor(coci_pm10)));
		}else{
			$("#pmat_total").text("");
		}
		
		if (coci_pm25 != "") {
			$("#pm25_title").text(pm25 + "㎍/㎥");
			$("#pm25_index").text(jisuTxt(coci_pm25));
			$("#pm25_index_val").text(coci_pm25);
			$("#pm25_index").css("color",txtColor(coci_pm25));
			$("#pm25_title").css("color",txtColor(coci_pm25));
			$("#pm25_index_val").css("color",txtColor(coci_pm25));
			$("#pm25_total").text("/100");
			AmCharts.makeChart("pm25", MakeObj("pm25",coci_pm25,txtColor(coci_pm25)));
		} else {
			$("#pm25_total").text("");
		}
		
		if (cici_co2 != "") {
			$("#co2_title").text(co2 + "ppm");
			$("#co2_index").text(jisuTxt(cici_co2));
			$("#co2_index_val").text(cici_co2);
			$("#co2_index").css("color",txtColor(cici_co2));
			$("#co2_title").css("color",txtColor(cici_co2));
			$("#co2_index_val").css("color",txtColor(cici_co2));
			$("#co2_total").text("/100");
			AmCharts.makeChart("co2", MakeObj("co2",cici_co2,txtColor(cici_co2)));

		} else {
			$("#co2_total").text("");
		}

		if (cici_noise != ""){
			$("#noise_title").text(noise + "㏈");
			$("#noise_index").text(jisuTxt(cici_noise));
			$("#noise_index_val").text(cici_noise);
			$("#noise_index").css("color",txtColor(cici_noise));
			$("#noise_title").css("color",txtColor(cici_noise));
			$("#noise_index_val").css("color",txtColor(cici_noise));
			$("#noise_total").text("/100");
			AmCharts.makeChart("noise", MakeObj("noise",cici_noise,txtColor(cici_noise)));

		} else {
			$("#noise_total").text("");
		}

		if (coci != "") {
			$("#cici_index").text(jisuTxt(coci));
			$("#cici_index_val").text(coci);
			$("#cici_index").css("color",txtColor(coci));
			$("#cici_title").css("color",txtColor(coci));
			$("#cici_index_val").css("color",txtColor(coci));
			$("#cici_total").text("/100");
			AmCharts.makeChart("cici", MakeObj("coci",coci,txtColor(coci)));

			if (coci >= 0 && coci <= 49) {
				$("#49").text(coci);
				$("#79").text("");
				$("#89").text("");
				$("#100").text("");
				$("#iaq_arrow1").addClass("info_arrow_05");
				$("#iaq_arrow2").removeClass("info_arrow_04");
				$("#iaq_arrow4").removeClass("info_arrow_02");
				$("#iaq_arrow5").removeClass("info_arrow_01");

			} else if (coci >= 50 && coci <= 79) {
				$("#49").text("");
				$("#79").text(coci);
				$("#89").text("");
				$("#100").text("");
				$("#arrow1").removeClass("info_arrow_05");
				$("#arrow2").addClass("info_arrow_04");
				$("#arrow4").removeClass("info_arrow_02");
				$("#arrow5").removeClass("info_arrow_01");

			} else if (coci >= 80 && coci <= 89) {
				$("#49").text("");
				$("#79").text("");
				$("#89").text(coci);
				$("#100").text("");
				$("#arrow1").removeClass("info_arrow_05");
				$("#arrow2").removeClass("info_arrow_04");
				$("#arrow4").addClass("info_arrow_02");
				$("#arrow5").removeClass("info_arrow_01");

			} else if (coci >= 90 && coci <= 100) {
				$("#49").text("");
				$("#79").text("");
				$("#89").text("");
				$("#100").text(coci);
				$("#arrow1").removeClass("info_arrow_05");
				$("#arrow2").removeClass("info_arrow_04");
				$("#arrow4").removeClass("info_arrow_02");
				$("#arrow5").addClass("info_arrow_01");
			}

		} else {
			$("#coci_total").text("");
		}
	}

	arrayIdx++;
}

function getVentData(){
	$.ajax({
		 url:"/api/datacenter/vent",
		 type:"GET",
		 async : false,
		 success : function (param) {
			 //console.log(param.data.length);
			 if(param.data.length != 0){
				 $("#vent_btn").show();
			 }else{
				 $("#vent_btn").hide();
			 }
		 }
	})
}

function openVentPop(){
	var serial = $("#serial").val();
	var deviceType = $("#deviceType").val();
	
	if(!serial){
		return false;
	}
	
	if(deviceType == "IAQ"){
		winWidth = 1000;
		winHeight = 600;
		
		window.open('','vent_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='+winWidth+',height='+winHeight+',left=0,top=0');
		
		var act_url = "/datacenter/vent";
		var target =	document.getElementById("detail_info_form");
		target.target =	"vent_pop";
		target.action =	act_url;
		target.serial.value =	serial;
		target.deviceType.value = deviceType;
		target.submit();
	}
}

function txtColor(val){
	var colorCode = "";
	if(val >= 0 && val <= 49) {
		colorCode = "#ff0000";
	}else if(val >= 50 && val <= 79) {
		colorCode = "#ff6700";
	}else if(val >= 80 && val <= 89) {
		colorCode = "#8dd638";
	}else if(val >= 90 && val <= 100) {
		colorCode = "#44c7f4";
	}else {
		colorCode = "#929498";
	}
	return colorCode;
}


function jisuTxt(val){
	var txt = "";
	if(val >= 0 && val <= 49) {
		txt = "매우나쁨";
	}else if(val >= 50 && val <= 79) {
		txt = "나쁨";
	}else if(val >= 80 && val <= 89) {
		txt = "보통";
	}else if(val >= 90 && val <= 100) {
		txt = "좋음";
	}else {
		txt = "";
	}
	return txt;
}


function MakeObj(type,typeVal,colorCode){
	var objData = {
		"type":"gauge",
		"pathToImages":".\/lib\/amcharts\/images\/",
		"arrows":[
			{
				"id":type+"_arrow",
				"value":typeVal, //cici_temp
				"innerRadius":50,
				"nailAlpha":0,
				"startWidth":15,
				"color":colorCode,
				"radius":"65%"
			}
		],
		"axes":[
			{
				"startAngle":-160,
				"endAngle":160,
				"endValue":100,
				"id":type+"_axis",
				"axisAlpha":0,
				"tickAlpha":0,
				"gridCount":1,
				"showFirstLabel":false,
				"showLastLabel":false,
				"bands":[
					{
					"color":colorCode,
					"startValue":0,
					"endValue":typeVal, //cici_temp
					"id":"startval",
					"innerRadius":"75%"
					},
					{
					"color":"#ffffff",
					"startValue":typeVal, //cici_temp
					"endValue":100,
					"id":"endval",
					"innerRadius":"75%"
					}
				]
			}
		]
	}
	
	return objData;
}
function goLogOut(){
	location.href="/datacenter/logout";
}

function goList(){
	
	location.href="/datacenter/list";
	
}

$(document).ready(function(){
	$("#id_area").hide();
	$("#id_area").text("");
	
	var getCookie = function(name) {
	    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return value? value[2] : null;
	};
	
	if(getCookie("_USER_AUTH") == "group"){
		if(getCookie("GROUP_AUTH")){
			$("#id_area").show();
		}else{
			$("#id_area").hide();
			$("#id_area").text("");
		}
		$("#listBtnArea").show();
		$("#ventBtnArea").hide();
		$("#detailBtnArea").hide();
	}else{
		if(getCookie("MEMBER_AUTH")){
			$("#id_area").show();
		}else{
			$("#id_area").hide();
			$("#id_area").text("");
		}
		$("#listBtnArea").hide();
		$("#ventBtnArea").show();
		$("#detailBtnArea").show();
	}
	
	function reload(){
		location.href="/datacenter/did/";
	}
	
	getSensorData();
	
	if(getCookie("_USER_AUTH") != "group"){
		getVentData();
	}
	
	station_no = $("#station_no").val();
	region_id = $("#region_id").val();
	weighted_value = $("#weighted_value").val();
	co2_index = $("#co2_index").val();
	voc_index = $("#voc_index").val();
	db_index = $("#db_index").val();
});
