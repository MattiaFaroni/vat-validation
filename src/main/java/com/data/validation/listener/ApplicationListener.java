package com.data.validation.listener;

import com.data.validation.config.Configuration;
import com.data.validation.config.Settings;
import com.data.validation.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class ApplicationListener extends Logger implements ServletContextListener {

    private static String version;
    public static Configuration configuration = new Configuration();

    public ApplicationListener() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("../build.properties");
        Properties props = new Properties();
        props.load(in);
        version = props.get("projectVersion").toString();

        Gson gson = new Gson();
        JsonObject jsonObject = Configuration.readJsonData("api-settings.json");
        configuration.setSettings(gson.fromJson(jsonObject, Settings.class));
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        createTomcatRunFile();

        printInfo("---------------------------------------------");
        printInfo("-- Start vat-validation API version " + version + " ---");
        printInfo("---------------------------------------------");
    }

    /**
     * Method used to create the TomcatRun.info file
     */
    private void createTomcatRunFile() {
        File tomcatRun = new File(System.getProperty("catalina.home") + "/" + "TomcatRun.info");
        try {
            tomcatRun.createNewFile();
        } catch (Exception e) {
            printError("Error creating tomcat run file: " + e.getMessage());
            System.exit(99);
        }
    }
}
