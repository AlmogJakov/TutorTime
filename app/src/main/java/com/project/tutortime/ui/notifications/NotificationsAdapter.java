package com.project.tutortime.ui.notifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.firebase.userObj;

import java.util.HashMap;
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
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder,int position) {
        Notifications notifications = mNotifications.get(position);
        getUser(FirebaseAuth.getInstance().getUid(),holder.UserName);
        holder.Remarks.setText(notifications.getRemarks());
        //the request was accepted
        switch (notifications.getRequestStatus()){
            case "Accepted":
                readContactInformation(holder,notifications);
                break;
            case "Waiting for response":
                readTeachingRequest(holder,notifications);
                break;
            //the request was decline or it is system notice
            default:
                holder.Open.setVisibility(View.GONE);
        }
        //allow the user to delete notification by pressing the bell icon
        holder.Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(notifications,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        public TextView UserName;
        public TextView Remarks;
        public ImageView Open;
        public ImageView Bell;
        public Dialog popup_request;
        public Dialog popup_accepted;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            popup_request = new Dialog(mContext);
            popup_accepted = new Dialog(mContext);
            UserName = (TextView)itemView.findViewById(R.id.UserName);
            Remarks = (TextView)itemView.findViewById(R.id.Remarks);
            Open = (ImageView)itemView.findViewById(R.id.open);
            Bell = (ImageView)itemView.findViewById(R.id.bell);

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
    private void CustomAlert( String message,String title ) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(title)
                .setIcon(R.drawable.bell)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }
    private void removeNotification(Notifications notifications, int position,String userID) {
        //remove from list view
        mNotifications.remove(notifications);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mNotifications.size());
        //remove from data base
        FirebaseDatabase.getInstance().getReference().child("notifications").child(userID)
                .child(notifications.getNotificationKey()).removeValue();
    }
    private void readContactInformation(NotificationsViewHolder holder,Notifications notifications ) {
        holder.popup_accepted.setContentView(R.layout.popup_notification_contact_information);
        TextView teacherName = holder.popup_accepted.findViewById(R.id.teacherName);
        TextView teacherEmail = holder.popup_accepted.findViewById(R.id.teacherEmail);
        TextView teacherPhoneNumber = holder.popup_accepted.findViewById(R.id.teacherPhoneNumber);
        teacherName.append(notifications.getTeacherName());
        teacherEmail.append(notifications.getTeacherEmail());
        teacherPhoneNumber.append(notifications.getPhoneNumber());
        holder.Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popup_accepted.show();
            }
        });
        holder.popup_accepted.findViewById(R.id.closePopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popup_accepted.dismiss();
            }
        });
    }

    private void readTeachingRequest(NotificationsViewHolder holder,Notifications notifications){
        holder.popup_request.setContentView(R.layout.popup_notifications);
        //when the user click on the door icon
        holder.Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popup_request.show();//show the popup window
            }
        });
        //allow the user to close the popup window with the X icon
        holder.popup_request.findViewById(R.id.closePopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popup_request.dismiss();
            }
        });
        //edit the popup window textview
        TextView remarks = (TextView)holder.popup_request.findViewById(R.id.remarks);
        TextView from = (TextView)holder.popup_request.findViewById(R.id.from);
        TextView subject = (TextView)holder.popup_request.findViewById(R.id.subject);
        TextView requestStatus = (TextView)holder.popup_request.findViewById(R.id.requestStatus);
        TextView formOfLearning = (TextView) holder.popup_request.findViewById(R.id.formOfLearning);
        remarks.append(notifications.getRemarks());
        from.append(notifications.getUserEmail());
        subject.append(notifications.getSubject());
        requestStatus.append(notifications.getRequestStatus());
        formOfLearning.append(notifications.getFormOfLearning()+" ");
        //accept or decline the request
        Button Accept = (Button)holder.popup_request.findViewById(R.id.acceptBtn);
        Button Decline = (Button)holder.popup_request.findViewById(R.id.declineBtn);
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendContactInformation(notifications.getSubject(),notifications.getSendTo(),notifications.getTeacherEmail());
                CustomAlert("Your contact information sent successfully","Notification");
                removeNotification(notifications,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                holder.popup_request.dismiss();
            }
        });
        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDeclineNotification(notifications.getSendTo(),notifications.getTeacherName());
                CustomAlert("You decided not accept this student request","Notification");
                removeNotification(notifications,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                holder.popup_request.dismiss();
            }
        });


    }
    private void sendContactInformation(String subject,String userID,String email){
        HashMap<String,Object> map = new HashMap<>();
        String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).push().getKey();
        map.put("TeacherEmail",email);
        map.put("TeacherName","");
        map.put("UserEmail","");
        map.put("Subject",subject);
        map.put("FormOfLearning","");
        map.put("Remarks","Your request was accepted!");
        map.put("RequestStatus","Accepted");
        map.put("PhoneNumber","");
        map.put("sendTo",userID);
        map.put("sentFrom",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("NotificationKey",key);
        if (key != null)
            FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(key).setValue(map);
    }
    private void sendDeclineNotification(String userID,String teacherName){
        HashMap<String,Object> map = new HashMap<>();
        String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).push().getKey();
        map.put("TeacherEmail","");
        map.put("TeacherName","");
        map.put("UserEmail","");
        map.put("Subject","");
        map.put("FormOfLearning","");
        map.put("Remarks",teacherName+" declined your request...");
        map.put("RequestStatus","Decline");
        map.put("PhoneNumber","");
        map.put("sendTo",userID);
        map.put("sentFrom",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("NotificationKey",key);
        if(key!=null)FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(key).setValue(map);
    }
    private void deleteDialog(Notifications notifications, int position,String userID){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Delete notification")
                .setIcon(R.drawable.bell)
                .setMessage("Are you sure?")
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).setNegativeButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                removeNotification(notifications,position,userID);
            }
        }).show();
    }




}