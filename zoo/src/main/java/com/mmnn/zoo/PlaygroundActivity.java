package com.mmnn.zoo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.mmnn.zoo.utils.DateTimeUtils;
import com.mmnn.zoo.utils.DeviceUtils;
import com.mmnn.zoo.view.test.Sea;
import com.mmnn.zoo.view.widget.PullDownRefreshListView;

import java.util.Calendar;

public class PlaygroundActivity extends Activity {

    public final static String SRC_LOCKSCREEN = "lock_screen";
    private final static String TAG = PlaygroundActivity.class.getCanonicalName();
    private final static String CONTROLLER_AUTHORITY = "content://com.xiaomi.mitv.phone.remotecontroller.provider.LockScreenProvider";
    private final static String MI_WEATHER = "/mi_weather";
    private final static String ACTION_CONTROLLER_MAIN = "com.xiaomi.mitv.phone.remotecontroller.main";
    private final static String EXTRA_CALL_FROM = "call_from";
    private final static String SRC_MIHOME = "mihome";
    private final static String EXTRA_CONTROLLER_ID = "controller_id";
    private Sea mPlayGround;
    private ImageView mElephant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_playground);
//        View rootView = getWindow().getDecorView();
//        mPlayGround = (FrameLayout) rootView;
//		View rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
//		View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        TextView tv = (TextView) findViewById(R.id.sd_text);
        Typeface mMiuiTypeface = Typeface.createFromAsset(getAssets(), "fonts/miui_ex_nomal_0.ttf");
        tv.setTypeface(mMiuiTypeface);
        TextView tv2 = (TextView) findViewById(R.id.sd_text2);
        Typeface mCXHTypeface = Typeface.createFromAsset(getAssets(), "fonts/chaoxihei.ttf");
        tv2.setTypeface(mCXHTypeface);
        TextView tv3 = (TextView) findViewById(R.id.sd_text3);
        Typeface mPxTypeface = Typeface.createFromAsset(getAssets(), "fonts/fangzhengxiangsu12.ttf");
        tv3.setTypeface(mPxTypeface);


        mPlayGround = (Sea) findViewById(R.id.sea);

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
                gotoController();
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
        testProvider();
//        startActivity(new Intent(this, ShortcutActivity.class));
        if (mElephant.equals(null)) {

        }
        DeviceUtils.getScreenSize(this);
        Log.d(TAG, "========test=========" + DateTimeUtils.getCurFormattedDateTime());
        System.out.println(Calendar.getInstance().getTime());
        System.out.println(Calendar.getInstance().getTimeInMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(DateTimeUtils.addWeekDay("2016-02-28 16:00"));

        System.out.println(mElephant.getScaleX() + " " + mElephant.getScaleY());
        System.out.println(mElephant.getScrollX() + " " + mElephant.getScrollY());
        System.out.println(mElephant.getTranslationX() + " " + mElephant.getTranslationY());
        System.out.println(mElephant.getLeft() + " " + mElephant.getTop() + " " + mElephant.getRight() + " " + mElephant.getBottom());

        mElephant.setScrollX(20);
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

    private void gotoController() {
        Intent intent = new Intent(ACTION_CONTROLLER_MAIN);
        intent.putExtra(EXTRA_CALL_FROM, SRC_LOCKSCREEN);
        intent.putExtra(EXTRA_CONTROLLER_ID, 3);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "startIrController startActivity " + intent);
//        XmPluginHostApi.startActivity(context, intent);
        MyApp.getInstance().startActivity(intent);

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
            Cursor cursor = getContentResolver().query(Uri.parse(CONTROLLER_AUTHORITY /*+ MI_WEATHER*/), projection, null, null, null);
            if (cursor != null) {
                Log.d(TAG, "cursor = " + cursor.getCount());
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

