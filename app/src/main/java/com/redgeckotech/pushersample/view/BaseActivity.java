package com.redgeckotech.pushersample.view;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    public void makeShortToast(final String toastMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
