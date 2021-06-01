package services.utility;

import com.sun.mail.imap.protocol.FLAGS;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.WebDriver;
import services.Constants;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static services.utility.ReportService.catchException;
import static services.utility.WaiterService.sleep;

@Log4j
public class MailService {

    //https://www.javatpoint.com/example-of-deleting-email-using-java-mail-api
    public static Folder findAllMails(String email, String password, String host, String protocol) {

        Properties properties = new Properties();
        if(protocol.equals(Constants.PROTOCOL_IMAP)){
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.user", email);
            properties.put("mail.imap.socketFactory", 993);
            properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.imap.port", 993);
        }
            else{
                properties = System.getProperties();
        }
        Session session = Session.getDefaultInstance(properties);

        Store store;
        Folder folder = null;

        try {
            store = session.getStore(protocol);
            store.connect(host, email, password);

            folder = store.getFolder("inbox");

            if (!folder.exists()) {
                System.out.println("inbox not found");
                throw new MessagingException();
            }

            folder.open(Folder.READ_WRITE);

            } catch (MessagingException e) {
                catchException(e);
        }

        return folder;
    }

    public static List<Message> findMailsBySender(String email, String password, String senderEmail, String host, String protocol) throws MessagingException {

        Folder folder = findAllMails(email, password, host, protocol);
        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getSenderEmailFromMessage(message).equals(senderEmail)) {
                listSenderEmails.add(message);
            }
        }
        return listSenderEmails;
    }

    public static List<Message> findMailsByReceiver(String email, String password, String receiver, String host, String protocol) throws MessagingException {

        Folder folder = findAllMails(email, password, host, protocol);
        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getRecipientEmailFromMessage(message).equals(receiver)) {
                listSenderEmails.add(message);
            }
        }
        return listSenderEmails;
    }

    public static List<Message> findMailsByReceiverAndSubject(String email, String password, String receiver, String subject, String host, String protocol) throws MessagingException {

        Folder folder = findAllMails(email, password, host, protocol);
        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getRecipientEmailFromMessage(message).equals(receiver) && message.getSubject().equals(subject)) {
                listSenderEmails.add(message);
            }
        }
        return listSenderEmails;
    }

    public static List<Message> findMailsBySubjectAndText(String email, String password, String subject, List<String> text, String host, String protocol) throws MessagingException, IOException {

        Folder folder = findAllMails(email, password, host, protocol);
        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (message.getSubject().equals(subject)) {
                List<String> listOccurrences = new ArrayList<>();
                for (String s : text) {
                    if (getTextFromMessage(message).contains(s))
                        listOccurrences.add(s);
                }
                if(listOccurrences.equals(text))
                    listSenderEmails.add(message);
            }
        }
        return listSenderEmails;

    }

    public static List<Message> findMailsBySenderAndSubject(String email, String password, String sender, String subject, String host, String protocol) throws MessagingException, IOException {

        Folder folder = findAllMails(email, password, host, protocol);
        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getSenderEmailFromMessage(message).equals(sender) && message.getSubject().equals(subject)){
                listSenderEmails.add(message);
            }
        }
        return listSenderEmails;

    }

    public static void deleteLastMailBySenderEmail(String email, String password, String senderEmail, String host, String protocol) throws MessagingException, IOException {

        Folder folder = findAllMails(email, password, host, protocol);

        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getSenderEmailFromMessage(message).equals(senderEmail)) {
                listSenderEmails.add(message);
            }
        }

        listSenderEmails.get(listSenderEmails.size()-1).setFlag(FLAGS.Flag.DELETED, true);
        folder.close(true);
    }

    public static void deleteLastMailByReceiverEmail(String email, String password, String receiver, String host, String protocol) throws MessagingException, IOException {

        log.info("Delete email by receiver email - " + receiver);

        Folder folder = findAllMails(email, password, host, protocol);

        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getRecipientEmailFromMessage(message).equals(receiver)) {
                listSenderEmails.add(message);
            }
        }

        listSenderEmails.get(listSenderEmails.size()-1).setFlag(FLAGS.Flag.DELETED, true);
        folder.close(true);
    }

    public static void deleteAllMailsBySenderEmail(String email, String password, String senderEmail, String host, String protocol) throws MessagingException, IOException {

        log.info("Delete all emails by sender email: " + senderEmail);

        Folder folder = findAllMails(email, password, host, protocol);

        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (getSenderEmailFromMessage(message).equals(senderEmail)) {
                listSenderEmails.add(message);
            }
        }

        if(!listSenderEmails.isEmpty()){
            listSenderEmails.forEach(message ->
            {
                try {
                    message.setFlag(FLAGS.Flag.DELETED, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }
        else{
            log.info("No email found with sender : " + senderEmail);
        }

        folder.close(true);
    }

    public static void deleteLastMailBySubjectAndText(String email, String password, String subject, List<String> text, String host, String protocol) throws MessagingException, IOException {

        log.info("Delete email by subject - " + subject + " and text - " + text.get(0));
        Folder folder = findAllMails(email, password, host, protocol);

        Message[] list = folder.getMessages();
        List<Message> listSenderEmails = new ArrayList<>();
        for (Message message : list) {
            if (message.getSubject().equals(subject)) {
                List<String> listOccurrences = new ArrayList<>();
                for (String s : text) {
                    if (getTextFromMessage(message).contains(s))
                        listOccurrences.add(s);
                }
                if(listOccurrences.equals(text))
                    listSenderEmails.add(message);
            }
        }

        listSenderEmails.get(listSenderEmails.size()-1).setFlag(FLAGS.Flag.DELETED, true);
        folder.close(true);
    }

    public static void waitForLetter(String email, String password, String sender, String host, WebDriver driver, int timeout, String protocol) throws MessagingException {

        log.info("Waiting for letter from " + sender);
        int attempt = 0;
        boolean flag = false;
        do {
            if(!findMailsBySender(email, password, sender, host, protocol).isEmpty()){
                flag = true;
                break;
            }
            attempt++;
            log.info("Iteration #" + attempt);
            sleep(20);
            driver.getCurrentUrl();
        }
        while (!flag && attempt< timeout);

        if(!flag)
            throw new CustomException("Didn't receive mail from user " + sender);
    }

    public static void waitForLetterByReceiver(String email, String password, String receiver, String host, WebDriver driver, int timeout, String protocol) throws MessagingException {

        log.info("Waiting for letter for: " + receiver);
        int attempt = 0;
        boolean flag = false;
        do {
            if(!findMailsByReceiver(email, password, receiver, host, protocol).isEmpty()){
                flag = true;
                break;
            }
            attempt++;
            log.info("Iteration #" + attempt);
            sleep(20);
            driver.getCurrentUrl();
        }
        while (!flag && attempt< timeout);

        if(!flag)
            throw new CustomException("Didn't receive mail for user: " + receiver);
    }

    public static void waitForLetterByReceiverAndSubject(String email, String password, String receiver, String subject, String host, WebDriver driver, int timeout, String protocol) throws MessagingException {

        log.info("Waiting for letter for: " + receiver);
        int attempt = 0;
        boolean flag = false;
        do {
            if(!findMailsByReceiverAndSubject(email, password, receiver, subject, host, protocol).isEmpty()){
                flag = true;
                break;
            }
            attempt++;
            log.info("Iteration #" + attempt);
            sleep(20);
            driver.getCurrentUrl();
        }
        while (!flag && attempt< timeout);

        if(!flag)
            throw new CustomException("Didn't receive mail for user: " + receiver);
    }

    public static void waitForLetterSubjectAndText(String email, String password, String subject, List<String> text, String host, WebDriver driver, int timeout, String protocol) throws MessagingException, IOException {

        log.info("Waiting for letter with subject " + subject);
        int attempt = 0;
        boolean flag = false;
        do {
            if(!findMailsBySubjectAndText(email, password, subject, text, host, protocol).isEmpty()){
                flag = true;
                break;
            }
            attempt++;
            log.info("Iteration #" + attempt);
            sleep(10);
            driver.getCurrentUrl();
        }
        while (!flag && attempt < timeout);

        if(!flag)
            throw new CustomException("Didn't receive mail with subject " + subject + " and text " + text);
    }

    public static void waitForNewMessages(String email, String password, String sender, int messagesNumber, String host, WebDriver driver, int timeout, String protocol) throws MessagingException, IOException {

        log.info("Waiting for new messages");
        int attempt = 0;
        boolean flag = false;
        do {
            if(findMailsBySender(email, password, sender, host, protocol).size() > messagesNumber){
                flag = true;
                break;
            }
            attempt++;
            log.info("Iteration #" + attempt);
            sleep(10);
            driver.getCurrentUrl();
        }
        while (!flag && attempt < timeout);

        if(!flag)
            throw new CustomException("Didn't receive new messages");
    }

    public static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append("\n").append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    public static String getSenderEmailFromMessage(Message message) throws MessagingException {
        Address[] from = message.getFrom();
        return from == null ? null : ((InternetAddress) from[0]).getAddress();
    }

    public static String getRecipientEmailFromMessage(Message message) throws MessagingException {
        Address[] from = message.getAllRecipients();
        return from == null ? null : ((InternetAddress) from[0]).getAddress();
    }
}
