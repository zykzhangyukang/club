package com.coderman.club.config;

import com.coderman.club.utils.DesUtil;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyukang
 */
@Configuration
public class EncryptPropertyConfig {


    /**
     * 配置文件解密
     *
     * @return {@link EncryptablePropertyResolver}
     */
    @Bean
    public EncryptablePropertyResolver encryptablePropertyResolver() {

        return value -> {

            if (StringUtils.isBlank(value)) {

                return value;
            }

            String crypyKey = System.getProperty("secret.key");

            if (value.trim().startsWith("DES@") && StringUtils.isBlank(crypyKey)) {

                value =  DesUtil.decrypt(value.trim().substring(4));

            }else if (value.trim().startsWith("DES@") && StringUtils.isNotBlank(crypyKey)){

                value =  DesUtil.decrypt(value.trim().substring(4), crypyKey);
            }

            return value;
        };
    }

}
