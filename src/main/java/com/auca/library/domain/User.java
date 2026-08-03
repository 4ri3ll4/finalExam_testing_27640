package com.auca.library.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user") // "user" is a reserved word in PostgreSQL — renamed to avoid it
public class User extends Person {

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String userName;

    // Diagram calls this "village_id" — mapped here as a real reference to
    // Location (not just a raw UUID column) so we can reuse the same
    // parent-walking logic from LocationService.
    @ManyToOne
    @JoinColumn(name = "village_id")
    private Location village;

    public User() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Location getVillage() {
        return village;
    }

    public void setVillage(Location village) {
        this.village = village;
    }
}