package com.coditory.mongo.reader;

import java.util.ArrayList;
import java.util.List;

class CsvParser {
    private static final char SEPARATOR = ',';
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';

    static List<String> parseCsvRow(String row) {
        List<String> result = new ArrayList<>();
        boolean isInQuote = false;
        boolean afterSecondQuote = false;
        char activeQuote = DOUBLE_QUOTE;
        StringBuilder builder = new StringBuilder();
        char[] chars = row.toCharArray();
        for (char c : chars) {
            if (isInQuote) {
                if (c == activeQuote) {
                    isInQuote = false;
                    afterSecondQuote = true;
                    result.add(builder.toString());
                    builder = new StringBuilder();
                } else {
                    builder.append(c);
                }
            } else if (c == SEPARATOR) {
                if (afterSecondQuote) {
                    afterSecondQuote = false;
                } else {
                    result.add(builder.toString().trim());
                    builder = new StringBuilder();
                }
            } else if (builder.length() == 0 && !afterSecondQuote) {
                if (c == DOUBLE_QUOTE) {
                    activeQuote = DOUBLE_QUOTE;
                    isInQuote = true;
                } else if (c == SINGLE_QUOTE) {
                    activeQuote = SINGLE_QUOTE;
                    isInQuote = true;
                } else if (c != ' ' && c != '\t') {
                    builder.append(c);
                }
            } else if (!afterSecondQuote) {
                builder.append(c);
            }
        }
        if (!afterSecondQuote) {
            result.add(builder.toString().trim());
        }
        return result;
    }
}
