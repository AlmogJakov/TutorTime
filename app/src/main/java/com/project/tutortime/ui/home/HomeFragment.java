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

import com.google.firebase.auth.FirebaseAuth;
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
    TextView hello;
    ListView listview;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        hello = binding.helloName;
        listview = binding.featuresList;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("users").child(userID).child("fName").getValue(String.class);
                //hello.append(" " + name + ",");
                hello.setText("שלום " + name + ",");
                //if (!dataSnapshot.child("search").exists()) return;
                //long counter = dataSnapshot.child("search").getChildrenCount();
                //String teacher = dataSnapshot.child("search").getChildren();
                //DatabaseReference refer = dataSnapshot.getRef();
                //Query query = refer.
                //System.out.println("Search childs: "+counter);
                List<TutorAdapterItem> teachersToShow = new ArrayList<>();
                Random random = new Random();
                int teachersNum = (int) dataSnapshot.child("teachers").getChildrenCount();
                Set<Integer> data = new LinkedHashSet<>();
                System.out.println("yto: ");
                while (data.size()<3 && teachersNum>=3) {
                    int rand = random.nextInt(teachersNum);
                    if (!data.contains(rand)) data.add(rand);
                    System.out.println("yo: ");
                }
                int counter = 0;
                for(DataSnapshot ds : dataSnapshot.child("teachers").getChildren()) {
                    if (data.contains(counter)) {
                        System.out.println("child: "+ds.getKey());
                        data.remove(counter);
                        teacherObj teacher = ds.getValue(teacherObj.class);
                        userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                        int subsNum = teacher.getSub().size();
                        Object[] subs = teacher.getSub().keySet().toArray();
                        String sub = (String) subs[random.nextInt(subsNum)];
                        if (user==null) {
                            System.out.println("Found tutor without user information. Tutor ID:" + ds.getKey());
                            continue;
                        }
                        TutorAdapterItem item = new TutorAdapterItem(user,teacher,sub);
                        teachersToShow.add(item);
                        System.out.println("teacher: "+teacher.getPhoneNum());
                        System.out.println("imgUrl: "+teacher.getImgUrl());
                        System.out.println("sub: "+sub);
                        System.out.println("user: "+user.getfName());
                    }
                    counter++;
                }

                String[] values = new String[] { "David", "Yogev", "Avihu"};
                final TutorAdapter adapter = new TutorAdapter(getContext(), teachersToShow);
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



//                        Bitmap image;
//                        if (teacher.getImgUrl()!=null) {
//                            StorageReference storageReference = storage.getReference().child(teacher.getImgUrl());
//                            try {
//                                //image = Glide.with(getContext()).asBitmap().load(storageReference).submit().get();
//                                image = Glide.with(getContext()).asBitmap()
//                                        .load(storageReference)
//                                        .centerCrop()
//                                        .into(500, 500)
//                                        .get();
//                            } catch (ExecutionException e) {
//                                e.printStackTrace();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//Glide.with(getContext()).load(storageReference).into(profile);