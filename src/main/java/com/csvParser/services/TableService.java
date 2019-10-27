package com.csvParser.services;

import com.csvParser.models.Task;
import com.csvParser.services.abstraction.AbstractService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class TableService extends AbstractService {
    public static final String TASK_STORE_SCHEMA_NAME = "task";

    public void createTable(Task task){
        String tableName = TASK_STORE_SCHEMA_NAME +"."+task.getToken();

        if(tableExists(tableName)){
            return;
        }
        String columnsSql = Arrays.stream(task.getColumns())
            .map(column -> column + " varchar(500)")
            .collect(Collectors.joining(",\n"))
            + " \n"
        ;

        String sql =
            "CREATE TABLE IF NOT EXISTS "+tableName+"\n" +
            "(\n" +
            "    id serial PRIMARY KEY NOT NULL,\n" +
            columnsSql +
            ");\n" +
            "CREATE UNIQUE INDEX IF NOT EXISTS table_name_id_uindex ON "+tableName+" (id);"
        ;
        executeQuery(sql);
    }

    public boolean tableExists(String tableName){
        String sql = "SELECT to_regclass('"+tableName+"')";

        String result = jdbcTemplate.queryForObject(sql, new Object[] {  }, String.class);

        return result != null;
    }
}