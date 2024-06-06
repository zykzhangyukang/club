package com.coderman.club.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author ：zhangyukang
 * @date ：2024/06/06 11:28
 */
@Slf4j
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    private final String from;

    private static class Holder {
        private static final EmailUtil INSTANCE = new EmailUtil();
    }

    private EmailUtil() {
        javaMailSender = SpringContextUtil.getBean(JavaMailSender.class);
        from = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.mail.username");
    }

    public static EmailUtil getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 发送邮箱
     * @param to 接受人
     * @param subject 标题
     * @param text 内容
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.format("%s<%s>", "章鱼社区",from));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}
