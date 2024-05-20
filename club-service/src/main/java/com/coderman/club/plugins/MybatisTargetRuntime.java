package com.coderman.club.plugins;

import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;

import java.util.List;

/**
 * @author coderman
 * @Title: 自定mybatis生成dao, mapper
 * @Description: TOD
 * @date 2022/5/2323:15
 */
/**
 * @author zhangyukang
 * @Title: 自定mybatis生成dao, mapper
 * @Description: TOD
 * @date 2022/5/2323:15
 */
public class MybatisTargetRuntime extends IntrospectedTableMyBatis3Impl {

    /**
     * 生成xml名称
     * @return
     */
    @Override
    protected String calculateMyBatis3XmlMapperFileName() {
        StringBuilder sb = new StringBuilder();
        String domainObjectName = fullyQualifiedTable.getDomainObjectName();
        sb.append(domainObjectName);
        sb.append("Mapper.xml");
        return sb.toString();
    }

    /**
     * 自定义Model生成器
     * 1. 不生成ExampleClass
     * @param warnings
     * @param progressCallback
     */
    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {

        if (getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        if (getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
    }

    @Override
    protected void calculateModelAttributes() {

        String packkage = calculateJavaModelPackage();

        String domainObjectName = fullyQualifiedTable.getDomainObjectName();

        StringBuilder sb = new StringBuilder();


        sb.append(packkage);
        sb.append(".");
        sb.append(domainObjectName);
        sb.append("Key");
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(packkage);
        sb.append(".");
        sb.append(domainObjectName);
        setBaseRecordType(sb.toString());


        sb.setLength(0);
        sb.append(packkage);
        sb.append('.');
        sb.append(domainObjectName);
        sb.append("WithBLOBs");
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(packkage);
        sb.append('.');
        sb.append(domainObjectName);
        sb.append("Example");
        setExampleType(sb.toString());

    }

    @Override
    protected void calculateJavaClientAttributes() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return;
        }

        String domainObjectName = fullyQualifiedTable.getDomainObjectName();

        StringBuilder sb = new StringBuilder();
        sb.append(calculateJavaClientImplementationPackage());
        sb.append('.');
        sb.append(domainObjectName);
        sb.append("MapperImpl");
        setDAOImplementationType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(domainObjectName);
        sb.append("Mapper");
        setDAOInterfaceType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(domainObjectName);
        sb.append("Mapper");
        setMyBatis3JavaMapperType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(domainObjectName);
        sb.append("SqlProvider");
        setMyBatis3SqlProviderType(sb.toString());
    }


}

