package com.auca.library.service;

// Custom, specific exception — used by validateBorrowLimit so callers can
// catch this exact case rather than a generic RuntimeException, matching
// the assignment's method signature exactly.
public class BorrowLimitExceededException extends RuntimeException {

    public BorrowLimitExceededException(String message) {
        super(message);
    }
}