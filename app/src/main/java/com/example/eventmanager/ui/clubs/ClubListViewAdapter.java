package com.example.eventmanager.ui.clubs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventmanager.ClubViewActivity;
import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.ClubRowItem;
import com.example.eventmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ClubListViewAdapter extends RecyclerView.Adapter<ClubListViewAdapter.ViewHolder> {

    private static final String TAG = "ClubListViewAdapter";
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

        holder.clubName.setText(club.getName());
        holder.clubDept.setText(club.getBranch());

        StorageReference clubImagesRef = FirebaseStorage.getInstance().getReference().child("clubImages");
        clubImagesRef.child(club.getClubId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(String.valueOf(uri)).into(holder.clubImage);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to get club image");
            }
        });
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
}
