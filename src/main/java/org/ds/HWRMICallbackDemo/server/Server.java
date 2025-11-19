package org.ds.HWRMICallbackDemo.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws RemoteException {
        try{
        Registry registry = LocateRegistry.createRegistry(1099);
        QuizServiceImp serverImpl = new QuizServiceImp();
        registry.rebind("QuizService", serverImpl);
        System.out.println("Quiz Service Server is ready.");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}
