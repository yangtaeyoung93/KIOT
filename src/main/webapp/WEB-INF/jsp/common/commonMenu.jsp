<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- ##### Header Area Start ##### -->
<script src="/resources/share/js/common/common.js"></script>
<!-- ##### Header Area End ##### -->
<style>
.form-control{font-size:12px !important}
</style>
<!--  <body class="hold-transition sidebar-mini text-sm">-->
<body class="hold-transition sidebar-mini" style="font-size:12px !important;">
	<div class="wrapper">
		<nav
			class="main-header navbar navbar-expand navbar-white navbar-light">
			<ul class="navbar-nav">
				<li class="nav-item"><a class="nav-link" data-widget="pushmenu"
					href="#"><i class="fas fa-bars"></i></a></li>
			</ul>

			<div class="navbar-nav ml-auto">
				<a href="/logoutTry">
					<span class="btn-info" style="margin-right:20px;padding: 10px;border-radius: 20px;">
						<strong >로그아웃</strong>
					</span>
				</a>
			</div>
		</nav>
		<aside class="main-sidebar sidebar-dark-primary elevation-4">
			<a href="/dashboard/receive" class="brand-link"> <img
				src="/resources/dist/img/main_logo_sub_kwt.png" alt="AdminLTE Logo"
				class="brand-image img-circle elevation-3" style="background: white; opacity: .8">
				<span class="brand-text font-weight-light"><strong>케이웨더</strong></span>
			</a>
			<input type="hidden" id="oneDepth" value="${oneDepth }"/>
			<input type="hidden" id="twoDepth" value="${twoDepth }"/>
			<input type="hidden" id="threeDepth" value="${threeDepth }"/> 
			<!-- menu-open -->
			<div class="sidebar">
				<nav class="mt-2">
					<ul id="menuListUl" class="nav nav-pills nav-sidebar flex-column nav-legacy nav-child-indent" data-widget="treeview" role="menu" data-accordion="false">
					</ul>
				</nav>
			</div>
		</aside>