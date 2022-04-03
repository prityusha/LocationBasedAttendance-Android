package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public class Profile extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private TextView tvname,tvenroll,tvbatch,tvgender;
    private ProgressBar progressBar;
    String fullName, Enrollment, Batch, Gender;
    private ImageView img;
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");

        // bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);

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
                        startActivity(new Intent(getApplicationContext(), Schedule.class));
                        overridePendingTransition(0,0 );

                        finish();

                        return true;
                    case R.id.profile:
                        //startActivity(new Intent(getApplicationContext(), Profile.class));
                        //overridePendingTransition(0,0 );
                        return true;
                }
                return false;
            }
        });

        tvname=findViewById(R.id.name);
        tvbatch=findViewById(R.id.batch);
        tvenroll=findViewById(R.id.enroll);
        tvgender=findViewById(R.id.gender);
        progressBar=findViewById(R.id.progress_bar_profile_page);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(this, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_SHORT).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("registeredUsers");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ReadWriteUserDetail readWriteUserDetail=snapshot.getValue(ReadWriteUserDetail.class);
                if(readWriteUserDetail!=null){
                    fullName = readWriteUserDetail.textFullName;
                    Enrollment = readWriteUserDetail.textEnrollment;
                    Batch = readWriteUserDetail.textBatch;
                    Gender = readWriteUserDetail.textGender;

                    tvname.setText(fullName);
                    tvenroll.setText(Enrollment);
                    tvbatch.setText(Batch);
                    tvgender.setText(Gender);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(Profile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

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
                startActivity(new Intent(getApplicationContext(), Schedule.class));
                overridePendingTransition(0,0 );

                finish();
                return true;
            case R.id.hamProfile:
                //startActivity(new Intent(getApplicationContext(), Profile.class));
                //overridePendingTransition(0,0 );

                //finish();

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
        Intent intent = new Intent(Profile.this, LoginPage.class);
        startActivity(intent);
        finish();



    }

}