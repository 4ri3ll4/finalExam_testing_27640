package com.auca.library;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import com.auca.library.dao.LocationDao;
import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.*;
import com.auca.library.service.MembershipService;
import com.auca.library.util.HibernateUtil;

public class MembershipServiceTest {

    private static SessionFactory sessionFactory;
    private static UserDao userDao;
    private static MembershipDao membershipDao;
    private static MembershipTypeDao membershipTypeDao;
    private static MembershipService membershipService;

    private MembershipType goldType;

    @BeforeAll
    static void setUpClass() {
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
        userDao = new UserDao(sessionFactory);
        membershipDao = new MembershipDao(sessionFactory);
        membershipTypeDao = new MembershipTypeDao(sessionFactory);
        membershipService = new MembershipService(membershipDao, membershipTypeDao, userDao);
    }

    @BeforeEach
    void cleanTables() {
        TestCleaner.cleanAll(sessionFactory);
        goldType = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
    }

    @AfterAll
    static void tearDownClass() {
        sessionFactory.close();
    }

    private User createTestUser(String userName) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender(Gender.MALE);
        user.setPhoneNumber("0788000000");
        user.setUserName(userName);
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        return userDao.save(user);
    }

    @Test
    void registerMembership_gold_createsPendingMembershipLinkedToGoldType() {
        User user = createTestUser("golduser1");

        Membership membership = membershipService.registerMembership(
                user.getPersonId(), goldType.getMembershipTypeId());

        assertNotNull(membership.getMembershipId());
        assertEquals(Status.PENDING, membership.getMembershipStatus());
        assertEquals("GOLD", membership.getMembershipType().getMembershipName());
        assertEquals(user.getPersonId(), membership.getReader().getPersonId());
    }

    @Test
    void registerMembership_userAlreadyHasActiveMembership_throwsException() {
        User user = createTestUser("golduser2");
        membershipService.registerMembership(user.getPersonId(), goldType.getMembershipTypeId());

        // Second attempt for the same user should be rejected
        assertThrows(IllegalStateException.class, () ->
                membershipService.registerMembership(user.getPersonId(), goldType.getMembershipTypeId()));
    }
}