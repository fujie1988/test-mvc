package com.lianjia.base.dao.ibatis.interceptor;

import com.lianjia.common.datasource.AccessType;
import com.lianjia.common.datasource.DataRegion;
import com.lianjia.common.datasource.DatasourceRoute;
import com.lianjia.common.datasource.mybatis.DataRegionSqlSession;
import com.lianjia.common.datasource.mybatis.DataRegionSqlSessionDaoSupport;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloworld on 2017/3/16.
 */
@Component("myBatisBaseDao")
public class MyBatisBaseDaoImpl<T, PK extends Serializable> extends DataRegionSqlSessionDaoSupport
        implements MyBatisBaseDao<T, PK>{

    private static final int BATCH_SIZE = 1000;
    private static Logger logger = Logger.getLogger(MyBatisBaseDaoImpl.class);

    public String INSERT = ".insert";
    public String INSERT_BATCH = ".insertBatch";
    public String UPDATE = ".update";
    public String UPDATE_BATCH = ".updateBatch";
    public String DELETE = ".delete";
    public String DELETE_BATCH = ".deleteBatch";
    public String GETBYID = ".getById";
    public String COUNT = ".findPage_count";
    public String PAGESELECT = ".findPage";

    @SuppressWarnings("unused")
    private SqlSessionFactory sqlSessionFactory;

    protected DataRegionSqlSession sqlSessionTemplate = null;

    @Autowired
    @Qualifier("sqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        super.setSqlSessionFactory(sqlSessionFactory);
        this.sqlSessionTemplate = getSqlSession();
    }

    private Object target;
    private Method invokingMethod;

    public int save(DataRegion region,T object) {
        if (object == null) {
            throw new IllegalArgumentException(" object can't null!");
        }
        return this.sqlSessionTemplate.insert(region,object.getClass().getName() + this.INSERT, object);
    }

    @DatasourceRoute(type=AccessType.SLAVE)
    public List<T> findByCondition(DataRegion region,T obj) {
        if (obj == null) {
            throw new IllegalArgumentException(" condition can't null!");
        }
        return this.sqlSessionTemplate.selectList(region,obj.getClass().getName() + this.PAGESELECT, obj);
    }

    @DatasourceRoute(type=AccessType.SLAVE)
    public T findByPK(DataRegion region,PK pk, Class<T> cls) {
        if (pk == null) {
            throw new IllegalArgumentException(" pk can't null!");
        }
        return (T) this.sqlSessionTemplate.selectOne(region,cls.getName() + this.GETBYID, pk);
    }

    public void update(DataRegion region,T object) {
        if (object == null) {
            throw new IllegalArgumentException(" object can't null!");
        }
        this.sqlSessionTemplate.update(region,object.getClass().getName() + this.UPDATE, object);
    }

    public List<BatchResult> updateBatch(DataRegion region,List<T> list) throws SQLException {
        return updateBatch(region,list, BATCH_SIZE);
    }

    public List<BatchResult> updateBatch(DataRegion region,List<T> list, Integer batchFlushSize) throws SQLException {
        if (list == null) {
            throw new IllegalArgumentException(" object can't null!");
        }
        SqlSession session = null;
        List<BatchResult> ret = new ArrayList<BatchResult>(list.size());
        try {
            session = sqlSessionFactory.openSession(ExecutorType.BATCH, true);
            int i = 0;
            for (T entity : list) {
                session.update(entity.getClass().getName() + this.UPDATE_BATCH, entity);
                if (i++ > 0 && i % batchFlushSize == 0) {
                    ret.addAll(session.flushStatements());
                }
            }
            ret = session.flushStatements();
        } finally {
            if (null != session) {
                session.clearCache();
                session.close();
            }
        }
        return ret;
    }

    public void delete(DataRegion region,PK pk, Class<T> cls) {
        if (pk == null) {
            throw new IllegalArgumentException(" pk can't null!");
        }
        this.sqlSessionTemplate.delete(region,cls.getName() + this.DELETE, pk);
    }

    @DatasourceRoute(type=AccessType.SLAVE)
    public Integer getTotalCount(DataRegion region,T object) {
        if (object == null) {
            throw new IllegalArgumentException(" condition can't null!");
        }
        Object obj = this.sqlSessionTemplate.selectOne(region,object.getClass().getName() + this.COUNT, object);
        if (obj != null) {
            return Integer.valueOf(Integer.parseInt(obj.toString()));
        }
        return Integer.valueOf(0);
    }

    public int insertBatch(DataRegion region,Class<T> cls, List<T> domainList) {
        return this.sqlSessionTemplate.insert(region,cls.getName() + this.INSERT_BATCH, domainList);
    }

    public int deleteBatch(DataRegion region,Class<T> cls, List<T> domainList) {
        return this.sqlSessionTemplate.delete(region,cls.getName() + this.DELETE_BATCH, domainList);
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getInvokingMethod() {
        return this.invokingMethod;
    }

    public void setInvokingMethod(Method invokingMethod) {
        this.invokingMethod = invokingMethod;
    }

    public int updateExp(DataRegion region,T paramT) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int deleteExp(DataRegion region,PK paramPK, Class<T> paramClass) {
        // TODO Auto-generated method stub
        return 0;
    }

}
