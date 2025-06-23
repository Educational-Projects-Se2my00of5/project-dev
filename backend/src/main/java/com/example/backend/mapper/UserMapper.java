package com.example.backend.mapper;

import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {


    // MapStruct сам поймет, что нужно взять из аргумента
    @Mapping(source = "refreshToken", target = "refreshToken")
    UserDTO.Response.TokenAndShortUserInfo toTokenAndShortUserInfoDTO(User user, String refreshToken);

    @Mapping(source = "accessToken", target = "accessToken")
    @Mapping(source = "refreshToken", target = "refreshToken")
    UserDTO.Response.PairTokens toPairTokensDTO(String accessToken, String refreshToken);

    UserDTO.Response.FullProfile toFullProfileDTO(User user);

    List<UserDTO.Response.ShortProfile> toListUsersDTO(List<User> users);

    UserDTO.Response.ShortProfile toShortProfileDTO(User user);
}

