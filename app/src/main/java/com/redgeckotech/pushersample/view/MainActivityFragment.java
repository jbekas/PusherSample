package com.redgeckotech.pushersample.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.redgeckotech.pushersample.Constants;
import com.redgeckotech.pushersample.MyApplication;
import com.redgeckotech.pushersample.PusherService;
import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.bus.MessageReceived;
import com.redgeckotech.pushersample.bus.RxBus;
import com.redgeckotech.pushersample.util.Utilities;
import com.redgeckotech.pushersample.view.adapter.ChannelAdapter;
import com.redgeckotech.pushersample.view.adapter.MessageAdapter;
import com.redgeckotech.pushersample.widget.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class MainActivityFragment extends Fragment implements ChannelAdapter.ChannelClickListener {

    private static final int VERTICAL_ITEM_SPACE = 16;

    @Bind(R.id.channel_list) RecyclerView channelRecyclerView;

    private ChannelAdapter channelAdapter;
    private List<String> channelList;

    private Subscription eventSubscription;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        channelRecyclerView.setLayoutManager(llm);

        PusherService pusherService = Utilities.getPusherService(getActivity());
        channelList = pusherService.getSubscribedChannelList();

        channelAdapter = new ChannelAdapter(this, channelList);

        channelRecyclerView.setAdapter(channelAdapter);
        channelRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        return rootView;
    }

    @Override
    public void channelClicked(String channelName) {
        Intent intent = new Intent(getActivity(), ChannelActivity.class);
        intent.putExtra(Constants.EXTRA_CHANNEL_NAME, channelName);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        RxBus rxBus = Utilities.getRxBusInstance();

        eventSubscription = rxBus.toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        Timber.d("event %s", event);

                        if (event instanceof MessageReceived) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    channelAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (eventSubscription != null) {
            eventSubscription.unsubscribe();
        }
    }
}
