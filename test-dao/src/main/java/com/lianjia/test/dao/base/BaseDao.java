package com.lianjia.test.dao.base;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by helloworld on 2017/3/17.
 */
public class BaseDao<T> extends SqlSessionDaoSupport {

    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    protected SqlSession getBatchSqlSession(){
        SqlSession batchSqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        return batchSqlSession;
    }

    protected String obtainSQLID(String sqlId) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append(".").append(sqlId);
        return sb.toString();
    }

    /**
     * 插入
     *
     * @param entity
     *            条件参数
     * @return
     */
    public int insert(T entity) {
        return getSqlSession().insert(obtainSQLID("insert"), entity) ;
    }

    public void inserts(Collection<T> objs) {
        inserts(objs,true);
    }

    public void inserts(Collection<T> objs, boolean generatedId) {
        if (generatedId) {
            SqlSession batchSqlSession = getBatchSqlSession();
            for (T o : objs) {
                this.insert(o);
            }
            batchSqlSession.flushStatements();
        } else {
            getSqlSession().insert(obtainSQLID("insertBatch"), objs);
        }
    }

    /**
     * 更新:默认仅更新非空属性，除非属性名在参数includes中指定
     * @param entity
     * @param includes 需要设值为null的属性名
     * @return
     */
    public int update(T entity, String... includes) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("entity", entity);
        if (includes.length > 0) {
            List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
            for (String s : includes) {
                Map<String, String> field = new HashMap<String, String>();
                field.put("tname", propertyToField(s));
                field.put("name", s);
                maps.add(field);
            }
            param.put("includes", maps);
        }
        return getSqlSession().update(obtainSQLID("update"), param);
    }

    public void updates(Collection<T> list, String... includes) {
        SqlSession batchSqlSession=getBatchSqlSession();
        for (T o : list) {
            this.update(o,includes);
        }
        batchSqlSession.flushStatements();
    }


    private String propertyToField(String property) {
        if (null == property) {
            return "";
        }
        char[] chars = property.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            if (CharUtils.isAsciiAlphaUpper(c)) {
                sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 删除
     *
     * @param entity
     *            条件参数
     * @return
     */
    public int deleteByPk(Long pk) {
        return getSqlSession().delete(obtainSQLID("deleteByPk"), pk);
    }

    /**
     * 查询唯一对象。默认获取所有字段，除非属性名在参数includes中指定，则仅获取指定字段
     *
     * @param entity
     *            条件参数
     * @return
     */
    public T queryByPk(Long pk, String... includes) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pk", pk);
        if (includes.length > 0) {
            List<String> includeCols = new ArrayList<String>();
            for (String s : includes) {
                includeCols.add(propertyToField(s));
            }
            param.put("includes", includeCols);
        }
        T result = getSqlSession().selectOne(obtainSQLID("queryByPk"), param);
        return result;
    }

    /**
     * 查询数量
     *
     * @param condition
     *            条件参数
     * @return
     */
    public Integer queryCountByCondition(Map<String,Object> condition) {
        String sqlId = obtainSQLID("queryCountByCondition");
        Object result = getSqlSession().selectOne(sqlId, condition);
        Integer count;
        try {
            count = (null == result) ? 0 : (Integer) result;
        } catch (ClassCastException e) {
            logger.error("sql语句：" + sqlId + "查询结果非数字类型，无法用于查询数量");
            throw e;
        }
        return count;
    }

    /**
     * 查询集合
     *
     * @param condition
     *            条件参数
     * @return
     */
    public List<T> queryListByCondition(Map<String,Object> condition) {
        List<T> result = getSqlSession().selectList(obtainSQLID("queryListByCondition"), condition);
        return result;
    }

    /**
     * 分页查询
     *
     * @param condition
     *            条件参数
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<T> queryListByCondition(Map<String, Object> condition, int pageNo, int pageSize) {
        Pagination<T> pageBean = new Pagination<T>(pageNo, pageSize);
        List<T> list = getSqlSession().selectList(obtainSQLID("queryListByCondition"), condition,
                new RowBounds(pageBean.getStartIndex(), pageBean.getPageSize()));
        return list;
    }

    /**
     * 分页查询
     *
     * @param condition
     *            条件参数
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Pagination<T> queryPageByCondition(Map<String,Object> condition, int pageNo, int pageSize) {
        Pagination<T> pageBean = new Pagination<T>(pageNo, pageSize);

        List<T> list = getSqlSession().selectList(obtainSQLID("queryListByCondition"), condition,
                new RowBounds(pageBean.getStartIndex(), pageBean.getPageSize()));

        Integer totalCount = queryCountByCondition(condition);
        // 如果计算数量的sql为空，用list.size代表总数量，若sql非空要专门去查一次总数量
        totalCount = (totalCount == null) ? list.size() : totalCount;
        pageBean.setTotalCount(totalCount);
        pageBean.setList(list);
        return pageBean;
    }
}
