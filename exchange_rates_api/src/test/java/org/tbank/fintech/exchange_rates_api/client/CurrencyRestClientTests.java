package org.tbank.fintech.exchange_rates_api.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.tbank.fintech.exchange_rates_api.config.ClientConfig;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/**
 * Test class for the {@link CurrencyRestClient}
 */
@SpringBootTest(classes = {ClientConfig.class}, webEnvironment = NONE)
@Testcontainers(disabledWithoutDocker = true)
public class CurrencyRestClientTests {

    @Autowired
    private CurrencyRestClient restClient;

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource(CurrencyRestClientTests.class, "mocks-config.json");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("clients.currency.url", wireMockContainer::getBaseUrl);
    }


    @Test
    @DisplayName("Calling the findAllCurrenciesXml method should return the correct xml because the call is successful")
    public void findAllCurrenciesXmlTest() {
        // Arrange
        LocalDate testDate = LocalDate.of(2005, 9, 26);
        String expectedXml = """
                <ValCurs Date="26.09.2005" name="Foreign Currency Market">
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
                                <Valute ID="R01090">
                                <NumCode>974</NumCode>
                                <CharCode>BYR</CharCode>
                                <Nominal>1000</Nominal>
                                <Name>Белорусских рублей</Name>
                                <Value>13,2242</Value>
                                <VunitRate>0,0132242</VunitRate>
                                </Valute>
                                </ValCurs>""";

        // Act
        String actualXml = this.restClient.findAllCurrenciesXml(testDate);

        // Assert
        assertThat(actualXml)
                .as(() -> "Mock server produce " + expectedXml + ", but rest client return " + actualXml)
                .isEqualTo(expectedXml);
    }

    @Test
    @DisplayName("Calling the findAllCurrenciesCodesXml method should return the correct xml because the call is successful")
    public void findAllCurrenciesCodesXmlTest() {
        // Arrange
        String expectedXml = """
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
                </Item></Valuta>""";

        // Act
        String actualXml = this.restClient.findAllCurrenciesCodesXml();

        // Assert
        assertThat(actualXml)
                .as(() -> "Mock server produce " + expectedXml + ", but rest client return " + actualXml)
                .isEqualTo(expectedXml);
    }

    @Test
    @DisplayName("Calling the findAllCurrenciesXml method should throw UnavailableServiceException because service answer is 404")
    public void findAllCurrenciesXmlShouldThrowUnavailableServiceExceptionTest() {
        // Arrange
        LocalDate testDate = LocalDate.of(2007, 7, 7);

        // Act
        // Assert
        assertThatExceptionOfType(UnavailableServiceException.class)
                .as(() -> "Mock server produce 404 code, but rest client dont throw UnavailableServiceException")
                .isThrownBy(() -> this.restClient.findAllCurrenciesXml(testDate))
                .withMessage("Central Bank service is unavailable: 404 Not Found: [no body]");
    }

}
