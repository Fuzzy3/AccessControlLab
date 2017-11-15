package com.java.oestjacobsen;

import sun.java2d.cmm.CMMServiceProvider;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static PrintServer mServer;
    private static String mUsername = "";

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        //Connect to server
        mServer = (PrintServer) Naming.lookup("rmi://localhost:5099/printserver");


        //Read from input
        Scanner sc = new Scanner(System.in);
        String input;
        System.out.println("Specify printer command...");
        while(true) {
            input = sc.nextLine();
            if(input.equals("quit")) {
                 System.out.println("Client exitting...");
                break;
            }
            String response = parseCommand(input);
            if(response.equals(PrintServerImpl.AUTH_TESTER)) {
                 authenticate();
            } else {
                System.out.println(response);
            }
            System.out.println("");
        }
    }

    private static String parseCommand(String input) {
        String[] commands = input.split("\\s+");
        try {
            switch (commands[0]) {
                case ("start"): {
                    return mServer.start();
                }
                case ("stop"): {
                    return mServer.stop(mUsername);
                }
                case ("print"): {
                    if(commands.length >= 3) {
                        return mServer.print(commands[1], commands[2], mUsername);
                    }
                    return "print requires two arguments: print filename printer";
                }
                case ("queue"): {
                    return mServer.queue(mUsername);
                }
                case ("topqueue") : {
                    if(commands.length >= 2) {
                        return mServer.topQueue(Integer.valueOf(commands[1]), mUsername);
                    }
                    return "topqueue requires 1 argument: topqueue [jobnumber]";
                }
                case ("restart") : {
                    return mServer.restart(mUsername);
                }
                case("status") : {
                    return mServer.status(mUsername);
                }
                case ("setconfig") : {
                    if(commands.length >= 3) {
                        return mServer.setConfig(commands[1], commands[2], mUsername);
                    }
                    return "setconfig requires 2 arguments: setconfig colors false";
                }
                case ("readconfig") : {
                    if(commands.length >= 2) {
                        return mServer.readConfig(commands[1], mUsername);
                    }
                    return "readconfig requires 1 argument: readconfig colors";
                }
                case ("auth") : {
                    if(commands.length >= 3) {
                        mUsername = commands[1];
                        return mServer.authenticate(commands[1], commands[2]);
                    }
                    return "auth requires 2 arguments: auth username password";
                }
            }
        } catch (Exception e) {
            System.out.println("Internal server exception: " + e.toString());
            e.printStackTrace();
        }

        return "Couldn't understand the command: " + input;
    }

    private static void authenticate() {
        Scanner sc = new Scanner(System.in);
        String username, password;
        System.out.println("Log in is needed");
        System.out.println("Enter username:");
        username = sc.nextLine().trim();
        while(username.length() == 0) {
            System.out.println("Username cannot be empty, try again!");
            username = sc.nextLine().trim();
        }
        System.out.println("Enter password:");
        password = sc.nextLine().trim();
        while(password.length() == 0) {
            System.out.println("Password cannot be empty, try again!");
            password = sc.nextLine().trim();
        }
        try {
            mUsername = username;
            System.out.println(mServer.authenticate(username, password));
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }


}
