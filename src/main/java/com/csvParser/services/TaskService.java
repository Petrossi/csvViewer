package com.csvParser.services;

import com.csvParser.models.Task;
import com.csvParser.repositories.TaskRepository;
import com.csvParser.services.fineuploader.StorageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        int size = dbService.getFistRow(task.getToken()).length;
        List<String> g = IntStream.range(0, size).boxed().collect(Collectors.toList()).stream().map(i -> "column_" + i).map(String::valueOf).collect(Collectors.toList());

        long rowCount = dbService.getFileRowCount(task.getToken());
        task.setRowCount(rowCount);
        String[] headers = new String[g.size()];
        headers = g.toArray(headers);
        task.setHeaders(headers);

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