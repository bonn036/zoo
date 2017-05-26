package com.mmnn.zoo.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.mmnn.zoo.BuildConfig;
import com.mmnn.zoo.R;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, DefaultHardwareBackBtnHandler {
    public final static String TAG = HomeActivity.class.getCanonicalName();

    private SparseArray<BaseFragment> fragments = new SparseArray<>();
    private BaseFragment mCurFragment = null;
    private View mCurrentTab;

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReactRootView = new ReactRootView(this);

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

        mReactRootView.startReactApplication(mReactInstanceManager, "Zoo", null);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    private void initUI() {
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onClick(View v) {
        if (v.isSelected()) {
            return;
        }
        v.setSelected(true);
        if (mCurrentTab != null) {
            mCurrentTab.setSelected(false);
        }
        mCurrentTab = v;
        // Add or replace fragment.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurFragment != null) {
            mCurFragment.onPause();
            transaction.hide(mCurFragment);
        }
        mCurFragment = fragments.get(v.getId());
        if (mCurFragment == null) {
            switch (v.getId()) {
                case R.id.tab_main:
                    mCurFragment = new MainFragmentMain();
                    break;
                case R.id.tab_sub:
                    mCurFragment = new MainFragmentSub();
                    break;
                default:
                    return;
            }
            fragments.put(v.getId(), mCurFragment);
            transaction.add(R.id.contentContainer, mCurFragment);
        } else {
            mCurFragment.onResume();
        }
        transaction.show(mCurFragment);
        transaction.commit();

//        mCurFragment = (BaseFragment) fragments.get(v.getId());
//        String tag = String.valueOf(v.getId());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        int count = getSupportFragmentManager().getBackStackEntryCount();
//        Log.e("XXXXX", "count: " + count + " tag: " + tag);
//        if (count > 0) {
//            transaction.replace(R.id.contentContainer, mCurFragment, tag);
////            transaction.show(mCurFragment);
//        } else {
//            transaction.add(R.id.contentContainer, mCurFragment, tag);
//        }
//        transaction.addToBackStack(null);
//        transaction.commit();

    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }
}
