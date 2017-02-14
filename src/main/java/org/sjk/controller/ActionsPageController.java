package org.sjk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by vkalashnykov on 13.02.17.
 */
@Controller
public class ActionsPageController {

    @RequestMapping("actions")
    public ModelAndView actionsPage(){
        return new ModelAndView("actions_page");
    }
}
