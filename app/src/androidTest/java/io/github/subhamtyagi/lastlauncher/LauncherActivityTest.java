package io.github.subhamtyagi.lastlauncher;


import static org.junit.Assert.assertEquals;


import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.subhamtyagi.lastlauncher.utils.DbUtils;


@LargeTest
@RunWith(AndroidJUnit4.class)
/**
 * To test if it starts with default theme
 * CS304 Issue Link : https://github.com/SubhamTyagi/Last-Launcher/issue/142
 */
public class LauncherActivityTest { //NOPMD - suppressed AtLeastOneConstructor - It is a test file


    /**
     * Construct LauncherActivity
     */
    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    /**
     * Test
     */
    @Test
    public void launcherActivityTest() {

        assertEquals("Theme is not default",R.style.AppTheme,DbUtils.getTheme());
    }


}
