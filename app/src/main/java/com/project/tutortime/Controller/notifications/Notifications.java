package com.project.tutortime.Controller.notifications;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.Model.firebase.FireBaseNotifications;
import com.project.tutortime.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represent Notification and allows to read the notification data from the database
 */

public class Notifications extends Fragment {
    //notification data
    private String notificationID;
    private String title;
    private String sentFrom;
    private int read;

    //tools
    private RecyclerView recyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notifications> notifications;

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
        /* set the notifications fragment layout and the recycler view */
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        /* init the notifications list and notification adapter */
        notifications = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(getContext(),notifications);
        recyclerView.setAdapter(notificationsAdapter);
        /* read the notifications from the database */
        FireBaseNotifications.readNotifications(notifications,notificationsAdapter);
        return view;
    }

    /**
     * This method allows to read the notification data from the database
     * and update the notification adapter on any change
     */
    private void readNotifications() {
        /* get the data of the notification */
        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /* if some of the notification data has changed then it will update the adapter */
                        for (DataSnapshot dss : snapshot.getChildren()) {
                            Notifications notification = dss.getValue(Notifications.class);
                            if(notification != null) {
                                notifications.add(notification);
                            }
                        }
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
    }
}