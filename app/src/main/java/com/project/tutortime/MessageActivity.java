package com.project.tutortime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
//import com.project.tutortime.ui.chats.Message;
import com.project.tutortime.ui.chats.MessageListAdapter;
import com.project.tutortime.ui.chats.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MessageActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList;
    private FloatingActionButton send;
    private EditText input;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_list);
        mMessageRecycler = findViewById(R.id.rv_messages);
        send = (FloatingActionButton) findViewById(R.id.sendBtn);
        input = (EditText)findViewById(R.id.input_text);
        Intent intent = getIntent();
        String studentID = intent.getStringExtra("student");
        String teacherID = intent.getStringExtra("teacher");
        String chatID = intent.getStringExtra("chat");
        String studentName = intent.getStringExtra("studentName");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(getApplicationContext(),messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);
        FirebaseDatabase.getInstance().getReference().child("messages").child(chatID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Message messageModel = snapshot.getValue(Message.class);
                        messageList.add(messageModel);

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
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"You need to write a message",Toast.LENGTH_LONG).show();
                }
                else {
                    sendMessage(chatID, input.getText().toString(), studentName, studentID, teacherID);
                    input.setText("");
                }
            }
        });
        mMessageAdapter.updateList(messageList);
    }


    private void sendMessage(String chatID,String Text,String senderName,String studentID,String teacherID){

        HashMap<String,Object> messageMap = new HashMap<>();
        String messageID = FirebaseDatabase.getInstance().getReference().child("messages").child(chatID).push().getKey();
        messageMap.put("messageText",Text);
        messageMap.put("senderID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        messageMap.put("senderName",senderName);
        messageMap.put("messageID",messageID);
        messageMap.put("time",new Date().getTime());
        if(messageID!=null)FirebaseDatabase.getInstance().getReference().child("messages").child(chatID).child(messageID).setValue(messageMap);
        FirebaseDatabase.getInstance().getReference().child("chats").child(studentID).child(chatID).child("lastMessage").setValue(Text);
        FirebaseDatabase.getInstance().getReference().child("chats").child(teacherID).child(chatID).child("lastMessage").setValue(Text);



    }

}