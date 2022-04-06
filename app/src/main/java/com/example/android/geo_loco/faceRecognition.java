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

import java.io.ByteArrayOutputStream;

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
            byte bb[] = bytes.toByteArray();
            img.setImageBitmap(clickedImage);




            uploadPicture(bb);

            referenceCheckPointImageUrl = FirebaseDatabase.getInstance().getReference().child("clickedImage");
            Query query=referenceCheckPointImageUrl.orderByKey().limitToFirst(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot datas: snapshot.getChildren()){
                        checkPointUrl = datas.getValue().toString();
                        Log.d("AskPermission",checkPointUrl);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            referenceBaseImageUrl = FirebaseDatabase.getInstance().getReference().child("registeredUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            referenceBaseImageUrl.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    baseUrl = String.valueOf(snapshot.child("BASE_IMG_URL").getValue());
                    Log.d("AskPermission",baseUrl);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(faceRecognition.this, "Failed to retreive base image", Toast.LENGTH_LONG).show();
                }
            });

            /*referenceBaseImageUrl.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {

                        DataSnapshot snapshot = task.getResult();
                        baseUrl = String.valueOf(snapshot.child("IMG_URL").getValue());
                        Log.d("AskPermission",baseUrl);
                    } else {
                        Toast.makeText(faceRecognition.this, "Failed to retreive base image", Toast.LENGTH_LONG).show();
                    }

                }
            });*/
            /*Query query1=referenceBaseImageUrl.orderByKey().limitToFirst(1);
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot datas: snapshot.getChildren()){
                        baseUrl = datas.getValue().toString();
                        Log.d("AskPermission",baseUrl);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });*/


            startActivity(new Intent(faceRecognition.this , MainActivity.class));

            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.id.capturedimage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);*/
        }
    }

    private void uploadPicture(byte[] bb) {

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


}