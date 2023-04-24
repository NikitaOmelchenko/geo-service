package ru.netology.i18n;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import ru.netology.entity.Country;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalizationServiceImplTests {

    private static LocalizationServiceImpl sut = null;

    private static String expectedRussianText = "Добро пожаловать";
    private static String expectedEnglishText = "Welcome";

    @BeforeAll
    public static void beforeAll() {
        sut = new LocalizationServiceImpl();
    }

    @AfterAll
    public static void afterAll() {
        sut = null;
    }

    @ParameterizedTest
    @NullSource
    public void testLocale_null_failure(Country country) {
        assertThrows(RuntimeException.class, () -> sut.locale(country));
    }

    @ParameterizedTest
    @MethodSource("testLocaleSource")
    public void testLocale_success(Country country, String expectedString) {
        assertThat("localized message", sut.locale(country), equalTo(expectedString));
    }

    private static Stream<Arguments> testLocaleSource() {
        return Stream.of(
                Arguments.of(Country.USA, expectedEnglishText),
                Arguments.of(Country.BRAZIL, expectedEnglishText),
                Arguments.of(Country.GERMANY, expectedEnglishText),
                Arguments.of(Country.RUSSIA, expectedRussianText)
        );
    }

}