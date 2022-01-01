package com.project.tutortime.Controller.notifications;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.R;

import java.util.List;
/**
 * This class adapter the Notification fragment and the view holder
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{

    private final Context mContext;
    private final List<Notifications> mNotifications; //list of all the notifications




    public NotificationsAdapter(Context mContext, List<Notifications> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //set the notification item layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationsAdapter.NotificationsViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder,int position) {

        Notifications notifications = mNotifications.get(position);//get the current notification
        /* get the type of the notification and set the correct text and title */
        switch (notifications.getTitle()){
            case "Register":
                holder.title.setText(mContext.getResources().getText(R.string.notificationRegisterTitle));
                holder.text.setText(mContext.getResources().getText(R.string.notificationRegisterText));
                break;
            case "TutorProfile":
                holder.title.setText(mContext.getResources().getText(R.string.notificationTutorProfileTitle));
                holder.text.setText(mContext.getResources().getText(R.string.notificationTutorProfileText));
                break;
            case "Chat":
                holder.title.setText(mContext.getResources().getText(R.string.notificationChatTitle));
                holder.text.setText(mContext.getResources().getText(R.string.notificationChatTextWith)
                       +" "+notifications.getSentFrom()+" "+mContext.getResources().getString(R.string.notificationChatTextIs) );
                break;
            case "Ranking":
                holder.title.setText(mContext.getResources().getText(R.string.notificationRankingTitle));
                holder.text.setText(mContext.getResources().getText(R.string.notificationRankingText));
                break;
        }
        /* if the user mark the notification as "read" - invisible new alert */
        if(notifications.getRead()==1){
            holder.newAlert.setVisibility(View.GONE);
            holder.readBtn.setVisibility(View.GONE); }
        /* delete notifications from view and database */
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(notifications,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });
        /* mark this notification as read set the view to invisible*/
        holder.readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("notifications")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notifications.getNotificationID())
                        .child("read").setValue(1);
                /* set the btns to invisible */
                holder.readBtn.setVisibility(View.GONE);
                holder.newAlert.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This method return the amount of the notifications in the list
     * @return amount of notifications
     */

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    /**
     * This class describes the notification view objects
     */

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        ImageView newAlert;
        ImageView readBtn;
        ImageView deleteBtn;
        TextView title;
        TextView text;


        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            newAlert = (ImageView)itemView.findViewById(R.id.new_alert);
            readBtn = (ImageView)itemView.findViewById(R.id.read_notification);
            deleteBtn = (ImageView)itemView.findViewById(R.id.delete_notification);
            title = (TextView)itemView.findViewById(R.id.notification_title);
            text = (TextView)itemView.findViewById(R.id.notification_text);

        }
    }

    /**
     * This method allows to remove notification from the list view and from the database
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
                .child(notifications.getNotificationID()).removeValue();
    }


    /**
     * This method create a dialog with the user to be sure that he wants the remove the notification
     * @param notifications the notification to remove
     * @param position the current position of the notification
     * @param userID the user id
     */
    private void deleteDialog(Notifications notifications, int position,String userID){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.deleteNotificationDialog))
                .setIcon(R.drawable.bell)
                .setMessage(mContext.getResources().getString(R.string.areYouSure))
                .setPositiveButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).setNegativeButton(mContext.getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                removeNotification(notifications,position,userID);
            }
        }).show();
    }

}