package com.example.eventmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.TemporalAdjuster;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class EventFormActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 3;
    private static final String TAG = "EventFormActivity";
    private static final int RESULT_CROP = 4;
    private EditText  eventNameEt, eventVenueEt,eventDescriptionEt,eventOrganizerEt;
    private Button submitBtn;
    private TextView changeDpTv,eventDateTv,eventTimeTv,eventClubNameTv;
    private ImageView eventImage;
    private Uri imageUri;
    private int eventHour,eventMin;
    private String clubId;
    private Event editableEvent;

    DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        Intent intent = getIntent();
        String eventClubName = intent.getStringExtra("clubName");
        clubId = intent.getStringExtra("clubId");
        editableEvent = (Event) intent.getSerializableExtra("eventObject");


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
            String eventId;

            if(!isAllFilled()) {
                Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(editableEvent != null){
                eventId = editableEvent.getEventId();
            }
            else {
                eventId= mDatabaseReference.push().getKey();
            }
            uploadFile(eventId);
            //event constructor
            Event newEvent = new Event(eventNameEt.getText().toString(),
                                        eventClubNameTv.getText().toString(),
                                        eventDateTv.getText().toString(),
                                        eventTimeTv.getText().toString(),
                                        eventVenueEt.getText().toString(),
                                        eventDescriptionEt.getText().toString(),
                                        eventOrganizerEt.getText().toString(),
                                        clubId);
            newEvent.setEventId(eventId);
            mDatabaseReference.child(eventId).setValue(newEvent);
            Toast.makeText(getApplicationContext(), "Event added, hopefully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private boolean isAllFilled() {
        boolean filled = true;
        if(eventNameEt.getText().toString().trim().equals(""))
            filled = false;
        else if(eventDateTv.getText().toString().trim().equals(""))
            filled = false;
        else if(eventDescriptionEt.getText().toString().trim().equals(""))
            filled = false;
        else if(eventOrganizerEt.getText().toString().trim().equals(""))
            filled = false;
        else if(eventTimeTv.getText().toString().trim().equals(""))
            filled = false;
        else if(eventVenueEt.getText().toString().trim().equals(""))
            filled = false;

        return filled;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(editableEvent!=null){
            eventNameEt.setText(editableEvent.getEventName());
            eventDateTv.setText(editableEvent.getEventDate());
            eventTimeTv.setText(editableEvent.getEventTime());
            eventVenueEt.setText(editableEvent.getEventVenue());
            eventDescriptionEt.setText(editableEvent.getEventDescription());
            eventOrganizerEt.setText(editableEvent.getEventOrganizers());

            StorageReference fileReference  = FirebaseStorage.getInstance().getReference().child("eventImages");

            fileReference.child(editableEvent.getEventId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(String.valueOf(uri)).into(eventImage);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to get event image");
                        }
                    });
        }

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
                Glide.with(getApplicationContext()).load(String.valueOf(imageUri)).centerCrop().into(eventImage);
                //eventImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Image: " + imageUri.toString());
        }
    }

    /*private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }*/

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
    }
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
            bitmap = Ion.with((TemporalAdjuster) view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }*/

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}