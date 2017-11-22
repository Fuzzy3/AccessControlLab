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
        String username1 = "alice";
        String password1 = "alice";
        byte[] salt1 = generateSalt();
        mPasswordReader.savePassword(username1, salt1, hash(password1.toCharArray(), salt1));

        String username2 = "bob";
        String password2 = "bob";
        byte[] salt2 = generateSalt();
        mPasswordReader.savePassword(username2, salt2, hash(password2.toCharArray(), salt2));

        String username3 = "cecilia";
        String password3 = "cecilia";
        byte[] salt3 = generateSalt();
        mPasswordReader.savePassword(username3, salt3, hash(password3.toCharArray(), salt3));

        String username4= "david";
        String password4 = "david";
        byte[] salt4 = generateSalt();
        mPasswordReader.savePassword(username4, salt4, hash(password4.toCharArray(), salt4));

        String username5= "erica";
        String password5 = "erica";
        byte[] salt5 = generateSalt();
        mPasswordReader.savePassword(username5, salt5, hash(password5.toCharArray(), salt5));

        String username6= "fred";
        String password6 = "fred";
        byte[] salt6 = generateSalt();
        mPasswordReader.savePassword(username6, salt6, hash(password6.toCharArray(), salt6));

        String username7= "george";
        String password7 = "george";
        byte[] salt7 = generateSalt();
        mPasswordReader.savePassword(username7, salt7, hash(password7.toCharArray(), salt7));
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
