package org.tbank.fintech.exchange_rates_api.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tbank.fintech.exchange_rates_api.config.MapperConfig;
import org.tbank.fintech.exchange_rates_api.model.Currency;
import org.tbank.fintech.exchange_rates_api.service.impl.MapperServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {MapperConfig.class, MapperServiceImpl.class})
public class MapperServiceTests {

    @Autowired
    private MapperService mapperService;

    @Test
    @DisplayName("Method mapXmlToCurrencies should return List<Currency>, because Xml is correct")
    public void mapXmlToCurrenciesSuccessfulTest() {
        // Arrange
        String xml = """
                <ValCurs Date="24.09.2005" name="Foreign Currency Market">
                <Valute ID="R01010">
                <NumCode>036</NumCode>
                <CharCode>AUD</CharCode>
                <Nominal>1</Nominal>
                <Name>Австралийский доллар</Name>
                <Value>21,6225</Value>
                <VunitRate>21,6225</VunitRate>
                </Valute>
                <Valute ID="R01035">
                <NumCode>826</NumCode>
                <CharCode>GBP</CharCode>
                <Nominal>1</Nominal>
                <Name>Фунт стерлингов Соединенного королевства</Name>
                <Value>50,9047</Value>
                <VunitRate>50,9047</VunitRate>
                </Valute>
                </ValCurs>""";
        List<Currency> expectedCurrencies = List.of(
                new Currency("AUD", 21.6225),
                new Currency("GBP", 50.9047)
        );

        // Act
        List<Currency> currencies = mapperService.mapXmlToCurrencies(xml);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(currencies.size()).isEqualTo(expectedCurrencies.size());
        for (int i = 0; i < expectedCurrencies.size(); ++i) {
            softly.assertThat(currencies.get(i)).isEqualTo(expectedCurrencies.get(i));
        }

        softly.assertAll();
    }

    @Test
    @DisplayName("Method mapXmlToCurrencies should throw IllegalStateException, because Xml is incorrect")
    public void mapXmlToCurrenciesUnsuccessfulXmlIsIncorrectTest() {
        // Arrange
        String xml = """
                <ValCurs Date="24.09.2005" name="Foreign Currency Market">
                <Valute ID="R01010">
                <NumCode>036</NumCode>
                <CharCode>AUD</CharCode>
                """;

        // Act
        // Assert
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> mapperService.mapXmlToCurrencies(xml))
                .withMessage("External service has changed its API");
    }

    @Test
    @DisplayName("Method mapXmlToCodes should return List<String>, because Xml is correct")
    public void mapXmlToCodesSuccessfulTest() {
        // Arrange
        String xml = """
                <Valuta name="Foreign Currency Market Lib">
                <Item ID="R01010">
                <Name>Австралийский доллар</Name>
                <EngName>Australian Dollar</EngName>
                <Nominal>1</Nominal>
                <ParentCode>R01010</ParentCode>
                <ISO_Num_Code>36</ISO_Num_Code>
                <ISO_Char_Code>AUD</ISO_Char_Code>
                </Item>
                <Item ID="R01015">
                <Name>Австрийский шиллинг</Name>
                <EngName>Austrian Shilling</EngName>
                <Nominal>1000</Nominal>
                <ParentCode>R01015</ParentCode>
                <ISO_Num_Code>40</ISO_Num_Code>
                <ISO_Char_Code>ATS</ISO_Char_Code>
                </Item>
                </Valuta>
                """;
        List<String> expectedCodes = List.of("AUD", "ATS");

        // Act
        List<String> codes = mapperService.mapXmlToCodes(xml);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(codes.size()).isEqualTo(expectedCodes.size());
        for (int i = 0; i < codes.size(); ++i) {
            softly.assertThat(codes.get(i)).isEqualTo(expectedCodes.get(i));
        }

        softly.assertAll();
    }

    @Test
    @DisplayName("Method mapXmlToCodes should throw IllegalStateException, because Xml is incorrect")
    public void mapXmlToCodesUnsuccessfulXmlIsIncorrectTest() {
        // Arrange
        String xml = """
                <ISO_Num_Code>36</ISO_Num_Code>
                <ISO_Char_Code>AUD</ISO_Char_Code>
                </Item>
                <Item ID="R01015">
                <Name>Австрийский шиллинг</Name>
                <EngName>Austrian Shilling</EngName>
                <Nominal>1000</Nominal>
                """;

        // Act
        // Assert
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> mapperService.mapXmlToCodes(xml))
                .withMessage("External service has changed its API");
    }
}
