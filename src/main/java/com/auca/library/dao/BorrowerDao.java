package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.Borrower;

public class BorrowerDao {

    private final SessionFactory sessionFactory;

    public BorrowerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Borrower save(Borrower borrower) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            Borrower result = session.merge(borrower);
            tx.commit();
            return result;
        }
    }

    // "Active" = not yet returned (returnDate is null). This count is what
    // Requirement 7 compares against the membership's maxBooks.
    public long countActiveBorrowsByReaderId(UUID readerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select count(b) from Borrower b where b.reader.personId = :readerId and b.returnDate is null",
                    Long.class)
                    .setParameter("readerId", readerId)
                    .getSingleResult();
        }
    }

    public Borrower findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Borrower.class, id);
        }
    }
}