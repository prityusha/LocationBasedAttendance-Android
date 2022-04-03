package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class Schedule extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.schedule);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0 );

                        finish();

                        return true;
                    case R.id.schedule:
                        //startActivity(new Intent(getApplicationContext(), Schedule.class));
                        //overridePendingTransition(0,0 );
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0 );

                        finish();

                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hamburger_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.hamHome:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,0 );
                finish();
                return true;
            case R.id.hamSchedule:
                //startActivity(new Intent(getApplicationContext(), Schedule.class));
                //overridePendingTransition(0,0 );

                //finish();
                return true;
            case R.id.hamProfile:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                overridePendingTransition(0,0 );

                finish();

                return true;
            case R.id.hamLogout:
                signOut();
                break;
        }
        return false;
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();
        Intent intent = new Intent(Schedule.this, LoginPage.class);
        startActivity(intent);
        finish();



    }

}