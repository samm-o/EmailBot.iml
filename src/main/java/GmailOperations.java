package com.gmail.api;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Scanner;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import main.java.Main;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

public class GmailOperations {

    public static void sendMessage(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    public static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException, IOException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from)); //me
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to)); //
        email.setSubject(subject);

        email.setText(bodyText);

        return email;
    }
    public static ArrayList<String> getStringsFromUser() {
        ArrayList<String> strings = new ArrayList<String>();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter one email or a list of emails, separated by commas: ");
        String input = scanner.nextLine();

        // Split the input string by commas and add each resulting string to the list
        for (String str : input.split(",")) {
            strings.add(str.trim());
        }

        return strings;
    }

    public static void sendEmail() throws IOException, GeneralSecurityException, MessagingException {
        var subject = "";
        var body = "";

        Scanner sc = new Scanner(System.in);

        Gmail service = GmailQuickstart.getGmailService();

        System.out.println("Email Bot");
        System.out.println("-------------");
        ArrayList<String> strings = getStringsFromUser();
        System.out.println();

        System.out.println("Subject: ");
        subject = sc.nextLine();
        System.out.println();

        System.out.println("Body: ");
        body = sc.nextLine();

        for (String str : strings) {
            // Call the createEmail method with each string in the list
            MimeMessage Mimemessage = createEmail(str,"me",subject,body);

            Message message = createMessageWithEmail(Mimemessage);

            message = service.users().messages().send("me", message).execute();

            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        }
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException, MessagingException {
        // run with "gradlew --console plain run" to avoid initialization prompts
        sendEmail();
    }
}
