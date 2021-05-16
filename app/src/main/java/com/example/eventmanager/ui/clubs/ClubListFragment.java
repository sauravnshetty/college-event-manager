package com.example.eventmanager.ui.clubs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;

import static com.example.eventmanager.R.menu.search_menu;

public class ClubListFragment extends Fragment {

    private ClubListViewModel clubListViewModel;
    private ClubListViewAdapter clubViewAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        clubListViewModel = new ViewModelProvider(this).get(ClubListViewModel.class);

        View root = inflater.inflate(R.layout.fragment_club_list, container, false);
        final RecyclerView clubRecyclerView = root.findViewById(R.id.club_recycler_view);

        setHasOptionsMenu(true);

        clubListViewModel.getClubs().observe(getViewLifecycleOwner(), clubs -> {
            clubRecyclerView.setHasFixedSize(true);
            clubRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            clubViewAdapter = new ClubListViewAdapter(getActivity(), clubs);
            clubRecyclerView.setAdapter(clubViewAdapter);
        });
        return root;

        /*View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;*/
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(clubViewAdapter != null)
                    clubViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}