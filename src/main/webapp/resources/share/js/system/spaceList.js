/**
 * 시스템관리 > 설치 공간 관리
 */

var oriStationName;

function initDataTableCustom() {
	var searchType = $("#searchType").val();
	var searchType2 = $("#searchType2").val();
	var sch_station_name = $("#sch_station_name").val();
	var searchUseYn = $('input[name="useYnRadio"]:checked').attr('r-data');
	
	var table = $('#spaceTable').DataTable({
		scrollCollapse: true,
		autoWidth: false,
		language : {
			"emptyTable" : "데이터가 없습니다.",
			"lengthMenu" : "페이지당 _MENU_ 개씩 보기",
			"info" : "현재 _START_ - _END_ / _TOTAL_건",
			"infoEmpty" : "데이터 없음",
			"infoFiltered" : "( _MAX_건의 데이터에서 필터링됨 )",
			"search" : "",
			"zeroRecords" : "일치하는 데이터가 없습니다.",
			"loadingRecords" : "로딩중...",
			"processing" : "잠시만 기다려 주세요.",
			"paginate" : {
				"next" : "다음",
				"previous" : "이전"
			}
		},
        destroy: true,
        processing: true,
        scrollY: "400px",
        scrollCollapse: true,
        paging:         false,
        serverSide: true,
		ajax : {
			 "url":"/system/space/get",
			 "type":"GET",
			 data : function (param) {
				 param.useYn = searchUseYn;
				 param.deviceTypeIdx = searchType;
				 param.spaceName = sch_station_name;
				 param.parentSpaceIdx = searchType2;
             }
		},
        columns : [
        	{data: "rowNum"},
            {data: "deviceType"},
            {data: ""},
            {data: "createDt"},
            {data: "useYn"}
        ],
        columnDefs: [ 
	        {
	        	targets:   [0, 1, 4],
	        	render: function(data, type, full, meta) {
					return "<a href='#' style='cursor: pointer; width: 100%;' onclick='stationDetail(\""+full.idx+"\", \"" + full.deviceTypeIdx +"\", \"" + full.spaceName +"\", \"" + full.parentSpaceIdx +"\", \"" + full.spaceLevel +"\", \"" + full.spaceOrder +"\", \"" + full.useYn +"\")'> <strong> "+ data + "</strong></a>";
				},
	        },
	        {
	        	targets:   2,
	        	render: function(data, type, full, meta) {
	        		var categoryName = "";
	        		if(full.parentSpaceIdx == 0){
	        			categoryName = full.spaceName;
	        		}else{
	        			categoryName = full.parentSpaceName+" > "+full.spaceName;
	        		}
	        		
	        		//parentSpaceName spaceName
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='stationDetail(\""+full.idx+"\", \"" + full.deviceTypeIdx +"\", \"" + full.spaceName +"\", \"" + full.parentSpaceIdx +"\", \"" + full.spaceLevel +"\", \"" + full.spaceOrder +"\", \"" + full.useYn +"\")'> <strong> "+ categoryName + "</strong></a>";
	        		
				},
	        },
	        
	        {
	        	targets:   3,
	        	render: function(data, type, full, meta) {
	        		return "<a href='#' style='cursor: pointer; width: 100%;' onclick='stationDetail(\""+full.idx+"\", \"" + full.deviceTypeIdx +"\", \"" + full.spaceName +"\", \"" + full.parentSpaceIdx +"\", \"" + full.spaceLevel +"\", \"" + full.spaceOrder +"\", \"" + full.useYn +"\")'> <strong> "+ full.createDt.substring(0,19) + "</strong></a>";
				},
        	},
        ],
	});

	$("#spaceTable_filter").hide();
	$("#spaceTable_info").hide();
	getParentSpace();
	$("#searchType2").val(searchType2);
}


function insertStation() {
	var parentStation = $('#parentStation').val();
	var stationName = $("#stationName").val();
	var parentStationlevel = 0;
	//if문 추가할거 상위카테고리가 0번이면 level은 1 그외는 무조건2
	
	if(parentStation == 0){
		parentStationlevel = 1;
	}else{
		parentStationlevel = 2;
	}
	
	if(!stationName){
		alert("설치공간 카테고리명을 입력해 주세요.");
		$("#stationName").focus();
		return false
	}

	$.ajax({
		url:"/check/spaceName",
		type:"GET",
		contentType : "application/json; charset=utf-8",
		async : false,
		data : "deviceTypeIdx="+$('#deviceType').val()+"&parentSpaceIdx="+parentStation+"&spaceName="+$('#stationName').val(),
		success : function (param) {
			if (param.resultCode == 1) {
				$.ajax({
					method : "POST",
					url : "/system/space/post",
					data : JSON.stringify({
						deviceTypeIdx : $('#deviceType').val(),
						spaceName : $('#stationName').val(),
						parentSpaceIdx : parentStation,
						spaceLevel : parentStationlevel,
						spaceOrder : $("#stationOrder").val()
					}),
					contentType : "application/json; charset=utf-8",
					success : function(d) {
						alert("등록 되었습니다.");
						location.reload();
					}
				});
			} else if (param.resultCode == 2) {
				alert("중복되는 카테고리명 입니다.");
			} else {
				alert("삭제된 카테고리명 입니다.");
			}
		}
	})
}

