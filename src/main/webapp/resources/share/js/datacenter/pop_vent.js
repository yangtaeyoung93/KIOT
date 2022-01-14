
function getVentData(){
	var listHtml = "";
	var date = "";
	var year = "";
	var month = "";
	var day = "";
	var hour = "";
	var minute = "";
	var second = "";
	var convertTime = "";
	var checked = "";
	var checked2 = "";
	var disabled ="";
	var disabled2 = "";
	var serial = "";
	var auto_mode = "";
	var air_volume = "";
	var power = "";
	var iaqSerial = "";
	var aiMode = "";
	var tdColor = "";
	
	$.ajax({
		 url:"/api/datacenter/vent",
		 type:"GET",
		 async : false,
		 success : function (param) {
			 console.log(param);
			 
			 listHtml = "<tr><th class='bgGray1'>시리얼번호</th><th class='bgGray1'>데이터시간</th><th class='bgGray1'>AI모드</th><th class='bgGray1'>전원</th><th class='bgGray1'>풍량설정</th><th class='bgGray1'>비고</th></tr>";
			 for(var i = 0; i < param.data.length; i++){
				 serial = param.data[i].serial;
				 iaqSerial = param.data[i].iaqSerial;
				 aiMode = param.data[i].aiMode;
				 receiveFlag  = param.data[i].receiveFlag;
				 
				 if(!aiMode){
					 aiMode = 0;
				 }
				 
				 if(param.data[i].sensor == null){
					 auto_mode = "0";
				 	 air_volume = "1";
				 	 power = "0";
				 	
				 }else{
					 auto_mode = param.data[i].sensor.auto_mode;
				 	 air_volume = param.data[i].sensor.air_volume;
				 	 power = param.data[i].sensor.power;
				 	 disabled = "";
				 }
				 
				 if(receiveFlag == false){
					 tdColor = "color:#929498";
				 }else{
					 tdColor = "";
				 }

				 if(serialNum == iaqSerial){
					 date = new Date(param.data[i].timestamp*1000);
	
					 year = date.getFullYear();
					 month = date.getMonth()+1;
					 day = date.getDate();
					 hours = date.getHours();
					 minutes = date.getMinutes();
					 seconds = date.getSeconds();
					
					if(month <10) {	month = '0'+month; }
					if(day <10) {	day = '0'+day; }
					if(hours <10) {	hours = '0'+hours; }
					if(minutes <10) { minutes = '0'+minutes; }
					if(seconds <10) { seconds = '0'+seconds; }
					
					convertTime = year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
					
				 	 if(aiMode == 1){ //AI모드가 ON이면 전원 풍량은 제어 불가능
				 		checked = "checked";
				 		disabled = "disabled='true'";
				 		opacity = "opacity:40%";
				 		disabled2 = disabled;
				 	 }else{
				 		checked = "";
				 		disabled = "";
				 		opacity = "opacity:100%";
				 	 }

				 	 if(power == 1){
				 		checked2 = "checked";
				 		if(aiMode ==1){
				 			disabled2 = disabled;
				 		}else{
				 			disabled2 = "";
				 		}
				 	 }else{
				 		checked2 = "";
				 		if(aiMode ==1){
				 			disabled2 = disabled;
				 		}else if(param.data[i].sensor == null){
				 			disabled2 = "disabled='true'";
				 			opacity = "opacity:40%";
				 		}else{
				 			disabled2 = "disabled='true'";
				 		}
				 	 }
					 listHtml += "<tr>";
					 listHtml += "<td style='"+tdColor+"'>"+serial+"</td>";
					 listHtml += "<td style='"+tdColor+"'>"+convertTime+"</td>";
					 listHtml += "<td style='"+tdColor+"'>";
					 listHtml += "Off <div class='custom-control custom-switch' style='display:inline-block;vertical-align: top; margin-left:10px;'>";
					 listHtml += "<input type='checkbox' class='custom-control-input' "+checked+" id='aiMode_"+serial+"' value='"+aiMode+"' onClick='chkAi(\""+serial+"\");'>";
					 listHtml += "<label class='custom-control-label' for='aiMode_"+serial+"'></label>";
					 listHtml += "</div>  On";
					 listHtml += "</td>";
					 
					 listHtml += "<td style='"+tdColor+"'>";
					 listHtml += "Off <div class='custom-control custom-switch' style='display:inline-block;vertical-align: top; margin-left:10px;'>";
					 listHtml += "<input type='checkbox' class='custom-control-input' "+checked2+" id='power_mode_"+serial+"' value='"+power+"' onClick='chkPower(\""+serial+"\");' "+disabled+">";
					 listHtml += "<label class='custom-control-label' for='power_mode_"+serial+"'></label>";
					 listHtml += "</div>  On";
					 listHtml += "</td>";
					 
					 listHtml += "<td style='"+tdColor+"'>";
					 listHtml += "<input type='hidden' id='h_power_"+serial+"' value='"+power+"'/>";
					 listHtml += "<input type='hidden' id='h_aiMode_"+serial+"' value='"+aiMode+"'/>";
					 listHtml += "<input type='hidden' id='h_air_volume_"+serial+"' value='"+air_volume+"'/>";
					 listHtml += "<input type='button' class='air_btn' id='airLeft_"+serial+"' style='margin-right:10px;"+opacity+"' onClick = 'vent_wind(\""+serial+"\",0)' value='<' "+disabled2+"/>";
					 listHtml += "<span id='airTxt_"+serial+"'>"+air_volume+"</span>";
					 listHtml += "<input type='button' class='air_btn' id='airRight_"+serial+"' style='margin-left:10px;"+opacity+"' onClick= 'vent_wind(\""+serial+"\",1)' value='>' "+disabled2+"/>";
					 listHtml += "</td>";
					 listHtml += "<td style='"+tdColor+"'><input type='button' value='적용' style='width:100px;height:35px;' onClick='goExec(\""+serial+"\");' /></td>";
					 listHtml += "</tr>";
				 }
			 }
			 //alert(listHtml);
			 $("#popTable").html(listHtml);
		}
	});
}
function vent_wind(serial,type){ //풍량 버튼 
	var targetVal = parseInt($("#airTxt_"+serial).text());
	//var obj = new Object();

	if(type == 0){
		if(targetVal < 2){
			return false;
		}else{
			targetVal -= 1;
		}
	}else{
		if(targetVal > 5){
			return false;
		}else{
			targetVal += 1;
		}
	}
	
	$("#airTxt_"+serial).text(targetVal);
}

