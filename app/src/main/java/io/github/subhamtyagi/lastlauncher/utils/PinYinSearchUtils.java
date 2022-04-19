/*
 * PinYinSearchUtils.java
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

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PinYinSearchUtils {
    static {
        // Add custom dictionary for last launcher scenario,
        // which means we manually put some words used frequently in Chinese Apps' names to correct PinYin-s.
        Pinyin.init(Pinyin.newConfig()
                .with(new PinyinMapDict() {
                    @Override
                    public Map<String, String[]> mapping() {
                        HashMap<String, String[]> map = new HashMap<>();
                        map.put("电话薄", new String[]{"DIAN", "HUA", "BU"});
                        map.put("電話簿", new String[]{"DIAN", "HUA", "BU"});
                        map.put("音乐", new String[]{"YIN", "YUE"});
                        map.put("音樂", new String[]{"YIN", "YUE"});
                        map.put("银行", new String[]{"YIN", "HANG"});
                        map.put("銀行", new String[]{"YIN", "HANG"});
                        map.put("帕弥什", new String[]{"PA", "MI", "SHI"});
                        map.put("果壳", new String[]{"GUO", "KE"});
                        map.put("果殼", new String[]{"GUO", "KE"});
                        map.put("重庆", new String[]{"CHONG", "QING"});
                        map.put("重慶", new String[]{"CHONG", "QING"});
                        map.put("重返", new String[]{"CHONG", "FAN"});
                        map.put("东阿", new String[]{"DONG", "E"});
                        map.put("東阿", new String[]{"DONG", "E"});
                        map.put("番禺", new String[]{"PAN", "YU"});
                        return map;
                    }
                }));
    }

    /**
     * Converts the input string to pinyin, using the user dictionary you set up earlier,
     * and inserts separators in character units.
     * For example, when the separator is ",", given the input "hello:中国",
     * this method will output "h,e,l,l,o,:,ZHONG,GUO,!"
     *
     * @param str       input string
     * @param separator separator
     * @return converted string from Chinese to Pinyin.
     */
    public static String toPinyin(String str, String separator) {
        return Pinyin.toPinyin(str, separator); //This is a wrapper method for om.github.promeg.pinyinhelper.Pinyin.toPinyin .
    }

    /**
     * convert the input character to Pinyin
     * @param c input character
     * @return return pinyin if c is chinese in uppercase, String.valueOf(c) otherwise.
     */
    public static String toPinyin(char c) {
        return Pinyin.toPinyin(c); //This is a wrapper method for om.github.promeg.pinyinhelper.Pinyin.toPinyin .
    }

    /**
     * @param query   pattern string
     * @param strings text string
     * @return true if @query is sequentially found in @strings else false, and @query can be Chinese Pinyin .
     * For example, "LL" is sequentially found in "Last Launcher", so return true;
     * "yyds" is sequentially found in "你是永远滴神", so it is also true.
     * This method supports Chinese Pinyin Search with "Duoyinzi".
     * For example, "yin yue" matches "音乐" while "yin le" doesn't.
     */
    public static boolean pinYinSimpleFuzzySearch(CharSequence query, String strings) {
        return Utils.simpleFuzzySearch(toPinyin(query.toString().replaceAll("\\s+",""), ""),
                toPinyin(strings, ""));
    }
}
