package com.coderman.club.plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author coderman
 * @Title: 修复mybatis生成mapper.xml时会重复的问题
 * @Description: TOD
 * @date 2022/6/310:42
 */
public class UnmergeableXmlMappersPlugin extends PluginAdapter {

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            Field isMergeable = GeneratedXmlFile.class.getDeclaredField("isMergeable");
            isMergeable.setAccessible(true);
            isMergeable.setBoolean(sqlMap, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
