package com.lianjia.test.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;

/**
 * Created by helloworld on 2017/3/16.
 */
public class XmlWebApplicationContext extends org.springframework.web.context.support.XmlWebApplicationContext {
    public void setBean(String beanName, Object obj) {
        ConfigurableListableBeanFactory configurableListableBeanFactory = getBeanFactory();

        configurableListableBeanFactory.registerSingleton(beanName, obj);

        if (((obj instanceof DisposableBean)) && ((configurableListableBeanFactory instanceof SingletonBeanRegistry)))
            ((DefaultSingletonBeanRegistry) configurableListableBeanFactory).registerDisposableBean(beanName,
                    (DisposableBean) obj);
    }
}
