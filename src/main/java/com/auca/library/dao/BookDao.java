package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.Book;
import com.auca.library.domain.Shelf;

public class BookDao {

    private final SessionFactory sessionFactory;

    public BookDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Book save(Book book) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(book);
            tx.commit();
            return book;
        }
    }

    public Book findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Book.class, id);
        }
    }

    // Updates an existing book's status. We fetch it inside the SAME
    // session/transaction as the change — Hibernate's "dirty checking"
    // then automatically detects the field changed and writes the UPDATE
    // on commit, with no explicit save/update call needed.
    public void updateStatus(UUID bookId, com.auca.library.domain.BookStatus newStatus) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            Book book = session.find(Book.class, bookId);
            book.setBookStatus(newStatus);
            tx.commit();
        }
    }

    public void assignToShelf(UUID bookId, UUID shelfId) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            Book book = session.find(Book.class, bookId);
            Shelf shelf = session.find(Shelf.class, shelfId);
            book.setShelf(shelf);
            tx.commit();
        }
    }
}