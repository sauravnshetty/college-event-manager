package com.example.eventmanager.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventmanager.ClubFormActivity;
import com.example.eventmanager.LoginActivity;
import com.example.eventmanager.R;
import com.example.eventmanager.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile View: ";

    ImageView profileImg;
    TextView profileBranch, profileEmail, profileName;
    Button signOut, createClubBtn;

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        //final TextView textView = root.findViewById(R.id.textView);

        profileImg = root.findViewById(R.id.profile_image);
        profileBranch = root.findViewById(R.id.profileBranch);
        profileEmail = root.findViewById(R.id.profileEmail);
        profileName = root.findViewById(R.id.profile_name);
        signOut = root.findViewById(R.id.profile_signout);
        createClubBtn = root.findViewById(R.id.createClubBtn);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        createClubBtn.setEnabled(false);
        createClubBtn.setVisibility(View.GONE);
        createClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createClubIntent = new Intent(getActivity(), ClubFormActivity.class);
                startActivity(createClubIntent);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser acct = FirebaseAuth.getInstance().getCurrentUser();
        String userId = acct.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Your fragment is detached before response coming from firebase, So try to check getActivity() null before using that
                if (getActivity() == null) {
                    return;
                }
                updateData(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void updateData(User acct) {
        if(acct != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String personName = acct.getName();
            String personEmail = acct.getEmail();
            String personBranch = acct.getBranch();
            Uri personPhoto = user.getPhotoUrl();

            profileName.setText(personName);
            profileEmail.setText(personEmail);
            profileBranch.setText(personBranch);
            Glide.with(this).load(String.valueOf(personPhoto)).into(profileImg);

            if(acct.isAdmin()) {
                createClubBtn.setEnabled(true);
                createClubBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void signOut() {

        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getActivity(),
                LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}