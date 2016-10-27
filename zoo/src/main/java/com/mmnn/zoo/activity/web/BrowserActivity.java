package com.mmnn.zoo.activity.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends Activity {

    private BrowserFragment mFrag;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrag = new BrowserFragment();
        mFrag.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, mFrag, "BrowserActivity");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
    
    @Override
    public void onBackPressed() {
        if (mFrag != null && mFrag.canGoBack()) {
            mFrag.goBack();
        } else {
            super.onBackPressed();
        }
    }

}