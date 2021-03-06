package org.sjk.controller;

import org.sjk.dao.IpDao;
import org.sjk.dao.UserDao;
import org.sjk.dto.User;
import org.sjk.error.Errors;
import org.sjk.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@Scope("session")
public class LoginPageController{
    @Autowired
    private UserDao userDao;
    @Autowired
    private IpDao ipDao;
    private String clientIp;
    @Autowired
    private User currentUser;
    private long loginAttempts;

    @RequestMapping(method = RequestMethod.GET,value="login")
    public ModelAndView viewLoginPage(HttpServletRequest request, Model model) throws UnknownHostException {
        String clientIpAddress=request.getHeader("X-FORWARDED-FOR");
        if (clientIpAddress==null)
            clientIpAddress=request.getRemoteAddr();
        if ("127.0.0.1".equals(clientIpAddress)  || "0:0:0:0:0:0:0:1".equals(clientIpAddress) )
            clientIpAddress= InetAddress.getLocalHost().getHostAddress();
       if (ipDao.findIP(clientIpAddress)){
           if (currentUser!=null && userDao.isUserOnline(currentUser)) {
               return new ModelAndView("redirect:/actions");
           }
           return new ModelAndView("login_site");
       }

       return new ModelAndView("ip_error");
    }
    @RequestMapping(method = RequestMethod.POST,value = "login")
    public ModelAndView loginAction(Model model, @RequestParam(name = "username",required=true)String username,
                                    @RequestParam(name="password",required = true)String password,
                                    HttpServletRequest request) throws UnknownHostException {
        try {
            clientIp =request.getHeader("X-FORWARDED-FOR");
            if (clientIp==null)
                clientIp=request.getRemoteAddr();
            if ("127.0.0.1".equals(clientIp) || "0:0:0:0:0:0:0:1".equals(clientIp))
                clientIp=InetAddress.getLocalHost().getHostAddress();
            long currentUserId=userDao.loginUser(username,password,clientIp);
            loginAttempts=0;
            currentUser=userDao.findUserById(currentUserId);
            request.getSession().setAttribute("currentUser",currentUser);
            if (User.RegistrationStatuses.PENDING.equals(currentUser.getRegistrationStatus()))
                return new ModelAndView("redirect:/changePassword");
            ModelAndView modelAndView=new ModelAndView("redirect:/actions");
            return modelAndView;
        } catch (IpNotFoundException e) {
            model.addAttribute("error", Errors.IP_NOT_FOUND.getErrorDescription());
            return new ModelAndView("login_site");

        }  catch (UserNotFoundException e) {
            model.addAttribute("error", Errors.USER_NOT_FOUND.getErrorDescription());
            return new ModelAndView("login_site");
        } catch (BadCredentialException e) {
            clientIp =request.getHeader("X-FORWARDED-FOR");
            if (clientIp==null)
                clientIp=request.getRemoteAddr();
            if ("127.0.0.1".equals(clientIp) || "0:0:0:0:0:0:0:1".equals(clientIp))
                clientIp=InetAddress.getLocalHost().getHostAddress();
            model.addAttribute("error", Errors.BAD_CREDENTIALS.getErrorDescription());
            loginAttempts++;
            if (loginAttempts==3)
                userDao.blockUser(username,clientIp);
            return new ModelAndView("login_site");
        } catch (UserBlockedException e) {
            model.addAttribute("error", Errors.USER_BLOCKED.getErrorDescription());
            return new ModelAndView("login_site");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView start(HttpServletRequest request){
            currentUser=(User)request.getSession().getAttribute("currentUser");
            if (currentUser!=null && userDao.isUserOnline(currentUser))
                return new ModelAndView("redirect:/actions");
            return new ModelAndView("redirect:/login");
    }





}
