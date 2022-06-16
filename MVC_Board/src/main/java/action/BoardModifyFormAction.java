package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardModifyFormAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardModifyFormAction");
		
		ActionForward forward = null;
		
		// request 객체를 통해 전달받은 파라미터 가져오기
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		
		// 글 수정에 필요한 데이터를 조회하기 위해
		// 이미 만들어진 BoardDetailService 클래스의 getArticle() 메서드를 호출하여
		// 게시물 상세 정보를 리턴받아 qna_board_modify.jsp 페이지로 포워딩
		// => 단, 조회수 증가 작업은 수행하지 않음
		BoardDetailService service = new BoardDetailService();
		BoardDTO article = service.getArticle(board_num);
		
		// request 객체에 BoardDTO 객체 저장
		request.setAttribute("article", article);
		
		// board/qna_board_modify.jsp 페이지로 포워딩 설정 => Dispatcher 방식
		forward = new ActionForward();
		forward.setPath("./board/qna_board_modify.jsp");
		forward.setRedirect(false);
		
		return forward;
	}

}













