package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

// 데이터베이스 관련 보조 작업(설정 및 관리 작업)을 수행하는 클래스
// => DB 자원은 Connection Pool(DBCP) 로부터 Connection 객체를 가져와서 사용
// => 모든 메서드는 인스턴스 생성없이 클래스명만으로 접근하기 위해 static 메서드로 정의
public class JdbcUtil {
	// 1. DB 연결 작업을 수행한 후 Connection 객체를 리턴하는 getConnection() 메서드 정의
	// => 파라미터 : 없음    리턴타입 : java.sql.Connection
	public static Connection getConnection() {
		Connection con = null;
		
		try {
			// context.xml 에 설정된 DBCP(커넥션풀)로부터 Connection 객체를 가져오기
			// 1. 톰캣으로부터 context.xml 파일의 Context 태그 부분(객체) 가져오기
			// => InitialContext 객체 생성하여 Context 타입으로 업캐스팅하여 저장
			Context initCtx = new InitialContext();
			
			// 2. 생성된 Context 객체(initCtx)로부터 context.xml 의 Resource 태그 부분(객체) 가져오기
			// => Context 객체의 lookup() 메서드를 호출하여 찾아올 리소스 지정
			// => 리턴되는 Object 타입 객체를 Context 타입으로 다운캐스팅하여 저장
			// => 파라미터로 "java:comp/env" 문자열 전달(Resource 태그 가리킴)
//					Context envCtx = (Context)initCtx.lookup("java:comp/env");
			
			// 3. Resource 태그 내의 name 속성(리소스 이름 "jdbc/MySQL") 을 가져오기(*)
			// => 생성된 Context 객체(envCtx)의 lookup() 메서드를 호출하여 찾아올 리소스 이름 지정
			// => 리턴되는 Object 타입 객체를 javax.sql.DataSource 타입으로 다운캐스팅하여 저장
			// => 주의! context.xml 파일에 지정된 이름에 따라 문자열 바뀔 수 있다!
//					DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL");
			
			// --- 참고! 2번과 3번 작업을 하나로 결합하는 경우 ---
			// 2번과 3번에서 지정한 문자열을 결합(2번문자열/3번문자열)
			DataSource ds = (DataSource)initCtx.lookup("java:comp/env/jdbc/MySQL");
			// ----------------------------------------------------
			
			// 4. DataSource 객체(= 커넥션풀)로부터 미리 생성되어 있는 Connection 객체 가져오기
			// => 리턴타입 : java.sql.Connection
			con = ds.getConnection();
			// => 만약, context.xml 파일 내에서 계정명(username)과 패스워드(password) 미등록 시
			//    getConnection() 메서드 파라미터로 계정명, 패스워드 전달도 가능함
			//    ex) Connection con = ds.getConnection("root", "1234");
			// => 또한, Properties 객체 활용하여 아이디와 패스워드를 외부 파일로부터 가져올 수도 있음
		
			// ----------------------- 옵션 -----------------------------------
			// 트랜잭션 처리를 위해 데이터베이스(MySQL)의 Auto Commit 기능 해제
			// => Connection 객체의 setAutoCommit() 메서드를 호출하여 false 전달
			con.setAutoCommit(false); // Auto Commit 기능 해제(기능 실행 시 true 전달)
			// => 주의! 이후로 DML 작업(INSERT, UPDATE, DELETE 등) 수행 후
			//    반드시 commit 작업을 수동으로 실행해야함!
			// => 또한, 이전 상태로 되돌리려면 rollback 작업을 수동으로 실행해야함!
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Connection 객체 리턴
		return con;
		
	} // getConnection() 메서드 끝
	
	
	// 2. DB 작업 완료 후 자원을 반환하기 위한 close() 메서드 정의
	// => Connection, PreparedStatement, ResultSet 객체
	// => 반환할 객체만 다르고(파라미터 이름만 각각 다르고) 수행할 작업이 동일하기 때문에
	//    메서드 이름을 close() 메서드로 통일하여 정의 => 메서드 오버로딩
	public static void close(Connection con) {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(PreparedStatement pstmt) {
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 3. 트랜잭션 처리에 필요한 commit, rollback 작업을 수행하기 위한 메서드 정의
	// => 단, Connection 객체에 대해 Auto Commit 기능 해제 필수
	// => 파라미터 : Connection 객체(con)
	public static void commit(Connection con) {
		try {
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollback(Connection con) {
		try {
			con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}