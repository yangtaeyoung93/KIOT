<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/header.jsp"%>
<link rel="stylesheet" href="https://openlayers.org/en/v5.3.0/css/ol.css" type="text/css">
<link rel="stylesheet" href="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/css/ol.css">
<script src="https://cdn.rawgit.com/openlayers/openlayers.github.io/master/en/v5.3.0/build/ol.js"></script>
<script src="/resources/share/js/dashboard/dust.js"></script>
<!-- ##### Header Area End ##### -->
<style>
	.card-text{
		font-size:0.85rem;
	}
</style>
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
	</section>
	<section class="content">
		<div class="row">
			<div class="col-6">
				<div class="card">
					<div class="card-header">
						<h5><strong>동별미세먼지 실황</strong></h5>
						<div class="list-box" style="width: 210px; float: right; position: absolute; top: 85px; right: 15px; z-index:50">
							<ul>
								<li id="pm25ViewBtn" style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;" class="list-box-color" >
									PM2.5
								</li>
								<li id="pm10ViewBtn" style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;" class="list-box-white">
									PM10
								</li>
							</ul>
						</div>
					</div>
					<div class="card-body">
						<div id="map" style="height: 600px;"></div>
						<div id="popup"></div>
					</div>
				</div>
			</div>
			<div class="col-6">
				<div class="card">
					<div class="card-header">
						<h5><strong>관측망 실황</strong></h5>
						<div class="list-box" style="width: 210px; float: right; position: absolute; top: 85px; right: 15px; z-index:50">
							<ul>
								<li id="pm25ViewBtn2" style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;" class="list-box-color" >
									PM2.5
								</li>
								<li id="pm10ViewBtn2" style="float:right; line-height: 30px; font-weight:1000; margin-right: 10px; height: 33px;font-size: 15px;" class="list-box-white">
									PM10
								</li>
							</ul>
						</div>
					</div>
					<div class="card-body">
						<div id="map2" style="height: 600px;"></div>	
						<div id="popup2"></div>
					</div>
				</div>
			</div>
		</div>
	</section>
	<section class="content">
		<div class="row">
			<div class="col-12">
				<div class="card">
					<div class="card-header">
						<h5><strong>관측망현황</strong></h5>
					</div>
					<div class="card-body">
						<div class="card-deck">
	                        <div class="card" style="width:20%;">
	                        	<div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더: 847대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 434대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">전체</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 847대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 41대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">서울</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 24대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">부산</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 16대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">대구</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                        	<div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더: 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 23대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">인천</span></small></div>
	                        </div>
	                    </div>
	                    <div class="card-deck mt10">
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 9대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">광주</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 12대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">대전</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 17대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">울산</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 94대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">경기</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 24대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">강원</span></small></div>
	                        </div>
	                    </div>
	                    <div class="card-deck mt10">
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 20대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">충북</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 34대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">충남</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 24대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">전북</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 31대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">전남</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 24대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">경북</span></small></div>
	                        </div>
	                    </div>
	                    <div class="card-deck mt10">
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 30대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">경남</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 6대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">제주</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%;">
	                            <div class="kt-bg-metal w-100 kt-padding-t-40 kt-padding-b-40"></div>
	                            <div class="card-body">
	                                <h5 class="card-text"><span id="">케이웨더 : 0대</span></h5>
	                                <h5 class="card-text"><span id="">국가관측망: 4대</span></h5>
	                            </div>
	                            <div class="card-footer"><small><span id="">세종</span></small></div>
	                        </div>
	                        <div class="card" style="width:20%; border:0; box-shadow: none;">
	                        &nbsp;
	                        </div>
	                        <div class="card" style="width:20%; border:0; box-shadow: none;">
	                        &nbsp;
	                        </div>
	                    </div>
					</div>
				</div>
			</div>
		</div>
		
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/footer.jsp"%>
<!-- ##### Footer Area End ##### -->