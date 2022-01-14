/**
 * 시스템관리 > 그룹 DID 등록
 */
var obj = new Object();

function getGroupList(){
	$("#useGroup").html("");
	$("#notUseGroupMember").html("");
	$("#UseGroupDidMember").html("");
	
	var GroupHtml = "";
	$.ajax({
		 url:"/system/group/did/ajaxGroupList",
		 type:"GET",
		 success : function (param) {
			 var didGroupIdx = $("#idx").val();
			 var h_groupidx = $("#h_groupidx").val();
			 var disabled = "";
			 for(var i = 0; i < param.length; i++){
				var groupIdx = param[i].idx;
				var groupName = param[i].groupName;
				
				if(didGroupIdx){
					if(groupIdx == h_groupidx){
						disabled = "";
					}else{
						disabled = "disabled";
					}
				}
				GroupHtml += "<div class='row col-12 mt10 userDiv' id='Group"+groupIdx+"' style='height: 20px;line-height: 20px;'>";
				GroupHtml += "<input type='radio' class='userGroup mr10' name='userGroup' id='chkGroup"+groupIdx+"' value='"+groupIdx+"' style='width:20px;height:20px;' data-cartNum='"+groupIdx+"' onClick='getGroupMemberList("+groupIdx+");' "+disabled+"/>";
				GroupHtml += "<label for='chkGroup"+groupIdx+"'>"+groupName+"</label>";
				GroupHtml += "</div>";
					
				$("#useGroup").html(GroupHtml);
				
				if(didGroupIdx){
					if(groupIdx == h_groupidx){
						$("#chkGroup"+groupIdx).attr("checked",true);
						//$("#chkGroup"+groupIdx).attr("disabled",false);
					}else{
						$("#chkGroup"+groupIdx).attr("checked",false);
						//$("#chkGroup"+groupIdx).attr("disabled",true);
					}
				}
			 }
			 
			 autoheight();
			 //alert($("input[name='userGroup']").length);
			 
		 }
	})
	
	
}
function getGroupMemberList(groupIdx){
	var GroupMemberHtml = "";
	var didGroupIdx = $("#idx").val();
	var h_groupidx = $("#h_groupidx").val();
	var GroupDidMemberHtml = "";
	var groupDidmemberCnt = 0;
	$("#UseGroupDidMember").html("");
	$("#notUseGroupMember").html("");
	
	$.ajax({
		 url:"/system/group/did/ajaxGroupDidMemberList",
		 type:"GET",
		 data : "idx="+didGroupIdx,
		 async : false,
		 success : function (param) {
			//console.log(param);
			for(var i = 0; i < param.length; i++){
				var memberIdx = param[i].memberIdx;
				var memberId = param[i].userId;
				GroupDidMemberHtml += "<div class='row col-12 mt10 userDiv' id='use_GroupDidMember"+memberIdx+"' style='height: 20px;line-height: 20px;'>";
				GroupDidMemberHtml += "<input type='checkbox' class='use_GroupDidMember mr10' name='use_GroupMember' id='use_chkGroupMember"+memberIdx+"' value='"+memberIdx+"' style='width:20px;height:20px;' data-cartNum='"+memberIdx+"' data-GroupIdx='"+groupIdx+"'/>";
				GroupDidMemberHtml += "<label for='use_chkGroupDidMember"+memberIdx+"'>"+memberId+"</label>";
				GroupDidMemberHtml += "</div>";
				
				$("#UseGroupDidMember").html(GroupDidMemberHtml);
				
			}
			 groupDidmemberCnt = param.length;
			 autoheight();
		 }
	})
	$.ajax({
		 url:"/system/group/did/ajaxGroupMemberList",
		 type:"GET",
		 data : "idx="+groupIdx,
		 async : false,
		 success : function (param) {
			 //console.log(param);
			 for(var i = 0; i < param.length; i++){
				var memberIdx = param[i].memberIdx;
				var memberId = param[i].userId;
				GroupMemberHtml += "<div class='row col-12 mt10 userDiv' id='GroupDidMember"+memberIdx+"' style='height: 20px;line-height: 20px;'>";
				GroupMemberHtml += "<input type='checkbox' class='useGroupMember mr10' name='useGroupMember' id='chkGroupMember"+memberIdx+"' value='"+memberIdx+"' style='width:20px;height:20px;' data-cartNum='"+memberIdx+"' data-GroupIdx='"+groupIdx+"'/>";
				GroupMemberHtml += "<label for='chkGroupMember"+memberIdx+"' id='chkGroupMemberLabel"+memberIdx+"'>"+memberId+"</label>";
				GroupMemberHtml += "</div>";
					
				 $("#notUseGroupMember").html(GroupMemberHtml);
			 }
			 autoheight();
		 }
	})
	
}

function getUseGroupDidMember(){
	

}

