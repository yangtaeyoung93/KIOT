<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<script src="/resources/share/js/system/menuList.js"></script>
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
						<li class="breadcrumb-item active"><strong>${item }</strong></li>
					</ol>
				</div>
			</div>
		</div>
		<!-- /.container-fluid -->
	</section>
	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-12">
				<div class="card">
					<!-- /.card-header -->
					<div class="card-body">
						<table id="menuTable" class="table table-bordered table-hover text-center">
							 <thead>
                                <tr>
                                    <th class="bgGray1"><input type="checkbox" name="all_chk" id="all_chk" style="margin-left: 17px;" class="chBox" /></th>
                                    <th class="bgGray1">메뉴 이름</th>
                                    <th class="bgGray1">메뉴 경로</th>
                                    <th class="bgGray1">메뉴 아이콘</th>
                                    <th class="bgGray1">메뉴 Url</th>
                                </tr>
                            </thead>
						</table>
						<input type="button" data-toggle="modal" class="btn btn-primary" data-target="#modal-lg" value="메뉴 등록"/>
						<input type="button" class="btn btn-primary" id="deleteBtn" value="메뉴 삭제"/>
					</div>
					<!-- /.card-body -->
				</div>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->
	</section>
	<!-- /.content -->
	<!-- .Modal -->
	<div class="modal fade" id="modal-lg" style="display: none;"
		aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"><strong>메뉴 등록/수정</strong></h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<form role="form">
						<div class="card-body">
							<div class="form-group">
								<label for="exampleInputEmail1">상위 메뉴</label>
								<select class="form-control" id="highRankMenu" name="highRankMenu">
									<option class="highRankMenu" value="0" data-level="0">-</option>
									<c:forEach var="menuList" items="${menuList }">
										<option class="highRankMenu" value="${menuList.idx }" data-level="${menuList.menuLevel }">${menuList.fullMenuName }</option>
									</c:forEach>
								</select>
							</div>
							<div class="form-group">
								<label for="menuName">메뉴명</label> 
								<input type="text" class="form-control" id="menuName"
									placeholder="메뉴 명">
							</div>
							<div class="form-group">
								<label for="menuEng">메뉴명 (Eng)</label> 
								<input type="text" class="form-control" id="menuEng"
									placeholder="메뉴 명">
							</div>
							<div class="form-group">
								<label for="menuUrl">메뉴 Url</label> 
								<input type="text" class="form-control" id="menuUrl"
									placeholder="메뉴 Url">
							</div>
							<div class="form-group">
								<label for="menuTag">메뉴 아이콘</label>
								<div class="list-box">
									<input type="hidden" id="menuTag" value="far fa-circle"/>
									<ul>
										<li class="list-box-color" onclick="SearchEquiCnt('ic-rcir')" id="ic-rcir"><i class="far fa-circle"></i></li>
										<li onclick="SearchEquiCnt('ic-scir')" id="ic-scir"><i class="fas fa-circle"></i></li>
										<li onclick="SearchEquiCnt('ic-edit')" id="ic-edit"><i class="fas fa-edit"></i></li>
										<li onclick="SearchEquiCnt('ic-table')" id="ic-table"><i class="fas fa-table"></i></li>
										<li onclick="SearchEquiCnt('ic-th')" id="ic-th"><i class="fas fa-th"></i></li>
										<li onclick="SearchEquiCnt('ic-img')" id="ic-img"><i class="far fa-image"></i></li>
										<li onclick="SearchEquiCnt('ic-sqa')" id="ic-sqa"><i class="far fa-plus-square"></i></li>
										<li onclick="SearchEquiCnt('ic-tac')" id="ic-tac"><i class="fas fa-tachometer-alt"></i></li>
									</ul>
								</div>
							</div>
							<div class="form-group">
								<label for="menuOrder">표출 순서</label><input
									type="number" class="form-control" id="menuOrder" value="0"
									placeholder="표출 순서">
							</div>
						</div>
						<!-- /.card-body -->
						<div class="card-footer">
							<button type="button" id="insertBtn" class="btn btn-primary">등록</button>
						</div>
					</form>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.Modal -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->