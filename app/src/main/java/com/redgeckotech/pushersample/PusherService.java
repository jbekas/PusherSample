package com.redgeckotech.pushersample;

import android.content.Context;

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.redgeckotech.pushersample.bus.MessageReceived;
import com.redgeckotech.pushersample.bus.RxBus;
import com.redgeckotech.pushersample.model.Message;
import com.redgeckotech.pushersample.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class PusherService {
    private Context context;

    private Pusher pusher;
    private List<String> subscribedChannels = new ArrayList<>();

    private HashMap<String, Channel> subscribedChannelList = new HashMap<>();

    private RxBus rxBus;

    public PusherService(Context context) {
        this.context = context;

        // Normally, this would be cached locally, but the source would exist on a server and
        // retrieved upon login.
        subscribedChannels.add("Channel1");
        subscribedChannels.add("Channel2");
        subscribedChannels.add("Channel3");

        rxBus = Utilities.getRxBusInstance();
    }

    public List<String> getSubscribedChannelList() {
        return subscribedChannels;
    }

    public void start() {
        if (pusher != null) {
            Timber.d("PusherService already started.");
            return;
        }

        Timber.d("Starting PusherService");

        pusher = Utilities.getPusherInstance();
        pusher.connect();

        for (String channelName : subscribedChannels) {
            Channel channel = pusher.subscribe(channelName);

            subscribedChannelList.put(channelName, channel);

            channel.bind("my_event", eventListener);
        }
    }

    public void stop() {
        Timber.d("Stopping PusherService");

        for (String channel : subscribedChannels) {
            pusher.unsubscribe(channel);
        }

        if (pusher != null) {
            pusher.disconnect();
        }
    }

    private SubscriptionEventListener eventListener = new SubscriptionEventListener() {
        @Override
        public void onEvent(String channelName, String eventName, final String data) {
            try {
                //System.out.println(data);
                Timber.d("channelName: %s", channelName);
                Timber.d("eventName: %s", eventName);
                Timber.d("data: %s", data);

                Message message = new Gson().fromJson(data, Message.class);

                List<Message> messageList = Utilities.getChannelMessageList(channelName);

                messageList.add(message);

                // Notify channel listeners
                rxBus.send(new MessageReceived(channelName));

            } catch (Exception e) {
                Timber.e(e, null);
            }
        }
    };
}
