package com.redgeckotech.pushersample.view;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redgeckotech.pushersample.Constants;
import com.redgeckotech.pushersample.PusherService;
import com.redgeckotech.pushersample.R;
import com.redgeckotech.pushersample.util.Utilities;
import com.redgeckotech.pushersample.view.adapter.ChannelAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.login_layout) ViewGroup loginLayout;
    @Bind(R.id.username) TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateUI() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString(Constants.USERNAME, null);

        if (TextUtils.isEmpty(username)) {
            fab.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }
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
        if (id == R.id.action_logout) {
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.remove(Constants.USERNAME);
            edit.commit();

            updateUI();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CommitPrefEdits")
    @OnClick(R.id.login_button)
    public void onLoginClicked() {
        if (userName.getText().length() > 0) {
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString(Constants.USERNAME, userName.getText().toString());
            edit.commit();

            hideKeyboard(this);

            updateUI();
        } else {
            makeShortToast("Please enter your username.");
        }
    }
}
