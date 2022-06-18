package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardModifyProService;
import svc.BoardReplyProService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardReplyProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardReplyProAction");
		
		ActionForward forward = null;
		
		BoardDTO article = new BoardDTO();
		article.setBoard_num(Integer.parseInt(request.getParameter("board_num")));
		article.setBoard_name(request.getParameter("board_name"));
		article.setBoard_pass(request.getParameter("board_pass"));
		article.setBoard_subject(request.getParameter("board_subject"));
		article.setBoard_content(request.getParameter("board_content"));
		article.setBoard_re_ref(Integer.parseInt(request.getParameter("board_re_ref")));
		article.setBoard_re_lev(Integer.parseInt(request.getParameter("board_re_lev")));
		article.setBoard_re_seq(Integer.parseInt(request.getParameter("board_re_seq")));
//		System.out.println(article);
		
		// BoardReplyProService 의 replyArticle() 메서드를 호출하여 답글 등록 작업 요청
		// => 파라미터 : BoardDTO 객체(article)   리턴타입 : boolean(isReplySuccess)
		BoardReplyProService service = new BoardReplyProService();
		boolean isReplySuccess = service.replyArticle(article);
		
		// 답글 등록 작업 요청 처리 결과 판별
		// => 실패 시 자바스크립트를 사용하여 "답글 등록 실패!" 출력 후 이전페이지
		// => 성공 시 BoardList.bo 페이지로 포워딩(페이지 번호 전달)
		if(!isReplySuccess) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('답글 등록 실패!')");
			out.println("history.back()");
			out.println("</script>");
		} else {
			forward = new ActionForward();
			forward.setPath("BoardList.bo?page=" + request.getParameter("page"));
			forward.setRedirect(true); // Redirect 방식
		}
		
		return forward;
	}

}