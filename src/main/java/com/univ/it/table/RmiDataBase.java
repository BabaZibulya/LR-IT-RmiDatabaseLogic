package com.univ.it.table;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RmiDataBase extends UnicastRemoteObject implements IRemoteDataBase {

    private LocalDataBase db;
    private final String PATH_TO_DB = "/home/bondarenko";
    private final String DB_NAME = "test.db";

    public RmiDataBase() throws RemoteException {
        try {
            db = LocalDataBase.readFromFile(PATH_TO_DB + "/" + DB_NAME);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public boolean addTable(Table table) {
        return db.addTable(table);
    }

    @Override
    public boolean dropTable(String tableName) {
        return db.dropTable(tableName);
    }

    @Override
    public void addRow(String tableName, Row newRow) throws RemoteException {
        try {
            db.getTable(tableName).addNewRow(newRow);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public HashMap<String, Table> getTables() {
        return db.getTables();
    }

    @Override
    public Table getTable(String tableName) {
        return db.getTable(tableName);
    }

    @Override
    public void writeToFile(String pathToFile) throws RemoteException {
        try {
            db.writeToFile(PATH_TO_DB);
        } catch (FileNotFoundException e) {
            throw new RemoteException(e.getMessage());
        }
    }
}
