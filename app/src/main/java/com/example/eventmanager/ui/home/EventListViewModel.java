package com.example.eventmanager.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.EventRowItem;

import java.util.ArrayList;
import java.util.List;

public class EventListViewModel extends ViewModel {

    private MutableLiveData<List<EventRowItem>> events;
    private ArrayList<EventRowItem> eventList = new ArrayList<>();

    public LiveData<List<EventRowItem>> getEvents() {
        if(events == null) {
            events = new MutableLiveData<>();
            loadEvents();
        }
        return events;
    }

    private void loadEvents() {
        eventList.add(new EventRowItem("Art Attack", "CSI", "12/4/21"));
        eventList.add(new EventRowItem("Pictionary", "ACE", "3/5/21"));
        eventList.add(new EventRowItem("Art Attack2", "CSI", "12/4/21"));
        eventList.add(new EventRowItem("Pictionary2", "ACE", "3/5/21"));
        eventList.add(new EventRowItem("Art Attack3", "CSI", "12/4/21"));
        eventList.add(new EventRowItem("Pictionary3", "ACE", "3/5/21"));
        eventList.add(new EventRowItem("Art Attack4", "CSI", "12/4/21"));
        eventList.add(new EventRowItem("Pictionary4", "ACE", "3/5/21"));
        eventList.add(new EventRowItem("Art Attack5", "CSI", "12/4/21"));
        eventList.add(new EventRowItem("Pictionary5", "ACE", "3/5/21"));
        eventList.add(new EventRowItem("Art Attack6", "CSI", "12/4/21"));
        eventList.add(new EventRowItem("Pictionary6", "ACE", "3/5/21"));

        events.setValue(eventList);
    }
}