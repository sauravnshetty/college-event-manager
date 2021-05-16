package com.example.eventmanager;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.eventmanager.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class NavigationActivity extends AppCompatActivity {

    private final String TAG = "navActivity: ";

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        addUserToDatabase();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private boolean addUserToDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            Log.d(TAG, "Adding user to database " + user.getEmail());
            writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), "", "", false);
        }
        return false;
    }

    public void parseUserFromEmail(User user) {
        String email = user.getEmail();
        if(!email.startsWith("4nm")) {
            user.setBranch("Faculty/Staff");
            return;
        }
        String usn = email.replace("@nmamit.in", "");
        user.setUsn(usn);

        switch(usn.substring(5,7)) {
            case "cs":
                user.setBranch("Computer Science");
                break;
            case "is":
                user.setBranch("Information Science");
                break;
            case "me":
                user.setBranch("Mechanical");
                break;
            case "cv":
                user.setBranch("Civil");
                break;
            case "bt":
                user.setBranch("Biotechnology");
                break;
            case "ec":
                user.setBranch("Electronics and Communications");
                break;
            case "ee":
                user.setBranch("Electronics and Electricals");
                break;
            default:
                user.setBranch("unable to find");
        }
    }

    public void writeNewUser(String userId, String name, String email, String usn, String branch, boolean isAdmin) {
        User user = new User(email, name, usn, branch, isAdmin);
        Log.d(TAG, "Writing user to database " + user.getEmail());
        parseUserFromEmail(user);
        Log.d(TAG, "Writing user to database " + user.getUsn());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d(TAG, "User added " + user.getEmail());
                    mDatabase.child("users").child(userId).setValue(user);
                }else {
                    Log.d(TAG, "User exists!" + user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "cancelled bitch! " + user.getEmail());
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "connected");
                } else {
                    Log.d(TAG, "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });
    }

}