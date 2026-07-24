/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.service.issuance;

import za.bc.cleaninginventory.config.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *
 * @author BC-STUDENT
 */
public class EmailService {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(EmailConfig.SMTP_PORT));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.USERNAME, EmailConfig.PASSWORD);
            }
        });
    }

    private static void sendUrgentRequestNotification(String toEmail, String requesterName,
            String productName, int quantity, String description) throws MessagingException {

        Session session = createSession();
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(EmailConfig.FROM_ADDRESS));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject("URGENT stock request: " + productName);

        String body = "An urgent stock request has been submitted.\n\n"
                + "Requested by: " + requesterName + "\n"
                + "Product: " + productName + "\n"
                + "Quantity: " + quantity + "\n"
                + "Description: " + description + "\n\n"
                + "Please review and action it in the Cleaning Inventory System.";

        message.setText(body);
        Transport.send(message);
    }

    public static void sendUrgentRequestNotificationAsync(List<String> toEmails, String requesterName,
            String productName, int quantity, String description) {

        for (String toEmail : toEmails) {
            EXECUTOR.submit(() -> {
                try {
                    sendUrgentRequestNotification(toEmail, requesterName, productName, quantity, description);
                    System.err.println("[URGENT EMAIL] Successfully sent to " + toEmail);
                } catch (Throwable t) {
                    System.err.println("[URGENT EMAIL] FAILED to send to " + toEmail + ": " + t);
                    t.printStackTrace();
                }
            });
        }
    }
}