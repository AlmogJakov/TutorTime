package com.project.tutortime.ui.search;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    TextView price, description, back, subject;
    Button send, phone;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_teacher);
        image = findViewById(R.id.image);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        send = findViewById(R.id.send);
        phone = findViewById(R.id.phone);
        back = findViewById(R.id.textView);
        subject = findViewById(R.id.subject);

        userObj user = (userObj) getIntent().getSerializableExtra("user");
        teacherObj teacher = (teacherObj) getIntent().getSerializableExtra("teacher");
        String sub = getIntent().getStringExtra("sub");

        subject.setText(sub);
        description.setText(teacher.getDescription());
        System.out.println(sub);
        price.setText(teacher.getSub().get(sub).getPrice()+"");
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
//                            Palette.generateAsync(resource, new Palette.PaletteAsyncListener() {
//                                public void onGenerated(Palette palette) {
//                                    //Set the background color of a layout based on the vibrant color
//                                    int dominantColor = palette.getDominantColor(Color.WHITE);
//                                    double y = (299 * Color.red(dominantColor) + 587 * Color.green(dominantColor) + 114 * Color.blue(dominantColor)) / 1000;
//                                    int contrastColor = y >= 128 ? Color.BLACK : Color.WHITE;
//                                    /* Set color for the outer frame (as the dominant color) */
//                                    titleBackground.setBackgroundColor(dominantColor);
//                                    /* Set color for the title text (as the contrast of the dominant color) */
//                                    titleText.setTextColor(contrastColor);
//                                    /* Set color for the outer frame (as the contrast of the dominant color) */
//                                    profileImageBox.setCardBackgroundColor(contrastColor);
//                                }
//                            });
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

        phone.setOnClickListener(new View.OnClickListener() {
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
