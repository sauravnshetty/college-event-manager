package com.example.eventmanager.eventviewui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegisteredMembersDialogViewModel extends ViewModel {
    // TODO: Implement the ViewModel

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
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //Log.d("hi", "onChildAdded: "+dataSnapshot);
                String user = dataSnapshot.getValue().toString();
                usersArrayList.add(user);
                //since this function gets called later we need to update the livedata here
                users.setValue(usersArrayList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                String user = dataSnapshot.getKey();

                /*
                for(int i = 0; i < usersArrayList.size(); i++) {
                    if(usersArrayList.get(i).getEmail().equals(user.getEmail())) {
                        usersArrayList.set(i, user);
                        break;
                    }
                }
                users.setValue(usersArrayList);
                */
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Not needed currently as user can't be deleted
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}