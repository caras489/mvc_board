package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.BoardWriteProAction_Temp;
import vo.ActionForward;

// 서블릿 주소가 xxx.bo 로 끝날 경우 BoardFrontController 클래스로 해당 요청이 전달됨
//@WebServlet("*.bo")
public class BoardFrontController_Backup extends HttpServlet {
	
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("BoardFrontController");
		
		// POST 방식 요청에 대한 한글 처리를 위해 인코딩 방식을 UTF-8 로 변경
		request.setCharacterEncoding("UTF-8");
		
		// ------------------------------------------------------------------
		// 주소표시줄에 입력된 URL 에서 서블릿 주소 부분을 가져와서
		// 판별을 통해 수행해야할 동작을 결정하기 위해 서블릿 주소 추출 과정
		// ex) BoardList.bo 일 때와 BoardWriteForm.bo 일 때의 동작이 다르므로
		//     URL 에서 서블릿 주소(프로젝트명 뒷부분 "/xxx.bo") 를 추출한 후
		//     문자열 비교를 통해 서블릿 주소 판별 작업을 수행해야함
		// 0. 참고) 요청 주소(URL) 전체 추출
//		String requestURL = request.getRequestURL().toString();
//		System.out.println("requestURL : " + requestURL);
		
		// 1. 요청 주소 중 URI 부분(/프로젝트명/서블릿주소) 추출
//		String requestURI = request.getRequestURI();
//		System.out.println("requestURI : " + requestURI);
	
		// 2. 요청 주소 중 컨텍스트 경로(/프로젝트명) 추출
//		String contextPath = request.getContextPath();
//		System.out.println("contextPath : " + contextPath);
		
		// 3. 요청 주소 중 서블릿 주소 부분(/서블릿주소) 추출
		// => requestURI 와 contextPath 를 가공하여 추출
		// 1) String 객체의 replace() 메서드를 통해 contextPath 부분을 널스트링("") 으로 치환(제거)
//		String command = requestURI.replace(contextPath, "");
		// 2) String 객체의 substring() 메서드를 호출하여 문자열 추출(= 부분 문자열 구하기)
		// => 컨텍스트경로의 길이를 시작 인덱스 번호로 활용하여 URL 의 끝까지 추출하기
//		String command = requestURI.substring(contextPath.length());
		
		// --------------------------------------------------------------------
		// 위의 1 ~ 3번 과정을 하나의 메서드로 압축하여 제공 - getServletPath()
		String command = request.getServletPath();
		// --------------------------------------------------------------------
		System.out.println("서블릿 주소(command) : " + command);
		
		// Action 클래스를 공통으로 다루기 위한 Action 인터페이스 타입 변수 선언
		Action action = null;
		
		// 포워딩 작업에 필요한 ActionForward 타입 변수 선언
		ActionForward forward = null;

		// 추출된 서블릿 주소를 if 문을 사용하여 판별하고, 각 주소에 따른 액션(작업) 요청
		// ex) "/BoardWriteForm.bo" 일 경우 글쓰기 폼 페이지 이동을 위해 qna_board_write.jsp 로 이동
		// ex) "/BoardList.bo" 일 경우 글 목록 출력을 위해 비즈니스 로직 요청
		if(command.equals("/BoardWriteForm.bo")) {
			// 글쓰기 폼 표시를 위한 View 페이지(*.jsp) 로 포워딩
			// 별도의 비즈니스 로직(= DB 작업) 없이 뷰 페이지로 바로 연결
			// => 이 때, JSP 페이지의 URL 이 주소표시줄에 노출되지 않고
			//    이전의 요청 주소인 서블릿 주소를 그대로 유지해야하므로 Dispatcher 방식 포워딩
//			RequestDispatcher dispatcher = 
//					request.getRequestDispatcher("./board/qna_board_write.jsp");
//			dispatcher.forward(request, response);
			
			// ActionForward 객체를 생성한 후 URL 과 포워딩 방식(Dispatcher) 저장
			forward = new ActionForward();
			forward.setPath("./board/qna_board_write.jsp"); // 포워딩 할 URL 저장
			forward.setRedirect(false); // 포워딩 방식(Dispatcher) 저장(생략 가능 = 기본값 false)
		} else if(command.equals("/BoardWritePro.bo")) {
			// 글쓰기 비즈니스 로직 BoardWriteProAction - x - DB 수행
//			BoardWriteProAction action = new BoardWriteProAction();
			// => Action 인터페이스가 공통 타입으로 Action 타입으로 업캐스팅 가능
			action = new BoardWriteProAction_Temp();
			
			// Action 클래스의 execute() 메서드를 호출하여 작업을 수행하고
			// 포워딩 정보가 저장된 ActionForward 객체 리턴받기
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardList.bo")) {
			// 글목록 비즈니스 로직 BoardListAction
//			action = new BoardListAction();
//			try {
//				forward = action.execute(request, response);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		
		// ActionForward 객체에 저장된 정보에 따라 각각 다른 포워딩 작업 수행
		// => isRedirect() 메서드로 방식을 판별하고, 포워딩 주소는 getPath() 활용
		// 1. ActionForward 객체가 존재하는지 판별(존재할 경우에만 작업 수행)
		if(forward != null) {
			// 2. ActionForward 객체의 포워딩 방식 비교
			// => isRedirect 가 true 일 경우 Redirect 방식, 아니면 Dispatcher 방식
			if(forward.isRedirect()) { 
				// Redirect 방식으로 포워딩
				response.sendRedirect(forward.getPath());
			} else {
				// Dispatcher 방식으로 포워딩
				RequestDispatcher dispatcher = request.getRequestDispatcher(forward.getPath());
				dispatcher.forward(request, response);
			}
		}
		
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

}












