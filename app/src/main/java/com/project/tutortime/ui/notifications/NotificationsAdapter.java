package com.project.tutortime.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.firebase.userObj;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{

    private final Context mContext;
    private final List<Notifications> mNotifications;


    public NotificationsAdapter(Context mContext, List<Notifications> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationsAdapter.NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notifications notifications = mNotifications.get(position);
        getUser(notifications.getUserID(),holder.UserName);
        holder.Remarks.setText(notifications.getRemarks());
        if(notifications.isNote()){
            holder.Open.setVisibility(View.GONE);
        }
        else{

        }
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        public TextView UserName;
        public TextView Remarks;
        public ImageView Open;
        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName = itemView.findViewById(R.id.UserName);
            Remarks = itemView.findViewById(R.id.Remarks);
            Open = itemView.findViewById(R.id.open);
        }
    }
    private void getUser(String UserID,TextView username){
        FirebaseDatabase.getInstance().getReference().child("users").child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userObj userobj = snapshot.getValue(userObj.class);
                if(userobj!=null){
                username.setText(userobj.getfName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
