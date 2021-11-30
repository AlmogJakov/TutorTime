package com.project.tutortime.ui.tutorprofile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.databinding.FragmentTutorProfileBinding;
import com.project.tutortime.firebase.FireBaseTeacher;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.firebaseBaseModel;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;

public class TutorProfile extends Fragment {

    private TutorProfileViewModel TutorProfileViewModel;
    private FragmentTutorProfileBinding binding;
    EditText fname, lname, pnumber, description;
    Button saveProfile, addSub, updateImage;
    String teacherID;
    ImageView img;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    FirebaseAuth fAuth;
    //Uri imageData;
    //String imgURL;
    //private DatabaseReference mDatabase;
    //private static final int GALLERY_REQUEST_COD = 1;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
    FireBaseUser fbUser = new FireBaseUser();
    FireBaseTeacher fbTeacher = new FireBaseTeacher();
    userObj user_obj;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TutorProfileViewModel = new ViewModelProvider(this).get(TutorProfileViewModel.class);
        binding = FragmentTutorProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.myTutorProfile;
        TutorProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        fname = binding.myFName;
        lname = binding.myLName;
        pnumber = binding.myPhoneNumber;
        description = binding.editDescription;
        saveProfile = binding.btnSaveProfile;
        addSub = binding.addSubject;
        updateImage = binding.btnUpdateImage;
        img = binding.imageView;
        subjectList = binding.subList;
        ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();

        return root;
    }

    public void onResume() {
        super.onResume();
        String userID = fAuth.getInstance().getCurrentUser().getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fname.setText(dataSnapshot.child("users").child(userID).child("fName").getValue(String.class));
                lname.setText(dataSnapshot.child("users").child(userID).child("lName").getValue(String.class));
                teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                pnumber.setText(dataSnapshot.child("teachers").child(teacherID).
                        child("phoneNum").getValue(String.class));
                description.setText(dataSnapshot.child("teachers").child(teacherID).
                        child("description").getValue(String.class));
                String imgAdd =dataSnapshot.child("teachers").child(teacherID).child("imgUrl").getValue(String.class);
                if(imgAdd != null) {
                    Glide.with(getActivity()).load(dataSnapshot.child("teachers").child(teacherID).
                            child("imgUrl").getValue(String.class)).into(img);
                }

                for (DataSnapshot subSnapsot :  dataSnapshot.child("teachers").child(teacherID).
                        child("sub").getChildren()) {
                    subjectObj sub= new subjectObj(subSnapsot.child("sName").getValue(String.class),
                            subSnapsot.child("type").getValue(String.class),
                            subSnapsot.child("price").getValue(Integer.class),
                            subSnapsot.child("experience").getValue(String.class));
                    list.add(sub);
                }
                ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
                subjectList.setAdapter(a);
                a.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}