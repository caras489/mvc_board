package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import vo.MemberDTO;

import static db.JdbcUtil.*;

public class MemberDAO {
	// ------------ 싱글톤 디자인 패턴 구현 --------------
	private static MemberDAO instance = new MemberDAO();
	
	private MemberDAO() {}

	public static MemberDAO getInstance() {
		return instance;
	}
	// ---------------------------------------------------
	// 외부에서 Connection 객체를 전달받아 멤버변수에 저장
	private Connection con;

	public void setConnection(Connection con) {
		this.con = con;
	}
	// ---------------------------------------------------
	// 아이디 중복 판별을 위한 조회 작업을 수행하는 isDuplicateId() 메서드 정의
	public boolean isDuplicateId(String id) {
		boolean isDuplicate = false; // true : 중복, false : 중복 아님
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM member WHERE id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 입력받은 아이디가 존재할 경우(= 중복)
				isDuplicate = true;
			}
		} catch (SQLException e) {
			System.out.println("isDuplicateId() 메서드 오류!");
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return isDuplicate;
	}

	// 회원 등록 작업을 수행하는 insertMember() 메서드 정의
	public int insertMember(MemberDTO member) {
		int insertCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			String sql = "INSERT INTO member VALUES (null,?,?,?,?,?,now())";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, member.getName());
			pstmt.setString(2, member.getId());
			pstmt.setString(3, member.getPasswd());
			pstmt.setString(4, member.getEmail());
			pstmt.setString(5, member.getGender());
			insertCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("insertMember() 메서드 오류!");
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		
		return insertCount;
	}

	// 로그인 판별 작업 수행을 위한 isMember() 메서드 정의
	public boolean isMember(MemberDTO member) {
		boolean isMember = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try {
			// Statement 객체는 Connection 객체와 연결 시 SQL 구문을 미리 전달하지 않음
//			Statement stmt = con.createStatement();
//			// SQL 구문 작성 시 파라미터(?) 사용이 불가능하므로 데이터는 문자열 결합으로 전달함
//			// => 데이터 부분에 SQL 구문을 전달하면 구문으로 취급되어 SQL 삽입 공격이 가능해진다.
//			String sql = "SELECT * FROM member "
//					+ "WHERE id='" + member.getId() + "' AND passwd='" + member.getPasswd() + "'";
//			rs = stmt.executeQuery(sql);
			
			// PreparedStatement 객체는 Connection 객체와 연결 시 SQL 구문을 미리 전달하여
			// 컴파일만 해 놓고 차후에 데이터를 전달하여 결합(Bind) 후 실행하는 방식을 사용
			// => 따라서, 데이터 부분에 SQL 구문을 전달하더라도 구문으로 취급되지 않고 데이터로만 취급됨
			String sql = "SELECT * FROM member WHERE id=? AND passwd=?";
			pstmt = con.prepareStatement(sql);
			// => 이 시점에서 이미 실행될 SQL 구문의 형태는 확정됨(컴파일 완료)
			// => setXXX() 메서드는 단순히 데이터 결합 역할만 수행함(SQL 구문으로 동작하지 못함)
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPasswd());
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 로그인 성공 시
				isMember = true;
			}
		} catch (SQLException e) {
			System.out.println("isMember() 메서드 오류!");
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return isMember;
	}
}