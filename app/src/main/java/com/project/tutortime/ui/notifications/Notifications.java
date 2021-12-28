package com.project.tutortime.ui.notifications;

import androidx.lifecycle.Observer;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;

import com.project.tutortime.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represent Notification and allows to read the notification data from the database
 */

public class Notifications extends Fragment {
    private String notificationID;
    private String text;
    private String title;
    private String sentFrom;
    private int read;


    private NotificationsViewModel NotificationsViewModel;
    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notifications> notifications;
    private int unReadNotifications;

    public Notifications(){

    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //set the notification fragment and the recycler view
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notifications = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(getContext(),notifications,unReadNotifications);
        recyclerView.setAdapter(notificationsAdapter);
        readNotifications(); //read each notification from the database
        return view;
    }

    /**
     * This method allows to read the notification data from the database
     */
    private void readNotifications() {
        //get the value of the notification
        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //if some of the notification data has changed then it will updated
                        for (DataSnapshot dss : snapshot.getChildren()) {
                            Notifications notification = dss.getValue(Notifications.class);
                            if(notification.getRead()==1){
                                unReadNotifications++;
                            }
                            notifications.add(notification);
                        }
                        unReadNotifications = notifications.size()-unReadNotifications;
                        Collections.reverse(notifications);
                        notificationsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}