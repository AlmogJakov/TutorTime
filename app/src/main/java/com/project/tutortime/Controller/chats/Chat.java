package com.project.tutortime.Controller.chats;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class represent chat and allows to read the chat information from the database
 */
public class Chat extends Fragment {
    //chat data
    private String lastMessage;
    private String studentID;
    private String teacherID;
    private String chatID;
    private String studentName;
    private String teacherName;
    private String imageUrl;
    private int read;

    //tools
    private RecyclerView recyclerView;
    private ChatsAdapter chatsAdapter;
    private List<Chat> chats;


    public Chat(String lastMessage, String studentID, String teacherID, String chatID, String studentName, String teacherName) {
        this.lastMessage = lastMessage;
        this.studentID = studentID;
        this.teacherID = teacherID;
        this.chatID = chatID;
        this.studentName = studentName;
        this.teacherName = teacherName;
    }
    public Chat(){

    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }


    /**
     * set the view , init the chat adapter and the chat list
     *
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        /* set the chat fragment layout and the recycler view */
        View view = inflater.inflate(R.layout.chats_fragment,container,false);
        recyclerView = view.findViewById(R.id.recycler_view_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setLastSeenStatus();
        /* init the chat list and chat adapter */
        chats = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(getContext(),chats); //init the adapter
        recyclerView.setAdapter(chatsAdapter);
        readChats(); // read chats with listener to get the new chats
        return view;
    }

    /**
     * This method allows to read the  chat data from the database
     */
    private void readChats() {
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * This method update the last seen status in the users database when the user enter the chats
     */
    private void setLastSeenStatus(){
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                child("lSeen").setValue(new Date().getTime());
    }


}