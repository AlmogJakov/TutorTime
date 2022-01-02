package com.project.tutortime.Controller.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.project.tutortime.Model.firebase.FirebaseManager;
import com.project.tutortime.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.tutortime.View.LoadingDialog;
import com.project.tutortime.Model.adapter.TutorAdapter;
import com.project.tutortime.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    ListView listview;
    /* final value to pass the adapter to ValueEventListener in external class */
    final ArrayList<TutorAdapter> adapter = new ArrayList<>();
    LoadingDialog loadingDialog;
    MaterialButton myProfileButton, searchButton, chatsButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /* show loading dialog until all fragment resources ready */
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
        /* END show loading dialog until all fragment resources ready */
        listview = binding.featuresList;
        myProfileButton = binding.buttonProfile;
        searchButton = binding.buttonSearch;
        chatsButton = binding.buttonChats;
        //SpannableString ss = new SpannableString("(0)");
        //chatsButton.setText(ss);
        FirebaseManager fm = new FirebaseManager();
        fm.setUnreadedChats(chatsButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().performIdentifierAction(R.id.nav_search, 0);
            }
        });
        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().performIdentifierAction(R.id.nav_chats, 0);
            }
        });
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> arr = getActivity().getIntent().getExtras().getIntegerArrayList("status");
                if (arr.isEmpty() || (arr.get(0) != 0 && arr.get(0) != 1)) {
                    Toast.makeText(getContext(), "Could not retrieve value from database.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int status = arr.get(0);
                if (status==0) {
                    NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                    navigationView.getMenu().performIdentifierAction(R.id.nav_my_profile, 0);
                } else if (status==1) {
                    NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                    navigationView.getMenu().performIdentifierAction(R.id.nav_tutor_profile, 0);
                }
            }
        });
        /* set random tutors to listview using adapter */
        fm.setRandomTutors(getContext(),adapter,listview);
        /* open new thread to close loadingDialog after all fragment resources ready */
        closeLoadingDialog();
        return root;
    }

    @Override
    public void onDestroyView() {
        loadingDialog.dismiss();
        super.onDestroyView();
        closeLoadingDialog();
    }

    /* close Loading Dialog when all fragment resources ready */
    public void closeLoadingDialog() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /* wait until the adapter created & loaded all images */
                while(adapter.size()==0 || !adapter.get(0).isAllResourcesReady()) { }
                /* hide loading dialog (fragment resources ready) */
                loadingDialog.cancel();
            }
        });
    }
}