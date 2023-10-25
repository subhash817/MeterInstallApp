package com.rjdev.meterinstallapp.activity;

import static java.util.stream.Collectors.toSet;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Arrays;
import java.util.Set;

public class Test {
    public static String arrayChallenge(String[] strArr) {

        String wordToSplit = strArr[0];
        Set<String> dictionaryWords = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dictionaryWords = Arrays.stream(strArr[1].split(",")).collect(toSet());
        }

        for (int i = wordToSplit.length() - 1; i > 0; i--) {
            String firstWord = wordToSplit.substring(0, i);
            String lastWord = wordToSplit.substring(i);

            if (dictionaryWords.contains(firstWord) && dictionaryWords.contains(lastWord)) {
                return firstWord + "," + lastWord;
            }
        }
        return "not possible";
    }

    public static void main(String[] args) {
        String[] arr = new String[] {"baseball", "a,all,b,ball,bas,base,cat,code,d,e,quit,z"};

        System.out.println(arrayChallenge(arr));
    }
}
