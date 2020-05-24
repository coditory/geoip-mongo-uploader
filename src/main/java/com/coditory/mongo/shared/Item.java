package com.coditory.mongo.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Item {
    private final Map<String, Object> fields;

    private Item(Map<String, Object> fields) {
        this.fields = Map.copyOf(fields);
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public Object get(String field) {
        return fields.get(field);
    }

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }

    public static class ItemBuilder {
        static private final Pattern INT_REGEX = Pattern.compile("^-?\\d+$");
        static private final Pattern FLOAT_REGEX = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");
        static private final Pattern BOOLEAN_REGEX = Pattern.compile("^[Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee]$");
        private final Map<String, Object> fields = new HashMap<>();

        private ItemBuilder() {
        }

        public ItemBuilder addAndParseValue(String key, String value) {
            String name = toFieldName(key);
            if (name == null) {
                throw new IllegalArgumentException("Expected non empty item field name");
            }
            if (value == null || value.isBlank()) {
                return this;
            }
            value = value.trim();
            if (INT_REGEX.matcher(value).matches()) {
                return add(name, Integer.parseInt(value));
            }
            if (FLOAT_REGEX.matcher(value).matches()) {
                return add(name, Float.parseFloat(value));
            }
            if (BOOLEAN_REGEX.matcher(value).matches()) {
                return add(name, Boolean.parseBoolean(value.toLowerCase()));
            }
            return add(name, value);
        }

        public ItemBuilder add(String key, String value) {
            fields.put(key, value);
            return this;
        }

        public ItemBuilder add(String key, int value) {
            fields.put(key, value);
            return this;
        }

        public ItemBuilder add(String key, float value) {
            fields.put(key, value);
            return this;
        }

        public ItemBuilder add(String key, double value) {
            fields.put(key, value);
            return this;
        }

        public ItemBuilder add(String key, boolean value) {
            fields.put(key, value);
            return this;
        }

        private String toFieldName(String phrase) {
            if (phrase == null || phrase.isBlank()) {
                return null;
            }
            String result = Arrays.stream(phrase.split("[-_ ]"))
                    .filter(chunk -> !chunk.isBlank())
                    .map(chunk -> Character.toUpperCase(chunk.charAt(0)) + chunk.substring(1))
                    .collect(Collectors.joining(""));
            result = result.replaceAll("[^a-zA-Z0-9]]", "");
            return result.substring(0, 1).toLowerCase() + result.substring(1);
        }

        public Item build() {
            return new Item(fields);
        }
    }
}
