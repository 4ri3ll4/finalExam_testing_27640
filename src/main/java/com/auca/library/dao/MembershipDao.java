package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.Membership;
import com.auca.library.domain.Status;

public class MembershipDao {

    private final SessionFactory sessionFactory;

    public MembershipDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Membership save(Membership membership) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(membership);
            tx.commit();
            return membership;
        }
    }

    public Membership findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Membership.class, id);
        }
    }

    // "Active" here means PENDING or APPROVED — i.e. not REJECTED.
    // Used to enforce "one active membership per user" in the service.
    public Membership findActiveMembershipByReaderId(UUID readerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Membership m where m.reader.personId = :readerId " +
                    "and m.membershipStatus != :rejected",
                    Membership.class)
                    .setParameter("readerId", readerId)
                    .setParameter("rejected", Status.REJECTED)
                    .uniqueResult();
        }
    }
}