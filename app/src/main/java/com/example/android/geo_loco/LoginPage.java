package com.example.android.geo_loco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign In");

        //buuton for login activity
        Button buttonLogin = findViewById(R.id.loginbutton);
        //button for signup page
        Button buttonRegister = findViewById(R.id.signuppage);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this , MainActivity.class);
                startActivity(intent);
            }
        });


        // intent for the registration activity
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this , registeration_page.class);
                startActivity(intent);
            }
        });
    }
}