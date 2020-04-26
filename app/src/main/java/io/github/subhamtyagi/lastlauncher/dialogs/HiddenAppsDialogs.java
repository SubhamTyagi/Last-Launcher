/*
 * Last Launcher
 * Copyright (C) 2019,2020 Shubham Tyagi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.subhamtyagi.lastlauncher.dialogs;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.adapters.UniversalAdapter;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;

public class HiddenAppsDialogs extends Dialog {

    private final ArrayList<Apps> mAppsList;
    private ListView listView;
    private Context context;

    public HiddenAppsDialogs(Context context, ArrayList<Apps> appsList) {
        super(context);
        this.context = context;
        mAppsList = appsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hidden_apps);
        listView = findViewById(R.id.hidden_app_list);
        updateList(mAppsList);

    }



    private void confirmationAndRemove(Apps apps, View view) {

        Context ctx;
        if (DbUtils.getTheme() == R.style.Wallpaper)
            ctx = new ContextThemeWrapper(context, R.style.AppTheme);
        else
            ctx = new ContextThemeWrapper(context, DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.getMenuInflater().inflate(R.menu.remove_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            if (menuItem.getItemId() == R.id.menu_remove_this) {
                apps.setAppHidden(false);
                updateList(mAppsList);
            } else if (menuItem.getItemId() == R.id.menu_run_this_app) {
                if (!apps.isShortcut()) {
                    String[] strings = apps.getActivityName().split("/");
                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.setClassName(strings[0], strings[1]);
                    intent.setComponent(new ComponentName(strings[0], strings[1]));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
            return true;

        });
        popupMenu.show();

    }


    private void updateList(ArrayList<Apps> appsList) {
        ArrayList<Apps> appsList2 = new ArrayList<>();
        for (Apps apps : appsList) {
            if (apps.isHidden()) {
                appsList2.add(apps);
            }
        }
        if (appsList2.isEmpty()) {
            cancel();
            dismiss();
            return;
        }

        UniversalAdapter adapter = new UniversalAdapter(context, appsList2);
        listView.setAdapter(adapter);

        adapter.setOnClickListener(this::confirmationAndRemove);
    }

}
