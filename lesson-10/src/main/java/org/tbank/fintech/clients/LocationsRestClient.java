package org.tbank.fintech.clients;

import org.tbank.fintech.entity.Location;

import java.util.List;

public interface LocationsRestClient {
    List<Location> findAllLocations(String lang, String orderBy, List<String> fields);
}
