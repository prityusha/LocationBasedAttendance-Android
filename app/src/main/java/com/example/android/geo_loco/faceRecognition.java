package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class faceRecognition extends AppCompatActivity {

    public static final int cameraCode = 102;
    ImageView img;
    Button camera;

    final int cameraPermissionCode=999;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference root;
    private DatabaseReference referenceBaseImageUrl;
    private DatabaseReference referenceCheckPointImageUrl;
    private String baseUrl;
    private String checkPointUrl;
    private String url = "http://127.0.0.1:5000";
    List<String>imagesUrlList;
    byte bb[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        img=(ImageView) findViewById(R.id.capturedimage);
        camera = (Button) findViewById(R.id.cameraButton);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        root = FirebaseDatabase.getInstance().getReference("clickedImage");
        referenceBaseImageUrl = FirebaseDatabase.getInstance().getReference().child("baseImage");
        imagesUrlList=new ArrayList<>();


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });
    }

    private void askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},cameraPermissionCode);

        }
        else {
            openCamera();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == cameraPermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //request granted
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission Required to use camera!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void openCamera() {
        //Toast.makeText(this, "Clicked Camrea Button", Toast.LENGTH_SHORT).show();
        Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Camera, cameraCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==cameraCode){
            Bitmap clickedImage = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            clickedImage.compress(Bitmap.CompressFormat.JPEG,90,bytes);
            bb = bytes.toByteArray();
            img.setImageBitmap(clickedImage);


            readData(new FirebaseCallback() {
                @Override
                public void onCallback() {

                    readData1(new FirebaseCallback1() {
                        @Override
                        public void onCallback1() {
                            final boolean[] result = new boolean[1];
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String data = jsonObject.getString("verified");
                                        if(data=="true"){
                                            result[0] =true;
                                        }
                                        else{
                                            result[0]=false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("AskPermission","Error in API call"+error.getMessage());
                                            //Toast.makeText(faceRecognition.this, "Error in API call"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }){
                                @Override
                                protected Map<String,String> getParams(){
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("baseUrl", baseUrl);
                                    params.put("checkPointUrl",checkPointUrl);
                                    return params;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(faceRecognition.this);
                            queue.add(stringRequest);
                            //startActivity(new Intent(faceRecognition.this , MainActivity.class));
                            if(result[0]){
                                startActivity(new Intent(faceRecognition.this , MainActivity.class));
                            }
                            else{
                                Toast.makeText(faceRecognition.this, "Face did not match. Please Try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });



        }
    }

    /*private boolean getFacialRecognitionResult() {

        final boolean[] result = new boolean[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("verified");
                    if(data=="true"){
                        result[0] =true;
                    }
                    else{
                        result[0]=false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AskPermission","Error in API call"+error.getMessage());
                        //Toast.makeText(faceRecognition.this, "Error in API call"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("baseUrl", baseUrl);
                params.put("checkPointUrl",checkPointUrl);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(faceRecognition.this);
        queue.add(stringRequest);
        return result[0];
    }*/

    private void readData(FirebaseCallback firebaseCallback){
        StorageReference ref = storageReference.child("myImages/a.jpg");
        ref.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString());
                        String modelId=root.push().getKey();
                        root.child(modelId).setValue(model);

                        Log.d("AskPermission","Image Uploaded");
                        firebaseCallback.onCallback();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(faceRecognition.this, "FaceRecognition Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private interface FirebaseCallback{
        void onCallback();
    }

    private void readData1(FirebaseCallback1 firebaseCallback){
        referenceCheckPointImageUrl = FirebaseDatabase.getInstance().getReference().child("clickedImage");
        Query query=referenceCheckPointImageUrl.orderByKey().limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot datas: snapshot.getChildren()){
                    checkPointUrl = datas.getValue().toString();
                    Log.d("AskPermission","CheckPoint Image URL"+checkPointUrl);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d("AskPermission","Error in checkPoint Image URL creation");
            }
        });



        referenceBaseImageUrl = FirebaseDatabase.getInstance().getReference().child("registeredUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        referenceBaseImageUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                baseUrl = String.valueOf(snapshot.child("BASE_IMG_URL").getValue());
                Log.d("AskPermission","Base Image URL"+baseUrl);
                firebaseCallback.onCallback1();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(faceRecognition.this, "Failed to retreive base image", Toast.LENGTH_LONG).show();
            }
        });
    }
    private interface FirebaseCallback1{
        void onCallback1();
    }


}

