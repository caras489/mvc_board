package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vo.BoardDTO;

import static db.JdbcUtil.*;

// 실제 비즈니스 로직(DB 작업)을 처리할 BoardDAO 클래스 정의
// => 인스턴스를 여러개 생성할 필요가 없으므로 싱글톤 디자인 패턴을 통해 
//    하나의 인스턴스를 생성하여 모두가 공유하도록 할 수 있음
public class BoardDAO {
	// --------------- 싱글톤 디자인 패턴을 활용한 BoardDAO 인스턴스 생성 작업 --------------
	// 1. 외부에서 인스턴스 생성이 불가능하도록 생성자 정의 시 private 접근제한자 적용
	// 2. 자신의 클래스 내부에서 직접 인스턴스 생성하여 변수에 저장
	//    => 외부에서 변수에 접근이 불가능하도록 private 접근제한자 적용
	//    => 클래스 로딩 시점에 인스턴스가 생성되도록 static 멤버변수로 선언
	// 3. 생성된 인스턴스를 외부로 리턴하기 위한 Getter 메서드 정의
	//    => 외부에서 인스턴스 생성없이도 호출 가능하도록 static 메서드로 정의
	//    => 이 때, 2번에서 선언된 변수도 static 변수로 선언되어야 함
	//       (static 메서드 내에서 인스턴스 멤버에 접근 불가능하며, static 멤버만 접근 가능하므로)
	private static BoardDAO instance = new BoardDAO();
	
	private BoardDAO() {}

	public static BoardDAO getInstance() {
		return instance;
	};
	// ---------------------------------------------------------------------------------------
	// 외부(Service 클래스)로부터 Connection 객체를 전달받아 관리하기 위해
	// Connection 타입 멤버 변수와 Setter 메서드 정의
	Connection con;

	public void setConnection(Connection con) {
		this.con = con;
	}
	// ---------------------------------------------------------------------------------------
	// 글쓰기 작업 수행 insertArticle() 메서드 정의
	// => 파라미터 : BoardDTO 객체(article)   리턴타입 : int(insertCount)
	public int insertArticle(BoardDTO article) {
		System.out.println("BoardDAO - insertArticle()");
		
		// INSERT 작업 결과를 리턴받아 저장할 변수 선언
		int insertCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int num = 1; // 새 글 번호를 저장할 변수 
		
		try {
			// 현재 새 글의 번호로 사용될 번호를 조회
			// => 기존 글번호(board_num) 중에서 가장 큰 값 조회한 후 + 1 값을 새 글 번호로 설정
			String sql = "SELECT MAX(board_num) FROM board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// 조회된 레코드 중 Auto_increment 컬럼 값을 num 에 저장
				num = rs.getInt(1) + 1;
			}
			
			close(pstmt);
			
			// 전달받은 데이터(BoardDTO 객체)를 사용하여 board 테이블 INSERT 작업 수행
			sql = "INSERT INTO board VALUES (?,?,?,?,?,?,?,?,?,?,?,now())";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num); // 새 글 번호
			pstmt.setString(2, article.getBoard_name());
			pstmt.setString(3, article.getBoard_pass());
			pstmt.setString(4, article.getBoard_subject());
			pstmt.setString(5, article.getBoard_content());
			pstmt.setString(6, article.getBoard_file());
			pstmt.setString(7, article.getBoard_real_file());
			// 답글에 사용될 참조글 번호(board_re_ref)는 새 글이므로 새 글 번호와 동일하게 지정
			pstmt.setInt(8, num); // board_re_ref
			pstmt.setInt(9, 0); // board_re_lev
			pstmt.setInt(10, 0); // board_re_seq
			pstmt.setInt(11, 0); // board_readcount
			
			insertCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - insertArticle()");
			e.printStackTrace();
		} finally {
			// DB 자원 반환(주의! Connection 객체 반환 금지!)
			close(pstmt);
			close(rs);
		}
		
		// INSERT 작업 결과 리턴
		return insertCount;
	}

	// 총 게시물 수를 조회하는 selectListCount() 메서드 정의
	// 파라미터 : 없음   리턴타입 : int(listCount)
	public int selectListCount() {
		System.out.println("BoardDAO - selectListCount()");
		
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COUNT(*) FROM board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - selectListCount()");
			e.printStackTrace();
		} finally {
			// DB 자원 반환(주의! Connection 객체 반환 금지!)
			close(pstmt);
			close(rs);
		}
		
		return listCount;
	}

	// 게시물 목록을 조회하는 selectArticleList() 메서드 정의
	public ArrayList<BoardDTO> selectArticleList(int pageNum, int listLimit) {
		ArrayList<BoardDTO> articleList = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// 조회 시작 게시물 번호(행 번호) 계산
		int startRow = (pageNum - 1) * listLimit;
		
		try {
			// 게시물 목록 조회
			// => 답글에 대한 처리
			//    참조글번호(board_re_ref) 기준 내림차순, 
			//    순서번호(board_re_seq) 기준 오름차순 정렬
			// => 조회 시작 게시물 번호(startRow) 부터 목록의 게시물 수(listLimit) 만큼 조회
			String sql = "SELECT * FROM board "
						+ "ORDER BY board_re_ref DESC, board_re_seq ASC "
						+ "LIMIT ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, listLimit);
			
			rs = pstmt.executeQuery();
			
			// 전체 게시물을 저장할 ArrayList<BoardDTO> 타입 객체 생성
			articleList = new ArrayList<BoardDTO>();
			
			// while 문을 사용하여 조회 결과에 대한 반복 작업 수행
			while(rs.next()) {
				// 1개 게시물 정보를 저장할 BoardDTO 객체 생성 후 조회 결과 저장
				BoardDTO article = new BoardDTO();
				article.setBoard_num(rs.getInt("board_num"));
				article.setBoard_name(rs.getString("board_name"));
				article.setBoard_pass(rs.getString("board_pass"));
				article.setBoard_subject(rs.getString("board_subject"));
				article.setBoard_content(rs.getString("board_content"));
				article.setBoard_file(rs.getString("board_file"));
				article.setBoard_real_file(rs.getString("board_real_file"));
				article.setBoard_re_ref(rs.getInt("board_re_ref"));
				article.setBoard_re_lev(rs.getInt("board_re_lev"));
				article.setBoard_re_seq(rs.getInt("board_re_seq"));
				article.setBoard_readcount(rs.getInt("board_readcount"));
				article.setBoard_date(rs.getDate("board_date"));
				
				// 1개 게시물 정보를 다시 전체 게시물 정보 저장 객체(articleList)에 추가
				articleList.add(article);
			}
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - selectArticleList()");
			e.printStackTrace();
		} finally {
			// DB 자원 반환(주의! Connection 객체 반환 금지!)
			close(pstmt);
			close(rs);
		}
		
		return articleList;
	}

	// 게시물 상세 정보를 조회하는 selectArticle() 메서드 정의
	public BoardDTO selectArticle(int board_num) {
		// 글번호(board_num)에 해당하는 게시물 조회하여 BoardDTO 객체(article)에 저장 후 리턴
		BoardDTO article = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM board WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				article = new BoardDTO();
				article.setBoard_num(rs.getInt("board_num"));
				article.setBoard_name(rs.getString("board_name"));
				article.setBoard_pass(rs.getString("board_pass"));
				article.setBoard_subject(rs.getString("board_subject"));
				article.setBoard_content(rs.getString("board_content"));
				article.setBoard_file(rs.getString("board_file"));
				article.setBoard_real_file(rs.getString("board_real_file"));
				article.setBoard_re_ref(rs.getInt("board_re_ref"));
				article.setBoard_re_lev(rs.getInt("board_re_lev"));
				article.setBoard_re_seq(rs.getInt("board_re_seq"));
				article.setBoard_readcount(rs.getInt("board_readcount"));
				article.setBoard_date(rs.getDate("board_date"));
				
//				System.out.println(article);
			}
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - selectArticle()");
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(rs);
		}
		
		return article;
	}

	// 조회수 증가 작업을 수행하는 updateReadcount() 메서드 정의
	public void updateReadcount(int board_num) {
		PreparedStatement pstmt = null;
		
		try {
			String sql = "UPDATE board SET board_readcount=board_readcount+1 WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - updateReadcount()");
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
	}

	// 패스워드가 일치하는 게시물 조회를 통해 본인 여부 판별하는 isArticleWriter() 메서드 정의
	public boolean isArticleWriter(int board_num, String board_pass) {
		boolean isArticleWriter = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// 글번호와 패스워드가 모두 일치하는 게시물 조회
			String sql = "SELECT * FROM board WHERE board_num=? AND board_pass=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			pstmt.setString(2, board_pass);
			
			rs = pstmt.executeQuery();
		
			// 조회결과(rs.next()) 가 있을 경우 isArticleWriter 를 true 로 변경
			if(rs.next()) {
				isArticleWriter = true;
			}
			
	    } catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - isArticleWriter()");
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(rs);
		}
		
		return isArticleWriter;
	}

	// 글 삭제 작업을 수행하는 deleteArticle() 메서드 정의
	public int deleteArticle(int board_num) {
		int deleteCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			String sql = "DELETE FROM board WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			deleteCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - deleteArticle()");
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		
		return deleteCount;
	}

	// 글 수정 작업을 수행하는 updateArticle() 메서드 정의
	public int updateArticle(BoardDTO article) {
		int updateCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			String sql = "UPDATE board "
					+ "SET board_name=?,board_subject=?,board_content=? "
					+ "WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, article.getBoard_name());
			pstmt.setString(2, article.getBoard_subject());
			pstmt.setString(3, article.getBoard_content());
			pstmt.setInt(4, article.getBoard_num());
			
			updateCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - updateArticle()");
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		
		return updateCount;
	}

	// 답글 등록 작업을 수행하는 insertReplyArticle() 메서드 정의
	public int insertReplyArticle(BoardDTO article) {
		int insertCount = 0;
		
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rs = null;
		
		int num = 1; // 새 글 번호를 저장할 변수 
		
		try {
			// 현재 새 글의 번호로 사용될 번호를 조회
			// => 기존 글번호(board_num) 중에서 가장 큰 값 조회한 후 + 1 값을 새 글 번호로 설정
			String sql = "SELECT MAX(board_num) FROM board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// 조회된 레코드 중 Auto_increment 컬럼 값을 num 에 저장
				num = rs.getInt(1) + 1;
			}
			
			/*
			 * 답변글 등록을 위한 계산
			 * 1) 원본글 번호(board_num)
			 * 2) 새 글 번호(num)
			 * 3) 참조글번호(board_re_ref)
			 * 4) 순서번호(board_re_seq)
			 * 5) 들여쓰기레벨(board_re_lev)
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * | board_num |새글번호num| board_subject    |board_re_ref|board_re_seq|board_re_lev|
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * |        76 |        X  | 수정-사진입니다. |         76 |          0 |          0 |
			 * |        74 |        X  | 제목74           |         74 |          0 |          0 |
			 * |        73 |        X  | 제목73           |         73 |          0 |          0 |
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * 
			 * ex) 76번 게시물에 대한 답글 등록 => 등록할 새 글 번호가 100번이라고 가정
			 * => 76번에 대한 답글이므로 원본글(76번)의 참조글번호(board_re_ref)를 
			 *    새 글(100번)의 참조글번호(board_re_ref)로 사용
			 * => 원본글의 참조글번호(board_re_ref)가 동일한 게시물의 순서번호(board_re_seq)보다
			 *    큰 게시물들의 순서번호를 모두 + 1 씩 증가시킨 후
			 *    새 글의 순서번호는 원본글의 순서번호 + 1 값을 지정	
			 * => 새 글의 들여쓰기 레벨(board_re_lev)은 원본글의 들여쓰기 레벨 + 1 값을 지정
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * | 원본글번호|새글번호num| board_subject    |board_re_ref|board_re_seq|board_re_lev|
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * |        76 |        X  | 수정-사진입니다. |         76 |          0 |          0 |
			 *          76        100    Re:수정-사진입.            76            1            1
			 * |        74 |        X  | 제목74           |         74 |          0 |          0 |
			 * |        73 |        X  | 제목73           |         73 |          0 |          0 |
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * 
			 * ex) 76번 게시물에 대한 두번째 답글 등록 => 등록할 새 글 번호가 101번이라고 가정
			 * => 76번에 대한 답글이므로 원본글(76번)의 참조글번호(board_re_ref)를 
			 *    새 글(100번)의 참조글번호(board_re_ref)로 사용
			 * => 원본글과 참조글번호(ref)가 동일한 게시물이 존재(100번)하므로
			 *    100번 게시물의 순서번호(seq)를 1 증가시킨 후 
			 *    새 글의 순서번호(seq)는 원본글의 순서번호 + 1 로 설정
			 * => 새 글의 들여쓰기 레벨(lev)은 원본글의 들여쓰기레벨 + 1 로 설정
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * | 원본글번호|새글번호num| board_subject    |board_re_ref|board_re_seq|board_re_lev|
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * |        76 |        X  | 수정-사진입니다. |         76 |          0 |          0 |
			 *          76        101    Re:수정-사진입22           76            1            1
			 *          76        100    Re:수정-사진입.            76       1 -> 2            1
			 * |        74 |        X  | 제목74           |         74 |          0 |          0 |
			 * |        73 |        X  | 제목73           |         73 |          0 |          0 |
			 * +-----------+-----------+------------------+------------+------------+------------+
			 * 
			 * 
			 * ex) 101번 게시물에 대한 두번째 답글 등록 => 등록할 새 글 번호가 110번이라고 가정
			 * => 101번에 대한 답글이므로 원본글(101번)의 참조글번호(board_re_ref)를 
			 *    새 글(110번)의 참조글번호(board_re_ref)로 사용
			 * => 원본글과 참조글번호(ref)가 동일한 게시물이 존재하므로
			 *    해당 게시물들 중 원본글의 순서번호보다 큰 게시물(109, 100번)들의 
			 *    순서번호(seq)를 모두 1씩 증가시킨 후 
			 *    새 글의 순서번호(seq)는 원본글의 순서번호 + 1 로 설정
			 * => 새 글의 들여쓰기 레벨(lev)은 원본글의 들여쓰기레벨 + 1 로 설정
			 * +-----------+------------------+------------+------------+------------+
			 * |새글번호num| board_subject    |board_re_ref|board_re_seq|board_re_lev|
			 * +-----------+------------------+------------+------------+------------+
			 * |       76  | 수정-사진입니다. |         76 |          0 |          0 |
			 *        101    Re:수정-사진입2            76            1            1
			 *        110      Re:Re:수정-사22          76            2            2
			 *        109      Re:Re:수정-사진          76       2 -> 3            2
			 *        100    Re:수정-사진입.            76       3 -> 4            1
			 * |       74  | 제목74           |         74 |          0 |          0 |
			 *        200      Re:제목74                74            1            1
			 * |       73  | 제목73           |         73 |          0 |          0 |
			 * +-----------+------------------+------------+------------+------------+
			 */
			
			// 기존 답글들에 대한 순서 번호 증가 작업 처리
			// => 원본글의 참조글번호(board_re_ref)와 같고
			//    원본글의 순서번호(board_re_seq)보다 큰 레코드들의
			//    순서번호(board_re_seq)를 1씩 증가시키기
			sql = "UPDATE board SET board_re_seq=board_re_seq+1 "
					+ "WHERE board_re_ref=? AND board_re_seq>?";
			pstmt2 = con.prepareStatement(sql);
			pstmt2.setInt(1, article.getBoard_re_ref()); // 참조글번호
			pstmt2.setInt(2, article.getBoard_re_seq()); // 순서번호
			pstmt2.executeUpdate();
			
			// 답글(새글) 등록 작업 처리
			// 전달받은 데이터(BoardDTO 객체)를 사용하여 board 테이블 INSERT 작업 수행
			sql = "INSERT INTO board VALUES (?,?,?,?,?,?,?,?,?,?,?,now())";
			pstmt2 = con.prepareStatement(sql);
			pstmt2.setInt(1, num); // 새 글 번호
			pstmt2.setString(2, article.getBoard_name());
			pstmt2.setString(3, article.getBoard_pass());
			pstmt2.setString(4, article.getBoard_subject());
			pstmt2.setString(5, article.getBoard_content());
			pstmt2.setString(6, ""); // 답글에 파일 등록을 허용하지 않으므로
			pstmt2.setString(7, ""); // 파일명은 모두 널스트링("")으로 처리
			// -----------------------------------------------------------------------------
			// 주의해야할 부분(답글 관련 처리)
			// 답글에 사용될 참조글 번호(board_re_ref)는 원본글의 참조글번호와 동일하게 지정
			pstmt2.setInt(8, article.getBoard_re_ref());
			// 들여쓰기레벨(board_re_lev)과 순서번호(board_re_seq)는 원본글의 값 + 1 
			pstmt2.setInt(9, article.getBoard_re_lev() + 1);
			pstmt2.setInt(10, article.getBoard_re_seq() + 1);
			// -----------------------------------------------------------------------------			
			pstmt2.setInt(11, 0); // board_readcount
			
			insertCount = pstmt2.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류 발생! - insertReplyArticle()");
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(pstmt2);
		}
		
		return insertCount;
	}
	
}