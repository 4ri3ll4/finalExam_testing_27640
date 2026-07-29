package com.auca.library;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import com.auca.library.domain.Location;
import com.auca.library.domain.LocationType;
import com.auca.library.util.HibernateUtil;

public class LocationTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUpClass() {
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
    }

    @AfterAll
    static void tearDownClass() {
        sessionFactory.close();
    }

    @Test
    void saveProvince_withNoParent_persistsSuccessfully() {
        Location province = new Location("KIG", "Kigali City", LocationType.PROVINCE, null);

        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(province);
            tx.commit();
        }

        assertNotNull(province.getLocationId(), "Hibernate should have generated an id");
    }
}