package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.request.dto.CreateUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);

    User toEntity(CreateUserRequest dto);

    @Mapping(target = "id", source = "id")
    UserShortDto toShortDto(User user);

    @Mapping(target = "id", source = "id")
    UserDto toDto(User user);
}
