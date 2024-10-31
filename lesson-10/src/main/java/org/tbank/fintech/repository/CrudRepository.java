package org.tbank.fintech.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    List<T> findAll();
    T save(T elem);
    Optional<T> findById(Long id);
    void updateById(Long id, T elem);
    void deleteById(Long id);
}
