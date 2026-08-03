package com.auca.library.service;

import java.util.UUID;

import com.auca.library.dao.LocationDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.LocationType;

public class LocationService {

    private final LocationDao locationDao;

    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public String getProvinceNameByVillageId(UUID villageId) {
        Location location = locationDao.findById(villageId);
        if (location == null) {
            throw new IllegalArgumentException("No location found for id: " + villageId);
        }
        return getProvinceName(location);
    }

    // Shared logic: walk up the parent chain from ANY location until we
    // reach PROVINCE. Reused by both the village lookup and the person
    // lookup (Requirement 3), since both ultimately need this same walk.
    public String getProvinceName(Location startingLocation) {
        Location current = startingLocation;
        while (current.getLocationType() != LocationType.PROVINCE) {
            current = current.getParent();
            if (current == null) {
                throw new IllegalStateException("Location hierarchy has no PROVINCE ancestor");
            }
        }
        return current.getLocationName();
    }
}