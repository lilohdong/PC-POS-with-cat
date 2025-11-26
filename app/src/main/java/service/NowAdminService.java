package service;
// service/NowAdminService.java

import util.PasswdUtil;
import java.util.ArrayList;
import java.util.List;

public class NowAdminService {
    private static NowAdminService instance;
    private boolean isAdminMode = false;
    // 1. 리스너 목록 필드 추가
    private final List<NowAdminListener> listeners = new ArrayList<>();
    // public NowAdminService() {} -> 이 생성자 대신 private NowAdminService() {}를 사용하는 것이 싱글톤 패턴에 더 적합합니다.

    private NowAdminService() {} // private 생성자로 수정

    public static NowAdminService getInstance(){
        if(instance == null){
            instance = new NowAdminService();
        }
        return instance;
    }

    public boolean authenticate(String password){
        if(PasswdUtil.checkManagePwd(password)){
            isAdminMode = true;
            notifyListeners();
            return true;
        }
        isAdminMode = false;
        return false;
    }

    public boolean isAdminMode(){
        return isAdminMode;
    }

    public void addListener(NowAdminListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void notifyListeners() {
        for (NowAdminListener listener : listeners) {
            listener.onAdminModeChanged(isAdminMode);
        }
    }

    public void disableAdminMode() {
        if (isAdminMode) {
            isAdminMode = false;
            notifyListeners();
        }
    }
}