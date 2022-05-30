/**
 * 시스템관리 > 그룹 게정 등록/수정
 */
var obj = new Object();
var customUrlFormIdx = 1;

function addCustomUrlForm() {
	customUrlFormIdx++;
	var addItem = "<input type='text' class='form-control group-attribute' name='groupCustomUrl' id='groupCustomUrl" + customUrlFormIdx + "' placeholder='내용을 입력해 주세요'>";
	$('#customUrlForm').append(addItem);
}

function cancelCustomUrlForm() {
	$('#groupCustomUrl' + customUrlFormIdx).remove();
	customUrlFormIdx--;
}

function validate() {
    if (event.keyCode >=48 && event.keyCode <= 57 ) {
        return true;
    } else {
        event.returnValue = false;
    }
}

function getMembers(groupIdx) {
	var not_use_html = "";
	var use_html = "";

	var memberSelectHtml = "";
	$("#notUseGroup").html("");
	$("#UseGroup").html("");

	$.ajax({
		 url:"/system/master/groups",
		 type:"GET",
		 success : function (param) {
			for (var i = 0; i < param.length; i++) {
				var data_idx = param[i].masterIdx;
				var data_mem_idx = param[i].groupIdx; 
				var data_use_id = param[i].groupId;

				if (data_idx == 0) {
					memberSelectHtml += "<option value='" + data_mem_idx + "'>" + data_use_id + "</option>";
					not_use_html += "<div class='row col-12 mt10 userDiv' id='Member"+data_mem_idx+"' style='height: 20px;line-height: 20px;'>";
					not_use_html += "<input type='checkbox' class='UserMembers mr10' name='UserMembers' id='chkMember"+data_mem_idx+"' value='"+data_mem_idx+"' style='width:20px;height:20px;' data-cartNum='"+data_mem_idx+"'/>";
					not_use_html += "<label for='chkMember"+data_mem_idx+"'>"+data_use_id+"</label>";
					not_use_html += "</div>";

					$("#notUseGroup").html(not_use_html);

				} else {
					if (groupIdx == data_idx) {
						memberSelectHtml += "<option selected value='" + data_mem_idx + "'>" + data_use_id + "</option>";
						use_html += "<div class='row col-12 mt10 userDiv' id='Member"+data_mem_idx+"' style='height: 20px;line-height: 20px;'>";
						use_html += "<input type='checkbox' class='UserMembers mr10' name='UserMembers' id='chkMember"+data_mem_idx+"' value='"+data_mem_idx+"'style='width:20px;height:20px;' data-cartNum='"+data_mem_idx+"'/>";
						use_html += "<label for='chkMember"+data_mem_idx+"'>"+data_use_id+"</label>";
						use_html += "</div>";
						$("#UseGroup").html(use_html);
					}
				}
			}

			$('#memberSelect').html(memberSelectHtml);
			$('.duallistbox').bootstrapDualListbox('refresh');
			autoheight();
		 }
	})
}



function autoheight() {
	var notuseMember = $("#notUseGroup > .userDiv").length;
	var useMember = $("#UseGroup > .userDiv").length;

	if (notuseMember < 4) {
		$("#userArea1").css("height","auto");
		$("#userArea1").css("overflow-x","none");

	} else {
		$("#userArea1").css("height","135px");
		$("#userArea1").css("overflow-x","hidden");
	}
	
	if (useMember < 4) {
		$("#userArea2").css("height","auto");
		$("#userArea2").css("overflow-x","none");

	} else {
		$("#userArea2").css("height","135px");
		$("#userArea2").css("overflow-x","hidden");
	}
}

function chkGroupId(){
	var masterId = $("#masterId").val();
	
	if(!masterId){
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("아이디를 입력해주세요.");
		return false;
	}

	if (!re_email.test(masterId)) {
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("이메일 형식이 아닙니다.");
		return false;
	}

	$.ajax({
		 url:"/check/masterId",
		 type:"GET",
		 async : false,
		 data : "masterId="+masterId,
		 success : function (param) {
			var result_val = param.resultCode;
			if(result_val == 1){
				$("#chk_txt").css("color","#28a745");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("사용가능한 아이디 입니다.");
				$("#chk_id").val("1");
				return false;
			}else{
				$("#chk_txt").css("color","red");
				$("#chk_txt").css("font-weight","bold");
				$("#chk_txt").text("중복되는 아이디 입니다. 다시 확인하여 주세요.");
				$("#chk_id").val("0");
				return false;
			}
		 }
	})
}

