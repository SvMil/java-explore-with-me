package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.dto.RequestParticipationDto;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestParticipationDto toDto(Request request);
}
