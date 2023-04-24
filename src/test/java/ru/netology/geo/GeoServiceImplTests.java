package ru.netology.geo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import ru.netology.entity.Country;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoServiceImplTests {

    private static GeoServiceImpl sut = null;

    @BeforeAll
    public static void beforeAll() {
        sut = new GeoServiceImpl();
    }

    @AfterAll
    public static void afterAll() {
        sut = null;
    }

    @ParameterizedTest
    @NullSource
    public void testByIp_null_failure(String ipArg) {
        assertThrows(RuntimeException.class, () -> sut.byIp(ipArg));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"0.0.0.0", "255.255.255.255", "null", "randomtext56757"})
    public void testByIp_unknown_failure(String ipArg) {
        assertThat("location", sut.byIp(ipArg), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("testByIpSource")
    public void testByIp_success(String ipArg, Country expected) {
        assertThat("location country", sut.byIp(ipArg).getCountry(), is(equalTo(expected)));
    }

    public static Stream<Arguments> testByIpSource() {
        return Stream.of(
                Arguments.of("96.0.0.1", Country.USA),
                Arguments.of("127.0.0.1", null),
                Arguments.of("172.0.0.1", Country.RUSSIA),
                Arguments.of(GeoServiceImpl.LOCALHOST, null),
                Arguments.of(GeoServiceImpl.NEW_YORK_IP, Country.USA),
                Arguments.of(GeoServiceImpl.MOSCOW_IP, Country.RUSSIA)
        );
    }

}