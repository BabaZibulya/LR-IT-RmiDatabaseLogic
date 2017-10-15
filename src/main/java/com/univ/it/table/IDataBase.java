package com.univ.it.table;

import java.util.HashMap;

public interface IDataBase {
    boolean addTable(Table table) throws Exception;
    boolean dropTable(String tableName) throws Exception;
    HashMap<String, Table> getTables() throws Exception;
    Table getTable(String tableName) throws Exception;
    void writeToFile(String pathToFile) throws Exception;
}
