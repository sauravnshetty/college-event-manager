package com.example.eventmanager.ui.clubs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.ClubRowItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ClubListViewModel extends ViewModel {

    private MutableLiveData<List<Club>> clubs;
    private ArrayList<Club> clubsArrayList = new ArrayList<>();


    public LiveData<List<Club>> getClubs() {
        if (clubs == null) {
            clubs = new MutableLiveData<>();
            loadClubs();
        }
        return clubs;
    }

    private void loadClubs() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("clubs");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Club club = dataSnapshot.getValue(Club.class);
                club.setClubId(dataSnapshot.getKey());
                clubsArrayList.add(club);
                clubs.setValue(clubsArrayList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Club club = dataSnapshot.getValue(Club.class);
                club.setClubId(dataSnapshot.getKey());

                for(int i = 0; i < clubsArrayList.size(); i++) {
                    if(clubsArrayList.get(i).getClubId().equals(club.getClubId())) {
                        clubsArrayList.set(i, club);
                        break;
                    }
                }
                clubs.setValue(clubsArrayList);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Club club = dataSnapshot.getValue(Club.class);
                club.setClubId(dataSnapshot.getKey());

                for(int i = 0; i < clubsArrayList.size(); i++) {
                    if(clubsArrayList.get(i).getClubId().equals(club.getClubId())) {
                        clubsArrayList.remove(i);
                        break;
                    }
                }
                clubs.setValue(clubsArrayList);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}