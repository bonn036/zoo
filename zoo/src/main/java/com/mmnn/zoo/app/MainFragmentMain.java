package com.mmnn.zoo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmnn.zoo.R;

/**
 * Created by dz on 2017/5/26.
 */

public class MainFragmentMain extends BaseFragment implements View.OnClickListener {
    private ViewGroup mRootView, mContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, null);
//        mActionView = (ViewGroup) View.inflate(getActivity(), R.layout.action_bar_user, null);
            initView();
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return true;
    }

    private void initView() {

    }

    @Override
    public void onClick(View v) {

    }
}
