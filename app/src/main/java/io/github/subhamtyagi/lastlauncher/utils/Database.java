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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import io.github.subhamtyagi.lastlauncher.model.Shortcut;

//This is new database based on SQLITE
// Why not ROOM: simply make this launcher lightweight
public class Database extends SQLiteOpenHelper {


    private static final String DB_NAME = "launcher.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME_SHORTCUTS = "shortcuts";
    private static final String SHORTCUT_URI = "uri";
    private static final String SHORTCUT_NAME = "name";
    private static final String CREATE_SHORTCUTS_TABLE = "CREATE TABLE shortcuts(id INTEGER  PRIMARY KEY AUTOINCREMENT, name TEXT, uri TEXT )";

    //private static final String TABLE_NAME_APPS = "apps";
    /*private static final String APP_ACTIVITY = "activity";
    private static final String IS_SHORTCUTS="shortcut";
    private static final String APP_NAME = "name";
    private static final String APP_ORIGINAL_NAME = "original_name";
    private static final String APP_SIZE = "size";
    private static final String APP_COLOR = "color";
    private static final String APP_VISIBILITY = "visibility";
    private static final String APP_FROZEN = "frozen";
    private static final String APP_EXTERNAL_SOURCE_COLOR = "extrn_src_color";
    private static final String APP_GROUP_PREFIX = "gp_prefix";
    private static final String APP_CATEGORY = "category";
    private static final String APP_OPENING_COUNTS = "open_count";
    private static final String id="id";
    */
    // private static final String CREATE_APPS_TABLE = "CREATE TABLE

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHORTCUTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SHORTCUTS);
        onCreate(db);
    }

    public void insertShortcut(String name, String uri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(SHORTCUT_NAME, name);
        cValues.put(SHORTCUT_URI, uri);
        long newRowId = db.insert(TABLE_NAME_SHORTCUTS, null, cValues);
        db.close();
    }

    public void deleteShortcuts(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_SHORTCUTS, SHORTCUT_NAME + " = ?", new String[]{name});
        db.close();
    }

    public ArrayList<Shortcut> getAllShortcuts() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Shortcut> shortcuts = new ArrayList<>();
        String query = "SELECT uri,name FROM " + TABLE_NAME_SHORTCUTS;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Shortcut shortcut = new Shortcut();
            shortcut.setName(cursor.getString(cursor.getColumnIndex(SHORTCUT_NAME)));
            shortcut.setUri(cursor.getString(cursor.getColumnIndex(SHORTCUT_URI)));
            shortcuts.add(shortcut);
        }
        cursor.close();
        return shortcuts;
    }

    public int getShortcutsCounts() {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME_SHORTCUTS);
        db.close();
        return (int) count;
    }

    public boolean shortcutsExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT EXISTS (SELECT * FROM shortcuts WHERE uri='" + name + "' LIMIT 1)";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
}
