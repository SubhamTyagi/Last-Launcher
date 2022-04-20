/*
 * PinYinSearchUtilsTest.java
 * Copyright (C) 2022 Ye Canming <2603119857@qq.com>
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
package io.github.subhamtyagi.lastlauncher.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PinYinSearchUtilsTest {
    /**
     * Suite: testToPinyin
     *      Test whether the pinyin library that we depends on is reliable.
     * Case: when the input Chinese is simple.
     */
    @Test
    public void testToPinyinSimple(){
        assertEquals("ZHONG WEN CE SHI", PinYinSearchUtils.toPinyin("中文测试", " "));
        assertEquals("FAN TI CE SHI", PinYinSearchUtils.toPinyin("繁體測試", " "));
        assertEquals("DIAN ZI YOU JIAN", PinYinSearchUtils.toPinyin("电子邮件", " "));
        assertEquals("DIAN ZI YOU JIAN", PinYinSearchUtils.toPinyin("電子郵件", " "));
        assertEquals("XIANG CE", PinYinSearchUtils.toPinyin("相册", " "));
        assertEquals("RI LI", PinYinSearchUtils.toPinyin("日历", " "));
        assertEquals("SHE ZHI", PinYinSearchUtils.toPinyin("设置", " "));
        assertEquals("WEN JIAN GUAN LI", PinYinSearchUtils.toPinyin("文件管理", " "));
    }
    /**
     * Suite: testToPinyin
     *      Test whether the pinyin library that we depends on is reliable.
     * Case: when the input Chinese contains "Duoyinzi" (a single character
     * with multiple possible pinyin-s whose final pinyin is decided
     * by the specific context.)  .
     */
    @Test
    public void testToPinyinWhenDuoyinzi(){
        assertEquals("DIAN HUA BU", PinYinSearchUtils.toPinyin("电话薄", " "));
        assertEquals("DIAN HUA BU", PinYinSearchUtils.toPinyin("電話簿", " "));
        assertEquals("YIN YUE", PinYinSearchUtils.toPinyin("音乐", " "));
        assertEquals("WANG YI YUN YIN YUE", PinYinSearchUtils.toPinyin("网易云音乐", " "));
        assertEquals("WANG YI YUN YIN YUE", PinYinSearchUtils.toPinyin("網易雲音樂", " "));
        assertEquals("DE DAO", PinYinSearchUtils.toPinyin("得到", " "));
        assertEquals("ZHAN SHUANG PA MI SHI", PinYinSearchUtils.toPinyin("战双帕弥什", " "));
        assertEquals("SHEN ME ZHI DE MAI", PinYinSearchUtils.toPinyin("什么值得买", " "));
        assertEquals("GUO KE", PinYinSearchUtils.toPinyin("果壳", " "));
        assertEquals("CHONG QING YI DONG", PinYinSearchUtils.toPinyin("重庆移动", " "));
        assertEquals("CHONG QING YI DONG", PinYinSearchUtils.toPinyin("重慶移動", " "));
        assertEquals("CHONG QING LIAN TONG", PinYinSearchUtils.toPinyin("重庆联通", " "));
        assertEquals("DONG E CHUAN MEI", PinYinSearchUtils.toPinyin("东阿传媒", " "));
        assertEquals("PAN YU MIN SHENG KA", PinYinSearchUtils.toPinyin("番禺民生卡", " "));
        assertEquals("FAN QIE ZHONG", PinYinSearchUtils.toPinyin("番茄钟", " "));
        assertEquals("LE SHAN SHANG YE YIN HANG", PinYinSearchUtils.toPinyin("乐山商业银行", " "));
        assertEquals("ZHONG ZHUANG SHANG ZHEN", PinYinSearchUtils.toPinyin("重装上阵", " "));
        assertEquals("CHONG FAN DI GUO", PinYinSearchUtils.toPinyin("重返帝国", " "));
    }
    /**
     * Suite: testToPinyin
     *      Test whether the pinyin library that we depends on is reliable.
     * Case: when it is a mix of Chinese and other languages.
     */
    @Test
    public void testToPinyinWithNonChinese(){
        assertEquals("ZHONGWEN English CESHI", PinYinSearchUtils.toPinyin("中文 English 测试", ""));
        assertEquals("F-Droid", PinYinSearchUtils.toPinyin("F-Droid", ""));
    }

    /**
     * Suite: testPinyinSearch
     *      Test whether the fuzzy search algorithm is reliable.
     * Case: when it is simple.
     */
    @Test
    public void testPinYinSearchWhenSimple() {
        // basic
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("zgyd", "中国移动"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("ZgYd", "中国移动"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("ZhGYD", "中國移動"));
        assertFalse(PinYinSearchUtils.pinYinSimpleFuzzySearch("ZhDyg", "中国移动"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("Zhong Guo Yi Dong", "中国移动"));
    }
    /**
     * Suite: testPinyinSearch
     *      Test whether the fuzzy search algorithm is reliable.
     * Case: when the query is with blank and chinese character.
     */
    @Test
    public void testPinYinSearchWithBlankAndChineseInQuery(){
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch(" Zhong 国 Yi\nDong", "中国移动"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch(" ZhongYi动 \t", "中國移動"));
        assertFalse(PinYinSearchUtils.pinYinSimpleFuzzySearch("ZhongYiD动 \t", "中国移动"));
    }

    /**
     * Suite: testPinyinSearch
     *      Test whether the fuzzy search algorithm is reliable.
     * Case: when the text to be matched is with blank and chinese character.
     */
    @Test
    public void testPinYinSearchWithBlankAndChineseInText(){
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("Zhong Guo Yi Dong", " 中国 移动"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch(" Zhong 国 Yi\nDong", "中 国移\n动"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch(" ZhongYi动 \t", "中國移 动"));
        assertFalse(PinYinSearchUtils.pinYinSimpleFuzzySearch("ZhongYiD动 \t", "中国\t移动"));
    }
    /**
     * Suite: testPinyinSearch
     *      Test whether the fuzzy search algorithm is reliable.
     * Case: when the text to be matched contains non-Chinese characters.
     */
    @Test
    public void testPinYinSearchWhenTextIsNotAllChinese(){
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("Lst Laun", "Last Launcher"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("ll ", "Last Launcher"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("aa ", "Last Launcher"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("啟動器", "Last Launcher 让您爱不释手的究极启动器"));
        assertTrue(PinYinSearchUtils.pinYinSimpleFuzzySearch("LLQ", "Last Launcher 萊斯特啟動器"));
    }
}
