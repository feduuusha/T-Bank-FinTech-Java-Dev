package org.tbank.fintech.lesson2;

import org.tbank.fintech.lesson2.model.City;
import org.tbank.fintech.lesson2.util.CityMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter the path to the file: ");
            String fileName = reader.readLine();
            if (!fileName.endsWith(".json")) {
                throw new IOException("The input file is not json");
            }
            File jsonFile = new File(fileName);
            if (jsonFile.exists()) {
                City city = CityMapper.parseJsonToCityObject(jsonFile);
                String xml = CityMapper.convertCityObjectToXML(city);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.getName() + ".xml"))) {
                    writer.write(xml);
                }
            } else {
                throw new IOException("File was not found at the specified path");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
