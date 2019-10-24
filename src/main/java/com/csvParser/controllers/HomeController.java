package com.csvParser.controllers;

import com.csvParser.config.Route;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = Route.ROOT, method = RequestMethod.GET)
    public String index() {
        return "dash/index";
    }

    @RequestMapping(value = "/uploader", method = RequestMethod.GET)
    public String uploaderPage() {
        return "uploader";
    }
}