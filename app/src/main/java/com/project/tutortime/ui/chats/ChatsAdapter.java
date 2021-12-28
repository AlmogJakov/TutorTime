package com.project.tutortime.ui.chats;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.MessageActivity;
import com.project.tutortime.R;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;
import com.project.tutortime.ui.notifications.Notifications;
import com.project.tutortime.ui.search.Search;
import com.project.tutortime.ui.search.SearchResults;
import com.project.tutortime.ui.search.TeacherCard;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * This class adapter the chat with  the view
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

    /**
     * This method bind between the chat data and the view
     * here we set the chat data and the buttons
     */
    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Chat chat = mChats.get(position); //get the current chat
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // set the chat to the other user data
        if(userID.equals(chat.getStudentID())){
            holder.UserName.setText(chat.getTeacherName());//set name
            if(chat.getImageUrl()!=null) { //this user already set profile image
                loadUserImage(chat.getImageUrl(),holder.ProfilePicture); //load the image
            }
            else{ // if the teacher didn't added a profile picture set the image to default
                holder.ProfilePicture.setImageResource(R.drawable.profile);
            }
        }
        else{// this is the teacher
            holder.UserName.setText(chat.getStudentName());
            holder.ProfilePicture.setImageResource(R.drawable.profile);
        }
        holder.lastMessage.setText(chat.getLastMessage());//set the last message that sent
        //set the online status Green for online Gray for offline
        setOnlineStatus(holder.userStatus,chat.getStudentID(),chat.getTeacherID());
        //allow the user to remove chat
        holder.deleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//delete the chat from the view and database
                deleteChatDialog(chat,holder.getAdapterPosition(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });//when click on profile picture show the user the teacher card if he is teacher
        holder.ProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //itay add here the teacherCard fragment - only if the user is teacher !


            }
        }); //when click on open , the chat will open
        holder.Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the activity and pass the chat data to the message activity
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("studentName",chat.getStudentName());
                intent.putExtra("student",chat.getStudentID().toString());
                intent.putExtra("teacher",chat.getTeacherID().toString());
                intent.putExtra("chat",chat.getChatID().toString());
                mContext.startActivity(intent);
            }
        });
    }


    /**
     * return the chats active amount
     * @return chat amounts
     */
    @Override
    public int getItemCount() {
        return mChats.size();
    }

    /**
     * This class hold the chats view objects
     */
    public class ChatsViewHolder extends RecyclerView.ViewHolder {

        public TextView UserName;
        public TextView lastMessage;
        public ImageView Open;
        public ImageView ProfilePicture;
        public MaterialCardView userStatus;
        public ImageButton deleteChat;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName = (TextView)itemView.findViewById(R.id.UserNameChat);
            lastMessage = (TextView)itemView.findViewById(R.id.last_message);
            Open = (ImageView)itemView.findViewById(R.id.open);
            ProfilePicture = (ImageView)itemView.findViewById(R.id.profile_image_chat);
            userStatus = (MaterialCardView) itemView.findViewById(R.id.image_cv);
            deleteChat = (ImageButton) itemView.findViewById(R.id.delete_chat);

        }
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


    /**
     * This method check if the user is online and set the
     * background to green or black depend on the user status
     */
    private void setOnlineStatus(MaterialCardView userStatus,String userID,String teacherID){
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userID)) { //get the other user online status
            FirebaseDatabase.getInstance().getReference().child("users").child(teacherID).child("isOnline")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int status = snapshot.getValue(int.class);
                            if(status==1){//other user is online - Green
                                userStatus.setStrokeColor(Color.parseColor("#418e2d"));
                            }
                            else{ //offline -Gray
                                userStatus.setStrokeColor(Color.parseColor("#b4c9d6"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("isOnline")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int status = snapshot.getValue(int.class);
                            if(status==1){//other user is online
                                userStatus.setStrokeColor(Color.parseColor("#418e2d"));
                            }
                            else{
                                userStatus.setStrokeColor(Color.parseColor("#b4c9d6"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    /**
     * This method load the user image from given url and set the imageview
     * @param url image url
     * @param image the imageview to load
     */
    private void loadUserImage(String url,ImageView image){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(url);
        Glide.with(mContext)
                .asBitmap()
                .load(storageReference)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        image.setImageBitmap(resource);

                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }
    private void updateLastMessage(String chatID,TextView lastMessage){
        FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatID).child("lastMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String msg = snapshot.getValue(String.class);
                lastMessage.setText(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}




