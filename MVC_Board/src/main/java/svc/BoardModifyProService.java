package svc;

import static db.JdbcUtil.close;
import static db.JdbcUtil.commit;
import static db.JdbcUtil.getConnection;
import static db.JdbcUtil.rollback;

import java.sql.Connection;

import dao.BoardDAO;
import vo.BoardDTO;

public class BoardModifyProService {
	// 게시물 수정을 위한 패스워드 판별 작업을 요청하는 isArticleWriter() 메서드 정의
	public boolean isArticleWriter(int board_num, String board_pass) {
		boolean isArticleWriter = false;
		
		Connection con = getConnection();
		
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		boardDAO.setConnection(con);
		
		// BoardDAO 의 isArticleWriter() 메서드를 호출하여 패스워드 판별
		// => 파라미터 : 글번호(board_num), 패스워드(board_pass)
		//    리턴타입 : boolean(isArticleWriter)
		isArticleWriter = boardDAO.isArticleWriter(board_num, board_pass);
		
		close(con);
		
		return isArticleWriter;
	}

	// 글 수정 작업을 요청하는 modifyArticle() 메서드 정의
	public boolean modifyArticle(BoardDTO article) {
		boolean isModifySuccess = false;
		
		Connection con = getConnection();
		
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		boardDAO.setConnection(con);
		
		// BoardDAO 의 updateArticle() 메서드 호출하여 글 수정 작업 수행
		// => 파라미터 : BoardDTO 객체    리턴타입 : int(updateCount)
		int updateCount = boardDAO.updateArticle(article);
		
		// updateCount 가 0보다 크면 commit, 아니면 rollback 작업 수행
		if(updateCount > 0) {
			commit(con);
			// isModifySuccess 를 true 로 변경
			isModifySuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isModifySuccess;
	}
}