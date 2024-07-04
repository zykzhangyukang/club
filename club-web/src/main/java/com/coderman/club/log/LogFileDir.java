package com.coderman.club.log;

import ch.qos.logback.core.PropertyDefinerBase;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author coderman
 */
@Component
public class LogFileDir extends PropertyDefinerBase {


    @Override
    public String getPropertyValue() {

        System.setProperty("server.tomcat.accesslog.pattern","%v %{X-Forwarded-For}i %l %{Cdn-Src-Ip}i %u %h %t \"%r\"%{Referer}i\" \"{User-Agent}i\" %s %b %T %D");

        String logFile = System.getProperty("log.file");

        String env = System.getProperty("spring.profiles.active");

        if(StringUtils.isEmpty(env)){

            try {

                throw new Exception("请指定环境[dev,fat,uat,pro]值,具体配置请参考:-Dspring.profiles.active=dev");

            }catch (Exception e){

                e.printStackTrace();
                System.exit(0);
            }
        }

        if(StringUtils.isEmpty(logFile)){


            try {

                throw new Exception("请指定log.file路径值,具体配置请参考:-Dlog.file=D:\\log\\springboot\\xx");

            }catch (Exception e){

                e.printStackTrace();
                System.exit(0);
            }
        }

        return logFile;
    }
}
