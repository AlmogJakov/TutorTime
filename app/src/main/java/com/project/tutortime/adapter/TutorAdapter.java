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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.R;
import com.project.tutortime.firebase.subjectObj;

import java.util.List;

public class TutorAdapter extends ArrayAdapter<TutorAdapterItem> {
    private final Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final List<TutorAdapterItem> teachersToShow;

    public TutorAdapter(Context context, List<TutorAdapterItem> teachersToShow) {
        super(context, R.layout.tutor_card_view, teachersToShow);
        this.context = context;
        this.teachersToShow = teachersToShow;
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
            Glide.with(context)
                    .asBitmap()
                    .load(storageReference)
                    .into(new CustomTarget<Bitmap>() {
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
                                }
                            });
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }
        titleText.setText(teachersToShow.get(position).user.getfName());
        description.setText(teachersToShow.get(position).teacher.getDescription());
        String subjectName = teachersToShow.get(position).subName;
        subject.setText(subjectName);
        subjectObj sub = teachersToShow.get(position).teacher.getSub().get(subjectName);
        price.setText(sub.getPrice()+"₪");
        rating.setRating((float) 4.5);
        return rowView;
    }
}