$().ready(function() {
	var groupIdx = $("#masterIdx").val();

    $('.duallistbox').bootstrapDualListbox({
    	height: "400px",
		"infoText" : "",
		"infoTextEmpty" : ""
    });

    $('#_helper1').css("height", "200px");
    if(!groupIdx){
		$("#groupId").attr("readonly",false);
		$("#chkpwDiv").hide();
		$("#groupPw").attr("disabled",false);
		$("#chk_groupId").show();
		$("#chk_id").val("0");
		$("#groupId").blur(function(){
			chkGroupId();
		});
	}else{
		$("#groupId").attr("readonly",true);
		$("#groupPw").attr("disabled",true);
		$("#chk_groupId").hide();
	}

    getMembers(groupIdx);
	$("#addGroupMember").click(function(){
		var html = "";
		$("#notUseGroup > div > input[name='UserMembers']:checked").each(function() {
			var memberIdx = $(this).val();
			$("#UseMember").append($("#Member"+memberIdx));
		});	
		autoheight();
	})
	$("#delMasterGroup").click(function(){
		var html = "";
		
		$("#UseGroup > div > input[name='UserMembers']:checked").each(function() {
			var memberIdx = $(this).val();
			$("#notUseMember").append($("#Member"+memberIdx));
		});	
		autoheight();
	})

	$('#listBtn').click(function() {
		location.href="/system/master/list";
	})

	$("#chkmasterPw").click(function(){
		var chkPw = $("input:checkbox[id='chkmasterPw']").is(":checked");
		if(chkPw == true){
			$("#masterPw").attr("disabled",false);
		}else{
			$("#masterPw").attr("disabled",true);
		}
	})

	$('#insertBtn').click(function() {

	    var masterId = $("#masterId").val();
		var masterPw = $("#masterPw").val();
		var masterEmail = $("#masterEmail").val();
		var masterPhoneNumber = $("#masterPhoneNumber").val();
		var masterName = $("#masterName").val();
		var masterCompanyName = $("#masterCompanyName").val();
		var groupDepartName = $("#groupDepartName").val();
		var useYn = "Y";
		var groupArr= new Array(); 
		var use_GroupMember_len = $("#UseGroup > div > input:checkbox[name='UserMembers']").length;	
		var chkId = $("#chk_id").val();

		if(chkId == 0){
			alert("아이디 중복 체크를 하시기 바랍니다.");
			$("#masterId").focus();
			return false;
		}

		$('.duallistbox').each(function() {
			groupArr = $(this).val();
		});

		$("#UseGroup > div > .UserMembers").each(function() {
			groupArr.push($(this).attr("data-cartNum"));
		});

		if(!masterId){
			alert("그룹아이디를 입력해주세요.");
			$("#masterId").focus();
			return false;
		}

		if (!masterPw) {
			alert("비밀번호를 입력해주세요.");
			$("#masterPw").focus();
			return false;
		} else {
			if (re_script.test(masterPw)) {
				alert("스크립트는 사용할 수 없습니다.");
				masterPw = masterPw.replace(re_script,"");
				$("#groupPw").focus();
				return false;
			}

			if (re_tag.test(masterPw)) {
				alert("태그를 사용할 수 없습니다.");
				masterPw = masterPw.replace(re_tag,"");
				$("#masterPw").focus();
				return false;
			}

			if (re_blank.test(masterPw)) {
				alert("공백만 사용할 수 없습니다.");
				$("#masterPw").focus();
				return false;
			}

			if (re_blank1.test(masterPw)) {
				alert("공백을 사용할 수 없습니다.");
				$("#masterPw").focus();
				return false;
			}
		}

		if (!masterName) {
			alert("그룹명을 입력해주세요.");
			$("#masterName").focus();
			return false;
		}

		if (!masterCompanyName) {
			alert("고객사를 입력해주세요.");
			$("#masterCompanyName").focus();
			return false;
		}

		if (!groupDepartName) {
			alert("고객담당자를 입력 해주세요.");
			$("#groupDepartName").focus();
			return false;
		}

		if (!masterPhoneNumber) {
			alert("전화번호를 입력 해주세요.");
			$("#masterPhoneNumber").focus();
			return false;
		}

		var obj = {
			masterId : masterId,
			masterPw : masterPw,
			masterName : masterName,
			masterEmail : masterEmail,
			masterPhoneNumber : masterPhoneNumber,
			memberIdxs : groupArr,
			masterCompanyName : masterCompanyName,
			groupDepartName : groupDepartName,
			useYn : useYn
		};

		$.ajax({
			method : "POST",
			url : "/system/master/post",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("등록 되었습니다.");
				location.reload();
			}
		});
	})

	$('#updateBtn').click(function() {

		

		var masterId = $("#masterId").val();
		var masterPw = $("#masterPw").val();
		var masterEmail = $("#masterEmail").val();
		var masterPhoneNumber = $("#masterPhoneNumber").val();
		var masterName = $("#masterName").val();
		var masterCompanyName = $("#masterCompanyName").val();
		var groupDepartName = $("#groupDepartName").val();
		var useYn = "Y";
		var groupArr= new Array(); 
		var chkPw = $("input:checkbox[id='chkmasterPw']").is(":checked");
		var use_GroupMember_len = $("#UseGroup > div > input:checkbox[name='UserMembers']").length;	

		$('.duallistbox').each(function() {
			groupArr = $(this).val();
		});

		$("#UseGroup > div > .UserMembers").each(function() {
			groupArr.push($(this).attr("data-cartNum"));
		});

		console.log(groupArr);
		if(!masterId){
			alert("그룹아이디를 입력 해주세요.");
			$("#masterId").focus();
			return false;
		}

		if(chkPw == true){
			if(!masterPw){
				alert("비밀번호를 입력 해주세요.");
				$("#masterPw").focus();
				return false;
			}else{
				if(re_script.test(masterPw)){
					alert("스크립트는 사용할 수 없습니다.");
					masterPw = masterPw.replace(re_script,"");
					$("#masterPw").focus();
					return false;
				}

				if(re_tag.test(groupPw)){
					alert("태그를 사용할 수 없습니다.");
					masterPw = masterPw.replace(re_tag,"");
					$("#masterPw").focus();
					return false;
				}
				
				if(re_blank.test(masterPw)) {
					alert("공백만 사용할 수 없습니다.");
					$("#masterPw").focus();
					return false;
				}
				
				
				if(re_blank1.test(masterPw)) {
					alert("공백을 사용할 수 없습니다.");
					$("#masterPw").focus();
					return false;
				}
			}
		}

		if(!masterName){
			alert("그룹명을 입력해주세요.");
			$("#masterName").focus();
			return false;
		}

		if(!masterCompanyName){
			alert("고객사를 입력해주세요.");
			$("#masterCompanyName").focus();
			return false;
		}

		if (!groupDepartName) {
			alert("고객담당자를 입력 해주세요.");
			$("#groupDepartName").focus();
			return false;
		}

		if (!masterPhoneNumber) {
			alert("전화번호를 입력 해주세요.");
			$("#masterPhoneNumber").focus();
			return false;
		}

		var obj = { 
			idx : $("#masterIdx").val(),
			masterId : masterId,
			masterPw : masterPw,
			masterName : masterName,
			masterEmail : masterEmail,
			masterPhoneNumber : masterPhoneNumber,
			groupIdxs : groupArr,
			masterCompanyName : masterCompanyName,
			groupDepartName : groupDepartName,
			useYn : useYn
		}

		$.ajax({
			type : "PUT",
			url : "/system/master/put",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("수정 되었습니다.");
				location.reload();
			}
		});
	})
})