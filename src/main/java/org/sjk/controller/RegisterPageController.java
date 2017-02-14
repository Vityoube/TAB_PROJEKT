package org.sjk.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.sjk.dao.UserDao;
import org.sjk.dto.User;
import org.sjk.error.Errors;
import org.sjk.exception.PasswordExistsException;
import org.sjk.exception.UserExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by vkalashnykov on 13.02.17.
 */
@Controller
@Scope("session")
public class RegisterPageController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private User currentUser;

    @RequestMapping(method = RequestMethod.GET, value = "register")
    public ModelAndView registerPageView(HttpServletRequest request, Model model){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        if (currentUser==null)
            return new ModelAndView("register_page");
        return new ModelAndView("redirect:/actions");
    }

    @RequestMapping(method = RequestMethod.POST,value = "register")
    public ModelAndView registerUser(@RequestParam(name = "username",required = true) String username,
                                     @RequestParam(name="firstName",required = false)String firstName,
                                     @RequestParam(name="lastName",required = false)String lastName,
                                     @RequestParam(name="email",required = true)String email,
                                     @RequestParam(name="phone",required = false)String phone,
                                     @RequestParam(name="address",required = false)String address,
                                     Model model,HttpServletRequest request) throws UnknownHostException {
//        SecureRandom random=new SecureRandom();
        String inputError="";
        if (username==null || "".equals(username))
            inputError+=Errors.USERNAME_NOT_NULL.getErrorDescripton()+"\n";
        if (email==null || "".equals(email))
            inputError+=Errors.EMAIL_NOT_NULL.getErrorDescripton()+"\n";
        String generatedPassword= RandomStringUtils.random(8,true,true);
        User user=User.builder().userName(username).email(email).registrationStatus(User.RegistrationStatuses.PENDING)
                .userStatus(User.UserStatuses.USER).firstName(firstName).lastName(lastName).phone(phone)
                .address(address).build();
        String clientIpAddress=request.getHeader("X-FORWARDED-FOR");
        if (clientIpAddress==null)
            clientIpAddress=request.getRemoteAddr();
        if ("127.0.0.1".equals(clientIpAddress)  || "0:0:0:0:0:0:0:1".equals(clientIpAddress) )
            clientIpAddress= InetAddress.getLocalHost().getHostAddress();
        if ("".equals(inputError)){
            try {
                userDao.insertUser(user,generatedPassword,clientIpAddress);
            } catch (PasswordExistsException e) {
                model.addAttribute("registerError", Errors.PASSWORD_EXISTS.getErrorDescripton());
                return new ModelAndView("register_page");
            } catch (UserExistsException e) {
                model.addAttribute("registerError",Errors.USER_EXISTS.getErrorDescripton());
            }
            model.addAttribute("generatedPassword",generatedPassword);
            return new ModelAndView("register_page");
        } else {
            model.addAttribute("registerError",inputError);
            return new ModelAndView("register_page");
        }

    }




}