function autoheight(){
	var useGroup = $("#useGroup > .userDiv").length;
	var notUseGroupMember = $("#notUseGroupMember > .userDiv").length;
	var UseGroupDidMember = $("#UseGroupDidMember").length;
	
	if(useGroup < 15){
		$("#userArea1").css("height","auto");
		$("#userArea1").css("overflow-x","none");
	}else{
		$("#userArea1").css("height","450px");
		$("#userArea1").css("overflow-x","hidden");
		
	}
	
	if(notUseGroupMember < 4){
		$("#userArea2").css("height","auto");
		$("#userArea2").css("overflow-x","none");
	}else{
		$("#userArea2").css("height","135px");
		$("#userArea2").css("overflow-x","hidden");
		
	}
	
	if(UseGroupDidMember < 4){
		$("#userArea3").css("height","auto");
		$("#userArea3").css("overflow-x","none");
	}else{
		$("#userArea3").css("height","135px");
		$("#userArea3").css("overflow-x","hidden");
		
	}
}

$().ready(function() {
	var didGroupIdx = $("#idx").val();
	var h_groupidx = $("#h_groupidx").val();
	 
	getGroupList();
	
	if(didGroupIdx){
		getGroupMemberList(h_groupidx);
		//getUseGroupDidMember();
		$("#chk_didCode").hide();
	}else{
		$("#chk_didCode").show();
		$("#chk_code").val("0");
	}
	
	$("#addGroupDidMember").click(function(){
		var html = "";

		$("#notUseGroupMember > div > input[name='useGroupMember']:checked").each(function() {
			var memberIdx = $(this).val();
			var memberId = $("#chkGroupMemberLabel"+memberIdx).text();
			var groupIdx = $("input:radio[name='userGroup']:checked").val();
			
			//var test = $("#GroupDidMember"+memberIdx).clone();
			//alert(test);
			html += "<div class='row col-12 mt10 userDiv' id='use_GroupDidMember"+memberIdx+"' style='height: 20px;line-height: 20px;'>";
			html += "<input type='checkbox' class='use_GroupDidMember mr10' name='use_GroupMember' id='use_chkGroupMember"+memberIdx+"' value='"+memberIdx+"' style='width:20px;height:20px;' data-cartNum='"+memberIdx+"' data-GroupIdx='"+groupIdx+"'/>";
			html += "<label for='use_chkGroupDidMember"+memberIdx+"' id='use_chkGroupMember"+memberIdx+"'>"+memberId+"</label>";
			html += "</div>";
		});	
		$("#UseGroupDidMember").append(html);
		autoheight();
	})
	
	$("#delGroupDidMember").click(function(){
		var html = "";
		var chk_len = $("#UseGroupDidMember > div > input[name='use_GroupMember']:checked").length;
		if(chk_len == 0){
			alert("최소 1개 이상 선택해 주세요.");
			return false;
		}
		
		$("#UseGroupDidMember > div > input[name='use_GroupMember']:checked").each(function() {
			var memberIdx = $(this).val();
			$("#use_GroupDidMember"+memberIdx).remove();
			//$("#notUseGroupMember").append($("#GroupDidMember"+memberIdx));
		});	
		autoheight();
	})
	
	$('#listBtn').click(function() {
		location.href="/system/group/did/list";
	})
	
	
	
	$('#insertBtn').click(function() {
		var didName = $("#didName").val();
		var userGroupIdx = $("input:radio[name='userGroup']:checked").val();
		var groupDidMemberArr= new Array(); 
		var use_GroupMember_len = $("input:checkbox[name='use_GroupMember']").length;	
		var chk_code = $("#chk_code").val("1");
		
		if(!userGroupIdx){
			alert("그룹을 선택해 주세요.");
			return false;
		}
		
		if(!didName){
			alert("DID명을 입력하세요.");
			$("#didName").focus();
			return false;
		}
		
		if(use_GroupMember_len == 0){
			alert("1명 이상의 사용자를 선택해 주세요 ");
			return false;
		}
		
		$("#UseGroupDidMember > div > .use_GroupDidMember").each(function() {
			groupDidMemberArr.push($(this).attr("data-cartNum"));
		});
		//console.log(groupDidMemberArr);
		var obj = {
			groupIdx : userGroupIdx,
			didName : didName,
			members : groupDidMemberArr
		}
		//console.log(obj);
		//return false;
		$.ajax({
			method : "POST",
			url : "/system/group/did/post",
			data : JSON.stringify(obj),
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				alert("등록 되었습니다.");
				location.reload();
			}
		});
	});
	$('#updateBtn').click(function() {
		var idx = $("#idx").val();
		var didName = $("#didName").val();
		var userGroupIdx = $("input:radio[name='userGroup']:checked").val();
		var groupDidMemberArr= new Array(); 
		var use_GroupMember_len = $("input:checkbox[name='use_GroupMember']").length;	
		
		if(use_GroupMember_len == 0){
			alert("최소 1개 이상 선택해 주세요 ");
			return false;
		}
		
		if(!didName){
			alert("DID명을 입력하세요.");
			$("#didName").focus();
			return false;
		}
		
		$("#UseGroupDidMember > div > .use_GroupDidMember").each(function() {
			groupDidMemberArr.push($(this).attr("data-cartNum"));
		});
		//console.log(groupDidMemberArr);
		var obj = {
			didGroupIdx : idx,
			groupIdx : userGroupIdx,
			didName : didName,
			members : groupDidMemberArr
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
	})
})