package com.project.tutortime.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;

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
    TextView price, description, subjectAndType, type, name, place;
    Button send, phone;
    ImageButton back;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String phoneNum;
    ImageView titleBackground;
    CardView profileImageBox;
    RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_teacher);
        image = findViewById(R.id.profile_image);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        send = findViewById(R.id.send);
//        phone = findViewById(R.id.phone);
        back = findViewById(R.id.textView);
        subjectAndType = findViewById(R.id.subjectPlusType);
//        type = findViewById(R.id.typeOfeTeaching);
        name = findViewById(R.id.name);
        place = findViewById(R.id.area);
        titleBackground = (ImageView) findViewById(R.id.title_background);
        profileImageBox = (CardView)findViewById(R.id.profile_image_box);
        rating = (RatingBar)findViewById(R.id.rating);

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
        subjectAndType.setText(sub +" | "+s);
        place.setText("\uD83D\uDCCD " + user.getCity());
        description.setText("Description: "+teacher.getDescription());
        price.setText(teacher.getSub().get(sub).getPrice()+"₪");
        rating.setRating((float) 4.5);

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
//                                    name.setTextColor(contrastColor);
                                    /* Set color for the outer frame of the profile image (as the contrast of the vibrant color) */
                                    profileImageBox.setCardBackgroundColor(contrastColor);
                                }
                            });
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




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
