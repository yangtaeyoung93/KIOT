/**
 * 시스템관리 > 장비 관리 상세
 */
function goExec() {
	var deviceModelIdx = $("#deviceModelIdx option:selected").val();
	var serialNum = $("#serialNum").val();
	var productDt = $("#productDt").val()

	if (!deviceModelIdx) {
		alert("장비 모델을 선택해주세요.");
		$("#device_model_idx").focus();
		return false;
	}
	
	if (!serialNum) {
		alert("시리얼번호를 입력해주세요.");
		$("#serial_num").focus();
		return false;
	}
}

function createData() {
	var deviceIdx = $('#deviceIdx').val();
	var deviceModelIdx = $('#deviceModelIdx').val();
	var serialNum = $('#serialNum').val();
	var productDt = $('#productDt').val();
	// var testYn = $('input[name="testYn"]:checked').val();
	var deviceTypeIdx = $("#deviceModelIdx option:selected").attr("data-value");
	var chk_serial = $("#chk_serial").val();
	
	if(!serialNum){
		alert("시리얼번호를 입력해 주세요.");
		$("#serialNum").focus()
		return false;
	}
	
	if(!deviceIdx){
		if(chk_serial == 0){
			alert("시리얼 중복 체크를 하시기 바랍니다.");
			$("#chk_serial").focus();
			return false;
		}
	}

	/*if(serialNum.length > 8){
		alert("최대 8자리까지 입력해주세요.");
		return false;
	}*/
	
	var ob = {
		idx : deviceIdx,
		deviceModelIdx : deviceModelIdx,
		serialNum : serialNum,
		productDt : productDt,
		// testYn : testYn,
		deviceTypeIdx : deviceTypeIdx
	}

	return ob;
}

function goList() {
	location.href = "/system/device/list/" + $('#deviceType').val();
}

function insertDevice() {
	goExec();
	obj = createData();

	$.ajax({
		method : "POST",
		url : "/system/device/post",
		data : JSON.stringify(obj),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			alert("등록 되었습니다.");
			location.reload();
		}
	});
}

function detailCheck() {
	var hiTestYn = $('#hiTestYn').val();
	if (hiTestYn) {
		$('input:radio[name=testYn]:input[value=' + hiTestYn + ']').attr("checked", true);
	}
}

function updateDeviec() {
	obj = createData();

	$.ajax({
		method : "PUT",
		url : "/system/device/put",
		data : JSON.stringify(obj),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			alert("수정 되었습니다.");
			location.reload();
		}
	});
}

function datePickerSetting() {
	$.datepicker.regional['kr'] = {
		    monthNames: ['1 월','2 월','3 월','4 월','5 월','6 월','7 월','8 월','9 월','10 월','11 월','12 월'], 
		    monthNamesShort: ['1 월','2 월','3 월','4 월','5 월','6 월','7 월','8 월','9 월','10 월','11 월','12 월'], 
		    dayNames: ['월요일','화요일','수요일','목요일','금요일','토요일','일요일'], // 요일 텍스트 설정
		    dayNamesShort: ['월','화','수','목','금','토','일'],
			dayNamesMin: ['일','월','화','수','목','금','토'],
		};

	$.datepicker.setDefaults($.datepicker.regional['kr']);
}
function chkSerialNum(){
	var serialNum = $("#serialNum").val();
	
	if(!serialNum){
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("시리얼번호를  입력해주세요.");
		return false;
	}
	
	$.ajax({
		 url:"/check/serialNum",
		 type:"GET",
		 async : false,
		 data : "serialNum="+serialNum,
		 success : function (param) {
			//console.log(param);
			var result_val = param.resultCode;
			if(result_val == 1){
				$("#chk_txt").css("color","#28a745");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("사용가능한 시리얼 입니다.");
				$("#chk_serial").val("1");
				return false;
			}else{
				$("#chk_txt").css("color","red");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("중복되는 시리얼 입니다. 다시 확인하여 주세요.");
				$("#chk_serial").val("0");
				return false;
			}
		 }
	})
}
$().ready(function() {
	var h_idx = $("#deviceIdx").val();
	if(h_idx){
		$("#deviceModelIdx").attr("disabled",true);
		$("#serialNum").attr("readonly",true);
		//$("#chk_serial_btn").hide();
	}else{
		$("#deviceModelIdx").attr("disabled",false);
		$("#serialNum").attr("readonly",false);
		$("#chk_serial").val("0");
		//$("#chk_serial_btn").show();
		$("#test_yn1").attr("checked",true);
		$("#serialNum").blur(function(){
			chkSerialNum();
		})
	}
	
	datePickerSetting();

	$("#productDt").datepicker({
		dateFormat: 'yy-mm-dd'
	})

	$("#datepicker_ic").click(function(){
		$("#productDt").focus();
	})

	// detailCheck();
	
	$('#insertBtn').click(function() {
		insertDevice();
	})

	$('#updateBtn').click(function() {
		updateDeviec();
	})
})