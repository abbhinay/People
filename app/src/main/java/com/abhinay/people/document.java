package com.abhinay.people;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class document extends AppCompatActivity {

    Button uploadButton;
    Uri pdfUri; //Uri are actually URLs that are meant for local storage
    ProgressDialog mProgressDialog;

    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseFirestore db;
    RecyclerView recyclerView;

    String displayName = null;
    String urlToDatabase;
    String fileName1;

    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

         db = FirebaseFirestore.getInstance();

        uploadButton = (Button) findViewById(R.id.uploadButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(recyclerView, getApplicationContext(), new ArrayList<String>(), new ArrayList<String>());
        recyclerView.setAdapter(myAdapter);


        //final DocumentReference docRef = db.collection("documents").document("SF");

        db.collection("documents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String fileName = document.getId();
                                String url = document.getData().get(fileName).toString();
                                ((MyAdapter)recyclerView.getAdapter()).update(fileName, url);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(pdfUri==null){
                    if(ContextCompat.checkSelfPermission(document.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        selectPdf();
                    }else{
                        ActivityCompat.requestPermissions(document.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                    }
                }
                else{
                    uploadFile(pdfUri);
                    pdfUri = null;
                }

            }
        });


    }

    private void uploadFile(Uri pdfUri) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("Uploading file...");
        mProgressDialog.setProgress(0);
        mProgressDialog.show();

        fileName = displayName;
        //fileName = System.currentTimeMillis()+".pdf";
//        String[] name = displayName.split(".");
        fileName1 = displayName.replaceAll("[-+.^:,_]","");
        //final String fileName1 = System.currentTimeMillis()+"";
        final StorageReference storageReference = storage.getReference(); //returns root path
        storageReference.child("Uploads").child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    getUrl();


                Log.d("fileName12", "after getUrl");
                //uploadToFirestore(fileName1);

//                    if(urlToDatabase!=null){
//
//                    }else{
//                        getUrl();
//                        uploadToFirestore(fileName1);
//                    }


//                DatabaseReference reference = database.getReference(); //return the path to root
//
//                reference.child(fileName1).setValue(urlToDatabase).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            Toast.makeText(document.this, "flile successfully uploaded", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(document.this, "flile not uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });










            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(document.this, "file not uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                mProgressDialog.setProgress(currentProgress);
            }
        });
    }

    private void uploadToFirestore(){
        DocumentReference docRef = db.collection("documents").document(fileName1);
        Map<String, String> data = new HashMap<>();
        data.put(fileName1, urlToDatabase);
        docRef.set(data);
        urlToDatabase = null;

        onBackPressed();

    }


    private void getUrl() {
        Log.d("fileName12", "inside getUrl");
        final StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads/"+fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urlToDatabase = uri.toString();
                uploadToFirestore();

                //Log.d("fileName12", urlToDatabase);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("fileName12", "inside failure");
                getUrl();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }else{
            Toast.makeText(document.this, "please provide permission to access storage...", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==86 && resultCode==RESULT_OK && data!=null){
            pdfUri=data.getData(); // return the uri of selected file.
            Log.d("fileName", pdfUri.toString());


            String uriString = pdfUri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();


            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(pdfUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("fileName", displayName);
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }


        }else{
            Toast.makeText(document.this, "please select a file", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(document.this, homeActivity.class);
        startActivity(intent);
    }
}
