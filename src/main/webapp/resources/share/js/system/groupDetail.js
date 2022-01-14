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
	$("#notUseMember").html("");
	$("#UseMember").html("");

	$.ajax({
		 url:"/system/group/members",
		 type:"GET",
		 success : function (param) {
			for (var i = 0; i < param.length; i++) {
				var data_idx = param[i].groupIdx;
				var data_mem_idx = param[i].memberIdx; 
				var data_use_id = param[i].userId;

				if (data_idx == 0) {
					memberSelectHtml += "<option value='" + data_mem_idx + "'>" + data_use_id + "</option>";
					not_use_html += "<div class='row col-12 mt10 userDiv' id='Member"+data_mem_idx+"' style='height: 20px;line-height: 20px;'>";
					not_use_html += "<input type='checkbox' class='UserMembers mr10' name='UserMembers' id='chkMember"+data_mem_idx+"' value='"+data_mem_idx+"' style='width:20px;height:20px;' data-cartNum='"+data_mem_idx+"'/>";
					not_use_html += "<label for='chkMember"+data_mem_idx+"'>"+data_use_id+"</label>";
					not_use_html += "</div>";

					$("#notUseMember").html(not_use_html);

				} else {
					if (groupIdx == data_idx) {
						memberSelectHtml += "<option selected value='" + data_mem_idx + "'>" + data_use_id + "</option>";
						use_html += "<div class='row col-12 mt10 userDiv' id='Member"+data_mem_idx+"' style='height: 20px;line-height: 20px;'>";
						use_html += "<input type='checkbox' class='UserMembers mr10' name='UserMembers' id='chkMember"+data_mem_idx+"' value='"+data_mem_idx+"'style='width:20px;height:20px;' data-cartNum='"+data_mem_idx+"'/>";
						use_html += "<label for='chkMember"+data_mem_idx+"'>"+data_use_id+"</label>";
						use_html += "</div>";
						$("#UseMember").html(use_html);
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
	var notuseMember = $("#notUseMember > .userDiv").length;
	var useMember = $("#UseMember > .userDiv").length;

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
	var groupId = $("#groupId").val();
	
	if(!groupId){
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("아이디를 입력해주세요.");
		return false;
	}

	if (!re_email.test(groupId)) {
		$("#chk_txt").css("color","red");
		$("#chk_txt").css("font-weight","bold");
		$("#chk_txt").text("이메일 형식이 아닙니다.");
		return false;
	}

	$.ajax({
		 url:"/check/groupId",
		 type:"GET",
		 async : false,
		 data : "groupId="+groupId,
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
	var groupIdx = $("#groupIdx").val();

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
		$("#notUseMember > div > input[name='UserMembers']:checked").each(function() {
			var memberIdx = $(this).val();
			$("#UseMember").append($("#Member"+memberIdx));
		});	
		autoheight();
	})
	$("#delGroupMember").click(function(){
		var html = "";
		
		$("#UseMember > div > input[name='UserMembers']:checked").each(function() {
			var memberIdx = $(this).val();
			$("#notUseMember").append($("#Member"+memberIdx));
		});	
		autoheight();
	})

	$('#listBtn').click(function() {
		location.href="/system/group/list";
	})

	$("#chkGroupPw").click(function(){
		var chkPw = $("input:checkbox[id='chkGroupPw']").is(":checked");
		if(chkPw == true){
			$("#groupPw").attr("disabled",false);
		}else{
			$("#groupPw").attr("disabled",true);
		}
	})

	$('#insertBtn').click(function() {
		var groupCustomUrl = "";

		var customUrlList = Array();
	    var obj = document.getElementsByName("groupCustomUrl");
	    for (var i = 0; i < obj.length; i++) {
	    	if (document.getElementsByName("groupCustomUrl")[i].value != "") {
	    		groupCustomUrl += (groupCustomUrl == "" ? "" : ",") + document.getElementsByName("groupCustomUrl")[i].value;
	    		console.log(i, groupCustomUrl);
	    	}
	    }

	    var groupId = $("#groupId").val();
		var groupPw = $("#groupPw").val();
		var groupEmail = $("#groupEmail").val();
		var groupPhoneNumber = $("#groupPhoneNumber").val();
		var groupName = $("#groupName").val();
		var groupCompanyName = $("#groupCompanyName").val();
		var groupDepartName = $("#groupDepartName").val();
		var useYn = "Y";
		var memberArr= new Array(); 
		var use_GroupMember_len = $("#UseMember > div > input:checkbox[name='UserMembers']").length;	
		var chkId = $("#chk_id").val();

		if(chkId == 0){
			alert("아이디 중복 체크를 하시기 바랍니다.");
			$("#groupId").focus();
			return false;
		}

		$('.duallistbox').each(function() {
			memberArr = $(this).val();
		});

		$("#UseMember > div > .UserMembers").each(function() {
			memberArr.push($(this).attr("data-cartNum"));
		});

		if(!groupId){
			alert("그룹아이디를 입력해주세요.");
			$("#groupId").focus();
			return false;
		}

		if (!groupPw) {
			alert("비밀번호를 입력해주세요.");
			$("#groupPw").focus();
			return false;
		} else {
			if (re_script.test(groupPw)) {
				alert("스크립트는 사용할 수 없습니다.");
				groupPw = groupPw.replace(re_script,"");
				$("#groupPw").focus();
				return false;
			}

			if (re_tag.test(groupPw)) {
				alert("태그를 사용할 수 없습니다.");
				groupPw = groupPw.replace(re_tag,"");
				$("#groupPw").focus();
				return false;
			}

			if (re_blank.test(groupPw)) {
				alert("공백만 사용할 수 없습니다.");
				$("#groupPw").focus();
				return false;
			}

			if (re_blank1.test(groupPw)) {
				alert("공백을 사용할 수 없습니다.");
				$("#groupPw").focus();
				return false;
			}
		}

		if (!groupName) {
			alert("그룹명을 입력해주세요.");
			$("#groupName").focus();
			return false;
		}

		if (!groupCompanyName) {
			alert("고객사를 입력해주세요.");
			$("#groupCompanyName").focus();
			return false;
		}

		if (!groupDepartName) {
			alert("고객담당자를 입력 해주세요.");
			$("#groupDepartName").focus();
			return false;
		}

		if (!groupPhoneNumber) {
			alert("전화번호를 입력 해주세요.");
			$("#groupPhoneNumber").focus();
			return false;
		}

		var obj = {
			groupId : groupId,
			groupPw : groupPw,
			groupName : groupName,
			groupEmail : groupEmail,
			groupPhoneNumber : groupPhoneNumber,
			groupCustomUrl : groupCustomUrl,
			memberIdxs : memberArr,
			groupCompanyName : groupCompanyName,
			groupDepartName : groupDepartName,
			useYn : useYn
		};

		$.ajax({
			method : "POST",
			url : "/system/group/post",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("등록 되었습니다.");
				location.reload();
			}
		});
	})

	$('#updateBtn').click(function() {
		var groupCustomUrl = "";

		var customUrlList = Array();
	    var obj = document.getElementsByName("groupCustomUrl");
	    for (var i = 0; i < obj.length; i++) {
	    	if (document.getElementsByName("groupCustomUrl")[i].value != "") {
	    		groupCustomUrl += (groupCustomUrl == "" ? "" : ",") + document.getElementsByName("groupCustomUrl")[i].value;
	    		console.log(i, groupCustomUrl);
	    	}
	    }

	    var groupId = $("#groupId").val();
		var groupPw = $("#groupPw").val();
		var groupEmail = $("#groupEmail").val();
		var groupPhoneNumber = $("#groupPhoneNumber").val();
		var groupName = $("#groupName").val();
		var groupCompanyName = $("#groupCompanyName").val();
		var groupDepartName = $("#groupDepartName").val();
		var memberArr= new Array(); 
		var useYn = "Y";
		var chkPw = $("input:checkbox[id='chkGroupPw']").is(":checked");
		var use_GroupMember_len = $("#UseMember > div > input:checkbox[name='UserMembers']").length;	

		$('.duallistbox').each(function() {
			memberArr = $(this).val();
		});

		$("#UseMember > div > .UserMembers").each(function() {
			memberArr.push($(this).attr("data-cartNum"));
		});

		console.log(memberArr);
		if(!groupId){
			alert("그룹아이디를 입력 해주세요.");
			$("#groupId").focus();
			return false;
		}

		if(chkPw == true){
			if(!groupPw){
				alert("비밀번호를 입력 해주세요.");
				$("#groupPw").focus();
				return false;
			}else{
				if(re_script.test(groupPw)){
					alert("스크립트는 사용할 수 없습니다.");
					groupPw = groupPw.replace(re_script,"");
					$("#groupPw").focus();
					return false;
				}

				if(re_tag.test(groupPw)){
					alert("태그를 사용할 수 없습니다.");
					groupPw = groupPw.replace(re_tag,"");
					$("#groupPw").focus();
					return false;
				}
				
				if(re_blank.test(groupPw)) {
					alert("공백만 사용할 수 없습니다.");
					$("#groupPw").focus();
					return false;
				}
				
				
				if(re_blank1.test(groupPw)) {
					alert("공백을 사용할 수 없습니다.");
					$("#groupPw").focus();
					return false;
				}
			}
		}

		if(!groupName){
			alert("그룹명을 입력해주세요.");
			$("#groupName").focus();
			return false;
		}

		if(!groupCompanyName){
			alert("고객사를 입력해주세요.");
			$("#groupCompanyName").focus();
			return false;
		}

		if (!groupDepartName) {
			alert("고객담당자를 입력 해주세요.");
			$("#groupDepartName").focus();
			return false;
		}

		if (!groupPhoneNumber) {
			alert("전화번호를 입력 해주세요.");
			$("#groupPhoneNumber").focus();
			return false;
		}

		var obj = { 
			idx : $("#groupIdx").val(),
			groupPw : groupPw,
			groupId : groupId,
			groupName : groupName,
			groupEmail : groupEmail,
			groupPhoneNumber : groupPhoneNumber,
			groupCustomUrl : groupCustomUrl,
			memberIdxs : memberArr,
			groupCompanyName : groupCompanyName,
			groupDepartName : groupDepartName,
			useYn : useYn
		}

		$.ajax({
			method : "PUT",
			url : "/system/group/put",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("수정 되었습니다.");
				location.reload();
			}
		});
	})
})