# JKBlog

#### use lib
``` html
Guice ( Ioc )
Netty ( Http Server )
MyBatis ( 数据库操作 )
HikariCP ( 数据连接池 )
Gson ( json 序列化 )
slf4j ( 日志 )
freemarker ( 模板，用于页面调试 )
```

#### 特点

* 使用 Netty 为高性能服务器，gradle release 打出 jar 包后

通过 java -jar demo.jar you_config_path & 即可启动服务


#### 完成进度
``` html
Guice 整合 netty [OK]
Netty 支持 Http1 [OK]
Netty 支持 Https [OK]
Netty 支持 Http2 [NO]
Netty 支持 Websocket [NO]
数据库 ORM 操作 [OK]
支持注解输入 json [OK]
Freemarker 模板 [OK]
Redis <jedis> [OK]
Protobuf [NO]
```
