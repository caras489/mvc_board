package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;

import dao.MemberDAO;
import vo.MemberDTO;

public class MemberJoinProService {
	// 회원 등록 작업 요청을 위한 registMember() 메서드 정의
	public boolean registMember(MemberDTO member) {
		boolean isRegistSuccess = false;
		
		Connection con = getConnection();
		
		MemberDAO memberDAO = MemberDAO.getInstance();
		
		memberDAO.setConnection(con);
		
		// MemberDAO 의 insertMember() 메서드를 호출하여 회원 등록 작업 수행
		// => 파라미터 : MemberDTO 객체(member)   리턴타입 : int(insertCount) 
		int insertCount = memberDAO.insertMember(member);
		
		// 작업 수행 결과에 따른 commit, rollback 작업 수행
		if(insertCount > 0) {
			commit(con);
			isRegistSuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isRegistSuccess;
	}
}