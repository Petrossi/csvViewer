package com.csvParser.services.abstraction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public abstract class AbstractService extends LoggableService{

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected int getExceptionLineNumber(Exception e, String methodNameExp)
    {
        StackTraceElement ele = Arrays.stream(e.getStackTrace()).filter(o -> o.getMethodName().equals(methodNameExp)).findAny().orElse(null);
        if(ele != null){
            return ele.getLineNumber();
        }
        return -1;
    }

    protected void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void sleepMinutes(int minutes){
        try {
            Thread.sleep(1000*60*minutes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void sleepSeconds(int seconds){
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void executeQuery(String query){
        jdbcTemplate.execute(query);
    }

    protected void executeQueryWithRetries(String query){
        int retryCount = 0;
        int retryMax = 3;
        boolean success = false;
        while (!success || retryCount > retryMax){
            retryCount++;
            try {
                jdbcTemplate.execute(query);

                success = true;
            }catch (Exception e){
                sleepSeconds(3);
            }
        }
    }

    protected int getCountBySql(String sql){
        return getCountBySql(sql, new Object[] {  });
    }

    protected int getCountBySql(String sql, Object[] args){
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

    protected int parseInt(String value){
        return Integer.parseInt(value);
    }
}
