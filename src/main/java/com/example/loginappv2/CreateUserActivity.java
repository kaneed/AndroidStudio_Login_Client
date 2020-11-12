package com.example.loginappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateUserActivity extends AppCompatActivity {
    Button btnOk;
    Button btnCancel;
    EditText etEmail;
    EditText etPass;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPassword);

        btnOk.setOnClickListener(new OkHandler());
        btnCancel.setOnClickListener(new CancelHandler());
    }

    private class OkHandler implements View.OnClickListener {
        public boolean isValid(String em,String pw) {
            Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = emailPattern.matcher(em);
            if(!matcher.find()) { return false; }
            if(pw.length() == 0) { return false; }
            return true;
        }
        public void onClick( View v ) {
            final String email = etEmail.getText().toString();
            final String password = etPass.getText().toString();
            if (!isValid(email,password))
                Toast.makeText(CreateUserActivity.this,"Error; Invalid entries",Toast.LENGTH_SHORT).show();
            else {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            LoginClient c = new LoginClient("opsys.clarkson.edu", 2020);
                            ArrayList<String> helloFrame = c.rframe(); // Receive HELLO
                            if (!helloFrame.get(0).equals("HELLO")) {
                                throw new IOException("ERROR");
                            }
                            String[] typeFrame = {"TYPE", "LOGIN"};
                            c.sframe(typeFrame); // send TYPE
                            ArrayList<String> back_typeFrame = c.rframe(); // Receive back TYPE
                            if (!back_typeFrame.get(0).equals("TYPE") || !back_typeFrame.get(1).equals("LOGIN")) {
                                throw new IOException("ERROR");
                            }
                            String[] regFrame = {"REGISTER", email, password};
                            c.sframe(regFrame); // send REGISTER
                            ArrayList<String> back_regFrame = c.rframe(); // Receive back REGISTER
                            if (!back_regFrame.get(0).equals("REGISTER")) {
                                throw new IOException("ERROR");
                            }
                            response = back_regFrame.get(1);
                            c.close();
                        } catch (SocketTimeoutException e) {
                            response = "Authentication Server Timeout.";
                        } catch (Exception e) {
                            response = "Authentication Server Error";
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (Exception e) {
                    System.out.println("FAILED TO COMPLETE THREAD");
                    response = "Unknown Error";
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("response",response);
                returnIntent.putExtra("email",email);
                returnIntent.putExtra("password",password);
                setResult(CreateUserActivity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    private class CancelHandler implements View.OnClickListener {
        public void onClick( View v ) {
            finish();
        }
    }
}