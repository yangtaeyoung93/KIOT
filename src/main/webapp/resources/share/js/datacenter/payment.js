function createXMLHttpRequest()	{
	if(window.ActiveXObject)	{
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	} else if(window.XMLHttpRequest)	{
		xmlHttp = new XMLHttpRequest();
	}

	if(xmlHttp == null)
		alert('XML 객체생성 실패');

	return xmlHttp;
}


function call_user_myphone(user_id, user_type){

	var target =	document.userModifyPhone;

	if(target == undefined) return 0;
	else if(target == null) return 0;

	target.u_id.value = user_id;
	target.u_type.value = user_type;

	if(user_id.length < 2){
		alert("사용자 정보가 없습니다.");
		return 0;
	}

	if(user_type.length < 1){
		alert("구분이 설정되지 않았습니다.");
		return 0;
	}
	
	var params =	"u_id="+user_id+"&u_type="+user_type;
	var url =		"/payment_admin/ajax_data/get_payment_user_info.php";
	document.getElementById("user_phone_div").style.display =	"";

	createXMLHttpRequest();
	xmlHttp.onreadystatechange = function(){
		if(xmlHttp.readyState == 4)	{
			if(xmlHttp.status == 200)	{
				if(xmlHttp.responseText == undefined){
					alert("응답 실패")
				}
				else{
					var exp =	xmlHttp.responseText.split("#");
					var target =	document.userModifyPhone;
					if(exp[0] == "success"){

						//	호출요소들
						var phone_list =	exp[4].split("/");
						var sms_table =	document.getElementById("user_phone_div");

						if(sms_table == undefined) return 0;
						else if(target == undefined) return 0;

						var sms_cnt =	(target.u_type.value == 2) ? 3 : 1;

						//	안쓰는 tr 지우기
						while(sms_table.rows.length > (sms_cnt+2)){
							sms_table.deleteRow(sms_cnt);
						}

						var nc=0;
						for(var i=0; i<sms_cnt; i++){
							if(target.tel_no[i] != undefined) var tmp_input = target.tel_no[i];
							else if (target.tel_no != undefined) var tmp_input = target.tel_no;
							else continue;
							
							if(sms_table.rows[i+1].cells[1] != undefined) sms_table.rows[i+1].cells[1].innerHTML =	"";
							if(phone_list[i] == undefined){
								tmp_input.value =	"";
							}
							else if(phone_list[i].length < 4){
								tmp_input.value =	"";
							}
							else if(phone_list[i].length == 10){
								tmp_input.value =	 phone_list[i].substr(0, 3)+" - "
															+phone_list[i].substr(3, 3)+" - "
															+phone_list[i].substr(6, 4);
								nc += 1;
							}
							else{
								tmp_input.value =	 phone_list[i].substr(0, 3)+" - "
															+phone_list[i].substr(3, 4)+" - "
															+phone_list[i].substr(7, 4);
								nc += 1;
							}
						}//end_for

						var btn_modify =	document.getElementById("sms_btn_modify");
						var btn_accept =	document.getElementById("sms_btn_accept");
						if(nc > 0){
							if(btn_modify != undefined){
								btn_modify.style.display =	"";
								btn_modify.innerHTML =	"수정";
								btn_accept.style.display =	"none";

							}
						}
						else{
							if(btn_modify != undefined){
								btn_modify.style.display =	"";
								btn_modify.innerHTML =	"등록";
								btn_accept.style.display =	"none";
							}
						}//end_else

					}//end_if
				}//end_else
			}//end_if(200)
		}
	}//end_xmlhttp

	xmlHttp.open("POST", url, true);
	xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
	xmlHttp.setRequestHeader("Cache-Control","no-cache, must-revalidate");
	xmlHttp.setRequestHeader("Pragma","no-cache");
	xmlHttp.send(params);
}

function close_user_myphone(){
	var sms_table =	document.getElementById("user_phone_div");
	if(sms_table != undefined) sms_table.style.display =	"none";
}

