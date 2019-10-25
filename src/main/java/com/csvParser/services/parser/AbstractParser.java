package com.csvParser.services.parser;

import com.csvParser.services.TaskService;
import com.csvParser.services.fineuploader.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractParser {

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected StorageService storageService;
}
