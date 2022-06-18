package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardModifyProService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardModifyProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardModifyProAction");
		
		ActionForward forward = null;
		
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		String board_name = request.getParameter("board_name");
		String board_pass = request.getParameter("board_pass");
		String board_subject = request.getParameter("board_subject");
		String board_content = request.getParameter("board_content");
		
		// 게시물 수정 권한 판별을 위해 전달받은 파라미터 중 패스워드 비교
		// => BoardModifyProService 의 isArticleWriter() 메서드 호출
		BoardModifyProService service = new BoardModifyProService();
		boolean isArticleWriter = service.isArticleWriter(board_num, board_pass);
		
		// 수정 가능 여부 판별
		if(!isArticleWriter) { // 패스워드가 일치하지 않을 경우(= 수정 권한 없음)
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('수정 권한이 없습니다!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 패스워드가 일치할 경우(= 수정 권한 있음)
			// 수정 가능할 경우 BoardModifyProService - modifyArticle() 메서드 호출하여 수정 요청
			// => 파라미터 : BoardDTO 객체(article)
			// => 리턴타입 : boolean(isModifySuccess)
			BoardDTO article = new BoardDTO();
			article.setBoard_num(board_num);
			article.setBoard_name(board_name);
			article.setBoard_subject(board_subject);
			article.setBoard_content(board_content);
			
			boolean isModifySuccess = service.modifyArticle(article);
			
			// 수정 완료 결과 판별 후 실패 시 자바스크립트로 "수정 실패!" 출력 후 이전페이지,
			// 성공 시 BoardDetail.bo 로 포워딩 요청
			if(!isModifySuccess) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('수정 실패!')");
				out.println("history.back()");
				out.println("</script>");
			} else {
				// 수정 성공 시(isModifySuccess 가 true) BoardList.bo 페이지 포워딩 설정
				// => 페이지 번호를 URL 에 포함시켜 포워딩
				forward = new ActionForward();
				forward.setPath("BoardList.bo?page=" + request.getParameter("page"));
				forward.setRedirect(true);
			}
		}
		
		return forward;
	}

}