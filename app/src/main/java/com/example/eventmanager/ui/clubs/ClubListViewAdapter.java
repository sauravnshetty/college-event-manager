package com.example.eventmanager.ui.clubs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.Club;
import com.example.eventmanager.R;

import java.util.List;

public class ClubListViewAdapter extends RecyclerView.Adapter<ClubListViewAdapter.ViewHolder> {

    private Context context;
    private List<Club> clubList;

    public ClubListViewAdapter(Context context, List<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
    }

    @NonNull
    @Override
    public ClubListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubListViewAdapter.ViewHolder holder, int position) {
        Club club = clubList.get(position);

        holder.clubName.setText(club.getClubName());
        holder.clubDept.setText(club.getClubDept());
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView clubName;
        public TextView clubDept;
        public ImageView clubImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            clubName = itemView.findViewById(R.id.club_name);
            clubDept = itemView.findViewById(R.id.club_department);
            clubImage = itemView.findViewById(R.id.club_image);
        }

        @Override
        public void onClick(View v) {
            Log.d("ClickFromViewHolder", "Clicked");
        }
    }
}
