package za.bc.cleaninginventory.model.entity;

public class Cleaner {

    private int employeeId;
    private String name;
    private String surname;
    private int campusId;
    private String campusName;
    private String role;
    private String email;
    private String phone;
    private String password;

    public Cleaner() {
    }

    public Cleaner(
            int employeeId,
            String name,
            String surname,
            int campusId,
            String campusName,
            String role,
            String email,
            String phone
    ) {
        this.employeeId = employeeId;
        this.name = name;
        this.surname = surname;
        this.campusId = campusId;
        this.campusName = campusName;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getCampusId() {
        return campusId;
    }

    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}