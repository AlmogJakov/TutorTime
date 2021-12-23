package com.project.tutortime.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.R;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

import java.util.Objects;

public class TeacherCard extends AppCompatActivity {
    ImageView image;
    TextView price, description, subject, type, name, place;
    Button send, phone;
    ImageButton back;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_teacher);
        image = findViewById(R.id.imageView);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        send = findViewById(R.id.send);
//        phone = findViewById(R.id.phone);
        back = findViewById(R.id.textView);
        subject = findViewById(R.id.subject);
        type = findViewById(R.id.typeOfeTeaching);
        name = findViewById(R.id.card_teacher_name);
        place = findViewById(R.id.AreaOfTeaching);

        userObj user = (userObj) getIntent().getSerializableExtra("user");
        teacherObj teacher = (teacherObj) getIntent().getSerializableExtra("teacher");
        String sub = getIntent().getStringExtra("sub");
        phoneNum = teacher.getPhoneNum();
        name.setText(user.getfName() +" "+ user.getlName());
        String s="";
        switch (teacher.getSub().get(sub).getType()) {
            case "both": s = "Online/Frontal";break;
            case "online": s = "Online";break;
            case "frontal": s = "Frontal";break;
        }
        type.setText(s);
        place.setText("\uD83D\uDCCD " + user.getCity());
        subject.setText(sub);
        description.setText(teacher.getDescription());
        System.out.println(sub);
        price.setText(teacher.getSub().get(sub).getPrice()+"₪");
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
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

//        phone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherCard.this);
//                builder.setTitle(name.getText())
//                        .setMessage("\uD83D\uDCDE "+phoneNum)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//
//                builder.setCancelable(false);
//                builder.show();
//            }
//        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
