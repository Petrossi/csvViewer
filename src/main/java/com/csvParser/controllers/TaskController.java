package com.csvParser.controllers;

import com.csvParser.common.request.task.TaskDataPaginationConfig;
import com.csvParser.models.Task;
import com.csvParser.services.DBService;
import com.csvParser.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private DBService dbService;

    @RequestMapping(value = "/task/{token}", method = RequestMethod.GET)
    public String show(@PathVariable String token, Model model) {

        Task task = taskService.findByToken(token);

        model.addAttribute("task", task);

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
        @RequestParam(value = "search", defaultValue = "") String search,
        @RequestParam(value = "token") String token
    ) {
        return ResponseEntity.ok().body(dbService.parseData(new TaskDataPaginationConfig(sortBy, sortParam, page, pageSize, token, search)));
    }

    @PostMapping(value="/task/data/pagination/", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<String> getPaginationData(@RequestBody TaskDataPaginationConfig config) {
        return ResponseEntity.ok().body(dbService.parseData(config));
    }

    @GetMapping(value="/task/process/{token}", produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public ResponseEntity<String> process(@PathVariable String token) {

        new Thread(() -> {
            Task task = taskService.findByToken(token);

            try {
                dbService.importData(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok().body("");
    }

    @PostMapping(value="/task/updateHeaders/{token}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<Task> updateHeaders(@PathVariable String token, @RequestBody String[] headers) {
        return ResponseEntity.ok().body(taskService.updateHeaders(token, headers));
    }
}