function chkPower(serial){ //전원이 off이면 제어 불가능하게 설정 ON이면 제어가능하게 설정
	var chkVal = $("#power_mode_"+serial).val();
	if($("input:checkbox[id=power_mode_"+serial+"]").is(":checked") == true){
		$("#power_mode_"+serial).val("1");
		$("#airLeft_"+serial).attr("disabled",false);
		$("#airRight_"+serial).attr("disabled",false);
		$("#airLeft_"+serial).css("opacity","100%");
		$("#airRight_"+serial).css("opacity","100%");
	}else{
		$("#power_mode_"+serial).val("0");
		$("#airLeft_"+serial).attr("disabled",true);
		$("#airRight_"+serial).attr("disabled",true);
		$("#airLeft_"+serial).css("opacity","40%");
		$("#airRight_"+serial).css("opacity","40%");
	}
}

function chkAi(serial){
	//var modeVal = $("input:checkbox[id=power_mode_"+serial+"]:checked").val();
	var obj = new Object();
	var chkVal = $("#aiMode_"+serial).val();
	var HaiVal = parseInt($("#h_aiMode_"+serial).val());//히든 ai모드 value 이값에 의해 ui에서 전원 풍량 diasbled 유무 설정
	
	if(HaiVal == 1 && $("input:checkbox[id=aiMode_"+serial+"]").is(":checked") == false){
		$("#aiMode_"+serial).val("0");
		$("#power_mode_"+serial).attr("disabled",true);
		$("#airLeft_"+serial).attr("disabled",true);
		$("#airRight_"+serial).attr("disabled",true);
		$("#airLeft_"+serial).css("opacity","40%");
		$("#airRight_"+serial).css("opacity","40%");
	}else{
		if($("input:checkbox[id=aiMode_"+serial+"]").is(":checked") == true){
			console.log("111");
			$("#aiMode_"+serial).val("1");
			$("#power_mode_"+serial).attr("disabled",true);
			$("#airLeft_"+serial).attr("disabled",true);
			$("#airRight_"+serial).attr("disabled",true);
			$("#airLeft_"+serial).css("opacity","40%");
			$("#airRight_"+serial).css("opacity","40%");
		}else{
			console.log("222");
			$("#aiMode_"+serial).val("0");
			$("#power_mode_"+serial).attr("disabled",false);
			$("#airLeft_"+serial).attr("disabled",false);
			$("#airRight_"+serial).attr("disabled",false);
			$("#airLeft_"+serial).css("opacity","100%");
			$("#airRight_"+serial).css("opacity","100%");
		}
	}
}

