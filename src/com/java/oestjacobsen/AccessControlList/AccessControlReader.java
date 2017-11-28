package com.java.oestjacobsen.AccessControlList;

import java.io.*;
import java.util.Arrays;

public class AccessControlReader {

    private final String mAccessControlFileName = "accesscontrollist.txt";
    private File mAccessControlFile;


    public AccessControlReader() {
    }

    public boolean fileExist() {
        mAccessControlFile = new File(mAccessControlFileName);
        return mAccessControlFile.exists();
    }

    public void saveAccessControl(String username, String[] commands) {
        try (FileWriter fw = new FileWriter(mAccessControlFileName, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)){

            StringBuilder builder = new StringBuilder();
            builder.append(username);
            builder.append(" ");
            for(String command : commands) {
                builder.append(command);
                builder.append(" ");
            }

            out.println(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readAccessControl(String username) {
        try{
            if (fileExist()) {
                BufferedReader br = new BufferedReader(new FileReader(mAccessControlFileName));
                for(String line; (line = br.readLine()) != null;) {
                    String[] userAccessControl = line.split("\\s+");
                    if(userAccessControl[0].equals(username)) {
                        return Arrays.copyOfRange(userAccessControl, 1,userAccessControl.length);
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }



}
