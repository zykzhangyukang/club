package com.coderman.club.config;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * 配置事务管理器
 *
 * @author ：zhangyukang
 * @date ：2023/11/20 11:40
 */
@Aspect
@Configuration
@Slf4j
public class TransactionConfig {


    @Resource
    private DataSource dataSource;


    @Bean(value = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean("txAdvice")
    public TransactionInterceptor transactionInterceptor(@Qualifier(value = "transactionManager") PlatformTransactionManager transactionManager) {
        NameMatchTransactionAttributeSource transactionAttributeSource = new NameMatchTransactionAttributeSource();
        List<RollbackRuleAttribute> rollbackRuleAttributeList = Collections.singletonList(new RollbackRuleAttribute(Throwable.class));


        // 使用事务
        RuleBasedTransactionAttribute requiredLongTx = new RuleBasedTransactionAttribute();
        requiredLongTx.setTimeout(30);
        requiredLongTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredLongTx.setRollbackRules(rollbackRuleAttributeList);
        transactionAttributeSource.addTransactionalMethod("save", requiredLongTx);

        // 使用事务
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setTimeout(15);
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTx.setRollbackRules(rollbackRuleAttributeList);

        transactionAttributeSource.addTransactionalMethod("create*",requiredTx);
        transactionAttributeSource.addTransactionalMethod("insert*", requiredTx);
        transactionAttributeSource.addTransactionalMethod("update*", requiredTx);
        transactionAttributeSource.addTransactionalMethod("modify*", requiredTx);
        transactionAttributeSource.addTransactionalMethod("delete*", requiredTx);
        transactionAttributeSource.addTransactionalMethod("remove*", requiredTx);

        // 使用事务
        RuleBasedTransactionAttribute requiredNewTx = new RuleBasedTransactionAttribute();
        requiredNewTx.setTimeout(15);
        requiredNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        requiredNewTx.setRollbackRules(rollbackRuleAttributeList);

        transactionAttributeSource.addTransactionalMethod("noTran*", requiredNewTx);

        // 使用事务
        RuleBasedTransactionAttribute requiredTimerTx = new RuleBasedTransactionAttribute();
        requiredTimerTx.setTimeout(50);
        requiredTimerTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTimerTx.setRollbackRules(rollbackRuleAttributeList);
        transactionAttributeSource.addTransactionalMethod("*Timer", requiredTimerTx);

        // 使用事务
        RuleBasedTransactionAttribute requiredLongTimerTx = new RuleBasedTransactionAttribute();
        requiredLongTimerTx.setTimeout(150);
        requiredLongTimerTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredLongTimerTx.setRollbackRules(rollbackRuleAttributeList);
        transactionAttributeSource.addTransactionalMethod("*LongTimer", requiredLongTimerTx);

        // 只读事务
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        transactionAttributeSource.addTransactionalMethod("*", readOnlyTx);

        log.info("自定义事务拦截器创建");
        return new TransactionInterceptor(transactionManager, transactionAttributeSource);
    }


    @Bean
    public Advisor advisor(@Qualifier(value = "txAdvice") Advice advice) {

        AspectJExpressionPointcut expressionPointcut = new AspectJExpressionPointcut();
        expressionPointcut.setExpression("execution(* com.coderman..service..*(..))");
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(expressionPointcut, advice);
        defaultPointcutAdvisor.setOrder(5);

        return defaultPointcutAdvisor;
    }
}
