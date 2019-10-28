package com.csvParser.models;

import com.csvParser.hibernate.StringArrayType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Entity
@TypeDefs({
    @TypeDef(typeClass = StringArrayType.class, defaultForType = String[].class)
})
public class Task {

    public enum STATUS {
        IN_PROGRESS,
        FINISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    private String token;

    private String originFileName;

    private STATUS status;

    @Column(columnDefinition = "text[]")
    private String[] columns;

    @Column(columnDefinition = "text[]")
    private String[] headers;

    private long rowCount;

    private boolean ignoreFirstRow;

    @Transient
    private boolean success;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getOriginFileName() {
        return originFileName;
    }
    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public STATUS getStatus() {
        return status;
    }
    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String[] getHeaders() {
        return headers;
    }
    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public long getRowCount() {
        return rowCount;
    }
    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public String[] getColumns() {
        return columns;
    }
    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public boolean isIgnoreFirstRow() {
        return ignoreFirstRow;
    }
    public void setIgnoreFirstRow(boolean ignoreFirstRow) {
        this.ignoreFirstRow = ignoreFirstRow;
    }
}