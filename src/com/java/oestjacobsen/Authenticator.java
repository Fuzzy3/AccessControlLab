package com.java.oestjacobsen;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Authenticator {

    private PasswordReader mPasswordReader = new PasswordReader();

    public Authenticator() {
        if(!mPasswordReader.fileExist()) {
            addDefaultUsers();
        }
    }

    private void addDefaultUsers() {
        String username1 = "lars";
        String password1 = "rabbit123";
        byte[] salt1 = generateSalt();
        mPasswordReader.savePassword(username1, salt1, hash(password1.toCharArray(), salt1));

        String username2 = "admin";
        String password2 = "admin";
        byte[] salt2 = generateSalt();
        mPasswordReader.savePassword(username2, salt2, hash(password2.toCharArray(), salt2));

        String username3 = "obi_10_wan";
        String password3 = "imnotthefather";
        byte[] salt3 = generateSalt();
        mPasswordReader.savePassword(username3, salt3, hash(password3.toCharArray(), salt3));
    }

    private byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];

        secureRandom.nextBytes(salt);

        return salt;
    }

    private byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 40000,256);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            spec.clearPassword();
        }
        return null;
    }

    public boolean authenticate(String username, String password) {
        String[] credentials = mPasswordReader.readPassword(username, password);

        if(credentials.length == 0) {
            return false;
        }
        byte[] passwordBytes = Base64.getDecoder().decode(credentials[2]);
        byte[] saltBytes = Base64.getDecoder().decode(credentials[1]);

        if(Arrays.equals(passwordBytes, hash(password.toCharArray(), saltBytes))) {
            return true;
        }

        //String newpass = Base64.getEncoder().encodeToString(hash(password.toCharArray(), saltBytes));
        return false;
    }


}
