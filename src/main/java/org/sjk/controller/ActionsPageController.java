package org.sjk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by vkalashnykov on 13.02.17.
 */
@Controller
public class ActionsPageController {

    @RequestMapping(value = "/actions",method = RequestMethod.GET)
    public ModelAndView actionsPage(Model model){
        return new ModelAndView("actions_page");
    }
}
