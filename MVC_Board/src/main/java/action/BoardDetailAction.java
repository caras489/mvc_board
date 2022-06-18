package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardDetailAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardDetailAction");
		
		ActionForward forward = null;
		
		// request 객체를 통해 전달받은 파라미터(board_num) 가져오기
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		
		// BoardDetailService 인스턴스 생성 및 getArticle() 메서드 호출
		// => 파라미터 : 글번호(board_num)    리턴타입 : BoardDTO(article)
		BoardDetailService service = new BoardDetailService();
		BoardDTO article = service.getArticle(board_num);
		
		// 조회수 증가 작업 요청(단, 게시물 조회 성공 시에만 수행)
		if(article != null) {
			System.out.println("조회수 증가");
			// BoardDetailService 객체의 increaseReadcount() 메서드 호출
			// => 파라미터 : 글번호(board_num)
			service.increaseReadcount(board_num);
		}
		
		// request 객체의 setAttribute() 메서드 호출하여 
		// BoardDTO("article") 객체 저장
		request.setAttribute("article", article);
		
		// ActionForward 객체 생성 및 포워딩 정보 설정(Dispatcher 방식)
		// => board 폴더의 qna_board_view.jsp
		forward = new ActionForward();
		forward.setPath("board/qna_board_view.jsp");
		forward.setRedirect(false);
		
		return forward;
	}

}