package com.redgeckotech.pushersample.net;

public class MessageData extends Data {
    public String userName;
    public String message;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
