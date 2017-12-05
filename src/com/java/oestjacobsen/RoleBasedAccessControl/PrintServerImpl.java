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
            Logger.LogBefore("Unknown user", START_COMMAND);
            Logger.LogBeforeAuth("Unknown user", false);
            return AUTH_TESTER;
        } else {
            Logger.LogBefore(username, START_COMMAND);
            Logger.LogBeforeAuth(username, true);
        }
        if(mAccessController.userHasAccessRights(username, START_COMMAND)) {
            Logger.LogAccess(username, START_COMMAND, true);
            if (mServerRunning) {
                return "Server is already running";
            }
            mServerRunning = true;
            Logger.Log("Server has started");
            return "Server has started";
        } else {
            Logger.LogAccess(username, START_COMMAND,false);
            return NORIGHTS_MSG;
        }
    }

    @Override
    public String stop(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", STOP_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, STOP_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, STOP_COMMAND)) {
                Logger.LogAccess(username, STOP_COMMAND, true);
                mQueue = new ArrayList<>();
                mAuthenticatedUsers = new HashSet<>();
                mServerRunning = false;
                Logger.Log("Server has stopped");
                return "Server has stopped";
            } else {
                Logger.LogAccess(username, STOP_COMMAND,false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String print(String filename, String printer, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", PRINT_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, PRINT_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, PRINT_COMMAND)) {
                Logger.LogAccess(username, PRINT_COMMAND, true);
                mQueue.add(new Job(filename, printer));
                return "added following document: " + filename + " to " + printer + "'s queue";
            } else {
                Logger.LogAccess(username, STOP_COMMAND,false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String queue(String username) {
        if (mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", QUEUE_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, QUEUE_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, QUEUE_COMMAND)) {
                Logger.LogAccess(username, QUEUE_COMMAND, true);
                return queueToString();
            } else {
                Logger.LogAccess(username, QUEUE_COMMAND, false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String topQueue(int jobindex, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", TOPQUEUE_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, TOPQUEUE_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, TOPQUEUE_COMMAND)) {
                Logger.LogAccess(username, TOPQUEUE_COMMAND, true);
                if((jobindex <= mQueue.size()) && (jobindex > 0)) {
                    moveJobToTop(jobindex);
                    return "job " + jobindex + " moved to the top";
                }
                return "no job at specified jobnumber";
            } else {
                Logger.LogAccess(username, TOPQUEUE_COMMAND, false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String restart(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", RESTART_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, RESTART_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, RESTART_COMMAND)) {
                Logger.LogAccess(username, RESTART_COMMAND, true);
                mQueue = new ArrayList<>();
                mAuthenticatedUsers = new HashSet<>();
                Logger.Log("Server has restarted, authentication is needed again");
                return "Server restarted";
            } else {
                Logger.LogAccess(username, RESTART_COMMAND, false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String status(String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", STATUS_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, STATUS_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, STATUS_COMMAND)) {
                Logger.LogAccess(username, STATUS_COMMAND, true);
                if(mQueue.isEmpty()) {
                    return "Server is online\nwaiting for job...";
                }
                return "Server is online\nprinting document...";
            } else {
                Logger.LogAccess(username, STATUS_COMMAND, false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String readConfig(String parameter, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", READCONFIG_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, READCONFIG_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, READCONFIG_COMMAND)) {
                Logger.LogAccess(username, READCONFIG_COMMAND, true);
                String value = mConfig.getOrDefault(parameter,"null");
                if(value.equals("null")) {
                    return "Couldn't find specific configuration";
                }
                return "Configuration " + parameter + " is set to: " + value;
            } else {
                Logger.LogAccess(username, READCONFIG_COMMAND, false);
                return NORIGHTS_MSG;
            }
        }
        return "Server is offline";
    }

    @Override
    public String setConfig(String parameter, String value, String username) {
        if(mServerRunning) {
            if(!userIsAuthenticated(username)) {
                Logger.LogBefore("Unknown user", SETCONFIG_COMMAND);
                Logger.LogBeforeAuth("Unknown user", false);
                return AUTH_TESTER;
            } else {
                Logger.LogBefore(username, SETCONFIG_COMMAND);
                Logger.LogBeforeAuth(username, true);
            }
            if(mAccessController.userHasAccessRights(username, SETCONFIG_COMMAND)) {
                Logger.LogAccess(username, SETCONFIG_COMMAND, true);
                mConfig.put(parameter, value);
                return "Configuration set for: " + parameter + " => " + value;
            } else {
                Logger.LogAccess(username, SETCONFIG_COMMAND, false);
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
        public static void Log(String msg) {
            System.out.println(msg);
        }

        public static void LogBefore(String username, String command) {
            System.out.println("\n" + username + " wants to execute command " + command);
        }

        public static void LogAuth(String username, boolean success) {
            if(success) {
                System.out.println(username + " successfully logged in!");
            } else {
                System.out.println(username + " not found or wrong password!");
            }
        }

        public static void LogBeforeAuth(String username, boolean success) {
            if(success) {
                System.out.println(username +  " is properly authenticated!");
            } else {
                System.out.println(username + " needs to be authenticated!");
            }
        }

        public static void LogAccess(String username, String command,boolean success) {
            if(success) {
                System.out.println(username + " has permission and invokes " + command + " command");
            } else {
                System.out.println(username + " doesn't have the permission to invoke " + command);
            }
        }
    }
}
