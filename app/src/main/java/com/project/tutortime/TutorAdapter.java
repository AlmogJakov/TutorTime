package com.project.tutortime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;

public class TutorAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public TutorAdapter(Context context, String[] values) {
        super(context, R.layout.tutor_card_view, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tutor_card_view, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.head_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        ImageView imageBackgroundView = (ImageView) rowView.findViewById(R.id.header_cover_image);
        CardView imageFrame = (CardView)rowView.findViewById(R.id.cardview_image_box);
        RatingBar rating = (RatingBar)rowView.findViewById(R.id.rating);
        rating.setRating((float) 4.5);
        //Drawable drawableReview = rating.getProgressDrawable();

        // blur background image
        Drawable myDrawable = getContext().getResources().getDrawable(R.drawable.tutor_example1);
        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
//        Palette palette = Palette.from(myLogo).generate();
//        Palette.Swatch vibrant = palette.getVibrantSwatch();
//        if (vibrant != null) {
//            // Set the background color of a layout based on the vibrant color
//            imageBackgroundView.setBackgroundColor(vibrant.getRgb());
//            // Update the title TextView with the proper text color
//            textView.setTextColor(vibrant.getTitleTextColor());
//        }
        Palette.generateAsync(myLogo, new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                // Do something with colors...
                //palette.getDominantColor(0);
                //Set the background color of a layout based on the vibrant color
                int dominantColor = palette.getDominantColor(Color.WHITE);

                //int red = Color.red(dominantColor);
                //int green = Color.green(dominantColor);
                //int blue = Color.blue(dominantColor);
                //int alpha = Color.alpha(dominantColor);

                //Color color = Color.valueOf(dominantColor);
                double y = (299 * Color.red(dominantColor) + 587 * Color.green(dominantColor) + 114 * Color.blue(dominantColor)) / 1000;
                int contrastColor = y >= 128 ? Color.BLACK : Color.WHITE;
                imageBackgroundView.setBackgroundColor(dominantColor);
                // Update the title TextView with the proper text color
                textView.setTextColor(contrastColor);
                imageFrame.setCardBackgroundColor(contrastColor);
                //drawableReview.setColorFilter(contrastColor, PorterDuff.Mode.SRC_ATOP); //Color.parseColor("#6A9A28")
            }
        });
        // END blur background image

        textView.setText(values[position]);
        return rowView;
    }
}
