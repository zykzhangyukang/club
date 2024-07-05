## 章鱼社区

## 在线演示地址
[https://www.coderman.club](https://www.coderman.club)，任何配置、使用和答疑问题，可以 👉[点击](#联系我) 联系我，也可以拉你进群沟通。

## 主要功能
- *用户登录和注册*：支持用户创建账号并进行登录操作。
- *Token 自动续签*：实现 Token 的自动刷新，提升用户体验和安全性。
- *帖子分类和标签*：将帖子按照不同的分类和标签进行管理和展示。
- *发布帖子*：用户可以发布新的帖子内容。
- *个人中心*：用户可以管理个人信息和设置。
- *帖子详情页*：展示单个帖子的详细内容和讨论。
- *帖子列表*：按照不同条件和分类展示帖子列表。
- *上传头像*：用户可以上传并设置个人头像。
- *消息通知*：实时通知用户有关评论、关注等活动的消息。
- *评论和点赞*：用户可以对帖子进行评论和点赞操作。
- *关注功能*：支持用户关注其他用户或者关注感兴趣的帖子。

## IDEA 启动参数
```
-Dsecret.key=秘钥
-Dspring.profiles.active=dev
-Dlog.file=D:\java\logs\club
-Xms256m
-Xmx256m
```

## Linux部署命令
```$xslt
nohup java -Xms256m -Xmx256m -Dserver.port=8989 -Dsecret.key=秘钥 -Dspring.profiles.active=pro -Dlog.file=./logs -jar club-web-1.0.0.jar >>/dev/null 2>&1 &
```

