
$(document).ready(function(){
	$("#login").on("click", function(){
		var info = new LoginCheck();
		return false;
	});
	
});

function member_type_check(val)	{
	if(val == 'individual')	{
		document.getElementById('individual_btn').className	= 'individual_on';
		document.getElementById('corporation_btn').className	= 'corporation_off';
		login_check.member_type_val.value	= 'individual';
	} else if(val == 'corporation')	{
		document.getElementById('individual_btn').className	= 'individual_off';
		document.getElementById('corporation_btn').className	= 'corporation_on';
		login_check.member_type_val.value	= 'corporation';
	}

//	alert(login_check.member_type_val.value);
}

//로그인 유효성 체크
function LoginCheck() {
	var f = document.login_check;

	if(f.member_type_val.value == 'individual')	{
		if(f.idcheck.checked){
			saveLogin(login_check.id.value);
		}else{
			saveLogin("");
		}
		if(f.id.value == ""){
			alert("이메일 주소를 입력하세요.");
			f.id.focus();
			return false;
		}
		if(f.id.value.length < 5 ){
			alert("이메일 주소를 5자 이상 입력하세요.");
			f.id.focus();
			return false;
		}
		if(f.pwd.value == ""){
			alert("비밀번호를 입력하세요.");
			f.pwd.focus();
			return false;
		}
		if(f.pwd.value.length < 3 ){
			alert("비밀번호를 3자 이상 입력하세요.");
			f.pwd.focus();
			return false;
		}

		//f.action	= "";
		//f.submit();

	} else if(f.member_type_val.value == 'corporation')	{
		if(f.idcheck.checked){
			saveLogin(login_check.id.value);
		}else{
			saveLogin("");
		}
		if(f.id.value == ""){
			alert("이메일 주소를 입력하세요.");
			f.id.focus();
			return false;
		}
		if(f.id.value.length < 5 ){
			alert("이메일 주소를 5자 이상 입력하세요.");
			f.id.focus();
			return false;
		}
		if(f.pwd.value == ""){
			alert("비밀번호를 입력하세요.");
			f.pwd.focus();
			return false;
		}
		if(f.pwd.value.length < 3 ){
			alert("비밀번호를 3자 이상 입력하세요.");
			f.pwd.focus();
			return false;
		}

		//f.action	= "./remote_login_b2b_payment.php";
		//f.submit();
	}
}


//로그인 아이디 기억 체크박스 체크 여부 판별
function isChecked(){
	var chk = document.getElementsByName("idcheck");
	var len = chk.length;
	var i = 0;
	var saved = false;

	for(i = 0; i <len; i++){
		if(chk[i].checked == true){
			saved = true;
			break;
		}
	}
	return saved;
}

//체크 박스 체크 시 컨펌창
function confirmSave(checkbox){
	var isRemember;
	if(checkbox.checked){
		isRemember = confirm("이 PC에 로그인 정보를 저장하시겠습니까? \nPC방등의 공공장소에서는 개인정보가 유출될 수 있으니 주의해주십시오.");
		if(!isRemember){
			checkbox.checked = false;
		}
	}
}

function setsave(name, value, expiredays){
	var today = new Date();
	today.setDate(today.getDate() + expiredays);
	document.cookie = name + "=" + escape(value) + "; path=/; expires=" + today.toGMTString() + ";"
}

function saveLogin(id){
	if(id != ""){
		// userid 쿠키에 id 값을 7일간 저장
		setsave("userid", id, 7);
	}else{
		// userid 쿠키 삭제
		setsave("userid", id, -1);
	}
}

function readCookie(name){
	var cook = document.cookie;
	var search = name+"=";

	if(cook.length >0){
		var offset = document.cookie.indexOf(search);

		if(offset != -1){
			offset += search.length;  
			end =  cook.indexOf(";",offset)
			
			if(end == -1){
				end = cook.length;
			}
//			alert(cook.substring(offset,end));
			$("#idcheck").attr("checked", "checked")
			$("#pwd").focus();
			return cook.substring(offset,end);
		}else{
			return "";
		}
	}else{
		return "";
	}
}

function getLogin(){
	// userid 쿠키에서 id 값을 가져온다.
	document.login_check.id.value = unescape(readCookie("userid"));
}


window.onload= function(){
	getLogin();
}

$(document).ready(function(){
	$("#forgot").on("click", function(){
		alert("AirGuard K 앱을 통해 아이디와 비밀번호를 찾을 수 있습니다.");
	});

	$("#signup").on("click", function(){
		alert("AirGuard K 앱을 통해 스테이션을 등록하고 가입할 수 있습니다.");
	});
});