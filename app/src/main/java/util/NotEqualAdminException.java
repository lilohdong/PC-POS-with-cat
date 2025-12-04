package util;
// Admin 아닐 떄 발생시키는 메소드 RuntimeException 계열 예외
public class NotEqualAdminException extends RuntimeException {
    public NotEqualAdminException(String message) {
        super(message);
    }
}
