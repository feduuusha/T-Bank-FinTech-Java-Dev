package org.tbank.fintech.lesson2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbank.fintech.lesson2.model.City;
import org.tbank.fintech.lesson2.model.Coordinates;
import org.tbank.fintech.lesson2.util.CityMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class CityMapperTest {

    @Test
    void parseCorrectJsonToCityObjectTest() throws Exception {
        // given
        File testFile = new File("src/test/test.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
              writer.write("{ \"slug\" : \"oren\", \"coords\" : { \"lat\" : 51.7727, \"lon\" : 55.0988 }}");
        }

        // when
        City city = CityMapper.parseJsonToCityObject(testFile);

        // then
        Assertions.assertEquals(new City("oren", new Coordinates(51.7727, 55.0988)), city);
    }

    @Test
    void parseIncorrectJsonToCityObjectTest() throws Exception {
        // given
        File testFile = new File("src/test/test.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
            writer.write("{ \"slug\" : \"kzn\", \"coords\" : { \"latify\" :  55.7887, \"lon\" : 49.1221 }}");
        }

        // when
        Assertions.assertThrows(IllegalStateException.class, () -> CityMapper.parseJsonToCityObject(testFile));
    }

    @Test
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
        Assertions.assertEquals(expectedXML, actualXML);
    }


    @AfterEach
    void deleteTestFile() {
        File testFile = new File("src/test/test.json");
        if (testFile.exists()) testFile.delete();
    }
}
