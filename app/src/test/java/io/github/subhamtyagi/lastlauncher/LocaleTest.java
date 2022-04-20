package io.github.subhamtyagi.lastlauncher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Locale;

public class LocaleTest {
    /**
     * In java8, switch does not support something like
     * <code>
     * switch (mLocale.getLanguage()){
     * case Locale.CHINESE.getLanguage():{
     * <p>
     * }
     * }
     * </code> because Locale.CHINESE.getLanguage() is not constant.
     * instead, we write "zh" directly.
     * We use this test to make sure "zh" always works.
     */
    @Test
    public void testGet() {
        Locale zh_CN = Locale.SIMPLIFIED_CHINESE;
        Locale zh_TW = Locale.TRADITIONAL_CHINESE;
        Locale zh = Locale.CHINESE;
        assertEquals("zh", zh_CN.getLanguage());
        assertEquals("zh", zh_TW.getLanguage());
        assertEquals("zh", zh.getLanguage());
        assertEquals("CN", zh_CN.getCountry());
        assertEquals("TW", zh_TW.getCountry());
        assertEquals("", zh.getCountry());
        assertEquals("zh-CN", zh_CN.toLanguageTag());
        assertEquals("zh-TW", zh_TW.toLanguageTag());
        assertEquals("zh", zh.toLanguageTag());
    }
}
