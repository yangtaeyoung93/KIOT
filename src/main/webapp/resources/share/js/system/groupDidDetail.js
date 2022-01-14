/**
 * 시스템관리 > 그룹 DID 상세 / 수정
 */
var obj = new Object();

var fieldGroupDidIdx;

function getDidinfo(idx,groupIdx){
	fieldGroupDidIdx = idx;
	$('#didUpdateBtn').show();
	readMode();
	$("#chk_txt").text("");
	var didName = $("#chkDid"+idx).attr("data-didName");
	var didCode = $("#chkDid"+idx).attr("data-didCode");
	var didMemberCnt = $("#chkDid"+idx).attr("data-memberCnt");
	var useYn = $("#chkDid"+idx).attr("data-useYn");
	
	var didMemberHtml = "";
	$("#memberDeleteBtn").hide();
	
	$.ajax({
		 url:"/system/group/did/ajaxGroupDidMemberList",
		 type:"GET",
		 async : false,
		 data : "idx="+idx,
		 success : function (param) {
			//console.log(param);
			for(var i = 0; i < param.length; i++){
				var groupIdx = param[i].groupIdx;
				var groupDidIdx = param[i].groupDidIdx;
				var memberIdx = param[i].memberIdx;
				var userId = param[i].userId;
				
				didMemberHtml += "<div class='row col-12 mt10 useMember' id='useMember"+memberIdx+"' style='height: 38px;line-height: 35px;'>";
				didMemberHtml += "<span class='userId mr10' name='userId' id='chkuseMember"+memberIdx+"' value='"+memberIdx+"' style='height:20px;' data-groupDidIdx = '"+groupDidIdx+"'>"+userId+"</span>";
				didMemberHtml += "<input type='button' class='btn btn-primary' value='삭제' onClick='MemberDelete("+memberIdx+");'/>";
				didMemberHtml += "</div>";
			}
			$("#memberDiv").html(didMemberHtml);
			$("#txtDidName").text(didName);
			$("#txtDidCode").text(didCode);
			$("#txtUseYn").text(useYn);
			$("#txt_memberCnt").text(didMemberCnt);
			$("#memberDeleteBtn").show();
			getGroupMemberList();
		 }
	})
	
}

function MemberDelete(val){
	var chkMember = val;
	var groupDidIdx = $("#chkuseMember"+chkMember).attr("data-groupDidIdx");
	var confirmVal = confirm("정말 삭제하시겠습니까?");
	var memberId = $("#chkuseMember" + chkMember).text();
	if (confirmVal) {
		if(!chkMember){
			alert("삭제할 사용자를 선택해 주세요");
			return false;
		}
		
		 $.ajax({
			 url:"/system/group/did/member/delete",
			 type:"DELETE",
			 data : "memberIdx="+chkMember+"&groupDidIdx="+groupDidIdx,
			 success : function (param) {
				 alert("삭제 되었습니다.");
				 $('#useMember' + chkMember).remove();
				 //$('#memberDiv2').append('<div class="row col-12 mt10 userDiv" id="GroupDidMember' + chkMember + '" style="height: 38px;line-height: 35px;"><span type="checkbox" class="useGroupMember mr10" name="useGroupMember" id="chkGroupMember' + chkMember + '" style="height:20px;" data-cartnum="' + chkMember + '" data-groupidx="' + groupDidIdx + '">' + memberId + '</span><input type="button" class="btn btn-primary" value="추가" onclick="addMember(' + chkMember + ');"></div>');
				 //location.reload();
			 }
		})
	}
}

function getGroupMemberList(){
	var groupIdx = $("#h_idx").val();
	var GroupMemberHtml = "";
	
	$("#memberDiv2").html("");
	$.ajax({
		 url:"/system/group/did/ajaxGroupMemberList",
		 type:"GET",
		 data : "idx="+groupIdx,
		 async : false,
		 success : function (param) {
			 console.log(param);
			 for(var i = 0; i < param.length; i++){
				var memberIdx = param[i].memberIdx;
				var memberId = param[i].userId;
				GroupMemberHtml += "<div class='row col-12 mt10 userDiv' id='GroupDidMember"+memberIdx+"' style='height: 38px;line-height: 35px;'>";
				GroupMemberHtml += "<span type='checkbox' class='useGroupMember mr10' name='useGroupMember' id='chkGroupMember"+memberIdx+"' style='height:20px;' data-cartNum='"+memberIdx+"' data-GroupIdx='"+groupIdx+"'>"+memberId+"</span>";
				GroupMemberHtml += "<input type='button' class='btn btn-primary' value='추가' onClick='addMember("+memberIdx+");'/>";
				GroupMemberHtml += "</div>";
					
				 $("#memberDiv2").html(GroupMemberHtml);
			 }
			 autoheight();
		 }
	})
}

