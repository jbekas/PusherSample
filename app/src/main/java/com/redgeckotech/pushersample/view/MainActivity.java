package com.redgeckotech.pushersample.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.view.adapter.ChannelAdapter;

import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.subscribed_channels);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeShortToast(getString(R.string.subscribing_to_channels_not_implemented));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                }
            });
        }


    }

    @Override
    public void onResume() {
        super.onResume();

//        Pusher pusher = PusherUtils.getPusherInstance();
//        pusher.connect();
//
//        final Channel channel = pusher.subscribe("test_channel");
//
//        channel.bind("my_event", subscriptionEventListener);

//        channel.bind("my-event", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                System.out.println(data);
//            }
//        });

    }

//    SubscriptionEventListener subscriptionEventListener = new SubscriptionEventListener() {
//        @Override
//        public void onEvent(String channelName, String eventName, final String data) {
//            //System.out.println(data);
//            Timber.d("channelName: %s", channelName);
//            Timber.d("eventName: %s", eventName);
//            Timber.d("data: %s", data);
//        }
//    };

    @Override
    public void onPause() {
        super.onPause();

//        Pusher pusher = PusherUtils.getPusherInstance();
//
//        // Unsubscribe from channel
//        pusher.unsubscribe("test_channel");
//
//        pusher.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
