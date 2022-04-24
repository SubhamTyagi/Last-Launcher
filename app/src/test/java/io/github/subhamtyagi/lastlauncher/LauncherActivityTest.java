package io.github.subhamtyagi.lastlauncher;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import io.github.subhamtyagi.lastlauncher.utils.Gestures;
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


    //Last-Launcher Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/155
    @Test
    public void testOnClick() {
        // Setup
        final View view = new View(null);

        // Run the test
        launcherActivityUnderTest.onClick(view);

        // Verify the results
    }
}
