package com.project.tutortime.ui.notifications;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        if(notifications.getRequestStatus().equals("")){
            notifications.setNote(true);
        }
        getUser(notifications.getUserID(),holder.UserName);
        holder.Remarks.setText(notifications.getRemarks());
        if(notifications.isNote()){
            holder.Open.setVisibility(View.GONE);
        }
        else{
            holder.popup.setContentView(R.layout.popup_notifications);
            holder.Open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.popup.show();
                }
            });
            holder.popup.findViewById(R.id.closePopup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.popup.dismiss();
                }
            });
            //edit the popup window textview
            TextView remarks = (TextView)holder.popup.findViewById(R.id.remarks);
            TextView from = (TextView)holder.popup.findViewById(R.id.from);
            TextView subject = (TextView)holder.popup.findViewById(R.id.subject);
            TextView requestStatus = (TextView)holder.popup.findViewById(R.id.requestStatus);
            TextView formOfLearning = (TextView) holder.popup.findViewById(R.id.formOfLearning);
            remarks.append(notifications.getRemarks());
            from.append(notifications.getUserEmail());
            subject.append(notifications.getSubject());
            requestStatus.append(notifications.getRequestStatus());
            formOfLearning.append(notifications.getFormOfLearning()+" ");
            //allow the user to accept or decline the request
            Button Accept = (Button)holder.popup.findViewById(R.id.acceptBtn);
            Button Decline = (Button)holder.popup.findViewById(R.id.declineBtn);
            Accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.popup.dismiss();
                }
            });
            Decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.popup.dismiss();
                }
            });


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
        public Dialog popup;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            popup = new Dialog(mContext);
            UserName = (TextView)itemView.findViewById(R.id.UserName);
            Remarks = (TextView)itemView.findViewById(R.id.Remarks);
            Open = (ImageView)itemView.findViewById(R.id.open);

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
