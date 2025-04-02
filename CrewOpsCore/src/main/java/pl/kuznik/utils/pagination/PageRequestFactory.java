package pl.kuznik.utils.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestFactory {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 15;

    public static PageRequest createPageRequest(int page, int size, Sort sort) {
        int finalPage;
        int finalSize;

        finalPage = page > 0 ? page : DEFAULT_PAGE;

        finalSize = size > 100 ? 100 : size;

        return PageRequest.of(finalPage, finalSize, sort);
    }
}
