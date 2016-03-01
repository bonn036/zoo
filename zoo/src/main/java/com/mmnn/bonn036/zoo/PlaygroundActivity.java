package com.mmnn.bonn036.zoo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mmnn.bonn036.zoo.utils.DateTimeUtils;
import com.mmnn.bonn036.zoo.utils.DeviceUtils;
import com.mmnn.bonn036.zoo.view.widget.PullDownRefreshListView;

import java.util.Calendar;

public class PlaygroundActivity extends Activity {

    private final static String TAG = PlaygroundActivity.class.getCanonicalName();
    private final static String CONRTOLLER_AUTHORITY = "content://com.xiaomi.mitv.phone.remotecontroller.provider.LockScreenProvider";
    private final static String MI_WEATHER = "/mi_weather";
    private FrameLayout mPlayGround;
    private ImageView mElephant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_playground);
        View rootView = getWindow().getDecorView();
//		View rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
//		View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        mPlayGround = (FrameLayout) rootView;

        mElephant = new ImageView(this);
        mElephant.setImageResource(R.mipmap.ic_launcher);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mPlayGround.addView(mElephant, lp);
//
        PullDownRefreshListView listView = (PullDownRefreshListView) findViewById(R.id.main_list);
//
//        String[] adapterData = new String[]{"Afghanistan", "Albania", "Algeria",
//                "American Samoa", "Andorra", "Angola", "Anguilla",
//                "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia",
//                "Aruba", "Australia", "Austria", "Azerbaijan", "Bahrain",
//                "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
//                "Benin", "Bermuda", "Bhutan", "Bolivia",
//                "Bosnia and Herzegovina", "Botswana", "Bouvet Island"};
//        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterData));
        listView.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                DeviceUtils.getScreenSize(this);
//                gotoM();
//                testProvider();
//                Scale();
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                test();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void test() {
        startActivity(new Intent(this, ShortcutActivity.class));
        Log.d(TAG, "========test=========");
        System.out.println(Calendar.getInstance().getTime());
        System.out.println(Calendar.getInstance().getTimeInMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(DateTimeUtils.addWeekDay("2016-02-28 16:00"));
    }

    private void gotoM() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse("miremote://addir/brand?deviceid=1"));
            intent.setData(Uri.parse("miremote://home/controller"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void comboAct() {
        AnimationSet combo = new AnimationSet(false);
        ScaleAnimation sa = new ScaleAnimation(2.0f, 0.1f, 2.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(2000);
        combo.addAnimation(sa);
        combo.start();
    }

    private void Scale() {
        ScaleAnimation sa = new ScaleAnimation(2.0f, 0.1f, 2.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(2000);
        mElephant.startAnimation(sa);
    }

    private void testProvider() {
        try {
            String[] projection = new String[]{"controller_id", "controller_name", "device_type", "intent_action"};
            Cursor cursor = getContentResolver().query(Uri.parse(CONRTOLLER_AUTHORITY + MI_WEATHER), projection, null, null, null);
            Log.d(TAG, "cursor = " + cursor);
            if (cursor != null) {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

