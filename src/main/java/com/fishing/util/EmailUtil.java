
package com.fishing.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    public static void sendOtp(String recipientEmail, String otp)
            throws Exception {

        String senderEmail = System.getenv("GMAIL_EMAIL");
        String appPassword = System.getenv("GMAIL_APP_PASSWORD");

        if (senderEmail == null || senderEmail.isBlank()
                || appPassword == null || appPassword.isBlank()) {
            throw new IllegalStateException(
                    "Chưa cấu hình GMAIL_EMAIL hoặc GMAIL_APP_PASSWORD.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipientEmail)
        );
        message.setSubject("Ma OTP dat lai mat khau - Fishing Tackle Shop");
        message.setText(
                "Xin chao,\n\n"
                + "Ma OTP dat lai mat khau cua ban la: " + otp + "\n\n"
                + "Ma co hieu luc trong 5 phut. Khong chia se ma nay voi bat ky ai.\n\n"
                + "Fishing Tackle Shop"
        );

        Transport.send(message);
    }
}
