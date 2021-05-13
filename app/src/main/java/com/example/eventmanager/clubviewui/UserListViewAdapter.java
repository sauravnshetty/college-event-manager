package com.example.eventmanager.clubviewui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Club;
import com.example.eventmanager.model.User;
import com.example.eventmanager.ui.clubs.ClubListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserListViewAdapter extends RecyclerView.Adapter<UserListViewAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "UserListViewAdapter";
    private Context context;
    private List<User> userList;
    private List<User> userListFull;

    public UserListViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.userListFull = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public UserListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.userNameTv.setText(user.getName());
        holder.selectedCb.setSelected(false);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public User getItem(int position) {
        return userList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView userNameTv;
        public CheckBox selectedCb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            userNameTv = itemView.findViewById(R.id.userNameTv);
            selectedCb = itemView.findViewById(R.id.selectedCb);
        }

        @Override
        public void onClick(View view) {
            if(selectedCb.isChecked())
                selectedCb.setChecked(false);
            else
                selectedCb.setChecked(true);
        }
    }

    @Override
    public Filter getFilter() {
        return userListFilter;
    }

    private Filter userListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(userListFull);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(User item : userListFull) {
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
            userList.clear();
            userList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
