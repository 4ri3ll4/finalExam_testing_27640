# AUCA Library Management System

Final project for Software Testing and Techniques (Summer 2025).

NAME: RUREBWAYIRE AMPOZE Ariella

ID: 27640

## Stack
- Java 21
- Maven
- Hibernate ORM 6.5
- PostgreSQL (`auca_library_db`)
- JUnit 5

## Architecture

```
src/main/java/com/auca/library/
├── domain/   entities (Location, User, Book, ...)
├── dao/      Hibernate Session/HQL data access
├── service/  business rules (validation, workflows)
└── util/     HibernateUtil (SessionFactory setup)
```

No UI — this project is validated entirely through JUnit test cases that insert and query data, per the assignment brief.

## Progress

- [x] Project scaffolding, `pom.xml`, database created
- [x] Hibernate + PostgreSQL connection working
- [x] Requirement 1: `Location` entity with self-referencing hierarchy (Province → District → Sector → Cell → Village)
- [ ] Requirement 2: village → province lookup
- [ ] Requirement 3: person → province lookup
- [ ] Requirement 4: user authentication
- [ ] Requirement 5: membership registration
- [ ] Requirement 6: borrowing a book
- [ ] Requirement 7: borrow limit validation
- [ ] Requirement 8: assign book to shelf
- [ ] Requirement 9: assign shelf to room
- [ ] Requirement 10: count books in a room
- [ ] Requirement 11: find room with fewest books
- [ ] Requirement 12: late fee calculation

## Running tests

```bash
mvn test
```