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

        String[] firstRow = dbService.getFistRow(task.getToken());

        int size = firstRow.length;

        long rowCount = dbService.getFileRowCount(task.getToken());
        task.setRowCount(rowCount);
        String[] columns = generateColumns(size);

        task.setColumns(columns);
        task.setHeaders(firstRow);

        taskRepository.save(task);

        return task;
    }

    public String[] generateColumns(int size){
        List<String> g = IntStream.range(0, size).boxed().collect(Collectors.toList()).stream().map(i -> "column_" + i).map(String::valueOf).collect(Collectors.toList());
        String[] columns = new String[g.size()];
        columns = g.toArray(columns);

        return columns;
    }

    public Task findByToken(String token) {
        return taskRepository.findFirstByToken(token);
    }

    public String getData(String token) {
        Task task = findByToken(token);

        return dbService.parseData(task.getToken());
    }

    public Task updateHeaders(String token, String[] headers) {
        Task task = findByToken(token);
        String[] columns = generateColumns(headers.length);
        task.setColumns(columns);
        task.setHeaders(headers);

        taskRepository.save(task);

        return task;
    }
}