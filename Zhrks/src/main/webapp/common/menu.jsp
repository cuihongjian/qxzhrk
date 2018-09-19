<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
%>
<!--左侧悬浮导航-->
<div
	class="ft_theme_switcher ocult bd-sidebar bg-white shadow rounded-right border-light py-2 px-3"
	style="left: -215px;">
	<div class="toggle bg-info text-white rounded-right py-1 px-2">
		<i class="fa fa-bars fa-lg"></i>
	</div>
	<div class="desc">
		<!--急救，小屏幕导航按钮-->
		<div class="bd-search d-flex align-items-center">
			<button onclick="selectUser();" class="btn btn-danger btn-lg w-100" data-toggle="modal">
				<i class="fa fa-exclamation mr-1"></i>急救
			</button>
		</div>
	</div>
	<div class="style_list">
		<!--左侧导航-->
		<nav class="bd-links">
			<div class="bd-toc-item mx-3">
				<a class="bd-toc-link" href="#" data-toggle="collapse"
					data-target="#sidenavUser"><i class="fa fa-user mr-1"></i>用户展示<i
					class="fa fa-sort-down float-right"></i></a>
				<ul class="nav bd-sidenav collapse show" id="sidenavUser">
					<li><a href="/missing/regiMiss"><i
							class="fa fa-fw fa-pencil mr-1"></i>登记失踪人口</a></li>
					<li><a href="/missing/publicindex"><i
							class="fa fa-fw fa-eye mr-1"></i>公示信息</a></li>
				</ul>
			</div>
			<c:if test="${sessionScope.user.ROLECODE == 'admin' || sessionScope.user.ROLECODE == 'police' }"> 
			<div class="bd-toc-item mx-3">
				<a class="bd-toc-link collapsed" href="#" data-toggle="collapse"
					data-target="#sidenavBack"><i class="fa fa-search mr-1"></i>人口追踪<i
					class="fa fa-sort-down float-right"></i></a>
				<ul class="nav bd-sidenav collapse show" id="sidenavBack">
					<li><a href="/disaster"><i
							class="fa fa-fw fa-heartbeat mr-1"></i>灾难信息</a></li>
					<li><a href="/missing"><i
							class="fa fa-fw fa-user-times mr-1"></i>失踪人口信息</a></li>
				</ul>
			</div>
			</c:if>
			<div class="bd-toc-item mx-3">
				<a class="bd-toc-link collapsed" href="#" data-toggle="collapse"
					data-target="#sidenavHistory"><i class="fa fa-list-ol mr-1"></i>历史信息<i
					class="fa fa-sort-down float-right"></i></a>
				<ul class="nav bd-sidenav collapse show" id="sidenavHistory">
					<li><a href="/missing/doneindex"><i
							class="fa fa-fw fa-address-book-o mr-1"></i>失踪人口记录</a></li>
					<li><a href="/message"><i
							class="fa fa-fw fa-comment mr-1"></i>信息推送记录</a></li>
					<c:if test="${sessionScope.user.ROLECODE == 'admin' }">
						<li><a href="/send"><i
								class="fa fa-fw fa-user-circle-o mr-1"></i>推送人管理</a></li>
					</c:if> 
				</ul>
			</div>
			<c:if test="${sessionScope.user.ROLECODE == 'admin' }"> 
			<div class="bd-toc-item mx-3">
				<a class="bd-toc-link collapsed" href="#" data-toggle="collapse"
					data-target="#sidenavBasic"><i class="fa fa-address-card mr-1"></i>基本信息<i
					class="fa fa-sort-down float-right"></i></a>
				<ul class="nav bd-sidenav collapse show" id="sidenavBasic">
					<li><a href="/userLibrary"><i
							class="fa fa-fw fa-newspaper-o mr-1"></i>人口基础信息库</a></li>
					<li><a href="/cama"><i
							class="fa fa-fw fa-camera-retro mr-1"></i>配置监控</a></li>
					<li><a href="/video"><i
							class="fa fa-fw fa-play-circle mr-1"></i>视频信息</a></li>
				</ul>
			</div>
			</c:if>
			<c:if test="${sessionScope.user.ROLECODE == 'admin' }">
			<div class="bd-toc-item mx-3">  
				<a class="bd-toc-link collapsed" href="#" data-toggle="collapse"
					data-target="#sidenavSystem"><i class="fa fa-cog mr-1"></i>系统管理<i
					class="fa fa-sort-down float-right"></i></a>
				<ul class="nav bd-sidenav collapse show" id="sidenavSystem">
					<li><a href="/user"><i
							class="fa fa-fw fa-address-book mr-1"></i>用户管理</a></li>
					<li><a href="/role"><i
							class="fa fa-fw fa-cogs mr-1"></i>角色管理</a></li>
					<li><a href="/menu"><i
							class="fa fa-fw fa-navicon mr-1"></i>菜单管理</a></li>
				</ul>
			</div>
			</c:if> 
		</nav>
	</div>
