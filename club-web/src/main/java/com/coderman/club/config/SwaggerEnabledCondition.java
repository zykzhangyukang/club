package com.coderman.club.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

/**
 * 判断是否启用swagger
 * @author ：zhangyukang
 * @date ：2023/09/19 14:27
 */
public class SwaggerEnabledCondition implements Condition {

    @Override
    public boolean matches(@NonNull ConditionContext conditionContext,@NonNull  AnnotatedTypeMetadata annotatedTypeMetadata) {

        Environment env = conditionContext.getEnvironment();
        boolean isProfileActive = env.acceptsProfiles(Profiles.of("dev","fat","uat"));
        boolean isSwaggerEnabled = env.getProperty("swagger.enabled", Boolean.class, false);

        return isProfileActive || isSwaggerEnabled;
    }
}
