package com.lianjia.test.dao;

import com.lianjia.test.datasource.TestDataRegion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.lianjia.test.dao.impl.*;
import com.lianjia.test.model.*;
import javax.annotation.Resource;

/**
 * Created by helloworld on 2017/3/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-dao.xml"})
public class DaoTest {

    @Resource
    private UserDao userDao;

    @Test
    public void test1() {
        User user = new User();
        user.setUserId(1);
        user.setPassword("123456");
        user.setUserName("FUJIE");
        userDao.insert(TestDataRegion.getInstance(), user );
    }
}
