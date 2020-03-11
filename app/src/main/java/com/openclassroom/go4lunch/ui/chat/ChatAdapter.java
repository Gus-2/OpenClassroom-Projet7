package com.openclassroom.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Message;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by de Meeûs Augustin on 10/03/2020
 **/
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{

    private ArrayList<Message> messages;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_workmate_sent_message_name)
        TextView name;
        @BindView(R.id.tv_workmate_sent_message_message)
        TextView message;
        @BindView(R.id.cv_workmate_sent_message)
        CircleImageView picture;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ChatAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        return new ChatAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(messages.get(position).getName());
        holder.message.setText(messages.get(position).getMessage());
        Glide.with(holder.itemView.getContext())
                .load(messages.get(position).getImageUrl())
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
