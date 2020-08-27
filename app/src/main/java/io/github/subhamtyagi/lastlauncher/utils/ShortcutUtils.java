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

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import io.github.subhamtyagi.lastlauncher.model.Shortcut;

/**
 * This class manages Shortcut installed by user
 * most of the methods are now wrapper to Database
 */
public class ShortcutUtils {

    private volatile static Database db;
    private static ShortcutUtils mInstance;

    public static ShortcutUtils getInstance(Context context) {
        if (null == db) {
            synchronized (ShortcutUtils.class) {
                if (null == db) {
                    db = new Database(context);
                    mInstance = new ShortcutUtils();
                }
            }
        }
        return mInstance;
    }

    public void close() {
        db.close();
    }

    private void checkDB() throws Throwable {
        if (db == null) {
            throw new Throwable("Db is null");
        }
    }

    public ArrayList<Shortcut> getAllShortcuts() {
        return db.getAllShortcuts();
    }

    /**
     * Add new shortcut
     *
     * @param shortcut instance of shortcut to be added
     */

    public void addShortcut(Shortcut shortcut) {

        db.insertShortcut(shortcut.getName(), shortcut.getUri());
    }

    /**
     * remove the shortcuts
     *
     * @param shortcut to be removed
     */


    public void removeShortcut(Shortcut shortcut) {
        db.deleteShortcuts(shortcut.getName());
    }


    /**
     * return true if shortcut is already install
     *
     * @param name uri of shortcut
     * @return true if already installed
     */
    public boolean isShortcutAlreadyAvailable(String name) {
        return db.shortcutsExists(name);
    }


    /**
     * number of shortcut installed
     *
     * @return number of shortcut installed in this launcher
     */
    public int getShortcutCounts() {
        return db.getShortcutsCounts();
    }

    public boolean isShortcutToApp(String uri) {
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
