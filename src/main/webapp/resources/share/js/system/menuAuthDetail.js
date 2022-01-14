/**
 * 
 */

function insertMenuAuth(menuIdx) {
	$.ajax({
		method : "POST",
		url : "/system/admin/menu/auth/insert",
		data : JSON.stringify({
			adminIdx : $('#adminIdx').val(),
			menuIdx : menuIdx
		}),
		contentType : "application/json; charset=utf-8"
	});
}

function deleteMenuAuth(menuIdx) {
	$.ajax({
		method : "DELETE",
		url : "/system/admin/menu/auth/delete",
		data : JSON.stringify({
			adminIdx : $('#adminIdx').val(),
			menuIdx : menuIdx
		}),
		contentType : "application/json; charset=utf-8"
	});
}

$().ready(function() {
	$("input[data-bootstrap-switch]").each(function() {
		$(this).bootstrapSwitch('state', $(this).prop('checked'));
	});

	$('#listBtn').click(function() {
		location.href = "/system/admin/menu/auth/list";
	})

	$('#refBtn').click(function() {
		location.reload();
	})

	$('.menuCheckBox').on('switchChange.bootstrapSwitch', function (e, state) {
		if (e.target.checked) insertMenuAuth($(this).val());
		else deleteMenuAuth($(this).val());
	})
})