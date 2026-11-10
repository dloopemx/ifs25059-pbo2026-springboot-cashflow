package org.delcom.ifs25059pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25059pbo2026springbootcashflow.domain.repository.IRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/** Implementasi penyimpanan generik berbasis memori dengan salinan defensif. */
public class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    private final Map<ID, T> storage = new LinkedHashMap<>();
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override public synchronized List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
    @Override public synchronized Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }
    @Override public synchronized T save(T entity) {
        storage.put(idExtractor.apply(entity), entity);
        return entity;
    }
    @Override public synchronized boolean deleteById(ID id) {
        return storage.remove(id) != null;
    }
    @Override public synchronized void update(T entity) {
        ID id = idExtractor.apply(entity);
        if (!storage.containsKey(id)) throw new IllegalArgumentException("Entitas tidak ditemukan: " + id);
        storage.put(id, entity);
    }
    @Override public synchronized List<T> findBy(Predicate<T> predicate) {
        return storage.values().stream().filter(predicate).toList();
    }
}
