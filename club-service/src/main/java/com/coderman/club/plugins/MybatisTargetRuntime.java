package com.coderman.club.plugins;

import org.apache.commons.lang3.StringUtils;
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

    private String mybatisNameChange(String sourceStr) {

        if (StringUtils.isBlank(sourceStr)) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        String[] array = sourceStr.trim().toLowerCase().split("_");
        boolean flag = false;

        for (String str : array) {

            if (StringUtils.isBlank(str)) {
                continue;
            }

            if (!flag && array.length > 1) {
                flag = true;
                continue;
            }
            sb.append(StringUtils.capitalize(str));
        }
        return sb.toString();
    }

    /**
     * 生成xml名称
     * @return
     */
    @Override
    protected String calculateMyBatis3XmlMapperFileName() {
        StringBuilder sb = new StringBuilder();
        String attrName = mybatisNameChange(super.tableConfiguration.getTableName());
        sb.append(attrName);
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

        String tableName = mybatisNameChange(super.tableConfiguration.getTableName());

        StringBuilder sb = new StringBuilder();

        sb.append(packkage);
        sb.append(".");
        sb.append(tableName);
        sb.append("Key");
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(packkage);
        sb.append(".");
        sb.append(tableName);
        sb.append("Model"); //$NON-NLS-1$
        setBaseRecordType(sb.toString());


        sb.setLength(0);
        sb.append(packkage);
        sb.append('.');
        sb.append(tableName);
        sb.append("WithBLOBs");
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(packkage);
        sb.append('.');
        sb.append(tableName);
        sb.append("Example");
        setExampleType(sb.toString());

    }


    @Override
    protected void calculateJavaClientAttributes() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return;
        }

        String tableName = mybatisNameChange(super.tableConfiguration.getTableName());

        StringBuilder sb = new StringBuilder();
        sb.append(calculateJavaClientImplementationPackage());
        sb.append('.');
        sb.append(tableName);
        sb.append("MapperImpl");
        setDAOImplementationType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(tableName);
        sb.append("Mapper");
        setDAOInterfaceType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(tableName);
        sb.append("Mapper");
        setMyBatis3JavaMapperType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(tableName);
        sb.append("SqlProvider");
        setMyBatis3SqlProviderType(sb.toString());
    }


}

