package com.example.eventmanager.ui.clubs;

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
import com.example.eventmanager.ClubViewActivity;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Club;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ClubListViewAdapter extends RecyclerView.Adapter<ClubListViewAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "ClubListViewAdapter";
    private final Context context;
    private List<Club> clubList;
    private List<Club> clubListFull;

    public ClubListViewAdapter(Context context, List<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
        this.clubListFull = new ArrayList<>(clubList);
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

        holder.clubName.setText(club.getName());
        holder.clubDept.setText(club.getBranch());

        StorageReference clubImagesRef = FirebaseStorage.getInstance().getReference().child("clubImages");
        clubImagesRef.child(club.getClubId()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(String.valueOf(uri)).centerCrop().into(holder.clubImage))
        .addOnFailureListener(e -> Log.d(TAG, "Failed to get club image"));
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
            Intent clubViewIntent = new Intent(context, ClubViewActivity.class);
            String cName = this.clubName.getText().toString();
            String selectedClubId = null;
            for(int i = 0; i < clubList.size(); i++) {
                if(clubList.get(i).getName().equals(cName)) {
                    selectedClubId = clubList.get(i).getClubId();
                }
            }
            clubViewIntent.putExtra("selectedClubId", selectedClubId);
            context.startActivity(clubViewIntent);
        }
    }

    @Override
    public Filter getFilter() {
        return clubListFilter;
    }

    private Filter clubListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Club> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(clubListFull);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Club item : clubListFull) {
                    if(item.getName().toLowerCase().contains(filterPattern)) {
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
            clubList.clear();
            clubList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
