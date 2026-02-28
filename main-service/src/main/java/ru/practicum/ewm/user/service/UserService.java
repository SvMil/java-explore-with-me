package ru.practicum.ewm.user.service;

import ru.practicum.ewm.request.dto.CreateRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto create(CreateRequestDto dto);

    void delete(Long id);

}
