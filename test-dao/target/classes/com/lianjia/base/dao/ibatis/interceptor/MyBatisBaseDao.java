package com.lianjia.base.dao.ibatis.interceptor;

import com.lianjia.common.datasource.DataRegion;
import org.apache.ibatis.executor.BatchResult;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by helloworld on 2017/3/16.
 */
public interface MyBatisBaseDao<T,PK extends Serializable> {

    public abstract int save(DataRegion region,T paramT);

    public abstract List<T> findByCondition(DataRegion region,T paramT);

    public abstract T findByPK(DataRegion region,PK paramPK, Class<T> paramClass);

    public abstract void update(DataRegion region,T paramT);

    public abstract int updateExp(DataRegion region,T paramT);

    public abstract void delete(DataRegion region,PK paramPK, Class<T> paramClass);

    public abstract int deleteExp(DataRegion region,PK paramPK, Class<T> paramClass);

    public abstract Integer getTotalCount(DataRegion region,T paramT);

    public abstract int insertBatch(DataRegion region,Class<T> cls, List<T> paramList);

    public abstract List<BatchResult> updateBatch(DataRegion region,List<T> domainList) throws SQLException;

    public abstract List<BatchResult> updateBatch(DataRegion region,List<T> paramList, Integer batchFlushSize) throws SQLException;

    public abstract int deleteBatch(DataRegion region,Class<T> cls, List<T> domainList);

}
