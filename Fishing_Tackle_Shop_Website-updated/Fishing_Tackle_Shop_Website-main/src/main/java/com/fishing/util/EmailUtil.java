
package com.fishing.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class EmailUtil {

    private EmailUtil() {
    }

    public static void sendOtp(String recipientEmail, String otp)
            throws Exception {

        String senderEmail = System.getenv("GMAIL_EMAIL");
        String appPassword = System.getenv("GMAIL_APP_PASSWORD");

        if (senderEmail == null || senderEmail.isBlank()
                || appPassword == null || appPassword.isBlank()) {
            throw new IllegalStateException(
                    "Thiếu cấu hình GMAIL_EMAIL hoặc GMAIL_APP_PASSWORD.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session mailSession = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                senderEmail, appPassword);
                    }
                });

        MimeMessage message = new MimeMessage(mailSession);

        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(recipientEmail));

        message.setSubject(
                "Ma OTP dat lai mat khau - Fishing Tackle Shop",
                StandardCharsets.UTF_8.name());

        String content =
                "Xin chao,\n\n"
                        + "Ban da yeu cau dat lai mat khau tai Fishing Tackle Shop.\n\n"
                        + "Ma OTP cua ban la: " + otp + "\n\n"
                        + "Ma co hieu luc trong 5 phut va chi duoc su dung mot lan.\n"
                        + "Neu ban khong yeu cau dat lai mat khau, "
                        + "hay bo qua email nay.\n\n"
                        + "Fishing Tackle Shop";

        message.setText(content, StandardCharsets.UTF_8.name());

        Transport.send(message);
    }
}
