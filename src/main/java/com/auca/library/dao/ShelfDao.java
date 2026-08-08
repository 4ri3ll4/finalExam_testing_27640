package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.Shelf;
import com.auca.library.domain.Room;

public class ShelfDao {

    private final SessionFactory sessionFactory;

    public ShelfDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Shelf save(Shelf shelf) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(shelf);
            tx.commit();
            return shelf;
        }
    }

    public Shelf findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Shelf.class, id);
        }
    }

    // All shelves currently assigned to a given room — used to sum book
    // counts for Requirement 10.
    public List<Shelf> findByRoomId(UUID roomId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Shelf s where s.room.roomId = :roomId", Shelf.class)
                    .setParameter("roomId", roomId)
                    .getResultList();
        }
    }

    public void incrementAvailableStock(UUID shelfId) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            Shelf shelf = session.find(Shelf.class, shelfId);
            shelf.setAvailableStock(shelf.getAvailableStock() + 1); // dirty checking handles the UPDATE
            tx.commit();
        }
    }

    public void assignToRoom(UUID shelfId, UUID roomId) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            Shelf shelf = session.find(Shelf.class, shelfId);
            Room room = session.find(Room.class, roomId);
            shelf.setRoom(room);
            tx.commit();
        }
    }
}