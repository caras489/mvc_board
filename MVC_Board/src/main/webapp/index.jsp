<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
// 세션 객체에 저장된 세션 아이디("sId") 가져와서 변수에 저장
String sId = (String)session.getAttribute("sId");
%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	function confirmLogout() {
		if(confirm("로그아웃 하시겠습니까?")) {
			// MemberLogout.me 서블릿 주소 요청
			location.href = "./MemberLogout.me";
		}
	}
</script>
</head>
<body>
	<header>
		<div align="right">
			<!-- 세션 아이디가 null 또는 "" 일 경우 로그인, 회원가입 링크 표시 -->
			<%if(sId == null || sId.equals("")) { %>
				<h5><a href="./MemberLoginForm.me">로그인</a> | <a href="./MemberJoinForm.me">회원가입</a></h5>
			<%} else { %>
				<h5>
					<a href="./MemberInfo.me"><%=sId %></a> 님 |
					<a href="javascript:void(0)" onclick="confirmLogout()">로그아웃</a>
<!-- 					<a href="#" onclick="confirmLogout()">로그아웃</a>  -->
					<!-- 
					하이퍼링크 클릭 시 자바스크립트를 실행해야할 경우
					href 속성에 # 또는 javscript:void(0) 를 지정하면 페이지 갱신 없이 실행됨
					-->
				</h5>
			<%} %>
		</div>
	</header>
	<h1>MVC_Board 메인페이지</h1>
	<h3><a href="./BoardWriteForm.bo">글쓰기</a></h3>
	<h3><a href="./BoardList.bo">글목록</a></h3>
</body>
</html>















