package com.example.loginappv2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class AuthActivity extends AppCompatActivity{
    Button btnVerify;
    EditText etCode;
    String email;
    String response;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Bundle extras = getIntent().getExtras(); // PASS EMAIL THROUGH INTENT
        email = extras.getString("key");
        btnVerify = (Button)findViewById(R.id.btnVerify);
        etCode = (EditText)findViewById(R.id.etCode);


        btnVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inCode = etCode.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    public void run()
                    {
                        try
                        {
                            LoginClient c = new LoginClient("opsys.clarkson.edu",2020);
                            ArrayList<String> helloFrame = c.rframe(); // Receive Hello
                            if(!helloFrame.get(0).equals("HELLO")) { throw new IOException("ERROR"); }
                            String[] typeFrame = {"TYPE","LOGIN"};
                            c.sframe(typeFrame); // send TYPE
                            ArrayList<String> back_typeFrame = c.rframe(); // Receive back TYPE
                            if(!back_typeFrame.get(0).equals("TYPE") || !back_typeFrame.get(1).equals("LOGIN")) { throw new IOException("ERROR"); }
                            String[] codeFrame = {"CODE",email,inCode};
                            c.sframe(codeFrame); // send CODE
                            ArrayList<String> back_codeFrame = c.rframe(); // Receive back CODE
                            if(!back_codeFrame.get(0).equals("CODE")) { throw new IOException("ERROR"); }
                            response = back_codeFrame.get(1);
                            c.close();
                        } catch(SocketTimeoutException e) {response = "Authentication Server Down.";}
                        catch (Exception e) { response = "Unknown Error"; }
                    }});
                thread.start();
                try{ thread.join(); }
                catch(Exception e) {System.out.println("FAILED TO COMPLETE THREAD");}
                ////////////////////////////////////////////////////////
                if(response.equals("VALID")) { auth(); }
                else { authF(); }
            }
        });

    }
    public void auth()
    {
        Intent appIntent = new Intent( this, AppActivity.class );
        this.startActivity( appIntent );
    }
    public void authF()
    {
        Toast.makeText(AuthActivity.this,"Authentication Failed: " + response,Toast.LENGTH_SHORT).show();
    }
}