function goExec(val){
	var serial = val; //시리얼 번호
	var powerVal = parseInt($("#power_mode_"+serial).val()); //실제 전원 value
	var HpowerVal = parseInt($("#h_power_"+serial).val());  //히든 전원 value 딱히 하는건 없음
	var aiVal = parseInt($("#aiMode_"+serial).val()); //ai모드 value
	var HaiVal = parseInt($("#h_aiMode_"+serial).val());//히든 ai모드 value 이값에 의해 ui에서 전원 풍량 diasbled 유무 설정
	var h_air_volume = parseInt($("#h_air_volume_"+serial).val()); //히든 풍량 value
	var air_volume = parseInt($("#airTxt_"+serial).text());//풍량 value 
	
	var obj = new Object();
	var obj2 = new Object();
	var obj3 = new Object();
	
	
	obj = { serial : serial, mode : "P"+powerVal } //전원 오브젝트 P0, P1
	
	obj3 = { serial : serial, mode : "W"+air_volume } //풍량 오브젝트 W1~6
	console.log(obj2);
	
	
	
	if(aiVal == 0){ //ai모드가 off일때만 전원,풍량은 제어가능
		
		if(HaiVal == 1 && aiVal == 0){ //off로 되어있고 로딩햇을때 on이였으면
			obj2 = { serial : serial, mode : "A0"} //ai모드 오브젝트 A0, A1
			$.ajax({
				 url:"/api/platform/mqtt",
				 type:"POST",
				 async : false,
				 data : obj2,
				 success : function (d) {
					 console.log(d);
				}
			});
		}else{
			if(HpowerVal != 0){ //1이면
				if(powerVal == 1){ //전원이 on일때 제어가능
					$.ajax({
						 url:"/api/platform/mqtt",
						 type:"POST",
						 async : false,
						 data : obj3,
						 success : function (d) {
							 console.log(d);
						}
					});
				}else{ //off로 전원을 설정하면 값 보냄
					$.ajax({
						 url:"/api/platform/mqtt",
						 type:"POST",
						 async : false,
						 data : obj,
						 success : function (d) {
							 console.log(d);
						}
					});
				}
			}else{
				
				$.ajax({
					 url:"/api/platform/mqtt",
					 type:"POST",
					 async : false,
					 data : obj,
					 success : function (d) {
						 console.log(d);
					}
				});
				
				if(powerVal == 1){ //전원이 on일때 제어가능
					$.ajax({
						 url:"/api/platform/mqtt",
						 type:"POST",
						 async : false,
						 data : obj3,
						 success : function (d) {
							 console.log(d);
						}
					});
				}
				
			}
		}
			
			
		
	}else{
		obj2 = { serial : serial, mode : "A1"} //ai모드 오브젝트 A0, A1
		$.ajax({
			 url:"/api/platform/mqtt",
			 type:"POST",
			 async : false,
			 data : obj2,
			 success : function (d) {
				 console.log(d);
			}
		});
	}
	
	//console.log(powerVal+":"+HpowerVal+":"+autoVal+":"+HautoVal+":"+air_volume+":"+h_air_volume);
	
	alert("적용 완료");
	location.reload();
}

$().ready(function() {
	if(!serialNum){
		$("#txtSpanSerial").text("시리얼번호 : 없음");	
	}else{
		$("#txtSpanSerial").text("시리얼번호 : "+serialNum);
	}
	
	if(!stationName){
		$("#txtSpanStation").text("스테이션명 : 없음");
	}else{
		$("#txtSpanStation").text("스테이션명 : "+stationName);
	}
	
	getVentData();
})
