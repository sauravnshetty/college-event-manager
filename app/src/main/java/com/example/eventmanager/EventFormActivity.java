package com.example.eventmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class EventFormActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 3;
    private static final String TAG = "EventFormActivity";
    private EditText  eventNameEt, eventVenueEt,eventDescriptionEt,eventOrganizerEt;
    private Button submitBtn;
    private TextView changeDpTv,eventDateTv,eventTimeTv,eventClubNameTv;
    private ImageView eventImage;
    private Uri imageUri;
    private int eventHour,eventMin;

    DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        Intent intent = getIntent();
        String eventClubName = intent.getStringExtra("clubName");

        eventClubNameTv = findViewById(R.id.clubNameEt);
        eventClubNameTv.setText(eventClubName);

        eventNameEt = findViewById(R.id.eventNameEt);
        eventVenueEt = findViewById(R.id.eventVenueEt);
        eventDescriptionEt = findViewById(R.id.eventDescriptionEt);
        eventOrganizerEt = findViewById(R.id.eventOrganizerEt);
        eventTimeTv = findViewById(R.id.eventTimeTv);
        eventDateTv = findViewById(R.id.eventDateTv);
        submitBtn = findViewById(R.id.eventRegisterBtn);
        changeDpTv = findViewById(R.id.changeEventImage);
        eventImage = findViewById(R.id.eventImage);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        eventDateTv.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EventFormActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListener,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            /*to block the user to choose today's date
            Calendar today = Calendar.getInstance();
            Calendar aDayLater = (Calendar) today.clone();aDayLater.add(Calendar.DATE, 1);
            datePickerDialog.getDatePicker().setMinDate(aDayLater.getTimeInMillis());*/
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            datePickerDialog.show();
        });

        dateSetListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 +1;
            String date = dayOfMonth+"-"+ month1 +"-"+ year1;
            eventDateTv.setText(date);
        };

        eventTimeTv.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    EventFormActivity.this,
                    (view, hourOfDay, minute) -> {
                        eventHour = hourOfDay;
                        eventMin = minute;
                        calendar.set(0,0,0,eventHour,eventMin);
                        eventTimeTv.setText(DateFormat.format("hh:mm aa",calendar));
                    },12,0,false
            );
            timePickerDialog.updateTime(eventHour,eventMin);
            timePickerDialog.show();
        });

        changeDpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        submitBtn.setOnClickListener(view -> {
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events");
            String eventId= mDatabaseReference.push().getKey();
            uploadFile(eventId);
            //event constructor
            Event newEvent = new Event(eventNameEt.getText().toString(),
                                        eventClubNameTv.getText().toString(),
                                        eventDateTv.getText().toString(),
                                        eventTimeTv.getText().toString(),
                                        eventVenueEt.getText().toString(),
                                        eventDescriptionEt.getText().toString(),
                                        eventOrganizerEt.getText().toString());

            mDatabaseReference.child(eventId).setValue(newEvent);
            Toast.makeText(getApplicationContext(), "Event added, hopefully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                eventImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Image: " + imageUri.toString());
        }
    }

    private void uploadFile(String fileName) {
        final ProgressDialog progressDialog = new ProgressDialog(getBaseContext());
        progressDialog.setTitle("Uploading");

        if (imageUri != null){
            StorageReference fileReference  = FirebaseStorage.getInstance().getReference().child("eventImages/"+fileName);

            UploadTask uploadTask = fileReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();
                    //progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.dismiss();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed Successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "failed to upload");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setMessage("Uploaded  " +(int)progress+"%");
                        }
                    });
        }
        else
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
}}