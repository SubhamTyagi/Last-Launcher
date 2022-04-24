package io.github.subhamtyagi.lastlauncher.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConstantsTest {

    //Last-Launcher Issue link: https://github.com/SubhamTyagi/Last-Launcher/issues/155
    @Test
    public void testContantsNumber(){
        int target1 = Constants.SORT_BY_SIZE_ASENDING;
        int target2 = Constants.SORT_BY_SIZE;
        Assert.assertEquals(8,Constants.SORT_BY_SIZE_ASENDING);
        Assert.assertEquals(2,Constants.SORT_BY_SIZE);
    }
}
