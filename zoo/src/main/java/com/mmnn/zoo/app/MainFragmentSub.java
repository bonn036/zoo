package com.mmnn.zoo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.shell.MainReactPackage;
import com.mmnn.zoo.BuildConfig;
import com.mmnn.zoo.R;

import static com.facebook.react.common.ApplicationHolder.getApplication;

/**
 * Created by dz on 2017/5/26.
 */

public class MainFragmentSub extends BaseFragment implements View.OnClickListener {
    private ViewGroup mRootView, mContainer;

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_sub, null);
            mReactRootView = (ReactRootView) mRootView.findViewById(R.id.rn_root);
//        mActionView = (ViewGroup) View.inflate(getActivity(), R.layout.action_bar_user, null);
            initView();
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        mReactRootView.startReactApplication(mReactInstanceManager, "MMNNZooApp", null);
    }

    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return true;
    }

    private void initView() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {

    }
}
