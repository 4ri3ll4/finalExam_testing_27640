package com.auca.library;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import com.auca.library.dao.*;
import com.auca.library.domain.*;
import com.auca.library.service.BorrowLimitExceededException;
import com.auca.library.service.BorrowerService;
import com.auca.library.util.HibernateUtil;

public class BorrowerServiceTest {

    private static SessionFactory sessionFactory;
    private static UserDao userDao;
    private static BookDao bookDao;
    private static BorrowerDao borrowerDao;
    private static MembershipDao membershipDao;
    private static MembershipTypeDao membershipTypeDao;
    private static BorrowerService borrowerService;

    @BeforeAll
    static void setUpClass() {
        sessionFactory = HibernateUtil.buildSessionFactory("application.properties");
        userDao = new UserDao(sessionFactory);
        bookDao = new BookDao(sessionFactory);
        borrowerDao = new BorrowerDao(sessionFactory);
        membershipDao = new MembershipDao(sessionFactory);
        membershipTypeDao = new MembershipTypeDao(sessionFactory);
        borrowerService = new BorrowerService(borrowerDao, bookDao, membershipDao);
    }

    @BeforeEach
    void cleanTables() {
        TestCleaner.cleanAll(sessionFactory);
    }

    @AfterAll
    static void tearDownClass() {
        sessionFactory.close();
    }

    // ── helpers ──────────────────────────────────────────────

    private User createUser(String userName) {
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

    // Directly saves an APPROVED membership, bypassing the PENDING default
    // from MembershipService.registerMembership — appropriate here since
    // we're testing borrowing, not the registration workflow itself.
    private void giveApprovedMembership(User user, MembershipType type) {
        Membership membership = new Membership();
        membership.setMembershipCode("MEM-" + UUID.randomUUID());
        membership.setRegistrationDate(LocalDate.now());
        membership.setExpiringTime(LocalDate.now().plusYears(1));
        membership.setMembershipStatus(Status.APPROVED);
        membership.setReader(user);
        membership.setMembershipType(type);
        membershipDao.save(membership);
    }

    private Book createAvailableBook(String title) {
        return bookDao.save(new Book(title, "ISBN-" + UUID.randomUUID(), 1, "Test Publisher", LocalDate.of(2020, 1, 1)));
    }

    // ── Requirement 6 ────────────────────────────────────────

    @Test
    void borrowBook_availableBook_createsBorrowerRecordWithZeroFine() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("reader1");
        giveApprovedMembership(user, gold);
        Book book = createAvailableBook("Clean Code");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());

