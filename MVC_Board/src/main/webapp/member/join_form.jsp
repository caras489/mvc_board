<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	// submit 작업을 수행하기 전 각 작업의 완료 여부를 체크할 변수(전역변수) 선언
	let isCheckConfirmPasswd = false; // 패스워드 중복확인 체크 변수
	
	// 1. ID 중복확인 버튼 클릭 시 새 창 띄우기
	// => MemberCheckId.me 서블릿 요청 -> member/check_id.jsp
	function checkDuplicateId() {
		window.open("MemberCheckId.me", "check", "width=400,height=200");
	}
	
	// 2. 비밀번호 입력란에 키를 누를때마다 비밀번호 길이 체크하기
	// => 체크 결과를 비밀번호 입력창 우측 빈공간에 표시하기
	// => 비밀번호 길이 체크를 통해 8 ~ 16글자 사이이면 "사용 가능한 패스워드"(파란색) 표시,
    // 아니면, "사용 불가능한 패스워드"(빨간색) 표시
	function checkPasswd(passwd) {
		// span 태그 영역(checkPasswdResult)
		var span_checkPasswdResult = document.getElementById("checkPasswdResult");
		
		// 입력된 패스워드 길이 체크
		if(passwd.length >= 8 && passwd.length <= 16) {
			span_checkPasswdResult.innerHTML = "사용 가능한 패스워드";
			span_checkPasswdResult.style.color = "BLUE";
		} else {
			span_checkPasswdResult.innerHTML = "사용 불가능한 패스워드";
			span_checkPasswdResult.style.color = "RED";
		}
	}
	
	// 3. 비밀번호확인 입력란에 키를 누를때마다 비밀번호와 같은지 체크하기
	// => 체크 결과를 비밀번호확인 입력창 우측 빈공간에 표시하기
	// => 비밀번호와 비밀번호확인 입력 내용이 같으면 "비밀번호 일치"(파란색) 표시,
	//    아니면, "비밀번호 불일치"(빨간색) 표시
	function checkConfirmPasswd(confirmPasswd) {
		// 패스워드 입력란에 입력된 패스워드를 가져오기
		var passwd = document.fr.passwd.value;
		
		var span_checkConfirmPasswdResult = document.getElementById("checkConfirmPasswdResult");
		
		// 패스워드 일치 여부 판별
		if(passwd == confirmPasswd) { // 일치할 경우
			span_checkConfirmPasswdResult.innerHTML = "비밀번호 일치";
			span_checkConfirmPasswdResult.style.color = "BLUE";
			
			// 패스워드 일치 여부 확인을 위해 isCheckConfirmPasswd 변수값을 true 로 변경
			isCheckConfirmPasswd = true;
		} else { // 일치하지 않을 경우
			span_checkConfirmPasswdResult.innerHTML = "비밀번호 불일치";
			span_checkConfirmPasswdResult.style.color = "RED";

			isCheckConfirmPasswd = false;
		}
	}
	
	// 4. 주민번호 숫자 입력할때마다 길이 체크하기
	// => 주민번호 앞자리 입력란에 입력된 숫자가 6자리이면 뒷자리 입력란으로 커서 이동시키기
	// => 주민번호 뒷자리 입력란에 입력된 숫자가 7자리이면 뒷자리 입력란에서 커서 제거하기
	function checkJumin1(jumin1) {
		if(jumin1.length == 6) {
			document.fr.jumin2.focus(); // 커서 요청(포커스 요청)
		}
	}
	
	function checkJumin2(jumin2) {
		if(jumin2.length == 7) {
			document.fr.jumin2.blur(); // 커서 제거(포커스 해제)
		}
	}
	
	// 5. 이메일 도메인 선택 셀렉트 박스 항목 변경 시 선택된 셀렉트 박스 값을 
	//    이메일 두번째 항목(@ 기호 뒤)에 표시하기
	//    단, 직접입력 선택 시 표시된 도메인 삭제하기
	function selectDomain(domain) {
		document.fr.email2.value = domain;
		
		// 만약, "직접입력" 항목 선택 시 도메인 입력창(email2)에 커서 요청(옵션)
		if(domain == "") { // "직접입력" 선택 시 domain 변수에 "" 값이 전달됨
// 			document.fr.email2.readonly = false; // 입력창 잠금 해제
			document.fr.email2.focus(); // 커서 요청
		} else { // 다른 도메인 선택 시
// 			document.fr.email2.readonly = true; // 입력창 잠금
		} 
	}
	
	// 6. 취미의 "전체선택" 체크박스 체크 시 취미 항목 모두 체크, 
	//    "전체선택" 해제 시 취미 항목 모두 체크 해제하기
	function checkAll(isChecked) {
		// 전체선택 체크 항목값(isChecked)에 따라 취미 항목 체크 또는 체크 해제
		if(isChecked) { // isChecked == true 와 동일한 조건식
			// 복수개의 동일한 name 값을 갖는 체크박스는 배열로 관리되므로 배열 인덱스 활용
			document.fr.hobby[0].checked = true;
			document.fr.hobby[1].checked = true;
			document.fr.hobby[2].checked = true;
		} else {
			document.fr.hobby[0].checked = false;
			document.fr.hobby[1].checked = false;
			document.fr.hobby[2].checked = false;
		}
	}
	
	function checkForm() {
		/*
		7. submit 버튼 클릭 시 필수 조건들이 만족할 경우에만 다음 페이지로 이동하기(submit())
	    - 아이디 중복 확인 버튼을 통해 아이디 중복 확인을 수행하고(버튼 클릭으로 대체(임시))
	    - 비밀번호와 비밀번호확인 두 개가 일치하고
	    - 주민번호 앞자리와 뒷자리가 모두 정상적으로 입력되고
	    - 이메일이 정상적으로 입력됐을 경우
	    => 위의 모든 조건이 만족할 경우 true 리턴하여 submit 작업을 수행하고
	       아니면, false 를 리턴하여 submit 작업을 취소
	    => 단, 현재 함수에서 다른 작업의 수행여부를 확인하려면
	       해당 작업 수행 후 전역변수를 사용하여 값을 변경해 놓아야함
	    */
	    // 아이디는 중복 확인 결과(중복확인 통과한 아이디)값이 입력되어 있지 않은지 확인하고
	    // 패스워드는 전역변수에 저장된 boolean 타입 값을 확인하고
	    // 주민번호, 이메일은 해당 입력값 자체를 가져와서 확인
	    if(document.fr.id.value == "") { // 아이디 중복확인을 수행하지 않은 경우(또는 중복인 경우)
	    	alert("아이디 중복확인 필수!");
	    	document.fr.id.focus();
	    	// 더 이상 작업이 진행되지 않고, submit 동작을 취소하기 위해 false 값 리턴
	    	return false; // 리턴값이 폼태그의 onsubmit="return checkForm()" 부분으로 전달되어
	    	// false 일 때 return false 가 되어 submit 동작이 취소됨(생략 시 true 리턴됨)
	    } else if(!isCheckConfirmPasswd) {
	    	alert("패스워드 확인 필수!");
	    	document.fr.passwd2.focus();
	    	return false;
	    } else if(document.fr.jumin1.value.length != 6) {
	    	alert("주민번호 앞자리 6자리 필수!");
	    	document.fr.jumin1.focus();
	    	return false;
	    } else if(document.fr.jumin2.value.length != 7) {
	    	alert("주민번호 뒷자리 7자리 필수!");
	    	document.fr.jumin2.focus();
	    	return false;
	    } else if(document.fr.email1.value.length == 0) {
	    	alert("이메일 계정 입력 필수!");
	    	document.fr.email1.focus();
	    	return false;
	    } else if(document.fr.email2.value.length == 0) {
	    	alert("이메일 도메인 입력 필수!");
	    	document.fr.email2.focus();
	    	return false;
	    }
	    
	    // 모든 조건을 통과했을 경우 submit 동작 실행
// 	    return true; // 생략 가능
	}
