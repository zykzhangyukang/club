#!/bin/bash

# 设置Java运行参数和系统属性
JAVA_OPTS="-Xms256m -Xmx256m"
JAVA_PROPS="-Dserver.port=8989 -Dsecret.key=秘钥 -Dspring.profiles.active=pro -Dlog.file=./logs"

# Java命令
JAVA_CMD="java $JAVA_OPTS $JAVA_PROPS -jar club-web-1.0.0.jar"

function start() {
    # 启动Java应用并重定向输出到/dev/null
    nohup $JAVA_CMD >> /dev/null 2>&1 &
    echo "Java应用已启动，日志输出已重定向到/dev/null"
}

function restart() {
    # 查找Java进程并杀死
    echo "正在重启Java应用..."
    PID=$(ps aux | grep 'java.*club-web-1.0.0.jar' | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        kill -9 $PID
        echo "已终止旧进程 PID: $PID"
    else
        echo "未找到运行中的Java应用"
    fi

    # 启动新的Java应用
    start
}

function stop() {
    # 查找Java进程并杀死
    echo "正在停止Java应用..."
    PID=$(ps aux | grep 'java.*club-web-1.0.0.jar' | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        kill $PID
        echo "已终止进程 PID: $PID"
    else
        echo "未找到运行中的Java应用"
    fi
}

function status() {
    # 检查Java进程是否在运行
    PID=$(ps aux | grep 'java.*club-web-1.0.0.jar' | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        echo "Java应用正在运行，进程PID: $PID"
    else
        echo "Java应用未运行"
    fi
}

# 判断命令参数
case "$1" in
    start)
        start
        ;;
    restart)
        restart
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    *)
        echo "用法: $0 {start|restart|stop|status}"
        exit 1
esac

exit 0
