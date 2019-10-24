package com.csvParser.services.abstraction;

import com.csvParser.models.Task;
import com.csvParser.repositories.TaskRepository;
import com.csvParser.services.fineuploader.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StorageService storageService;

    public Task create(String uuid, String fileName){
        Task task = new Task();
        task.setToken(uuid);
        task.setOriginFileName(fileName);
        task.setStatus(Task.STATUS.IN_PROGRESS);
        task.setSuccess(true);

        taskRepository.save(task);
        storageService.moveToFinalDir(uuid, fileName);

        return task;
    }

    public Task findByToken(String token) {
        return taskRepository.findFirstByToken(token);
    }

    public String getData(String token) {
        Task task = findByToken(token);

        return "";
    }
}
