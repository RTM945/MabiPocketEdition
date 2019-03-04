<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	request.setCharacterEncoding("utf-8");
	response.setHeader("Pragma", "No-cache");//HTTP 1.1
	response.setHeader("Cache-Control", "no-cache");//HTTP 1.0
	response.setHeader("Expires", "0");//防止被proxy
%>
<html>
	<head>
		<title>卡斯骑士屯</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link type="image/x-icon" rel="icon" href="icon.ico" >
		<link type="image/x-icon" rel="shortcut icon" href="icon.ico">
		<link rel='stylesheet' href='css/style.css' type='text/css' media='all' />
		<link rel='stylesheet' href='css/dark.css' type='text/css' media='all' />
		<script type='text/javascript' src='js/jquery-1.7.2.min.js'></script>
		<script type='text/javascript' src='js/gamepress.js'></script>
		<script type='text/javascript' src='js/jquery.tools.min.js'></script>
		<script type='text/javascript' src='js/jquery.easing.1.3.js'></script>
		<script type='text/javascript' src='js/modernizr-custom.min.js'></script>
		<script type='text/javascript' src='js/jquery.placeholder.min.js'></script>
		<style type="text/css">
			table tbody td {
				padding: 2px 5px 2px 5px;
				vertical-align: middle;
			}
		</style>
	</head>
	<body>
		<div id="page">
			
			<!-- 内容 -->
			<div id="content-wrapper">
				<div id="content-inner">
					<!-- 搜索条件 -->
					<form action="shop" method="get">
					<p>
						<select name="server">
							<option value="16"<c:if test="${search.server == 16}"> selected="selected"</c:if>>玛丽</option>
							<option value="17"<c:if test="${search.server == 17}"> selected="selected"</c:if>>鲁拉里</option>
							<option value="21"<c:if test="${search.server == 21}"> selected="selected"</c:if>>克莉斯特</option>
							<option value="27"<c:if test="${search.server == 27}"> selected="selected"</c:if>>莉莉丝</option>
							<option value="28"<c:if test="${search.server == 28}"> selected="selected"</c:if>>伊文</option>
							<option value="29"<c:if test="${search.server == 29}"> selected="selected"</c:if>>西蒙</option>
						</select>
					每页数量
						<select name="row">
							<option value="10"<c:if test="${search.row == 10}"> selected="selected"</c:if>>10</option>
							<option value="15"<c:if test="${search.row == 15}"> selected="selected"</c:if>>15</option>
							<option value="20"<c:if test="${search.row == 20}"> selected="selected"</c:if>>20</option>
							<option value="30"<c:if test="${search.row == 30}"> selected="selected"</c:if>>30</option>
							<option value="50"<c:if test="${search.row == 50}"> selected="selected"</c:if>>50</option>
							<option value="100"<c:if test="${search.row == 100}"> selected="selected"</c:if>>100</option>
						</select>
					搜索
						<select name="searchType">
							<option value="4"<c:if test="${search.searchType == 4}"> selected="selected"</c:if>>按物品名</option>
							<option value="1"<c:if test="${search.searchType == 1}"> selected="selected"</c:if>>按人物名</option>
							<option value="2"<c:if test="${search.searchType == 2}"> selected="selected"</c:if>>按广告语</option>
						</select>
					关键字
						<input type='text' name='searchWord' size='20' class='text' value="${search.searchWord}">
						<input type='submit' value='搜索' class='button2'>
					</p>
					</form>
					
					

					<div style="background-color: #DEDEDE">
						<table width="100%" cellpadding="1" cellspacing="1" id="Filetable">
							<tr style="background-color:#555555">
								<th width="10%"><a class="button" href="shop?server=${search.server }&searchType=${search.searchType }&searchWord=${search.searchWord }&row=${search.row}&sortType=1&sortOption=${search.sortOption % 2 + 1}">卖家</a></th>
								<th width="10%">图像</th>
								<th width="35%"><a class="button" href="shop?server=${search.server }&searchType=${search.searchType }&searchWord=${search.searchWord }&row=${search.row}&sortType=4&sortOption=${search.sortOption % 2 + 1}">产品名</a></th>
								<th width="10%"><a class="button" href="shop?server=${search.server }&searchType=${search.searchType }&searchWord=${search.searchWord }&row=${search.row}&sortType=5&sortOption=${search.sortOption % 2 + 1}">价格</a></th>
								<th>广告券</th>
							</tr>
							<c:if test="${empty adItems.data}">
							<tr style="background-color:#333333">
								<td colspan="5" align="center">
									<br/>
									<br/>
									<p><img src="images/notfound2.jpg"></img></p>
									<br/>
									<p>没有找到您搜索的物品，请确认输入的条件是否正确。</p>
									<br/>
									<p>或者您可以尝试<a href="shop?server=${search.server }" class="button red">刷新</a>本页。</p>
									<br/>
									<br/>
								</td>
							</tr>
							</c:if>
							<c:if test="${!empty adItems.data}">
							<c:forEach var="item" items="${adItems.data}">
							<tr style="background-color:#333333">
								<td><a href="shop?server=${search.server }&row=${search.row}&searchType=1&searchWord=${item.charName}">${item.charName}</a></td>
								<td align="center">
									<img src="http://rua.erinn.biz/itemimage2/files/${item.image }/${item.color1 }_${item.color2 }_${item.color3 }.gif"></img>
								</td>
								<td>
									<p style="font-family:cursive">
										<a href="shop?server=${search.server }&row=${search.row}&searchType=4&searchWord=${item.name}">${item.name}</a>
										<br/>
										<font color="${item.color1 }">■</font>${item.color1 }
										<font color="${item.color2 }">■</font>${item.color2 }
										<font color="${item.color3 }">■</font>${item.color3 }
									</p>
								</td>
								<td>${item.price }</td>
								<td>
									<c:if test="${!empty item.comment}">
										<font color="red">${item.comment }</font> 
										<br/>
									</c:if>
									剩余 ${item.count } 片
								</td>
							</tr>
							</c:forEach>
							</c:if>
						</table>
						
						<!-- 分页导航 -->
						<div class="wp-pagenavi">
							<div class="alignleft">
								<!-- 上一页 -->
								<c:if test="${adItems.pre}">
									<a href="shop?server=${search.server }&page=${search.page-1}&row=${search.row}&sortType=${search.sortType }&sortOption=${search.sortOption }&searchType=${search.searchType }&searchWord=${search.searchWord }">上一页</a>
								</c:if>
								<c:if test="${!empty adItems.data}">
									第${adItems.page }页
								</c:if>
								<!-- 下一页 -->
								<c:if test="${adItems.next}">
									<a href="shop?server=${search.server }&page=${search.page+1}&row=${search.row}&sortType=${search.sortType }&sortOption=${search.sortOption }&searchType=${search.searchType }&searchWord=${search.searchWord }">下一页</a>
								</c:if>
							</div>
							<div class="alignright"></div>
						</div>
					</div>
						
					<div class="clear"></div>
				</div><!-- content-wrapper END -->
			</div><!-- content-inner END -->
			
			<jsp:include page="footer.jsp"/>
		</div><!-- page END -->
		
	</body>
</html>
