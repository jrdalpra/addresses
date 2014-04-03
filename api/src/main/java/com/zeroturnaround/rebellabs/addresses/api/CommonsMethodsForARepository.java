package com.zeroturnaround.rebellabs.addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;

public interface CommonsMethodsForARepository<T, K> {

    T get(K id) throws NotFoundException;

    T reload(T entity) throws NotFoundException;

    List<T> list(int page, int max);

    Integer lastPage(int max);
}
