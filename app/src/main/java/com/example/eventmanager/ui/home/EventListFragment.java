package com.example.eventmanager.ui.home;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.eventmanager.R.menu.search_menu;

public class EventListFragment extends Fragment {

    public static final String ARG_POS = "position";

    private List<Event> upcomingEvents;
    private List<Event> recentEvents;

    private EventListViewModel mViewModel;
    private EventRecyclerViewAdapter eventViewAdapter;

    public EventListFragment() {
        this.upcomingEvents = new ArrayList<>();
        this.recentEvents = new ArrayList<>();
    }

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.event_list_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        final RecyclerView eventRecyclerView = root.findViewById(R.id.eventRecyclerView);
        Bundle args = getArguments();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        Date presentDate = new Date();

        mViewModel.getEvents().observe(getViewLifecycleOwner(), eventRowItems -> {
            eventRecyclerView.setHasFixedSize(true);
            eventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            
            upcomingEvents.clear();
            recentEvents.clear();
            for(Event event : eventRowItems) {
                String dateTime = event.getEventDate() + " " + event.getEventTime();
                try {
                    Date eDate = sdf.parse(dateTime);

                    if(eDate.after(presentDate))
                        upcomingEvents.add(event);
                    else
                        recentEvents.add(event);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            switch (args.getInt(ARG_POS)) {
                case 0:
                    //upcoming events
                    eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), upcomingEvents);
                    break;
                case 1:
                    //recent events;
                    eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), recentEvents);
                    break;
                default:
                    eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), eventRowItems);
            }

            eventRecyclerView.setAdapter(eventViewAdapter);
        });

        //setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        switch (args.getInt(ARG_POS)) {
            case 0:
                //upcoming events
                break;
            case 1:
                //recent events;
                break;
        }
    }

    public EventRecyclerViewAdapter getListViewAdapter() {
        return eventViewAdapter;
    }

}