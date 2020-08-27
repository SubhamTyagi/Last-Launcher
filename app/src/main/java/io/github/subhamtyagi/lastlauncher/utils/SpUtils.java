/*
 * Last Launcher
 * Copyright (C) 2019 Shubham Tyagi
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

// utility to handle shared prefs
class SpUtils {
    private volatile static SpUtils mInstance;
    private SharedPreferences mPref;

    private SpUtils() {
    }

    static SpUtils getInstance() {
        if (null == mInstance) {
            synchronized (SpUtils.class) {
                if (null == mInstance) {
                    mInstance = new SpUtils();
                }
            }
        }
        return mInstance;
    }

    void init(Context context) {
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    void putString(String key, String value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putString(key, value);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }

    void putLong(String key, long value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putLong(key, value);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }

    void putInt(String key, int value) {

        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putInt(key, value);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }

    void putIntCommit(String key, int value) {

        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putInt(key, value);
            editor.commit();
        } else throw new RuntimeException("First Initialize context");
    }

    void putBoolean(String key, boolean value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }

    public boolean getBoolean(String key) {
        if (mPref != null) {
            return mPref.getBoolean(key, false);
        } else throw new RuntimeException("First Initialize context");
    }

    boolean getBoolean(String key, boolean def) {
        if (mPref != null) {
            return mPref.getBoolean(key, def);
        } else throw new RuntimeException("First Initialize context");
    }

   /* Set<String> getStringSet(String key) {
        if (mPref != null) {
            return mPref.getStringSet(key, null);
        } else throw new RuntimeException("First Initialize context");
    }


    void putStringSet(String key, Set<String> value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putStringSet(key, value);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }*/


    String getString(String key) {
        if (mPref != null) {
            return mPref.getString(key, "");
        } else throw new RuntimeException("First Initialize context");
    }

    String getString(String key, String def) {
        if (mPref != null) {
            return mPref.getString(key, def);
        } else throw new RuntimeException("First Initialize context");
    }

    public long getLong(String key) {
        if (mPref != null) {
            return mPref.getLong(key, 0);
        } else throw new RuntimeException("First Initialize context");
    }

    public long getLong(String key, int defInt) {
        if (mPref != null) {
            return mPref.getLong(key, defInt);
        } else throw new RuntimeException("First Initialize context");
    }

    int getInt(String key) {
        if (mPref != null) {
            return mPref.getInt(key, 0);
        } else throw new RuntimeException("First Initialize context");
    }

    int getInt(String key, int defInt) {
        if (mPref != null) {
            return mPref.getInt(key, defInt);
        } else throw new RuntimeException("First Initialize context");
    }

    boolean contains(String key) {
        if (mPref != null) {
            return mPref.contains(key);
        } else throw new RuntimeException("First Initialize context");
    }


    void remove(String key) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.remove(key);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }

    void clear() {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.clear();
            editor.commit();
        } else throw new RuntimeException("First Initialize context");
    }

    boolean saveSharedPreferencesToFile() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HHSS", Locale.getDefault());
        df.format(new Date());
        String date = df.format(new Date());
        boolean res = false;
        File dst = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Backup_LastLauncher_" + date);
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            output.writeObject(mPref.getAll());
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }


    //stub
    @SuppressWarnings("unchecked")
    boolean loadSharedPreferencesFromFile(InputStream inputS) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(inputS);
            clear();
            //noinspection unchecked
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    putBoolean(key, (Boolean) v);
                else if (v instanceof Integer)
                    putInt(key, (Integer) v);
                else if (v instanceof Long)
                    putLong(key, (Long) v);
                else if (v instanceof String)
                    putString(key, ((String) v));
            }

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    Map<String, ?> getAll() {
        return mPref.getAll();
    }
}
