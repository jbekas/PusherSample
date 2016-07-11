package com.redgeckotech.pushersample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.channel.User;
import com.redgeckotech.pushersample.bus.MessageReceived;
import com.redgeckotech.pushersample.bus.RxBus;
import com.redgeckotech.pushersample.model.Message;
import com.redgeckotech.pushersample.util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import timber.log.Timber;

public class PusherService {
    public static final String MY_EVENT = "my_event";
    public static final String CLIENT_MESSAGE_EVENT = "client-message";

    private Context context;

    private Pusher pusher;
    private List<String> subscribedChannels = new ArrayList<>();

    private TreeMap<String, Channel> subscribedChannelList = new TreeMap<>();

    private RxBus rxBus;

    private Timer mShutdownTimer;

    public enum CHANNEL_TYPE {PUBLIC, PRIVATE, PRESENCE}

    public PusherService(Context context) {
        this.context = context;

        // Normally, this would be cached locally, but the source would exist on a server and
        // retrieved upon login.
        subscribedChannels.add("Channel1");
        subscribedChannels.add("Channel2");
        subscribedChannels.add("Channel3");

        rxBus = Utilities.getRxBusInstance();
    }

    /**
     * Get a sorted list of subscribed channels.
     *
     * @return A sorted List of channel names.
     */
    public List<String> getSubscribedChannelList() {
        Timber.d("getSubscribedChannelList");
        for (String key : subscribedChannelList.keySet()) {
            Timber.d("key: %s", key);
        }

        return new ArrayList<>(subscribedChannelList.keySet());
        //return subscribedChannels;
    }

    /**
     * Start the connection to Pusher and subscribe to public channels.
     */
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

            channel.bind(PusherService.MY_EVENT, eventListener);
        }
    }

    /**
     * Terminate the Pusher connection and clean up.
     */
    public void stop() {
        Timber.d("Stopping PusherService");

        for (String channel : subscribedChannels) {
            pusher.unsubscribe(channel);
        }

        if (pusher != null) {
            pusher.disconnect();
        }

        pusher = null;
    }

    /**
     * Start a timer to close connections to Pusher if the app is exited.
     */
    public void startShutdownTimer() {
        Timber.d("Starting shutdown timer.");
        mShutdownTimer = new Timer();
        mShutdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timber.d("Shutdown timer is killing the Pusher connection.");
                stop();
            }
        }, 2000);
    }

    /**
     * Cancel the shutdown timer.
     */
    public void stopShutdownTimer() {
        if (mShutdownTimer != null) {
            Timber.d("Shutdown timer was canceled.");
            mShutdownTimer.cancel();
            mShutdownTimer = null;
        }
    }

    /**
     * Send a message to a private or presence channel.
     *
     * @param channelName The channel name.
     * @param eventName The event name.
     * @param data The stringified version of the JSON payload.
     */
    public void sendMessage(String channelName, String eventName, String data) {
        Channel channel = subscribedChannelList.get(channelName);
        if (channel instanceof PrivateChannel) {
            ((PrivateChannel) channel).trigger(eventName, data);

            // for some reason we do not get notified that message send was successful or failed
            List<Message> messageList = Utilities.getChannelMessageList(channelName);

            Message message = new Gson().fromJson(data, Message.class);
            messageList.add(message);

            // Notify channel listeners
            rxBus.send(new MessageReceived(channelName));
        }
    }

    /**
     * Create a new channel.
     *
     * @param channelType Channel Type.
     * @param name Channel Name without channel type prefix.
     * @return true if successful, false otherwise.
     */
    public void createChannel(CHANNEL_TYPE channelType, String name, @NonNull ChannelCreatedListener channelCreatedListener) {
        String channelName;
        switch (channelType) {
            case PUBLIC:
                channelName = name;
                break;
            case PRIVATE:
                channelName = String.format(Locale.US, "private-%s", name);
                break;
            default:
                channelName = String.format(Locale.US, "presence-%s", name);
                break;
        }

        Channel channel = subscribedChannelList.get(channelName);

        if (channel != null) {
            channelCreatedListener.channelCreatedFailure(channelName, new NullPointerException("Channel name is null."));
            return;
        }

        switch (channelType) {
            case PUBLIC:
                channel = pusher.subscribe(channelName, publicChannelEventListener, PusherService.MY_EVENT);

                break;
            case PRIVATE:
                channel = pusher.subscribePrivate(channelName, privateChannelEventListener, PusherService.CLIENT_MESSAGE_EVENT);

                break;
            default:
                channel = pusher.subscribePresence(channelName, presenceChannelEventListener, PusherService.CLIENT_MESSAGE_EVENT);

                break;
        }

        if (channel != null) {
            subscribedChannelList.put(channelName, channel);
            channelCreatedListener.channelCreatedSuccess(channelName);
        } else {
            channelCreatedListener.channelCreatedFailure(channelName, new NullPointerException("Channel creation failed."));
        }
    }

    /**
     * Get a subscribed Channel by name.
     * @param channelName The name of the channel
     * @return The subscribed Channel or null if one does not exist.
     */
    @Nullable
    public Channel getChannel(String channelName) {
        return subscribedChannelList.get(channelName);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Interfaces
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Used to notify a channel creator about channel creation success or failure.
     */
    public interface ChannelCreatedListener {
        public void channelCreatedSuccess(String channelName);
        public void channelCreatedFailure(String channelName, Exception exception);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private event listener instances
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private SubscriptionEventListener eventListener = new SubscriptionEventListener() {
        @Override
        public void onEvent(String channelName, String eventName, final String data) {
            processEvent(channelName, eventName, data);
        }
    };

    private ChannelEventListener publicChannelEventListener = new ChannelEventListener() {
        @Override
        public void onSubscriptionSucceeded(String s) {
            Timber.d("onSubscriptionSucceeded: %s", s);
        }

        @Override
        public void onEvent(String channelName, String eventName, String data) {
            processEvent(channelName, eventName, data);
        }
    };

    private PrivateChannelEventListener privateChannelEventListener = new PrivateChannelEventListener() {
        @Override
        public void onAuthenticationFailure(String s, Exception e) {
            Timber.e(e, "onAuthenticationFailure: %s", s);
        }

        @Override
        public void onSubscriptionSucceeded(String s) {
            Timber.d("onSubscriptionSucceeded: %s", s);
        }

        @Override
        public void onEvent(String channelName, String eventName, String data) {
            processEvent(channelName, eventName, data);
        }
    };

    private PresenceChannelEventListener presenceChannelEventListener = new PresenceChannelEventListener() {
        @Override
        public void onUsersInformationReceived(String s, Set<User> set) {
            Timber.d("onUsersInformationReceived: %s %s", s, set);
        }

        @Override
        public void userSubscribed(String s, User user) {
            Timber.d("userSubscribed: %s %s", s, user);
        }

        @Override
        public void userUnsubscribed(String s, User user) {
            Timber.d("userUnsubscribed: %s %s", s, user);
        }

        @Override
        public void onAuthenticationFailure(String s, Exception e) {
            Timber.e(e, "onAuthenticationFailure: %s", s);
        }

        @Override
        public void onSubscriptionSucceeded(String s) {
            Timber.d("onSubscriptionSucceeded: %s", s);
        }

        @Override
        public void onEvent(String channelName, String eventName, String data) {
            processEvent(channelName, eventName, data);
        }
    };

    private void processEvent(String channelName, String eventName, String data) {
        try {
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
}
