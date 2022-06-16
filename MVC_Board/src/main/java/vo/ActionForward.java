package vo;

/*
 * FrontController(서블릿 클래스)에서 클라이언트로부터의 요청을 받아
 * Action 클래스 등에서 작업 처리 등을 수행한 후
 * View 페이지 또는 다른 서블릿 주소를 요청할 때(= 포워딩 할 때)
 * 포워딩 할 URL(주소) 와 포워딩 방식(Redirect vs Dispatcher)을 관리하기 위한 클래스 정의
 */
public class ActionForward {
	private String path; // 포워딩 주소 저장
	private boolean isRedirect; // 포워딩 방식 저장
	// true : Redirect 방식, false : Dispatcher 방식
	
	// Getter/Setter 정의
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isRedirect() {
		return isRedirect;
	}
	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}
	
}
