package com.mmnn.bonn036.zoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShortcutActivity extends Activity {

    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
//		createShortcutByCall();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initUI() {
        setContentView(R.layout.activity_shortcut);
        Button addShortcutButton = (Button) findViewById(R.id.button_add_shortcut);
        addShortcutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createShortcut();
            }
        });
        Button delShortcutButton = (Button) findViewById(R.id.button_del_shortcut);
        delShortcutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delShortcut();
            }
        });
    }

    private void createShortcutByCall() {
        if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_CREATE_SHORTCUT)) {
            Intent shortcutIntent = new Intent();
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut));
            Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.default_portrait);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(this, MainActivity.class.getName());
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            setResult(RESULT_OK, shortcutIntent);
        }
    }

    private void createShortcut() {
        sendBroadcast(createShortcutIntent(ACTION_INSTALL_SHORTCUT));
    }

    private void delShortcut() {
        sendBroadcast(createShortcutIntent(ACTION_UNINSTALL_SHORTCUT));
    }

    public Intent createShortcutIntent(String action) {
        Intent shortcutIntent = new Intent(action);
        Intent intent = createPendingIntent();
        if (intent != null) {
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
//			shortcutIntent.putExtra("duplicate", false);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut));
            Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        }
        return shortcutIntent;
    }

    private Intent createPendingIntent() {
//        Intent intent = new Intent();
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        intent.setAction("shortcut_test");
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Log.d("Short Cut Test", getPackageName() + " " + MainActivity.class.getName());
//        intent.setComponent(new ComponentName(getPackageName(), PlaygroundActivity.class.getName()));
//        intent.putExtra("type", 1);

        Intent intent = new Intent(this, PlaygroundActivity.class);
        return intent;
    }
}
