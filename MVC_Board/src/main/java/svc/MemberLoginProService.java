package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;

import dao.MemberDAO;
import vo.MemberDTO;

public class MemberLoginProService {
	// 로그인 판별 작업 요청을 위한 loginMember() 메서드 정의
	public boolean loginMember(MemberDTO member) {
		boolean isMember = false;
		
		Connection con = getConnection();
		
		MemberDAO memberDAO = MemberDAO.getInstance();
		
		memberDAO.setConnection(con);
		
		// MemberDAO 의 isMember() 메서드를 호출하여 회원 등록 작업 수행
		// => 파라미터 : MemberDTO 객체(member)   리턴타입 : boolean(isMember)
		isMember = memberDAO.isMember(member);
		
		close(con);
		
		return isMember;
	}
}











