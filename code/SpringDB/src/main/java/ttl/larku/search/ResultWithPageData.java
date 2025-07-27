package ttl.larku.search;

import java.util.List;

public record ResultWithPageData<T>(List<T> result,
                                    int page,
                                    int pageSize,
                                    long totalElements,
                                    int totalPages) {
}
