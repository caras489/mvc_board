<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	// MemberCheckId.me 서블릿 주소 뒤에 id 파라미터나 isDuplicate 파라미터가 있을 경우
	// (http://localhost:8080/MVC_Board/MemberCheckId.me?id=admin&isDuplicate=false)
	// 아이디 중복 확인 작업을 끝낸 후에 현재 페이지로 돌아왔다(새로고침)는 의미이다.
	// 따라서, 중복 확인 작업이 끝낸 후 돌아왔을 때
	// 1) 아이디 입력창에 검사 완료된 아이디를 표시하고
	// 2) 검사 결과 출력 영역(div 태그)에 검사 결과를 표시
	// 만약, 주소 뒤에 파라미터가 없을 경우 처음 열린 페이지이므로 수행할 작업이 아무것도 없음
	window.onload = function() { // body 영역 로딩 완료 시 수행할 익명 함수 정의 
		// body 태그 속성으로 <body onload="check()"> 형식으로 사용 가능
		// 1. URL 파라미터에서 id 파라미터가 존재하는지 확인
		<%if(request.getParameter("id") != null) {%>
			let id = "<%=request.getParameter("id")%>";
			document.fr.id.value = id; // 아이디 입력란에 검사 완료된 아이디 표시
			
			// 2. 검사 결과 값을 boolean 타입으로 변환하여 저장하기
			let isDuplicate = "<%=request.getParameter("isDuplicate")%>";
// 			alert(isDuplicate + ", " + typeof(isDuplicate));
			
			if(isDuplicate == "true") { // = 아이디 중복일 경우
				document.getElementById("checkIdResult").innerHTML = "이미 사용중인 아이디";
				document.getElementById("checkIdResult").style.color = "RED";
			} else { // 아이디가 중복이 아닐 경우
				let btn = "<input type='button' value='아이디 사용' onclick='useId()'>";
				
				document.getElementById("checkIdResult").innerHTML = "사용 가능한 아이디<br>" + btn;
				document.getElementById("checkIdResult").style.color = "GREEN";
			}
		<%}%>
	}
	
	function useId() {
		// 부모창(join_form.jsp)의 ID 입력란에 중복 확인 완료된 ID 값 표시
		window.opener.document.fr.id.value = document.fr.id.value;
		
		// 현재 자식창(check_id.jsp) 닫기
		window.close();
	}
	
	function checkId() {
		// 입력된 아이디 가져와서 id 변수에 저장
		let id = document.fr.id.value;
		
		if(id.length >= 4 && id.length <= 8) { // 아이디 규칙이 적합할 경우
			// 아이디 중복 확인을 위한 비즈니스 로직 처리를 위해 CheckIdDuplicate.me 요청
			// => 요청 URL 에 입력된 id 값을 id 라는 파라미터명으로 전달
			location.href = "CheckIdDuplicate.me?id=" + id; 
			// http://localhost:8080/MVC_Board/CheckIdDuplicate.me?id=admin
		} else { // 아이디 규칙이 적합하지 않을 경우
			alert("4~8글자만 사용 가능합니다.");
			document.getElementById("checkIdResult").innerHTML = "";
			document.fr.id.select(); // 입력항목 포커스 요청 및 블럭지정
		}
	}
	
	// document.getElementById("checkIdResult").innerHTML = "중복확인완료";
</script>
</head>
<body>
	<h1>ID 중복 체크</h1>
	<form action="" name="fr">
		<input type="text" name="id" placeholder="4 ~ 8글자 문자, 숫자 조합 필수!">
		<input type="button" value="중복확인" onclick="checkId()"><br>
		<div id="checkIdResult"></div>
	</form>
</body>
</html>









