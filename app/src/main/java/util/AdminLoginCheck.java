package util;

public class AdminLoginCheck {
    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PW = "1234";
    public static int COUNT = 5;
    // 단순 로그인 체크만, 로그인실패 시 카운트 깎아버림
    public static void check(String id, String pw) throws NotEqualAdminException{
         if(!(ADMIN_ID.equals(id) && ADMIN_PW.equals(pw))) {
            COUNT--;
            throw new NotEqualAdminException("아이디 또는 비밀번호가 일치하지 않습니다.\n남은 시도 횟수: " + AdminLoginCheck.COUNT);
        }
    }
}
