package service;

import dao.MemberDAO;
import dto.MemberDTO;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MemberService {
    private MemberService(){}
    private static MemberService instance;
    public static MemberService getInstance(){
        if(instance == null){
            instance = new MemberService();
        }
        return instance;
    }

    // 전체 검색
    public void loadTable(DefaultTableModel tm) {
        tm.setRowCount(0); // 데이터 초기화
        List<MemberDTO> list = MemberDAO.getInstance().getAllMembers();
        for(MemberDTO dto : list) {
            addToModel(tm, dto);
        }
    }

    //검색 조건
    public void loadTable(DefaultTableModel tm, String keyword, String type) {
        tm.setRowCount(0); // 기존 데이터 초기화

        if(type.equals("전체검색")) {
            List<MemberDTO> list = MemberDAO.getInstance().getAllMembers();
            for(MemberDTO dto : list) {
                if(dto.getName().contains(keyword) || dto.getmId().contains(keyword)) {
                    addToModel(tm, dto);
                }
            }
            return;
        }
        if(type.equals("이름")) {
            List<MemberDTO> list = MemberDAO.getInstance().getMembersByName(keyword);
            for(MemberDTO dto : list) {
                addToModel(tm, dto);
            }
        } else { // 아이디 검색
            MemberDTO dto = MemberDAO.getInstance().getMemberById(keyword);
            if(dto.getmId() != null) { // 데이터가 있을 때만
                addToModel(tm, dto);
            }
        }
    }

    // 잔여시간 형식
    private String formatRemainTime(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    // 테이블에 행 추가
    private void addToModel(DefaultTableModel tm, MemberDTO dto) {
        // 나이 계산 (년도)
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        int birthYear = dto.getBirth().toLocalDate().getYear();
        int age = currentYear - birthYear + 1;

        // 연령대 계산
        String ageGroup = getAgeGroup(age);

        String remainTimeStr = formatRemainTime(dto.getRemainTime());

        Object[] dd = {
                ageGroup,
                dto.getName(),
                dto.getmId(),
                dto.getBirth(),
                dto.getSex(),
                age,
                remainTimeStr,
                dto.getPhone()
        };
        tm.addRow(dd);
    }

    private String getAgeGroup(int age) {
        if(age <= 13) return "초등학생";
        if(age <= 16) return "중학생";
        if(age <= 19) return "고등학생";
        return "성인";
    }


    // 회원 가입
    public boolean joinMember(MemberDTO dto) {
        return MemberDAO.getInstance().insertMember(dto);
    }

    // 회원 수정
    public boolean updateMember(MemberDTO dto) {
        return MemberDAO.getInstance().updateMember(dto);
    }

    // 회원 삭제
    public boolean deleteMember(String mId) {
        return MemberDAO.getInstance().deleteMember(mId);
    }

    // 아이디 중복 확인
    public boolean isIdDuplicate(String mId) {
        return MemberDAO.isMemberIdValid(mId);
    }
}
