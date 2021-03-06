package com.tobe.talyeh3.myapplication.Posts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tobe.talyeh3.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etTitle,etBody,etSubtitle;
    Button btnSave;
    FirebaseDatabase database;
    DatabaseReference postRef;

    String key;
    Post p;


    ImageView imgProfile;
    String generatedFilePath = "https://firebasestorage.googleapis.com/v0/b/tobe-722db.appspot.com/o/images%2Fnewside.png?alt=media&token=35abdf61-7b54-41fc-b27d-20894393b393";
    Button btnChoose;
    private static final int PICK_IMAGE_REQUEST = 234;
    private StorageReference mStorageRef;
    private Uri filePath;
    Boolean b = true;//if the user uploded photo
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_add_post);
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        etTitle = (EditText) findViewById(R.id.etTitle);
        etSubtitle = (EditText) findViewById(R.id.etSubtitle);
        etBody = (EditText) findViewById(R.id.etBody);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog( this );
        imgProfile = (ImageView) findViewById( R.id.imgProfile );
        btnChoose = (Button) findViewById( R.id.btnChoose );
        btnChoose.setOnClickListener(this);
    }









    private void uploadFile(final Post p) {

        if (filePath != null) {
            b=false;
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference riversRef = mStorageRef.child("images/"+p.key+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();


                            Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                            p.imgUrl= downloadUri.toString();
                            postRef.setValue( p );
                            Intent intent = new Intent( AddPostActivity.this, AllPostActivity.class );
                            intent.putExtra( "teamKey", p.key );
                            startActivity( intent );
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int)progress)+"% Uploaded...");
                        }
                    })
            ;
        }
    }


    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"),PICK_IMAGE_REQUEST);


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST &&resultCode==RESULT_OK&&data!=null && data.getData()!=null)
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }










    @Override
    public void onClick(View v) {
        if (v == btnSave)
        {
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy, hh:mm a");
            String dateString = sdf.format(date);

            String uid = FirebaseAuth.getInstance().getCurrentUser().toString();
        Post p = new Post( uid, etTitle.getText().toString(), etBody.getText().toString(), 0, "", generatedFilePath,etSubtitle.getText().toString(), dateString );
        postRef = database.getReference( "Posts" ).push();
        p.key = postRef.getKey();
        uploadFile( p );
        if(b==true) {
            postRef.setValue( p );
            finish();
        }
    }
        else if(v==btnChoose)
        {
            showFileChooser();
        }
    }
}
