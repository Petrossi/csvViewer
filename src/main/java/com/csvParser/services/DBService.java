package com.csvParser.services;

import com.csvParser.common.request.task.TaskDataPaginationConfig;
import com.csvParser.common.response.TaskDataResponse;
import com.csvParser.models.Task;
import com.csvParser.services.abstraction.AbstractService;
import com.csvParser.services.fineuploader.StorageService;
import com.csvParser.utils.JsonConverter;
import liquibase.util.csv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.csvParser.services.TableService.TASK_STORE_SCHEMA_NAME;

@Service
public class DBService extends AbstractService {

    @Autowired
    private TaskImporterService taskImporterService;

    @Autowired
    private TableService tableService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected StorageService storageService;

    @Autowired
    protected SimpMessagingTemplate messagingTemplate;

    public void importData(Task task) throws IOException {
        tableService.createTable(task);

        readAndSave(task);
    }

    public String parseData(String token)  {
        Task task = taskService.findByToken(token);
        StringBuilder result = new StringBuilder();

        try {
            CSVReader reader = getReader(task.getToken());

            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                result.append(String.join(",", nextLine)).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public String parseData(TaskDataPaginationConfig config) {
        Task task = taskService.findByToken(config.getToken());

        SQLBuilder builder = new SQLBuilder()
            .setSelect(Arrays.stream(task.getHeaders()).collect(Collectors.joining(",")))
            .setFromTable(task.getToken())
            .setFromSchema(TASK_STORE_SCHEMA_NAME)
            .setFromTableAlias("t")
        ;

        if(config.getPageSize() != -1){
            builder.setLimit(config.getPageSize());
            builder.setOffset( config.getPageSize() * (config.getPage() - 1));
        }

        JSONArray summary = getColumnsList(builder.buildSql(), Arrays.asList(task.getHeaders()));

        int totalCount = getTotalCount(task);
        int filteredCount = summary.length();
        int pages = 1;
        if( config.getPageSize() != -1){
            if(filteredCount % config.getPageSize() == 0){
                pages = totalCount/config.getPageSize();
            }else{
                pages = totalCount/config.getPageSize() + 1;
            }
        }

        TaskDataResponse response = new TaskDataResponse(
            true,
            "success",
            summary,
            totalCount,
            summary.length(),
            pages
        );

        return JsonConverter.convertToJsonObject(response).toString();
    }

    public int getTotalCount(Task task){
        String sql = new SQLBuilder()
            .setSelect("count(*)")
            .setFromTable(task.getToken())
            .setFromSchema(TASK_STORE_SCHEMA_NAME)
            .setFromTableAlias("t")
            .buildSql()
        ;

        return getCountBySql(sql);
    }

    private CSVReader getReader(String fileName) throws FileNotFoundException {
        return new CSVReader(new FileReader(storageService.getFinalPath().resolve(fileName).toFile()));
    }

    public long getFileRowCount(String fileName) throws IOException {
        return Files.lines(storageService.getFinalPath().resolve(fileName)).count();
    }

    public String [] getFistRow(String fileName) throws IOException {
        return getReader(fileName).readNext();
    }

    private void readAndSave(Task task) throws IOException {
        executeQuery("TRUNCATE TABLE "+TASK_STORE_SCHEMA_NAME+"."+task.getToken()+" CONTINUE IDENTITY RESTRICT;");
        String urlToSend = "/channel/app/parsingProgress/" + task.getToken();

        int batchCount = 200;
        int processedCount = 0;
        CSVReader reader = getReader(task.getToken());

        List<String []> data = new ArrayList<>();
        String [] nextLine;

        while ((nextLine = reader.readNext()) != null) {
            data.add(nextLine);

            if(data.size() == batchCount){
                taskImporterService.insertData(task, data);
                data.clear();

                processedCount += batchCount;

                messagingTemplate.convertAndSend(urlToSend, new JSONObject()
                    .put("status", Task.STATUS.IN_PROGRESS)
                    .put("processedCount", processedCount)
                    .put("totalCount", task.getRowCount()).toString()
                );
            }
        }

        if(!data.isEmpty()){
            taskImporterService.insertData(task, data);
        }

        messagingTemplate.convertAndSend(urlToSend, new JSONObject().put("status", Task.STATUS.FINISHED).toString());
    }

    public JSONArray getColumnsList(String sqlToGetData, List<String> columns){
        return new JSONArray(jdbcTemplate.query(
            sqlToGetData,
            (rs, rowNum) -> {
                JSONObject incomingObj = new JSONObject();

                columns.forEach((column) -> {
                    try {
                        incomingObj.put(column, rs.getString(column));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                return incomingObj;
            }
        ));
    }
}