package util;

public class PasswdUtil {
    private static final String MANAGER_PWD = "manager";
    public static boolean checkManagePwd(String password){
        return password.equals(MANAGER_PWD);
    }
}
