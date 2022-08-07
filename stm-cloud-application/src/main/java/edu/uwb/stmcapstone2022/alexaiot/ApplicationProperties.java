package edu.uwb.stmcapstone2022.alexaiot;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ApplicationProperties {
    public static Properties getProperties() {
        Properties properties = new Properties();
        fillProperties(properties, ResourceBundle.getBundle("application"));
        try {
            fillProperties(properties, ResourceBundle.getBundle("application-override"));
        } catch (MissingResourceException e) {
            System.err.println("Skipping missing resource bundle 'application-override'");
        }

        return properties;
    }

    private static void fillProperties(Properties properties, ResourceBundle bundle) {
        for(String key : bundle.keySet()) {
            properties.put(key, bundle.getString(key));
        }
    }
}
