package com.tvd12.ezyhttp.core.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BytesRange {
    private final long from;
    private final long to;

    public BytesRange(String range) {
        this(extractRange(range));
    }

    public BytesRange(String[] fromTo) {
        this(
            toBytesOrThrow(fromTo[0]),
            fromTo.length == 1 ? 0 : toBytesOrThrow(fromTo[1])
        );
    }

    public static long toBytesOrThrow(String value) {
        long answer;
        try {
            answer = Long.parseLong(value.trim());
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        if (answer < 0) {
            throw new IllegalArgumentException(
                "byte range can not accept nagative (" + answer + ") value"
            );
        }
        return answer;
    }

    private static String[] extractRange(String range) {
        int splitIndex = range.indexOf('=');
        if (splitIndex < 0) {
            splitIndex = range.indexOf(':');
        }
        String actualRange = range;
        if (splitIndex >= 0) {
            actualRange = range
                .substring(splitIndex + 1)
                .trim();
        }
        final int dashIndex = actualRange.lastIndexOf('-');
        if (dashIndex <= 0) {
            return new String[] { actualRange };
        }
        if (dashIndex >= actualRange.length() - 1) {
            return new String[] {
                actualRange.substring(0, dashIndex)
            };
        }
        return new String[] {
            actualRange.substring(0, dashIndex).trim(),
            actualRange.substring(dashIndex + 1).trim()
        };
    }
}
