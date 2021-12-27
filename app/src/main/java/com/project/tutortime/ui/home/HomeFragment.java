package com.project.tutortime.ui.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.tutortime.LoadingDialog;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;
import com.project.tutortime.adapter.TutorAdapter;

import com.project.tutortime.adapter.TutorAdapterItem;
import com.project.tutortime.databinding.FragmentHomeBinding;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;
import com.project.tutortime.ui.search.TeacherCard;

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
    TextView notificationsText;
    ListView listview;
    ValueEventListener listViewListener;
    TutorAdapter adapter;
    LoadingDialog loadingDialog;
    List<TutorAdapterItem> tutorsToShow = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /* show loading dialog until all fragment resources ready */
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
        /* END show loading dialog until all fragment resources ready */
        notificationsText = binding.notificationsText;
        listview = binding.featuresList;
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
                        int subsNum = teacher.getSub().size();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        myRef.removeEventListener(listViewListener);
//        listViewListener=null;
//        myRef=null;
//        listview = binding.featuresList;
//        listview.setAdapter(null);
//        binding = null;
//        adapter.killTargets();
//        adapter = null;
    }

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