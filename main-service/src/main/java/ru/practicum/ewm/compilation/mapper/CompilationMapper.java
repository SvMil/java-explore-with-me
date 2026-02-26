package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.dto.CompilationDto;

@Mapper(
        componentModel = "spring",
        uses = {EventMapper.class}
)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation);
}
