package org.tbank.fintech;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.tbank.fintech.model.City;
import org.tbank.fintech.util.CityMapper;

import java.io.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter the path to the file: ");
            String fileName = reader.readLine();
            File jsonFile = new File(fileName);
            if (jsonFile.exists()) {
                if (jsonFile.getName().endsWith(".json")) {
                    City city = CityMapper.parseJsonToCityObject(jsonFile);
                    String xml = CityMapper.convertCityObjectToXML(city);
                    @Cleanup
                    BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile.getName() + ".xml"));
                    writer.write(xml);
                } else {
                    throw new IOException("The input file is not json");
                }
            } else {
                throw new IOException("File was not found at the specified path");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
