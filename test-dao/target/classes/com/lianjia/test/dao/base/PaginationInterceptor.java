package com.lianjia.test.dao.base;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by helloworld on 2017/3/17.
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PaginationInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(PaginationInterceptor.class);

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    protected static final String SQL_END_DELIMITER = ";";

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");

        //若不分页，直接退出分页拦截器
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return invocation.proceed();
        }

        //获得原始SQL语句
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        //使用自定义的物理分页方法
        originalSql = getLimitString(originalSql, rowBounds.getOffset(), rowBounds.getLimit());
        //使用自定义的物理分页时，不再需要mybatis进行额外的操作了，所以把rowBounds的偏移量恢复为初始值，否则offset和limit将会把结果集再次截取
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        metaStatementHandler.setValue("delegate.boundSql.sql", originalSql);

        if (logger.isDebugEnabled()) {
            BoundSql boundSql = statementHandler.getBoundSql();
            logger.debug("生成分页SQL : " + boundSql.getSql());
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }


    /**
     * 获得包含分页条件的语句
     *
     * @param sql    原始sql语句
     * @param offset 分页起始
     * @param limit  分页限制
     * @return
     */
    private String getLimitString(String sql, int offset, int limit) {
        sql = trim(sql);
        StringBuffer sb = new StringBuffer(sql.length() + 20);
        sb.append(sql);
        if (offset > 0) {
            sb.append(" limit ").append(offset).append(',').append(limit).append(SQL_END_DELIMITER);
        } else {
            sb.append(" limit ").append(limit).append(SQL_END_DELIMITER);
        }
        return sb.toString();
    }


    @SuppressWarnings("unused")
    private String getOrderString(String originalSql, LinkedHashMap<String, String> orderItems) {
        String ex = "";
        if (orderItems != null && orderItems.size() > 0) {
            Iterator<Map.Entry<String, String>> it = orderItems.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                ex = ex + "," + entry.getKey() + " " + entry.getValue();
            }
        }

        if (ex.trim().length() > 0) {
            if (ex.startsWith(",")) {
                ex = ex.substring(1);
            }
            return originalSql + " order by " + ex;
        }

        return originalSql;
    }

    /**
     * 去除SQL语句结束符";"
     *
     * @param sql SQL语句
     * @return
     */
    private String trim(String sql) {
        sql = sql.trim();
        if (sql.endsWith(SQL_END_DELIMITER)) {
            sql = sql.substring(0, sql.length() - 1 - SQL_END_DELIMITER.length());
        }
        return sql;
    }
}
