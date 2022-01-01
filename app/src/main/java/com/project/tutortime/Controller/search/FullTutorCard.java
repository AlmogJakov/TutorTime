package com.project.tutortime.Controller.search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.View.LoadingDialog;
import com.project.tutortime.View.MessageActivity;
import com.project.tutortime.R;
//import com.project.tutortime.datafindViewById(R.id.FragmentTeacherCardBinding;
import com.project.tutortime.Model.firebase.rankObj;
import com.project.tutortime.Model.firebase.tutorObj;
import com.project.tutortime.Model.firebase.userObj;
import com.project.tutortime.Controller.chats.Chat;

import java.util.HashMap;


public class FullTutorCard extends AppCompatActivity {
    ImageView image;
    TextView price, description, subject, type, opinion, name, place;
    Button send, phone;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String phoneNum;
    ImageView titleBackground;
    CardView profileImageBox, rate;
    RatingBar rating;
    LoadingDialog loadingDialog;
    userObj user;
    tutorObj teacher;
    String sub;
    int kindOfRank = -1, editRank = -1;
    //chat data
    boolean thereIsChat = true;
    boolean firstTimeSending = true;
    String studentName;
    String chatID;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        setContentView(R.layout.activity_full_tutor_card);
        initThereIsChat();

//        findViewById(R.id.)
        image = findViewById(R.id.profile_image);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        send = findViewById(R.id.send);
        phone = findViewById(R.id.phone);
        subject = findViewById(R.id.subject);
        type = findViewById(R.id.type);
        name = findViewById(R.id.name);
        place = findViewById(R.id.area);
        titleBackground = (ImageView) findViewById(R.id.title_background);
        profileImageBox = (CardView)findViewById(R.id.profile_image_box);
        rating = (RatingBar)findViewById(R.id.rating);
        opinion = findViewById(R.id.opinion);
        rate = findViewById(R.id.rateTutor);


        Bundle bundle = getIntent().getExtras();
        user = (userObj) bundle.getSerializable("user");
        teacher = (tutorObj) bundle.getSerializable("teacher");
        sub = bundle.getString("sub");

        phoneNum = teacher.getPhoneNum();
        if (teacher.getRank().getUserRating() == null){
            opinion.setText("0 " + getResources().getString(R.string.opinion));
        }
        else {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (teacher.getRank().getUserRating().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                HashMap<String, Integer> hashMap = teacher.getRank().getUserRating();
                kindOfRank = ((int)hashMap.get(id))-1;
                editRank = kindOfRank;
            }
            opinion.setText(teacher.getRank().getUserRating().size() + " " + getResources().getString(R.string.opinion));
        }
        name.setText(user.getfName() +" "+ user.getlName());
        String s="";
        switch (teacher.getSub().get(sub).getType()) {
            case "both": s = getResources().getString(R.string.Online_Frontal);break;
            case "online": s = getResources().getString(R.string.Online);break;
            case "frontal": s = getResources().getString(R.string.Frontal);break;
        }
        subject.setText(sub);
        type.setText(s);
        place.setText("\uD83D\uDCCD " + user.getCity());
        description.setText(getResources().getString(R.string.Description)+teacher.getDescription());
        price.setText(teacher.getSub().get(sub).getPrice()+"₪");
        rating.setRating(teacher.getRank().getAvgRank());

        String[] typeRank = {"\uD83D\uDE41 - ⭐", "\uD83D\uDE15 - ⭐⭐", "\uD83D\uDE10 - ⭐⭐⭐", "\uD83D\uDE42 - ⭐⭐⭐⭐","\uD83D\uDE03 - ⭐⭐⭐⭐⭐"};
        setSpinner(rate, typeRank, kindOfRank);

