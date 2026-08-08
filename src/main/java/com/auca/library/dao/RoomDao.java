package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.Room;

public class RoomDao {

    private final SessionFactory sessionFactory;

    public RoomDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Room save(Room room) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(room);
            tx.commit();
            return room;
        }
    }

    public Room findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Room.class, id);
        }
    }

    public List<Room> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Room", Room.class).getResultList();
        }
    }
}