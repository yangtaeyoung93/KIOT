<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>

<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<!-- Select2 -->
<script src="/resources/plugins/select2/js/select2.full.min.js"></script>
<!-- ##### Header Area End ##### -->
<style>
.filter-cli {
	background: rebeccapurple;
	color: white;
	border-radius: 5px;
}
</style>
<div class="content-wrapper">
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>
						<strong>외부 프로젝트 URL </strong>
					</h1>
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

						<table id="urlTable" class="table table-bordered table-hover" style="text-align: center;">
							 <thead>
                                <tr>
                                    <th class="bgGray1">번호</th>
                                    <th class="bgGray1">페이지 URL</th>
                                    <th class="bgGray1">설명</th>
                                </tr>

                            </thead>
							
						</table>
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

<script>
const data = [{"number" : "1", "url" : "http://220.95.232.80:8002/admin/dashboard/receive","description" : "[공공데이터개방] 구로구청 관리자페이지"},
                {"number" : "2", "url" : "http://220.95.232.80:8005/member/guroMap", "description" : "[공공데이터개방] 구로구청 사용자페이지"},
                {"number" : "3", "url" : "http://220.95.232.79:8889/map", "description" : "[공공데이터개방] 인천서구 사용자페이지"},
                {"number" : "4", "url" : "http://220.95.232.79:8889/admin", "description" : "[공공데이터개방] 인천서구 관리자페이지"}
]
$(document).ready(function() {
    $('#urlTable').DataTable({
        data : data,
        columns : [
            {data : 'number'},
            {data : 'url'},
            {data : 'description'}
        ],
         language: {
                    emptyTable: "데이터가 없습니다.",
                    lengthMenu: "페이지당 _MENU_ 개씩 보기",
                    info: "현재 _START_ - _END_ / _TOTAL_건",
                    infoEmpty: "데이터 없음",
                    infoFiltered: "( _MAX_건의 데이터에서 필터링됨 )",
                    search: "",
                    zeroRecords: "일치하는 데이터가 없습니다.",
                    loadingRecords: "로딩중...",
                    processing: "잠시만 기다려 주세요.",
                    paginate: {
                      next: "다음",
                      previous: "이전",
                    },
                  },
        columnDefs : [{
            targets : 1,
            render : function (data){
                return "<a href='"+data+"'>"+data+"</a>";
            }
            }
        ]

    });
    $('#urlTable_filter').css('display','none');
} );

</script>