package io.github.subhamtyagi.lastlauncher.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class SpUtils {
    private volatile static SpUtils mInstance;
    private SharedPreferences mPref;

    private SpUtils() {
    }

    public static SpUtils getInstance() {
        if (null == mInstance) {
            synchronized (SpUtils.class) {
                if (null == mInstance) {
                    mInstance = new SpUtils();
                }
            }
        }
        return mInstance;
    }

    public SpUtils init(Context context) {
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return this;
    }

    public SpUtils putString(String key, String value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putString(key, value);
            editor.apply();
            return this;
        } else throw new RuntimeException("First Initialize context");
    }

    public SpUtils putLong(String key, long value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putLong(key, value);
            editor.apply();
            return this;
        } else throw new RuntimeException("First Initialize context");
    }

    public SpUtils putInt(String key, int value) {

        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putInt(key, value);
            editor.apply();
            return this;
        } else throw new RuntimeException("First Initialize context");
    }

    public SpUtils putBoolean(String key, boolean value) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.putBoolean(key, value);
            editor.apply();
            return this;
        } else throw new RuntimeException("First Initialize context");
    }

    public boolean getBoolean(String key) {
        if (mPref != null) {
            return mPref.getBoolean(key, false);
        } else throw new RuntimeException("First Initialize context");
    }

    public boolean getBoolean(String key, boolean def) {
        if (mPref != null) {
            return mPref.getBoolean(key, def);
        } else throw new RuntimeException("First Initialize context");
    }


    public String getString(String key) {
        if (mPref != null) {
            return mPref.getString(key, "");
        } else throw new RuntimeException("First Initialize context");
    }

    public String getString(String key, String def) {
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

    public int getInt(String key) {
        if (mPref != null) {
            return mPref.getInt(key, 0);
        } else throw new RuntimeException("First Initialize context");
    }

    public long getInt(String key, int defInt) {
        if (mPref != null) {
            return mPref.getInt(key, defInt);
        } else throw new RuntimeException("First Initialize context");
    }

    public boolean contains(String key) {
        if (mPref != null) {
            return mPref.contains(key);
        } else throw new RuntimeException("First Initialize context");
    }


    public void remove(String key) {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.remove(key);
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }

    public void clear() {
        if (mPref != null) {
            Editor editor = mPref.edit();
            editor.clear();
            editor.apply();
        } else throw new RuntimeException("First Initialize context");
    }


}
