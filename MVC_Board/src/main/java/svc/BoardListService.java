package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;
import java.util.ArrayList;

import dao.BoardDAO;
import vo.BoardDTO;

public class BoardListService {

	public int getListCount() {
		System.out.println("BoardListService - getListCount()");
		
		// 1. 리턴할 데이터를 저장할 변수 선언
		int listCount = 0;
		
		// 2. JdbcUtil 클래스로부터 Connection Pool 에 저장된 Connection 객체 가져오기 - 공통
		Connection con = getConnection(); // static import 적용되어 있을 경우
		
		// 3. BoardDAO 클래스로부터 BoardDAO 인스턴스 가져오기 - 공통
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		// 4. BoardDAO 객체에 Connection 객체 전달하기 - 공통
		boardDAO.setConnection(con);
		
		// 5. BoardDAO 객체의 selectListCount() 메서드를 호출하여 총 게시물 수 조회
		listCount = boardDAO.selectListCount();
		
		// 6. JdbcUtil 클래스로부터 가져온 Connection 객체를 반환 - 공통
		close(con);
		
		// 7. 조회 결과 리턴
		return listCount;
	}

	public ArrayList<BoardDTO> getArticleList(int pageNum, int listLimit) {
		System.out.println("BoardListService - getArticleList()");
		
		// 1. 리턴할 데이터를 저장할 변수 선언
		ArrayList<BoardDTO> articleList = null;
		
		// 2. JdbcUtil 클래스로부터 Connection Pool 에 저장된 Connection 객체 가져오기 - 공통
		Connection con = getConnection(); // static import 적용되어 있을 경우
		
		// 3. BoardDAO 클래스로부터 BoardDAO 인스턴스 가져오기 - 공통
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		// 4. BoardDAO 객체에 Connection 객체 전달하기 - 공통
		boardDAO.setConnection(con);
		
		// 5. BoardDAO 객체의 selectArticleList() 메서드를 호출하여 게시물 목록 조회
		// => 파라미터 : pageNum, listLimit    리턴타입 : ArrayList<BoardDTO>
		articleList = boardDAO.selectArticleList(pageNum, listLimit);
		
		// 6. JdbcUtil 클래스로부터 가져온 Connection 객체를 반환 - 공통
		close(con);
		
		// 7. 조회 결과 리턴
		return articleList;
	}

}