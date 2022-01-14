function open_report()	{
	var winWidth = 400; 
	var winheight = 400; 

	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}

	newWindow = window.open('./repo/main_grp_payment.html','newPopup','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='+winWidth+',height='+winHeight+',left=0,top=0');
	newWindow.focus();
}

function view_detail(){
	var winWidth = 400; 
	var winheight = 400; 

	if (screen) {
		winWidth = screen.width;
		winHeight = screen.height;
	}

	newWindow = window.open('','airguardk_detail_pop','toolbar=no, location=no, scrollbars=yes,resizable=yes,width='+winWidth+',height='+winHeight+',left=0,top=0');
		
	var target =	document.getElementById("detail_info_form");

	var param = target.serial.value.substr(target.serial.value.length-5, 5)+"|"+target.nowAll.value;
	target.target =	"airguardk_detail_pop";
	target.action =	act_url;
	target.param.value =	param;
	target.submit();

	newWindow.focus();
}

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
		success : function(d){
			// console.log(d.data);
			sensorDatas = d.data;
			setInterval(drawSensorData, 1000);
		}
	});
}

function drawSensorData() {
	if (sensorDatas.length <= arrayIdx) arrayIdx = 0;

	console.log('data : ', sensorDatas[arrayIdx]);

	$("#locationSpan").text(sensorDatas[arrayIdx].spaceName);
	$("#stationNameSpan").text(sensorDatas[arrayIdx].stationName);
	$("#serialNumSpan").text(sensorDatas[arrayIdx].serial);

	var time = sensorDatas[arrayIdx].sensor.tm;
	var y = time.substring(0,4);
	var m = time.substring(4,6) -1;
	var d = time.substring(6,8);
	var h = time.substring(8,10);
	var i = time.substring(10,12);
	var update = new Date(y, m, d, h, i).format("YY.MM.dd a/p hh:mm");
	$("#update_time").text(update);
	$("#update_time2").text("*기준 : " + update + "  *제공 : 케이웨더");

	if (sensorDatas[arrayIdx].deviceType == "IAQ") {
		$('#oaqDiv').hide();
		$('#iaqDiv').show();

		$("#cici_index").text(sensorDatas[arrayIdx].sensor.cici);
		$("#cici_index_val").text(sensorDatas[arrayIdx].sensor.cici);

		$("#temp_title").text(sensorDatas[arrayIdx].sensor.temp);
		$("#temp_index").text(sensorDatas[arrayIdx].sensor.temp);
		$("#temp_index_val").text(sensorDatas[arrayIdx].sensor.temp);

		$("#hum_title").text(sensorDatas[arrayIdx].sensor.humi);
		$("#humi_index").text(sensorDatas[arrayIdx].sensor.humi);
		$("#humi_index_val").text(sensorDatas[arrayIdx].sensor.humi);

		$("#pmat_title").text(sensorDatas[arrayIdx].sensor.pm10);
		$("#pmat_index").text(sensorDatas[arrayIdx].sensor.pm10);
		$("#pmat_index_val").text(sensorDatas[arrayIdx].sensor.pm10);

		$("#vocs_title").text(sensorDatas[arrayIdx].sensor.vocs);
		$("#vocs_index").text(sensorDatas[arrayIdx].sensor.vocs);
		$("#vocs_index_val").text(sensorDatas[arrayIdx].sensor.vocs);

		$("#co2_title").text(sensorDatas[arrayIdx].sensor.co2);
		$("#co2_index").text(sensorDatas[arrayIdx].sensor.co2);
		$("#co2_index_val").text(sensorDatas[arrayIdx].sensor.co2);

		$("#noise_title").text(sensorDatas[arrayIdx].sensor.noise);
		$("#noise_index").text(sensorDatas[arrayIdx].sensor.noise);
		$("#noise_index_val").text(sensorDatas[arrayIdx].sensor.noise);

		if(sensorDatas[arrayIdx].sensor.cici >= 0 && sensorDatas[arrayIdx].sensor.cici <= 20) {
			$("#40").text("");
			$("#60").text("");
			$("#80").text("");
			$("#100").text("");
			$("#20").text(sensorDatas[arrayIdx].sensor.cici);
		}else if(sensorDatas[arrayIdx].sensor.cici >= 21 && sensorDatas[arrayIdx].sensor.cici <= 40) {
			$("#20").text("");
			$("#60").text("");
			$("#80").text("");
			$("#100").text("");
			$("#40").text(sensorDatas[arrayIdx].sensor.cici);
		}else if(sensorDatas[arrayIdx].sensor.cici >= 41 && sensorDatas[arrayIdx].sensor.cici <= 60) {
			$("#20").text("");
			$("#40").text("");
			$("#80").text("");
			$("#100").text("");
			$("#60").text(sensorDatas[arrayIdx].sensor.cici);
		}else if(sensorDatas[arrayIdx].sensor.cici >= 61 && sensorDatas[arrayIdx].sensor.cici <= 80){
			$("#20").text("");
			$("#40").text("");
			$("#60").text("");
			$("#100").text("");
			$("#80").text(sensorDatas[arrayIdx].sensor.cici);
		}else if(sensorDatas[arrayIdx].sensor.cici >= 81 && sensorDatas[arrayIdx].sensor.cici <= 100) {
			$("#20").text("");
			$("#40").text("");
			$("#60").text("");
			$("#80").text("");
			$("#100").text(sensorDatas[arrayIdx].sensor.cici);
		}

	} else {
		$('#iaqDiv').hide();
		$('#oaqDiv').show();

		$("#cici_index").text(sensorDatas[arrayIdx].sensor.coci);
		$("#cici_index_val").text(sensorDatas[arrayIdx].sensor.coci);

		$("#temp_title").text(sensorDatas[arrayIdx].sensor.temp);
		$("#temp_index").text(sensorDatas[arrayIdx].sensor.temp);
		$("#temp_index_val").text(sensorDatas[arrayIdx].sensor.temp);

		$("#hum_title").text(sensorDatas[arrayIdx].sensor.humi);
		$("#humi_index").text(sensorDatas[arrayIdx].sensor.humi);
		$("#humi_index_val").text(sensorDatas[arrayIdx].sensor.humi);

		$("#pmat_title").text(sensorDatas[arrayIdx].sensor.pm10);
		$("#pmat_index").text(sensorDatas[arrayIdx].sensor.pm10);
		$("#pmat_index_val").text(sensorDatas[arrayIdx].sensor.pm10);

		$("#vocs_title").text(sensorDatas[arrayIdx].sensor.vocs);
		$("#vocs_index").text(sensorDatas[arrayIdx].sensor.vocs);
		$("#vocs_index_val").text(sensorDatas[arrayIdx].sensor.vocs);

		$("#co2_title").text(sensorDatas[arrayIdx].sensor.co2);
		$("#co2_index").text(sensorDatas[arrayIdx].sensor.co2);
		$("#co2_index_val").text(sensorDatas[arrayIdx].sensor.co2);

		$("#noise_title").text(sensorDatas[arrayIdx].sensor.noise);
		$("#noise_index").text(sensorDatas[arrayIdx].sensor.noise);
		$("#noise_index_val").text(sensorDatas[arrayIdx].sensor.noise);

		if(sensorDatas[arrayIdx].sensor.coci >= 0 && sensorDatas[arrayIdx].sensor.coci <= 20) {
			$("#40").text("");
			$("#60").text("");
			$("#80").text("");
			$("#100").text("");
			$("#20").text(sensorDatas[arrayIdx].sensor.coci);
		}else if(sensorDatas[arrayIdx].sensor.coci >= 21 && sensorDatas[arrayIdx].sensor.coci <= 40) {
			$("#20").text("");
			$("#60").text("");
			$("#80").text("");
			$("#100").text("");
			$("#40").text(sensorDatas[arrayIdx].sensor.coci);
		}else if(sensorDatas[arrayIdx].sensor.coci >= 41 && sensorDatas[arrayIdx].sensor.coci <= 60) {
			$("#20").text("");
			$("#40").text("");
			$("#80").text("");
			$("#100").text("");
			$("#60").text(sensorDatas[arrayIdx].sensor.coci);
		}else if(sensorDatas[arrayIdx].sensor.coci >= 61 && sensorDatas[arrayIdx].sensor.coci <= 80) {
			$("#20").text("");
			$("#40").text("");
			$("#60").text("");
			$("#100").text("");
			$("#80").text(sensorDatas[arrayIdx].sensor.coci);
		}else if(sensorDatas[arrayIdx].sensor.coci >= 81 && sensorDatas[arrayIdx].sensor.coci <= 100) {
			$("#20").text("");
			$("#40").text("");
			$("#60").text("");
			$("#80").text("");
			$("#100").text(sensorDatas[arrayIdx].sensor.coci);
		}
	}

	arrayIdx++;
}

$(document).ready(function(){
	getSensorData();

/*
	station_no = $("#station_no").val();
	region_id = $("#region_id").val();
	weighted_value = $("#weighted_value").val();
	co2_index = $("#co2_index").val();
	voc_index = $("#voc_index").val();
	db_index = $("#db_index").val();
*/

	$.ajax({
		cache: false,
		url: "/resources/share/js/datacenter/test.json",
		type:"GET",
		success : function(result) {
			var res = result;
			console.log("res.temp.temp : ", res.temp.temp);
			AmCharts.makeChart("temp", res.temp.temp);
			AmCharts.makeChart("hum", res.humi.humi);
			AmCharts.makeChart("pmat", res.pm10.pm10);
			AmCharts.makeChart("vocs", res.vocs.vocs);
			AmCharts.makeChart("co2", res.co2.co2);
			AmCharts.makeChart("noise", res.noise.noise);
			AmCharts.makeChart("cici", res.cici.cici);
		},
	});
});
