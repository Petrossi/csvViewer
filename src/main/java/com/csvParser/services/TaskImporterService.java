package com.csvParser.services;

import com.csvParser.models.Task;
import com.csvParser.services.abstraction.AbstractService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.csvParser.services.TableService.TASK_STORE_SCHEMA_NAME;
import static com.csvParser.utils.Utils.limit;

@Service
public class TaskImporterService extends AbstractService {

    public void insertData(Task task, List<String []> data){
        String tableName = TASK_STORE_SCHEMA_NAME +"."+task.getToken();

        String valuesSql = IntStream.range(0, task.getHeaders().length).mapToObj(i -> "?").collect(Collectors.joining(",")) + " \n";
        String columnsSql = Arrays.stream(task.getHeaders()).collect(Collectors.joining(",")) + " \n";
        String insertString = "INSERT INTO "+tableName+"(" + columnsSql + ") values("+valuesSql+")";
        int headersSize =  task.getHeaders().length;

        try{
            jdbcTemplate.batchUpdate(insertString, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int itemIndex) throws SQLException {
                    String [] currentData = data.get(itemIndex);

                    for (int i = 0; i < headersSize; i++) {
                        String data = "";
                        try {
                            data = (i >= currentData.length) ? "" : currentData[i];
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println(e);
                        }

                        ps.setString(i + 1, limit(data, 500));
                    }
                }

                @Override
                public int getBatchSize() {
                    return data.size();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }

    }
}
