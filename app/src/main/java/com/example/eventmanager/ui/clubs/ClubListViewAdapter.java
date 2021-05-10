package com.example.eventmanager.ui.clubs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.ClubViewActivity;
import com.example.eventmanager.model.ClubRowItem;
import com.example.eventmanager.R;

import java.util.List;

public class ClubListViewAdapter extends RecyclerView.Adapter<ClubListViewAdapter.ViewHolder> {

    private Context context;
    private List<ClubRowItem> clubRowItemList;

    public ClubListViewAdapter(Context context, List<ClubRowItem> clubRowItemList) {
        this.context = context;
        this.clubRowItemList = clubRowItemList;
    }

    @NonNull
    @Override
    public ClubListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubListViewAdapter.ViewHolder holder, int position) {
        ClubRowItem clubRowItem = clubRowItemList.get(position);

        holder.clubName.setText(clubRowItem.getClubName());
        holder.clubDept.setText(clubRowItem.getClubDept());
    }

    @Override
    public int getItemCount() {
        return clubRowItemList.size();
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
            Intent clubViewIntent = new Intent(context, ClubViewActivity.class);
            context.startActivity(clubViewIntent);
        }
    }
}
