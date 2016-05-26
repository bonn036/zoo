package com.mmnn.bonn036.zoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mmnn.bonn036.zoo.activity.WebViewActivity;
import com.mmnn.bonn036.zoo.expendable.ExpandableExampleActivity;

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
        mMainList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: " + "position: " + position + " id: " + id);
                switch ((int) id) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, ExpandableExampleActivity.class));
                        break;
//				case 1:
//					startActivity(new Intent(MainActivity.this, LocationActivity.class));
//					break;
//				case 2:
//					startActivity(new Intent(MainActivity.this, SNSShareActivity.class));
//					break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, ShortcutActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, WebViewActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, QRActivity.class));
                        break;
                    default:
                        break;
                }
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
        return titleList;
    }

}
