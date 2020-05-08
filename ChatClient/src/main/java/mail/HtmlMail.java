package mail;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import util.VerifyCodeUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * 发送邮件
 * @author livejq
 * @since 2020/5/4
 **/
public class HtmlMail {

    /**
     * 向指定邮箱发送验证码
     *
     * @param target 目标邮箱地址
     * @return java.lang.String
     */
    public String send(String target) {
        SAXReader reader = new SAXReader();
        String code = null;
        try {
            //获取html邮件模板
            Document document = reader.read(this.getClass().getResource("../ui/util/html/model.html").getPath());
            Element root = document.getRootElement();
            //获取id为verifyCode的节点。
            Element verifyCode = getNodes(root, "id", "verifyCode");
            //填充模板
            code = VerifyCodeUtil.generateVerifyCode(4);
            verifyCode.setText(code);
            //保存到临时文件
            FileWriter fileWriter = new FileWriter("temp.html");
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write(document);
            writer.flush();
            //读取临时文件，并把html数据写入到字符串str中，通过邮箱工具发送
            FileReader in = new FileReader("temp.html");
            char[] buff = new char[1024 * 10];
            in.read(buff);
            String str = new String(buff);
            //fixme 模板中读取出来的信息在信箱邮件尾部出现大量AAAAA...（乱码）
            Sender sender = new Sender("欢迎回来，您的验证码是：" + code, target);
            sender.init();
        } catch (Exception e) {
            System.out.println("客户端网络异常，请检查网络连接情况!");
            e.printStackTrace();
        }

        return code;
    }

    /**
     * 递归遍历子节点，根据属性名和属性值，找到对应属性名和属性值的那个子孙节点
     *
     * @param node      要进行子节点遍历的节点
     * @param attrName  属性名
     * @param attrValue 属性值
     * @return 返回对应的节点或null
     */
    public Element getNodes(Element node, String attrName, String attrValue) {

        //当前节点的所有属性
        final List<Attribute> listAttr = node.attributes();
        //遍历当前节点的所有属性
        for (final Attribute attr : listAttr) {
            //属性名称
            final String name = attr.getName();
            //属性的值
            final String value = attr.getValue();
            if (attrName.equals(name) && attrValue.equals(value)) {
                return node;
            }
        }
        //所有一级子节点的list
        final List<Element> listElement = node.elements();
        //遍历所有一级子节点
        for (Element e : listElement) {
            Element temp = getNodes(e, attrName, attrValue);
            if (temp != null) {
                return temp;
            }
        }

        return null;
    }
}
