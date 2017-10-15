package com.univ.it.table;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface IRemoteDataBase extends IDataBase, Remote {
    boolean addTable(Table table) throws RemoteException;
    boolean dropTable(String tableName) throws RemoteException;
    void addRow(String tableName, Row newRow) throws RemoteException;
    HashMap<String, Table> getTables() throws RemoteException;
    Table getTable(String tableName) throws RemoteException;
    void writeToFile(String pathToFile) throws RemoteException;
}
