package com.project.tutortime.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.LoadingDialog;
import com.project.tutortime.adapter.TutorAdapter;

import com.project.tutortime.adapter.TutorAdapterItem;
import com.project.tutortime.databinding.FragmentHomeBinding;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;
import com.project.tutortime.ui.chats.Chat;
import com.project.tutortime.ui.customerprofile.CustomerProfile;
import com.project.tutortime.ui.search.Search;
import com.project.tutortime.ui.tutorprofile.TutorProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    final int TUTORS_TO_SHOW = 5;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    ListView listview;
    ValueEventListener listViewListener;
    TutorAdapter adapter;
    LoadingDialog loadingDialog;
    MaterialButton myProfileButton, searchButton, chatsButton;
    List<TutorAdapterItem> tutorsToShow = new ArrayList<>();

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
        SpannableString ss = new SpannableString("(0)");
        chatsButton.setText(ss);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Search();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // add to back stack
                //transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, fragment).commit();

            }
        });
        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Chat();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // add to back stack
                //transaction.addToBackStack("HomeFragment");
                transaction.replace(R.id.fragment_container, fragment).commit();
            }
        });
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ArrayList<Integer> arr = getActivity().getIntent().getExtras().getIntegerArrayList("status");
                if (arr.isEmpty() || (arr.get(0) != 0 && arr.get(0) != 1)) {
                    Toast.makeText(getContext(), "Could not retrieve value from database.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int status = arr.get(0);
                if (status==0) {
                    // add to back stack
                    //transaction.addToBackStack("HomeFragment");
                    Fragment fragment = new CustomerProfile();
                    transaction.replace(R.id.fragment_container, fragment).commit();
                } else if (status==1) {
                    Fragment fragment = new TutorProfile();
                    // add to back stack
                    //transaction.addToBackStack("HomeFragment");
                    transaction.replace(R.id.fragment_container, fragment).commit();
                }
            }
        });
        listViewListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /* Init 'TutorAdapterItem' list for the adapter */
                tutorsToShow = new ArrayList<>();
                Random random = new Random();
                int tutorsNum = (int)dataSnapshot.child("teachers").getChildrenCount();
                /* Init set to store 3 random numbers */
                Set<Integer> data = new LinkedHashSet<>();
                /* add 3 random numbers in range (0,teachersNum) [each indicates tutor index] */
                while (data.size()<TUTORS_TO_SHOW && tutorsNum>data.size()) {
                    int rand = random.nextInt(tutorsNum);
                    data.add(rand);
                }
                int counter = 0;
                for (DataSnapshot ds : dataSnapshot.child("teachers").getChildren()) {
                    /* If the pointer indicates one of the random numbers - add the tutor to the list */
                    if (data.contains(counter)) {
                        data.remove(counter);
                        teacherObj teacher = ds.getValue(teacherObj.class);
                        userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                        int subsNum = 0;
                        if (teacher.getSub() != null){
                            subsNum =teacher.getSub().size();
                        }

                        Object[] subs = teacher.getSub().keySet().toArray();
                        String sub = (String) subs[random.nextInt(subsNum)];
                        if (user==null) {
                            System.out.println("Found tutor without user information. Tutor ID:" + ds.getKey());
                            continue; }
                        TutorAdapterItem item = new TutorAdapterItem(user,teacher,sub);
                        tutorsToShow.add(item);
                        /* done adding all random tutors */
                        if (data.size()==0) break;
                    }
                    counter++;
                }
                /* Shuffle the chronological order (of the tutors' keys) */
                Collections.shuffle(tutorsToShow);
                /* init the adapter with the 'tutorsToShow' list */
                adapter = new TutorAdapter(getContext(), tutorsToShow);
                listview.setAdapter(adapter);
                adapter.updateListViewHeight(listview);
                closeLoadingDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        myRef.addListenerForSingleValueEvent(listViewListener);
//        listview.setTextFilterEnabled(true);
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), TeacherCard.class);
//                intent.putExtra("user", tutorsToShow.get(position).getUser());
//                intent.putExtra("teacher", tutorsToShow.get(position).getTeacher());
//                intent.putExtra("sub", tutorsToShow.get(position).getSubName());
//                startActivity(intent);
//            }
//        });
        return root;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        myProfileButton = binding.buttonProfile;
//        searchButton = binding.buttonSearch;
//        chatsButton = binding.buttonChats;
//        SpannableString ss = new SpannableString("(0)");
//        chatsButton.setText(ss);
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new Search();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                if (savedInstanceState == null) {
//
//                    transaction.replace(R.id.fragment_container, fragment).commit();;
//                    transaction.addToBackStack(null);
//                }
//            }
//        });
//        chatsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new Chat();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                if (savedInstanceState == null) {
//                    transaction.addToBackStack(null);
//                    transaction.replace(R.id.fragment_container, fragment).commit();;
//                }
//            }
//        });
//        myProfileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                ArrayList<Integer> arr = getActivity().getIntent().getExtras().getIntegerArrayList("status");
//                if (arr.isEmpty() || (arr.get(0) != 0 && arr.get(0) != 1)) {
//                    Toast.makeText(getContext(), "Could not retrieve value from database.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                int status = arr.get(0);
//                if (status==0) {
//                    Fragment fragment = new CustomerProfile();
//                    if (savedInstanceState == null) {
//                        transaction.addToBackStack(null);
//                        transaction.replace(R.id.fragment_container, fragment).commit();;
//                    }
//                } else if (status==1) {
//                    Fragment fragment = new TutorProfile();
//                    if (savedInstanceState == null) {
//                        transaction.addToBackStack(null);
//                        transaction.replace(R.id.fragment_container, fragment).commit();;
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        loadingDialog.dismiss();
        super.onDestroyView();
        closeLoadingDialog();

//        myRef.removeEventListener(listViewListener);
//        listViewListener=null;
//        myRef=null;
//        listview = binding.featuresList;
//        listview.setAdapter(null);
//        binding = null;
//        adapter.killTargets();
//        adapter = null;
    }

//    public void replaceFragment(Fragment someFragment) {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_container, someFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    /* close Loading Dialog when all fragment resources ready */
    public void closeLoadingDialog() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /* wait until the adapter loaded all images */
                while(!adapter.isAllResourcesReady()) { }
                /* hide loading dialog (fragment resources ready) */
                loadingDialog.cancel();
            }
        });
    }
}