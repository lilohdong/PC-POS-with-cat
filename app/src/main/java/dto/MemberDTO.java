package dto;

import java.sql.Date;
import java.sql.Timestamp;

public class MemberDTO {
    private String mId;
    private String passwd;
    private String name;
    private Date birth;
    private String sex;
    private int remainTime;
    private String phone;
    private Timestamp joinDate;

    public MemberDTO() {}

    public MemberDTO(String mId, String passwd, String name, Date birth, String sex, int remainTime, String phone) {
        this.mId = mId;
        this.passwd = passwd;
        this.name = name;
        this.birth = birth;
        this.sex = sex;
        this.remainTime = remainTime;
        this.phone = phone;
    }
    public String getmId() { return mId; }
    public void setmId(String mId) { this.mId = mId; }

    public String getPasswd() { return passwd; }
    public void setPasswd(String passwd) { this.passwd = passwd; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getBirth() { return birth; }
    public void setBirth(Date birth) { this.birth = birth; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public int getRemainTime() { return remainTime; }
    public void setRemainTime(int remainTime) { this.remainTime = remainTime; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Timestamp getJoinDate() { return joinDate; }
    public void setJoinDate(Timestamp joinDate) { this.joinDate = joinDate; }
}
