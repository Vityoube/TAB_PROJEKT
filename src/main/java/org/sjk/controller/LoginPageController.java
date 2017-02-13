package org.sjk.controller;

import org.sjk.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by vkalashnykov on 12.02.17.
 */
@Controller
public class LoginPageController{
    @Autowired
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.GET,value="login")
    public String viewLoginPage(){
       return "user_login";
    }
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView loginAction(){
        return new ModelAndView("login");
    }


}
