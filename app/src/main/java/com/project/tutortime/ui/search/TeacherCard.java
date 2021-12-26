package com.project.tutortime.ui.search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.LoadingDialog;
import com.project.tutortime.R;
import com.project.tutortime.databinding.FragmentSearchResultsBinding;
import com.project.tutortime.databinding.FragmentTeacherCardBinding;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;


public class TeacherCard extends Fragment {
    ImageView image;
    TextView price, description, subjectAndType, opinion, name, place;
    Button send;
    ImageButton back;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String phoneNum;
    ImageView titleBackground;
    CardView profileImageBox, rate;
    RatingBar rating;
    @NonNull FragmentTeacherCardBinding binding;
    LoadingDialog loadingDialog;
    userObj user;
    teacherObj teacher;
    String sub;
    public TeacherCard(userObj user, teacherObj teacher, String subName) {
        this.user = user;
        this.teacher = teacher;
        this.sub = subName;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeacherCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
        image = binding.profileImage;
        price = binding.price;
        description = binding.description;
        send = binding.send;
        back = binding.textView;
        subjectAndType = binding.subjectPlusType;
        name = binding.name;
        place = binding.area;
        titleBackground = (ImageView) binding.titleBackground;
        profileImageBox = (CardView)binding.profileImageBox;
        rating = (RatingBar)binding.rating;
        opinion = binding.opinion;
        rate = binding.rateTutor;

        String[] typeRank = {"⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐","⭐⭐⭐⭐⭐"};
        setSpinner(rate, typeRank);

//        userObj user = (userObj) getIntent().getSerializableExtra("user");
//        teacherObj teacher = (teacherObj) getIntent().getSerializableExtra("teacher");
//        String sub = getIntent().getStringExtra("sub");
        phoneNum = teacher.getPhoneNum();
        opinion.setText("0 " + getResources().getString(R.string.opinion));
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




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
            }
        });
        closeLoadingDialog();

        return root;
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

    private void setSpinner(CardView typeSpinner, String[] type) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.Select));
                builder.setCancelable(false);
                builder.setSingleChoiceItems(type, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Rated with "+(which+1)+" stars", Toast.LENGTH_SHORT).show();
                    }

                });
                builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    private void setOpinion(){
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* sort the list of service cities */
                //Collections.sort(listCities);
                /* get teacher ID */
//                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
//                        /* Make a list of all the RealTime DataBase commands to execute
//                            (for the purpose of executing all the commands at once) */
//                Map<String, Object> childUpdates = new HashMap<>();
//                if (imgURL != null)
//                    childUpdates.put("teachers/" + teacherID + "/imgUrl", imgURL);
//                childUpdates.put("teachers/" + teacherID + "/phoneNum", pNum);
//                childUpdates.put("teachers/" + teacherID + "/description", descrip);
//                //childUpdates.put("teachers/" + teacherID + "/serviceCities", listCities);
//                childUpdates.put("users/" + userID + "/fName", firstName);
//                childUpdates.put("users/" + userID + "/lName", lastName);
//                childUpdates.put("users/" + userID + "/city", city);
//                        /* If the user deleted the image - delete it from the storage and add
//                            a delete command to childUpdates (to delete it URL from the RealTime DataBase) */
//                if (del) {
//                    if (imgURL != null)
//                        childUpdates.put("teachers/" + teacherID + "/imgUrl", null);
//                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//                    StorageReference storageReference = firebaseStorage.getReference(imgURL);
//                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.e("Picture", "#deleted");
//                            imgURL = null;
//                        }
//                    });
//                }
//                /* Finally, execute all RealTime DataBase commands in one command (safely). */
//                myRef.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
