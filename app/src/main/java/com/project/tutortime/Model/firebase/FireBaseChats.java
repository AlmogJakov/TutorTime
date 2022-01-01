package com.project.tutortime.Model.firebase;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.Controller.chats.Chat;
import com.project.tutortime.Controller.chats.ChatsAdapter;
import com.project.tutortime.Controller.search.FullTutorCard;
import com.project.tutortime.R;
import com.project.tutortime.View.MessageActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FireBaseChats {

    public static void readChats(List<Chat> chats, ChatsAdapter chatsAdapter,String userID){
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /* add the chats to the list and notify the adapter on the changes */
                        for (DataSnapshot dss : snapshot.getChildren()) {
                            Chat chat = (Chat) dss.getValue(Chat.class);
                            if(chat != null){
                                chats.add(chat);
                            }
                        }
                        Collections.reverse(chats);
                        chatsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void removeChat(String userID,String chatID){
        FirebaseDatabase.getInstance().getReference().child("chats").child(userID)
                .child(chatID).removeValue();
    }
    public static void markChatAsRead(String chatID,String userID){
        FirebaseDatabase.getInstance().getReference().child("chats").child(userID)
                .child(chatID).child("read").setValue(1);
    }

    public static void setLastSeen(String userID, String teacherID, Context context,TextView lastSeen) {
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userID)) { //get the other user last seen time
            FirebaseDatabase.getInstance().getReference().child("users").child(teacherID).child("lSeen").
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String status = context.getResources().getString(R.string.offline); // in case that the user didnt visit the chats at all
                            try {
                                long time = snapshot.getValue(long.class); //get the value from database
                                status = context.getResources().getString(R.string.lastSeen) + DateFormat.format("dd-MM(HH:mm)", time);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /* set the status to the last seen value*/
                            lastSeen.setText(status);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else { //
            FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("lSeen")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String status = context.getResources().getString(R.string.offline); // in case that the user didnt visit the chats at all
                            try {
                                long time = snapshot.getValue(long.class); //get the value from database
                                status = context.getResources().getString(R.string.lastSeen) + DateFormat.format("dd-MM(HH:mm)", time);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /* set the status to the last seen value*/
                            lastSeen.setText(status);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
    public static boolean thereIsChat(String userID,String teacherID){
        final boolean[] thereIsChat = {false};
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dss : snapshot.getChildren()) {
                            Chat chat = (Chat) dss.getValue(Chat.class);
                            if (chat != null) {
                                /* check if there is an active chat */
                                if (chat.getTeacherID().equals(teacherID)) {
                                    thereIsChat[0] = true;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return thereIsChat[0];

    }
    public static void addChat(String studentID, String teacherID, String chatID, HashMap<String,Object> map){
        FirebaseDatabase.getInstance().getReference().child("chats").child(teacherID).child(chatID).setValue(map);
        FirebaseDatabase.getInstance().getReference().child("chats").child(studentID).child(chatID).setValue(map);
    }
    public static void openActiveChat(String userID,String teacherID,Context context){
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dss : snapshot.getChildren()) {
                            Chat chat = (Chat) dss.getValue(Chat.class);
                            if (chat != null) {
                                /* check if this is the active chat  */
                                if (chat.getTeacherID().equals(teacherID)) {
                                    /* this is correct chat - open this chat  */
                                    Intent intent = new Intent(context, MessageActivity.class);
                                    intent.putExtra("studentName", chat.getStudentName());
                                    intent.putExtra("student", chat.getStudentID().toString());
                                    intent.putExtra("teacher", chat.getTeacherID().toString());
                                    intent.putExtra("chat", chat.getChatID().toString());
                                    context.startActivity(intent);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public static void openNewChat(String userID,String teacherID,String teacherName,String imageUrl,Context context){
        FirebaseDatabase.getInstance().getReference().child("users").child(userID)
                .child("fName").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String studentName = snapshot.getValue(String.class);
                        /* send the teacher thats new chat is received */
                        FireBaseNotifications.sendNotification(teacherID,"Chat",studentName);
                        /* add the chat to the database and get the chatID */
                        String chatID = createChatID(teacherID);
                        HashMap<String, Object> chatMap = new HashMap<>();
                        chatMap.put("lastMessage", "Chat with " + studentName + " is active now");
                        chatMap.put("teacherID", teacherID);
                        chatMap.put("studentID", userID);
                        chatMap.put("teacherName", teacherName);
                        chatMap.put("studentName", studentName);
                        chatMap.put("chatID", chatID);
                        chatMap.put("imageUrl", imageUrl);
                        chatMap.put("read",0);

                        addChat(userID, teacherID,chatID,chatMap);
                        /* open the new chat */
                        if (chatID != null) {
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("studentName", studentName);
                            intent.putExtra("student", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            intent.putExtra("teacher", teacherID);
                            intent.putExtra("chat", chatID);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public static String createChatID(String userID){
        return FirebaseDatabase.getInstance().getReference().child("chats").child(userID).push().getKey();
    }



}
