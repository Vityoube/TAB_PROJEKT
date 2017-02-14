package org.sjk.controller;

import org.sjk.dao.IpDao;
import org.sjk.dao.UserDao;
import org.sjk.dto.User;
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

    @RequestMapping(method = RequestMethod.GET,value="/")
    public ModelAndView viewLoginForm(HttpServletRequest request){
        return  new ModelAndView("login_site");
    }

    @RequestMapping(method = RequestMethod.GET,value="login")
    public ModelAndView viewLoginPage(HttpServletRequest request,
                                      @RequestParam String name) throws UnknownHostException {
        String clientIpAddress=request.getHeader("X-FORWARDED-FOR");
        if (clientIpAddress==null)
            clientIpAddress=request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(clientIpAddress)||"127.0.0.1".equals(clientIpAddress))
            clientIpAddress= InetAddress.getLocalHost().getHostAddress();
       if (ipDao.findIP(clientIpAddress)) {
           ModelAndView user_login = new ModelAndView("user_login");
           ModelAndView ip_error = new ModelAndView("ip_error");
           String message;
           User user = new User();
           if (userDao.findUserByUsername(name) == true){
               message = "Hello " + name + "!";
               user_login.addObject("message", message);
               return user_login;
           }else{
               message = "U are not logged in!";
               ip_error.addObject("message", message);
               return ip_error;
           }

       }
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
            model.addAttribute("error", Errors.IP_NOT_FOUND);
            return new ModelAndView("user_login");

        } catch (UserOnlineException e) {
            e.printStackTrace();
            return new ModelAndView("user_login");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
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
