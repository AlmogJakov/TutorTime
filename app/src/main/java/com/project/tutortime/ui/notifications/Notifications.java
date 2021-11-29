package com.project.tutortime.ui.notifications;

import androidx.lifecycle.Observer;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
<<<<<<< HEAD
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
=======
import androidx.lifecycle.ViewModelProvider;
>>>>>>> 41ee5177b7cb0d7262d9ab187577ecbfc2e89a63

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
<<<<<<< HEAD

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.databinding.NotificationsFragmentBinding;
import com.project.tutortime.databinding.TeacherCardFragmentBinding;
import com.project.tutortime.ui.teachercard.TeacherCardViewModel;
=======
import com.project.tutortime.databinding.FragmentNotificationsBinding;
>>>>>>> 41ee5177b7cb0d7262d9ab187577ecbfc2e89a63

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Notifications extends Fragment {
    private String UserID;
    private String TeacherEmail;
    private String UserEmail;
    private String Subject;
    private String FormOfLearning;
    private String Remarks;
    private String RequestStatus;
    private boolean isNote = true;


<<<<<<< HEAD
    //private NotificationsViewModel NotificationsViewModel;
    private NotificationsFragmentBinding binding;
    private RecyclerView recyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notifications> notifications;

    public Notifications(){

    }

    public Notifications(String teacherEmail, String userEmail, String subject,
                         String formOfLearning, String remarks, String requestStatus,String userid) {
        TeacherEmail = teacherEmail;
        UserEmail = userEmail;
        Subject = subject;
        FormOfLearning = formOfLearning;
        Remarks = remarks;
        RequestStatus = requestStatus;
        UserID = userid;
        isNote = true;
    }

    public boolean isNote() {
        return isNote;
    }

    public void setNote(boolean note) {
        isNote = note;
    }

    public String getTeacherEmail() {
        return TeacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        TeacherEmail = teacherEmail;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getFormOfLearning() {
        return FormOfLearning;
    }

    public void setFormOfLearning(String formOfLearning) {
        FormOfLearning = formOfLearning;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getRequestStatus() {
        return RequestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        RequestStatus = requestStatus;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.notifications_fragment,container,false);
       recyclerView = view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       notifications = new ArrayList<>();
       notificationsAdapter = new NotificationsAdapter(getContext(),notifications);
       recyclerView.setAdapter(notificationsAdapter);
       readNotifications();
       return view;
    }

    private void readNotifications() {
        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dss: snapshot.getChildren()){
                            notifications.add(dss.getValue(Notifications.class));
                        }
                        Collections.reverse(notifications);
                        notificationsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
=======
    private NotificationsViewModel NotificationsViewModel;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        NotificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
>>>>>>> 41ee5177b7cb0d7262d9ab187577ecbfc2e89a63
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}