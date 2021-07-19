package com.example.diabeteshealthmonitoringapplication.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.ChatListAdapter.ChatListViewHolder;
import com.example.diabeteshealthmonitoringapplication.models.ChatListItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {
    private List<ChatListItem> chatListItems;
    private Context context;
    private OnItemClick listener;

    public ChatListAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClick{
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClick listener){
        this.listener = listener;
    }

    public void setData(List<ChatListItem> chatListItems) {
        this.chatListItems = chatListItems;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_list_item,
                                parent,
                                false),
                context,
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
        holder.bind(chatListItems.get(position), chatListItems.size() - 1);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv, messageTv, timeTv;
        private CircleImageView imageV;

        public ChatListViewHolder(View view, Context context,OnItemClick listener) {
            super(view);
            nameTv = view.findViewById(R.id.name_text_view);
            messageTv = view.findViewById(R.id.text_text_view);
            timeTv = view.findViewById(R.id.time_text_view);
            imageV = view.findViewById(R.id.image_pic);
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            });
        }

        protected void bind(ChatListItem item, int lastPosition) {
            nameTv.setText(item.getFromName());
            messageTv.setText(item.getChats().get(lastPosition).getMessage());
            timeTv.setText(item.getChats().get(lastPosition).getTime());
            Picasso.get().load(item.getFromImageUrl())
                    .placeholder(Objects.requireNonNull(
                            ResourcesCompat.getDrawable(Resources.getSystem(),
                                    R.drawable.outline_account_circle_24,
                                    null)))
                    .into(imageV);
        }
    }
}
