package org.tbank.fintech.repository.impl;

import org.springframework.stereotype.Repository;
import org.tbank.fintech.entity.Location;
import org.tbank.fintech.repository.LocationRepository;

@Repository
public class ConcurrentHashMapLocationRepository extends ConcurrentHashMapRepository<Location> implements LocationRepository {
    @Override
    public Location save(Location elem) {
        elem.setId(lastIndex);
        this.map.put(lastIndex++, elem);
        return elem;
    }
}
