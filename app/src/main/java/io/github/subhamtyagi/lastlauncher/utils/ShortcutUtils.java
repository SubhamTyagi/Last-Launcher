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

package io.github.subhamtyagi.lastlauncher.utils;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashSet;

import io.github.subhamtyagi.lastlauncher.model.Shortcut;

/**
 * This class manages Shortcut installed by user
 */
public class ShortcutUtils {
    //
    private static HashSet<String> shortcutName, shortcutUri;

    // get all shortcut installed in this launcher
    public static ArrayList<Shortcut> getAllShortcuts() {

        shortcutName = DbUtils.getShortcutNames();
        shortcutUri = DbUtils.getShortcutUris();

        if (shortcutUri == null) return null;
        if (shortcutName == null) return null;

        int size = shortcutUri.size();

        String[] names = new String[size];
        String[] uris = new String[size];

        int ii = 0;

        for (String s : shortcutUri) {
            uris[ii] = s;
            ii++;
        }

        ii = 0;
        for (String s : shortcutName) {
            names[ii] = s;
            ii++;
        }

        ArrayList<Shortcut> list = new ArrayList<>(shortcutName.size());

        for (int i = 0; i < names.length; i++) {
            list.add(new Shortcut(names[i], uris[i]));
        }

        return list;


    }

    /**
     * Add new shortcut
     *
     * @param shortcut instance of shortcut to be added
     */
    public static void addShortcut(Shortcut shortcut) {
        shortcutName = DbUtils.getShortcutNames();
        shortcutUri = DbUtils.getShortcutUris();

        if (shortcutName == null) {
            shortcutName = new HashSet<>();
            shortcutUri = new HashSet<>();
        }

        shortcutName.add(shortcut.getName());
        boolean b = shortcutUri.add(shortcut.getUri());

        DbUtils.setShortcutInstalledNames(shortcutName);
        DbUtils.setShortcutInstalledUris(shortcutUri);

    }

    /**
     * remove the shortcuts
     *
     * @param shortcut to be removed
     * @return true if successfully removed shortcut
     */
    public static boolean removeShortcut(Shortcut shortcut) {
        shortcutName = DbUtils.getShortcutNames();
        shortcutUri = DbUtils.getShortcutUris();

        // this condition never true
        if (shortcutUri == null) return false;

        shortcutName.remove(shortcut.getName());
        boolean b = shortcutUri.remove(shortcut.getUri());

        DbUtils.setShortcutInstalledNames(shortcutName);
        DbUtils.setShortcutInstalledUris(shortcutUri);

        return b;

    }

    /**
     * return true if shortcut is already install
     *
     * @param uri uri of shortcut
     * @return true if already installed
     */
    public static boolean isShortcutAlreadyAvailable(String uri) {
        shortcutUri = DbUtils.getShortcutUris();
        if (shortcutUri == null) return false;
        return shortcutUri.contains(uri);
    }

    /**
     * number of shortcut installed
     *
     * @return number of shortcut installed in this launcher
     */
    public static int getShortcutCounts() {
        shortcutUri = DbUtils.getShortcutUris();
        if (shortcutUri == null) return 0;
        return shortcutUri.size();
    }

    public static boolean isShortcutToApp(String uri) {
        try {
            Intent intent = Intent.parseUri(uri, 0);
            if (intent.getCategories() != null && intent.getCategories().contains(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
