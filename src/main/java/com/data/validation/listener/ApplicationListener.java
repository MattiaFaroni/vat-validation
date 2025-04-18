package com.data.validation.listener;

import com.data.validation.config.Configuration;
import com.data.validation.config.data.Parameters;
import com.data.validation.logging.Logger;
import com.data.validation.redis.RedisCacheManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
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
    private static String SENTRY_DSN;
    public static Configuration configuration = new Configuration();
    public static RedisCacheManager cacheManager;

    // spotless:off
    public ApplicationListener() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("../build.properties");
        Properties props = new Properties();
        props.load(in);
        version = props.get("projectVersion").toString();

        Gson gson = new Gson();
        JsonObject jsonObject = Configuration.readJsonData("api-settings.json");
        configuration.setParameters(gson.fromJson(jsonObject, Parameters.class));

        cacheManager = new RedisCacheManager(configuration.getParameters().getRedis().getHost(), configuration.getParameters().getRedis().getPort());

        if (configuration.getParameters().getSentry().getDsn() != null && !configuration.getParameters().getSentry().getDsn().isEmpty()) {
            Sentry.init(options -> {
                SENTRY_DSN = configuration.getParameters().getSentry().getDsn();
                options.setDsn(SENTRY_DSN);
                options.setTracesSampleRate(1.0);
            });
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        createTomcatRunFile();

        printInfo("---------------------------------------------");
        printInfo("-- Start vat-validation API version " + version + " ---");
        printInfo("---------------------------------------------");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        cacheManager.close();
    }

    /**
     * Creates a new file named "TomcatRun.info" in the directory specified by the "catalina.home".
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
    // spotless:on
}
