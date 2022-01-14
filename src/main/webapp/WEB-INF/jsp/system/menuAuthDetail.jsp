<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/menuAuthDetail.js"></script>
<!-- ##### Header Area End ##### -->

<div class="content-wrapper">
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>
						<strong>${item }</strong>
					</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><strong>${superItem }</strong></li>
						<li class="breadcrumb-item"><a href="/system/admin/menu/list">메뉴 권한 관리</a></li>
						<li class="breadcrumb-item active"><strong>${item }</strong></li>
					</ol>
				</div>
			</div>
		</div>
		<!-- /.container-fluid -->
	</section>
	<!-- Main content -->
	<section class="content">
		<input type="hidden" id="adminIdx" value="${idx }"/>
		<div class="row">
			<div class="col-12">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<h3>
							<strong>메뉴 목록</strong>
						</h3>
						<hr/>
						<div class="opt_wrap"
							style="height: 595px; overflow-y: auto; overflow-x: hidden; padding-right: 10px; ">
							<div class="row">
								<c:forEach var="data" items="${menuDatas }">
									<div class="opt_div col-4" 
										style="padding-top: 20px; padding-bottom: 20px;">
										<c:choose>
											<c:when test="${data.menuLevel == 1}">
												<h5><strong style="color: blue;">${data.fullMenuName }</strong></h5>
											</c:when>
											<c:when test="${data.menuLevel == 2}">
												<h6><strong style="color: green;">${data.fullMenuName }</strong></h6>
											</c:when>
											<c:otherwise>
												<strong>${data.fullMenuName }</strong><br/>
											</c:otherwise>
										</c:choose>

										<c:choose>
											<c:when test="${data.authFlag == 1}">
												<input type="checkbox" name="my-checkbox" class="menuCheckBox" value="${data.idx }" checked data-bootstrap-switch>
											</c:when>
											<c:otherwise>
												<input type="checkbox" name="my-checkbox" class="menuCheckBox" value="${data.idx }" data-bootstrap-switch>
											</c:otherwise>
										</c:choose>
									</div>
								</c:forEach>
							</div>
						</div>
						<hr/>
						<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="listBtn" value="목록으로" />
						<input type="button" class="btn btn-primary ml3" style="min-width:100px;" id="refBtn" value="새로고침" />
					</div>
					<!-- /.card-body -->
				</div>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->