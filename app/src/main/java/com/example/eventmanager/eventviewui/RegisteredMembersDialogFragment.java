package com.example.eventmanager.eventviewui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;

public class RegisteredMembersDialogFragment extends DialogFragment {

    private RegisteredListViewAdapter userListAdapter;
    private String eventId;

    private Dialog dialog;

    public RegisteredMembersDialogFragment() {
        super();
    }

    public RegisteredMembersDialogFragment(String eventId) {
        super();
        this.eventId = eventId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.registered_members_dialog_fragment, container, false);
        RegisteredMembersDialogViewModel mViewModel = new ViewModelProvider(this).get(RegisteredMembersDialogViewModel.class);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.registered_members_dialog_fragment, null);
        Button okBtn = dialogView.findViewById(R.id.okBtn);

        final RecyclerView usersRecyclerView = dialogView.findViewById(R.id.registered_members_rv);

        mViewModel.getUsers(eventId).observe(getViewLifecycleOwner(), users -> {
            usersRecyclerView.setHasFixedSize(true);
            usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            userListAdapter = new RegisteredListViewAdapter(getActivity(), users);
            usersRecyclerView.setAdapter(userListAdapter);
        });

        SearchView searchView = dialogView.findViewById(R.id.register_dialog_search);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        okBtn.setOnClickListener(view -> dialog.dismiss());
        return dialogView;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Registered members");
        return dialog;
    }

}