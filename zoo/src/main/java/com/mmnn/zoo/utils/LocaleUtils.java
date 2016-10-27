/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.mmnn.zoo.utils;


import com.mmnn.zoo.utils.HanziToPinyin.Token;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * This utility class provides customized sort key according the locale.
 * Add by libra.
 * @hide
 */
public class LocaleUtils {

    private static final String JAPANESE_LANGUAGE = Locale.JAPANESE.getLanguage().toLowerCase();
    private static final String KOREAN_LANGUAGE = Locale.KOREAN.getLanguage().toLowerCase();
    private static LocaleUtils sSingleton;
    private HashMap<Integer, LocaleUtilsBase> mUtils =
            new HashMap<>();
    private LocaleUtilsBase mBase = new LocaleUtilsBase();
    private String mLanguage;
    private LocaleUtils() {
        setLocale(null);
    }

    public static synchronized LocaleUtils getIntance() {
        if (sSingleton == null) {
            sSingleton = new LocaleUtils();
        }
        return sSingleton;
    }

    private void setLocale(Locale currentLocale) {
        if (currentLocale == null) {
            mLanguage = Locale.getDefault().getLanguage().toLowerCase();
        } else {
            mLanguage = currentLocale.getLanguage().toLowerCase();
        }
    }

    public String getSortKey(String displayName) {
        int nameStyle = NameSplitter.guessFullNameStyle(displayName);
        return getForSort(nameStyle).getSortKey(displayName);
    }

    private synchronized LocaleUtilsBase get(Integer nameStyle) {
        LocaleUtilsBase utils = mUtils.get(nameStyle);
        if (utils == null) {
            if (nameStyle == NameStyle.PINYIN) {
                utils = new ChineseLocaleUtils();
                mUtils.put(nameStyle, utils);
            }
        }
        return (utils == null) ? mBase: utils;
    }

    /**
     *  Determine the which utility should be used for generating sort key.
     *  <p>
     *  For Chinese and CJK style name if current language is neither Japanese or Korean,
     *  the ChineseLocalesUtils should be used.
     */
    private LocaleUtilsBase getForSort(Integer nameStyle) {
        return get(getAdjustedStyle(nameStyle));
    }

    private int getAdjustedStyle(int nameStyle) {
        if (nameStyle == NameStyle.CJK  && !JAPANESE_LANGUAGE.equals(mLanguage) &&
                !KOREAN_LANGUAGE.equals(mLanguage)) {
            return NameStyle.PINYIN;
        } else {
            return nameStyle;
        }
    }

    /**
     * language of name
     */
    private interface NameStyle {
        int UNDEFINED = 0;
        int WESTERN = 1;

        /**
         * Used if the name is written in Hanzi/Kanji/Hanja and we could not determine
         * which specific language it belongs to: Chinese, Japanese or Korean.
         */
        int CJK = 2;

        int PINYIN = 3;
        int JAPANESE = 4;
        int KOREAN = 5;
    }

    private static class NameSplitter {

        public static int guessFullNameStyle(String name) {
            if (name == null) {
                return NameStyle.UNDEFINED;
            }

            int nameStyle = NameStyle.UNDEFINED;
            int length = name.length();
            int offset = 0;
            while (offset < length) {
                int codePoint = Character.codePointAt(name, offset);
                if (Character.isLetter(codePoint)) {
                    UnicodeBlock unicodeBlock = UnicodeBlock.of(codePoint);

                    if (!isLatinUnicodeBlock(unicodeBlock)) {

                        if (isCJKUnicodeBlock(unicodeBlock)) {
                            // We don't know if this is Chinese, Japanese or Korean -
                            // trying to figure out by looking at other characters in the name
                            return guessCJKNameStyle(name, offset + Character.charCount(codePoint));
                        }

                        if (isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                            return NameStyle.JAPANESE;
                        }

                        if (isKoreanUnicodeBlock(unicodeBlock)) {
                            return NameStyle.KOREAN;
                        }
                    }
                    nameStyle = NameStyle.WESTERN;
                }
                offset += Character.charCount(codePoint);
            }
            return nameStyle;
        }

