package com.project.tutortime.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.tutortime.adapter.TutorAdapter;

import com.project.tutortime.adapter.TutorAdapterItem;
import com.project.tutortime.databinding.FragmentHomeBinding;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    TextView notificationsText;
    ListView listview;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        notificationsText = binding.notificationsText;
        listview = binding.featuresList;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /* Init 'TutorAdapterItem' list for the adapter */
                List<TutorAdapterItem> tutorsToShow = new ArrayList<>();
                Random random = new Random();
                int tutorsNum = (int)dataSnapshot.child("teachers").getChildrenCount();
                /* Init set to store 3 random numbers */
                Set<Integer> data = new LinkedHashSet<>();
                /* add 3 random numbers in range (0,teachersNum) [each indicates tutor index] */
                while (data.size()<3 && tutorsNum>data.size()) {
                    int rand = random.nextInt(tutorsNum);
                    data.add(rand);
                }
                int counter = 0;
                for(DataSnapshot ds : dataSnapshot.child("teachers").getChildren()) {
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
                    }
                    counter++;
                }
                /* init the adapter with the 'tutorsToShow' list */
                final TutorAdapter adapter = new TutorAdapter(getContext(), tutorsToShow);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}