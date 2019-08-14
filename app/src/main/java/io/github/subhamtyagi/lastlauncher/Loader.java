package io.github.subhamtyagi.lastlauncher;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

import io.github.subhamtyagi.lastlauncher.model.Apps;

public class Loader extends AsyncTaskLoader<ArrayList<Apps>> {
    final PackageManager mPackageManager;

    public Loader(@NonNull Context context) {
        super(context);
        mPackageManager = context.getPackageManager();
    }

    @Nullable
    @Override
    public ArrayList<Apps> loadInBackground() {
        return null;
    }

}
