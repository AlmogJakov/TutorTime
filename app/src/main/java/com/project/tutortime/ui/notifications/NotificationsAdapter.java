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
/**
 * This class adapter the Notification fragment and the view holder
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{

    private final Context mContext;
    private final List<Notifications> mNotifications; //list of the current notifications


    public NotificationsAdapter(Context mContext, List<Notifications> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //set the notification layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationsAdapter.NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder,int position) {
        //set the notification the the current position
        Notifications notifications = mNotifications.get(position);
        getUser(FirebaseAuth.getInstance().getUid(),holder.UserName);//get the relevant user data
        holder.Remarks.setText(notifications.getRemarks()); //set the notification remarks
        //the types of the notifications handle here
        switch (notifications.getRequestStatus()){
            case "Accepted":
                readContactInformation(holder,notifications);
                break;
            case "Waiting for response":
                readTeachingRequest(holder,notifications);
                break;
            //the request was decline or it is a system notice
            default:
                holder.Open.setVisibility(View.GONE);
        }
        //allow the user to delete notification by pressing on the bell icon
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

    /**
     * This class holds the notification view objects ass well as the popup windows
     */

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

    /**
     * This method set the current user data
     * @param UserID the id of the user
     * @param username the name of the user
     */
    private void getUser(String UserID,TextView username){
        //get the user data from the database
        FirebaseDatabase.getInstance().getReference().child("users").child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userObj userobj = snapshot.getValue(userObj.class);
                if(userobj!=null){
                    username.setText(userobj.getfName());//set the user first name
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * This message allows to create a custom alert dialog with the user
     * @param message - the message of the alert
     * @param title - the title of the alert
     */
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

    /**
     * This method allows to remove notfication from the list view and from the database
     * @param notifications - the notification that the user wants to remove
     * @param position - the current position in the list
     * @param userID - the id of the user
     */
    private void removeNotification(Notifications notifications, int position,String userID) {
        //remove from list view
        mNotifications.remove(notifications);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mNotifications.size());
        //remove from data base
        FirebaseDatabase.getInstance().getReference().child("notifications").child(userID)
                .child(notifications.getNotificationKey()).removeValue();
    }

    /**
     * This method read the teacher contact information
     * @param holder the view holder
     * @param notifications the notification with the contact information
     */
    private void readContactInformation(NotificationsViewHolder holder,Notifications notifications ) {
        //set the contact information
        holder.popup_accepted.setContentView(R.layout.popup_notification_contact_information);
        TextView teacherName = holder.popup_accepted.findViewById(R.id.teacherName);
        TextView teacherEmail = holder.popup_accepted.findViewById(R.id.teacherEmail);
        TextView teacherPhoneNumber = holder.popup_accepted.findViewById(R.id.teacherPhoneNumber);
        //add it to the popup window
        teacherName.append(notifications.getTeacherName());
        teacherEmail.append(notifications.getTeacherEmail());
        teacherPhoneNumber.append(notifications.getPhoneNumber());
        //allow the user to show the popup window
        holder.Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popup_accepted.show();
            }
        });
        //allow the user to close the popup window
        holder.popup_accepted.findViewById(R.id.closePopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popup_accepted.dismiss();
            }
        });
    }

    /**
     * This method read the teaching request and allows the user to accept the request or decline it
     * @param holder the view hlder
     * @param notifications the notification of the teaching request to read
     */

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
        //allow the user to accept the request
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendContactInformation(notifications.getSubject(),notifications.getSendTo(),notifications.getTeacherEmail());
                addToChats(notifications);
                CustomAlert("Your contact information sent successfully,and your chat is active","Notification");
                //removed automatically since the user already accepted it
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

    /**
     * This method send the contact information of the teacher to the user as a notification
     * @param subject the subject of the teaching request
     * @param userID the id of the user
     * @param email the email of the teacher
     */
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
    /**
     * This method send decline notification to the user
     * @param teacherName the first name of the teacher
     * @param userID the id of the user
     */
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

    /**
     * This method create a dialog with the user to be sure that he wants the remove the notification
     * @param notifications the notification to remove
     * @param position the current position of the notification
     * @param userID the user id
     */
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

    private void addToChats(Notifications notifications){
        HashMap<String, Object> mapTeacher = new HashMap<>();
        String studentid = "JyoevIP7cpZGMocgpWk5g4pLwM43";//student
        String teacherid = "L9g6rBsZqigUC4zTPSBwGNBK5p73";//teacher;
        //teacher chats
        String key = FirebaseDatabase.getInstance().getReference().child("chats").child(studentid).push().getKey();
        mapTeacher.put("lastMessage","Chat with "+notifications.getTeacherName()+" is active now");//student name should be here
        mapTeacher.put("teacherID",studentid);
        mapTeacher.put("studentID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        mapTeacher.put("teacherName",notifications.getTeacherName());
        mapTeacher.put("studentName",notifications.getTeacherName());
        mapTeacher.put("chatID",key);
        if (key != null)
            FirebaseDatabase.getInstance().getReference().child("chats").child(studentid).child(key).setValue(mapTeacher);
        //user chats
        HashMap<String, Object> mapStudent = new HashMap<>();
        String key2 = FirebaseDatabase.getInstance().getReference().child("chats").child(teacherid).push().getKey();
        mapTeacher.put("lastMessage","Chat with "+notifications.getTeacherName()+" is active now");//student name should be here
        mapTeacher.put("teacherID",teacherid);
        mapTeacher.put("studentID",studentid);
        mapTeacher.put("teacherName",notifications.getTeacherName());
        mapTeacher.put("studentName",notifications.getTeacherName());
        mapStudent.put("chatID",key2);
        if (key != null)
            FirebaseDatabase.getInstance().getReference().child("chats").child(teacherid).child(key).setValue(mapStudent);

    }




}