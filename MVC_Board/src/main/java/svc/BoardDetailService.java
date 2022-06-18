package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;
import java.util.ArrayList;

import dao.BoardDAO;
import vo.BoardDTO;

public class BoardDetailService {
	
	// 게시물 1개 정보를 조회 요청하는 getArticle() 메서드 정의
	public BoardDTO getArticle(int board_num) {
		System.out.println("BoardDetailService - getArticle()");
		
		// 1. 리턴할 데이터를 저장할 변수 선언
		BoardDTO article = null;
		
		// 2. JdbcUtil 클래스로부터 Connection Pool 에 저장된 Connection 객체 가져오기 - 공통
		Connection con = getConnection(); // static import 적용되어 있을 경우
		
		// 3. BoardDAO 클래스로부터 BoardDAO 인스턴스 가져오기 - 공통
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		// 4. BoardDAO 객체에 Connection 객체 전달하기 - 공통
		boardDAO.setConnection(con);
		
		// 5. BoardDAO 객체의 selectArticle() 메서드를 호출하여 게시물 상세 정보 조회
		// => 파라미터 : board_num    리턴타입 : BoardDTO(article)
		article = boardDAO.selectArticle(board_num);
		
		// 6. JdbcUtil 클래스로부터 가져온 Connection 객체를 반환 - 공통
		close(con);
		
		// 7. 조회 결과 리턴
		return article;
	}

	public void increaseReadcount(int board_num) {
		// 2. JdbcUtil 클래스로부터 Connection Pool 에 저장된 Connection 객체 가져오기 - 공통
		Connection con = getConnection(); // static import 적용되어 있을 경우
		
		// 3. BoardDAO 클래스로부터 BoardDAO 인스턴스 가져오기 - 공통
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		// 4. BoardDAO 객체에 Connection 객체 전달하기 - 공통
		boardDAO.setConnection(con);
		
		// 5. BoardDAO 객체의 updateReadcount() 메서드를 호출하여 게시물 조회수 증가
		// => 파라미터 : board_num
		boardDAO.updateReadcount(board_num);
		
		// 6. commit 작업 수행
		commit(con);
		
		// 7. JdbcUtil 클래스로부터 가져온 Connection 객체를 반환 - 공통
		close(con);
	}

}