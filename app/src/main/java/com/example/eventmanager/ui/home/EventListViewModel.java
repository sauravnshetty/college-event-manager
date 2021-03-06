package com.example.eventmanager.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventmanager.model.Event;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventListViewModel extends ViewModel {

    private MutableLiveData<List<Event>> events;//recentEvents;
    private ArrayList<Event> eventsArrayList = new ArrayList<>();

    public LiveData<List<Event>> getEvents() {
        if(events == null) {
            events = new MutableLiveData<>();
            loadEvents();
        }
        return events;
    }

    private void loadEvents() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {
                Event event = dataSnapshot.getValue(Event.class);
                assert event != null;
                event.setEventId(dataSnapshot.getKey());
                eventsArrayList.add(event);
                events.setValue(eventsArrayList);
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setEventId(dataSnapshot.getKey());

                for(int i = 0; i < eventsArrayList.size(); i++) {
                    if(eventsArrayList.get(i).getEventId().equals(event.getEventId())) {
                        eventsArrayList.set(i, event);
                        break;
                    }
                }
                events.setValue(eventsArrayList);
            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setEventId(dataSnapshot.getKey());

                for(int i = 0; i < eventsArrayList.size(); i++) {
                    if(eventsArrayList.get(i).getEventId().equals(event.getEventId())) {
                        eventsArrayList.remove(i);
                        break;
                    }
                }
                events.setValue(eventsArrayList);
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {}
        });
    }
}