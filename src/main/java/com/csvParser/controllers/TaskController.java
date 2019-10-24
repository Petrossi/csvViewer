package com.csvParser.controllers;

import com.csvParser.services.abstraction.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "task/{token}", method = RequestMethod.GET)
    public String show(@PathVariable String token, Model model) {

        model.addAttribute("task", taskService.findByToken(token));

        return "task";
    }
    @RequestMapping(value = "task/data/{token}", method = RequestMethod.GET)
    public String getData(@PathVariable String token) {

        return taskService.getData(token);
    }
}