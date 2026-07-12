package com.poke.api.repository;

import com.poke.api.model.SearchHistory;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class SearchHistorySpecifications {

    private SearchHistorySpecifications() {
    }

    // Builds optional filters for search history queries.
    public static Specification<SearchHistory> withFilters(LocalDateTime from, LocalDateTime to, String type, String text) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (from != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            if (type != null && !type.isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("searchType")), type.trim().toLowerCase()));
            }
            if (text != null && !text.isBlank()) {
                String likePattern = "%" + text.trim().toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("queryValue")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("endpoint")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("searchType")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("errorMessage")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("userIdentifier")), likePattern)
                ));
            }

            return predicate;
        };
    }
}
