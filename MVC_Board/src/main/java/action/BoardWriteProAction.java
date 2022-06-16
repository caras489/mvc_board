package action;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import svc.BoardWriteProService;
import vo.ActionForward;
import vo.BoardDTO;

/*
 * XXXAction 클래스가 공통으로 갖는 execute() 메서드를 직접 정의하지 않고
 * Action 인터페이스를 상속받아 추상메서드를 구현하여 실수를 예방 가능
 * => 추상메서드 execute() 구현을 강제 => 코드의 통일성과 안정성 향상
 */
public class BoardWriteProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardWriteProAction");
		
		// 포워딩 정보를 저장하는 ActionForward 타입 변수 선언
		ActionForward forward = null;
		
		// --------------------------------------------------------
		// 옵션. 작성자의 IP 주소를 가져오기
		// request 객체의 getRemoteAddr() 메서드를 호출
//		String userIpAddr = request.getRemoteAddr();
//		System.out.println(userIpAddr);
		// --------------------------------------------------------
		// 비즈니스 로직(데이터베이스 처리)을 위한 데이터 준비 작업 수행
		// => 글쓰기 폼에서 작성 후 글쓰기 버튼 클릭 시 현재 객체로 이동
		// => 폼 파라미터를 가져와서 준비 작업 수행(= 게시물 정보 저장)
		// 주의! form 태그에서 enctype="multipart/form-data" 를 명시했을 경우
		// 파라미터들은 request 객체에서 바로 접근할 수 없다!
//		String board_name = request.getParameter("board_name");
//		System.out.println(board_name); // null 값 출력됨
		// multipart/form-data 타입의 경우 com.oreilly.servlet.MultipartRequest 객체를 통해 접근 필요
		// => www.servlets.com/cos/ 사이트에서 cos-20.08.zip 파일 다운로드 후 압축 풀고
		//    cos.jar 라이브러리를 프로젝트에 등록
		
		// 파일 업로드 관련 정보 처리를 위해 MultiPartRequest 객체 활용
		// 1. 업로드 할 파일이 저장되는 이클립스 프로젝트 상의 경로를 변수에 저장
		String uploadPath = "upload";
		
		// 2. 업로드 가능한 파일의 크기를 정수 형태로 지정(10MB 제한 설정)
//		int fileSize = 10485760; // 10MB 크기를 직접 지정 시(2진수 기준으로 계산된 크기)
		// 유지보수를 유용하게 하기 위해서 크기 지정 등의 단위 사용 시
		// 기본 단위부터 계산 과정을 차례대로 명시하면 편리함(MB 의 경우 byte -> KB -> MB 로 명시)
		int fileSize = 1024 * 1024 * 10; // byte(1) -> KB(1024Byte) -> MB(1024KB) -> 10MB 단위로 변환
		
		// 3. 현재 프로젝트(서블릿)를 처리하는 객체인 서블릿 컨텍스트 객체 얻어오기
		// => request 객체의 getServletContext() 메서드를 호출
		ServletContext context = request.getServletContext();
		
		// 4. 업로드 할 파일이 저장되는 실제 경로를 얻어오기
		// => ServletContext 객체의 getRealPath() 메서드를 호출
		String realPath = context.getRealPath(uploadPath); // 가상의 업로드 폴더명을 파라미터로 전달
		System.out.println(realPath);
		// D:\Shared\JSP\workspace_jsp3_model2\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\MVC_Board\ upload 폴더
		// => 실제 업로드 될 폴더 위치(워크스페이스 내의 프로젝트 폴더에 있는 upload 폴더는 가상 폴더)
		
		// 5. 작성된 게시물 정보는 폼 파라미터 형태로 전달되어 request 객체에 저장되어 있으므로
		//    해당 파라미터를 가져와서 BoardDTO 객체에 저장해야함
		//    단, multipart/form-data 형식이므로 MultipartRequest 객체를 통해 가져와야 하며
		//    MultipartRequest 객체 생성 시 request 객체를 전달하여 데이터를 관리해야함
		//    (cos.jar 라이브러리 필수!)
		MultipartRequest multi = new MultipartRequest(
				request, // 1) 실제 요청 정보가 포함된 request 객체
				realPath, // 2) 실제 업로드 폴더 경로 
				fileSize, // 3) 업로드 파일 크기(10MB 제한)
				"UTF-8", // 4) 한글 파일명에 대한 인코딩 방식 
				new DefaultFileRenamePolicy()); // 5) 중복 파일명에 대한 이름 변경 처리를 담당하는 객체
		// => 객체 생성 시점에 이미 업로드 파일이 폴더에 실제 업로드 됨
		
		// 6. MultipartRequest 객체의 getParameter() 메서드를 호출하여 폼 파라미터 데이터 가져오기
		// => 주의! request.getParameter() 메서드가 아님!
		// => 가져온 데이터는 BoardDTO 객체에 저장
		BoardDTO board = new BoardDTO();
		board.setBoard_name(multi.getParameter("board_name"));
		board.setBoard_pass(multi.getParameter("board_pass"));
		board.setBoard_subject(multi.getParameter("board_subject"));
		board.setBoard_content(multi.getParameter("board_content"));
		
		// 주의! 파일 정보는 getParameter() 메서드로 가져올 수 없으며 별도의 추가 작업 필요
