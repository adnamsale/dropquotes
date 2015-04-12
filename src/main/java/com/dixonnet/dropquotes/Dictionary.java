package com.dixonnet.dropquotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dictionary {
    public static enum DictSize {
        s,m,l;
    }

    public Dictionary(DictSize size) throws IOException {
        for (int i = 0 ; i < 20 ; ++i) {
            contents.add(new ArrayList<String>());
        }

        String dictName = null;
        switch (size) {
            case s: dictName = "wlist_giga_20k_nvp";
                break;
            case m: dictName = "wlist_giga_64k_nvp";
                break;
            case l: dictName = "words.txt";
                break;
        }
        InputStream is = getClass().getClassLoader().getResourceAsStream(dictName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String s = br.readLine();
        while (null != s) {
            s = s.toLowerCase();
            s = s.replace("'", "");
            if (0 < s.length() && s.length() < 20) {
                List<String> row = contents.get(s.length());
                if (row.isEmpty() || !row.get(row.size() - 1).equals(s)) {
                    row.add(s);
                }
            }
            s = br.readLine();
        }
        for (int i = 0 ; i < 20 ; ++i) {
            Collections.sort(contents.get(i));
        }
    }

    public boolean isPrefix(char[] candidate) {
        List<String> words = contents.get(candidate.length);
        String key = "";
        for (char c : candidate) {
            if (0 == c) {
                break;
            }
            key += Character.toLowerCase(c);
        }
        int idx = Collections.binarySearch(words, key);
        if (0 <= idx) {
            return true;
        }
        idx = -(idx + 1);
        return idx < words.size() && words.get(idx).startsWith(key);
    }

    private List<List<String>> contents = new ArrayList<List<String>>();
}
