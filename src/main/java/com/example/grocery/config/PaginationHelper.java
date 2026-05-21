package com.example.grocery.config;

import java.util.List;

public final class PaginationHelper {

    private PaginationHelper() {
    }

    public static <T> PageSlice<T> paginate(List<T> source, int requestedPage, int requestedSize) {
        List<T> safeSource = source == null ? List.of() : source;
        int size = Math.max(1, Math.min(requestedSize, 50));
        int totalItems = safeSource.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));
        int currentPage = Math.max(1, Math.min(requestedPage, totalPages));

        int fromIndex = (currentPage - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<T> items = fromIndex >= totalItems ? List.of() : safeSource.subList(fromIndex, toIndex);

        return new PageSlice<>(items, currentPage, totalPages, totalItems, size);
    }

    public record PageSlice<T>(List<T> items, int currentPage, int totalPages, int totalItems, int size) {
        public boolean hasPrevious() {
            return currentPage > 1;
        }

        public boolean hasNext() {
            return currentPage < totalPages;
        }
    }
}
