package com.example.diabeteshealthmonitoringapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReadingListAdapter extends ArrayAdapter<Reading> {
    private final Context context;
    private final int resource;
    private final List<Reading> userList;
    private int lastPosition = -1;
    private OnItemClick listener;

    public interface OnItemClick{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClick listener){
        this.listener = listener;
    }

    public ReadingListAdapter(@NonNull Context context, int resource, @NonNull List<Reading> userList) {
        super(context, resource, userList);
        this.context = context;
        this.resource = resource;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = new ViewHolder();
        View result;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null, false);
            viewHolder.imageView = convertView.findViewById(R.id.patient_pro_pic_reading);
            viewHolder.name = convertView.findViewById(R.id.patients_name_reading);
            viewHolder.lastReading = convertView.findViewById(R.id.last_reading_value);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.get().load(userList.get(position).getFrom()).placeholder(R.drawable.outline_account_circle_24).into(viewHolder.imageView);
        viewHolder.name.setText(userList.get(position).getFrom());
        viewHolder.lastReading.setText(userList.get(position).getReading());
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        convertView.setOnClickListener(v -> listener.onItemClick(position));
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView name, lastReading;
    }
}
