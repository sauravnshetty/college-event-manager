package com.example.eventmanager.clubviewui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.eventmanager.R;
import com.example.eventmanager.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddMembersDialogFragment extends DialogFragment {

    private final String TAG = "DialogFragment: ";

    private AddMembersDialogViewModel mViewModel;
    private UserListViewAdapter userListAdapter;
    private String clubId;

    private Dialog dialog;

    public static AddMembersDialogFragment newInstance() {
        return new AddMembersDialogFragment();
    }

    public AddMembersDialogFragment() { super(); }

    public AddMembersDialogFragment(String clubId) {
        super();
        this.clubId = clubId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AddMembersDialogViewModel.class);
        //TODO: add a button to dialog view which adds the selected members
        //  also a heading and a cancel button
//        builder = new AlertDialog.Builder(getActivity());
        View dialogView = inflater.inflate(R.layout.add_members_dialog_fragment, null);
        Button addBtn, cancelBtn;
//        builder.setView(dialogView)
//                .setPositiveButton("Add Selected", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //TODO: add the selected members to database
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        AddMembersDialogFragment.this.getDialog().cancel();
//                    }
//                });

        final RecyclerView usersRecyclerView = dialogView.findViewById(R.id.add_members_rv);

        mViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            usersRecyclerView.setHasFixedSize(true);
            usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            userListAdapter = new UserListViewAdapter(getActivity(), users);
            usersRecyclerView.setAdapter(userListAdapter);
        });

        SearchView searchView = (SearchView) dialogView.findViewById(R.id.dialog_search);

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

        addBtn = dialogView.findViewById(R.id.addBtn);
        cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getting selected users by checking the view
                View itemView;
                List<User> selectedUsers = new ArrayList<>();
                for(int i = 0; i < userListAdapter.getItemCount(); i++) {
                    itemView = usersRecyclerView.getLayoutManager().findViewByPosition(i);
                    CheckBox cb = itemView.findViewById(R.id.selectedCb);
                    if(cb.isChecked()) {
                        selectedUsers.add(userListAdapter.getItem(i));
                    }
                }

                //Adding selected users as club members
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("clubmembers").child(clubId);

                for(User user : selectedUsers) {
                    String userId = user.getUid();
                    String userName = user.getName();

                    ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()) {
                                ref.child(userId).setValue(userName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "Failed to add a member");
                        }
                    });
                }
                Log.d(TAG, "Added " + selectedUsers.size() + " members!");
                //Toast.makeText(getActivity(), "Added " + selectedUsers.size() + " members!", Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getFragmentManager().popBackStack();
                dialog.dismiss();
            }
        });

        return dialogView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Select members to add");
        return dialog; //builder.create();
    }
}