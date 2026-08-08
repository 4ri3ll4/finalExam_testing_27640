package com.auca.library;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.*;
import com.auca.library.service.LibraryStructureService;
import com.auca.library.util.HibernateUtil;

import java.time.LocalDate;
import java.util.UUID;

public class LibraryStructureServiceTest {

    private static SessionFactory sessionFactory;
    private static BookDao bookDao;
    private static ShelfDao shelfDao;
    private static RoomDao roomDao;
    private static LibraryStructureService structureService;

    @BeforeAll
    static void setUpClass() {
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
        bookDao = new BookDao(sessionFactory);
        shelfDao = new ShelfDao(sessionFactory);
        roomDao = new RoomDao(sessionFactory);
        structureService = new LibraryStructureService(bookDao, shelfDao, roomDao);
    }

    @BeforeEach
    void cleanTables() {
        TestCleaner.cleanAll(sessionFactory);
    }

    @AfterAll
    static void tearDownClass() {
        sessionFactory.close();
    }

    private Book createBook(String title) {
        return bookDao.save(new Book(title, "ISBN-" + UUID.randomUUID(), 1, "Test Publisher", LocalDate.of(2020, 1, 1)));
    }

    // ── Requirement 8 ────────────────────────────────────────

    @Test
    void assignBookToShelf_updatesBookShelfId() {
        Shelf shelf = shelfDao.save(new Shelf("Fiction", 100));
        Book book = createBook("Dune");

        structureService.assignBookToShelf(book.getBookId(), shelf.getShelfId());

        Book refreshed = bookDao.findById(book.getBookId());
        assertNotNull(refreshed.getShelf());
        assertEquals(shelf.getShelfId(), refreshed.getShelf().getShelfId());
    }

    @Test
    void assignBookToShelf_incrementsShelfAvailableStock() {
        Shelf shelf = shelfDao.save(new Shelf("Fiction", 100));
        Book book = createBook("1984");

        structureService.assignBookToShelf(book.getBookId(), shelf.getShelfId());

        Shelf refreshed = shelfDao.findById(shelf.getShelfId());
        assertEquals(1, refreshed.getAvailableStock());
    }

    // ── Requirement 9 ────────────────────────────────────────

    @Test
    void assignShelfToRoom_updatesShelfRoomId() {
        Room room = roomDao.save(new Room("R101"));
        Shelf shelf = shelfDao.save(new Shelf("Fiction", 100));

        structureService.assignShelfToRoom(shelf.getShelfId(), room.getRoomId());

        Shelf refreshed = shelfDao.findById(shelf.getShelfId());
        assertNotNull(refreshed.getRoom());
        assertEquals(room.getRoomId(), refreshed.getRoom().getRoomId());
    }

    // ── Requirement 10 ───────────────────────────────────────

    @Test
    void roomWithMultipleShelves_sumsBookCountsAcrossShelves() {
        Room room = roomDao.save(new Room("R102"));

        Shelf shelf1 = shelfDao.save(new Shelf("Fiction", 100));
        Shelf shelf2 = shelfDao.save(new Shelf("Science", 100));
        structureService.assignShelfToRoom(shelf1.getShelfId(), room.getRoomId());
        structureService.assignShelfToRoom(shelf2.getShelfId(), room.getRoomId());

        Book book1 = createBook("Book A");
        Book book2 = createBook("Book B");
        Book book3 = createBook("Book C");
        structureService.assignBookToShelf(book1.getBookId(), shelf1.getShelfId());
        structureService.assignBookToShelf(book2.getBookId(), shelf1.getShelfId());
        structureService.assignBookToShelf(book3.getBookId(), shelf2.getShelfId());

        int count = structureService.countBooksInRoom(room.getRoomId());

        assertEquals(3, count);
    }

    @Test
    void roomWithNoShelves_returnsZero() {
        Room room = roomDao.save(new Room("R103"));

        int count = structureService.countBooksInRoom(room.getRoomId());

        assertEquals(0, count);
    }

    // ── Requirement 11 ───────────────────────────────────────

    @Test
    void multipleRooms_returnsRoomWithLowestBookCount() {
        Room busyRoom = roomDao.save(new Room("R104"));
        Room quietRoom = roomDao.save(new Room("R105"));

        Shelf busyShelf = shelfDao.save(new Shelf("Fiction", 100));
        Shelf quietShelf = shelfDao.save(new Shelf("Fiction", 100));
        structureService.assignShelfToRoom(busyShelf.getShelfId(), busyRoom.getRoomId());
        structureService.assignShelfToRoom(quietShelf.getShelfId(), quietRoom.getRoomId());

        // busyRoom gets 2 books, quietRoom gets 1
        Book book1 = createBook("Book A");
        Book book2 = createBook("Book B");
        Book book3 = createBook("Book C");
        structureService.assignBookToShelf(book1.getBookId(), busyShelf.getShelfId());
        structureService.assignBookToShelf(book2.getBookId(), busyShelf.getShelfId());
        structureService.assignBookToShelf(book3.getBookId(), quietShelf.getShelfId());

        Room result = structureService.findRoomWithFewestBooks();

        assertEquals(quietRoom.getRoomId(), result.getRoomId());
    }
}