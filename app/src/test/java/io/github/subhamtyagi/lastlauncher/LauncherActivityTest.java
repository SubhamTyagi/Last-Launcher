package io.github.subhamtyagi.lastlauncher;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import io.github.subhamtyagi.lastlauncher.utils.Constants;
import io.github.subhamtyagi.lastlauncher.utils.Gestures;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LauncherActivityTest {

    private LauncherActivity launcherActivityUnderTest;

    @Before
    public void setUp() {
        launcherActivityUnderTest = new LauncherActivity();
    }


    @Test
    //CS304 Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/157
    public void testOnClick() {
        // Setup
        final View view = new View(null);

        // Run the test
        launcherActivityUnderTest.onClick(view);

        // Verify the results
    }


    @Test
    //CS304 Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/157
    public void testOnLongClick() {
        // Setup
        final View view = new View(null);

        // Run the test
        final boolean result = launcherActivityUnderTest.onLongClick(view);

        // Verify the results
        assertTrue(result);
    }


    @Test
    //CS304 Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/157
    public void testOnSwipe() {
        // Setup
        // Run the test
        launcherActivityUnderTest.onSwipe(Gestures.Direction.SWIPE_UP);

        // Verify the results
    }

    @Test
    //CS304 Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/157
    public void testOnDoubleTap() {
        // Setup
        // Run the test
        launcherActivityUnderTest.onDoubleTap();

        // Verify the results
    }


}
