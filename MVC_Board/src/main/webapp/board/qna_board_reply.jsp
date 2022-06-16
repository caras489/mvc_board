<%@page import="vo.BoardDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
// request 객체에 저장된 BoardDTO 객체("article") 가져오기
BoardDTO article = (BoardDTO)request.getAttribute("article");
%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 게시판</title>
<style type="text/css">
	#replyForm {
		width: 500px;
		height: 450px;
		border: 1px solid red;
		margin: auto;
	}
	
	h1 {
		text-align: center;
	}
	
	table {
		margin: auto;
		width: 450px;
	}
	
	.td_left {
		width: 150px;
		background: orange;
		text-align: center;
	}
	
	.td_right {
		width: 300px;
		background: skyblue;
	}
	
	#commandCell {
		text-align: center;
	}
</style>
</head>
<body>
	<!-- 게시판 답글 작성 -->
	<section id="replyForm">
		<h1>게시판 답글 작성</h1>
		<form action="./BoardReplyPro.bo" name="boardForm" method="post">
			<!-- input type="hidden" 사용하여 글번호(board_num)와 페이지번호(page) 전달 -->
			<input type="hidden" name="board_num" value="<%=article.getBoard_num()%>">
			<input type="hidden" name="page" value="<%=request.getParameter("page")%>">
			<!-- 답글에 대한 원본글 관련 정보를 담는 board_re_ref, board_re_lev, board_re_seq 도 전달 -->
			<input type="hidden" name="board_re_ref" value="<%=article.getBoard_re_ref()%>">
			<input type="hidden" name="board_re_lev" value="<%=article.getBoard_re_lev()%>">
			<input type="hidden" name="board_re_seq" value="<%=article.getBoard_re_seq()%>">
			<table>
				<tr>
					<td class="td_left"><label for="board_name">글쓴이</label></td>
					<td class="td_right">
						<input type="text" name="board_name" required="required" />
					</td>
				</tr>
				<tr>
					<td class="td_left"><label for="board_pass">비밀번호</label></td>
					<td class="td_right">
						<input type="password" name="board_pass" required="required" />
					</td>
				</tr>
				<!-- 답글 작성 시 원본글의 제목, 내용은 표시 -->
				<tr>
					<td class="td_left"><label for="board_subject">제목</label></td>
					<td class="td_right">
						<input type="text" name="board_subject" value="Re:<%=article.getBoard_subject() %>" required="required" />
					</td>
				</tr>
				<tr>
					<td class="td_left"><label for="board_content">내용</label></td>
					<td class="td_right">
						<textarea id="board_content" name="board_content" cols="40" rows="15" required="required">
							-------- 원본 글 내용 --------
							<%=article.getBoard_content() %>
						</textarea>
					</td>
				</tr>
			</table>
			<section id="commandCell">
				<input type="submit" value="답글등록">&nbsp;&nbsp;
				<input type="reset" value="다시쓰기">&nbsp;&nbsp;
				<input type="button" value="취소" onclick="history.back()">
			</section>
		</form>
	</section>
</body>
</html>