        assertNotNull(borrower.getId());
        assertEquals(0, borrower.getFine());
    }

    @Test
    void borrowBook_setsBookStatusToBorrowed() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("reader2");
        giveApprovedMembership(user, gold);
        Book book = createAvailableBook("Effective Java");

        borrowerService.borrowBook(user.getPersonId(), book.getBookId());

        Book refreshed = bookDao.findById(book.getBookId());
        assertEquals(BookStatus.BORROWED, refreshed.getBookStatus());
    }

    @Test
    void borrowBook_dueDateIsPickupDatePlusLoanPeriod() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("reader3");
        giveApprovedMembership(user, gold);
        Book book = createAvailableBook("Refactoring");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());

        assertEquals(borrower.getPickupDate().plusDays(14), borrower.getDueDate());
    }

    // ── Requirement 7 ────────────────────────────────────────

    @Test
    void goldMember_withFourActiveBorrows_canBorrowAFifth() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("golduser");
        giveApprovedMembership(user, gold);

        for (int i = 0; i < 4; i++) {
            Book book = createAvailableBook("Gold Book " + i);
            borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        }

        Book fifthBook = createAvailableBook("Gold Book 5");
        assertDoesNotThrow(() -> borrowerService.borrowBook(user.getPersonId(), fifthBook.getBookId()));
    }

    @Test
    void goldMember_withFiveActiveBorrows_cannotBorrowASixth() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("golduser2");
        giveApprovedMembership(user, gold);

        for (int i = 0; i < 5; i++) {
            Book book = createAvailableBook("Gold Book " + i);
            borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        }

        Book sixthBook = createAvailableBook("Gold Book 6");
        assertThrows(BorrowLimitExceededException.class, () ->
                borrowerService.borrowBook(user.getPersonId(), sixthBook.getBookId()));
    }

    @Test
    void silverMember_withThreeActiveBorrows_isBlocked() {
        MembershipType silver = membershipTypeDao.save(new MembershipType("SILVER", 3, 30));
        User user = createUser("silveruser");
        giveApprovedMembership(user, silver);

        for (int i = 0; i < 3; i++) {
            Book book = createAvailableBook("Silver Book " + i);
            borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        }

        Book fourthBook = createAvailableBook("Silver Book 4");
        assertThrows(BorrowLimitExceededException.class, () ->
                borrowerService.borrowBook(user.getPersonId(), fourthBook.getBookId()));
    }

    @Test
    void striverMember_withTwoActiveBorrows_isBlocked() {
        MembershipType striver = membershipTypeDao.save(new MembershipType("STRIVER", 2, 10));
        User user = createUser("striveruser");
        giveApprovedMembership(user, striver);

        for (int i = 0; i < 2; i++) {
            Book book = createAvailableBook("Striver Book " + i);
            borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        }

        Book thirdBook = createAvailableBook("Striver Book 3");
        assertThrows(BorrowLimitExceededException.class, () ->
                borrowerService.borrowBook(user.getPersonId(), thirdBook.getBookId()));
    }

    @Test
    void userWithoutApprovedMembership_isBlocked() {
        User user = createUser("nomembershipuser");
        Book book = createAvailableBook("Some Book");

        assertThrows(BorrowLimitExceededException.class, () ->
                borrowerService.validateBorrowLimit(user.getPersonId()));
    }

    // ── Requirement 12 ───────────────────────────────────────

    @Test
    void returnedOnDueDate_feeIsZero() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("feeuser1");
        giveApprovedMembership(user, gold);
        Book book = createAvailableBook("Fee Test Book 1");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        borrower.setReturnDate(borrower.getDueDate()); // returned exactly on time
        borrowerDao.save(borrower);

        int fee = borrowerService.calculateLateFee(borrower.getId());

        assertEquals(0, fee);
    }

    @Test
    void goldMember_returnedThreeDaysLate_feeIs150() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("feeuser2");
        giveApprovedMembership(user, gold);
        Book book = createAvailableBook("Fee Test Book 2");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        borrower.setReturnDate(borrower.getDueDate().plusDays(3));
        borrowerDao.save(borrower);

        int fee = borrowerService.calculateLateFee(borrower.getId());

        assertEquals(150, fee); // 3 days x 50 Rwf
    }

    @Test
    void silverMember_returnedFiveDaysLate_feeIs150() {
        MembershipType silver = membershipTypeDao.save(new MembershipType("SILVER", 3, 30));
        User user = createUser("feeuser3");
        giveApprovedMembership(user, silver);
        Book book = createAvailableBook("Fee Test Book 3");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        borrower.setReturnDate(borrower.getDueDate().plusDays(5));
        borrowerDao.save(borrower);

        int fee = borrowerService.calculateLateFee(borrower.getId());

        assertEquals(150, fee); // 5 days x 30 Rwf
    }

    @Test
    void striverMember_returnedOneDayLate_feeIs10() {
        MembershipType striver = membershipTypeDao.save(new MembershipType("STRIVER", 2, 10));
        User user = createUser("feeuser4");
        giveApprovedMembership(user, striver);
        Book book = createAvailableBook("Fee Test Book 4");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        borrower.setReturnDate(borrower.getDueDate().plusDays(1));
        borrowerDao.save(borrower);

        int fee = borrowerService.calculateLateFee(borrower.getId());

        assertEquals(10, fee); // 1 day x 10 Rwf
    }

    @Test
    void notYetReturned_feeIsComputedAgainstToday() {
        MembershipType gold = membershipTypeDao.save(new MembershipType("GOLD", 5, 50));
        User user = createUser("feeuser5");
        giveApprovedMembership(user, gold);
        Book book = createAvailableBook("Fee Test Book 5");

        Borrower borrower = borrowerService.borrowBook(user.getPersonId(), book.getBookId());
        // Manually push the due date into the past to simulate an overdue,
        // still-not-returned book, without waiting for real time to pass.
        borrower.setDueDate(LocalDate.now().minusDays(4));
        borrowerDao.save(borrower);

        int fee = borrowerService.calculateLateFee(borrower.getId());

        assertEquals(200, fee); // 4 days x 50 Rwf, computed against today since returnDate is still null
    }
}