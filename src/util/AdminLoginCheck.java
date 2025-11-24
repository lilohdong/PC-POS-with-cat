package util;

public class AdminLoginCheck {
    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PW = "1234";
    public static int COUNT = 5;

    public static boolean check(String id, String pw) {
        if (id.equals(ADMIN_ID) && pw.equals(ADMIN_PW)) {
            return true;
        } else {
            COUNT--;
            return false;
        }
    }
}
