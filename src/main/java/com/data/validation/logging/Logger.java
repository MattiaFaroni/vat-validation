package com.data.validation.logging;

import org.slf4j.LoggerFactory;

public class Logger {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    /**
     * Logs an error message with the provided alert and description.
     * @param alert a brief alert message describing the context of the error
     * @param message a detailed error description or additional information about the error
     */
    public static void printError(String alert, String message) {
        logger.error("{} Message: {}", alert, message);
    }

    /**
     * Logs an error message using the specified alert.
     * @param alert a brief alert message describing the error to be logged
     */
    public static void printError(String alert) {
        logger.error(alert);
    }

    /**
     * Logs an informational message with the provided alert and description.
     * @param alert a brief alert message describing the context or situation
     * @param message additional details or information to be logged
     */
    public static void printInfo(String alert, String message) {
        logger.info("{} Message: {}", alert, message);
    }

    /**
     * Logs an informational message using the specified alert.
     * @param alert the informational message to be logged
     */
    public static void printInfo(String alert) {
        logger.info(alert);
    }
}
