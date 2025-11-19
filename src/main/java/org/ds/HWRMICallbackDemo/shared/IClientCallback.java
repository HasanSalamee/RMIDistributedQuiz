package org.ds.HWRMICallbackDemo.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientCallback extends Remote {
    void updateLeaderboard(String leaderboardData) throws RemoteException;
}

