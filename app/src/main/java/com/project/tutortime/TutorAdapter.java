package com.project.tutortime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        TextView textView = (TextView) rowView.findViewById(R.id.head);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        textView.setText(values[position]);
        return rowView;
    }
}
