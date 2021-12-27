package com.project.tutortime.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.util.ScopeUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.R;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.ui.search.TeacherCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TutorAdapter extends ArrayAdapter<TutorAdapterItem> {
    private final Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final List<TutorAdapterItem> teachersToShow;
    private List<Boolean> isResourceReady;
    int id;

    public TutorAdapter(Context context, List<TutorAdapterItem> teachersToShow) {
        super(context, R.layout.tutor_card_view, teachersToShow);
        this.context = context;
        this.teachersToShow = teachersToShow;
        this.isResourceReady = Arrays.asList(new Boolean[teachersToShow.size()]);
        this.id = id;
        Collections.fill(isResourceReady, Boolean.FALSE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //isResourceReady.set(position,Boolean.FALSE);
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
        if (imageLink!=null) { /* The tutor has a picture */
            StorageReference storageReference = storage.getReference().child(imageLink);
            CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            profileImage.setImageBitmap(resource);
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
                                    titleText.setTextColor(contrastColor);
                                    /* Set color for the outer frame of the profile image (as the contrast of the vibrant color) */
                                    profileImageBox.setCardBackgroundColor(contrastColor);
                                    setViews(position,titleText,description,subject, price,rating);
                                    /* Add true value for this resource (indicate that the resource is ready) */
                                    isResourceReady.set(position,Boolean.TRUE);
                                    //System.out.println("p:"+position);
                                }
                            });
                        }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) { }
                @Override
                        public void onLoadFailed(@Nullable Drawable placeholder) {
                            /* If the image failed to load - set only the rest of the tutor details */
                            setViews(position,titleText,description,subject, price,rating);
                            isResourceReady.set(position,Boolean.TRUE); }
                    };
            Glide.with(context)
                    .asBitmap()
                    .load(storageReference)
                    .into(target);
        } else { /* The tutor has no picture at all */
            setViews(position,titleText,description,subject, price,rating);
            /* Add true value for this resource (indicate that the resource is ready) */
            isResourceReady.set(position,Boolean.TRUE);
        }
        CardView currentCard = (CardView)rowView.findViewById(R.id.tutor_card_item);
        currentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TeacherCard.class);
                intent.putExtra("user", teachersToShow.get(position).getUser());
                intent.putExtra("teacher", teachersToShow.get(position).getTeacher());
                intent.putExtra("sub", teachersToShow.get(position).getSubName());
                getContext().startActivity(intent);
            }
        });
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

    /* Source: https://stackoverflow.com/questions/35115788/how-to-set-listview-height-depending-on-the-items-inside-scrollview/48027821 */
    public static void updateListViewHeight(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        // get listview height
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount; size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // Change Height of ListView
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = (totalHeight + (myListView.getDividerHeight() * (adapterCount)));
        myListView.setLayoutParams(params);
    }

    /* In order to use 'isAllResourcesReady' method with ListView - it is necessary to call
     * the 'updateListViewHeight' method after linking the adapter to the ListView.
     * Explanation: The method checks if all the resources are ready,
     * and if the height of the ListView is small
     * [and therefore will display only some of the elements]
     * then the app will not load the hidden elements which
     * will cause their resources to never load and the method will always return False.*/
    public Boolean isAllResourcesReady() { return !isResourceReady.contains(false); }
}