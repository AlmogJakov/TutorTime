package com.project.tutortime.Model.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.Controller.notifications.Notifications;
import com.project.tutortime.Controller.notifications.NotificationsAdapter;
import com.project.tutortime.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FireBaseNotifications {


    public static void readNotifications(List<Notifications> notifications, NotificationsAdapter notificationsAdapter) {
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
    public static String createNotificationID(String userID){
        return FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).push().getKey();
    }
    public static void getReadNotification(String notificationID){
        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notificationID)
                .child("read").setValue(1);
    }
    public static void removeNotification(String notificationID,String userID){
        FirebaseDatabase.getInstance().getReference().child("notifications").child(userID)
                .child(notificationID).removeValue();
    }
    public static void sendNotification(String userID,String title,String teacherName){
        HashMap<String, Object> map = new HashMap<>();
        String notificationID = createNotificationID(userID);
        map.put("notificationID",notificationID);
        map.put("title",title);
        map.put("sentFrom",teacherName);
        map.put("read",0);
        if (notificationID != null)
            FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(notificationID).setValue(map);
    }
}
