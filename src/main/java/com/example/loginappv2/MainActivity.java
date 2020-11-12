package com.example.loginappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnOk;
    Button btnAdd;
    EditText etEmail;
    EditText etPass;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPassword);

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreateUserActivity();
            }
        });

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                user = new User(email,password);
                int n = user.verify();
                if(n == 1) {success();}
                else {failure(n);}
            }
        });
    }
    public void success()
    {
        Intent authIntent = new Intent( MainActivity.this, AuthActivity.class );
        authIntent.putExtra("key",user.getEmail());
        this.startActivity( authIntent );
    }
    public void failure(int reason)
    {
        switch(reason)
        {
            case 2:
                Toast.makeText(MainActivity.this,"Login Failed: Authentication Server Down.",Toast.LENGTH_SHORT).show();
                break;
            case 0:
                Toast.makeText(MainActivity.this,"Login Failed: Invalid Credentials.",Toast.LENGTH_SHORT).show();
            default:
                Toast.makeText(MainActivity.this,"Login Failed: Authentication Server Error.",Toast.LENGTH_SHORT).show();
        }
    }

    public void launchCreateUserActivity() {
        Intent intent = new Intent(this,CreateUserActivity.class);
        this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == 1) {
            super.onActivityResult(requestCode, resultCode, i);
            if (resultCode == CreateUserActivity.RESULT_OK) {
                String response = i.getStringExtra("response");
                String tempe = i.getStringExtra("email");
                String tempp = i.getStringExtra("password");
                if(response.equals("VALID")) {
                    Toast.makeText(MainActivity.this, "Create Account Successful", Toast.LENGTH_SHORT).show();
                    etEmail.setText(tempe);
                    etPass.setText(tempp);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Create Account Failed: " + response, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}