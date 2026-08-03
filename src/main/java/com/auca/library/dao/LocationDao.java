package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.Location;

public class LocationDao {

    private final SessionFactory sessionFactory;

    // Constructor injection — same reasoning as your lecturer's StudentDao:
    // the DAO doesn't decide which database it talks to, the caller does.
    // This is what lets a test hand it a SessionFactory pointed at a test DB.
    public LocationDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Location save(Location location) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(location);
            tx.commit();
            return location;
        }
    }

    public Location findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            // session.find() is Hibernate's simplest lookup-by-primary-key
            // method — equivalent to "from Location where id = :id" but
            // shorter, since we're just fetching by id, not filtering.
            return session.find(Location.class, id);
        }
    }
}