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
        mAccessControlReader.saveAcessControlMember(username1, new String[]{AccessControlReader.ADMIN_ROLE});

        String username2 = "george";
        mAccessControlReader.saveAcessControlMember(username2, new String[]{AccessControlReader.USER_ROLE, AccessControlReader.SERVICE_ROLE});

        String username3 = "cecilia";
        mAccessControlReader.saveAcessControlMember(username3, new String[]{AccessControlReader.SUPER_ROLE, AccessControlReader.USER_ROLE});

        String username4 = "david";
        mAccessControlReader.saveAcessControlMember(username4, new String[]{AccessControlReader.USER_ROLE});

        String username5 = "erica";
        mAccessControlReader.saveAcessControlMember(username5, new String[]{AccessControlReader.USER_ROLE});

        String username6 = "fred";
        mAccessControlReader.saveAcessControlMember(username6, new String[]{AccessControlReader.USER_ROLE});

        String username7 = "henry";
        mAccessControlReader.saveAcessControlMember(username7, new String[]{AccessControlReader.USER_ROLE});

        String username8 = "ida";
        mAccessControlReader.saveAcessControlMember(username8, new String[]{AccessControlReader.USER_ROLE, AccessControlReader.SUPER_ROLE});
    }

    public boolean userHasAccessRights(String username, String command) {
        String[] roles = mAccessControlReader.readUserRoles(username);
        for(String role : roles) {
            if(commandIsIncludedInPermissions(command, mAccessControlReader.readRolePermissions(role))) {
                return true;
            }
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
