package org.sjk.controller;

import org.sjk.dao.PasswordDao;
import org.sjk.dao.UserDao;
import org.sjk.dto.User;
import org.sjk.error.Errors;
import org.sjk.exception.PasswordNotFoundException;
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
 * Created by vkalashnykov on 13.02.17.
 */
@Controller
@Scope("session")
public class PasswordChangePageController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordDao passwordDao;
    @Autowired
    private User currentUser;

    @RequestMapping(method = RequestMethod.GET, value="changePassword")
    public ModelAndView passwordChangePageView(HttpServletRequest request){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        if (currentUser!=null)
            return new ModelAndView("password_change");
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(method = RequestMethod.POST,value = "changePassword")
    public ModelAndView passwordChangeAction(@RequestParam(name = "oldPassword") String oldPassword,
                                             @RequestParam(name="newPassword") String newPassword,
                                             @RequestParam(name = "newPasswordConfirm")String newPasswordConfirm,
                                             HttpServletRequest request,
                                             Model model) throws UnknownHostException {
        String oldPasswordError="";
        if("".equals(oldPassword))
            oldPasswordError=Errors.PASSWORD_NOT_NULL.getErrorDescription();
        String newPasswordError="";
        if (newPassword.length()<8)
            newPasswordError= Errors.PASSWORD_TOO_SHORT.getErrorDescription();
        else if (newPassword.length()>20)
            newPasswordError= Errors.PASSWORD_TOO_LONG.getErrorDescription();
        String passwordConfirmError="";
        if (!newPassword.equals(newPasswordConfirm))
            passwordConfirmError=Errors.PASSWORD_NOT_SAME.getErrorDescription();
        if ("".equals(oldPasswordError) && "".equals(newPasswordError) && "".equals(passwordConfirmError)){
            currentUser=(User)request.getSession().getAttribute("currentUser");
            if (currentUser!=null){
                String clientIp =request.getHeader("X-FORWARDED-FOR");
                if (clientIp==null)
                    clientIp=request.getRemoteAddr();
                if ("127.0.0.1".equals(clientIp) || "0:0:0:0:0:0:0:1".equals(clientIp))
                    clientIp= InetAddress.getLocalHost().getHostAddress();
                try {
                    userDao.changePasswordForUser(currentUser.getUserName(),oldPassword,newPassword,clientIp);
                    if (User.RegistrationStatuses.PENDING.equals(currentUser.getRegistrationStatus())) {
                        userDao.registerUser(currentUser.getUserName(), newPassword, clientIp);
                        currentUser=userDao.findUserById(currentUser.getId());
                        request.getSession().setAttribute("currentUser",currentUser);
                        model.addAttribute("changePasswordSuccess",true);
                    }
                    return new ModelAndView("redirect:/actions");
                } catch (PasswordNotFoundException e) {
                    oldPasswordError=Errors.WRONG_PASSWORD.getErrorDescription();
                    model.addAttribute("oldPasswordError",oldPasswordError);
                    return new ModelAndView("password_change");
                }
            }
            return new ModelAndView("redirect:/login");
        }else {
            model.addAttribute("oldPasswordError",oldPasswordError);
            model.addAttribute("newPasswordError",newPasswordError);
            model.addAttribute("passwordConfirmError",passwordConfirmError);
            return new ModelAndView("password_change");
        }

    }
}
