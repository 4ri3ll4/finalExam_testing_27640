package com.auca.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.auca.library.domain.Book;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Location;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import com.auca.library.domain.User;

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
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(MembershipType.class)
                    .addAnnotatedClass(Membership.class)
                    .addAnnotatedClass(Room.class)
                    .addAnnotatedClass(Shelf.class)
                    .addAnnotatedClass(Book.class)
                    .addAnnotatedClass(Borrower.class)
                    .buildSessionFactory();

        } catch (IOException e) {
            throw new RuntimeException("Could not read " + propertiesFile, e);
        }
    }
}