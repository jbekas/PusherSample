package com.redgeckotech.pushersample.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.redgeckotech.pushersample.Constants;
import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.bus.MessageReceived;
import com.redgeckotech.pushersample.bus.RxBus;
import com.redgeckotech.pushersample.model.Message;
import com.redgeckotech.pushersample.net.Event;
import com.redgeckotech.pushersample.net.MessageData;
import com.redgeckotech.pushersample.net.PusherApi;
import com.redgeckotech.pushersample.util.Utilities;
import com.redgeckotech.pushersample.view.adapter.MessageAdapter;
import com.redgeckotech.pushersample.widget.VerticalSpaceItemDecoration;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class ChannelActivityFragment extends BaseFragment {

    private static final int VERTICAL_ITEM_SPACE = 16;

    private String channelName;

    @Bind(R.id.send_image)
    ImageView sendImageView;
    @Bind(R.id.message)
    EditText messageView;
    @Bind(R.id.message_list)
    RecyclerView messageRecyclerView;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private Subscription eventSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            channelName = getActivity().getIntent().getStringExtra(Constants.EXTRA_CHANNEL_NAME);
        } else {
            channelName = savedInstanceState.getString(Constants.EXTRA_CHANNEL_NAME);
        }

        if (channelName == null) {
            Toast.makeText(getActivity(), "No channel specified.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        Timber.d("Channel name: %s", channelName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_channel, container, false);

        ButterKnife.bind(this, rootView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        messageRecyclerView.setLayoutManager(llm);

        messageList = Utilities.getChannelMessageList(channelName);
        messageAdapter = new MessageAdapter(getActivity(), messageList);

        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        setHasOptionsMenu(true);

        Utilities.hideKeyboardFrom(getActivity(), messageView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set the Activity title to the channel name
        setActivityTitle(channelName);

        RxBus rxBus = Utilities.getRxBusInstance();

        eventSubscription = rxBus.toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        Timber.d("event %s", event);

                        if (event instanceof MessageReceived) {
                            MessageReceived messageReceived = (MessageReceived) event;

                            // only refresh if incoming message is for the current channel.
                            if (channelName.equals(((MessageReceived) event).getChannelName())) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_CHANNEL_NAME, channelName);
    }

    @OnClick(R.id.send_image)
    public void onSendImageClicked() {
        disableMessageUI();

        Utilities.hideKeyboardFrom(getActivity(), messageView);

        String message = messageView.getText().toString();

        if (message.trim().length() == 0) {
            enableMessageUI();
            makeShortToast(getString(R.string.message_cannot_be_blank));
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = prefs.getString(Constants.USERNAME, null);

        MessageData messageData = new MessageData();
        messageData.setUserName(username);
        messageData.setMessage(message);

        Event event = new Event();
        event.setChannel(channelName);
        event.setName("my_event");
        event.setData(messageData);

        PusherApi pusherApi = Utilities.getPusherApiInstance();
        pusherApi.sendEvent(event);

        Call<Void> call = pusherApi.sendEvent(event);

        // Fetch and print a list of the chat messages
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                enableMessageUI();

                if (response.code() != 200) {
                    String errorMesage = getString(R.string.unknown_error);
                    ResponseBody responseBody = response.errorBody();
                    Timber.d("content type: %s", responseBody.contentType());
                    if (responseBody.contentType().toString().equals("application/json")) {

                        try {
                            JSONObject json = new JSONObject(responseBody.string());

                            errorMesage = json.optString("error", getString(R.string.unknown_error));
                        } catch (Exception e) {
                            Timber.e(e, null);
                        }
                    }

                    Timber.e("Error: %s", errorMesage);

                    // TODO These are raw error messages. A real app would make these user friendly.
                    makeShortToast(errorMesage);

                    return;
                }

                // clear text
                messageView.setText(null);

                Timber.d("success");
            }

            @Override
            public void onFailure(Call<Void> call, final Throwable t) {

                enableMessageUI();

                Timber.e(t, "Event was not sent.");

                makeShortToast(t.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_channel, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_unsubscribe) {
            makeShortToast(getString(R.string.cannot_unsubscribe_from_channel));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void disableMessageUI() {
        sendImageView.setEnabled(false);
        sendImageView.setColorFilter(Utilities.getColor(getActivity(), R.color.gray));
        messageView.setEnabled(false);
    }

    private void enableMessageUI() {
        sendImageView.setEnabled(true);
        sendImageView.setColorFilter(Utilities.getColor(getActivity(), R.color.colorAccent));
        messageView.setEnabled(true);
    }
}
