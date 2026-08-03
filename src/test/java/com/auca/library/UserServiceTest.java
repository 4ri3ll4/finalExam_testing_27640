package com.auca.library;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import com.auca.library.dao.LocationDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.*;
import com.auca.library.service.LocationService;
import com.auca.library.service.UserService;
import com.auca.library.util.HibernateUtil;

public class UserServiceTest {

    private static SessionFactory sessionFactory;
    private static LocationDao locationDao;
    private static UserDao userDao;
    private static UserService userService;

    @BeforeAll
    static void setUpClass() {
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
        locationDao = new LocationDao(sessionFactory);
        userDao = new UserDao(sessionFactory);
        LocationService locationService = new LocationService(locationDao);
        userService = new UserService(userDao, locationService);
    }

    @BeforeEach
    void cleanTables() {
        TestCleaner.cleanAll(sessionFactory);
    }

    @AfterAll
    static void tearDownClass() {
        sessionFactory.close();
    }

    @Test
    void validPersonId_returnsCorrectProvinceName() {
        Location province = locationDao.save(
                new Location("WST", "Western Province", LocationType.PROVINCE, null));
        Location district = locationDao.save(
                new Location("RUS", "Rusizi", LocationType.DISTRICT, province));
        Location sector = locationDao.save(
                new Location("GIT", "Gitambi", LocationType.SECTOR, district));
        Location cell = locationDao.save(
                new Location("NYA", "Nyabihu Cell", LocationType.CELL, sector));
        Location village = locationDao.save(
                new Location("MUR", "Muriza Village", LocationType.VILLAGE, cell));

        User user = new User();
        user.setFirstName("Eric");
        user.setLastName("Niyonzima");
        user.setGender(Gender.MALE);
        user.setPhoneNumber("0788123456");
        user.setUserName("eniyonzima");
        user.setPassword("hashed-password-placeholder");
        user.setRole(Role.STUDENT);
        user.setVillage(village);
        userDao.save(user);

        String result = userService.getProvinceNameByPersonId(user.getPersonId());

        assertEquals("Western Province", result);
    }

    @Test
    void authenticate_correctCredentials_returnsTrue() {
        User user = new User();
        user.setFirstName("Grace");
        user.setLastName("Uwase");
        user.setGender(Gender.FEMALE);
        user.setPhoneNumber("0788000111");
        user.setUserName("guwase");
        user.setPassword("correct-password");
        user.setRole(Role.STUDENT);
        userDao.save(user);

        boolean result = userService.authenticate("guwase", "correct-password");

        assertTrue(result);
    }

    @Test
    void authenticate_wrongPassword_returnsFalse() {
        User user = new User();
        user.setFirstName("Grace");
        user.setLastName("Uwase");
        user.setGender(Gender.FEMALE);
        user.setPhoneNumber("0788000111");
        user.setUserName("guwase2");
        user.setPassword("correct-password");
        user.setRole(Role.STUDENT);
        userDao.save(user);

        boolean result = userService.authenticate("guwase2", "wrong-password");

        assertFalse(result);
    }

    @Test
    void authenticate_unknownUsername_returnsFalse() {
        boolean result = userService.authenticate("does-not-exist", "anything");

        assertFalse(result);
    }

    @Test
    void authenticate_nullOrBlankInput_returnsFalse() {
        assertFalse(userService.authenticate(null, "somepassword"));
        assertFalse(userService.authenticate("someuser", null));
        assertFalse(userService.authenticate("", "somepassword"));
        assertFalse(userService.authenticate("someuser", ""));
    }
}