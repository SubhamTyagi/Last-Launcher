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

import java.util.Arrays;
import java.util.List;
public class Utils {


    // popular and often used app on fdroid and in android system
    // and some my favourite apps
    public static List<String> getOftenAppsList() {
        String[] list = {
                "com.google.android.apps.maps",
                "com.android.gallery3d",
                "com.android.settings",
                "com.android.mms",
                "com.android.contacts",
                "com.android.email",
                "com.android.vending",
                "com.android.chrome",
                "com.android.calendar",
                "org.thoughtcrime.securesms",
                "org.telegram.messenger",
                "com.whatsapp",
                "org.sufficientlysecure.viewer",
                "org.fdroid.fdroid",
                "org.mozilla.firefox",
                "org.schabi.newpipe",
                "eu.faircode.email",
                "com.simplemobiletools.gallery.pro",
                "com.simplemobiletools.contacts.pro",
                "com.simplemobiletools.calendar.pro",
                "com.kunzisoft.keepas.libre",
                "org.videolan.vlc",
                "com.termux",
                "com.nextcloud.android",
                "ryey.easer.beta",
                "it.niedermann.owncloud.notes",
                "com.keylesspalace.tusky",
                "de.nproth.pin",
                "io.github.subhamtyagi.privacyapplock",
                "superfreeze.tool.android",
                "fr.gouv.etalab.mastodon",
                "im.vector.alpha",
                "com.nextcloud.client",
                "com.duckduckgo.mobile.android",
                "eu.siacs.conversations",
                "org.torproject.torbrowser_alpha",
                "com.aurora.adroid", "net.osmand.plus",
                "org.torproject.android", "com.mediatek.filemanager",
                "com.sec.android.gallery3d", "com.sec.android.app.myfiles",
                "com.android.dialer", "com.android.camera",
                "org.mozilla.fennec_fdroid",
                "com.mi.android.globalFileexplorer",
                "com.miui.gallery",
                "com.miui.calculator",
                "com.nonsenseapps.feeder" };
        return Arrays.asList(list);

    }

    public static String getPackageNameFromActivityName(String activityName){
        return activityName.split("/")[0];
    }
}
