package com.lianjia.test.dao.base;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by helloworld on 2017/3/17.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class DatabaseInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInterceptor.class);

    protected static final String SQL_END_DELIMITER = ";";

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        //获得原始SQL语句
        BoundSql boundSql= statementHandler.getBoundSql();
        String originalSql = (String) boundSql.getSql();

        if(originalSql.contains("{curDatabase}")){
            //session get
            originalSql=originalSql.replace("{curDatabase}", "customer");
            Field field=boundSql.getClass().getDeclaredField("sql");
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field,boundSql, originalSql);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("生成SQL : " + statementHandler.getBoundSql().getSql());
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }

}