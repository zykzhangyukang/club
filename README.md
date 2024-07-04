# club
论坛项目

#### IDEA 启动参数
```
-Dsecret.key=秘钥
-Xms256m 
-Xmx256m
```

#### 项目部署命令
```$xslt
nohup java -Xms256m -Xmx256m -Dserver.port=8989 -Dsecret.key=秘钥 -Dspring.profiles.active=pro -Dlog.file=./logs -jar club-web-1.0.0.jar >>/dev/null 2>&1 &
```

