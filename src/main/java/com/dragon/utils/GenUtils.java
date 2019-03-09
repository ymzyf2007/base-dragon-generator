package com.dragon.utils;

import com.dragon.entity.Column;
import com.dragon.entity.Table;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器，工具类
 */
public class GenUtils {

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
            return propConfig;
        } catch(Exception e) {
            throw new BaseException("获取配置文件失败 ", e);
        }
    }

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();

        templates.add("template/Entity.java.vm");
        templates.add("template/Dao.java.vm");
        templates.add("template/Dao.xml.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Controller.java.vm");
        templates.add("template/menu.sql.vm");

        templates.add("template/index.vue.vm");
        templates.add("template/add-or-update.vue.vm");
        return templates;
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
        boolean hasBigDecimal = false;
        // 表信息
        Table tb = new Table();
        tb.setTableName(table.get("tableName"));
        tb.setComments(table.get("tableComment"));
        // 表名转换成Java类名
        String className = tableToJava(tb.getTableName(), config.getString("tablePrefix"));
        tb.setClassName(className);
        tb.setClassname(StringUtils.uncapitalize(className));
        // 列信息
        List<Column> columnsList = new ArrayList<>();
        for(Map<String, String> columnMap : columns) {
            Column column = new Column();
            column.setColumnName(columnMap.get("columnName"));
            column.setDataType(columnMap.get("dataType"));
            column.setComments(columnMap.get("columnComment"));
            column.setExtra(columnMap.get("extra"));

            // 列名转换成Java属性名
            String attrName = columnToJava(column.getColumnName());
            column.setAttrName(attrName);
            column.setAttrname(StringUtils.uncapitalize(attrName));
            // 列的数据类型，转换成Java类型
            String attrType = config.getString(column.getDataType(), "unknowType");
            column.setAttrType(attrType);
            if(!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            // 是否主键
            if("PRI".equalsIgnoreCase(columnMap.get("columnKey")) && tb.getPk() == null) {
                tb.setPk(column);
            }
            columnsList.add(column);
        }
        tb.setColumns(columnsList);

        // 没主键，则第一个字段为主键
        if(tb.getPk() == null) {
            tb.setPk(tb.getColumns().get(0));
        }
        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "com.dragon" : mainPath;
        // 封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tb.getTableName());
        map.put("comments", tb.getComments());
        map.put("pk", tb.getPk());
        map.put("className", tb.getClassName());
        map.put("classname", tb.getClassname());
        map.put("pathName", tb.getClassname().toLowerCase());
        map.put("columns", tb.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("mainPath", mainPath);
        map.put("package", config.getString("package"));
        map.put("moduleName", config.getString("moduleName"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        // 获取模板列表
        List<String> templates = getTemplates();
        for(String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, tb.getClassName(), config.getString("package"), config.getString("moduleName"))));
                IOUtils.write(sw.toString(), zip, "UTF-8" );
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (Exception e) {
                throw new BaseException("渲染模板失败，表名：" + tb.getTableName(), e);
            }
        }
    }

    /**
     * 表名转换成Java类名
     * @param tableName
     * @param tablePrefix
     * @return
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if(StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 列名转换成Java属性名
     * @param columnName
     * @return
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[] {'_'}).replace("_", "");
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }
        if (template.contains("Entity.java.vm" )) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }
        if (template.contains("Dao.java.vm" )) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }
        if (template.contains("Service.java.vm" )) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }
        if (template.contains("ServiceImpl.java.vm" )) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }
        if (template.contains("Controller.java.vm" )) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }
        if (template.contains("Dao.xml.vm" )) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + moduleName + File.separator + className + "Dao.xml";
        }
        if (template.contains("menu.sql.vm" )) {
            return className.toLowerCase() + "_menu.sql";
        }
        if (template.contains("index.vue.vm" )) {
            return "main" + File.separator + "resources" + File.separator + "src" + File.separator + "views" + File.separator + "modules" +
                    File.separator + moduleName + File.separator + className.toLowerCase() + ".vue";
        }
        if (template.contains("add-or-update.vue.vm" )) {
            return "main" + File.separator + "resources" + File.separator + "src" + File.separator + "views" + File.separator + "modules" +
                    File.separator + moduleName + File.separator + className.toLowerCase() + "-add-or-update.vue";
        }
        return null;
    }

}