package com.example.backend.repository.specification;

import com.example.backend.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

    public static Specification<User> usernameContains(String username) {
        // Если фильтр не задан или пуст, мы не добавляем никаких условий в запрос
        if (!StringUtils.hasText(username)) {
            return null;
        }

        // Возвращаем лямбду, которая строит предикат (условие WHERE)
        return (root, query, criteriaBuilder) ->
                // WHERE lower(user.username) LIKE lower('%' + username + '%')
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                );
    }
}
