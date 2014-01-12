package com.zeroturnaround.rebellabs.addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;

public interface CommonsMethodsForARepository<T, K> {

    T get(K id) throws NotFoundException;

    List<T> list(int page, int max);
}