function chg_popup_mode_input(mode){
	if(mode == undefined) var mode = 0;

	var target =	document.userModifyPhone;
	var sms_table =	document.getElementById("user_phone_div");

	if(target == undefined) return 0;
	else if(sms_table == undefined) return 0;

	if(target.tel_no.length != undefined){
		for(var i=0; i<target.tel_no.length; i++){
			target.tel_no[i].value =	target.tel_no[i].value.replace(/[^0-9]/g,'');
			target.tel_no[i].disabled =	false;

			if(sms_table.rows[i+1].cells[1] != undefined){
				if(target.tel_no[i].value.length < 4){
					sms_table.rows[i+1].cells[1].innerHTML =	"<img src='/images/manager/btn_popup_icon_add.png' border='0' class='cursor_pointer' onclick='focus_object(userModifyPhone.tel_no["+i+"]);' />";
				}
				else{
					sms_table.rows[i+1].cells[1].innerHTML =	"<img src='/images/manager/btn_popup_icon_del.png' border='0' class='cursor_pointer' onclick='clear_object_value(userModifyPhone.tel_no["+i+"]);' />";
				}//end_else
			}
		}//end_for

		target.tel_no[0].focus();

	}
	else{
		target.tel_no.value =	target.tel_no.value.replace(/[^0-9]/g,'');
		target.tel_no.disabled =	false;

		if(sms_table.rows[1].cells[1] != undefined){
			if(target.tel_no.value.length < 4){
				sms_table.rows[1].cells[1].innerHTML =	"<img src='/images/manager/btn_popup_icon_add.png' border='0' class='cursor_pointer' onclick='focus_object(userModifyPhone.tel_no);' />";
			}
			else{
				sms_table.rows[1].cells[1].innerHTML =	"<img src='/images/manager/btn_popup_icon_del.png' border='0' class='cursor_pointer' onclick='clear_object_value(userModifyPhone.tel_no);' />";
			}//end_else
		}

		target.tel_no.focus();
	}

	var btn_modify =	document.getElementById("sms_btn_modify");
	var btn_accept =	document.getElementById("sms_btn_accept");
	btn_modify.style.display =	"none";
	btn_accept.style.display =	"";
}

function clear_object_value(obj){
	if(obj != undefined){
		if(obj.value != undefined) obj.value =	"";
		obj.focus();
	}
}

function focus_object(obj){
	if(obj != undefined) obj.focus();
}

function modify_user_myphone(){

	var target =	document.userModifyPhone;

	if(target == undefined) return 0;
	else if(target == null) return 0;

	var user_id = target.u_id.value;
	var user_type = target.u_type.value;

	if(user_id.length < 2){
		alert("사용자 정보가 없습니다..");
		return 0;
	}

	if(user_type.length < 1){
		alert("구분이 설정되지 않았습니다.");
		return 0;
	}

	var u_tel =	"";

	if(target.tel_no.length != undefined){
		for(var i=0; i<target.tel_no.length; i++){
			if(u_tel.length > 0) u_tel += "#";
			u_tel +=	target.tel_no[i].value.replace(/[^0-9]/g,'');
		}
	}
	else{
		u_tel =	target.tel_no.value.replace(/[^0-9]/g,'');
	}

	if( confirm("번호를 수정하시겠습니까?") ){
		var params =	"u_id="+user_id+"&u_type="+user_type+"&u_tels="+u_tel;
		var url =		"/payment_admin/ajax_data/payment_user_modify_phone.php";

		createXMLHttpRequest();
		xmlHttp.onreadystatechange = function(){
			if(xmlHttp.readyState == 4)	{
				if(xmlHttp.status == 200)	{
					if(xmlHttp.responseText == undefined){
						alert("응답 실패")
					}
					else{
						var exp =	xmlHttp.responseText.split("#");
						document.getElementById("user_phone_div").style.display =	"none";
						if(exp[0] == "success"){
							alert("번호가 수정되었습니다.");
						}//end_if
						else{
							alert("번호 수정을 실패했습니다.");
						}
					}//end_else
				}//end_if(200)
			}
		}//end_xmlhttp

		xmlHttp.open("POST", url, true);
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
		xmlHttp.setRequestHeader("Cache-Control","no-cache, must-revalidate");
		xmlHttp.setRequestHeader("Pragma","no-cache");
		xmlHttp.send(params);
	}//end_else
}