package org.tbank.fintech.lesson2.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.tbank.fintech.lesson2.model.City;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class CityMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    public static City parseJsonToCityObject(File json) {
        try {
            log.info("Start parseJsonToCityObject method with " + json.getName() + " argument");
            return objectMapper.readValue(json, City.class);
        } catch (IOException e) {
            log.error("Error while parsing to JSON", e);
            throw new IllegalStateException(e);
        }
    }


    public static String convertCityObjectToXML(City city) {
        try {
            log.info("Start convertCityObjectToXML method with " + city + " argument");
            return xmlMapper.writeValueAsString(city);
        } catch (IOException e) {
            log.error("Error while converting to XML", e);
            throw new IllegalStateException(e);
        }
    }

}
