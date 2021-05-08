package com.example.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ClubFormActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 3;
    private EditText clubNameEt, clubBranchEt, clubIntroEt;
    private Button submitBtn;
    private TextView changeDpTv;
    private ImageView clubImage;

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

        changeDpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                clubImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}