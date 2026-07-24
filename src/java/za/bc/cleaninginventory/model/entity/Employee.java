package za.bc.cleaninginventory.model.entity;

public class Employee {
    private int empId;
    private int campId;
    private String name;
    private String surname;
    private String role;
    private String password;
    private String email;

    public Employee() {
    }

    public Employee(int empId, int campId, String name, String surname, String role, String password, String email) {
        this.empId = empId;
        this.campId = campId;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.password = password;
        this.email = email;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getCampId() {
        return campId;
    }

    public void setCampId(int campId) {
        this.campId = campId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
