package com.example.eventmanager.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.EventRowItem;
import com.example.eventmanager.R;
import com.example.eventmanager.ui.clubs.ClubListViewAdapter;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<EventRowItem> eventRowItemList;

    public EventRecyclerViewAdapter(Context context, List<EventRowItem> eventRowItems) {
        this.context = context;
        this.eventRowItemList = eventRowItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_item, parent, false);
        return new EventRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        EventRowItem eventRowItem = eventRowItemList.get(position);
        holder.eventName.setText(eventRowItem.getEventName());
        holder.clubName.setText(eventRowItem.getClubName());
        holder.date.setText(eventRowItem.getDate());
    }

    @Override
    public int getItemCount() {
        return eventRowItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView eventName, clubName, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventNametv);
            clubName = itemView.findViewById(R.id.clubNametv);
            date = itemView.findViewById(R.id.datetv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ClickFromViewHolder", "Clicked");
                }
            });

        }
    }
}
