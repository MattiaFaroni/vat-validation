package com.data.validation.config;

import com.data.validation.config.data.Parameters;
import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Configuration extends Logger {

    private Parameters parameters;

    /**
     * Reads a JSON configuration file and parses its content into a JsonObject.
     * @param fileName the name of the JSON configuration file to be read
     * @return a JsonObject representation of the file content
     */
    // spotless:off
    public static JsonObject readJsonData(String fileName) {
        try {
            InputStream jsonConfig = searchFileOnTomcat(fileName);
            if (jsonConfig == null) {
                jsonConfig = searchFileOnResources(fileName);
            }
            if (jsonConfig != null) {
                return (JsonObject) JsonParser.parseReader(new InputStreamReader(jsonConfig, StandardCharsets.UTF_8));
            } else {
                return null;
            }
        } catch (Exception e) {
            printError("Error reading configuration json file", e.getMessage());
            return null;
        }
    }
    // spotless:on

    /**
     * Searches for a configuration file within the Tomcat configuration directory and returns an InputStream for the file content.
     * @param fileName the name of the configuration file to search
     * @return an InputStream for the file if found
     */
    private static InputStream searchFileOnTomcat(String fileName) {
        try {
            File configDir = new File(System.getProperty("catalina.home"), "config");
            File configFile = new File(configDir, fileName);
            return new FileInputStream(configFile);
        } catch (Exception e) {
            printInfo("Config file not found in tomcat", e.getMessage());
            return null;
        }
    }

    /**
     * Searches for a file in the application's resources directory and returns an InputStream to its content.
     * @param fileName the name of the file to search in the resources directory
     * @return an InputStream of the file content if found
     */
    private static InputStream searchFileOnResources(String fileName) {
        try {
            return ApplicationListener.class.getClassLoader().getResourceAsStream(fileName);
        } catch (Exception e) {
            printInfo("Config file not found in resources", e.getMessage());
            return null;
        }
    }
}
