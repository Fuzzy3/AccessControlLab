package com.java.oestjacobsen.RoleBasedAccessControl;

import java.io.*;
import java.rmi.ServerError;
import java.util.Arrays;

public class AccessControlReader {

    public static final String ADMIN_ROLE = "admin";
    public static final String SERVICE_ROLE = "service";
    public static final String SUPERUSER_ROLE = "superuser";
    public static final String USER_ROLE = "user";

    private String[] mRoles = new String[4];

    private final String mAccessControlRolesFilename = "accesscontrolroles.txt";
    private File mAccessControlRolesFile;

    private final String mAccessControlMembersFilename = "accesscontrolmembers.txt";
    private File mAccessControlMembersFile;


    public AccessControlReader() {
        if(!roleFileExist()) {
            defineRoles();
        }
        fillRoles();
    }

    private void fillRoles() {
        mRoles[0] = ADMIN_ROLE;
        mRoles[1] = SERVICE_ROLE;
        mRoles[2] = SUPERUSER_ROLE;
        mRoles[3] = USER_ROLE;
    }

    public boolean roleFileExist() {
        mAccessControlRolesFile = new File(mAccessControlRolesFilename);
        return mAccessControlRolesFile.exists();
    }

    public boolean membersFileExist() {
        mAccessControlMembersFile = new File(mAccessControlMembersFilename);
        return mAccessControlMembersFile.exists();
    }

    private void defineRoles() {

        String[] adminPolicy = {
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
        saveAccessControlRole(ADMIN_ROLE, adminPolicy);

        String[] servicePolicy = {
                PrintServerImpl.START_COMMAND,
                PrintServerImpl.STOP_COMMAND,
                PrintServerImpl.RESTART_COMMAND,
                PrintServerImpl.SETCONFIG_COMMAND,
                PrintServerImpl.READCONFIG_COMMAND
        };
        saveAccessControlRole(SERVICE_ROLE, servicePolicy);

        String[] superUserPolicy = {
                PrintServerImpl.PRINT_COMMAND,
                PrintServerImpl.QUEUE_COMMAND,
                PrintServerImpl.TOPQUEUE_COMMAND,
                PrintServerImpl.RESTART_COMMAND
        };
        saveAccessControlRole(SUPERUSER_ROLE, superUserPolicy);

        String[] userPolicy = {
                PrintServerImpl.PRINT_COMMAND,
                PrintServerImpl.QUEUE_COMMAND
        };
        saveAccessControlRole(USER_ROLE, userPolicy);
    }

    private boolean roleExist(String role) {
        if(role.equals(""))
        {
            return false;
        }
        for(String definedRole : mRoles) {
            if(definedRole.equals(role)) {
                return true;
            }
        }
        return false;
    }


    public void saveAcessControlMember(String username, String role) {
        try (FileWriter fw = new FileWriter(mAccessControlMembersFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)){

            StringBuilder builder = new StringBuilder();
            builder.append(username);
            builder.append(" ");
            builder.append(role);

            out.println(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAccessControlRole(String role, String[] permissions) {
        try (FileWriter fw = new FileWriter(mAccessControlRolesFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)){

            StringBuilder builder = new StringBuilder();
            builder.append(role);
            builder.append(" ");
            for(String permission : permissions) {
                builder.append(permission);
                builder.append(" ");
            }

            out.println(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readRolePermissions(String role) {
        try{
            if (roleFileExist()) {
                if(roleExist(role)) {
                    BufferedReader br = new BufferedReader(new FileReader(mAccessControlRolesFile));
                    for (String line; (line = br.readLine()) != null; ) {
                        String[] rolePermissions = line.split("\\s+");
                        if (rolePermissions[0].equals(role)) {
                            return Arrays.copyOfRange(rolePermissions, 1, rolePermissions.length);
                        }
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public String readUserRole(String username) {
        try{
            if (membersFileExist()) {
                BufferedReader br = new BufferedReader(new FileReader(mAccessControlMembersFile));
                for (String line; (line = br.readLine()) != null; ) {
                    String[] userRole = line.split("\\s+");
                    if (userRole[0].equals(username)) {
                        return userRole[1];
                    }
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