function updateStation() {
	var parentStation = $('#parentStation').val();
	var stationName = $("#stationName").val();
	var stationIdx = $('#stationIdx').val();
	var parentStationlevel = 0;
	//if문 추가할거 상위카테고리가 0번이면 level은 1 그외는 무조건2
	
	if(parentStation == 0){
		parentStationlevel = 1;
	}else{
		parentStationlevel = 2;
	}
	
	if(!stationName){
		alert("설치공간 카테고리명을 입력해 주세요.");
		$("#stationName").focus();
		return false
	}
	
	if (parentStation == stationIdx) {
		parentStation = 0;
	}

	$.ajax({
		url:"/check/spaceName",
		type:"GET",
		contentType : "application/json; charset=utf-8",
		async : false,
		data : "deviceTypeIdx="+$('#deviceType').val()+"&parentSpaceIdx="+parentStation+"&spaceName="+$('#stationName').val(),
		success : function (param) {
			console.log("! : " + oriStationName);
			if (oriStationName == $('#stationName').val() || param.resultCode == 1) {
				$.ajax({
					method : "PUT",
					url : "/system/space/put",
					data : JSON.stringify({
						idx : stationIdx,
						deviceTypeIdx : $('#deviceType').val(),
						spaceName : $('#stationName').val(),
						parentSpaceIdx : parentStation,
						spaceLevel : parentStationlevel,
						spaceOrder : $("#stationOrder").val()
					}),
					contentType : "application/json; charset=utf-8",
					success : function(d) {
						alert("수정 되었습니다.");
						location.reload();
					}
				});
			} else if (param.resultCode == 2) {
				alert("중복되는 카테고리명 입니다.");
			} else {
				alert("삭제된 카테고리명 입니다.");
			}
		}
	});
}

function deleteStation() {
	var confirmVal = confirm("정말 삭제하시겠습니까?");
	var idx = $("#stationIdx").val();

	if (confirmVal) {
		$.ajax({
			method : "DELETE",
			url : "/system/space/delete?idx=" + idx,
			contentType : "application/json; charset=utf-8",
			success : function(d) {
				console.log(d);
				if(d == 1){
					alert("하위카테고리가 있는경우 삭제 불가");
					return false;
				}else{
					alert("삭제 되었습니다.");
					location.reload();
				}
			}
		});
	}
}

function stationDetail(paramIdx, paramDeviceTypeIdx, paramStationName, paramParentStationIdx, paramStationLevel, paramStationOrder, paramUseYn) {
	var chkStationIdx;
	var SpaceHtml;
	var spaceLevel = "high";
	var useYn = "Y";
	var spaceName = "";
	var selected; 

	oriStationName = paramStationName;

	$('#insertBtn').hide();
	$('#deleteBtn').show();
	$('#updateBtn').show();
	$('#useYn').attr('disabled', true);
	$('#modal-lg').modal();

	$('#stationIdx').val(paramIdx);
	$('#deviceType').val(paramDeviceTypeIdx);
	$('#stationName').val(paramStationName);
	$('#parentStation').val("0").prop("selected","selected");
	$('#stationOrder').val(paramStationOrder);
	$('#useYn').val(paramUseYn);

	if (paramStationLevel != "1") {
		$('#parentStation').val(paramStationLevel -1 );

		$.ajax({
			url:"/system/space/high",
			type:"GET",
			async : false,
			data : "useYn="+useYn+"&deviceTypeIdx="+paramDeviceTypeIdx,
			success : function (param) {
				console.log(param);
				
				SpaceHtml = "<option value='0' data-value='0' >상위 카테고리로 등록</option>";
				for(var i = 0; i < param.data.length; i++){
					
					var idx = param.data[i].idx;
					var spaceName = param.data[i].spaceName;
					
					SpaceHtml += "<option value='"+idx+"' data-value = '"+idx+"'>"+spaceName+"</option>";
				}
				$("#parentStation").html(SpaceHtml);
			}
		})
	} else {
		console.log("상위");
	}
	
	stationChange(paramDeviceTypeIdx, paramIdx, 1);
	//2020-03-26 추가 
	chkStationIdx = $('#stationIdx').val();
	if(chkStationIdx){
		$("#deviceType").attr("disabled",true);
		if(paramParentStationIdx == 0){
			$("#parentStation").attr("disabled",true);
		}else{
			$("#parentStation").attr("disabled",false);
		}
	}else{
		$("#deviceType").attr("disabled",false);
		$("#parentStation").attr("disabled",false);
	}
	
	//해당 카테고리 옵션으로 selected
	if(paramParentStationIdx == 0){
		//$("#parentStation").val(paramIdx);
	}else{
		$("#parentStation").val(paramParentStationIdx);
	}
	
}

