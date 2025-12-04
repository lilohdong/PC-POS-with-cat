package util;

import dao.AdminStaffDAO;
import dto.AdminStaffDTO;

public class AdminLoginCheck {
    public static int COUNT = 5;
    private AdminStaffDTO dto;

    private AdminLoginCheck(){}
    private static AdminLoginCheck adminLoginCheck;
    public static AdminLoginCheck getInstance(){
        if(adminLoginCheck == null){
            adminLoginCheck = new AdminLoginCheck();
        }
        return adminLoginCheck;
    }
    // 단순 로그인 체크만, 로그인실패 시 카운트 깎아버림
    public void check(String name, String pw) throws NotEqualAdminException{
         dto = AdminStaffDAO.getInstance().getLoginStaff(name);
         // 이름으로 받아오고 비번이 아니면~ or dto가 null 즉 name이 존재하지않으면
         if(!(dto.getPasswd().equals(pw)) || dto == null) {
            COUNT--;
            throw new NotEqualAdminException("아이디 또는 비밀번호가 일치하지 않습니다.\n남은 시도 횟수: " + AdminLoginCheck.COUNT);
        }
    }
}
