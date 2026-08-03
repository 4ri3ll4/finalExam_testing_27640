package com.auca.library;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import com.auca.library.dao.LocationDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.LocationType;
import com.auca.library.service.LocationService;
import com.auca.library.util.HibernateUtil;

public class LocationServiceTest {

    private static SessionFactory sessionFactory;
    private static LocationDao locationDao;
    private static LocationService locationService;

    @BeforeAll
    static void setUpClass() {
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
        locationDao = new LocationDao(sessionFactory);
        locationService = new LocationService(locationDao);
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
    void validVillageId_returnsCorrectProvinceName() {
        Location province = locationDao.save(
                new Location("EST", "Eastern Province", LocationType.PROVINCE, null));
        Location district = locationDao.save(
                new Location("KAY", "Kayonza", LocationType.DISTRICT, province));
        Location sector = locationDao.save(
                new Location("MUK", "Mukarange", LocationType.SECTOR, district));
        Location cell = locationDao.save(
                new Location("GAH", "Gahini Cell", LocationType.CELL, sector));
        Location village = locationDao.save(
                new Location("KAB", "Kabare Village", LocationType.VILLAGE, cell));

        String result = locationService.getProvinceNameByVillageId(village.getLocationId());

        assertEquals("Eastern Province", result);
    }
}