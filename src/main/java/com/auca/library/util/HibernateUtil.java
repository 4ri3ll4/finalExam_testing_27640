package com.auca.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.auca.library.domain.Location;

public class HibernateUtil {

    public static SessionFactory buildSessionFactory(String propertiesFile) {
        try (InputStream input = HibernateUtil.class.getClassLoader()
                .getResourceAsStream(propertiesFile)) {

            if (input == null) {
                throw new IllegalArgumentException(propertiesFile + " not found on the classpath");
            }

            Properties properties = new Properties();
            properties.load(input);

            return new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(Location.class)
                    // more .addAnnotatedClass(...) lines get added here as we
                    // build each new entity — User, Borrower, Book, etc.
                    .buildSessionFactory();

        } catch (IOException e) {
            throw new RuntimeException("Could not read " + propertiesFile, e);
        }
    }
}