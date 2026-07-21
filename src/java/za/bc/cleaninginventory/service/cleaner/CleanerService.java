package za.bc.cleaninginventory.service.cleaner;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import za.bc.cleaninginventory.model.dao.cleaner.CleanerDAO;
import za.bc.cleaninginventory.model.entity.Cleaner;

public class CleanerService {

    private final CleanerDAO cleanerDAO;

    public CleanerService() {
        cleanerDAO = new CleanerDAO();
    }

    public List<Cleaner> getAllCleaners() throws SQLException {
        return cleanerDAO.getAllCleaners();
    }

    public Cleaner getCleanerById(int cleanerId)
            throws SQLException {

        validateCleanerId(cleanerId);

        return cleanerDAO.getCleanerById(cleanerId);
    }

    public Map<Integer, String> getAllCampuses()
            throws SQLException {

        return cleanerDAO.getAllCampuses();
    }

    public boolean addCleaner(Cleaner cleaner)
            throws SQLException {

        normaliseCleaner(cleaner);
        validateCleaner(cleaner);
        validateCampus(cleaner.getCampusId());

        if (cleanerDAO.emailExists(
                cleaner.getEmail(),
                0
        )) {
            throw new IllegalArgumentException(
                    "A cleaner with this email address already exists."
            );
        }

        return cleanerDAO.addCleaner(cleaner);
    }

    public boolean updateCleaner(Cleaner cleaner)
            throws SQLException {

        validateCleanerId(cleaner.getCleanerId());

        normaliseCleaner(cleaner);
        validateCleaner(cleaner);
        validateCampus(cleaner.getCampusId());

        if (cleanerDAO.emailExists(
                cleaner.getEmail(),
                cleaner.getCleanerId()
        )) {
            throw new IllegalArgumentException(
                    "Another cleaner already uses this email address."
            );
        }

        return cleanerDAO.updateCleaner(cleaner);
    }

    public boolean deleteCleaner(int cleanerId)
            throws SQLException {

        validateCleanerId(cleanerId);

        return cleanerDAO.deleteCleaner(cleanerId);
    }

    private void validateCleaner(Cleaner cleaner) {

        if (cleaner == null) {
            throw new IllegalArgumentException(
                    "Cleaner details are required."
            );
        }

        if (isEmpty(cleaner.getName())) {
            throw new IllegalArgumentException(
                    "Cleaner name is required."
            );
        }

        if (cleaner.getName().length() > 60) {
            throw new IllegalArgumentException(
                    "Cleaner name cannot exceed 60 characters."
            );
        }

        if (isEmpty(cleaner.getSurname())) {
            throw new IllegalArgumentException(
                    "Cleaner surname is required."
            );
        }

        if (cleaner.getSurname().length() > 60) {
            throw new IllegalArgumentException(
                    "Cleaner surname cannot exceed 60 characters."
            );
        }

        if (isEmpty(cleaner.getPhone())) {
            throw new IllegalArgumentException(
                    "Phone number is required."
            );
        }

        if (!cleaner.getPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits."
            );
        }

        if (isEmpty(cleaner.getEmail())) {
            throw new IllegalArgumentException(
                    "Email address is required."
            );
        }

        if (!cleaner.getEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }

        if (cleaner.getCampusId() <= 0) {
            throw new IllegalArgumentException(
                    "Select a campus."
            );
        }
    }

    private void validateCampus(int campusId)
            throws SQLException {

        if (!cleanerDAO.campusExists(campusId)) {
            throw new IllegalArgumentException(
                    "The selected campus does not exist."
            );
        }
    }

    private void validateCleanerId(int cleanerId) {

        if (cleanerId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid cleaner ID."
            );
        }
    }

    private void normaliseCleaner(Cleaner cleaner) {

        if (cleaner == null) {
            return;
        }

        cleaner.setName(trim(cleaner.getName()));
        cleaner.setSurname(trim(cleaner.getSurname()));

        String phone = trim(cleaner.getPhone());

        if (phone != null) {
            phone = phone.replaceAll("[\\s-]", "");
        }

        cleaner.setPhone(phone);

        String email = trim(cleaner.getEmail());

        if (email != null) {
            email = email.toLowerCase(Locale.ROOT);
        }

        cleaner.setEmail(email);
    }

    private String trim(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }
}
