package com.csvParser.services;

public class SQLBuilder {

    private String select;
    private String fromSchema = "public";
    private String fromTable;
    private String fromTableAlias;
    private String joins;
    private String where;
    private String orderBy;
    private String sortBy;
    private int limit = -1;
    private int offset = -1;

    public String buildSql() {
        String tableWithShema = fromSchema.equals("public") ? fromTable : fromSchema + "." + fromTable;

        String sql = "SELECT " + select + " FROM " + tableWithShema + " " + fromTableAlias + " " ;

        if (joins != null && !joins.equals("")) {
            sql = sql + " " +joins;
        }
        if (where != null && !where.equals("")) {
            sql = sql + " WHERE " + where;
        }
        if (orderBy != null && !orderBy.equals("")) {
            sql = sql + " ORDER BY " + fromTableAlias + "." + orderBy;

            if(sortBy != null && !sortBy.equals("")){
                sql = sql + " " + sortBy;
            }
        }
//        if (groupBy != null && !groupBy.equals("")) {
//            sql = sql + " GROUP BY " + fromTableAlias + "." + groupBy;
//        }
        if (limit != -1) {
            sql = sql + " LIMIT " + limit;
        }
        if (offset != -1) {
            sql = sql + " OFFSET " + offset;
        }

        sql = sql.replace("SCHEMA_NAME", fromSchema)
            .replace("crawling_page_DOMAIN_FILTER_TITLE", fromTable)
        ;
        return sql;
    }

    public SQLBuilder setSelect(String select) {
        this.select = select;
        return this;
    }

    public SQLBuilder setFromSchema(String fromSchema) {
        this.fromSchema = fromSchema;
        return this;
    }

    public SQLBuilder setFromTable(String fromTable) {
        this.fromTable = fromTable;
        return this;
    }

    public SQLBuilder setFromTableAlias(String fromTableAlias) {
        this.fromTableAlias = fromTableAlias;
        return this;
    }

    public SQLBuilder setJoins(String joins) {
        this.joins = joins;
        return this;
    }

    public SQLBuilder setWhere(String where) {
        this.where = where;
        return this;
    }

    public SQLBuilder andWhere(String where) {
        if( where == null || where.equals("")){
            return this;
        }

        if ( this.where != null && !this.where.equals("")) {
            this.where = " (" + this.where + ") " + " AND (" + where + ") ";
        }else{
            this.where = where;
        }

        return this;
    }

    public SQLBuilder orWhere(String where) {
        if( where == null || where.equals("")){
            return this;
        }

        if ( this.where != null && !this.where.equals("")) {
            this.where = " (" + this.where + ") " + " OR (" + where + ") ";
        }else{
            this.where = where;
        }

        return this;
    }

    public SQLBuilder setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public SQLBuilder setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public SQLBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public SQLBuilder setOffset(int offset) {
        this.offset = offset;
        return this;
    }
}
