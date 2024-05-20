package com.coderman.club.plugins;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * 设置自定义的类型转换器
 * @author zhangyukang
 */
public class TypeResolverDefaultImpl extends JavaTypeResolverDefaultImpl {

    public TypeResolverDefaultImpl() {
        super();

        //把数据库的 TINYINT 映射成 Integer
        super.typeMap.put(Types.TINYINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
    }

}
