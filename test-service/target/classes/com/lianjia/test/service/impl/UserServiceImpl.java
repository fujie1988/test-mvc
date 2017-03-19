package com.lianjia.test.service.impl;

import com.google.common.collect.Maps;
import com.lianjia.test.datasource.TestDataRegion;
import com.lianjia.test.model.User;
import com.lianjia.test.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import com.lianjia.test.dao.impl.UserDao;

import java.util.List;
import java.util.Map;

/**
 * Created by helloworld on 2017/3/19.
 */

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    public boolean userLogin(String userName, String password){
        Map<String,Object> param =  Maps.newHashMap();
        param.put("name", "FUJIE");
        param.put("password", "123456");
        List<User> userList = userDao.queryListByCondition(TestDataRegion.getInstance(),param );
        if(userList.size()>0){
            return true;
        }
        return false;
    }

}
