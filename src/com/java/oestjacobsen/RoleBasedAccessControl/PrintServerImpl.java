package com.java.oestjacobsen.RoleBasedAccessControl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PrintServerImpl extends UnicastRemoteObject implements PrintServer {
    //PRINT COMMANDS:
    public static final String PRINT_COMMAND = "print";
    public static final String START_COMMAND = "start";
    public static final String STOP_COMMAND = "stop";
    public static final String RESTART_COMMAND = "restart";
    public static final String QUEUE_COMMAND = "queue";
    public static final String TOPQUEUE_COMMAND = "topqueue";
    public static final String STATUS_COMMAND = "status";
    public static final String SETCONFIG_COMMAND = "setconfig";
    public static final String READCONFIG_COMMAND = "readconfig";

    public static final String NORIGHTS_MSG = "You don't have the proper rights to execute the command";

    public static final String AUTH_TESTER = "AUTHENTICATION_NEEDED";
    private boolean mServerRunning = false;
    private Authenticator mAuthenticator;
    private AccessController mAccessController;

    private ArrayList<Job> mQueue;
    private HashMap<String, String> mConfig;
    private HashSet<String> mAuthenticatedUsers;


    protected PrintServerImpl() throws RemoteException {
        super();
        mAuthenticator = new Authenticator();
        mAccessController = new AccessController();
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
    public String start(String username) {
        if(!userIsAuthenticated(username)) {
            return AUTH_TESTER;
        }
        if(mAccessController.userHasAccessRights(username, START_COMMAND)) {
            if (mServerRunning) {
                return "Server is already running";
            }
            Logger.Log("Start Server");
            mServerRunning = true;
            return "Server has started";
        } else {
            return NORIGHTS_MSG;
        }
    }

    @Override
    public String stop(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, STOP_COMMAND)) {
                Logger.Log(username,"Stop Server");
                mQueue = new ArrayList<>();
                mAuthenticatedUsers = new HashSet<>();
                mServerRunning = false;
                return "Server has stopped";
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String print(String filename, String printer, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, PRINT_COMMAND)) {
                Logger.Log(username,"Print Document");
                mQueue.add(new Job(filename, printer));
                return "added following document: " + filename + " to " + printer + "'s queue";
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String queue(String username) {
        if (mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, QUEUE_COMMAND)) {
                Logger.Log(username,"Display Queue");
                return queueToString();
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String topQueue(int jobindex, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, TOPQUEUE_COMMAND)) {
                Logger.Log(username,"Top Queue");
                if((jobindex <= mQueue.size()) && (jobindex > 0)) {
                    moveJobToTop(jobindex);
                    return "job " + jobindex + " moved to the top";
                }
                return "no job at specified jobnumber";
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String restart(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, RESTART_COMMAND)) {
                Logger.Log(username,"Restart Server");
                mQueue = new ArrayList<>();
                mAuthenticatedUsers = new HashSet<>();
                return "Server restarted";
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String status(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, STATUS_COMMAND)) {
                Logger.Log(username,"Server Status");
                if(mQueue.isEmpty()) {
                    return "Server is online\nwaiting for job...";
                }
                return "Server is online\nprinting document...";
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String readConfig(String parameter, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, READCONFIG_COMMAND)) {
                Logger.Log(username,"Read Config");
                String value = mConfig.getOrDefault(parameter,"null");
                if(value.equals("null")) {
                    return "Couldn't find specific configuration";
                }
                return "Configuration " + parameter + " is set to: " + value;
            } else {
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String setConfig(String parameter, String value, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                return AUTH_TESTER;
            }
            if(mAccessController.userHasAccessRights(username, SETCONFIG_COMMAND)) {
                Logger.Log(username,"Set Config");
                mConfig.put(parameter, value);
                return "Configuration set for: " + parameter + " => " + value;
            } else {
                return NORIGHTS_MSG;
            }
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
        if(mAuthenticator.authenticate(username, password)){
            mAuthenticatedUsers.add(username);
            Logger.LogAuth(username, true);
            return "Logged in successful as " + username;
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
