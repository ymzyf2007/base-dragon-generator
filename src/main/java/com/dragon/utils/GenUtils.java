package com.dragon.utils;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器，工具类
 */
public class GenUtils {

    public static List<String> getTemplates() {

    }

    /**
     * 获取配置信息
     * @return
     */
    public static Configuration getConfig() {
        try {
            Configurations configs = new Configurations();
            // setDefaultEncoding是个静态方法,用于设置指定类型(class)所有对象的编码方式。
            // 本例中是PropertiesConfiguration,要在PropertiesConfiguration实例创建之前调用。
            FileBasedConfigurationBuilder.setDefaultEncoding(PropertiesConfiguration.class, "UTF-8");
            PropertiesConfiguration propConfig = configs.properties(GenUtils.class.getClassLoader().getResource("generator.properties"));

        } catch(Exception e) {
            throw new
        }
    }


    /**
     * 生成代码
     * @param table
     * @param columns
     * @param zip
     */
    public static void generatorCode(Map<String, String> table, List<Map<String, String>> columns, ZipOutputStream zip) {
        // 配置信息
        Configuration config = getConfig();
    }

}
