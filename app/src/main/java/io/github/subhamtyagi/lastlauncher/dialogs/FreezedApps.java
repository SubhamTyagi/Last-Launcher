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
import android.content.Context;
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
import io.github.subhamtyagi.lastlauncher.util.DbUtils;

public class FreezedApps extends Dialog {

    private final ArrayList<Apps> mAppsList;
    private ListView listView;
    private Context context;

    public FreezedApps(Context context, ArrayList<Apps> appsList) {
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
                apps.setFreeze(false);
                updateList(mAppsList);
            }
            return true;

        });
        popupMenu.show();

    }


    private void updateList(ArrayList<Apps> appsList) {
        ArrayList<Apps> appsList2 = new ArrayList<>();
        for (Apps apps : appsList) {
            if (apps.isFreezeSize()) {
                appsList2.add(apps);
            }
        }

        if (appsList2.isEmpty()) {
            //Toast.makeText(context, "no freezed apps", Toast.LENGTH_SHORT).show();
            cancel();
            dismiss();
            return;
        }

        UniversalAdapter adapter = new UniversalAdapter(context, appsList2);
        listView.setAdapter(adapter);

        adapter.setOnClickListener(new UniversalAdapter.OnClickListener() {
            @Override
            public void onClick(Apps apps, View view) {
                confirmationAndRemove(apps, view);
            }
        });
    }

}
