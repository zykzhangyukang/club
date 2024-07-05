# 该镜像需要依赖的基础镜像
FROM java:8
# 将当前目录下的jar包复制到docker容器的/目录下
ADD ./club-web/target/club-web-1.0.0.jar .
# 声明服务运行在8080端口
EXPOSE 8989
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java","-Xms256m", "-Xmx256m","-Dsecret.key=秘钥", "-Dspring.profiles.active=pro","-Dlog.file=./logs", "-jar","club-web-1.0.0.jar"]
# 指定维护者的名字
MAINTAINER coderman