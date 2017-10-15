package com.univ.it.server;

import com.univ.it.table.RmiDataBase;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiServer {

    private RmiDataBase db;

    private RmiServer() throws RemoteException {
        db = new RmiDataBase();
    }

    public static void main(String args[]) {
        System.out.println("RMI server started");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
            System.out.println("Security manager installed.");
        } else {
            System.out.println("Security manager already exists.");
        }

        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        try {
            RmiServer server = new RmiServer();
            Naming.rebind("//localhost/RmiDataBase", server.db);
        } catch (Exception e) {
            System.err.println("RMI server exception:" + e);
            e.printStackTrace();
        }
    }

}
