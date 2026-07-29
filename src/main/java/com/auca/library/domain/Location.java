package com.auca.library.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate generates a random UUID for us
    private UUID locationId;

    private String locationCode;
    private String locationName;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    // Self-reference: a District's parent is a Province, a Sector's parent
    // is a District, etc. A Province itself has parent = null (top of the
    // hierarchy). Same pattern as AcademicUnit.parent from the JSP project.
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Location parent;

    public Location() {
    }

    public Location(String locationCode, String locationName, LocationType locationType, Location parent) {
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.locationType = locationType;
        this.parent = parent;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }
}