//		board.setBoard_file(multi.getParameter("board_file")); // null
		// 1) 파일명을 관리하는 객체에 접근하여 파일 정보 가져오기(board_file 파라미터명 가져오기)
		String fileElement = multi.getFileNames().nextElement().toString();
		// 2) 1번 작업을 통해 가져온 이름을 사용하여 원본 파일명과 실제 업로드 된 파일명 가져오기
		String board_file = multi.getOriginalFileName(fileElement); 
		String board_real_file = multi.getFilesystemName(fileElement);
//		System.out.println("원본 파일명 : " + board_file + ", 실제 파일명 : " + board_real_file);
		
		board.setBoard_file(board_file);
		board.setBoard_real_file(board_real_file);
//		System.out.println(board); // toString() 메서드 생략 가능
		// --------------------------------------------------------------------------
		// BoardWriteProService 클래스의 인스턴스 생성 후 
		// registArticle() 메서드 호출하여 글쓰기 작업 요청
		// => 파라미터 : BoardDTO 객체     리턴타입 : boolean(isWriteSuccess)
		BoardWriteProService service = new BoardWriteProService();
		boolean isWriteSuccess = service.registArticle(board);
		
		// Service 클래스로부터 글쓰기 작업 요청 처리 결과를 전달받아 성공/실패 여부 판별
		if(!isWriteSuccess) { // 글쓰기 실패 시(결과값이 false 일 경우)
			// 자바스크립트를 통해 "글쓰기 실패!" 출력하고 이전페이지로 돌아가기
			// => 자바 클래스에서 웹브라우저를 통해 HTML 코드 등을 출력하려면
			//    response 객체를 통해 문서 타입 설정 및 PrintWriter() 객체를 통해 태그 출력하기
			// 1) response 객체의 setContentType() 메서드를 호출하여 문서 타입(ContentType) 지정
			//    => jsp 파일 맨 위에 page 디렉티브 내의 contentType=XXXX 항목과 동일
			response.setContentType("text/html; charset=UTF-8");
			// 2) response 객체의 getWrite() 메서드를 호출하여 출력스트림 PrintWriter 객체 얻어오기
			PrintWriter out = response.getWriter();
			// 3) PrintWriter 객체의 println() 메서드를 호출하여 출력할 태그 작성
			out.println("<script>");
			out.println("alert('글쓰기 실패!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 글쓰기 성공 시(결과값이 true 일 경우)
			// ActionForward 객체를 통해 "BoardList.bo" 서블릿 주소 요청
			// => 새로운 요청이므로 서블릿 주소 변경을 위해 Redirect 방식으로 포워딩 설정
			forward = new ActionForward();
			forward.setPath("BoardList.bo");
			forward.setRedirect(true);
		}
		
		// 포워딩 정보가 저장된 ActionForward 객체 리턴 => BoardFrontController 로 전달
		return forward;
	}

}














