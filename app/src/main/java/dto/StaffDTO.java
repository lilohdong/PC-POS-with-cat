package dto;

import java.sql.Date;
import java.sql.Timestamp;

public class StaffDTO {
    private int staffId;
    private String staffName;
    private Date birth;
    private String gender;
    private int salary;
    private Timestamp hireDate;
    private boolean isActive;
    private String phone;

    public StaffDTO() {}

    public StaffDTO(int staffId, String staffName, Date birth, String gender, int salary,
                    Timestamp hireDate, boolean isActive, String phone) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.birth = birth;
        this.gender = gender;
        this.salary = salary;
        this.hireDate = hireDate;
        this.isActive = isActive;
        this.phone = phone;
    }

    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Timestamp getHireDate() {
        return hireDate;
    }

    public void setHireDate(Timestamp hireDate) {
        this.hireDate = hireDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}