<h1 align="center">聊吧</h1>
[![HitCount](http://hits.dwyl.io/livejq/Chatroom.svg)](http://hits.dwyl.io/livejq/Chatroom)
[![GitHub issues](https://img.shields.io/github/issues/livejq/Chatroom.svg)](https://github.com/livejq/Chatroom/issues)
[![JFoenix](https://img.shields.io/badge/JFoenix-8.0.8-red)](https://search.maven.org/remotecontent?filepath=com/jfoenix/jfoenix/8.0.8/jfoenix-8.0.8.jar)
[![Java Version](https://img.shields.io/badge/Java-1.8-important)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![fontawesomefx Version](https://img.shields.io/badge/fontawesomefx-8.9-blue)](https://bitbucket.org/Jerady/fontawesomefx/downloads/)
[![SceneBuilder](https://img.shields.io/badge/SceneBuilder-8.5.0-9cf)](https://bitbucket.org/Jerady/fontawesomefx/downloads/)
[![GitHub license](https://img.shields.io/github/license/livejq/Chatroom.svg)](https://github.com/livejq/Chatroom/blob/master/LICENSE)

<p align="center">
<sup>
<b>适合JavaFX新手练习的聊天室，UI采用了<a href="https://github.com/jfoenixadmin/JFoenix" target="_blank">JFoenix</a></b>
</sup>
</p>

<hr>

刚上手JavaFX？想对JavaFX做个系统性的了解？想来点简单的项目练练手？那这个项目就非常适合您的了。这个项目主要以JavaFX客户端设计为主，服务端采用Java RMI来进行简单的远程连接请求。服务端通过客户端对象数组对每个客户端进行管控，包括UI界面的更新；数据库中只需添加一张用户表用来验证用户登录即可。



## 实现效果

![Chatroom Login](/blog-comment-repo/Chatroom/login_ui.jpg)

<hr>

![Progress Bar](/blog-comment-repo/Chatroom/progressBar.jpg)

<hr>

![Chatroom Login](/blog-comment-repo/Chatroom/chatroom_ui.jpg)

<hr>

<div style="margin:10px;padding:10px;">
<img src="/blog-comment-repo/Chatroom/email.png" style="width:375px;height:667px;float:left"  ><img src="/blog-comment-repo/Chatroom/email2.png" style="width:375px;height:667px;margin-left:30px;float:left" >
</div><br><br>

<hr style="clear:both;">


## 内容列表

- [简单介绍](##简单介绍)
  - [界面设计](###界面设计)
  - [常规登录](###常规登录)
  - [聊天细节](###聊天细节)
  - [RMI通信原理](###RMI通信原理)
- [注意事项](##注意事项)
- [致谢](##致谢)
- [参与贡献方式](##参与贡献方式)
- [许可证](##许可证)



## 简单介绍



> <a href="/blog-comment-repo/Chatroom/Chatroom_Sequence_Diagram.svg">项目时序图</a>



### 界面设计

![Chatroom Fxml](/blog-comment-repo/Chatroom/chatroomfxml.png)

不管是登录注册还是聊天室窗体，基本的设计过程都一样：通过在图纸上画图，将整体格局设定好。如宽高，横纵坐标位置，对齐方式等。之后根据局部控件位置再进行适当的调整，将布局位置（包括内外边距）数值不断地进行精确计算，最后通过SceneBuilder工具将其转成FXML文件实现，类似如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.layout.StackPane?>

<StackPane fx:id="mainPane" stylesheets="@../util/css/dark-theme.css" xmlns="http://javafx.com/javafx/8.0.171" xmlns:fx="http://javafx.com/fxml/1" fx:controller="ui.controller.Chatroom">
	<xxx></xxx>
</StackPane>
```

在开头声明xml版本后，需要导入相应的JavaFX包。FXML文件的特点是，在最外层上可以声明其命名空间、使用的css文件位置和其对应的控制器位置。这是因为JavaFX在解析FXML文件时，首先读取的就是根节点，也就是最外层的节点，这里则为StackPane。



### 常规登录



![Chatroom Login](/blog-comment-repo/Chatroom/login.jpg)

用户先注册（新用户），接着进行登录操作。若忘记密码，还可以通过填入邮箱并获取验证码的方式，设置新密码。成功登陆后进入聊天室。也许你会说，聊天室要啥注册啊，直接让用户起个用户名，验证没有重复之后直接进入聊天吧。若没有意外的话，确实应该如此。但由于考虑到以后的改版，为实现N-to-N的聊天提前做好准备吧。若一开始就把自己局限起来，那还有什么发展可言嘞。



### 忘记密码



忘记密码需要实现SMTP的邮件服务。当用户填写完邮箱地址后点击获取验证码，则客户端向其发送随机4位数字验证码，每60秒刷新一次。填完验证码和自己的新密码后点击确定，验证无误后弹出修改成功的提示。这里可以验证用户输入的邮箱是否已注册，但无法在注册中确保用户的邮箱是否为真实邮箱，所以有可能发送失败。根源还是在于用户注册时该如何确保其输入了真实的邮箱地址。“市面上”的常规做法是当点击注册并验证无误后，向该邮箱地址发送一条确认注册的网址链接。用户点击验证后完成最终的注册。这需要额外的Http服务支持，所以暂时排除不了此Bug。



### 聊天细节

![Chatroom Process](/blog-comment-repo/Chatroom/chatroom.jpg)

聊天室主要需要优先实现发送消息的功能。在界面中，用户可以输入文本消息。或者通过点击表情按钮弹出Emoji表情面板，选择需要的表情，并将其添加到文本旁边，用以丰富用户的表达方式。点击发送后将其显示在用户聊天界面的右边。



### RMI通信原理

![RMI](/blog-comment-repo/Chatroom/RMI.jpg)

RMI技术的核心就是可以为客户端动态生成本地的Stub代理，而注册表则作为一种辅助工具提供协助连接，可以在本地或远端提供。服务端开启服务后，首先启动注册表并将自己声明的服务以一个别名注册到注册表中。当客户端向服务端发起请求时，将首先通过远端的注册表查找自己所请求的服务，若存在，则开始建立连接并将此服务在客户端本地生成一个Stub代理。需要注意的是，除了基本类型不用序列化外，其它引用类型必须要实现Serializable接口。服务端若需要对外提供服务，则必须实现Remote接口或继承UnicastRemoteObject，未在Remote接口中声明的方法则不会暴露给请求者 。



## 注意事项

- 服务端对外提供的接口需要继承Remote接口
- 服务端启动器启动时需要声明服务名称并绑定到注册表上，例如：

```java
String ip = properties.getProperty("ip");
String registryPort = properties.getProperty("registryPort");
sb.append("ip:" + ip + ", port:" + registryPort);
System.setProperty("java.rmi.server.hostname", ip);
Registry registry = LocateRegistry.createRegistry(Integer.parseInt(registryPort));
registry.rebind("ChatServer", new ChatControllerImpl());
logger.info("正在启动服务...");
```

<br>

- 客户端通过服务器地址和请求的服务名来连接，例如：

```java
logger.info("正在读取服务器配置...");
InputStream input = new FileInputStream(new File("ChatClient/config/settings.properties"));
properties.load(input);
logger.info(properties.getProperty("server-ip"));
url = String.format("rmi://%s:%s/ChatServer", properties.getProperty("server-ip"), properties.getProperty("registryPort"));
logger.info("正在查找服务器... {}", url);
chatController = (ChatController) Naming.lookup(url);
```

<br>

- 第一次进行连接的时候可能会出现无法连接的问题，那是由于服务器和客户端都在本地，而在配置中声明了127.0.0.1，这在host文件中映射的是localhost。当客户端向服务器发请连接请求时，注册表发现客户端想要连接的竟然是本地自己，所以导致阻塞，造成无法转接，将其改为局域网IP即可。

- 当客户端断开后再次进行连接时出现连接失败，只能重启服务器才能解决。这是由于需要将引用类型序列化后进行传输，但若没有指定序列号，系统每次会随机生成一个。因此，客户端一旦重启，则使得与服务端的序列号不同。解决办法是，为需要序列化的类手动声明序列号，例如：User类。

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 42L;
    private String email;
    private String password;
    private String nickname;
    private String gender;
    private String birth;
    private String avatar;
}
```



## 致谢

本项目由[Oshan96](https://github.com/Oshan96/ChatRoomFX)衍生而来，只为学习交流使用，在此感谢万分！



## 参与贡献方式

欢迎[issuess](https://github.com/livejq/Chatroom/issues)



## 许可证

@[MIT](https://github.com/livejq/Chatroom/blob/master/LICENSE) License

