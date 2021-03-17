# 视频会议系统
## 需求
1、多人视频音频
2、文字交流
3、白板
## 技术
1、Netty（重点）
 - 长连接
 - webSocket协议
 - 重连
 - 心跳机制
 - UDP
 - RTMP
 
2、GraphQL

3、Redis

4、rabbitMQ

## 设计思路
B/S架构，客户端访问服务端，再服务端随机生成一个6位数的数字，再把数字返回给客户端,<br/>
然后其他客户端带上该数字连接客户端，相同数字的客户端放到一个组中，他们之间可以进行信息交互

## 参考
1、[netty 实现长连接，心跳机制，以及重连](https://blog.csdn.net/weixin_41558061/article/details/80582996)<br>
2、[SpringBoot+Netty开发IM即时通讯系列（二）](https://blog.csdn.net/qq_26975307/article/details/85051833?spm=1001.2014.3001.5502)