package com.redgeckotech.pushersample.net;

import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("channel") public String channel;
    @SerializedName("name") public String name;
    @SerializedName("data") public Data data;

    public Event() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "channel='" + channel + '\'' +
                ", name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}

