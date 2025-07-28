package com.fawry.product_api.util;

import com.fawry.product_api.model.entity.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    // PostgreSQL Full-Text Search with ranking
    private static Specification<Product> withKeywordFullText(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Prepare the ts_query
            String tsQuery = keyword.trim()
                    .replaceAll("[^\\w\\s]", "")  // Remove special characters
                    .replaceAll("\\s+", " & ");  // Convert to tsquery format

            // Combine name and description
            Expression<String> nameDescCombined = criteriaBuilder.concat(
                    criteriaBuilder.coalesce(root.get("name"), ""),
                    criteriaBuilder.concat(
                            criteriaBuilder.literal(" "),
                            criteriaBuilder.coalesce(root.get("description"), "")
                    )
            );

            // to_tsvector('english', name || ' ' || description)
            Expression<Boolean> ftsMatch = criteriaBuilder.equal(
                    criteriaBuilder.function("@@", Boolean.class,
                            criteriaBuilder.function("to_tsvector", String.class,
                                    criteriaBuilder.literal("english"),
                                    nameDescCombined
                            ),
                            criteriaBuilder.function("to_tsquery", String.class,
                                    criteriaBuilder.literal("english"),
                                    criteriaBuilder.literal(tsQuery)
                            )
                    ),
                    criteriaBuilder.literal(true)
            );

            return (Predicate) ftsMatch;
        };
    }


    // Hybrid search: Full-text + LIKE fallback
    public static Specification<Product> withKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // For complex searches, use full-text
            if (keyword.contains(" ") || keyword.length() > 3) {
                return withKeywordFullText(keyword).toPredicate(root, query, criteriaBuilder);
            } else {
                // For simple searches, use LIKE (faster for short terms)
                String likePattern = "%" + keyword.toLowerCase() + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                );
            }
        };
    }

    public static Specification<Product> withCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.trim().isEmpty() || "All Categories".equals(category)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("name"), category);
        };
    }

    // Enhanced price range with better validation
    public static Specification<Product> withPriceRange(Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (min != null && min > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min));
            }

            if (max != null && max > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), max));
            }

            // Ensure min <= max if both are provided
            if (min != null && max != null && min > max) {
                // Swap values if min > max
                predicates.clear();
                predicates.add(criteriaBuilder.between(root.get("price"), max, min));
            }

            return predicates.isEmpty() ?
                    criteriaBuilder.conjunction() :
                    criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Main method that combines ALL filters
    public static Specification<Product> withAllFilters(String keyword, String category,
                                                        Double min, Double max) {
        return Specification.where(withKeyword(keyword))
                .and(withCategory(category))
                .and(withPriceRange(min, max));
    }

}
