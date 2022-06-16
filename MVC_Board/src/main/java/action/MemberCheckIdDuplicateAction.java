package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.MemberCheckIdDuplicateService;
import vo.ActionForward;

public class MemberCheckIdDuplicateAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("MemberCheckIdDuplicateAction");
		
		ActionForward forward = null;
		
		// URL 파라미터로 전달받은 id 가져와서 저장
		String id = request.getParameter("id");

		// MemberCheckIdDuplicateService 의 isDuplicateId() 메서드
		// -> MemberDAO 의 isDuplicateId() 메서드를 통해 중복 여부 조회
		// => 파라미터 : id     리턴타입 : boolean(isDuplicate)
		MemberCheckIdDuplicateService service = new MemberCheckIdDuplicateService();
		boolean isDuplicate = service.isDuplicateId(id);
		
		// ActionForward 객체를 사용하여 MemberCheckId.me 서블릿 주소 요청
		// => URL 파라미터로 검사한 아이디와 검사 결과를 전달
		// => 주소 변경을 위해 Redirect 방식 포워딩
		forward = new ActionForward();
		forward.setPath("MemberCheckId.me?id=" + id + "&isDuplicate=" + isDuplicate);
		forward.setRedirect(true);
		// ex) http://localhost:8080/MVC_Board/MemberCheckId.me?id=admin&isDuplicate=false
		
		return forward;
	}

}










