package com.redgeckotech.pushersample.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.redgeckotech.pushersample.PusherService;
import com.redgeckotech.pushersample.util.Utilities;

public class BaseActivity extends AppCompatActivity {

    public void makeShortToast(final String toastMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        PusherService pusherService = Utilities.getPusherService(this);
        pusherService.stopShutdownTimer();

        pusherService.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        PusherService pusherService = Utilities.getPusherService(this);
        pusherService.startShutdownTimer();
    }
}
