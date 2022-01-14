<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<%@ include file="../common/datacenter_head.jsp"%>
<script src="/resources/share/js/datacenter/pdc.js"></script>

<style>
/* 나눔고딕 */
@font-face {
	font-family: '나눔고딕';
	font-style: normal;
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.eot);
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.eot?#iefix) format('embedded-opentype'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.woff2) format('woff2'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.woff) format('woff'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Bold.ttf) format('truetype');
}
@font-face {
	font-family: 'NanumGothic';
	font-style: normal;
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.eot);
	src: url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.eot?#iefix) format('embedded-opentype'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.woff2) format('woff2'),
		url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.woff) format('woff'),
	   url(//themes.googleusercontent.com/static/fonts/earlyaccess/nanumgothic/v3/NanumGothic-Regular.ttf) format('truetype');
}
.number_infobox_02 {
	top: 55.5%;
}
.top_btn{ width:100px;height:25px;background-color:#32AEE5; color:white; border-radius:3px; font-size:14px; font-weight:normal; border:0; }
</style>

<div id="content_02">
	<div class="summary_02">
		<ul class="title_02" style="margin:0">
			<ul style="float:right;">
				<li class="tv_mode_02" id="id_area">
					
				</li>
				<li class="tv_mode_02" id="listBtnArea">
					<!--  <img src="/resources/share/img/datacenter/manager/detail_bt.png" alt="상세내역" />-->
					<input type="button" class="top_btn" id="list_btn" value="리스트모드" onClick= "goList();" style="cursor:pointer;"/>
				</li>
				<li class="tv_mode_02" id="ventBtnArea">
					<!--  <img src="/resources/share/img/datacenter/manager/detail_bt.png" alt="상세내역" />-->
					<input type="button" class="top_btn" id="vent_btn" value="환기청정기" onClick= "openVentPop();" style="cursor:pointer;"/>
				</li>
				<li class="tv_mode_02" id="detailBtnArea">
					<!--  <img src="/resources/share/img/datacenter/manager/detail_bt.png" alt="상세내역" />-->
					<input type="button" class="top_btn" id="detail_btn" value="상세정보" onClick= "view_detail();" style="cursor:pointer;"/>
				</li>
				<li class="tv_mode_02" id="logoutBtnArea">
					<input type="button" class="top_btn" value="로그아웃" style="cursor:pointer;" onClick="goLogOut();" />
				</li>
			</ul>
			<ul style="float:left; width:340px;">
				<li>* 업데이트 : </li>
				<li id="update_time"></li>
			</ul>
			<ul class="iaq_title" style="margin: -5px 0 0 378px;">
				<li><img src="/resources/share/img/datacenter/manager/logo_basic.png" alt="airguardk" style="cursor:pointer; width:250px; height:29px;"/></li>
			</ul>
		</ul>

		<div class="quotient_box" id="oaqDiv" style="display:none;">
			<div class="my_station_02" style="background:#101011;">
				<h4 class="quotient_title text_01" style="background:#07446e;">
					<img src="/resources/share/img/datacenter/manager/t_01.png" alt="나의스테이션111"/>
				</h4>
				<div class="product_oaq_02" >
					<img src="/resources/share/img/datacenter/manager/oaq_big_03.png" />
				</div>
			</div>

			<div class="my_station_02 ml8">
				<h4 class="quotient_title text_05">
					<div class="index_icon_02"><img src="/resources/share/img/icon/iaq_icon.png" width="42px" height="42px"/></div>
					<!--  <img src="/resources/share/img/datacenter/manager/t_054.png" alt="통합실외쾌적지수"/>-->
					<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">통합실외쾌적지수</div>
				</h4>
				<div class="numerical_text_02"><span id="cici_index"></span> </div>
				<div class="quotient_number_02"><span id="cici_title"></span><span>&nbsp;</span></div>
				<div class="number_infobox_02 cici_div"><li><span id="cici_index_val" style="font-weight:bold;" class="f50"></span><br/><span id="cici_total"></span></li></div>
				<div id="cici" class="numerical_02">
				</div>
			</div>
			
			<div class="my_station_02 ">
				<h4 class="quotient_title text_04">
					<div class="index_icon_02"><img src="/resources/share/img/icon/dust_icon.png" width="42px" height="42px"/></div>
					<!--  <img src="/resources/share/img/datacenter/manager/t_04.png" alt="미세먼지"/>-->
					<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">미세먼지</div>
				</h4>
				<div class="numerical_text_02"><span id="pmat_index"></span> </div>
				<div class="quotient_number_02"><span id="pmat_title"></span></div>
				<div class="number_infobox_02 pmat_div"><li><span id="pmat_index_val" style="font-weight:bold;" class="f50"></span><br/><span id="pmat_total"></span></li></div>
				<div id="pmat"class="numerical_02">
				</div>
			</div>

			<div class="my_station_02 ml8 ">
				<h4 class="quotient_title text_06">
					<div class="index_icon_02"><img src="/resources/share/img/icon/dust_icon.png" width="42px" height="42px"/></div>
					<!--  <img src="/resources/share/img/datacenter/manager/t_06.png" alt="휘발성유기화합물"/>-->
					<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">초미세먼지</div>
				</h4>
				<div class="numerical_text_02"><span id="pm25_index"></span></div>
				<div class="quotient_number_02 pm25_div"><span id="pm25_title"></span></div>
				<div class="number_infobox_02"><li><span id="pm25_index_val" style="font-weight:bold;" class="f50"></span><br/><span id="pm25_total"></span></li></div>
				<div id="pm25" class="numerical_02">
				</div>
			</div>
			
			
			<div class="my_station_02 ml8 mt8" style="background:#101011;">
				<div class="product_oaq_text_02">
					<span>위치</span><br/>
					<span id="locationSpan" style="color:#27A6F5; font-weight:bold; font-size:24px;"></span><br/>
					<span>스테이션 이름</span><br/>
					<span id="stationNameSpan" style="color:#27A6F5; font-weight:bold; font-size:24px;"></span><br/>
					<span>기기 고유 번호</span><br/>
					<span id="serialNumSpan" style="color:#27A6F5; font-weight:bold; font-size:24px;"></span><br/>
				</div>
			</div>

			<div class="my_station_02 ml8 mt8">
				<h4 class="quotient_title text_02">
					<div class="index_icon_02"><img src="/resources/share/img/icon/tem_icon.png" width="42px" height="42px"/></div>
					<!--  <img src="/resources/share/img/datacenter/manager/t_02.png" alt="온도"/>-->
					<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">온도</div>
				</h4>
				<div class="numerical_text_02"><span id="temp_index"></span></div>
				<div class="quotient_number_02"><span id="temp_title"></span></div>
				<div class="number_infobox_02 temp_div"><li><span id="temp_index_val" style="font-weight:bold;" class="f50"></span><br/><span id="temp_total"></span></li></div>
				<div id="temp" class="numerical_02">
				</div>
			</div>

			<div class="my_station_02 ml8 mt8">
				<h4 class="quotient_title text_03">
					<div class="index_icon_02"><img src="/resources/share/img/icon/hum_icon.png" width="42px" height="42px"/></div>
					<!--  <img src="/resources/share/img/datacenter/manager/t_03.png" alt="습도"/>-->
					<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">습도</div>
				</h4>
				<div class="numerical_text_02"><span id="humi_index"></span> </div>
				<div class="quotient_number_02"><span id="humi_title"></span></div>
				<div class="number_infobox_02 humi_div"><li><span id="humi_index_val" style="font-weight:bold;" class="f50"></span><br/><span id="humi_total"></span></li></div>
				<div id="humi" class="numerical_02">
				</div>
			</div>
			

			<div class="my_station_02 ml8 mt8">
				<h4 class="quotient_title text_08">
					<div class="index_icon_02"><img src="/resources/share/img/icon/noise_icon.png" width="42px" height="42px"/></div>
					<!--  <img src="/resources/share/img/datacenter/manager/t_08.png" alt="소음"/>-->
					<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">소음</div>
				</h4>
				<div class="numerical_text_02"><span id="noise_index"></span> </div>
				<div class="quotient_number_02"><span id="noise_title"></span></div>
				<div class="number_infobox_02 noise_div"><li><span id="noise_index_val" style="font-weight:bold;" class="f50"></span><br/><span id="noise_total"></span></li></div>
				<div id="noise" class="numerical_02">
					
				</div>
			</div>
		</div>
		<div class="quotient_box" id="iaqDiv" >
				<div class="my_station_02" style="background:#101011;">
					<h4 class="quotient_title text_01" style="background:#07446e;">
						<img src="/resources/share/img/datacenter/manager/t_01.png" alt="나의스테이션"/>
					</h4>
					<div class="product_02" >
						<img src="/resources/share/img/datacenter/manager/product_big_on.png" />
					</div>
					<div class="product_text_02">
						<span>위치</span><br/>
						<span id="locationSpan2" style="color:#27A6F5; font-weight:bold; font-size:24px;"></span><br/>
						<span>스테이션 이름</span><br/>
						<span id="stationNameSpan2" style="color:#27A6F5; font-weight:bold; font-size:24px;"></span><br/>
						<span>기기 고유 번호</span><br/>
						<span id="serialNumSpan2" style="color:#27A6F5; font-weight:bold; font-size:24px;"></span><br/>
					</div> 
				</div>
				
				<div class="my_station_02 ml8">
					<h4 class="quotient_title text_04">
						<div class="index_icon_02"><img src="/resources/share/img/icon/dust_icon.png" width="42px" height="42px"/></div>
						<!--  <img src="/resources/share/img/datacenter/manager/t_04.png" alt="미세먼지111"/>-->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">미세먼지</div>
					</h4>
					<div class="numerical_text_02"><span id="pmat_index2"></span> </div>
					<div class="quotient_number_02"><span id="pmat_title2"></span></div>
					<div class="number_infobox_02"><li><span id="pmat_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="pmat_2_total"></span></li></div>
					<div id="pmat_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/very_bad.png" />-->
					</div>
				</div>
				
				<div class="my_station_02 ml8 ">
					<h4 class="quotient_title text_08">
						<div class="index_icon_02"><img src="/resources/share/img/icon/dust_icon.png" width="42px" height="42px"/></div>
						<!--  <img src="/resources/share/img/datacenter/manager/t_04.png" alt="소음"/>-->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">초미세먼지</div>
					</h4>
					<div class="numerical_text_02"><span id="pm25_index2"></span> </div>
					<div class="quotient_number_02"><span id="pm25_title2"></span></div>
					<div class="number_infobox_02"><li><span id="pm25_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="pm25_2_total"></span></li></div>
					<div id="pm25_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/bad.png" />	-->
					</div>
				</div>
				
				<div class="my_station_02 ml8">
					<h4 class="quotient_title text_07">
						<div class="index_icon_02"><img src="/resources/share/img/icon/co2_icon.png" width="42px" height="42px"/></div>
						<!-- <img src="/resources/share/img/datacenter/manager/t_07.png" alt="이산화탄소"/> -->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">이산화탄소</div>
					</h4>
					<div class="numerical_text_02"><span id="co2_index2"></span></div>
					<div class="quotient_number_02"><span id="co2_title2"></span></div>
					<div class="number_infobox_02 "><li><span id="co2_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="co2_2_total"></span></li></div>
					<div id="co2_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/good.png" />-->
					</div>
				</div>
				
				<div class="my_station_02 mt8" style="background:#101011;">
					<h4 class="quotient_title text_05" style="background:#07446e;">
						<div class="index_icon_02"><img src="/resources/share/img/icon/iaq_icon.png" width="42px" height="42px"/></div>
						<!--  <img src="/resources/share/img/datacenter/manager/t_05.png" alt="통합실내쾌적지수"/>-->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">통합실내쾌적지수</div>
					</h4>
					<div class="numerical_text_02"><span id="cici_index2"></span> </div>
					<div class="quotient_number_02"><span id="cici_title2"></span><span>&nbsp;</span></div>
					<div class="number_infobox_02 "><li><span id="cici_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="cici_2_total"></span></li></div>
					<div id="cici_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/normal.png" />-->
					</div>
				</div>
				
				<div class="my_station_02 ml8 mt8">
					<h4 class="quotient_title text_02">
						<div class="index_icon_02"><img src="/resources/share/img/icon/tem_icon.png" width="42px" height="42px"/></div>
						<!--  <img src="/resources/share/img/datacenter/manager/t_02.png" alt="온도"/>-->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">온도</div>
					</h4>
					<div class="numerical_text_02"><span id="temp_index2"></span></div>
					<div class="quotient_number_02"><span id="temp_title2"></span></div>
					<div class="number_infobox_02 "><li><span id="temp_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="temp_2_total"></span></li></div>
					<div id="temp_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/good.png" />-->
					</div>
				</div>

				<div class="my_station_02 ml8 mt8">
					<h4 class="quotient_title text_03">
						<div class="index_icon_02"><img src="/resources/share/img/icon/hum_icon.png" width="42px" height="42px"/></div>
						<!--  <img src="/resources/share/img/datacenter/manager/t_03.png" alt="습도"/>-->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">습도</div>
					</h4>
					<div class="numerical_text_02 "><span id="humi_index2"></span> </div>
					<div class="quotient_number_02 "><span id="humi_title2"></span></div>
					<div class="number_infobox_02 "><li><span id="humi_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="humi_2_total"></span></li></div>
					<div id="hum_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/good.png" />-->
					</div>
				</div>

				<div class="my_station_02 ml8 mt8">
					<h4 class="quotient_title text_06">
						<div class="index_icon_02"><img src="/resources/share/img/icon/voc_icon.png" width="42px" height="42px"/></div>
						<!--  <img src="/resources/share/img/datacenter/manager/t_06.png" alt="휘발성유기화합물"/>-->
						<div style="color:white; font-size:30px; margin-top:1px; font-weight:normal;">휘발성유기화합물</div>
					</h4>
					<div class="numerical_text_02"><span id="vocs_index2"></span></div>
					<div class="quotient_number_02"><span id="vocs_title2"></span></div>
					<div class="number_infobox_02 "><li><span id="vocs_index_val2" style="font-weight:bold;" class="f50"></span><br/><span id="vocs_2_total"></span></li></div>
					<div id="vocs_2" class="numerical_02">
						<!--  <img src="/resources/share/img/datacenter/manager/little_bad.png" />-->
					</div>
				</div>


				
			</div>
		<div class="quotient_text_box_02">
			<div class="action" style="height:210px;">
				<h4 class="text_title_02">
					<img src="/resources/share/img/datacenter/manager/title_022.png" alt="행동요령"/>
				</h4>
				<div class="motion_02">
					<p id="behavior"></p>
				</div>
			</div>
			<div class="class" style="height:210px;">
				<h4 class="text_title_02">
					<img src="/resources/share/img/datacenter/manager/title_032.png" alt="지수등급"/>
					<span id="update_time2"></span>
				</h4>
				<div class="class_box_02" id="iaq_jisu_div">
					<li id="49" class="red" style="font-size:30px;"></li>
					<li id="79" class="orange" style="font-size:30px;"></li>
					<!--  <li id="60" class="yellow" style="font-size:30px;"></li>-->
					<li id="89" class="green" style="font-size:30px;"></li>
					<li id="100" class="blue" style="font-size:30px;"></li>

					<li id="arrow1" class="h17"></li>
					<li id="arrow2" class="h17"></li>
					<!--  <li id="arrow3" class="h17"></li>-->
					<li id="arrow4" class="h17"></li>
					<li id="arrow5" class="h17"></li>

					<li class="red_bak">매우나쁨<br/>0~49</li>
					<li class="orange_bak">나쁨<br/>50~79</li>
					<!--  <li class="yellow_bak">약간나쁨<br/>40~59</li>-->
					<li class="green_bak">보통<br/>80~89</li>
					<li class="blue_bak">좋음<br/>90~100</li>
				</div>
				
			</div>
		</div>
	</div>
	
</div>

<form id="detail_info_form" method="post">
	<input type="hidden" name="serial" id="serial"/>
	<input type="hidden" name="email" id="email" value="" />
	<input type="hidden" name="nowAll" />
	<input type="hidden" name="param" />
	<input type="hidden" name="member_level" value="0" />
	<input type="hidden" name="member_type" value="2" />
	<input type="hidden" name="parentSpaceName" id="parentSpaceName"/>
	<input type="hidden" name="spaceName" id="spaceName"/>
	<input type="hidden" name="productDt" id="productDt"/>
	<input type="hidden" name="stationName" id="stationName"/>
	<input type="hidden" name="standard" id="standard"/>
	<input type="hidden" name="deviceType" id="deviceType"/>
		
</form>

<!-- ##### Footer Area Start ##### -->
<%@ include file="../common/datacenter_footer.jsp"%>
<!-- ##### Footer Area End ##### -->