package org.sjk.controller;

import org.sjk.dao.ActionDao;
import org.sjk.dao.UserDao;
import org.sjk.dto.Action;
import org.sjk.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkalashnykov on 13.02.17.
 */
@Controller
@Scope("session")
public class ActionsPageController {
    @Autowired
    private ActionDao actionDao;
    @Autowired
    private User currentUser;
    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "actions",method = RequestMethod.GET)
    public ModelAndView actionsPage(Model model, HttpServletRequest request){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        if (currentUser!=null){
            List<Action> userActions=actionDao.findAllActions(currentUser.getId());
            model.addAttribute("actions",userActions);
            return new ModelAndView("actions_page");
        }
        return new ModelAndView("redirect:/login");

    }
    @RequestMapping(value="actionsRemove",method = RequestMethod.POST)
    public ModelAndView removeActions(Model model, HttpServletRequest request){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        if (currentUser!=null){
            actionDao.removeActionsForUser(currentUser.getId());
            List<Action> userActions=actionDao.findAllActions(currentUser.getId());
            model.addAttribute("actions",userActions);
            model.addAttribute("removeActionsSuccess",true);
            return new ModelAndView("actions_page");
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "logout",method = RequestMethod.POST)
    public ModelAndView logoutAction(HttpServletRequest request) throws UnknownHostException {
        currentUser=(User)request.getSession().getAttribute("currentUser");
        if (currentUser!=null){
            String clientIpAddress=request.getHeader("X-FORWARDED-FOR");
            if (clientIpAddress==null)
                clientIpAddress=request.getRemoteAddr();
            if ("127.0.0.1".equals(clientIpAddress)  || "0:0:0:0:0:0:0:1".equals(clientIpAddress) )
                clientIpAddress= InetAddress.getLocalHost().getHostAddress();
            userDao.logoutUser(currentUser.getUserName(),clientIpAddress);
            currentUser=null;
            request.getSession().setAttribute("currentUser",currentUser);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value="cancel", method = RequestMethod.POST)
    public ModelAndView cancelAction(HttpServletRequest request){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        if (currentUser!=null)
            return new ModelAndView("redirect:/actions");
        return new ModelAndView("redirect:/login");
    }

}
