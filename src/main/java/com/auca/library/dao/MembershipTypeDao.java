package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.MembershipType;

public class MembershipTypeDao {

    private final SessionFactory sessionFactory;

    public MembershipTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public MembershipType save(MembershipType membershipType) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(membershipType);
            tx.commit();
            return membershipType;
        }
    }

    public MembershipType findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(MembershipType.class, id);
        }
    }
}