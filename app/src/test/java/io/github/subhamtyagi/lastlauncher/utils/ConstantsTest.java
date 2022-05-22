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

    //CS304 Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/162
    @Test
    public void testSortByName() {
        Assert.assertEquals(Constants.SORT_BY_NAME, 1);
    }

    //CS304 Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/162
    @Test
    public void testSortBySize() {
        Assert.assertEquals(Constants.SORT_BY_SIZE, 2);
    }
}
