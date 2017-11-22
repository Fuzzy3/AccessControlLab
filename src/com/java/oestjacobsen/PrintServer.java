package com.java.oestjacobsen;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PrintServer extends Remote{


    String start(String username) throws RemoteException;
    String stop(String username) throws RemoteException;
    String print(String filename, String printer, String username) throws RemoteException;
    String queue(String username) throws RemoteException;
    String topQueue(int jobindex, String username) throws RemoteException;
    String restart(String username) throws RemoteException;
    String status(String username) throws RemoteException;
    String readConfig(String parameter, String username) throws RemoteException;
    String setConfig(String parameter, String value, String username) throws RemoteException;

    String authenticate(String username, String password) throws RemoteException;
}
