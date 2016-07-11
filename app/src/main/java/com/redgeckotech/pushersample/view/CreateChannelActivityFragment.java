package com.redgeckotech.pushersample.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.redgeckotech.pushersample.PusherService;
import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.util.Utilities;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class CreateChannelActivityFragment extends BaseFragment {

    @Bind(R.id.channel_name) EditText channelNameView;
    @Bind(R.id.channel_type) RadioGroup channelTypeGroup;
    @Bind(R.id.public_channel) RadioButton publicChannelButton;
    @Bind(R.id.private_channel) RadioButton privateChannelButton;
    @Bind(R.id.presence_channel) RadioButton presenceChannelButton;

    public CreateChannelActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_channel, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.create_channel)
    public void onCreateChannelClicked() {
        int selectedId = channelTypeGroup.getCheckedRadioButtonId();

        String channelName = channelNameView.getText().toString().trim();

        if (TextUtils.isEmpty(channelName)) {
            makeShortToast("Channel Name cannot be empty.");
            return;
        }

        PusherService.CHANNEL_TYPE channelType;

        // find which radioButton is checked by id
        if(selectedId == publicChannelButton.getId()) {
            Timber.d("You chose a Public channel type");
            channelType = PusherService.CHANNEL_TYPE.PUBLIC;
        } else if(selectedId == privateChannelButton.getId()) {
            Timber.d("You chose a Private channel type");
            channelType = PusherService.CHANNEL_TYPE.PRIVATE;
        } else {
            Timber.d("You chose a Presence channel type");
            channelType = PusherService.CHANNEL_TYPE.PRESENCE;
        }

        PusherService pusherService = Utilities.getPusherService(getActivity());
        pusherService.createChannel(channelType, channelName, new PusherService.ChannelCreatedListener() {
            @Override
            public void channelCreatedSuccess(String channelName) {
                Toast.makeText(getActivity(), R.string.channel_created, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void channelCreatedFailure(String channelName, Exception exception) {
                Timber.e(exception, null);
                Toast.makeText(getActivity(), R.string.channel_creation_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
