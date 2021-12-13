package com.project.tutortime.ui.chats;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.firebase.userObj;
import com.project.tutortime.ui.notifications.Notifications;

import java.util.List;

/**
 * This class adapter the chat fragment and the view holder
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    private final Context mContext;
    private final List<Chat> mChats; //list of all the active chats

    public ChatsAdapter(Context mContext, List<Chat> mChats) {
        this.mContext = mContext;
        this.mChats = mChats;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //set the view to the chats item
        View view = LayoutInflater.from(mContext).inflate(R.layout.chats_item,parent,false);
        return new ChatsAdapter.ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        Chat chat = mChats.get(position); //get the specific chat
        getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(),holder.UserName,holder.ProfilePicture);//user information
        holder.lastMessage.setText(chat.getLastMessage());//set the last message that sent
        holder.UserName.setText(chat.getSenderName()); // set the sender UserName
        holder.ProfilePicture.setOnClickListener(new View.OnClickListener() { //when click on profile picture he can remove the chat
            @Override
            public void onClick(View v) {
                deleteChatDialog(chat,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

    }


    //
    @Override
    public int getItemCount() {
        return mChats.size();
    }

    /**
     * This class hold the chats objects ass well as the chat window
     */
    public class ChatsViewHolder extends RecyclerView.ViewHolder {

        public TextView UserName;
        public TextView lastMessage;
        public ImageView Open;
        public ImageView ProfilePicture;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName = (TextView)itemView.findViewById(R.id.UserNameChat);
            lastMessage = (TextView)itemView.findViewById(R.id.last_message);
            Open = (ImageView)itemView.findViewById(R.id.open);
            ProfilePicture = (ImageView)itemView.findViewById(R.id.profilePicture);

        }
    }

    /**
     * this method set the user information
     * @param UserID the id of the user
     * @param username the name of the user
     * @param profilePic the profile picture of the user
     */
    private void getUser(String UserID,TextView username,ImageView profilePic) {
        FirebaseDatabase.getInstance().getReference().child("chats").child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Chat chatObj = snapshot.getValue(Chat.class);
                if (chatObj != null) {
                    //username.setText(chatObj.getSenderName());
                    profilePic.setImageResource(R.drawable.logo);//need to change to sender profile picture
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * This method allows to remove the chat from the list view and from the database
     * @param chat the chat that the user want to remove
     * @param position the current position of the chat in the view
     * @param userID the id of the user
     */

        private void removeChat(Chat chat, int position,String userID) {
            //remove from list view
            mChats.remove(chat);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mChats.size());
            //remove from data base
            FirebaseDatabase.getInstance().getReference().child("chats").child(userID)
                    .child(chat.getChatID()).removeValue();
        }

    /**
     * This method creating a dialog with the user and when click yes the chat will removed
     * otherwise nothing will happened
     */
    private void deleteChatDialog(Chat chat, int position,String userID){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Delete Chat")
                .setIcon(R.drawable.bell)
                .setMessage("Are you sure?")
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).setNegativeButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                removeChat(chat,position,userID);
            }
        }).show();
    }


    }


