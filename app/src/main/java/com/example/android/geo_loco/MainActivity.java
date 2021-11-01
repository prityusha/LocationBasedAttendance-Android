package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    final int Access_location_request_code = 10001001;
    private static final String TAG = "AskPermission";
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView textViewLat;
    private TextView textViewLong;
    private Button updateButton;
    private String variableLatitude;
    private String variableLongitude;
    private UserLocation mUserLocation;

    private void getUserDetail(){
        if(mUserLocation==null){
            mUserLocation=new UserLocation();
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("registeredUsers");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this,"User Data Retreived",Toast.LENGTH_SHORT).show();
                        DataSnapshot snapshot=task.getResult();

                        String textFullName=String.valueOf(snapshot.child("textFullName").getValue());
                        String enroll=String.valueOf(snapshot.child("textEnrollment").getValue());
                        String dob=String.valueOf(snapshot.child("textDob").getValue());
                        String gender=String.valueOf(snapshot.child("textGender").getValue());
                        String batch=String.valueOf(snapshot.child("textBatch").getValue());


                        ReadWriteUserDetail user=new ReadWriteUserDetail(textFullName,enroll,dob,gender,batch);
                        mUserLocation.setUserLatitude(variableLatitude.toString());
                        mUserLocation.setUserLongitude(variableLongitude.toString());
                        mUserLocation.setUserDetail(user);
                        saveUSerLocation();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Failed to user Info!",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        else{
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("UserLocation");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLatitude").setValue(variableLatitude);
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userLongitude").setValue(variableLongitude);
            //saveUSerLocation();
        }
    }

    private void saveUSerLocation(){
        if(mUserLocation!=null){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("UserLocation");
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"User Location Added", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Failed to add", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            //super.onLocationResult(locationResult);
            if(locationResult==null){
                return;
            }
            for(Location location:locationResult.getLocations()){
                Log.d(TAG, "onLocation Result" + location.toString());



                variableLatitude=Double.toString(location.getLatitude());
                variableLongitude=Double.toString(location.getLongitude());





                textViewLat.setText(Double.toString(location.getLatitude()));
                textViewLong.setText(Double.toString(location.getLongitude()));



            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("registeredUsers");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot= task.getResult();

                    String user_name=String.valueOf(dataSnapshot.child("textFullName").getValue());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(user_name);
                }
            }
        });


        textViewLat=findViewById(R.id.textView2);
        textViewLong=findViewById(R.id.textView3);
        updateButton=findViewById(R.id.buttonview);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getUserDetail();

                //databaseReference.child("Latitude").push().setValue(variableLatitude.toString());
                //databaseReference.child("Longitude").push().setValue(variableLongitude.toString());




                //ref.child(firebaseUser.getUid()).child("Latitude").setValue(value_lat);
                //ref.child(firebaseUser.getUid()).child("Longitude").setValue(value_lng);

                Toast.makeText(MainActivity.this,"Clicked!",Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Location");



        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }


    protected void onStart() {

        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLastLocation();
            checkSettingsAndStartLocationUpdate();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    private void checkSettingsAndStartLocationUpdate() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(MainActivity.this).checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //settings are satisfied and we can start location update
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MainActivity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    private void stopLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    /*private void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "On Success: " + location.toString());
                        Log.d(TAG, "On Success: " + location.getLatitude());
                        Log.d(TAG, "On Success: " + location.getLongitude());
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "On Success: Location is NULL");
                        Toast.makeText(MainActivity.this, "Success: Location is NULL", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            fusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "on Failure" + e.getLocalizedMessage());
                    Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }*/

    private void askLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG,"Ask Permission, You should show an alert Dialog!");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Access_location_request_code);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Access_location_request_code);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==Access_location_request_code){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //request granted
                //getLastLocation();
                checkSettingsAndStartLocationUpdate();
            }
            else{

            }
        }
    }
}