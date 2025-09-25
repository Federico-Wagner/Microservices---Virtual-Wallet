package com.billeteraVirtual.users.Mapper;

import com.billeteraVirtual.users.dto.UserDTO;
import com.billeteraVirtual.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);

    User toEntity(UserDTO dto);
}
