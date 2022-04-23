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
    public void testSortBySize() {
        Assert.assertEquals(Constants.SORT_BY_SIZE, 2);
    }

    @Test
    public void testSortByRecentOpen(){
        Assert.assertEquals(Constants.SORT_BY_RECENT_OPEN,7);
    }
}
