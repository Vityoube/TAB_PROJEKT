package org.sjk.controller;

import org.sjk.dao.IpDao;
import org.sjk.dao.UserDao;
import org.sjk.error.Errors;
import org.sjk.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by vkalashnykov on 12.02.17.
 */
@Controller
public class LoginPageController{
    @Autowired
    private UserDao userDao;
    @Autowired
    private IpDao ipDao;

    @RequestMapping(method = RequestMethod.GET,value="login")
    public ModelAndView viewLoginPage(Model model,HttpServletRequest request) throws UnknownHostException {
        String clientIpAddress=request.getHeader("X-FORWARDED-FOR");
        if (clientIpAddress==null)
            clientIpAddress=request.getRemoteAddr();
        if ("127.0.0.1".equals(clientIpAddress) || "0:0:0:0:0:0:0:1".equals(clientIpAddress))
            clientIpAddress= InetAddress.getLocalHost().getHostAddress();
       if (ipDao.findIP(clientIpAddress))
            return new ModelAndView("user_login");
       model.addAttribute("error",Errors.IP_NOT_FOUND.getErrorDescripton());
       return new ModelAndView("ip_error");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView loginAction(Model model, @RequestParam(name = "username",required=true)String username,
                                    @RequestParam(name="password",required = true)String password,
                                    @RequestParam(name="ip",required=true)String ip){
        try {
            long currrentUserId=userDao.loginUser(username,password,ip);
            ModelAndView modelAndView=new ModelAndView("action_page");
            modelAndView.addObject("userId",currrentUserId);
            return modelAndView;
        } catch (IpNotFoundException e) {
            e.printStackTrace();
            model.addAttribute("error", Errors.IP_NOT_FOUND.getErrorDescripton());
            return new ModelAndView("ip_error");

        } catch (UserOnlineException e) {
            e.printStackTrace();
            model.addAttribute("error", Errors.USER_ONLINE.getErrorDescripton());
            return new ModelAndView("user_login");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            model.addAttribute("error",Errors.USER_NOT_FOUND.getErrorDescripton());
            return new ModelAndView("user_login");
        } catch (BadCredentialException e) {
            e.printStackTrace();
            return new ModelAndView("user_login");
        } catch (UserBlockeException e) {
            e.printStackTrace();
            return new ModelAndView("user_login");
        }

    }


}
