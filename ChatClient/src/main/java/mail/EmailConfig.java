package mail;

import java.io.*;
import java.util.Properties;

/**
 * 读取邮件服务配置
 * @author livejq
 * @since 2020/5/4
 **/
public class EmailConfig {

    public static final String MAIL_DEBUT = "mail.debug";
    public static final String MAIL_HOST = "mail.host";
    public static final String MAIL_OBJECT = "mail.object";
    public static final String MAIL_PERSONAL = "mail.personal";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_USER = "mail.smtp.user";
    public static final String MAIL_SMTP_PASS = "mail.smtp.pass";



    //是否开启debug调试
    private static String debug;

    //发送服务器是否需要身份验证
    private static String auth;

    //发送邮件协议名称
    private static String protocol;

    //发送邮件发件人
    private static String from;

    //发送邮件端口
    private static String port;

    //邮件服务器主机名
    private static String host;

    //发送邮件用户名
    private static String user;

    //发送邮件邮箱密码
    private static String pass;

    //邮件主题
    private static String object;

    //邮件类型
    private static String personal;

    //创建Session配置信息
    private static final Properties sessionProperties = new Properties();

    {
        try {
            InputStream fis = new FileInputStream(new File("ChatClient/config/mail.properties"));
            Properties prop = new Properties();
            prop.load(fis);
            EmailConfig.debug = prop.getProperty(EmailConfig.MAIL_DEBUT, "true").trim();
            EmailConfig.host = prop.getProperty(EmailConfig.MAIL_HOST, "smtp.163.com").trim();
            EmailConfig.pass = prop.getProperty(EmailConfig.MAIL_OBJECT, "来自“聊吧”毕设项目的验证码").trim();
            EmailConfig.pass = prop.getProperty(EmailConfig.MAIL_PERSONAL, "通知邮件").trim();
            EmailConfig.auth = prop.getProperty(EmailConfig.MAIL_SMTP_AUTH, "true").trim();
            EmailConfig.protocol = prop.getProperty(EmailConfig.MAIL_TRANSPORT_PROTOCOL, "smtp").trim();
            EmailConfig.from = prop.getProperty(EmailConfig.MAIL_SMTP_FROM, "15767232209@163.com").trim();
            EmailConfig.port = prop.getProperty(EmailConfig.MAIL_SMTP_PORT, "25").trim();
            EmailConfig.user = prop.getProperty(EmailConfig.MAIL_SMTP_USER, "15767232209@163.com").trim();
            EmailConfig.pass = prop.getProperty(EmailConfig.MAIL_SMTP_PASS, "LNWWWCAJADCCTMVX").trim();

            fis.close();

            System.out.println("开始：");
            System.out.println(debug);
            System.out.println(host);
            System.out.println(object);
            System.out.println(personal);
            System.out.println(auth);
            System.out.println(protocol);
            System.out.println(from);
            System.out.println(port);
            System.out.println(user);
            System.out.println(pass);
            sessionProperties.setProperty(EmailConfig.MAIL_DEBUT, EmailConfig.debug);
            sessionProperties.setProperty(EmailConfig.MAIL_HOST, EmailConfig.host);
            sessionProperties.setProperty(EmailConfig.MAIL_OBJECT, EmailConfig.object);
            sessionProperties.setProperty(EmailConfig.MAIL_PERSONAL, EmailConfig.personal);
            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_AUTH, EmailConfig.auth);
            sessionProperties.setProperty(EmailConfig.MAIL_TRANSPORT_PROTOCOL, EmailConfig.protocol);
            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_FROM, EmailConfig.from);
            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_PORT, EmailConfig.port);
            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_USER, EmailConfig.user);
            sessionProperties.setProperty(EmailConfig.MAIL_SMTP_PASS, EmailConfig.pass);

        } catch (Exception e) {
            System.out.println("邮箱配置信息初始化异常");
        }
    }

    public static Properties getSessionProperties() {
        return sessionProperties;
    }
}