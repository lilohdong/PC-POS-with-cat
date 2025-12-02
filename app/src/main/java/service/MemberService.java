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
    public void loadTable(DefaultTableModel tm) { //type 기준 , keyword 검색창 문자열
        List<MemberDTO> list = MemberDAO.getInstance().getAllMembers();
        //-----------------------------------------------//
        // 테이블 데이터 가져오기 만들 예정, DB 생성 후 //
        for(MemberDTO dto : list) {
            Object[] dd = {
                    dto.getBirth(), dto.getName(),dto.getmId(), dto.getBirth(), dto.getSex(), dto.getBirth(), dto.getRemainTime(),dto.getPhone()
            };
            tm.addRow(dd);
        }
    }
    public void loadTable(DefaultTableModel tm, String keyword, String type) {
        if(type.equals("이름")) {
            List<MemberDTO> list = MemberDAO.getInstance().getMembersByName(keyword);
            for(MemberDTO dto : list) {
                Object[] dd = {
                        dto.getBirth(), dto.getName(),dto.getmId(), dto.getBirth(), dto.getSex(), dto.getBirth(), dto.getRemainTime(),dto.getPhone()
                };
                tm.addRow(dd);
            }
        } else {
            MemberDTO dto = MemberDAO.getInstance().getMemberById(keyword);
            Object[] dd = {
                    dto.getBirth(), dto.getName(),dto.getmId(), dto.getBirth(), dto.getSex(), dto.getBirth(), dto.getRemainTime(),dto.getPhone()
            };
            tm.addRow(dd);
        }

    }
}
