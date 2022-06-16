<%@page import="vo.BoardDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 게시판</title>
<style type="text/css">
	#articleForm {
		width: 500px;
		height: 550px;
		border: 1px solid red;
		margin: auto;
	}
	
	h2 {
		text-align: center;
	}
	
	table {
		border: 1px solid black;
		border-collapse: collapse; 
	 	width: 500px;
	}
	
	th {
		text-align: center;
	}
	
	td {
		width: 150px;
		text-align: center;
	}
	
	#basicInfoArea {
		height: 70px;
		text-align: center;
	}
	
	#articleContentArea {
		background: orange;
		margin-top: 20px;
		height: 350px;
		text-align: center;
		overflow: auto;
		white-space: pre-line;
	}
	
	#commandList {
		margin: auto;
		width: 500px;
		text-align: center;
	}
</style>
</head>
<body>
	<!-- 게시판 상세내용 보기 -->
	<section id="articleForm">
		<h2>글 상세내용 보기</h2>
		<section id="basicInfoArea">
			<table border="1">
			<tr><th width="70">제 목</th><td colspan="3" >${article.getBoard_subject() }</td></tr>
			<tr>
				<th width="70">작성자</th><td>${article.getBoard_name() }</td>
				<th width="70">작성일</th><td>${article.getBoard_date() }</td>
			</tr>
			<tr>
				<th width="70">첨부파일</th>
				<td>
					<!-- 
					파일 다운로드 링크 사용법(중복 파일명 처리로 인해 실제 파일과 업로드 한 파일명이 다를 수 있음)
					<a href="다운로드 할 실제 파일명(위치포함)" download>화면에 보여줄 파일명</a>
					-->
					<a href="./upload/${article.getBoard_real_file() }" download="${article.getBoard_file() }">
						${article.getBoard_file() }
					</a>
				</td>
				<th width="70">조회수</th>
				<td>${article.getBoard_readcount() }</td>
			</tr>
			</table>
		</section>
		<section id="articleContentArea">
			${article.getBoard_content() }
		</section>
	</section>
	<section id="commandList">
		<input type="button" value="답변" onclick="location.href='BoardReplyForm.bo?board_num=${param.board_num}&page=${param.page}'">
		<input type="button" value="수정" onclick="location.href='BoardModifyForm.bo?board_num=${param.board_num}&page=${param.page}'">
		<input type="button" value="삭제" onclick="location.href='BoardDeleteForm.bo?board_num=${param.board_num}&page=${param.page}'">
		<input type="button" value="목록" onclick="location.href='BoardList.bo?page=${param.page}'">
	</section>
</body>
</html>
















