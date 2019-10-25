package com.csvParser.services;

import com.csvParser.models.Task;
import com.csvParser.services.abstraction.AbstractService;
import com.csvParser.services.fineuploader.StorageService;
import liquibase.util.csv.CSVReader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void importData(String token) throws IOException {
        Task task = taskService.findByToken(token);

        String [] firstLine = getFistRow(task.getToken());

        tableService.createTable(task, firstLine.length);

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

    private CSVReader getReader(String fileName) throws FileNotFoundException {
        return new CSVReader(new FileReader(storageService.getFinalPath().resolve(fileName).toFile()));
    }

    public String [] getFistRow(String fileName) throws IOException {
        return getReader(fileName).readNext();
    }

    private void readAndSave(Task task) throws IOException {
        int batchCount = 200;

        CSVReader reader = getReader(task.getToken());

        List<String []> data = new ArrayList<>();
        String [] nextLine;

        while ((nextLine = reader.readNext()) != null) {
            data.add(nextLine);

            if(data.size() == batchCount){
                taskImporterService.insertData(task, data);
                data.clear();
            }
        }

        if(!data.isEmpty()){
            taskImporterService.insertData(task, data);
        }
    }


    public List getColumnsList(String sqlToGetData, List<String> columns){
        return jdbcTemplate.query(
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
        );
    }
}