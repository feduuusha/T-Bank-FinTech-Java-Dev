package org.tbank.fintech.repository.impl;

import org.tbank.fintech.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConcurrentHashMapRepository<T> implements CrudRepository<T> {
    protected final ConcurrentHashMap<Long, T> map = new ConcurrentHashMap<>();
    protected Long lastIndex = 1L;

    public List<T> findAll() {
        return this.map.values().stream().toList();
    }

    public abstract T save(T elem);

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(this.map.get(id));
    }

    public void updateById(Long id, T elem) {}

    public void deleteById(Long id) {
        this.map.remove(id);
    }
}
