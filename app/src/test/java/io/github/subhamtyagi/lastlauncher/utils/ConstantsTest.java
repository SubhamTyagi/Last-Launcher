package io.github.subhamtyagi.lastlauncher.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConstantsTest {

    private Constants constantsUnderTest;

    @Before
    public void setUp() {
        constantsUnderTest = new Constants();
    }


    @Test
    //CS304 Issue link:
    public void testOnConstants() {
        int a= Constants.SORT_BY_NAME_MODIFY;
        Assert.assertEquals(1,a);
    }
}
