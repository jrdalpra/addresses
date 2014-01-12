package addresses.api;

import java.util.List;

public interface CommonsMethodsForARepository<T, K> {

    T get(K id);

    List<T> list(int page, int max);
}
