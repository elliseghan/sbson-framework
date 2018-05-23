package ca.concordia.cs.aseg.sbson.experiment.survey;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;


public class EmailDistributor {
    private Properties emailProperties;
    private String login, password, message, subject;

    public static void main(String[] args) {
        EmailDistributor emailDistributor = new EmailDistributor("e_eghan@encs.concordia.ca", "Tr1n!ty");
        emailDistributor.sendEmail("api-break-survey@encs.concordia.ca");
    }

    public EmailDistributor() {
    }

    public EmailDistributor(String login, String password) {
        emailProperties = new Properties();
        emailProperties.setProperty("mail.host", "smtp.encs.concordia.ca");
        emailProperties.setProperty("mail.smtp.port", "25");
        emailProperties.setProperty("mail.smtp.auth", "true");
        emailProperties.setProperty("mail.smtp.starttls.enable", "true");
        this.login = login;
        this.password = password;
        this.subject = getSubject();
    }

    /*public String cleanEmail(String emailString) {
        if (emailString.endsWith(".edu"))
            return "n/a";
        if (emailString.endsWith("@qq.com"))
            return "n/a";
        if (emailString.endsWith("noreply") || emailString.endsWith("no-reply"))
            return "n/a";
        if (!(emailString.contains("gmail") || emailString.contains("yahoo") || emailString.contains("hotmail") || emailString.contains("live")))
            return "n/a";
        if (emailString.contains("{") || emailString.contains("}") || emailString.contains("+") || emailString.contains("*"))
            return "n/a";
        if (emailString.contains(" at ")) {
            emailString = emailString.replace(" at ", "@");
        }
        // String cleanEmailString = "n/a";
       *//* Matcher m = null;
        if(emailString.contains("noreply"))
            return cleanEmailString;
        if (emailString.contains(" at ")) {
            emailString = emailString.replace(" at ", "@");
        }

        m = Pattern.compile(
                "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
                .matcher(emailString);

        if (m != null)
            while (m.find()) {
                cleanEmailString = m.group();
                //if (cleanEmailString.contains(".edu."))
                //System.out.println(cleanEm
                ailString);
            }*//*
        if (EmailValidator.getInstance().isValid(emailString)) {
            return emailString;
        } else {
            return "n/a";
        }
    }

    private int getAverageCommits() throws IOException {
        int avgCommits = 0;
        int sum = 0, count = 0;
        String emailListLocation = "/emails.csv";
        InputStream is = EmailDistributor.class.getResourceAsStream(emailListLocation);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] stringParts = line.split(",");
            int commits = 0;
            try {
                commits = Integer.valueOf(stringParts[2]);
            } catch (NumberFormatException e) {
            }

            sum = sum + commits;
            count++;
        }
        avgCommits = sum / count;
        System.out.println(sum + ", " + count + ", " + avgCommits);
        is.close();
        bufferedReader.close();

        return avgCommits;
    }

    private Set<String> pickRandomEmails(String fileLocation, int n) throws IOException {
        List<String> emailList = new ArrayList<>();
        Set<String> chosenEmails = new TreeSet<>();
        InputStream is = EmailDistributor.class.getResourceAsStream(fileLocation);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            emailList.add(line);
        }

        for (int i = 0; i < n; i++) {
            Collections.shuffle(emailList);
            String email = emailList.get(0);
            if (email != null) {
                chosenEmails.add(email);
                emailList.remove(email);
            }
        }

        return chosenEmails;
    }*/
    public void sendEmail(String receipientEmail) {
        // System.out.println(receipientEmail);
        Authenticator auth = new SMTPAuthenticator(this.login, this.password);
        Session session = Session.getInstance(this.emailProperties, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            //	msg.setText(message);
            msg.setContent(getMessage(), "text/html");
            msg.setSubject(this.subject);
            msg.setFrom(new InternetAddress(this.login));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receipientEmail));
            Transport.send(msg);

        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMessage() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream is = EmailDistributor.class.getResourceAsStream("/email.html");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        // System.out.println(stringBuffer.toString());
        return stringBuffer.toString();
    }

    private String getSubject() {
        return "Research study on the impact of API breaking changes";
    }

    private class SMTPAuthenticator extends Authenticator {
        private PasswordAuthentication authentication;

        public SMTPAuthenticator(String login, String password) {
            authentication = new PasswordAuthentication(login, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }
}
