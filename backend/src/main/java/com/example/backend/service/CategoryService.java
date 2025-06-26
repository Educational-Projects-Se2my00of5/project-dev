package com.example.backend.service;


import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.MessageDTO;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.backend.util.GetModelOrThrow.getCategoryOrThrow;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDTO.Response.FullInfoCategory getCategory(Long id) {
        return categoryMapper.toFullInfoDTO(getCategoryOrThrow(id));
    }

    public List<CategoryDTO.Response.FullInfoCategory> getAllCategories(String nameFilter) {
        Specification<Category> spec = CategorySpecification.nameContains(nameFilter);

        List<Category> categories = categoryRepository.findAll(spec);
        return categories
                .stream()
                .map(categoryMapper::toFullInfoDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO.Response.FullInfoCategory createCategory(CategoryDTO.Request.CreateOrUpdateCategory category) {
        Category newCategory = new Category();

        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());

        newCategory = categoryRepository.save(newCategory);
        return categoryMapper.toFullInfoDTO(newCategory);
    }

    public CategoryDTO.Response.FullInfoCategory updateCategory(Long id, CategoryDTO.Request.CreateOrUpdateCategory category) {
        Category oldCategory = getCategoryOrThrow(id);

        oldCategory.setName(category.getName());
        oldCategory.setDescription(category.getDescription());

        Category newCategory = categoryRepository.save(oldCategory);
        return categoryMapper.toFullInfoDTO(newCategory);
    }

    public MessageDTO.Response.GetMessage deleteCategory(Long id) {
        Category category = getCategoryOrThrow(id);
        categoryRepository.delete(category);
        return new MessageDTO.Response.GetMessage("success delete category");
    }

}