        String imageLink = teacher.getImgUrl();
        if (imageLink!=null) {
            StorageReference storageReference = storage.getReference().child(imageLink);
            Glide.with(this)
                    .asBitmap()
                    .load(storageReference)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            image.setImageBitmap(resource);
                            Palette.generateAsync(resource, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    //Set the background color of a layout based on the vibrant color
                                    int vibrantColor = palette.getVibrantColor(Color.WHITE);
                                    //int dominantColor = palette.getDominantColor(Color.WHITE);
                                    double y = (299 * Color.red(vibrantColor) + 587 * Color.green(vibrantColor) + 114 * Color.blue(vibrantColor)) / 1000;
                                    int contrastColor = y >= 128 ? Color.BLACK : Color.WHITE;
                                    /* Set color for the title background (as the vibrant color) */
                                    titleBackground.setBackgroundColor(vibrantColor);
                                    /* Set color for the title text (as the contrast of the vibrant color) */
                                    opinion.setTextColor(contrastColor);
                                    /* Set color for the outer frame of the profile image (as the contrast of the vibrant color) */
                                    profileImageBox.setCardBackgroundColor(contrastColor);
                                }
                            });
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("\uD83D\uDCDE "+teacher.getPhoneNum()) .setTitle(getResources().getString(R.string.Phone_of)+" "+user.getfName());

                //Setting message manually and performing action on button click
                builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                }).show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* verify that the user dont sending message to himself */
                if(!teacher.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    /* if the user already has chat with this teacher then open this chat  */
                    if(thereIsChat) {
                        FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dss : snapshot.getChildren()) {
                                            Chat chat = (Chat) dss.getValue(Chat.class);
                                            if (chat != null) {
                                                /* check if this is the active chat  */
                                                if (chat.getTeacherID().equals(teacher.getUserID())) {
                                                    /* this is correct chat - open this chat  */
                                                    Intent intent = new Intent(FullTutorCard.this, MessageActivity.class);
                                                    intent.putExtra("studentName", chat.getStudentName());
                                                    intent.putExtra("student", chat.getStudentID().toString());
                                                    intent.putExtra("teacher", chat.getTeacherID().toString());
                                                    intent.putExtra("chat", chat.getChatID().toString());
                                                    startActivity(intent);
                                                    thereIsChat = true;
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                    else{ /* there is no active chat */
                        /* get this user name */
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("fName").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String studentName = snapshot.getValue(String.class);
                                        /* send the teacher thats new chat is received */
                                        sendNotification(studentName, teacher.getUserID());
                                        /* add the chat to the database and get the chatID */
                                        String chatID = addChat(FirebaseAuth.getInstance().getCurrentUser().getUid(), teacher.getUserID(),
                                                studentName, user.getfName(), teacher.getImgUrl());
                                        /* open the new chat */
                                        if (chatID != null) {
                                            thereIsChat = true;
                                            Intent intent = new Intent(FullTutorCard.this, MessageActivity.class);
                                            intent.putExtra("studentName", studentName);
                                            intent.putExtra("student", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            intent.putExtra("teacher", teacher.getUserID());
                                            intent.putExtra("chat", chatID);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
                else{ /* The teacher is trying to send message to himself */
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.cantSendMessage),Toast.LENGTH_LONG).show();
                }
            }
        });
        closeLoadingDialog();

    }

    public void closeLoadingDialog() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /* wait until the adapter loaded all images */
                /* hide loading dialog (fragment resources ready) */
                loadingDialog.cancel();
            }
        });
    }

    private void setSpinner(CardView typeSpinner, String[] type, int kindOfRank) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teacher.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Toast.makeText(FullTutorCard.this, getResources().getString(R.string.TutorCardCantRate), Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(FullTutorCard.this);
                builder.setTitle(getResources().getString(R.string.Select_Rating));
                builder.setCancelable(false);
                builder.setSingleChoiceItems(type, kindOfRank, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editRank = which;
                    }

                });
                builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(TeacherCard.this, "Rated with "+(which+1)+" stars", Toast.LENGTH_SHORT).show();

                        System.out.println(editRank + " - " + kindOfRank);
                        if (editRank != kindOfRank) {
                            setOpinion(editRank+1);
                        }
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (kindOfRank != -1) {
                            setOpinion(-1);
                        }
                    }
                });
                builder.show();
            }
        });
    }


    synchronized private void setOpinion(int numRank){
        rankObj r = teacher.getRank();
        if (numRank == -1){
            if (r.getUserRating() != null){
                int n = r.getUserRating().size();
                int i = r.getUserRating().remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (n != 1) r.setAvgRank((n*r.getAvgRank()-i)/(n-1));
                else r.setAvgRank(0);
            }
        }
        else if (r.getUserRating() == null){
            HashMap<String, Integer> userRating = new HashMap<>();
            userRating.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), numRank);
            r.setUserRating(userRating);
            r.setAvgRank(numRank);
        }
        else{
            int n = r.getUserRating().size();
            if (kindOfRank != -1){
                float avg = (numRank - (kindOfRank+1) + n*r.getAvgRank())/n;
                r.setAvgRank(avg);
                r.getUserRating().put(FirebaseAuth.getInstance().getCurrentUser().getUid(), numRank);
            }
            else{
                r.getUserRating().put(FirebaseAuth.getInstance().getCurrentUser().getUid(), numRank);
                float avg = (numRank + n*r.getAvgRank())/(n+1);
                r.setAvgRank(avg);
            }
        }
        sendNotification();
        teacher.setRank(r);
        FirebaseDatabase.getInstance().getReference().child("teachers").child(user.getTeacherID()).setValue(teacher);
        Intent intent = new Intent(this, FullTutorCard.class);
        intent.putExtra("user", user);
        intent.putExtra("teacher", teacher);
        intent.putExtra("sub", sub);
        finish();
        startActivity(intent);
    }

    private void sendNotification() {//
        HashMap<String, Object> map = new HashMap<>();
        String notificationID = FirebaseDatabase.getInstance().getReference().child("notifications").child(teacher.getUserID()).push().getKey();
        map.put("notificationID",notificationID);
       // map.put("text","You are rated! Your rating now is: "+teacher.getRank().getAvgRank()+ " Stars.");
        map.put("title","Ranking");
        //map.put("sentFrom",teacher.getRank().getAvgRank());
        map.put("read",1);
        if (notificationID != null)
            FirebaseDatabase.getInstance().getReference().child("notifications").child(teacher.getUserID()).child(notificationID).setValue(map);
    }
    private String addChat(String studentID,String teacherID,String studentName,String teacherName,String imageUrl) {
        //add chat to student and teacher
        HashMap<String, Object> chatMap = new HashMap<>();
        String chatID = FirebaseDatabase.getInstance().getReference().child("chats").child(teacherID).push().getKey();
        chatMap.put("lastMessage", "Chat with " + studentName + " is active now");
        chatMap.put("teacherID", teacherID);
        chatMap.put("studentID", studentID);
        chatMap.put("teacherName", teacherName);
        chatMap.put("studentName", studentName);
        chatMap.put("chatID", chatID);
        chatMap.put("imageUrl", imageUrl);
        chatMap.put("read",0);
        if (chatID != null) {
            FirebaseDatabase.getInstance().getReference().child("chats").child(teacherID).child(chatID).setValue(chatMap);
            FirebaseDatabase.getInstance().getReference().child("chats").child(studentID).child(chatID).setValue(chatMap);
            return chatID;
        }
        return null;
    }
    private void sendNotification(String teacherName,String teacherID) {
        HashMap<String, Object> map = new HashMap<>();
        String notificationID = FirebaseDatabase.getInstance().getReference().child("notifications").child(teacherID).push().getKey();
        map.put("notificationID",notificationID);
        map.put("title",getResources().getString(R.string.chatReceived));
        map.put("sentFrom",teacherName);
        map.put("read",0);
        if (notificationID != null)
            FirebaseDatabase.getInstance().getReference().child("notifications").child(teacherID).child(notificationID).setValue(map);
    }

    /**
     * This method check if this user has an active chat with this teacher
     * if yes then set thereIsChat to true else set to false
     */
    private void initThereIsChat(){
        thereIsChat = false;
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dss : snapshot.getChildren()) {
                            Chat chat = (Chat) dss.getValue(Chat.class);
                            if (chat != null) {
                                /* check if there is an active chat */
                                if (chat.getTeacherID().equals(teacher.getUserID())) {
                                    thereIsChat = true;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}