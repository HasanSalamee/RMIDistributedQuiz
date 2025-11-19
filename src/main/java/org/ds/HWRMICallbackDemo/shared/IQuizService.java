package org.ds.HWRMICallbackDemo.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IQuizService extends Remote {

    String requestQuestion(String studentName) throws RemoteException;
    String submitAnswer(String studentName, String answer) throws RemoteException;
    String getLeaderboard() throws RemoteException;
    void registerForCallback(String studentName, IClientCallback callbackObject) throws RemoteException;
}
