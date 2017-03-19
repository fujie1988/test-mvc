package com.lianjia.test.dao.base;

import com.lianjia.common.datasource.DataRegion;
import com.lianjia.common.datasource.mybatis.DataRegionSqlSessionDaoSupport;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by helloworld on 2017/3/17.
 */
public class DataRegionDao<T> extends DataRegionSqlSessionDaoSupport {
    @SuppressWarnings("unused")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    @Qualifier("sqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        super.setSqlSessionFactory(sqlSessionFactory);
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
    public int insert(DataRegion region, T entity) {
        return getSqlSession().insert(region,obtainSQLID("insert"), entity) ;
    }

    public void inserts(DataRegion region, Collection<T> objs) {
        for(T entity:objs){
            insert(region,entity);
        }
    }

    public void insertBatch(DataRegion region, Collection<T> objs) {
        if (objs != null && objs.size() > 0) {
            getSqlSession().insert(region, obtainSQLID("insertBatch"), objs);
        }
    }

    /**
     * 更新:默认仅更新非空属性，除非属性名在参数includes中指定
     * @param entity
     * @param includes 需要设值为null的属性名
     * @return
     */
    public int update(DataRegion region, T entity, String... includes) {
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
        return getSqlSession().update(region,obtainSQLID("update"), param);
    }

    public void updates(DataRegion region, Collection<T> list, String... includes) {
        for (T o : list) {
            this.update(region,o,includes);
        }
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
    public int deleteByPk(DataRegion region, Long pk) {
        return getSqlSession().delete(region,obtainSQLID("deleteByPk"), pk);
    }

    /**
     * 查询唯一对象。默认获取所有字段，除非属性名在参数includes中指定，则仅获取指定字段
     *
     * @param entity
     *            条件参数
     * @return
     */
    public T queryByPk(DataRegion region, Long pk, String... includes) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pk", pk);
        if (includes.length > 0) {
            List<String> includeCols = new ArrayList<String>();
            for (String s : includes) {
                includeCols.add(propertyToField(s));
            }
            param.put("includes", includeCols);
        }
        T result = getSqlSession().selectOne(region,obtainSQLID("queryByPk"), param);
        return result;
    }

    /**
     * 查询数量
     *
     * @param condition
     *            条件参数
     * @return
     */
    public Integer queryCountByCondition(DataRegion region, Map<String,Object> condition) {
        String sqlId = obtainSQLID("queryCountByCondition");
        Object result = getSqlSession().selectOne(region,sqlId, condition);
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
    public List<T> queryListByCondition(DataRegion region, Map<String,Object> condition) {
        List<T> result = getSqlSession().selectList(region,obtainSQLID("queryListByCondition"), condition);
        return result;
    }

    /**
     * 查询集合
     *
     * @param condition
     *            条件参数
     * @return
     */
    public List<T> queryListByCondition(DataRegion region, Map<String,Object> condition, String udfQueryName) {
        List<T> result = getSqlSession().selectList(region,obtainSQLID(udfQueryName), condition);
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
    public List<T> queryListByCondition(DataRegion region, Map<String, Object> condition, int pageNo, int pageSize) {
        Pagination<T> pageBean = new Pagination<T>(pageNo, pageSize);
        List<T> list = getSqlSession().selectList(region,obtainSQLID("queryListByCondition"), condition,
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
    public Pagination<T> queryPageByCondition(DataRegion region, Map<String,Object> condition, int pageNo, int pageSize) {
        Pagination<T> pageBean = new Pagination<T>(pageNo, pageSize);

        List<T> list = getSqlSession().selectList(region,obtainSQLID("queryListByCondition"), condition,
                new RowBounds(pageBean.getStartIndex(), pageBean.getPageSize()));

        Integer totalCount = queryCountByCondition(region,condition);
        // 如果计算数量的sql为空，用list.size代表总数量，若sql非空要专门去查一次总数量
        totalCount = (totalCount == null) ? list.size() : totalCount;
        pageBean.setTotalCount(totalCount);
        pageBean.setList(list);
        return pageBean;
    }



    /**
     * 分页查询，，使用用户定义的query查询，
     * @param region
     * @param condition
     * @param pageNo
     * @param pageSize
     * @param udfQueryStr
     * @param udfQueryCountStr 为空时默认 使用【udfQueryStr+"count"】找不到会报错。。。
     * @return
     */

    public Pagination<T> queryPageUdfByCondition(DataRegion region, Map<String,Object> condition, int pageNo, int pageSize, String udfQueryStr, String udfQueryCountStr) {
        Pagination<T> pageBean = new Pagination<T>(pageNo, pageSize);

        List<T> list = getSqlSession().selectList(region,obtainSQLID(udfQueryStr), condition,
                new RowBounds(pageBean.getStartIndex(), pageBean.getPageSize()));
        Integer totalCount=null;
        if(udfQueryCountStr==null){
            totalCount = queryUdfCountByCondition(region,condition,udfQueryStr+"count");
        }else{
            totalCount = queryUdfCountByCondition(region,condition,udfQueryCountStr);
        }
        // 如果计算数量的sql为空，用list.size代表总数量，若sql非空要专门去查一次总数量
        totalCount = (totalCount == null) ? list.size() : totalCount;
        pageBean.setTotalCount(totalCount);
        pageBean.setList(list);
        return pageBean;
    }

    public Integer queryUdfCountByCondition(DataRegion region, Map<String, Object> condition, String udfQueryStr) {
        String sqlId = obtainSQLID(udfQueryStr);
        Object result = getSqlSession().selectOne(region,sqlId, condition);
        Integer count;
        try {
            count = (null == result) ? 0 : (Integer) result;
        } catch (ClassCastException e) {
            logger.error("sql语句：" + sqlId + "查询结果非数字类型，无法用于查询数量");
            throw e;
        }
        return count;
    }
}
