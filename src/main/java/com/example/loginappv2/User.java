package com.example.loginappv2;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    int valid = -1;
    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public int verify()
    {
        Thread thread = new Thread(new Runnable() {
            public void run()
            {
                try
                {
                    LoginClient c = new LoginClient("opsys.clarkson.edu",2020);
                    ArrayList<String> helloFrame = c.rframe(); // Receive Hello
                    String[] typeFrame = {"TYPE","LOGIN"};
                    c.sframe(typeFrame); // send TYPE
                    ArrayList<String> back_typeFrame = c.rframe(); // Receive back TYPE
                    String[] loginFrame = {"LOGIN",email,password};
                    c.sframe(loginFrame);
                    ArrayList<String> back_loginFrame = c.rframe(); // Receive back LOGIN
                    if(back_loginFrame.get(1).equals("VALID")) { valid = 1; }
                    else { valid = 0; }
                    c.close();

                } catch (SocketTimeoutException e) { valid = 2; }
                catch(Exception e) { valid = 3; }
            }});
        thread.start();
        try{ thread.join(); }
        catch(Exception e) {System.out.println("FAILED TO COMPLETE THREAD");}
        return valid;
    }
}
