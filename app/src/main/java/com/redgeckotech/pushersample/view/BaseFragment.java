package com.redgeckotech.pushersample.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment {
    public void makeShortToast(String toastMessage) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).makeShortToast(toastMessage);
        }
    }

    public void setActivityTitle(String title) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }
}
