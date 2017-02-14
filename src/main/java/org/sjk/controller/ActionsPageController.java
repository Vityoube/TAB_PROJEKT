package org.sjk.controller;

import org.sjk.dao.ActionDao;
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

    @RequestMapping(value = "actions",method = RequestMethod.GET)
    public ModelAndView actionsPage(Model model, HttpServletRequest request){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        List<Action> userActions=actionDao.findAllActions(currentUser.getId());
        model.addAttribute("actions",userActions);
        return new ModelAndView("actions_page");
    }
    @RequestMapping(value="actionsRemove",method = RequestMethod.POST)
    public ModelAndView removeActions(Model model, HttpServletRequest request){
        currentUser=(User)request.getSession().getAttribute("currentUser");
        actionDao.removeActionsForUser(currentUser.getId());
        List<Action> userActions=actionDao.findAllActions(currentUser.getId());
        model.addAttribute("actions",userActions);
        return new ModelAndView("actions_page");
    }

}
