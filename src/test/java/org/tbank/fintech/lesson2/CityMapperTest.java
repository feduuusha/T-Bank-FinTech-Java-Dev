package org.tbank.fintech.lesson2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tbank.fintech.lesson2.model.City;
import org.tbank.fintech.lesson2.model.Coordinates;
import org.tbank.fintech.lesson2.util.CityMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class CityMapperTest {

    @Test
    @DisplayName("Checking the correctness of parsing correct json to city object")
    void parseCorrectJsonToCityObjectTest() throws Exception {
        // given
        File testFile = new File("src/test/test.json");
        String jsonString = "{ \"slug\" : \"oren\", \"coords\" : { \"lat\" : 51.7727, \"lon\" : 55.0988 }}";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
              writer.write(jsonString);
        }

        // when
        City city = CityMapper.parseJsonToCityObject(testFile);

        // then
        assertThat(city).isEqualTo(new City("oren", new Coordinates(51.7727, 55.0988))).as(() -> "correct json: " + jsonString + " was incorrectly converted to a city: " + city);
    }

    @Test
    @DisplayName("Checking that a parsing incorrect json will throw IllegalStateException")
    void parseIncorrectJsonToCityObjectTest() throws Exception {
        // given
        File testFile = new File("src/test/test.json");
        String jsonString = "{ \"slug\" : \"kzn\", \"coords\" : { \"latify\" :  55.7887, \"lon\" : 49.1221 }}";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
            writer.write(jsonString);
        }

        // when
        assertThatIllegalStateException().isThrownBy(() -> CityMapper.parseJsonToCityObject(testFile)).as(() -> "CityMapper.parseJsonToCityObject with incorrect json: " + jsonString + " doesn't throw IllegalStateException");
    }

    @Test
    @DisplayName("Checking the correctness of converting city object to xml string")
    void convertCityObjectToXML() {
        // given
        City testCity = new City("oren", new Coordinates(51.7727, 55.0988));

        // when
        String actualXML = CityMapper.convertCityObjectToXML(testCity);

        // then
        String expectedXML =
                "<City>" +
                    "<slug>oren</slug>" +
                    "<coords>" +
                        "<lat>51.7727</lat>" +
                        "<lon>55.0988</lon>" +
                    "</coords>" +
                "</City>";
        assertThat(actualXML).isEqualTo(expectedXML).as(() -> "city object: " + testCity + " incorrect covert to xml: " + actualXML);
    }


    @AfterEach
    void deleteTestFile() {
        File testFile = new File("src/test/test.json");
        if (testFile.exists()) testFile.delete();
    }
}
