# club
论坛项目

#### IDEA 启动参数
```
-Dsecret.key=XXX
-Xms256m 
-Xmx256m
```

#### 项目部署命令
`nohup java -Xms256m -Xmx256m -Dserver.port=8989 -Dsecret.key=XXX -Dspring.profiles.active=pro -jar club-web-1.0.0.jar &`

