package com.java.oestjacobsen.RoleBasedAccessControl;

public class AccessController {

    private AccessControlReader mAccessControlReader = new AccessControlReader();


    public AccessController() {
        if(!mAccessControlReader.membersFileExist()) {
            addDefaultUsers();
        }
    }

    private void addDefaultUsers() {
        String username1 = "alice";
        mAccessControlReader.saveAcessControlMember(username1, AccessControlReader.ADMIN_ROLE);

        String username2 = "bob";
        mAccessControlReader.saveAcessControlMember(username2, AccessControlReader.SERVICE_ROLE);

        String username3 = "cecilia";
        mAccessControlReader.saveAcessControlMember(username3, AccessControlReader.SUPERUSER_ROLE);

        String username4 = "david";
        mAccessControlReader.saveAcessControlMember(username4, AccessControlReader.USER_ROLE);

        String username5 = "erica";
        mAccessControlReader.saveAcessControlMember(username5, AccessControlReader.USER_ROLE);

        String username6 = "fred";
        mAccessControlReader.saveAcessControlMember(username6, AccessControlReader.USER_ROLE);

        String username7 = "george";
        mAccessControlReader.saveAcessControlMember(username7, AccessControlReader.USER_ROLE);
    }

    public boolean userHasAccessRights(String username, String command) {
        String role = mAccessControlReader.readUserRole(username);
        if(commandIsIncludedInPermissions(command, mAccessControlReader.readRolePermissions(role))) {
            return true;
        }
        return false;
    }

    private boolean commandIsIncludedInPermissions(String command, String[] commands) {
        for(String storedCommand : commands) {
            if(command.equals(storedCommand)) {
                return true;
            }
        }
        return false;
    }



}
