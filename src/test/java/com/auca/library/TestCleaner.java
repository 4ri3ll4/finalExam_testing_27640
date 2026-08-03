package com.auca.library;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

// Central place for wiping test data between tests, in the correct
// foreign-key-safe order: children (tables with foreign keys pointing
// OUT) get deleted before parents (tables being pointed AT).
// As we add more entities (Borrower, Book, Shelf, Room), new deletes get
// added here, in dependency order, ONCE — instead of touching every
// test class's own cleanup method each time.
public class TestCleaner {

    public static void cleanAll(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();

            // Membership depends on User and MembershipType → delete first
            session.createMutationQuery("delete from Membership").executeUpdate();

            // User depends on Location → delete before Location
            session.createMutationQuery("delete from User").executeUpdate();

            // No dependents left pointing at these — safe to delete now
            session.createMutationQuery("delete from MembershipType").executeUpdate();
            session.createMutationQuery("delete from Location").executeUpdate();

            tx.commit();
        }
    }
}