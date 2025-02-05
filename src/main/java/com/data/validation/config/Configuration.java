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
     * Method used to read the configuration json
     * @param fileName json file name
     * @return information extracted from the configuration file
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
     * Method used to search for configuration file in tomcat
     * @param fileName json file name
     * @return information extracted from the configuration file
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
     * Method used to search for configuration file in resources
     * @param fileName json file name
     * @return information extracted from the configuration file
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
