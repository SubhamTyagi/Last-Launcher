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

package io.github.subhamtyagi.lastlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShortcutReceiver extends BroadcastReceiver {

    private static final String TAG = "ShortcutReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent shortcutIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        String uri = shortcutIntent.toUri(0);
        // check this is already included in list

        if (shortcutIntent.getAction() == null) {
            shortcutIntent.setAction(Intent.ACTION_VIEW);
        }
        String name = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        Log.d(TAG, "onReceive: uri:::" + uri);
        Log.d(TAG, "onReceive: name::" + name);



      /*  String packageName;
        String resourceName;


        Intent.ShortcutIconResource resource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        if (resource != null) {
            packageName = resource.packageName;
            resourceName = resource.resourceName;
        } else {
            // wtf shortcut
            return;

        }

        Log.d(TAG, "onReceive: package::" + packageName);
        Log.d(TAG, "onReceive: resource" + resourceName);
*/
    }


    private boolean isAppLink(String uri) {
        try {
            Intent intent = Intent.parseUri(uri, 0);
            if (intent.getCategories() != null && intent.getCategories().contains(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
