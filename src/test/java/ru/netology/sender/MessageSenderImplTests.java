package ru.netology.sender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.i18n.LocalizationService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;

class MessageSenderImplTests {

    private GeoService geoServiceMock;
    private LocalizationService localizationServiceMock;
    private MessageSenderImpl sut;

    private static final String TEST_IP = "254.254.254.254";
    private static final Country TEST_COUNTRY = Country.USA;
    private static final String TEST_MESSAGE = "test message";

    @BeforeEach
    public void setUp() {
        geoServiceMock = Mockito.mock(GeoService.class);
        localizationServiceMock = Mockito.mock(LocalizationService.class);
        sut = new MessageSenderImpl(geoServiceMock, localizationServiceMock);
    }

    @AfterEach
    public void tearDown() {
        sut = null;
        geoServiceMock = null;
        localizationServiceMock = null;
    }

    @Test
    public void testSend_calls_GeoService() {
        Location locationMock = Mockito.mock(Location.class);
        Mockito.when(locationMock.getCountry()).thenReturn(TEST_COUNTRY);

        Mockito.when(geoServiceMock.byIp(anyString())).thenReturn(locationMock);
        Mockito.when(localizationServiceMock.locale(any(Country.class))).thenReturn(TEST_MESSAGE);

        Map<String, String> headers = Map.of(MessageSenderImpl.IP_ADDRESS_HEADER, TEST_IP);
        sut.send(headers);

        Mockito.verify(geoServiceMock, atLeastOnce()).byIp(TEST_IP);
    }

    @Test
    public void testSend_calls_LocalizationService() {
        Location locationMock = Mockito.mock(Location.class);
        Mockito.when(locationMock.getCountry()).thenReturn(TEST_COUNTRY);

        Mockito.when(geoServiceMock.byIp(anyString())).thenReturn(locationMock);
        Mockito.when(localizationServiceMock.locale(any(Country.class))).thenReturn(TEST_MESSAGE);

        Map<String, String> headers = Map.of(MessageSenderImpl.IP_ADDRESS_HEADER, TEST_IP);
        sut.send(headers);

        Mockito.verify(localizationServiceMock, atLeastOnce()).locale(TEST_COUNTRY);
    }

    @ParameterizedTest
    @EmptySource
    public void testSend_empty_defaultUsa_success(String ip) {
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        Mockito.when(localizationServiceMock.locale(Country.USA)).thenReturn(TEST_MESSAGE);

        assertThat("result message", sut.send(headers), equalTo(TEST_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("ipCountryMessageSource")
    public void testSend_success(String ipArg, Country country, String expectedText) {
        Location locationMock = Mockito.mock(Location.class);
        Mockito.when(locationMock.getCountry()).thenReturn(country);

        Mockito.when(geoServiceMock.byIp(ipArg)).thenReturn(locationMock);
        Mockito.when(localizationServiceMock.locale(country)).thenReturn(expectedText);

        Map<String, String> headers = Map.of(MessageSenderImpl.IP_ADDRESS_HEADER, ipArg);

        assertThat("result message", sut.send(headers), equalTo(expectedText));
    }

    public static Stream<Arguments> ipCountryMessageSource() {
        return Stream.of(
                Arguments.of("96.0.0.0", Country.USA, "english"),
                Arguments.of("172.0.0.0", Country.RUSSIA, "russian"),
                Arguments.of("46.95.0.0", Country.GERMANY, "german"),
                Arguments.of("177.70.143.0", Country.BRAZIL, "portuguese"),
                Arguments.of(TEST_IP, TEST_COUNTRY, "test")
        );
    }

}