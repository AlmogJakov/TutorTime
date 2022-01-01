package com.project.tutortime.Model.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.Controller.chats.Message;
import com.project.tutortime.Controller.chats.MessageAdapter;

import java.util.HashMap;
import java.util.List;

public class FireBaseMessages {

    public static void readMessaages(String chatID, List<Message> messages, MessageAdapter messageAdapter){
        FirebaseDatabase.getInstance().getReference().child("messages").child(chatID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Message messageModel = snapshot.getValue(Message.class);
                        messages.add(messageModel);
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void updateLastMessage(String userID,String chatID,String text){
        FirebaseDatabase.getInstance().getReference().child("chats").child(userID).child(chatID).child("lastMessage").setValue(text);
    }
    public static void addMessage(String chatID, String messageID, HashMap<String,Object> map){
        FirebaseDatabase.getInstance().getReference().child("messages").child(chatID).child(messageID).setValue(map);
    }
    public static String createMessageID(String chatID){
       return FirebaseDatabase.getInstance().getReference().child("messages").child(chatID).push().getKey();
    }

}