        private static int guessCJKNameStyle(String name, int offset) {
            int length = name.length();
            while (offset < length) {
                int codePoint = Character.codePointAt(name, offset);
                if (Character.isLetter(codePoint)) {
                    UnicodeBlock unicodeBlock = UnicodeBlock.of(codePoint);
                    if (isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                        return NameStyle.JAPANESE;
                    }
                    if (isKoreanUnicodeBlock(unicodeBlock)) {
                        return NameStyle.KOREAN;
                    }
                }
                offset += Character.charCount(codePoint);
            }

            return NameStyle.CJK;
        }

        private static boolean isLatinUnicodeBlock(UnicodeBlock unicodeBlock) {
            return unicodeBlock == UnicodeBlock.BASIC_LATIN ||
                    unicodeBlock == UnicodeBlock.LATIN_1_SUPPLEMENT ||
                    unicodeBlock == UnicodeBlock.LATIN_EXTENDED_A ||
                    unicodeBlock == UnicodeBlock.LATIN_EXTENDED_B ||
                    unicodeBlock == UnicodeBlock.LATIN_EXTENDED_ADDITIONAL;
        }

        private static boolean isCJKUnicodeBlock(UnicodeBlock block) {
            return block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || block == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || block == UnicodeBlock.CJK_RADICALS_SUPPLEMENT
                    || block == UnicodeBlock.CJK_COMPATIBILITY
                    || block == UnicodeBlock.CJK_COMPATIBILITY_FORMS
                    || block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
        }

        private static boolean isKoreanUnicodeBlock(UnicodeBlock unicodeBlock) {
            return unicodeBlock == UnicodeBlock.HANGUL_SYLLABLES ||
                    unicodeBlock == UnicodeBlock.HANGUL_JAMO ||
                    unicodeBlock == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
        }

        private static boolean isJapanesePhoneticUnicodeBlock(UnicodeBlock unicodeBlock) {
            return unicodeBlock == UnicodeBlock.KATAKANA ||
                    unicodeBlock == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS ||
                    unicodeBlock == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                    unicodeBlock == UnicodeBlock.HIRAGANA;
        }
    }

    /**
     * This class is the default implementation.
     * <p/>
     * It should be the base class for other locales' implementation.
     */
    private class LocaleUtilsBase {
        public String getSortKey(String displayName) {
            return displayName;
        }
    }

    /**
     * The classes to generate the Chinese style sort and search keys.
     * <p/>
     * The sorting key is generated as each Chinese character' pinyin proceeding with
     * space and character itself. If the character's pinyin unable to find, the character
     * itself will be used.
     * <p/>
     * The below additional name lookup keys will be generated.
     * a. Chinese character's pinyin and pinyin's initial character.
     * b. Latin word and the initial character for Latin word.
     * The name lookup keys are generated to make sure the name can be found by from any
     * initial character.
     */
    private class ChineseLocaleUtils extends LocaleUtilsBase {
        @Override
        public String getSortKey(String displayName) {
            ArrayList<Token> tokens = HanziToPinyin.getInstance().get(displayName);
            if (tokens != null && tokens.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Token token : tokens) {
                    // Put Chinese character's pinyin, then proceed with the
                    // character itself.
                    if (Token.PINYIN == token.type) {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }

                        // 为了在排序时让同一组的中文在前，英文在后，插入一些空格，举例如下：
                        // 中文名是“李宇春”，对应的拼音是“LI YU CHUN”，对应的SortKey则是“L   I 李 Y   U 宇 C    HUN 春”
                        // 这样在跟英文进行比较时，永远出现在同首字母英文的前面，除非英文里面也有三个连续空格。
                        // 在Unicode里面空格（0020）是数值最小的可见字符，比较的时候会靠在最前，其它比它小的都不可见，不好调试，
                        // 所以用空格来做占位符比较合理，加三个空格是为了安全，不加更多是为了效率。
                        sb.append(token.target.charAt(0)); // 拼音的第一个字符
                        sb.append("   "); // 插入三个空格，确保在英文的前面
                        if (token.target.length() > 1) {
                            sb.append(token.target.substring(1)); // 拼音的剩余字符
                        }
                        sb.append(' ');
                        sb.append(token.source);
                    } else {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(token.source);
                    }
                }
                return sb.toString();
            }
            return super.getSortKey(displayName);
        }

    }

}
