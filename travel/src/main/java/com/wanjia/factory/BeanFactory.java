package com.wanjia.factory;

import java.io.InputStream;
import java.util.Properties;

public class BeanFactory {
    public static Object getBean(String id){
        Properties properties = new Properties();
        ClassLoader classLoader = BeanFactory.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream("ApplicationContext.properties");
        try {
            properties.load(is);
            String property = properties.getProperty(id);
            Class<?> aClass = Class.forName(property);
            return  aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
