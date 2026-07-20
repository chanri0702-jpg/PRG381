package za.bc.cleaninginventory.model.entity;

public class Supplier {

    // supplier_businesses table
    private int businessId;
    private String businessName;
    private String description;

    // supplier_offices table
    private int officeId;
    private String address;
    private String area;
    private String city;
    private String province;
    private String postalCode;

    // supplier_employees table
    private int supplierEmployeeId;
    private String contactName;
    private String contactSurname;
    private String contactEmail;
    private String contactPhone;

    public Supplier() {
    }

    public Supplier(
            int businessId,
            String businessName,
            String description,
            int officeId,
            String address,
            String area,
            String city,
            String province,
            String postalCode,
            int supplierEmployeeId,
            String contactName,
            String contactSurname,
            String contactEmail,
            String contactPhone
    ) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.description = description;
        this.officeId = officeId;
        this.address = address;
        this.area = area;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.supplierEmployeeId = supplierEmployeeId;
        this.contactName = contactName;
        this.contactSurname = contactSurname;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getSupplierEmployeeId() {
        return supplierEmployeeId;
    }

    public void setSupplierEmployeeId(int supplierEmployeeId) {
        this.supplierEmployeeId = supplierEmployeeId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactSurname() {
        return contactSurname;
    }

    public void setContactSurname(String contactSurname) {
        this.contactSurname = contactSurname;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}