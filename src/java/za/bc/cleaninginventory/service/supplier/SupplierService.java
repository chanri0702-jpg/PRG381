package za.bc.cleaninginventory.service.supplier;

import java.sql.SQLException;
import java.util.List;
import za.bc.cleaninginventory.model.dao.supplier.SupplierDAO;
import za.bc.cleaninginventory.model.entity.Supplier;

public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        supplierDAO = new SupplierDAO();
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        return supplierDAO.getAllSuppliers();
    }

    public Supplier getSupplierById(int businessId) throws SQLException {

        if (businessId <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }

        return supplierDAO.getSupplierById(businessId);
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {

        validateSupplier(supplier);

        return supplierDAO.addSupplier(supplier);
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {

        if (supplier.getBusinessId() <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }

        validateSupplier(supplier);

        return supplierDAO.updateSupplier(supplier);
    }

    public boolean deleteSupplier(int businessId) throws SQLException {

        if (businessId <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }

        return supplierDAO.deleteSupplier(businessId);
    }

    private void validateSupplier(Supplier supplier) {

        if (supplier == null) {
            throw new IllegalArgumentException("Supplier details are required.");
        }

        if (isEmpty(supplier.getBusinessName())) {
            throw new IllegalArgumentException("Business name is required.");
        }

        if (isEmpty(supplier.getAddress())) {
            throw new IllegalArgumentException("Address is required.");
        }

        if (isEmpty(supplier.getCity())) {
            throw new IllegalArgumentException("City is required.");
        }

        if (isEmpty(supplier.getProvince())) {
            throw new IllegalArgumentException("Province is required.");
        }

        if (isEmpty(supplier.getPostalCode())) {
            throw new IllegalArgumentException("Postal code is required.");
        }

        if (isEmpty(supplier.getContactName())) {
            throw new IllegalArgumentException("Contact name is required.");
        }

        if (isEmpty(supplier.getContactSurname())) {
            throw new IllegalArgumentException("Contact surname is required.");
        }

        if (isEmpty(supplier.getContactEmail())) {
            throw new IllegalArgumentException("Contact email is required.");
        }

        if (!supplier.getContactEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }

        if (isEmpty(supplier.getContactPhone())) {
            throw new IllegalArgumentException("Contact phone is required.");
        }

        if (!supplier.getContactPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain 10 digits."
            );
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}