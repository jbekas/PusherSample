package com.redgeckotech.pushersample.view.adapter;

import android.content.Context;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    Context context;
    List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;

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
        if (! TextUtils.isEmpty(message.getUserName())) {
            holder.userAttribution.setText(context.getString(R.string.by_user, message.getUserName()));
        } else {
            holder.userAttribution.setText(context.getString(R.string.by_user, context.getString(R.string.anonymous)));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView userAttribution;

        public MessageViewHolder(View view) {
            super(view);

            message = (TextView) itemView.findViewById(R.id.message);
            userAttribution = (TextView) itemView.findViewById(R.id.user_attribution);
        }
    }
}
