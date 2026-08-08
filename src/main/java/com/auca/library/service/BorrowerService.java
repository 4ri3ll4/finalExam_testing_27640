package com.auca.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.BorrowerDao;
import com.auca.library.dao.MembershipDao;
import com.auca.library.domain.Book;
import com.auca.library.domain.BookStatus;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Membership;
import com.auca.library.domain.User;

public class BorrowerService {

    // ASSUMPTION: the assignment doesn't specify a loan period, so I fixed
    // it at 14 days — a reasonable library default. Easy to change if your
    // lecturer specifies a different number.
    private static final int LOAN_PERIOD_DAYS = 14;

    private final BorrowerDao borrowerDao;
    private final BookDao bookDao;
    private final MembershipDao membershipDao;

    public BorrowerService(BorrowerDao borrowerDao, BookDao bookDao, MembershipDao membershipDao) {
        this.borrowerDao = borrowerDao;
        this.bookDao = bookDao;
        this.membershipDao = membershipDao;
    }

    // Requirement 7
    public void validateBorrowLimit(UUID readerId) {
        Membership membership = membershipDao.findApprovedMembershipByReaderId(readerId);

        if (membership == null) {
            throw new BorrowLimitExceededException(
                    "User has no approved membership and cannot borrow books");
        }

        long activeBorrows = borrowerDao.countActiveBorrowsByReaderId(readerId);
        int maxBooks = membership.getMembershipType().getMaxBooks();

        if (activeBorrows >= maxBooks) {
            throw new BorrowLimitExceededException(
                    "User has reached their borrow limit of " + maxBooks + " books");
        }
    }

    // Requirement 6
    public Borrower borrowBook(UUID readerId, UUID bookId) {
        // Enforce the limit BEFORE creating any record — fail fast.
        validateBorrowLimit(readerId);

        Book book = bookDao.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("No book found for id: " + bookId);
        }
        if (book.getBookStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available to borrow");
        }

        User reader = new User();
        reader.setPersonId(readerId); // lightweight reference — see note below

        Borrower borrower = new Borrower();
        borrower.setPickupDate(LocalDate.now());
        borrower.setDueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS));
        borrower.setFine(0); // "fine is initialized to zero at borrowing date" — assignment's own words
        borrower.setLateChargeFees(0);
        borrower.setReader(reader);
        borrower.setBook(book);

        Borrower saved = borrowerDao.save(borrower);

        bookDao.updateStatus(bookId, BookStatus.BORROWED);

        return saved;
    }

    // Requirement 12
    public int calculateLateFee(UUID borrowerId) {
        Borrower borrower = borrowerDao.findById(borrowerId);
        if (borrower == null) {
            throw new IllegalArgumentException("No borrower record found for id: " + borrowerId);
        }

        // "notYetReturned_feeIsComputedAgainstToday" — if the book hasn't come
        // back yet, we calculate lateness as of right now rather than waiting
        // for a return date that doesn't exist.
        LocalDate effectiveReturnDate = borrower.getReturnDate() != null
            ? borrower.getReturnDate()
            : LocalDate.now();

        long daysLate = ChronoUnit.DAYS.between(borrower.getDueDate(), effectiveReturnDate);

        if (daysLate <= 0) {
            return 0; // returned on time or early — "returnedOnDueDate_feeIsZero"
        }

        Membership membership = membershipDao.findApprovedMembershipByReaderId(
            borrower.getReader().getPersonId());

        if (membership == null) {
            throw new IllegalStateException("Borrower has no approved membership to determine daily rate");
        }

        int dailyRate = membership.getMembershipType().getPrice();

        return (int) (daysLate * dailyRate);
    }
}