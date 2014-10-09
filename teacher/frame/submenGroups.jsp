<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<script type="text/javascript" src="../js/sub_top.js"></script>

<div id="submenuWrapper">
<c:forEach items="${data }" var="e">
<div class="submemGroupstable_wrapper">
<table class="submemGroupstable">
	<tr>	   
	<td>
	<c:choose>
	<c:when test="${e.level==76 }">
		<img src='../img/top/leadericon.png'>
	</c:when>
	<c:otherwise>
		<img src='../img/top/follower_icon.png'>
	</c:otherwise>
	</c:choose>	
	</td>
	<td>
	<a href="${e.groupno }.bit" class="groupChangeBtn" id="${e.groupno }">${e.groupname}</a>
	</td>
	<td>
	<fmt:formatDate value="${e.created_date}" pattern="yyyy-MM-dd"/> 
	</td>
	</tr>
</table>	
</div>
</c:forEach>

<form class="form-horizontal" role="form">
<div class="form-group form-group-sm">
<table id="createGroups">
<tr>
<td>
<div id= "createGroups_btnWrapper">
<button id="createGroupsBtn" type="button" class="btn btn-primary">GROUP 생성</button>
</div>
</td>
<td id="createGroups_tdWrapper">
<div class="col-sm-11">
<input class="form-control" id="submenGroups_searchInput" type="text" name="findGroups">
</div>
</td>	
</tr>
</table>	
</div>
</form>
</div>   


