package com.auca.library.service;

import java.util.List;
import java.util.UUID;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;

public class LibraryStructureService {

    private final BookDao bookDao;
    private final ShelfDao shelfDao;
    private final RoomDao roomDao;

    public LibraryStructureService(BookDao bookDao, ShelfDao shelfDao, RoomDao roomDao) {
        this.bookDao = bookDao;
        this.shelfDao = shelfDao;
        this.roomDao = roomDao;
    }

    // Requirement 8
    public void assignBookToShelf(UUID bookId, UUID shelfId) {
        bookDao.assignToShelf(bookId, shelfId);
        shelfDao.incrementAvailableStock(shelfId);
    }

    // Requirement 9
    public void assignShelfToRoom(UUID shelfId, UUID roomId) {
        shelfDao.assignToRoom(shelfId, roomId);
    }

    // Requirement 10 — see design note: sums the availableStock counter
    // across all shelves in the room, rather than querying Book rows
    // directly, since that's the counter Requirement 8 maintains.
    public int countBooksInRoom(UUID roomId) {
        List<Shelf> shelves = shelfDao.findByRoomId(roomId);
        int total = 0;
        for (Shelf shelf : shelves) {
            total += shelf.getAvailableStock();
        }
        return total;
    }

    // Requirement 11
    public Room findRoomWithFewestBooks() {
        List<Room> rooms = roomDao.findAll();

        if (rooms.isEmpty()) {
            throw new IllegalStateException("No rooms exist");
        }

        Room fewest = null;
        int fewestCount = Integer.MAX_VALUE;

        for (Room room : rooms) {
            int count = countBooksInRoom(room.getRoomId());
            if (count < fewestCount) {
                fewestCount = count;
                fewest = room;
            }
        }

        return fewest;
    }
}