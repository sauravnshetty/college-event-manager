package com.example.eventmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eventmanager.clubviewui.AddMembersDialogFragment;
import com.example.eventmanager.eventviewui.RegisteredMembersDialogFragment;
import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventViewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "EventViewActivity";
    private String eventId, eventClubId;
    private ImageView eventImage;
    private TextView eventNameTv,clubNameTv,eventDateTv,eventTimeTv,eventVenueTv,eventDescriptionTv,eventOrganizerTv;
    private Button registerBtn,registerListBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_top_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        eventImage = findViewById(R.id.eventImage);
        eventNameTv = findViewById(R.id.eventName);
        clubNameTv = findViewById(R.id.eventClub);
        eventDateTv = findViewById(R.id.eventDate);
        eventTimeTv = findViewById(R.id.eventTime);
        eventVenueTv = findViewById(R.id.eventVenue);
        eventDescriptionTv = findViewById(R.id.eventDescription);
        eventOrganizerTv = findViewById(R.id.eventOrganizer);

        registerBtn = findViewById(R.id.eventRegisterBtn);
        registerBtn.setOnClickListener(this);

        registerListBtn = findViewById(R.id.registerListBtn);
        registerListBtn.setEnabled(false);
        registerListBtn.setVisibility(View.GONE);
        registerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new RegisteredMembersDialogFragment(eventId);
                dialog.show(getSupportFragmentManager(), "RegisteredMembersDialogFragment");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventId = getIntent().getStringExtra("selectedEventId");
        eventClubId = getIntent().getStringExtra("eventClubId");

        DatabaseReference clubmembersRef = FirebaseDatabase.getInstance().getReference().child("clubmembers");
        clubmembersRef.child(eventClubId).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    registerListBtn.setEnabled(true);
                    registerListBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("events");
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                eventNameTv.setText(event.getEventName());
                clubNameTv.setText(event.getEventClubName());
                eventDateTv.setText(event.getEventDate());
                eventTimeTv.setText(event.getEventTime());
                eventVenueTv.setText(event.getEventVenue());
                eventDescriptionTv.setText(event.getEventDescription());
                eventOrganizerTv.setText(event.getEventOrganizers());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to get event data");
            }
        });

        StorageReference eventImagesRef = FirebaseStorage.getInstance().getReference().child("eventImages");

        eventImagesRef.child(eventId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    @Override
    public void onClick(View v) {
        DatabaseReference registerRef = mDatabase.child("registeredmembers").child(eventId);
        registerRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    registerRef.child(currentUser.getUid()).setValue(currentUser.getDisplayName());
                    Toast.makeText(getApplicationContext(),"You have successfully registered",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"You have already registered",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled:failed to add registered members");
            }
        });
        fireAlarm();
    }

    public void fireAlarm() {

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("eventName", eventNameTv.getText().toString());
        //intent.setAction("EVENT_REMINDER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, intent,0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        String dateTime = eventDateTv.getText().toString().trim() + " " + eventTimeTv.getText().toString().trim();
        //dateTime = "15-05-2021 12:04 am";
        Log.d(TAG, dateTime);
        try {
            Date date = sdf.parse(dateTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, -1);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            Log.d(TAG, "ALARM IS SET " + calendar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //AlarmReceiver alm = new AlarmReceiver();
        //alm.createNotification(getApplicationContext(), "test", "works fine");
    }
}