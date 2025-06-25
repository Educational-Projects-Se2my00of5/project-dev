package com.example.backend.mapper;

import com.example.backend.dto.CommentDTO;
import com.example.backend.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "authorInfo", source = "user")
    CommentDTO.Response.InfoComment toInfoCommentDTO(Comment comment);
}

