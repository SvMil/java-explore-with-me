package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import ru.practicum.ewm.event.dto.FullEventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.CreateEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.location.mapper.LocationMapper;


@Mapper(
        componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}
)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    Event toEntity(CreateEventDto dto);

    @Mapping(target = "views", source = "views")
    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toShortDto(Event event, long views);

    @Mapping(target = "views", source = "views")
    @Mapping(target = "confirmedRequests", ignore = true)
    FullEventDto toFullDto(Event event, long views);
}
