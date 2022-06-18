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
public class BoardWriteProAction_Temp implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardWriteProAction");
		
		// 포워딩 정보를 저장하는 ActionForward 타입 변수 선언
		ActionForward forward = null;

		// 파일 업로드 관련 정보 처리를 위해 MultiPartRequest 객체 활용
		// 1. 업로드 할 파일이 저장되는 이클립스 프로젝트 상의 경로를 변수에 저장
		String uploadPath = "upload";
		
		// 2. 업로드 가능한 파일의 크기를 정수 형태로 지정(10MB 제한 설정)
		int fileSize = 1024 * 1024 * 10; // byte(1) -> KB(1024Byte) -> MB(1024KB) -> 10MB 단위로 변환
		
		// 3. 현재 프로젝트(서블릿)를 처리하는 객체인 서블릿 컨텍스트 객체 얻어오기
		ServletContext context = request.getServletContext();
		
		// 4. 업로드 할 파일이 저장되는 실제 경로를 얻어오기
		String realPath = context.getRealPath(uploadPath); // 가상의 업로드 폴더명을 파라미터로 전달
		
		MultipartRequest multi = new MultipartRequest(
				request, // 1) 실제 요청 정보가 포함된 request 객체
				realPath, // 2) 실제 업로드 폴더 경로 
				fileSize, // 3) 업로드 파일 크기(10MB 제한)
				"UTF-8", // 4) 한글 파일명에 대한 인코딩 방식 
				new DefaultFileRenamePolicy()); // 5) 중복 파일명에 대한 이름 변경 처리를 담당하는 객체
		
		// 6. MultipartRequest 객체의 getParameter() 메서드를 호출하여 폼 파라미터 데이터 가져오기
		BoardDTO board = new BoardDTO();
		board.setBoard_name(multi.getParameter("board_name"));
		board.setBoard_pass(multi.getParameter("board_pass"));
		board.setBoard_subject(multi.getParameter("board_subject"));
		board.setBoard_content(multi.getParameter("board_content"));
		
		String fileElement = multi.getFileNames().nextElement().toString();
		String board_file = multi.getOriginalFileName(fileElement); 
		String board_real_file = multi.getFilesystemName(fileElement);
		
		board.setBoard_file(board_file);
		board.setBoard_real_file(board_real_file);
		
		BoardWriteProService service = new BoardWriteProService();
		boolean isWriteSuccess = service.registArticle(board);
		
		if(!isWriteSuccess) { // 글쓰기 실패 시(결과값이 false 일 경우)
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('글쓰기 실패!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 글쓰기 성공 시(결과값이 true 일 경우)
			forward = new ActionForward();
			forward.setPath("BoardList.bo");
			forward.setRedirect(true);
		}
		
		return forward;
	}

}