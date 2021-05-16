package com.example.eventmanager.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventmanager.EventViewActivity;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "EventRViewAdapter";
    private final Context context;
    private List<Event> eventsList;
    private List<Event> eventsListFull;

    public EventRecyclerViewAdapter(Context context, List<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
        sortByDate(eventsList);
        this.eventsListFull = new ArrayList<>(eventsList);
    }

    private void sortByDate(List<Event> eventsList) {
        Collections.sort(eventsList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                String date1 = event1.getEventDate() + " " + event1.getEventTime();
                String date2 = event2.getEventDate() + " " + event2.getEventTime();
                Date dateTime1, dateTime2;
                try {
                    dateTime1 = sdf.parse(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 1;
                }
                try {
                    dateTime2 = sdf.parse(date2);
                    return dateTime2.compareTo(dateTime1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_item, parent, false);
        return new EventRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Event event = eventsList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.date.setText(event.getEventDate());

        StorageReference eventImagesRef = FirebaseStorage.getInstance().getReference().child("eventImages");
        eventImagesRef.child(event.getEventId()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(String.valueOf(uri)).centerCrop().into(holder.eventImage))
                .addOnFailureListener(e -> Log.d(TAG, "Failed to get event image"));
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    @Override
    public Filter getFilter() {
        return eventsListFilter;
    }

    private Filter eventsListFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Event> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(eventsListFull);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Event item : eventsListFull) {
                    if(item.getEventName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            eventsList.clear();
            eventsList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    //removed static keyword for a class
    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventName,date;
        ImageView eventImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventNameTv);
            date = itemView.findViewById(R.id.eventDateTv);
            eventImage = itemView.findViewById(R.id.event_image);

            itemView.setOnClickListener(this);
            }

        @Override
        public void onClick(View v) {
            Intent eventViewIntent = new Intent(context, EventViewActivity.class);
            String eventName = this.eventName.getText().toString();
            String eventDate = this.date.getText().toString();
            String selectedEventId = null;
            String eventClubId = null;
            for(int i = 0; i < eventsList.size(); i++) {
                if(eventsList.get(i).getEventName().equals(eventName) &&  eventsList.get(i).getEventDate().equals(eventDate)) {
                    selectedEventId = eventsList.get(i).getEventId();
                    eventClubId = eventsList.get(i).getEventClubId();
                }
            }
            eventViewIntent.putExtra("selectedEventId", selectedEventId);
            eventViewIntent.putExtra("eventClubId", eventClubId);
            if(selectedEventId != null && eventClubId != null)
                context.startActivity(eventViewIntent);
        }

    }
}
