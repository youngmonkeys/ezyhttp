package com.tvd12.ezyhttp.core.test.data;

import com.tvd12.ezyhttp.core.data.BytesRange;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class BytesRangeTest {

    @Test
    public void fromTest() {
        // given
        final String fromTo = "bytes = 1-";

        // when
        final BytesRange actual = new BytesRange(fromTo);

        // then
        Asserts.assertEquals(actual.getFrom(), 1L);
        Asserts.assertEquals(actual.getTo(), 0L);
    }

    @Test
    public void fromNoDashTest() {
        // given
        final String fromTo = "bytes = 1";

        // when
        final BytesRange actual = new BytesRange(fromTo);

        // then
        Asserts.assertEquals(actual.getFrom(), 1L);
        Asserts.assertEquals(actual.getTo(), 0L);
    }

    @Test
    public void fromNoBytesPrefixTest() {
        // given
        final String fromTo = "1";

        // when
        final BytesRange actual = new BytesRange(fromTo);

        // then
        Asserts.assertEquals(actual.getFrom(), 1L);
        Asserts.assertEquals(actual.getTo(), 0L);
    }

    @Test
    public void fromAndToTest() {
        // given
        final String fromTo = "bytes = 1-2";

        // when
        final BytesRange actual = new BytesRange(fromTo);

        // then
        Asserts.assertEquals(actual.getFrom(), 1L);
        Asserts.assertEquals(actual.getTo(), 2L);
    }

    @Test
    public void fromAndToWithoutEqualsTest() {
        // given
        final String fromTo = "bytes : 1-2";

        // when
        final BytesRange actual = new BytesRange(fromTo);

        // then
        Asserts.assertEquals(actual.getFrom(), 1L);
        Asserts.assertEquals(actual.getTo(), 2L);
    }

    @Test
    public void fromAndToWithBytesInvalidTest() {
        // given
        final String fromTo = "bytes: a - b";

        // when
        final Throwable e = Asserts.assertThrows(() ->
            new BytesRange(fromTo)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }
}
