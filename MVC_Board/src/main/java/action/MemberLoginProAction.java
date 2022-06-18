package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import svc.MemberJoinProService;
import svc.MemberLoginProService;
import vo.ActionForward;
import vo.MemberDTO;

public class MemberLoginProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("MemberLoginProAction");
		ActionForward forward = null;
		
		// 폼 파라미터 가져와서 MemberDTO 객체에 저장
		MemberDTO member = new MemberDTO();
		member.setId(request.getParameter("id"));
		member.setPasswd(request.getParameter("passwd"));
//		System.out.println(member.toString());
		
		// MemberLoginProService 클래스의 loginMember() 메서드를 호출하여 로그인 판별 요청
		// => 파라미터 : MemberDTO 객체(member)    리턴타입 : boolean(isMember)
		MemberLoginProService service = new MemberLoginProService();
		boolean isMember = service.loginMember(member);
		
		// 로그인 판별 작업 요청 결과에 따른 판별 작업 수행
		if(!isMember) { // 로그인 실패
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('아이디 또는 패스워드 틀림!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 등록 성공
			// request 객체의 getSession() 메서드를 호출하여 HttpSesson 객체를 얻어오기
			HttpSession session = request.getSession();
			// HttpSession 객체의 setAttribute() 메서드를 호출하여 세션 아이디값 저장(속성명 : sId)
			session.setAttribute("sId", request.getParameter("id"));
			
			// ActionForward 객체를 통해 메인페이지 포워딩 설정
			forward = new ActionForward();
			forward.setPath("./");
			forward.setRedirect(true);
		}
		
		return forward;
	}

}