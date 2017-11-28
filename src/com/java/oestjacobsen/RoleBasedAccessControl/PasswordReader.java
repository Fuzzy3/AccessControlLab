package com.java.oestjacobsen.RoleBasedAccessControl;

import java.io.*;
import java.util.Base64;

public class PasswordReader {

    private final String mPasswordFileName = "passwords.txt";
    private File mPasswordFile;

    public PasswordReader() {

    }

    public boolean fileExist() {
        mPasswordFile = new File(mPasswordFileName);
        return mPasswordFile.exists();
    }

    public void savePassword(String username, byte[] salt, byte[] password) {
        try (FileWriter fw = new FileWriter(mPasswordFileName, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)){

            String saltString = Base64.getEncoder().encodeToString(salt);
            String passwordString = Base64.getEncoder().encodeToString(password);

            out.println(username + " " + saltString + " " + passwordString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readPassword(String username, String password) {
        try{
            if (fileExist()) {
                BufferedReader br = new BufferedReader(new FileReader(mPasswordFileName));
                for(String line; (line = br.readLine()) != null;) {
                    String[] usernameSaltPassword = line.split("\\s+");
                    if(usernameSaltPassword[0].equals(username)) {
                        return usernameSaltPassword;
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }


}
