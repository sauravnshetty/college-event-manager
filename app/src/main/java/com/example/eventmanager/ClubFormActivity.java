package com.example.eventmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventmanager.model.Club;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ClubFormActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 3;
    private static final String TAG = "ClubformActivity";
    private EditText clubNameEt, clubBranchEt, clubIntroEt;
    private Button submitBtn;
    private TextView changeDpTv;
    private ImageView clubImage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_form);

        clubNameEt = findViewById(R.id.clubNameEt);
        clubBranchEt = findViewById(R.id.clubBranchEt);
        clubIntroEt = findViewById(R.id.clubIntroEt);

        submitBtn = findViewById(R.id.submitBtn);
        changeDpTv = findViewById(R.id.changeImageBtn);

        clubImage = findViewById(R.id.clubImage);

        Club club = (Club)getIntent().getSerializableExtra("club");
        if(club != null)
            showClubDetailsInUI(club);

        changeDpTv.setOnClickListener(view -> startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY));

        submitBtn.setOnClickListener(view -> {
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("clubs");

            if(!isAllFilled()) {
                Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            String clubId;
            if(club == null) {
                //Add a new club
                clubId = mDatabaseReference.push().getKey();
            }else {
                //Edit information for an existing club
                clubId = club.getClubId();
            }
            uploadFile(clubId);
            Club newClub = new Club(clubNameEt.getText().toString(), clubBranchEt.getText().toString(),
                    clubIntroEt.getText().toString());

            //This was a major bug ^_^ , every time clubId of the model class used to be null!!!
            newClub.setClubId(clubId);

            assert clubId != null;
            mDatabaseReference.child(clubId).setValue(newClub);
            Toast.makeText(getApplicationContext(), "Club added, successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private boolean isAllFilled() {
        boolean filled = true;
        if(clubNameEt.getText().toString().trim().equals(""))
            filled = false;
        else if(clubBranchEt.getText().toString().trim().equals(""))
            filled = false;
        else if(clubIntroEt.getText().toString().trim().equals(""))
            filled = false;

        return filled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Glide.with(getApplicationContext()).load(String.valueOf(imageUri)).centerCrop().into(clubImage);
                clubImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Image: " + imageUri.toString());
        }
    }

    private void showClubDetailsInUI(Club club) {
        clubNameEt.setText(club.getName());
        clubBranchEt.setText(club.getBranch());
        clubIntroEt.setText(club.getIntroText());
        submitBtn.setText("Save");
        StorageReference clubImagesRef = FirebaseStorage.getInstance().getReference().child("clubImages");
        clubImagesRef.child(club.getClubId()).getDownloadUrl().addOnSuccessListener(
                uri -> Glide.with(getApplicationContext()).load(String.valueOf(uri)).centerCrop().into(clubImage))
        .addOnFailureListener(e -> Log.d(TAG, "Failed to get club image"));
    }


    private void uploadFile(String fileName) {
        final ProgressDialog progressDialog = new ProgressDialog(getBaseContext());
        progressDialog.setTitle("Uploading");

        if (imageUri != null){
            StorageReference  fileReference  = FirebaseStorage.getInstance().getReference().child("clubImages/"+fileName);

            UploadTask uploadTask = fileReference.putFile(imageUri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {

                        //progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed Successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "failed to upload");
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setMessage("Uploaded  " +(int)progress+"%");
                    });
        }
        else
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
    }
}