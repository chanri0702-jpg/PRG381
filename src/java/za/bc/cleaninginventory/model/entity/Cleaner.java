package za.bc.cleaninginventory.model.entity;

public class Cleaner {

    private int cleanerId;
    private String name;
    private String surname;
    private String phone;
    private String email;
  private int campId;

    private int campusId;
    private String campusName;


    public Cleaner() {
    }

    public Cleaner(
            int cleanerId,
            String name,
            String surname,
            String phone,
            String email,
            int campusId,
            String campusName
    ) {
        this.cleanerId = cleanerId;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.campusId = campusId;
        this.campusName = campusName;
    }


    public int getCleanerId() {
        return cleanerId;
    }

    public void setCleanerId(int cleanerId) {
        this.cleanerId = cleanerId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFullName() {
        return (name == null ? "" : name)
                + " "
                + (surname == null ? "" : surname);
    }
}
