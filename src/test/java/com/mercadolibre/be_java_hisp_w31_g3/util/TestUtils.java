package com.mercadolibre.be_java_hisp_w31_g3.util;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Comparator;
import java.util.stream.Stream;

public class TestUtils {
    public static Stream<Arguments> getCorrectDateOrders() {
        return Stream.of(
                Arguments.of("date_asc",  Comparator.naturalOrder()),
                Arguments.of("date_desc", Comparator.reverseOrder())
        );
    }

    public static Stream<Arguments> getWrongDateOrders() {
        return Stream.of(
                Arguments.of("date_asc",  Comparator.reverseOrder()),
                Arguments.of("date_desc", Comparator.naturalOrder())
        );
    }
}
