package com.redgeckotech.pushersample.bus;

public class MessageReceived {
    private String channelName;

    public MessageReceived(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return "MessageReceived{" +
                "channelName='" + channelName + '\'' +
                '}';
    }
}
