package com.redgeckotech.pushersample.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.model.Message;
import com.redgeckotech.pushersample.util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

    ChannelClickListener channelClickListener;
    List<String> channelList;

    public ChannelAdapter(ChannelClickListener listener, List<String> channelList) {
        channelClickListener = listener;

        if (channelList == null) {
            this.channelList = new ArrayList<>();
        } else {
            this.channelList = channelList;
        }
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_channel, parent, false);
        final ChannelViewHolder viewHolder = new ChannelViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("view clicked: %s", viewHolder.channelName.getText().toString());
                channelClickListener.channelClicked(viewHolder.channelName.getText().toString());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        String channel = channelList.get(position);

        holder.channelName.setText(channel);

        List<Message> messageList = Utilities.getChannelMessageList(channel);
        // TODO get unread count / currently display total message count
        holder.unreadCount.setText(String.format(Locale.US, "%d", messageList.size()));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        TextView channelName;
        TextView unreadCount;

        public ChannelViewHolder(View view) {
            super(view);

            channelName = (TextView) itemView.findViewById(R.id.channel_name);
            unreadCount = (TextView) itemView.findViewById(R.id.unread_count);
        }
    }

    public interface ChannelClickListener {
        public void channelClicked(String channelName);
    }
}
