package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;

import dao.BoardDAO;

public class BoardDeleteProService {
	// 게시물 삭제를 위한 패스워드 판별 작업을 요청하는 isArticleWriter() 메서드 정의
	public boolean isArticleWriter(int board_num, String board_pass) {
		boolean isArticleWriter = false;
		
		Connection con = getConnection();
		
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		boardDAO.setConnection(con);
		
		// BoardDeleteProService 에서 BoardDAO 의 isArticleWriter() 메서드를 호출하여 패스워드 판별
		// => 파라미터 : 글번호(board_num), 패스워드(board_pass)
		//    리턴타입 : boolean(isArticleWriter)
		isArticleWriter = boardDAO.isArticleWriter(board_num, board_pass);
		
		close(con);
		
		return isArticleWriter;
	}

	// 글 삭제 작업 요청을 수행하는 removeArticle() 메서드 정의
	public boolean removeArticle(int board_num) {
		boolean isDeleteSuccess = false;
		
		Connection con = getConnection();
		
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		boardDAO.setConnection(con);
		
		// BoardDAO 의 deleteArticle() 메서드 호출하여 글 삭제 작업 수행
		// => 파라미터 : 글번호(board_num)    리턴타입 : int(deleteCount)
		int deleteCount = boardDAO.deleteArticle(board_num);
		
		// deleteCount 가 0보다 크면 commit, 아니면 rollback 작업 수행
		if(deleteCount > 0) {
			commit(con);
			// isDeleteSuccess 를 true 로 변경
			isDeleteSuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isDeleteSuccess;
	}
}	
