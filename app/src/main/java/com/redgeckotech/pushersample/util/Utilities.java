package com.redgeckotech.pushersample.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.pusher.client.PusherOptions;
import com.pusher.client.util.HttpAuthorizer;
import com.redgeckotech.pushersample.BuildConfig;
import com.redgeckotech.pushersample.MyApplication;
import com.redgeckotech.pushersample.PusherService;
import com.redgeckotech.pushersample.bus.RxBus;
import com.redgeckotech.pushersample.model.Message;
import com.redgeckotech.pushersample.net.PusherApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Most of the static references in this class would be moved into Dagger components which
 * could be injected where needed. This implementation is quick-and-dirty.
 */
public class Utilities {
    private static com.pusher.client.Pusher pusherInstance;
    private static PusherApi pusherApiInstance;

    private static HashMap<String, List<Message>> channelMessageLists;

    private static PusherService pusherService;

    private static RxBus rxBus;

    // Note: Use Dagger for service creation/injection
    public static com.pusher.client.Pusher getPusherInstance() {
        if (pusherInstance == null) {
            HttpAuthorizer authorizer = new HttpAuthorizer("https://di.redgringo.com/auth.php");
            PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
            pusherInstance = new com.pusher.client.Pusher(BuildConfig.PUSHER_API_KEY, options);
        }

        return pusherInstance;
    }

    public static PusherApi getPusherApiInstance() {
        if (pusherApiInstance == null) {
            String url = "https://di.redgringo.com/";

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // add logging as last interceptor
            if (BuildConfig.LOG_RETROFIT_QUERIES) {
                httpClient.addInterceptor(logging);
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
            pusherApiInstance = retrofit.create(PusherApi.class);
        }

        return pusherApiInstance;
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int resourceId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getResources().getColor(resourceId, context.getTheme());
        } else {
            return context.getResources().getColor(resourceId);
        }
    }

    // In a real world app, these message lists would ideally be stored in a DB so
    // memory is not eaten up for inactive channels.
    public static List<Message> getChannelMessageList(String channelName) {
        if (channelMessageLists == null) {
            channelMessageLists = new HashMap<>();
        }

        List<Message> messageList = channelMessageLists.get(channelName);

        if (messageList == null) {
            // if no message list exists, create and add to our map
            messageList = new ArrayList<>();
            channelMessageLists.put(channelName, messageList);
        }

        return messageList;
    }

    public static PusherService getPusherService(@NonNull Context context) {

        if (pusherService != null) {
            return pusherService;
        }

        MyApplication myApplication = (MyApplication) context.getApplicationContext();

        pusherService = new PusherService(myApplication);

        return pusherService;
    }

    public static RxBus getRxBusInstance() {
        if (rxBus == null) {
            rxBus = new RxBus();
        }

        return rxBus;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
