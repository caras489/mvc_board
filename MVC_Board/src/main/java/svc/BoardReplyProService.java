package svc;

import static db.JdbcUtil.close;
import static db.JdbcUtil.commit;
import static db.JdbcUtil.getConnection;
import static db.JdbcUtil.rollback;

import java.sql.Connection;

import dao.BoardDAO;
import vo.BoardDTO;

public class BoardReplyProService {
	// 답글 등록 작업을 요청하는 replyArticle() 메서드 정의
	public boolean replyArticle(BoardDTO article) {
		boolean isReplySuccess = false;
		
		Connection con = getConnection();
		
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		boardDAO.setConnection(con);
		
		// BoardDAO 의 insertReplyArticle() 메서드 호출하여 답글 등록 작업 수행
		// => 파라미터 : BoardDTO 객체    리턴타입 : int(insertCount)
		int insertCount = boardDAO.insertReplyArticle(article);
		
		// insertCount 가 0보다 크면 commit, 아니면 rollback 작업 수행
		if(insertCount > 0) {
			commit(con);
			// isReplySuccess 를 true 로 변경
			isReplySuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isReplySuccess;
	}
}