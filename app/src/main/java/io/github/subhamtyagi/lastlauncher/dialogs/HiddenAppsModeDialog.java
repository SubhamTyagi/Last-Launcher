package io.github.subhamtyagi.lastlauncher.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.model.Apps;

public class HiddenAppsModeDialog extends Dialog implements View.OnClickListener {
    LauncherActivity launcherActivity;

    public HiddenAppsModeDialog(Context context, LauncherActivity launcherActivity) {
        super(context);
        this.launcherActivity = launcherActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = findViewById(R.id.hidden_linear_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            ll.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.in_list) {
            launcherActivity.showHiddenApps();
        } else if (view.getId() == R.id.in_main) {
            launcherActivity.changeAppsList();
        }
    }
}
