package com.csvParser.controllers;

import com.csvParser.common.pagination.task.TaskDataPaginationConfig;
import com.csvParser.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/task/{token}", method = RequestMethod.GET)
    public String show(@PathVariable String token, Model model) {

        model.addAttribute("token", token);

        return "task";
    }

    @GetMapping(value="/task/data/{token}", produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public ResponseEntity<String> getData(@PathVariable String token) {
        return ResponseEntity.ok().body(taskService.getData(token));
    }

    @GetMapping(value="/task/data/pagination/", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<String> getPaginationData(
        @RequestParam(value = "sortBy", defaultValue = "asc") String sortBy,
        @RequestParam(value = "sortParam", defaultValue = "") String sortParam,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "token") String token
    ) {
        return ResponseEntity.ok().body(taskService.getData(new TaskDataPaginationConfig(sortBy, sortParam, page, pageSize, token)));
    }

    @PostMapping(value="/task/data/pagination/", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<String> getPaginationData(@RequestBody TaskDataPaginationConfig config) {
        return ResponseEntity.ok().body(taskService.getData(config));
    }
}