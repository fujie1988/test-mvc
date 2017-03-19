package com.lianjia.test.controller;

import com.lianjia.test.service.UserService;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by helloworld on 2017/3/16.
 */

@Controller
@RequestMapping("/user")
public class BaseController {

    final private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Object userLogin(HttpServletRequest request) {
        if (userService.userLogin(request.getParameter("name"), request.getParameter("password"))) {
            return "1";
        }else{
            return "0";
        }
    }
}
