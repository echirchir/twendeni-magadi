package org.etma.main.helpers;

import android.content.Context;

import com.sun.mail.smtp.SMTPMessage;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class MailGunner {

    private static Context mContext;
    private static String _subject;
    private static String _content;

    public MailGunner(Context context, String content)
    {
        mContext = context;
        _subject      = "Support: Twendeni Magadi App";
        _content = content;
    }

    /**
     * Build a Session object for an SMTP server that requires both TSL and
     * authentication. This uses Gmail as an example of such a server
     *
     * @return a Session for sending email
     */
    public static Session buildGoogleSession(String email, String password) {
        Properties mailProps = new Properties();
        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.host", "smtp.gmail.com");
        mailProps.put("mail.from", "example@gmail.com");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.port", "587");
        mailProps.put("mail.smtp.auth", "true");
        // final, because we're using it in the closure below
        final PasswordAuthentication usernamePassword = new PasswordAuthentication(
                email, password);
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return usernamePassword;
            }
        };
        Session session = Session.getInstance(mailProps, auth);
        session.setDebug(true);
        return session;

    }

    /**
     * Build an HTML message with an image embedded in the message.
     *
     * @param session session for email
     * @return a multipart MIME message where the main part is an HTML message and the
     * second part is an image that will be displayed within the HTML.
     * @throws MessagingException
     * @throws IOException
     */
    public Message buildMessageForSupport(Session session) throws MessagingException, IOException {
        SMTPMessage m = new SMTPMessage(session);
        MimeMultipart content = new MimeMultipart("related");
        MimeBodyPart textPart = new MimeBodyPart();

        textPart.setText("<html><head>"
                        + "</head>\n"
                        + "<body>"
                        + "<h2>Support Issues Experienced</h2>"
                        + "<p>" + _content + "</p>"
                        + "<div>Thank You!</div>"
                        + "</body></html>",
                "US-ASCII", "html");

        content.addBodyPart(textPart);

        m.setContent(content);
        m.setSubject(_subject);
        return m;
    }

    //
    //
    // Message sending methods
    //
    //

    /**
     * Send the message with Transport.send(Message)
     *
     * @param message
     * @param recipient
     * @throws MessagingException
     */
    public void addressAndSendMessage(Message message, String recipient) throws AddressException, MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        Transport.send(message);
    }

}