function stationChange(DeviceTypeIdx, paramIdx, modeVal){
	//var SelectOpt = $("#deviceType option:selected").val(); //장비유형 idx
	var SelectOpt = DeviceTypeIdx; //장비유형 idx
	var parentStation_len = $("#parentStation option").length; //상위카태고리 아이템 총 개수
	var parentStation_sel_opt = $("#parentStation option:selected").val();
	var mode = modeVal;
	
	getSpaceList();
	
	//왜만들었는지 기억안남 안씀 
	/*$('#parentStation option').each(function(){
		//alert(this.value);
		if(mode != 1){
			$("#parentStation").val("0");
		}
		
		var station_opt = this.value;
		var station_name = this.text;
		var subValue = $(this).data("value");
		
		if(SelectOpt == subValue){
			//$(".station_opt_"+subValue).attr('disabled',false);
			$(".space_opt_"+subValue).show();
		}else{
			//$(".station_opt_"+subValue).attr('disabled',true);
			$(".space_opt_"+subValue).hide();
			//$(".station_opt_"+subValue).css('background-color',"red");
			
		}
	})*/
}

function getSpaceList(){
	var useYn = "Y";
	var deviceTypeIdx = $("#deviceType").val();
	var spaceName = "";
	var spaceLevel = "high";
	var SpaceHtml = "";

	$.ajax({
		 url:"/system/space/high",
		 type:"GET",
		 async : false,
		 data : "useYn="+useYn+"&deviceTypeIdx="+deviceTypeIdx,
		 success : function (param) {
			console.log(param);
			
			SpaceHtml = "<option value='0' data-value='0' >상위 카테고리로 등록</option>";
			for(var i = 0; i < param.data.length; i++){
					
				var idx = param.data[i].idx;
				var spaceName = param.data[i].spaceName;
				
				SpaceHtml += "<option value='"+idx+"' data-value = '"+idx+"'>"+spaceName+"</option>";
			}
			$("#parentStation").html(SpaceHtml);
		 }
	})
	/*
	$.ajax({
		 url:"/system/space/get",
		 type:"GET",
		 async : false,
		 data : "useYn="+useYn+"&deviceTypeIdx="+deviceTypeIdx+"&spaceName="+spaceName+"&spaceLevel="+spaceLevel,
		 success : function (param) {
			//console.log(param);
			
			SpaceHtml = "<option value='0' data-value='0' >상위 카테고리로 등록</option>";
			for(var i = 0; i < param.data.length; i++){
				var idx = param.data[i].idx;
				var spaceName = param.data[i].spaceName;
				//
				SpaceHtml += "<option value='"+idx+"' data-value = '"+idx+"'>"+spaceName+"</option>";
			}
			$("#parentStation").html(SpaceHtml);
		 }
	})*/
}

//리스트 검색옵션 카테고리
function getParentSpace(){
	//searchType2
	var searchTypeHtml = "";
	var deviceTypeIdx = $("#searchType option:selected").val();
	
	if(!deviceTypeIdx){
		deviceTypeIdx = 1;
	}

	$.ajax({
		 url:"/system/space/high",
		 type:"GET",
		 async : false,
		 data : "useYn=Y&deviceTypeIdx="+deviceTypeIdx,
		 success : function (param) {
			console.log(param);
			
			searchTypeHtml = "<option value='' data-value='' >전체</option>";
			for(var i = 0; i < param.data.length; i++){
				var idx = param.data[i].idx;
				var spaceName = param.data[i].spaceName;
				//
				searchTypeHtml += "<option value='"+idx+"' data-value = '"+idx+"'>"+spaceName+"</option>";
			}
			$("#searchType2").html(searchTypeHtml);
		 }
	})
}


$().ready(function() {
	initDataTableCustom();
	getParentSpace();
	
	$("#spaceTable_filter").hide();
	$("#spaceTable_info").hide();
	
	$('#openModalBtn').click(function() {
		$('#insertBtn').show();
		$('#updateBtn').hide();
		$('#deleteBtn').hide();
		$('#useYn').attr('disabled', false);
		$("#deviceType").attr("disabled",false);
		$("#parentStation").attr("disabled",false);

		// Defaullt 설정 
		$('#stationIdx').val("");
		$('#deviceType').val("1");
		$('#stationName').val("");
		$('#parentStation').val("0");
		$('#stationOrder').val("0");
		$('#useYn').val("Y");
		stationChange($('#deviceType').val(),$('#deviceType').val());
		getSpaceList();
	})
	
	$('#searchBtn').click(function() {
		initDataTableCustom();
	})

	$('#insertBtn').click(function() {
		insertStation();
	})

	$('#updateBtn').click(function() {
		updateStation();
	})

	$('#deleteBtn').click(function() {
		deleteStation();
	})
	
	$(".close").click(function(){
		$("#stationIdx").val("");
		$("#deviceType").val("1");
		$("#stationName").val("");
		$("#stationOrder").val("");
		$("#parentStation").html("");
	})

	// 추가 처리 검토
	$('#deviceType').change(function() {
		var showDeviceType = $(this).val();

		$(".parent-space").each(function() {
			
		})
	})
})

function chageLangSelect() {
	getParentSpace();
}