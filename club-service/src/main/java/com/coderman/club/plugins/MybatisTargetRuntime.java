package com.coderman.club.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

/**
 * @author coderman
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
        sb.append("DAO.xml");
        return sb.toString();
    }



    @Override
    protected void calculateModelAttributes() {

        String packkage = calculateJavaModelPackage();


        String attrName = mybatisNameChange(super.tableConfiguration.getTableName());
        StringBuilder sb = new StringBuilder();

        sb.append(packkage);
        sb.append(".");
        sb.append(attrName);
        sb.append("Key"); //$NON-NLS-1$
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(packkage);
        sb.append(".");
        sb.append(attrName);
        sb.append("Model"); //$NON-NLS-1$
        setBaseRecordType(sb.toString());


        sb.setLength(0);
        sb.append(packkage);
        sb.append('.');
        sb.append(attrName);
        sb.append("WithBLOBs"); //$NON-NLS-1$
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(packkage);
        sb.append('.');
        sb.append(attrName);
        sb.append("Example"); //$NON-NLS-1$
        setExampleType(sb.toString());

    }

    @Override
    protected void calculateJavaClientAttributes() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return;
        }

        String attrName = mybatisNameChange(super.tableConfiguration.getTableName());

        StringBuilder sb = new StringBuilder();
        sb.append(calculateJavaClientImplementationPackage());
        sb.append('.');
        sb.append(attrName);
        sb.append("DAOImpl"); //$NON-NLS-1$
        setDAOImplementationType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(attrName);
        sb.append("DAO"); //$NON-NLS-1$
        setDAOInterfaceType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(attrName);
        sb.append("DAO"); //$NON-NLS-1$
        setMyBatis3JavaMapperType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        sb.append(attrName);
        sb.append("SqlProvider"); //$NON-NLS-1$
        setMyBatis3SqlProviderType(sb.toString());
    }


}
