package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDeleteProService;
import vo.ActionForward;

public class BoardDeleteProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardDeleteProAction");
		
		ActionForward forward = null;
		
		// 전달받은 파라미터 가져오기
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		String pageNum = request.getParameter("page");
		String board_pass = request.getParameter("board_pass");
		
		// BoardDeleteProService 클래스의 isArticleWriter() 메서드를 호출하여 패스워드 판별 요청
		// => 파라미터 : 글번호(board_num), 패스워드(board_pass)
		//    리턴타입 : boolean(isArticleWriter)
		BoardDeleteProService service = new BoardDeleteProService();
		boolean isArticleWriter = service.isArticleWriter(board_num, board_pass);
		
		// 패스워드가 일치하지 않을 경우 자바스크립트를 사용하여
		// "삭제 권한이 없습니다!" 출력 후 이전페이지로 돌아가기
		if(!isArticleWriter) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('삭제 권한이 없습니다!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 패스워드가 일치할 경우(= 삭제 권한 있음)
			// 글번호(board_num)를 사용하여 삭제 작업 수행 
			// => service 클래스의 removeArticle() 메서드 호출
			//    리턴타입 : boolean(isDeleteSuccess)
			boolean isDeleteSuccess = service.removeArticle(board_num);
			
			// 삭제 작업 후 리턴받은 결과 판별
			// 삭제 실패 시(isDeleteSuccess 가 false) 자바스크립트를 통해
			// "삭제 실패!" 출력하고 이전페이지로 돌아가기
			if(!isDeleteSuccess) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('삭제 실패!')");
				out.println("history.back()");
				out.println("</script>");
			} else {
				// 삭제 성공 시(isDeleteSuccess 가 true) BoardList.bo 페이지 포워딩 설정
				// => 페이지 번호를 URL 에 포함시켜 포워딩
				forward = new ActionForward();
				forward.setPath("BoardList.bo?page=" + pageNum);
				forward.setRedirect(true);
			}
		}
		
		return forward;
	}
}