package com.csvParser.repositories;

import com.csvParser.models.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
    Task findFirstByToken(String token);
}