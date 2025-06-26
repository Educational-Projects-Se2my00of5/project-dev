package com.example.backend.mapper;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO.Response.FullInfoCategory toFullInfoDTO(Category category);

    CategoryDTO.Response.ShortInfoCategory toShortInfoDTO(Category category);
}
