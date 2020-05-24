package com.coditory.mongo.shared;

import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Value
public class Arguments {
    public static Arguments parse(String[] args) {
        return new Arguments(List.of(args).stream()
            .filter(a -> a != null && !a.isBlank())
            .collect(toList()));
    }

    List<String> args;

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public boolean contains(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Expected non empty values");
        }
        return Arrays.stream(values)
            .anyMatch(args::contains);
    }

    public String getRequiredStringArgument(String... names) {
        if (names == null || names.length == 0) {
            throw new IllegalArgumentException("Expected non empty names");
        }
        return getOptionalStringArgument(names)
            .orElseThrow(() -> new ArgumentsException("Missing required argument: " + names[0]));
    }

    public Optional<Integer> getOptionalIntArgument(String... names) {
        return getOptionalStringArgument(names)
            .map(Integer::parseInt);
    }

    public Optional<String> getOptionalStringArgument(String... names) {
        if (names == null || names.length == 0) {
            throw new IllegalArgumentException("Expected non empty names");
        }
        Set<String> list = Set.of(names);
        int i = 0;
        while (i < args.size() - 1 && !list.contains(args.get(i))) {
            ++i;
        }
        if (i < args.size() - 1) {
            String value = args.get(i + 1);
            return value.startsWith("-")
                ? Optional.empty()
                : Optional.of(value);
        }
        return Optional.empty();
    }

    public static class ArgumentsException extends RuntimeException {
        public ArgumentsException(String message) {
            super(message);
        }
    }
}
