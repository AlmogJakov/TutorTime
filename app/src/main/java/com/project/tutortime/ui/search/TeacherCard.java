package com.project.tutortime.ui.search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.LoadingDialog;
import com.project.tutortime.R;
//import com.project.tutortime.datafindViewById(R.id.FragmentTeacherCardBinding;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.rankObj;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;
import com.project.tutortime.ui.home.HomeFragment;

import java.util.HashMap;
import java.util.List;


public class TeacherCard extends AppCompatActivity {
    ImageView image;
    TextView price, description, subjectAndType, opinion, name, place;
    Button send;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String phoneNum;
    ImageView titleBackground;
    CardView profileImageBox, rate;
    RatingBar rating;
    LoadingDialog loadingDialog;
    userObj user;
    teacherObj teacher;
    String sub;
    int kindOfRank = -1, editRank;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        setContentView(R.layout.activity_teacher_card);

//        findViewById(R.id.)
        image = findViewById(R.id.profile_image);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        send = findViewById(R.id.send);
        subjectAndType = findViewById(R.id.subjectPlusType);
        name = findViewById(R.id.name);
        place = findViewById(R.id.area);
        titleBackground = (ImageView) findViewById(R.id.title_background);
        profileImageBox = (CardView)findViewById(R.id.profile_image_box);
        rating = (RatingBar)findViewById(R.id.rating);
        opinion = findViewById(R.id.opinion);
        rate = findViewById(R.id.rateTutor);


        Bundle bundle = getIntent().getExtras();
        user = (userObj) bundle.getSerializable("user");
        teacher = (teacherObj) bundle.getSerializable("teacher");
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
        subjectAndType.setText(sub +" | "+s);
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
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(TeacherCard.this, "You can not rate yourself!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherCard.this);
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
                builder.show();
            }
        });
    }

    synchronized private void setOpinion(int numRank){
        rankObj r = teacher.getRank();
        if (r.getUserRating() == null){
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
        teacher.setRank(r);
        FirebaseDatabase.getInstance().getReference().child("teachers").child(user.getTeacherID()).setValue(teacher);
        Intent intent = new Intent(this, TeacherCard.class);
        intent.putExtra("user", user);
        intent.putExtra("teacher", teacher);
        intent.putExtra("sub", sub);
        finish();
        startActivity(intent);
    }
}
