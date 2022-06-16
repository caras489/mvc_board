package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.MemberJoinProService;
import vo.ActionForward;
import vo.MemberDTO;

public class MemberJoinProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("MemberJoinProAction");
		ActionForward forward = null;
		
		// 폼 파라미터 가져와서 MemberDTO 객체에 저장
		MemberDTO member = new MemberDTO();
		member.setName(request.getParameter("name"));
		member.setId(request.getParameter("id"));
		member.setPasswd(request.getParameter("passwd"));
		member.setEmail(request.getParameter("email1") + "@" + request.getParameter("email2"));
		member.setGender(request.getParameter("gender"));
//		System.out.println(member.toString());
		
		// MemberJoinProService 클래스의 registMember() 메서드를 호출하여 회원 등록 요청
		// => 파라미터 : MemberDTO 객체(member)    리턴타입 : boolean(isRegistSuccess)
		MemberJoinProService service = new MemberJoinProService();
		boolean isRegistSuccess = service.registMember(member);
		
		// 등록 작업 요청 결과에 따른 판별 작업 수행
		if(!isRegistSuccess) { // 등록 실패
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('회원 가입 실패!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 등록 성공
			// ActionForward 객체를 통해 메인페이지 포워딩 설정
			forward = new ActionForward();
			forward.setPath("./");
			forward.setRedirect(true);
		}
		
		return forward;
	}

}












