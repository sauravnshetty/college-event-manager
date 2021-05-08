package com.example.eventmanager.ui.clubs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.model.ClubRowItem;

import java.util.ArrayList;
import java.util.List;

public class ClubListViewModel extends ViewModel {

    private MutableLiveData<List<ClubRowItem>> clubs;
    private ArrayList<ClubRowItem> clubsArrayList = new ArrayList<>();


    public LiveData<List<ClubRowItem>> getClubs() {
        if (clubs == null) {
            clubs = new MutableLiveData<>();
            loadClubs();
        }
        return clubs;
    }

    private void loadClubs() {
        ClubRowItem c1 = new ClubRowItem();
        ClubRowItem c2 = new ClubRowItem();
        c1.setClubName("Ace");
        c1.setClubDept("CSE");
        c2.setClubName("CSI");
        c2.setClubDept("CSE");
        clubsArrayList.add(c1);
        clubsArrayList.add(c2);

        clubs.setValue(clubsArrayList);
    }
}