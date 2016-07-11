package com.redgeckotech.pushersample.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        if (messageList == null) {
            this.messageList = new ArrayList<>();
        } else {
            this.messageList = messageList;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_message, parent, false);
        MessageViewHolder viewHolder = new MessageViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.message.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public MessageViewHolder(View view) {
            super(view);

            message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
