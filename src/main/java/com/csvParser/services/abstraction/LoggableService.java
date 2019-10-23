package com.csvParser.services.abstraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public abstract class LoggableService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void info(String message){
        logger.info(message);
    }

    protected void error(String message){
        logger.error(message);
    }

    protected void warn(String message){
        logger.warn(message);
    }
}
