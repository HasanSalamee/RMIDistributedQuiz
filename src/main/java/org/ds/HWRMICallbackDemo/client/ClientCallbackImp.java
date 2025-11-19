package org.ds.HWRMICallbackDemo.client;

import org.ds.HWRMICallbackDemo.shared.IClientCallback;

import java.rmi.RemoteException;

public class ClientCallbackImp extends java.rmi.server.UnicastRemoteObject implements IClientCallback {

    protected ClientCallbackImp() throws RemoteException {
        super();
    }

    @Override
    public void updateLeaderboard(String leaderboardData) throws java.rmi.RemoteException {
        System.out.println("Leaderboard Updated:\n" + leaderboardData);
    }

}
