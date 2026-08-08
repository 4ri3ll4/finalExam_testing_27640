package com.auca.library;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class TestCleaner {

    public static void cleanAll(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();

            // Borrower depends on Book and User
            session.createMutationQuery("delete from Borrower").executeUpdate();

            // Book depends on Shelf (now a real relationship)
            session.createMutationQuery("delete from Book").executeUpdate();

            // Shelf depends on Room
            session.createMutationQuery("delete from Shelf").executeUpdate();

            session.createMutationQuery("delete from Room").executeUpdate();

            // Membership depends on User and MembershipType
            session.createMutationQuery("delete from Membership").executeUpdate();

            // User depends on Location
            session.createMutationQuery("delete from User").executeUpdate();

            session.createMutationQuery("delete from MembershipType").executeUpdate();
            session.createMutationQuery("delete from Location").executeUpdate();

            tx.commit();
        }
    }
}