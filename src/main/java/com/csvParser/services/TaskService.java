package com.csvParser.services;

import com.csvParser.models.Task;
import com.csvParser.repositories.TaskRepository;
import com.csvParser.services.fineuploader.StorageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private DBService dbService;

    public Task create(String uuid, String fileName) throws IOException {

        Task task = new Task();
        task.setToken(RandomStringUtils.randomAlphabetic(15).toLowerCase());
        task.setOriginFileName(fileName);
        task.setStatus(Task.STATUS.IN_PROGRESS);
        task.setSuccess(true);

        storageService.moveToFinalDir(task, uuid, fileName);

        task.setHeaders(dbService.getFistRow(task.getToken()));

        taskRepository.save(task);

        return task;
    }

    public Task findByToken(String token) {
        return taskRepository.findFirstByToken(token);
    }

    public String getData(String token) {
        Task task = findByToken(token);

        return dbService.parseData(task.getToken());
    }
}