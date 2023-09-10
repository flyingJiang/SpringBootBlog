# 生尘
仅供交流学习使用，请勿用于其他

由于本人并不擅长前端开发，相关效果实现带有局限性，还望后续使用的同学，自行修正
## 中医未病诊疗
由SpringBoot1.5 + MyBatis + Thymeleaf等技术实现的，如果觉得项目不错，请帮忙`Star`支持一下。
原项目是一个博客，目前保留了博客的功能，并设计成了一款医疗主题的项目
### 适用对象
* Spring Boot 初学者。
* 与作者一样，不擅长前端架构的
* 该博客系统模仿 hexo 生成的访问路径，并支持 markdown 文件导入功能。
* 懵懂者。初次接触博客系统的人。
### 技术栈
#### 后端
* 核心框架：SpringBoot
* 持久层框架：MyBatis
* 模板框架：Thymeleaf
* 分页插件：PageHelper
* 缓存框架：Ehcache
* Markdown：Commonmark

#### 前端
* JS框架：Jquery
* CSS框架：Bootstrap
* 富文本编辑器：editor.md
* 文件上传：dropzone
* 弹框插件：sweetalert

#### 第三方
* 七牛云（文件上传）
* 百度统计

### 预览效果
#### 医疗主题效果
![index](http://pb84kab39.bkt.clouddn.com/sc/demo01.jpg)
![see](http://pb84kab39.bkt.clouddn.com/sc/demo02.png)
![ask](http://pb84kab39.bkt.clouddn.com/sc/demo03.png)
![goto](http://pb84kab39.bkt.clouddn.com/sc/demo04.png)
![report](http://pb84kab39.bkt.clouddn.com/sc/demo05.png)
![recommend](http://pb84kab39.bkt.clouddn.com/sc/demo06.png)

#### 前端效果
![index](https://github.com/caozongpeng/github-static/blob/master/springBootBlog/index.png)

#### 后端效果
![adminlogin](https://github.com/caozongpeng/github-static/blob/master/springBootBlog/adminlogin.png)

### 安装
下载源码，执行sql文件，然后修改application-dev.yml文件中连接数据库的用户名、密码。运行项目即可。

医疗主题地址：
http://localhost:8888/index

前端访问地址：
http://localhost:8888

后台访问地址：http://localhost:8888/admin 用户名：admin 密码：123456

### 辅助工具
```shell script
mysql.server start
```

### Question
```shell script
Mon Aug 07 21:06:43 CST 2023 WARN: Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.
```

解决方法：

在mysql连接字符串的url中添加配置 ?useSSL=false即可：

useSSL=false        禁用SSL
useServerPrepStmts=true        开启预编译功能