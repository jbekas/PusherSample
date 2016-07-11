package com.redgeckotech.pushersample.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.redgeckotech.pushersample.R;

public class ChannelActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
