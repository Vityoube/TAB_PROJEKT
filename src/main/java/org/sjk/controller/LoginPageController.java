package org.sjk.controller;

import org.sjk.dao.IpDao;
import org.sjk.dao.UserDao;
import org.sjk.dto.User;
import org.sjk.error.Errors;
import org.sjk.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    private String clientIp;

    @RequestMapping(method = RequestMethod.GET,value="login")
    public ModelAndView viewLoginPage(HttpServletRequest request) throws UnknownHostException {
        String clientIpAddress=request.getHeader("X-FORWARDED-FOR");
        if (clientIpAddress==null)
            clientIpAddress=request.getRemoteAddr();
        if ("127.0.0.1".equals(clientIpAddress)|| "0:0:0:0:0:0:0:1".equals(clientIpAddress))
            clientIpAddress= InetAddress.getLocalHost().getHostAddress();
       if (ipDao.findIP(clientIpAddress))
            return new ModelAndView("login_site");
       return new ModelAndView("ip_error");
    }
    @RequestMapping(method = RequestMethod.POST,value = "login")
    public ModelAndView loginAction(Model model, @RequestParam(name = "username",required=true)String username,
                                    @RequestParam(name="password",required = true)String password,
                                    HttpServletRequest request,
                                    @ModelAttribute("user") User currentUser,
                                    RedirectAttributes redirectAttributes) throws UnknownHostException {
        try {
            clientIp =request.getHeader("X-FORWARDED-FOR");
            if (clientIp==null)
                clientIp=request.getRemoteAddr();
            if ("127.0.0.1".equals(clientIp)|| "0:0:0:0:0:0:0:1".equals(clientIp))
                clientIp=InetAddress.getLocalHost().getHostAddress();
            long currentUserId=userDao.loginUser(username,password,clientIp);
            currentUser=userDao.findUserById(currentUserId);
            setCurrentUser(currentUser);
            redirectAttributes.addFlashAttribute("user",currentUser);
            ModelAndView modelAndView=new ModelAndView("redirect:/actions");
            return modelAndView;
        } catch (IpNotFoundException e) {
            e.printStackTrace();
            model.addAttribute("error", Errors.IP_NOT_FOUND.getErrorDescripton());
            return new ModelAndView("login_site");

        }  catch (UserNotFoundException e) {
            e.printStackTrace();
            return new ModelAndView("login_site");
        } catch (BadCredentialException e) {
            e.printStackTrace();
            return new ModelAndView("login_site");
        } catch (UserBlockedException e) {
            e.printStackTrace();
            return new ModelAndView("login_site");
        }
    }

    @ModelAttribute("user")
    public User setCurrentUser(User currentUser){
        return currentUser;
    }



}
