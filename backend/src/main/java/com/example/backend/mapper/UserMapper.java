package com.example.backend.mapper;

import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO.Response.Profile toProfileDTO(User user);

    // MapStruct сам поймет, что нужно взять из аргумента
    @Mapping(source = "refreshToken", target = "refreshToken")
    UserDTO.Response.TokenAndShortUserInfo toTokenAndShortUserInfoDTO(User user, String refreshToken);

    @Mapping(source = "accessToken", target = "accessToken")
    @Mapping(source = "refreshToken", target = "refreshToken")
    UserDTO.Response.PairTokens toPairTokensDTO(String accessToken, String refreshToken);


}

