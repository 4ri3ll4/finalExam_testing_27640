package com.auca.library.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shelfId;

    private int availableStock;
    private String bookCategory;
    private int borrowedNumber;
    private int initialStock;

    // Nullable — a shelf can exist before being assigned to a room
    // (Requirement 9 is a separate step from creating the shelf).
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public Shelf() {
    }

    public Shelf(String bookCategory, int initialStock) {
        this.bookCategory = bookCategory;
        this.initialStock = initialStock;
        this.availableStock = 0;
        this.borrowedNumber = 0;
    }

    public UUID getShelfId() {
        return shelfId;
    }

    public void setShelfId(UUID shelfId) {
        this.shelfId = shelfId;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public int getBorrowedNumber() {
        return borrowedNumber;
    }

    public void setBorrowedNumber(int borrowedNumber) {
        this.borrowedNumber = borrowedNumber;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}