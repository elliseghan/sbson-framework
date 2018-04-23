package ca.concordia.cs.aseg.sbson.experiment.survey;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailDistributor {
    private Properties emailProperties;
    private String login, password, message, subject;

    public static void main(String[] args) {
        TreeSet<String> email = new TreeSet<>();
        try {
            EmailDistributor emailDistributor = new EmailDistributor("e_eghan@encs.concordia.ca", "Tr1n!ty");
            String emailListLocation = "/emails-maven.txt";
            TreeMap<String, String> mavenEmails = new TreeMap<>();
            InputStream is = EmailDistributor.class.getResourceAsStream(emailListLocation);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String string = stringBuffer.substring(1);
            String[] strings = string.split(",");
            for (String str : strings) {
                String[] stringParts = str.split("=");
                String cleanEmail = emailDistributor.cleanEmail(stringParts[0].replaceAll("\"", "").trim());
                if (!cleanEmail.equals("n/a"))
                    email.add(cleanEmail);
            }
            is.close();
            bufferedReader.close();

            emailListLocation = "/emails.csv";
            is = EmailDistributor.class.getResourceAsStream(emailListLocation);
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            while ((line = bufferedReader.readLine()) != null) {
                String[] stringParts = line.split(",");
                String cleanEmail = emailDistributor.cleanEmail(stringParts[1].replaceAll("\"", "").trim());
                if (!cleanEmail.equals("n/a"))
                    email.add(cleanEmail);
            }
            is.close();
            bufferedReader.close();

            email.forEach((str) -> emailDistributor.sendEmail(str));
            //System.out.println(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String cleanEmail(String emailString) {
        String cleanEmailString = "n/a";
        Matcher m = null;
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
                //System.out.println(cleanEmailString);
            }
        return cleanEmailString;
    }

    public void sendEmail(String receipientEmail) {
        System.out.println(receipientEmail);
        Authenticator auth = new SMTPAuthenticator(this.login, this.password);
        Session session = Session.getInstance(this.emailProperties, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            //	msg.setText(message);
            msg.setContent(getMessage(), "text/html");
            msg.setSubject(this.subject);
            msg.setFrom(new InternetAddress(this.login));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receipientEmail));
            //Transport.send(msg);

            //add email to success file
            URL resourceUrl = EmailDistributor.class.getResource("/sentEmails.txt");
            File file = Paths.get(resourceUrl.toURI()).toFile();
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(receipientEmail);
            bufferedWriter.write("\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDistributor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
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
