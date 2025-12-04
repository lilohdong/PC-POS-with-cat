package service;

import util.PasswdUtil;

import java.util.ArrayList;
import java.util.List;

public class NowAdminService {
    private static NowAdminService instance;
    private boolean isAdminMode = false;

    private final List<NowAdminListener> listeners = new ArrayList<>();
    // 싱글톤으로 잠궈버리기
    private NowAdminService() {}

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
    // 모든 Admin 인증 시스템에 권한이 변했다고 뿌려줌
    // POS_PLACE 프로젝트 내에서는 Admin 권한 사용하는 것이 SideBar밖에 없기 때문에,
    // SideBar에 알림. 향후 코드 확장성을 위해 이런 방식으로 설계
    // Observer 패턴
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