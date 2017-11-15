package com.java.oestjacobsen;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PrintServerImpl extends UnicastRemoteObject implements PrintServer {
    public static final String AUTH_TESTER = "AUTHENTICATION_NEEDED";
    private boolean mServerRunning = false;
    private ArrayList<Job> mQueue;
    private HashMap<String, String> mConfig;
    private HashSet<String> mAuthenticatedUsers;



    protected PrintServerImpl() throws RemoteException {
        super();
        mQueue = new ArrayList<>();
        mConfig = new HashMap<>();
        mAuthenticatedUsers = new HashSet<>();
    }

    public static void main(String args[]) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(5099);
        registry.rebind("printserver", new PrintServerImpl());
    }

    private boolean userIsAuthenticated(String username) {
        if(mAuthenticatedUsers.contains(username)) {
            return true;
        }
        return false;
    }

    @Override
    public String start() {
        Logger.Log("Start Server");
        if(mServerRunning) {
            return "Server is already running";
        }
        mServerRunning = true;
        return "Server has started";
    }

    @Override
    public String stop(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Stop Server");
            mQueue = new ArrayList<>();
            mAuthenticatedUsers = new HashSet<>();
            mServerRunning = false;
            return "Server has stopped";
        }
        return "Server is offline";
    }

    @Override
    public String print(String filename, String printer, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Print Document");
            mQueue.add(new Job(filename, printer));
            return "added following document: " + filename + " to " + printer + "'s queue";
        }
        return "Server is offline";
    }

    @Override
    public String queue(String username) {
        if (mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Display Queue");
            return queueToString();
        }
        return "Server is offline";
    }

    @Override
    public String topQueue(int jobindex, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Top Queue");
            if((jobindex <= mQueue.size()) && (jobindex > 0)) {
                moveJobToTop(jobindex);
                return "job " + jobindex + " moved to the top";
            }
            return "no job at specified jobnumber";
        }
        return "Server is offline";

    }

    @Override
    public String restart(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Restart Server");
            mServerRunning = false;
            mQueue = new ArrayList<>();
            mAuthenticatedUsers = new HashSet<>();
            mServerRunning = true;
            return "Server restarted";
        }
        return "Server is offline";
    }

    @Override
    public String status(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Server Status");
            if(mQueue.isEmpty()) {
                return "Server is online\nwaiting for job...";
            }
            return "Server is online\nprinting document...";
        }
        return "Server is offline";
    }

    @Override
    public String readConfig(String parameter, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Read Config");
            String value = mConfig.getOrDefault(parameter,"null");
            if(value.equals("null")) {
                return "Couldn't find specific configuration";
            }
            return "Configuration " + parameter + " is set to: " + value;
        }
        return "Server is offline";
    }

    @Override
    public String setConfig(String parameter, String value, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            Logger.Log(username,"Set Config");
            mConfig.put(parameter, value);
            return "Configuration set for: " + parameter + " => " + value;
        }
        return "Server is offline";
    }

    private void moveJobToTop(int index) {
        ArrayList<Job> newQueue = new ArrayList<>();
        newQueue.add(mQueue.get(index-1 ));
        for(int i = 0; i < mQueue.size(); i++) {
            if(i != index-1) {
                newQueue.add(mQueue.get(i));
            }
        }
        mQueue = newQueue;
    }

    private String queueToString() {
        StringBuilder sb = new StringBuilder("print queue:\n");
        int jobindex = 1;
        if((mQueue.size() > 0)) {
            for(Job job: mQueue) {
                if(mQueue.size() == jobindex) {
                    sb.append(job.filename + " {" + jobindex + "}");
                } else {
                    sb.append(job.filename + " {" + jobindex + "}\n");
                    jobindex++;
                }
            }
        } else {
            sb.append("queue is empty");
        }
        return sb.toString();
    }

    @Override
    public String authenticate(String username, String password) {
        Authenticator auth = new Authenticator();
        if(auth.authenticate(username, password)){
            mAuthenticatedUsers.add(username);
            Logger.LogAuth(username, true);
            return "Log in successful!";
        }
        Logger.LogAuth(username, false);
        return "Username or password incorrect.";
    }

    private static class Logger {
        public static void Log(String username, String command) {
            System.out.println("Command: '" + command + "' executed by " + username);
        }

        public static void Log(String command) {
            System.out.println("Command: '" + command + "' executed");
        }
        public static void LogAuth(String username, boolean success) {
            if(success) {
                System.out.println(username + " successfully logged in!");
            } else {
                System.out.println(username + " not found or wrong password!");
            }
        }
    }
}
