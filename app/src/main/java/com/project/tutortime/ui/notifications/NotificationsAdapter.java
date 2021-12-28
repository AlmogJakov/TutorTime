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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.firebase.userObj;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.List;
/**
 * This class adapter the Notification fragment and the view holder
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{

    private final Context mContext;
    private final List<Notifications> mNotifications; //list of the current notifications
    private int unReadNotifications;
    private DatabaseReference ref;




    public NotificationsAdapter(Context mContext, List<Notifications> mNotifications,int unReadNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
        this.unReadNotifications = unReadNotifications;
        this.ref = FirebaseDatabase.getInstance().getReference();
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

        Notifications notifications = mNotifications.get(position);//get the current notification
        holder.text.setText(notifications.getText());
        holder.title.setText(notifications.getTitle());
        //if the user didnt mark the notifcation as "read" invisible new alert
        if(notifications.getRead()==1){
            holder.newAlert.setVisibility(View.GONE);
            holder.readBtn.setVisibility(View.GONE);
        }// delete notifications from view and database
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(notifications,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });
        holder.readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notifications.getNotificationID())
                        .child("read").setValue(1);
                holder.readBtn.setVisibility(View.GONE);
                holder.newAlert.setVisibility(View.GONE);
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