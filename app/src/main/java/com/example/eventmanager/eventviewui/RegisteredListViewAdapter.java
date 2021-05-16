package com.example.eventmanager.eventviewui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;

import java.util.ArrayList;
import java.util.List;

public class RegisteredListViewAdapter extends RecyclerView.Adapter<RegisteredListViewAdapter.ViewHolder> implements Filterable {

    private List<String> userList;
    private List<String> userListFull;

    public RegisteredListViewAdapter(Context context, List<String> userList) {
        this.userList = userList;
        this.userListFull = new ArrayList<>(userList);
    }

    @Override
    public Filter getFilter() {
        return userListFilter;
    }

    @NonNull
    @Override
    public RegisteredListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registered_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegisteredListViewAdapter.ViewHolder holder, int position) {
        String user = userList.get(position);
        holder.userEmailTv.setText(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userEmailTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTv = itemView.findViewById(R.id.userEmailTv);
        }

    }

    private Filter userListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(userListFull);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(String item : userListFull) {
                    if(item.toLowerCase().contains(filterPattern)) {
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
