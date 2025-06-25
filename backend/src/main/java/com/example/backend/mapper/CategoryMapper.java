package com.example.backend.mapper;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.CommentDTO;
import com.example.backend.model.Category;
import com.example.backend.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO.Response.FullInfoCategory toFullInfoDTO(Category category);
    CategoryDTO.Response.ShortInfoCategory toShortInfoDTO(Category category);
}
