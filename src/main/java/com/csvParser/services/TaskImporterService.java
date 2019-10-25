package com.csvParser.services;

import com.csvParser.models.Task;
import com.csvParser.services.abstraction.AbstractService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.csvParser.services.TableService.TASK_STORE_SCHEMA_NAME;

@Service
public class TaskImporterService extends AbstractService {

    public void insertData(Task task, List<String []> data){
        String tableName = TASK_STORE_SCHEMA_NAME +"."+task.getToken();

        String valuesSql = IntStream.range(0, data.get(0).length).mapToObj(i -> "?").collect(Collectors.joining(",")) + " \n";
        String columnsSql = IntStream.range(0, data.get(0).length ).mapToObj(i -> "column_"+String.valueOf(i)).collect(Collectors.joining(",")) + " \n"
                ;
        String insertString = "INSERT INTO "+tableName+"(" + columnsSql + ") values("+valuesSql+")";

        jdbcTemplate.batchUpdate(insertString, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int itemIndex) throws SQLException {
                String [] currentData = data.get(itemIndex);

                for (int i = 0; i < data.get(0).length; i++) {
                    ps.setString(i + 1, currentData[i]);
                }
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
    }
}
