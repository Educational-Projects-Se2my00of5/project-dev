package com.example.backend.repository.specification;

import com.example.backend.model.Category;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PostSpecification {

    /**
     * Создает объединенную спецификацию на основе всех фильтров.
     */
    public static Specification<Post> byFilters(Long userId, String title, Set<Category> categories) {
        return (root, query, criteriaBuilder) -> {
            // Создаем список предикатов (условий WHERE)
            List<Predicate> predicates = new ArrayList<>();

            // 1. Фильтр по автору (userId)
            if (userId != null) {
                Join<Post, User> authorJoin = root.join("author"); // Присоединяем таблицу users (по полю author)
                predicates.add(criteriaBuilder.equal(authorJoin.get("id"), userId));
            }

            // 2. Фильтр по названию поста (title)
            if (StringUtils.hasText(title)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                ));
            }

            // 3. Фильтр по категориям
            if (!CollectionUtils.isEmpty(categories)) {

                // Получаем список ID категорий из фильтра
                List<Long> categoryIds = categories.stream().map(Category::getId).toList();

                // Присоединяем таблицу категорий
                Join<Post, Category> categoryJoin = root.join("categories");

                // Добавляем базовое условие, что категория поста должна быть в нашем списке.
                // Это нужно для оптимизации и для работы `HAVING`.
                predicates.add(categoryJoin.get("id").in(categoryIds));

                // Группируем по ID поста
                query.groupBy(root.get("id"));

                // Добавляем условие HAVING: количество совпавших категорий
                // должно быть равно размеру нашего списка фильтра.
                query.having(criteriaBuilder.equal(
                        criteriaBuilder.count(categoryJoin.get("id")),
                        (long) categoryIds.size() // Приводим к long для строгого сравнения типов
                ));
            }

            // Объединяем все предикаты через оператор AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}