function addMember(val){
	//group_idx, 
    //group_did_idx, 
    //member_idx
	var groupIdx = $("#h_idx").val();
	var groupDidIdx = $('input:radio[name="chkDid"]:checked').val();
	var memberIdx = val;
	var memberId = $("#chkGroupMember" + memberIdx).text();
	var obj = {
		memberIdx : memberIdx, //사용자 계정
		groupDidIdx : groupDidIdx, //장비idx
		groupIdx : groupIdx
	}
	
	$.ajax({
		 url:"/system/group/did/member/insert",
		 type:"POST",
		 data : JSON.stringify(obj),
		 contentType : "application/json; charset=utf-8",
		 success : function (param) {
			console.log(param);
			if (param.resultCode == 1) {
				alert("추가 되었습니다.");
				//$('#GroupDidMember' + memberIdx).remove();
				$('#memberDiv').append('<div class="row col-12 mt10 useMember" id="useMember' + memberIdx +'" style="height: 38px;line-height: 35px;"><span class="userId mr10" name="userId" id="chkuseMember' + memberIdx + '" value="' + memberIdx + '" style="height:20px;" data-groupdididx="' + groupDidIdx + '">' + memberId + '</span><input type="button" class="btn btn-primary" value="삭제" onclick="MemberDelete(' + memberIdx + ');"></div>');
			} else {
				alert("(" + param.inputValue + ") 사용자는 이미 포함 되어 있습니다.");
			}
		 }
	})
}
function autoheight(){
	var useGroup = $("#memberDiv > div").length;
	var notUseGroupMember = $("#memberDiv2 > div").length;
	
	if(useGroup < 4){
		$("#memberDiv").css("height","auto");
		$("#memberDiv").css("overflow-x","none");
	}else{
		$("#memberDiv").css("height","450px");
		$("#memberDiv").css("overflow-x","hidden");
		
	}
	
	if(notUseGroupMember < 4){
		$("#memberDiv2").css("height","auto");
		$("#memberDiv2").css("overflow-x","none");
	}else{
		$("#memberDiv2").css("height","135px");
		$("#memberDiv2").css("overflow-x","hidden");
		
	}
	
}


$().ready(function() {
	var did_cnt = $("#didDiv > .did").length;
	var memberTotal = 0;
	$("#txtDidCnt").text(did_cnt);
	$("#memberDeleteBtn").hide();
	$('#didUpdateBtn').hide();
	$('#checkArea').hide();
	
	$("input[name='chkDid']").each(function() {
		var memberCnt = $(this).attr("data-memberCnt");
		memberTotal = parseInt(memberTotal) + parseInt(memberCnt);
	})
	
	$("#txtMemberCnt").text(memberTotal);
	
	$("#groupDeleteBtn").click(function(){
		var chkDid = $('input:radio[name="chkDid"]:checked').val();
		var confirmVal = confirm("정말 삭제하시겠습니까?");
		
		if (confirmVal) {
			if(!chkDid){
				alert("삭제할 DID를 선택해 주세요");
				return false;
			}
			
			 $.ajax({
				 url:"/system/group/did/delete",
				 type:"DELETE",
				 data : "idx="+chkDid,
				 success : function (param) {
					 alert("삭제 되었습니다.");
					 location.reload();
				 }
			})
		}
	})

	$('#listBtn').click(function() {
		location.href="/system/group/did/list";
	})

	
})

function updateMode() {
	var txtDidName = $('#txtDidName').text();
	$("#txtDidName").html("<input type='text' class='form-control group-attribute' placeholder='" + txtDidName + "'/>");

	$("#didUpdateBtn").val("취소");
	$("#didUpdateBtn").attr("onclick", "readMode()");
	$('#updateSave').css("display", "inline-block");

	$('#checkArea').show();
}

function readMode() {
	var txtDidName = $('#txtDidName').children('input').attr('placeholder');
	$("#txtDidName").html(txtDidName);

	$("#didUpdateBtn").val("수정");
	$("#didUpdateBtn").attr("onclick", "updateMode()");
	$('#updateSave').css("display", "none");
	
	$('#checkArea').hide();
}

function updateSave() {
	var txtDidName = $('#txtDidName').children('input').val();
	
	var chk_code = $("#chk_code").val();
	
	if(!txtDidName){
		alert("DID명을 입력하세요.");
		$("#didName").focus();
		return false;
	}

	obj = 
	{
		idx : fieldGroupDidIdx,
		didName : txtDidName
	}

	$.ajax({
		method : "PUT",
		url : "/system/group/did/put",
		data : JSON.stringify(obj),
		contentType : "application/json; charset=utf-8",
		success : function(d) {
			alert("수정 되었습니다.");
			location.reload();
		}
	});
}
