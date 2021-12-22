package com.project.tutortime.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.R;
import com.project.tutortime.firebase.subjectObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TutorAdapter extends ArrayAdapter<TutorAdapterItem> {
    private final Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final List<TutorAdapterItem> teachersToShow;
    private List<Boolean> isResourceReady;

    public TutorAdapter(Context context, List<TutorAdapterItem> teachersToShow) {
        super(context, R.layout.tutor_card_view, teachersToShow);
        this.context = context;
        this.teachersToShow = teachersToShow;
        this.isResourceReady = Arrays.asList(new Boolean[teachersToShow.size()]);
        Collections.fill(isResourceReady, Boolean.FALSE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tutor_card_view, parent, false);
        TextView titleText = (TextView) rowView.findViewById(R.id.title_text);
        TextView price = (TextView) rowView.findViewById(R.id.price);
        TextView subject = (TextView) rowView.findViewById(R.id.subject);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        ImageView profileImage = (ImageView) rowView.findViewById(R.id.profile_image);
        ImageView titleBackground = (ImageView) rowView.findViewById(R.id.title_background);
        CardView profileImageBox = (CardView)rowView.findViewById(R.id.profile_image_box);
        RatingBar rating = (RatingBar)rowView.findViewById(R.id.rating);

        String imageLink = teachersToShow.get(position).teacher.getImgUrl();
        if (imageLink!=null) {
            StorageReference storageReference = storage.getReference().child(imageLink);
            CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            profileImage.setImageBitmap(resource);
                            Palette.generateAsync(resource, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    //Set the background color of a layout based on the vibrant color
                                    int dominantColor = palette.getDominantColor(Color.WHITE);
                                    double y = (299 * Color.red(dominantColor) + 587 * Color.green(dominantColor) + 114 * Color.blue(dominantColor)) / 1000;
                                    int contrastColor = y >= 128 ? Color.BLACK : Color.WHITE;
                                    /* Set color for the outer frame (as the dominant color) */
                                    titleBackground.setBackgroundColor(dominantColor);
                                    /* Set color for the title text (as the contrast of the dominant color) */
                                    titleText.setTextColor(contrastColor);
                                    /* Set color for the outer frame (as the contrast of the dominant color) */
                                    profileImageBox.setCardBackgroundColor(contrastColor);
                                    setViews(position,titleText,description,subject, price,rating);
                                    /* Add true value for this resource (indicate that the resource is ready) */
                                    isResourceReady.set(position,Boolean.TRUE);
                                }
                            });
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    };
            Glide.with(context)
                    .asBitmap()
                    .load(storageReference)
                    .into(target);
        } else {
            setViews(position,titleText,description,subject, price,rating);
            /* Add true value for this resource (indicate that the resource is ready) */
            isResourceReady.set(position,Boolean.TRUE);
        }
        return rowView;
    }

    public void setViews(int position,TextView titleText, TextView description, TextView subject,
                         TextView price, RatingBar rating) {
        titleText.setText(teachersToShow.get(position).user.getfName());
        description.setText(teachersToShow.get(position).teacher.getDescription());
        String subjectName = teachersToShow.get(position).subName;
        subject.setText(subjectName);
        subjectObj sub = teachersToShow.get(position).teacher.getSub().get(subjectName);
        price.setText(sub.getPrice()+"₪");
        rating.setRating((float) 4.5);
    }

    public Boolean isAllResourcesReady() {
        return !isResourceReady.contains(false);
    }
}