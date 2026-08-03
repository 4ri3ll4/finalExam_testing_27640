package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.auca.library.domain.User;

public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User save(User user) {
        try (Session session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return user;
        }
    }

    public User findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(User.class, id);
        }
    }

    public User findByUserName(String userName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from User u where u.userName = :userName", User.class)
                    .setParameter("userName", userName)
                    .uniqueResult(); // null if not found, no exception — we'll use this in Requirement 4 too
        }
    }
}