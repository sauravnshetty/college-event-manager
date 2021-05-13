package com.example.eventmanager.clubviewui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddMembersDialogViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<User>> users;
    private ArrayList<User> usersArrayList = new ArrayList<>();


    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers();
        }
        return users;
    }

    public void loadUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
                usersArrayList.add(user);
                //since this function gets called later we need to update the livedata here
                users.setValue(usersArrayList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());

                for(int i = 0; i < usersArrayList.size(); i++) {
                    if(usersArrayList.get(i).getEmail().equals(user.getEmail())) {
                        usersArrayList.set(i, user);
                        break;
                    }
                }
                users.setValue(usersArrayList);
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