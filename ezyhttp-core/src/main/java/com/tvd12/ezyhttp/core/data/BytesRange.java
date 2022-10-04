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
            Long.parseLong(fromTo[0].trim()),
            fromTo.length == 1 ? 0 : Long.parseLong(fromTo[1].trim())
        );
    }

    private static String[] extractRange(String range) {
        final int equalsIndex = range.indexOf('=');
        String actualRange = range;
        if (equalsIndex >= 0) {
            actualRange = range
                .substring(equalsIndex + 1)
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
