package com.example.eventmanager.eventviewui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisteredMembersDialogViewModel extends ViewModel {

    private MutableLiveData<List<String>> users;
    private ArrayList<String> usersArrayList = new ArrayList<>();


    public LiveData<List<String>> getUsers(String eventId) {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers(eventId);
        }
        return users;
    }

    private void loadUsers(String eventId) {
        DatabaseReference registerRef = FirebaseDatabase.getInstance().getReference().child("registeredmembers").child(eventId);
        registerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {
                //Log.d("hi", "onChildAdded: "+dataSnapshot);
                String user = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                usersArrayList.add(user);
                //since this function gets called later we need to update the livedata here
                users.setValue(usersArrayList);
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
                //Not needed currently as user can't be deleted
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {}
        });
    }
}