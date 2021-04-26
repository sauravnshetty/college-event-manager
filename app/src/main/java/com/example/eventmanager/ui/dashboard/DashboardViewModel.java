package com.example.eventmanager.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.Club;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

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
        Club c1 = new Club();
        Club c2 = new Club();
        c1.setClubName("Ace");
        c1.setClubDept("CSE");
        c2.setClubName("CSI");
        c2.setClubDept("CSE");
        clubsArrayList.add(c1);
        clubsArrayList.add(c2);

        clubs.setValue(clubsArrayList);
    }
}