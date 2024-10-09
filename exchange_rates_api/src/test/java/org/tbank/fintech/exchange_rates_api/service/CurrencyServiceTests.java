package org.tbank.fintech.exchange_rates_api.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.tbank.fintech.exchange_rates_api.client.CurrencyRestClient;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.model.Currency;
import org.tbank.fintech.exchange_rates_api.model.response.Conversion;
import org.tbank.fintech.exchange_rates_api.model.response.CurrencyRate;
import org.tbank.fintech.exchange_rates_api.service.impl.CurrencyServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = CurrencyServiceImpl.class)
public class CurrencyServiceTests {

    @Autowired
    private CurrencyService currencyService;

    @MockBean
    private MapperService mapperService;

    @MockBean
    private CurrencyRestClient currencyRestClient;

    @Test
    @DisplayName("Method findRatesOfCode should return CurrencyRate, because currency code is correct")
    public void findRatesOfCodeSuccessfulTest() {
        // Arrange
        String currencyCode = "EUR";
        Double currencyRate = 34.5022;
        LocalDate localDate = LocalDate.now();
        String currencyXml = """
                <ValCurs Date="24.09.2005" name="Foreign Currency Market">
                <Valute ID="R01239">
                <NumCode>978</NumCode>
                <CharCode>EUR</CharCode>
                <Nominal>1</Nominal>
                <Name>Евро</Name>
                <Value>34,5022</Value>
                <VunitRate>34,5022</VunitRate>
                </Valute>
                </ValCurs>""";
        String currencyCodesXml = """
                <Valuta name="Foreign Currency Market Lib">
                <Item ID="R01239">
                <Name>Евро</Name>
                <EngName>Euro</EngName>
                <Nominal>1</Nominal>
                <ParentCode>R01239</ParentCode>
                <ISO_Num_Code>978</ISO_Num_Code>
                <ISO_Char_Code>EUR</ISO_Char_Code>
                </Item>
                </Valuta>""";
        when(currencyRestClient.findAllCurrenciesXml(localDate)).thenReturn(currencyXml);
        when(currencyRestClient.findAllCurrenciesCodesXml()).thenReturn(currencyCodesXml);
        when(mapperService.mapXmlToCodes(currencyCodesXml)).thenReturn(List.of("EUR"));
        when(mapperService.mapXmlToCurrencies(currencyXml)).thenReturn(List.of(new Currency(currencyCode, currencyRate)));

        // Act
        CurrencyRate rate = currencyService.findRatesOfCode(currencyCode);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(rate.rate()).isEqualTo(currencyRate);
        softly.assertThat(rate.currency()).isEqualTo(currencyCode);

        softly.assertAll();
    }

    @Test
    @DisplayName("Method findRatesOfCode(RUB) should return {currency: RUB, rate: 1}")
    public void findRatesOfCodeSuccessfulWithCodeRUB() {
        // Arrange
        // Act
        CurrencyRate currencyRate = currencyService.findRatesOfCode("RUB");

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currencyRate.currency()).isEqualTo("RUB");
        softly.assertThat(currencyRate.rate()).isEqualTo(1D);

        softly.assertAll();
    }

    @Test
    @DisplayName("Method findRatesOfCode should throw NoSuchElementException, because currency code is dont exist")
    public void findRatesOfCodeUnsuccessfulCurrencyCodeDontExistTest() {
        // Arrange
        String currencyCode = "ATS";
        LocalDate localDate = LocalDate.now();
        when(currencyRestClient.findAllCurrenciesXml(localDate)).thenReturn("{currencyXml}");
        when(currencyRestClient.findAllCurrenciesCodesXml()).thenReturn("{currencyCodesXml}");
        when(mapperService.mapXmlToCodes("{currencyCodesXml}")).thenReturn(List.of(currencyCode));
        when(mapperService.mapXmlToCurrencies("{currencyXml}")).thenReturn(List.of());

        // Act
        // Assert
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> currencyService.findRatesOfCode(currencyCode))
                .withMessage(currencyCode + " code is not included in the current currency quotes");

    }

    @Test
    @DisplayName("Method findRatesOfCode should throw BadRequestException, because currency code is incorrect")
    public void findRatesOfCodeUnsuccessfulIncorrectCurrencyCodeTest() {
        // Arrange
        String currencyCode = "LOL";
        LocalDate localDate = LocalDate.now();
        when(currencyRestClient.findAllCurrenciesXml(localDate)).thenReturn("{currencyXml}");
        when(currencyRestClient.findAllCurrenciesCodesXml()).thenReturn("{currencyCodesXml}");
        when(mapperService.mapXmlToCodes("{currencyCodesXml}")).thenReturn(List.of());
        when(mapperService.mapXmlToCurrencies("{currencyXml}")).thenReturn(List.of());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> currencyService.findRatesOfCode(currencyCode))
                .withMessage("Incorrect currency code: " + currencyCode);

    }

    @Test
    @DisplayName("Method convertCurrency should return Conversion, because currency codes is correct")
    public void convertCurrencySuccessfulTest() {
        // Arrange
        String currencyCode1 = "EUR";
        String currencyCode2 = "USD";
        Double amount = 100.5;
        Currency currency1 = new Currency(currencyCode1, 50.5);
        Currency currency2 = new Currency(currencyCode2, 45.5);
        LocalDate localDate = LocalDate.now();
        when(currencyRestClient.findAllCurrenciesXml(localDate)).thenReturn("{currencyXml}");
        when(currencyRestClient.findAllCurrenciesCodesXml()).thenReturn("{currencyCodesXml}");
        when(mapperService.mapXmlToCodes("{currencyCodesXml}")).thenReturn(List.of("USD", "EUR"));
        when(mapperService.mapXmlToCurrencies("{currencyXml}")).thenReturn(List.of(currency1, currency2));

        // Act
        Conversion conversion = currencyService.convertCurrency(currencyCode1, currencyCode2, amount);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(conversion.fromCurrency()).isEqualTo(currencyCode1);
        softly.assertThat(conversion.toCurrency()).isEqualTo(currencyCode2);
        softly.assertThat(conversion.convertedAmount()).isEqualTo(amount * currency1.unitRate() / currency2.unitRate());

        softly.assertAll();
    }

}
