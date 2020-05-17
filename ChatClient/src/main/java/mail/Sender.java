package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * 通过smtp协议发送验证码
 * @author livejq
 * @since 2020/5/4
 **/
public class Sender {

    private String content;
    private String to;

    public Sender(String content, String to) {
        this.content = content;
        this.to = to;
    }
    public void init() {
        Properties prop = new Properties();

        try {
            InputStream fis = this.getClass().getResourceAsStream("/mail.properties");
            prop.load(fis);
        }catch (IOException e) {
            e.printStackTrace();
        }

        //方式一 由于没有设置收件人，有可能会被当作垃圾邮件
        /*Session session = Session.getInstance(prop);
        Message msg = new MimeMessage(session);
        try {
            Transport transport = session.getTransport();
            msg.setSubject(prop.getProperty("subject"));
            msg.setFrom(new InternetAddress(prop.getProperty("from"), prop.getProperty("personal")));
            msg.setContent(content, "text/plain;charset=utf-8");
            transport.connect(prop.getProperty("user"), prop.getProperty("password"));
            transport.sendMessage(msg, new Address[]{new InternetAddress(to)});
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        //方式二
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("user"), prop.getProperty("password"));
            }
        });
        Message msg = new MimeMessage(session);
        try {
            msg.setSubject(prop.getProperty("subject"));
            msg.setFrom(new InternetAddress(prop.getProperty("from"), prop.getProperty("personal")));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setContent(content, "text/plain;charset=utf-8");
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

