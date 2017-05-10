package com.mmnn.zoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mmnn.zoo.activity.WebViewActivity;

import java.util.ArrayList;

public class MainActivity extends Activity {
    public final static String TAG = MainActivity.class.getCanonicalName();

    private ListView mMainList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.main_activity_layout);
        mMainList = (ListView) findViewById(R.id.main_list);
        mMainList.addHeaderView(View.inflate(MainActivity.this, R.layout.main_list_header, null));
        mMainList.addFooterView(View.inflate(MainActivity.this, R.layout.main_list_footer, null));
        mAdapter = new ArrayAdapter<>(this, R.layout.main_list_item, getTitleList());
        mMainList.setAdapter(mAdapter);
        mMainList.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "onItemClick: " + "position: " + position + " id: " + id);
            switch ((int) id) {
                case 0:
//                        startActivity(new Intent(MainActivity.this, ExpandableExampleActivity.class));
                    startActivity(new Intent(MainActivity.this, DrawerActivity.class));
                    break;
                case 1:
//					startActivity(new Intent(MainActivity.this, LocationActivity.class));
                    break;
                case 2:
//					startActivity(new Intent(MainActivity.this, SNSShareActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, ShortcutActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MainActivity.this, WebViewActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(MainActivity.this, QRActivity.class));
                    break;
                case 6:
                    startActivity(new Intent(MainActivity.this, NativeActivity.class));
                    break;
                case 7:
                    startActivity(new Intent(MainActivity.this, PagerActivity.class));
                    break;
                default:
                    break;
            }
        });
    }

    private ArrayList<String> getTitleList() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.Playground));
        titleList.add(getString(R.string.title_location));
        titleList.add(getString(R.string.title_sns_share));
        titleList.add(getString(R.string.shortcut));
        titleList.add(getString(R.string.webview));
        titleList.add(getString(R.string.qrcode));
        titleList.add(getString(R.string.native_));
        titleList.add(getString(R.string.pager));
        return titleList;
    }

}
