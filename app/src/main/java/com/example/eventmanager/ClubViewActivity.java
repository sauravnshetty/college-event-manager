 package com.example.eventmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eventmanager.clubviewui.AddMembersDialogFragment;
import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.User;
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

public class ClubViewActivity extends AppCompatActivity {

    private final String TAG = "ClubViewActivity";

    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private TextView clubNameTv, clubBranchTv, clubIntroTv;
    private ImageView clubImage;
    private Button addMembersBtn;
    private Button createEventBtn;

    private String clubId;
    private Club club;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        clubNameTv = findViewById(R.id.clubName);
        clubBranchTv = findViewById(R.id.clubBranch);
        clubIntroTv = findViewById(R.id.clubIntro);
        clubImage = findViewById(R.id.clubImage);

        addMembersBtn = findViewById(R.id.addMembersBtn);

        addMembersBtn.setEnabled(false);
        addMembersBtn.setVisibility(View.GONE);
        addMembersBtn.setOnClickListener(view -> {
            DialogFragment dialog = new AddMembersDialogFragment(clubId);
            dialog.show(getSupportFragmentManager(), "AddMembersDialogFragment");
        });

        createEventBtn = findViewById(R.id.createEventBtn);
        createEventBtn.setEnabled(false);
        createEventBtn.setVisibility(View.GONE);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),EventFormActivity.class);
                i.putExtra("clubName",clubNameTv.getText().toString());
                i.putExtra("clubId",clubId);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        clubId = getIntent().getStringExtra("selectedClubId");

        mDatabase.child("clubmembers").child(clubId).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    createEventBtn.setEnabled(true);
                    createEventBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference clubsRef = mDatabase.child("clubs");
        clubsRef.child(clubId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                club = snapshot.getValue(Club.class);
                club.setClubId(snapshot.getKey());
                clubNameTv.setText(club.getName());
                clubBranchTv.setText(club.getBranch());
                clubIntroTv.setText(club.getIntroText());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to get club data");
            }
        });

        StorageReference clubImagesRef = FirebaseStorage.getInstance().getReference().child("clubImages");

        clubImagesRef.child(clubId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(String.valueOf(uri)).into(clubImage);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to get club image");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_top_menu, menu);
        menu.getItem(0).setEnabled(false);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setEnabled(false);
        menu.getItem(1).setVisible(false);
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.isAdmin()) {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setEnabled(true);
                    menu.getItem(1).setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit:
                Intent clubFormIntent = new Intent(this, ClubFormActivity.class);
                clubFormIntent.putExtra("club", club);
                startActivity(clubFormIntent);
                return true;
            case R.id.add_members:
                DialogFragment dialog = new AddMembersDialogFragment(clubId);
                dialog.show(getSupportFragmentManager(), "AddMembersDialogFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}