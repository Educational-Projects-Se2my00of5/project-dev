package com.example.backend.repository.specification;

import com.example.backend.model.Category;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class CategorySpecification {

    public static Specification<Category> nameContains(String name) {
        // Если фильтр не задан или пуст, мы не добавляем никаких условий в запрос
        if (!StringUtils.hasText(name)) {
            return null;
        }

        // Возвращаем лямбду, которая строит предикат (условие WHERE)
        return (root, query, criteriaBuilder) ->
                // WHERE lower(category.name) LIKE lower('%' + name + '%')
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}
