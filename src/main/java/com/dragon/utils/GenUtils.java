package com.dragon.utils;

import com.dragon.entity.Table;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

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
            return propConfig;
        } catch(Exception e) {
            throw new BaseException("获取配置文件失败 ", e);
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
        boolean hasBigDecimal = false;
        // 表信息
        Table tb = new Table();
        tb.setTableName(table.get("tableName"));
        tb.setComments(table.get("tableComment"));
        // 表名转换成Java类名
        String className = tableToJava(tb.getTableName(), config.getString("tablePrefix"));
        tb.setClassName(className);
        tb.setClassname(StringUtils.uncapitalize(className));

    }











//        tableEntity.setClassName(className);
//        tableEntity.setClassname(StringUtils.uncapitalize(className));
//
//        //列信息
//        List<ColumnEntity> columsList = new ArrayList<>();
//        for(Map<String, String> column : columns){
//            ColumnEntity columnEntity = new ColumnEntity();
//            columnEntity.setColumnName(column.get("columnName" ));
//            columnEntity.setDataType(column.get("dataType" ));
//            columnEntity.setComments(column.get("columnComment" ));
//            columnEntity.setExtra(column.get("extra" ));
//
//            //列名转换成Java属性名
//            String attrName = columnToJava(columnEntity.getColumnName());
//            columnEntity.setAttrName(attrName);
//            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));
//
//            //列的数据类型，转换成Java类型
//            String attrType = config.getString(columnEntity.getDataType(), "unknowType" );
//            columnEntity.setAttrType(attrType);
//            if (!hasBigDecimal && attrType.equals("BigDecimal" )) {
//                hasBigDecimal = true;
//            }
//            //是否主键
//            if ("PRI".equalsIgnoreCase(column.get("columnKey" )) && tableEntity.getPk() == null) {
//                tableEntity.setPk(columnEntity);
//            }
//
//            columsList.add(columnEntity);
//        }
//        tableEntity.setColumns(columsList);
//
//        //没主键，则第一个字段为主键
//        if (tableEntity.getPk() == null) {
//            tableEntity.setPk(tableEntity.getColumns().get(0));
//        }
//
//        //设置velocity资源加载器
//        Properties prop = new Properties();
//        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
//        Velocity.init(prop);
//        String mainPath = config.getString("mainPath" );
//        mainPath = StringUtils.isBlank(mainPath) ? "io.renren" : mainPath;
//        //封装模板数据
//        Map<String, Object> map = new HashMap<>();
//        map.put("tableName", tableEntity.getTableName());
//        map.put("comments", tableEntity.getComments());
//        map.put("pk", tableEntity.getPk());
//        map.put("className", tableEntity.getClassName());
//        map.put("classname", tableEntity.getClassname());
//        map.put("pathName", tableEntity.getClassname().toLowerCase());
//        map.put("columns", tableEntity.getColumns());
//        map.put("hasBigDecimal", hasBigDecimal);
//        map.put("mainPath", mainPath);
//        map.put("package", config.getString("package" ));
//        map.put("moduleName", config.getString("moduleName" ));
//        map.put("author", config.getString("author" ));
//        map.put("email", config.getString("email" ));
//        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
//        VelocityContext context = new VelocityContext(map);
//
//        //获取模板列表
//        List<String> templates = getTemplates();
//        for (String template : templates) {
//            //渲染模板
//            StringWriter sw = new StringWriter();
//            Template tpl = Velocity.getTemplate(template, "UTF-8" );
//            tpl.merge(context, sw);
//
//            try {
//                //添加到zip
//                zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), config.getString("package" ), config.getString("moduleName" ))));
//                IOUtils.write(sw.toString(), zip, "UTF-8" );
//                IOUtils.closeQuietly(sw);
//                zip.closeEntry();
//            } catch (IOException e) {
//                throw new RRException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
//            }
//        }


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







}