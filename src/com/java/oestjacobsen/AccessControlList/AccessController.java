package com.java.oestjacobsen.AccessControlList;

public class AccessController {

    private AccessControlReader mAccessControlReader = new AccessControlReader();


    public AccessController() {
        if(!mAccessControlReader.fileExist()) {
            addDefaultUsers();
        }
    }

    private void addDefaultUsers() {
        String username1 = "alice";
        String[] commands1 = {
                PrintServerImpl.START_COMMAND,
                PrintServerImpl.STOP_COMMAND,
                PrintServerImpl.RESTART_COMMAND,
                PrintServerImpl.PRINT_COMMAND,
                PrintServerImpl.QUEUE_COMMAND,
                PrintServerImpl.TOPQUEUE_COMMAND,
                PrintServerImpl.STATUS_COMMAND,
                PrintServerImpl.SETCONFIG_COMMAND,
                PrintServerImpl.READCONFIG_COMMAND
        };
        mAccessControlReader.saveAccessControl(username1, commands1);

        String username2 = "bob";
        String[] commands2 = {
                PrintServerImpl.START_COMMAND,
                PrintServerImpl.STOP_COMMAND,
                PrintServerImpl.RESTART_COMMAND,
                PrintServerImpl.SETCONFIG_COMMAND,
                PrintServerImpl.READCONFIG_COMMAND
        };
        mAccessControlReader.saveAccessControl(username2, commands2);

        String username3 = "cecilia";
        String[] commands3 = {
                PrintServerImpl.PRINT_COMMAND,
                PrintServerImpl.QUEUE_COMMAND,
                PrintServerImpl.TOPQUEUE_COMMAND,
                PrintServerImpl.RESTART_COMMAND
        };
        mAccessControlReader.saveAccessControl(username3, commands3);

        String username4 = "david";
        String[] commands4 = {
                PrintServerImpl.PRINT_COMMAND,
                PrintServerImpl.QUEUE_COMMAND
        };

        mAccessControlReader.saveAccessControl(username4, commands4);
        String username5 = "erica";
        mAccessControlReader.saveAccessControl(username5, commands4);
        String username6 = "fred";
        mAccessControlReader.saveAccessControl(username6, commands4);
        String username7 = "george";
        mAccessControlReader.saveAccessControl(username7, commands4);
    }

    public boolean userHasAccessRights(String username, String command) {
        String[] accessRights = mAccessControlReader.readAccessControl(username);
        for(String allowedCommand : accessRights) {
            if(allowedCommand.equals(command)) {
                return true;
            }
        }
        return false;
    }



}