</div>

<!-- 急救信息弹出框 -->
<div class="modal fade" id="CryHelpNews" tabindex="-1" role="dialog"
	aria-labelledby="ModalCenterTitle" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header bg-danger text-white">
				<h5 class="modal-title" id="exampleModalLongTitle">
					<i class="fa fa-exclamation mr-1"></i>急救信息
				</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body text-center">
				<form class="registerform am-form" id="misForm" action="/saveMis" method="post">
					<input type="hidden" name="mis.miuser" id="userId">
					<div class="form-group row">
						<label for="CryHelpName"
							class="col-sm-3 col-form-label col-form-label-sm">姓名</label>
						<div class="col-sm-9">
							<input id="CryHelpName" type="text"
								class="form-control form-control-sm" value=" " readonly="readonly">
						</div>
					</div>
					<div class="form-group row">
						<label for="CryHelpCardID"
							class="col-sm-3 col-form-label col-form-label-sm">身份证号</label>
						<div class="col-sm-9">
							<input id="CryHelpCardID" type="text"
								class="form-control form-control-sm" value=" " readonly="readonly">
						</div>
					</div>
					<div class="form-group row">
						<label for="CryHelpPhoneNum"
							class="col-sm-3 col-form-label col-form-label-sm">手机号</label>
						<div class="col-sm-9">
							<input id="CryHelpPhoneNum" type="number"
								class="form-control form-control-sm" value=" ">
						</div>
					</div>
					<div class="form-group row">
						<label for="CryHelpAdress"
							class="col-sm-3 col-form-label col-form-label-sm">地点</label>
						<div class="col-sm-9">
							<input id="CryHelpAdress" type="text" name="mis.address"
								class="form-control form-control-sm" value=" ">
						</div>
					</div>
					<div class="form-group row">
						<label for="CryHelpRemark"
							class="col-sm-3 col-form-label col-form-label-sm">描述</label>
						<div class="col-sm-9">
							<textarea id="CryHelpRemark" type="textarea" class="form-control"
								value=" "></textarea>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" onclick="saveMis();" class="btn btn-danger" data-dismiss="modal"
							data-toggle="modal">发送急救消息</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<!-- 发送急救消息成功弹出框 -->
<div class="modal fade" id="SendCryHelpSuccess" tabindex="-1"
	role="dialog" aria-labelledby="ModalCenterTitle" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header bg-info text-white">
				<h5 class="modal-title" id="exampleModalLongTitle">
					<i class="fa fa-lightbulb-o mr-1"></i>提示
				</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body text-center">
				<i class="fa fa-check text-info mr-1" aria-hidden="true"></i>发送成功！
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-info" data-dismiss="modal">确定</button>
			</div>
		</div>
	</div>
</div>
<!-- sidebar end -->
<script type="text/javascript">
	function selectUser(){
		$.ajax({
			async : false,
			type : "POST",
			url : "<%=path %>/findUser",
			dataType : 'json',
			success : function(data) {
				$("#userId").val(data.ID);
				$('#CryHelpName').val(data.NAME);
		    	$('#CryHelpCardID').val(data.USERCODE);
		    	$('#CryHelpPhoneNum').val(data.TEL);
			}
		});
		$('#CryHelpNews').modal('show');
	}
	
	function saveMis(){
		var addr = $("#CryHelpAdress").val();
		if(addr == ""){
			alert("地点信息不能为空");
			return false;
		}
		$.ajax({
			async : false,
			type : "POST",
			data : {userId:$("#userId").val(),addr:addr,remark:$("#CryHelpRemark").val(),cardId:$("#CryHelpCardID").val()},
			url : "<%=path %>/saveMis",
			dataType : 'json',
			success : function(ret) {
				if (ret.state == "ok") {
					$('#SendCryHelpSuccess').modal('show');
					location.href = "<%=path %>/desktop";
				} else {
					alert(ret.msg);
				}
			}
		});
	}
</script>

