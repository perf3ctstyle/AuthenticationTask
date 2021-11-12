package com.epam.esm.hibernate;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    List<T> getAll(int limit, int offset);

    Optional<T> getById(long id);

    long create(T entity);

    void delete(T entity);

    default Optional<T> returnSingleFoundResult(List<T> results) {
        if (results.size() > 1) {
            throw new DaoException(GenericExceptionMessageConstants.MORE_ENTITIES_THAN_EXPECTED);
        } else if (results.size() == 1) {
            return Optional.of(results.get(0));
        } else {
            return Optional.empty();
        }
    }
}
