package com.lianjia.test.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public synchronized void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    public static Object getBean(String beanName)
            throws BeansException
    {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType)
            throws BeansException
    {
        return applicationContext.getBean(beanName, requiredType);
    }

    public static boolean containsBean(String beanName)
    {
        return applicationContext.containsBean(beanName);
    }

    public static boolean isSingleton(String beanName)
            throws NoSuchBeanDefinitionException
    {
        return applicationContext.isSingleton(beanName);
    }

    public static Class getType(String beanName)
            throws NoSuchBeanDefinitionException
    {
        return applicationContext.getType(beanName);
    }

    public static String[] getAliases(String beanName)
            throws NoSuchBeanDefinitionException
    {
        return applicationContext.getAliases(beanName);
    }

    public static void setBean(String beanName, Object obj)
    {
        if ((applicationContext instanceof XmlWebApplicationContext))
            ((XmlWebApplicationContext)applicationContext).setBean(beanName, obj);
        else if ((applicationContext instanceof ClassPathXmlApplicationContext))
            ((ClassPathXmlApplicationContext)applicationContext).setBean(beanName, obj);
    }

}
