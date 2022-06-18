package svc;

import java.sql.Connection;

import dao.BoardDAO;
import vo.BoardDTO;
// db.JdbcUtil 클래스의 모든 static 메서드를 import 할 경우 - static import 필요
import static db.JdbcUtil.*;

// Action 클래스로부터 요청(지시)을 받아 DAO 클래스와 상호작용을 통해
// 실제 DB 작업 수행을 지시하는 클래스
// => 주로, Connection 객체 관리, DAO 객체 관리, DAO 객체의 메서드 호출
//    작업 수행 후 결과에 대한 판별을 통해 트랜잭션 관리
public class BoardWriteProService {
	// 글쓰기 작업 요청을 위한 registArticle() 메서드 정의
	// => 파라미터 : BoardDTO 객체(board)   리턴타입 : boolean(isWriteSuccess)
	public boolean registArticle(BoardDTO board) {
		System.out.println("BoardWriteProService - registArticle()");
		
		// 1. 글쓰기 작업 요청 처리 결과를 판별하여 저장할 boolean 타입 변수 선언
		boolean isWriteSuccess = false;
		
		// 2. JdbcUtil 클래스로부터 Connection Pool 에 저장된 Connection 객체 가져오기 - 공통
//		Connection con = JdbcUtil.getConnection();
		Connection con = getConnection(); // static import 적용되어 있을 경우
		
		// 3. BoardDAO 클래스로부터 BoardDAO 인스턴스 가져오기 - 공통
		//    (getInstance() 메서드를 호출하여 싱글톤 패턴으로 생성된 인스턴스 리턴받기)
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		// 4. BoardDAO 객체에 Connection 객체 전달하기 - 공통
		//    (setConnection() 메서드를 호출하여 Connection 객체를 파라미터로 지정)
		boardDAO.setConnection(con);
		
		// 5. BoardDAO 객체의 XXX 메서드를 호출하여 요청받은 XXX 작업 수행 및 결과 리턴받기
		// BoardDAO 객체의 insertArticle() 메서드를 호출하여 글쓰기 작업 수행 후 결과값 리턴받기
		// => 파라미터 : BoardDTO 객체(board)   리턴타입 : int(insertCount)
		int insertCount = boardDAO.insertArticle(board);
		
		// 6. 리턴받은 작업 수행 결과를 통해 판별 후 처리 작업 수행(트랜잭션 처리)
		if(insertCount > 0) { // 작업 성공 시
			// 트랜잭션 적용을 위해 JdbcUtil 클래스의 commit() 메서드를 호출하여 commit 작업 수행
			commit(con);
			
			// 작업 처리 결과를 성공으로 표시하기 위해 isWriteSuccess 를 true 로 변경
			isWriteSuccess = true;
		} else { // 작업 실패 시
			// 트랜잭션 취소를 위해 JdbcUtil 클래스의 rollback() 메서드를 호출하여 rollback 작업 수행
			rollback(con);
		}
		
		// 7. JdbcUtil 클래스로부터 가져온 Connection 객체를 반환 - 공통
		//    => 주의! DAO 클래스에서 Connection 객체 반환 금지!
		close(con);
		
		// 8. 작업 처리 결과 리턴
		return isWriteSuccess;
	}
	
}