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

package io.github.subhamtyagi.lastlauncher.util;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class UserUtils {
    private UserManager userManager;


    public UserUtils(Context context) {

        this.userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);

    }

    public long getSerial(UserHandle user) {

        return userManager.getSerialNumberForUser(user);

    }

    public UserHandle getUser(long serial) {
        return userManager.getUserForSerialNumber(serial);
    }

    public long getCurrentSerial() {

        return getSerial(getCurrentUser());

    }

    public UserHandle getCurrentUser() {
        return Process.myUserHandle();

    }
}