</script>
</head>
<body>
	<h1>회원 가입</h1>
	<form action="MemberJoinPro.me" name="fr" method="post" onsubmit="return checkForm()">
		<table border="1">
			<tr><td>이름</td><td><input type="text" name="name" required="required"></td></tr>
			<tr>
				<td>ID</td>
				<td>
					<input type="text" name="id" placeholder="중복확인 버튼 클릭" readonly="readonly" required="required">
					<input type="button" value="ID중복확인" onclick="checkDuplicateId()">
					<span id="checkIdResult"></span>
				</td>
			</tr>
			<tr>
				<td>비밀번호</td>
				<td>
					<input type="password" name="passwd" onkeyup="checkPasswd(this.value)" placeholder="8 ~ 16글자 사이 입력" required="required">
					<span id="checkPasswdResult"></span>
				</td>
			</tr>
			<tr>
				<td>비밀번호확인</td>
				<td>
					<input type="password" name="passwd2" onkeyup="checkConfirmPasswd(this.value)" required="required">
					<span id="checkConfirmPasswdResult"></span>
				</td>
			</tr>
			<tr>
				<td>E-Mail</td>
				<td>
					<input type="text" name="email1" required="required">@
					<input type="text" name="email2" required="required">
					<select name="emailDomain" onchange="selectDomain(this.value)">
						<option value="">직접입력</option>
						<option value="naver.com">naver.com</option>
						<option value="nate.com">nate.com</option>
						<option value="daum.net">daum.net</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>성별</td>
				<td>
					<input type="radio" name="gender" value="남">남
					<input type="radio" name="gender" value="여">여
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" value="가입">
					<input type="reset" value="초기화">
					<input type="button" value="돌아가기" onclick="history.back()">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>